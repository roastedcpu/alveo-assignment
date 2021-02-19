package com.assignment.logmonitor.service;

import com.assignment.logmonitor.model.LogLevelEnum;
import com.assignment.logmonitor.model.LogMetric;
import com.assignment.logmonitor.service.helpers.MonitorServiceThread;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MonitorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorService.class);
    private static final Pattern LOG_LINE_REGEX = Pattern.compile("^([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}),([0-9]{3})+[ \\t]+([A-Z]+)[ \\t]+(.+)");

    private static Integer DEFAULT_REFRESH_INTERVAL = 15;

    @Getter @Setter
    private Integer refreshInterval;

    @Value("${services.monitor.sourcefile}")
    private String sourceFile;

    private MonitorServiceThread monitorServiceThread;
    private BufferedReader fReader;

    private LocalDateTime latestMetricTimestamp;
    private Map<LogLevelEnum, LogMetric> metrics;

    public synchronized Map<LogLevelEnum, LogMetric> getMetrics() {
        if(this.metrics == null) return new HashMap<>();
        return new HashMap<>(this.metrics);
    }

    private synchronized Map<LogLevelEnum, LogMetric> getMetricsForModification() {
        return this.metrics;
    }

    private synchronized void setMetrics(Map<LogLevelEnum, LogMetric> metrics) {
        this.metrics = metrics;
    }

    public MonitorService() throws InterruptedException {
        this.refreshInterval = DEFAULT_REFRESH_INTERVAL;
        if(this.monitorServiceThread == null) {
            this.monitorServiceThread = new MonitorServiceThread(this);
            this.monitorServiceThread.setDaemon(true);
            this.monitorServiceThread.start();
        }
    }

    public void _loggingMonitorTask() throws InterruptedException {
        Integer _refreshInterval;
        initializeReader();  // The first time this method is called it will fail because the instance can still be being created

        LOGGER.info("Initializing logging monitor task");
        while(true) {
            _refreshInterval = this.refreshInterval;
            // as long as the refresh rate doesn't change and no exceptions are thrown from file operations, we will be inside the inner while
            // getting to the outer while will be close to restarting the metrics job for resiliency
            while( _refreshInterval == this.refreshInterval ) {
                boolean refreshIntervalChangedWhileStalling = false;
                LocalDateTime tic = LocalDateTime.now();
                LocalDateTime target = tic.plusSeconds(_refreshInterval);
                while(LocalDateTime.now().compareTo(target) < 0 && !refreshIntervalChangedWhileStalling) {
                    if(this.refreshInterval != _refreshInterval) {
                        refreshIntervalChangedWhileStalling = true;
                        break;
                    }
                    Thread.sleep(250);
                }
                if(refreshIntervalChangedWhileStalling) break;

                try {
                    this.takeMetrics();
                } catch(Exception ex) {
                    break;
                }
            }
            LOGGER.debug("Logging monitor task is restarting...");
            if(!this.initializeReader()) {
                LOGGER.error("Error opening input log file");
                Thread.sleep(2500);
            }
        }
    }

    private boolean initializeReader() {
        if(this.sourceFile == null) return false;
        this.latestMetricTimestamp = LocalDateTime.now();
        try {
            LOGGER.debug("Opening file " + this.sourceFile);
            this.fReader = new BufferedReader(new FileReader(this.sourceFile));
            takeMetrics();
            return true;
        } catch (Exception e) {
            LOGGER.debug("XX " + this.sourceFile);

            return false;
        }
    }

    private void takeMetrics() throws IOException {
        final Integer _refreshInterval = this.refreshInterval;
        final LocalDateTime rightTimestamp = LocalDateTime.now();
        final LocalDateTime leftTimestamp =  rightTimestamp.minusSeconds(_refreshInterval);
        Map<LogLevelEnum, LogMetric> _metrics = new HashMap<>();
        for(LogLevelEnum level : LogLevelEnum.values()) {
            _metrics.put(level, new LogMetric().level(level).count(0));
        }

        while(true) {  // I'm trusting of the stop condition, no need for anything more advanced
            String nextLine = this.fReader.readLine();
            if(nextLine == null) break;
            Optional<LogLevelEnum> logLevel = validateTimeAndExtractLogLevelFromLine(nextLine, leftTimestamp, rightTimestamp);
            if(logLevel.isPresent()) {
                LogMetric metric = _metrics.get(logLevel.get());
                Integer currentCount = metric.getCount();
                metric.setCount(currentCount+1);
            }
        }

        this.latestMetricTimestamp = rightTimestamp;
        this.setMetrics(_metrics);
    }

    private Optional<LogLevelEnum> validateTimeAndExtractLogLevelFromLine(final String line, final LocalDateTime leftTime, final LocalDateTime rightTime) {
        Matcher m = LOG_LINE_REGEX.matcher(line);
        try {
            if (m.find()) {
                LocalDateTime timestamp = LocalDateTime.parse(m.group(1) + "T" + m.group(2) + "." + m.group(3), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                if (timestamp.compareTo(leftTime) < 0 || timestamp.compareTo(rightTime) > 0) return Optional.empty();
                // Time is within the desired range; Extract log level
                return Optional.of(LogLevelEnum.fromValue(m.group(4)));
            }
        } catch(Exception ex) { } // Whatever had just happened, this line is not to be counted.
        return Optional.empty();
    }

}

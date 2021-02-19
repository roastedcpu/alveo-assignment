package com.assignment.logmonitor.service;

import com.assignment.logmonitor.model.LogMetric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class MetricsService {
    @Autowired
    private MonitorService monitorService;

    public Flux<LogMetric> getLoggingMetricsFromLastPeriod() {
        return Flux.fromIterable(monitorService.getMetrics().values());
    }
}

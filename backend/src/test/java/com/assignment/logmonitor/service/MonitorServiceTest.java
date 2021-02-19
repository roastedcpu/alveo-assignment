package com.assignment.logmonitor.service;

import com.assignment.logmonitor.model.LogLevelEnum;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MonitorServiceTest {
    @Test
    public void testRegex() throws Exception {
        Field field = MonitorService.class.getDeclaredField("LOG_LINE_REGEX");
        field.setAccessible(true);
        Pattern LOG_LINE_REGEX = (Pattern) field.get(MonitorService.class);

        String logLine1 = "2021-01-05 10:30:00,000 invalid invalid_regex";
        String logLine2 = "2021-01-05 10:30:00,000-INVALID invalid_regex";
        String logLine3 = "2021-01-05 10:30:00,000 WARNING valid regex";
        String logLine4 = "2021-01-0610:30:00,000 ERROR invalid_regex";
        String logLine5 = "2021-01-01 10:30:00,000 INFO-invalid_regex";
        String logLine6 = "2021-01-01 10:30:00,abc INFO invalid_regex";
        String logLine7 = "2021-01-01 10:30:00,000 INFO valid-regex";

        // Test matching
        Matcher m1 = LOG_LINE_REGEX.matcher(logLine1);
        Matcher m2 = LOG_LINE_REGEX.matcher(logLine2);
        Matcher m3 = LOG_LINE_REGEX.matcher(logLine3);
        Matcher m4 = LOG_LINE_REGEX.matcher(logLine4);
        Matcher m5 = LOG_LINE_REGEX.matcher(logLine5);
        Matcher m6 = LOG_LINE_REGEX.matcher(logLine6);
        Matcher m7 = LOG_LINE_REGEX.matcher(logLine7);

        assert !m1.find();
        assert !m2.find();
        assert m3.find();
        assert !m4.find();
        assert !m5.find();
        assert !m6.find();
        assert m7.find();

        // Test extraction
        assert m3.group(1).equals("2021-01-05");
        assert m3.group(2).equals("10:30:00");
        assert m3.group(3).equals("000");
        assert m3.group(4).equals("WARNING");
        assert m7.group(1).equals("2021-01-01");
        assert m7.group(2).equals("10:30:00");
        assert m7.group(3).equals("000");
        assert m7.group(4).equals("INFO");

    }

    @Test 
    public void validateTimeAndExtractLogLevelFromLine_wrongInput() throws Exception {
        MonitorService monitorService = new MonitorService();
        Method method = MonitorService.class.getDeclaredMethod("validateTimeAndExtractLogLevelFromLine", String.class, LocalDateTime.class, LocalDateTime.class);
        method.setAccessible(true);

        LocalDateTime left = LocalDateTime.parse("2021-01-05T10:00:00.000", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime right = LocalDateTime.parse("2021-01-05T11:00:00.000", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String logLine1 = "2021-01-05 10:30:00,000 INVALID invalid_tag";
        String logLine2 = "2021-01-05 10:30:00,000-INVALID invalid_regex";

        Optional<LogLevelEnum> res1 = (Optional<LogLevelEnum>) method.invoke(monitorService, logLine1, left, right);
        Optional<LogLevelEnum> res2 = (Optional<LogLevelEnum>) method.invoke(monitorService, logLine2, left, right);

        assert !res1.isPresent();
        assert !res2.isPresent();
    }

    @Test
    public void validateTimeAndExtractLogLevelFromLineTest() throws Exception {
        MonitorService monitorService = new MonitorService();
        Method method = MonitorService.class.getDeclaredMethod("validateTimeAndExtractLogLevelFromLine", String.class, LocalDateTime.class, LocalDateTime.class);
        method.setAccessible(true);

        LocalDateTime left = LocalDateTime.parse("2021-01-05T10:00:00.000", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime right = LocalDateTime.parse("2021-01-05T11:00:00.000", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String logLineInsideInterval = "2021-01-05 10:30:00,000 ERROR 123123";
        String logLineOutsideInterval1 = "2021-01-06 10:30:00,000 ERROR 123123";
        String logLineOutsideInterval2 = "2021-01-01 10:30:00,000 INFO 123123";

        Optional<LogLevelEnum> res1 = (Optional<LogLevelEnum>) method.invoke(monitorService, logLineInsideInterval, left, right);
        Optional<LogLevelEnum> res2 = (Optional<LogLevelEnum>) method.invoke(monitorService, logLineOutsideInterval1, left, right);
        Optional<LogLevelEnum> res3 = (Optional<LogLevelEnum>) method.invoke(monitorService, logLineOutsideInterval2, left, right);

        assert res1.isPresent();
        assert !res2.isPresent();
        assert !res3.isPresent();
    }
}
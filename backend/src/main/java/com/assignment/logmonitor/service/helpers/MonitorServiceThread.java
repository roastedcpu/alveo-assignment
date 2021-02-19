package com.assignment.logmonitor.service.helpers;

import com.assignment.logmonitor.service.MonitorService;

public class MonitorServiceThread extends Thread {

    private MonitorService monitorService;

    public MonitorServiceThread(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    public void run() {
        try {
            monitorService._loggingMonitorTask();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

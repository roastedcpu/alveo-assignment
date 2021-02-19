package com.assignment.logmonitor.controller;

import com.assignment.logmonitor.LogMonitorApplication;
import com.assignment.logmonitor.model.LogLevelEnum;
import com.assignment.logmonitor.model.LogMetric;
import com.assignment.logmonitor.service.MonitorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.HashMap;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LogMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class MetricsControllerIT {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MonitorService monitorService;

    @Test
    public void getMetricsIT() throws Exception {
        when(monitorService.getMetrics()).thenAnswer(discard -> new HashMap<LogLevelEnum, LogMetric>());

        // Test if the endpoint is active.
        webTestClient.get()
                .uri("/metrics/logging")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody();

        verify(monitorService, atLeastOnce()).getMetrics();
    }
}

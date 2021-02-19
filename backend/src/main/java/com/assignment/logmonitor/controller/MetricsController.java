package com.assignment.logmonitor.controller;

import com.assignment.logmonitor.model.LogMetric;
import com.assignment.logmonitor.service.MetricsService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/metrics")
public class MetricsController {
    @Autowired MetricsService metricsService;

    @ApiResponses(value = {@ApiResponse(code = 200, message = "Logging metrics for the last observed period", response = LogMetric.class, responseContainer = "List")})
    @CrossOrigin
    @GetMapping(value = "/logging", produces = { "application/json" })
    public Mono<ResponseEntity<Flux<LogMetric>>> getLoggingMetrics() {
        // There is really no advantage on a reactive api here, since we have to synchronously access the objects.
        // It would be an advantage if we were dealing with the possibility of doing async writes to a DB, for example.
        return Mono.just(ResponseEntity.ok(metricsService.getLoggingMetricsFromLastPeriod()));
    }
}
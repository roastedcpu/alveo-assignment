package com.assignment.logmonitor.controller;

import com.assignment.logmonitor.model.LogMonitorConfigs;
import com.assignment.logmonitor.exceptions.BadRequestException;
import com.assignment.logmonitor.service.MonitorService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import javax.validation.Valid;

@RestController
@RequestMapping("/configs")
public class ConfigsController {
    @Autowired private MonitorService monitorService;

    @ApiOperation(value = "Get current config for logging monitor", notes = "", response = LogMonitorConfigs.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Retrieved current configuration for logging monitor", response = LogMonitorConfigs.class)})
    @CrossOrigin
    @GetMapping(value = "/monitor/logging", produces = { "application/json" })
    public Mono<ResponseEntity<LogMonitorConfigs>> getLoggingMonitorConfig() {
        LogMonitorConfigs configs = new LogMonitorConfigs();
        configs.setRefreshInterval(monitorService.getRefreshInterval());
        return Mono.just(ResponseEntity.ok(configs));
    }

    @ApiOperation(value = "Submit a new configuration for logging monitor", notes = "", response = LogMonitorConfigs.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "New configuration for logging monitor", response = LogMonitorConfigs.class),
            @ApiResponse(code = 400, message = "The request could not be successfully performed.") })
    @CrossOrigin
    @PostMapping(value = "/monitor/logging", produces = { "application/json" }, consumes = { "application/json" })
    public Mono<ResponseEntity<LogMonitorConfigs>> setLoggingMonitorConfigs(@ApiParam(value = "" , required=true)  @Valid @RequestBody Mono<LogMonitorConfigs> logMonitorConfigs) {
        return logMonitorConfigs.map(configs -> {
                        if(configs.getRefreshInterval() <= 0) throw new BadRequestException("refreshInterval needs to be greater than 0");
                        return configs;
                }).onErrorStop()
                .doOnSuccess(configs -> monitorService.setRefreshInterval(configs.getRefreshInterval()))
                .map(configs -> ResponseEntity.ok(configs));
    }

}
package com.czertainly.controllers;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.SchedulerException;
import com.czertainly.api.model.scheduler.SchedulerRequestDto;
import com.czertainly.api.model.scheduler.SchedulerResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/scheduler")
public interface SchedulerController {


    @RequestMapping(path = "/create", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    void createNewJob(@RequestBody SchedulerRequestDto schedulerDto) throws SchedulerException;

    @GetMapping(path = "/update")
    void updateJob(@RequestBody final SchedulerRequestDto schedulerDto) throws SchedulerException;

    @DeleteMapping(path = "/{jobName}")
    void deleteJob(@Parameter(description = "Job name") @PathVariable final String jobName) throws SchedulerException;

    @GetMapping(path = "/list")
    SchedulerResponseDto listJobs() throws SchedulerException;

    @GetMapping(path = "/{jobName}/enable")
    void enableJob(@Parameter(description = "Job name") @PathVariable String jobName) throws SchedulerException, ConnectorException;

    @GetMapping(path = "/{jobName}/disable")
    void disableJob(@Parameter(description = "Job name") @PathVariable String jobName) throws SchedulerException;


}

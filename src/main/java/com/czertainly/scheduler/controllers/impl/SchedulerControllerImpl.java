package com.czertainly.scheduler.controllers.impl;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.SchedulerException;
import com.czertainly.api.model.scheduler.SchedulerRequestDto;
import com.czertainly.api.model.scheduler.SchedulerResponseDto;
import com.czertainly.scheduler.controllers.SchedulerController;
import com.czertainly.scheduler.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchedulerControllerImpl implements SchedulerController {

    private SchedulerService schedulerService;

    @Override
    public void createNewJob(final SchedulerRequestDto schedulerDto) throws SchedulerException {
        schedulerService.createNewJob(schedulerDto);
    }

    @Override
    public void updateJob(final SchedulerRequestDto schedulerDto) throws SchedulerException {
        schedulerService.updateJob(schedulerDto);
    }

    @Override
    public void deleteJob(final String jobName) throws SchedulerException {
        schedulerService.deleteJob(jobName);
    }

    @Override
    public SchedulerResponseDto listJobs() throws SchedulerException {
        return schedulerService.listJobs();
    }

    @Override
    public void enableJob(String jobName) throws SchedulerException, ConnectorException {
        schedulerService.enableJob(jobName);
    }

    @Override
    public void disableJob(String jobName) throws SchedulerException {
        schedulerService.disableJob(jobName);
    }

    // SETTERs

    @Autowired
    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}

package com.czertainly.scheduler.service.impl;

import com.czertainly.api.exception.SchedulerException;
import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.scheduler.SchedulerJobDto;
import com.czertainly.api.model.scheduler.SchedulerRequestDto;
import com.czertainly.api.model.scheduler.SchedulerResponseDto;
import com.czertainly.api.model.scheduler.SchedulerStatus;
import com.czertainly.scheduler.constants.JobConstants;
import com.czertainly.scheduler.service.SchedulerService;
import com.czertainly.scheduler.utils.SchedulerUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SchedulerServiceImpl implements SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    private Scheduler scheduler;

    @Override
    public void createNewJob(SchedulerRequestDto schedulerDto) throws SchedulerException {
        final SchedulerJobDto schedulerDetail = schedulerDto.getSchedulerJob();
        try {
            if (scheduler.checkExists(new JobKey(schedulerDetail.getJobName(), JobConstants.GROUP_NAME))) {
                logger.info("Job {} already exists.", schedulerDto.getSchedulerJob().getJobName());
                return;
            }

            if(!CronExpression.isValidExpression(schedulerDetail.getCronExpression())) {
                throw new ValidationException(ValidationError.create("Invalid format of CRON expression"));
            }

            logger.info("Scheduling new job withe name {}", schedulerDetail.getJobName());
            final JobDetail jobDetail
                    = SchedulerUtils.prepareJobDetail(schedulerDetail.getJobName(), schedulerDetail.getClassNameToBeExecuted());
            final Trigger jobTrigger
                    = SchedulerUtils.prepareTrigger(schedulerDetail.getJobName(), schedulerDetail.getCronExpression());
            scheduler.scheduleJob(jobDetail, jobTrigger);
            logger.info("Job {} scheduled with by {}", schedulerDetail.getJobName(), schedulerDetail.getCronExpression());
        } catch (org.quartz.SchedulerException e) {
            logger.error("Unable to schedule job {}", schedulerDetail.getJobName(), e.getMessage());
            throw new SchedulerException(e.getMessage());
        }
    }

    @Override
    public void updateJob(SchedulerRequestDto schedulerDto) throws SchedulerException {
        final SchedulerJobDto schedulerDetail = schedulerDto.getSchedulerJob();
        logger.info("Updating job with name {}", schedulerDetail.getJobName());
        deleteJob(schedulerDetail.getJobName());
        createNewJob(schedulerDto);
    }

    @Override
    public void deleteJob(String jobName) throws SchedulerException {
        logger.info("Delete/Unregister job with name {}", jobName);
        try {
            scheduler.unscheduleJob(new TriggerKey(jobName + JobConstants.JOB_TRIGGER_SUFFIX));
            scheduler.deleteJob(new JobKey(jobName, JobConstants.GROUP_NAME));
            logger.info("Job {} was unregistered.", jobName);
        } catch (org.quartz.SchedulerException e) {
            logger.error("Unable to unregister job {}" , jobName);
            throw new SchedulerException(e.getMessage());
        }
    }

    @Override
    public SchedulerResponseDto listJobs() throws SchedulerException {
        logger.info("Retrieve list of registered jobs.");
        final List<SchedulerJobDto> schedulerDetailList = new ArrayList<>();
        try {
            for (final JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(JobConstants.GROUP_NAME))) {
                final JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                final CronTrigger trigger = (CronTrigger) scheduler.getTrigger(new TriggerKey(jobKey.getName() + JobConstants.JOB_TRIGGER_SUFFIX));
                schedulerDetailList
                        .add(new SchedulerJobDto(jobKey.getName(), trigger.getCronExpression(), jobDetail.getJobDataMap().getString(JobConstants.CLASS_TOBE_EXECUTED)));
            }
        } catch (org.quartz.SchedulerException e) {
            logger.error("Unable to retrieve list of registered jobs.", e.getMessage());
            throw new SchedulerException(e.getMessage());
        }

        final SchedulerResponseDto schedulerResponseDto = new SchedulerResponseDto(SchedulerStatus.OK);
        schedulerResponseDto.setSchedulerJobList(schedulerDetailList);
        return schedulerResponseDto;
    }

    @Override
    public void enableJob(String jobName) throws SchedulerException {
        logger.info("Enabling job with name {}", jobName);
        try {
            scheduler.resumeJob(new JobKey(jobName, JobConstants.GROUP_NAME));
            logger.info("Job {} was resumed.", jobName);
        } catch (org.quartz.SchedulerException e) {
            logger.error("Unable to resume job {}", jobName, e.getMessage());
            throw new SchedulerException(e.getMessage());
        }
    }

    @Override
    public void disableJob(String jobName) throws SchedulerException {
        logger.info("Disabling job with name {}", jobName);
        try {
            scheduler.pauseJob(new JobKey(jobName, JobConstants.GROUP_NAME));
            logger.info("Job {} was paused.", jobName);
        } catch (org.quartz.SchedulerException e) {
            logger.error("Unable to pause job {}", jobName, e.getMessage());
            throw new SchedulerException(e.getMessage());
        }
    }


    // SETTERs

    @Autowired
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}

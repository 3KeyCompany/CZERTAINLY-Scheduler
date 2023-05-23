package com.czertainly.utils;

import com.czertainly.constants.JobConstants;
import com.czertainly.jobs.SchedulerJob;
import org.quartz.*;

public class SchedulerUtils {

    public static JobDetail prepareJobDetail(final String jobName, final String className) {
        return JobBuilder.newJob(SchedulerJob.class)
                .withIdentity(jobName, JobConstants.GROUP_NAME)
                .usingJobData(JobConstants.CLASS_TOBE_EXECUTED, className)
                .build();
    }

    public static Trigger prepareTrigger(final String jobName, final String cronExpression) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobName + JobConstants.JOB_TRIGGER_SUFFIX, JobConstants.GROUP_NAME)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }


}

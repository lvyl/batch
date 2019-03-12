package com.lvyl.configbeans;

import com.lvyl.actions.GetLotteryTicketDataTask;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfigBean {

    @Bean
    public JobDetail getLotteryTicketDataTask() {
        return JobBuilder.newJob(GetLotteryTicketDataTask.class).withIdentity("GetDataAction").storeDurably().build();
    }

    @Bean
    public CronTrigger getLotteryTicketDataTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("20 * * * * ?");
        return (CronTrigger) TriggerBuilder.newTrigger().forJob(this.getLotteryTicketDataTask()).withIdentity("GetDataTrigger").withSchedule(scheduleBuilder).build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactory() {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setJobDetails(new JobDetail[]{this.getLotteryTicketDataTask()});
        bean.setTriggers(new Trigger[]{this.getLotteryTicketDataTrigger()});
        return bean;
    }
}

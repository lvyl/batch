package com.lvyl.batch.configbeans;

import com.lvyl.batch.actions.CalLottery10;
import com.lvyl.batch.actions.GetLotteryTicketDataTask;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfigBean {

    @Bean
    public JobDetail getLotteryTicketDataJob() {
        return JobBuilder.newJob(GetLotteryTicketDataTask.class).withIdentity("GetDataAction").storeDurably().build();
    }

    @Bean
    public JobDetail getCalLottery10() {
        return JobBuilder.newJob(CalLottery10.class).withIdentity("CalLottery10").storeDurably().build();
    }

    @Bean
    public CronTrigger getLotteryTicketDataTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 20 * * ?");
        return (CronTrigger) TriggerBuilder.newTrigger().forJob(this.getLotteryTicketDataJob()).withIdentity("GetDataTrigger").withSchedule(scheduleBuilder).build();
    }

    @Bean
    public CronTrigger getCalLottery10Trigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 20 * * ?");
        return (CronTrigger) TriggerBuilder.newTrigger().forJob(this.getCalLottery10()).withIdentity("CalLottery10Trigger").withSchedule(scheduleBuilder).build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactory() {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setJobDetails(new JobDetail[]{this.getLotteryTicketDataJob(),this.getCalLottery10()});
        bean.setTriggers(new Trigger[]{this.getLotteryTicketDataTrigger(),this.getCalLottery10Trigger()});
        return bean;
    }
}

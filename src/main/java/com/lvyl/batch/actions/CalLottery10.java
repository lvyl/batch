package com.lvyl.batch.actions;

import com.lvyl.batch.services.Service01;
import com.lvyl.batch.utils.ApplicationContextProvider;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class CalLottery10 extends QuartzJobBean {
    Logger logger = LoggerFactory.getLogger(CalLottery10.class);
    @Autowired
    private Service01 service01;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        this.logger.info("calLottery10 begin!");
        service01 = ApplicationContextProvider.getBean("service01",Service01.class);
        service01.function02();
        service01.function03();
        this.logger.info("calLottery10 end!");
    }
}

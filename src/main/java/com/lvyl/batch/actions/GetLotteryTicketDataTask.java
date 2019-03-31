package com.lvyl.batch.actions;

import com.lvyl.batch.services.Service01;
import com.lvyl.batch.utils.ApplicationContextProvider;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class GetLotteryTicketDataTask extends QuartzJobBean {
    Logger logger = LoggerFactory.getLogger(GetLotteryTicketDataTask.class);
    @Autowired
    private Service01 service01;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext){
        this.logger.info("getLotteryTicketDataTask begin!");
        service01 = ApplicationContextProvider.getBean("service01",Service01.class);
        service01.function01();
        this.logger.info("getLotteryTicketDataTask end!");
    }


}

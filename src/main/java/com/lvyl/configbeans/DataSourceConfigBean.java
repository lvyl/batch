package com.lvyl.configbeans;

import com.alibaba.druid.pool.DruidDataSource;
import com.lvyl.mapper.LotteryTicketMapper;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfigBean {

    @Bean
    @ConfigurationProperties(
            prefix = "druid"
    )
    public DruidDataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        return dataSource;
    }

    @Bean
    @ConfigurationProperties(
            prefix = "mybatis"
    )
    public SqlSessionFactoryBean getSessionFactory() {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(this.getDataSource());
        return sqlSessionFactoryBean;
    }

    @Bean({"lotteryTicket"})
    public LotteryTicketMapper getLotteryTicketMapper() {
        SqlSession sqlSession = null;

        try {
            sqlSession = this.getSessionFactory().getObject().openSession();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        LotteryTicketMapper lotteryTicketMapper = (LotteryTicketMapper)sqlSession.getMapper(LotteryTicketMapper.class);
        return lotteryTicketMapper;
    }
}
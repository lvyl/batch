package com.lvyl.batch.configbeans;

import com.alibaba.druid.pool.DruidDataSource;
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


}
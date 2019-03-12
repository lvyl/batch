package com.lvyl.configbeans;

import com.lvyl.utils.HttpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilConfigBean {

    @Bean({"httpUtil"})
    public HttpUtil getUtil() {
        return new HttpUtil();
    }
}
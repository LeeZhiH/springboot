package com.zhihao.zhiyin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 86159
 * @time 2022/9/4 10:24
 */
@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching   //开启缓存注解功能
public class ZhiyinApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZhiyinApplication.class,args);
        log.info("只因外卖启动了...");
    }
}

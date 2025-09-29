package com.rental.saas.basedata;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 基础数据服务启动类
 * 
 * @author Rental SaaS Team
 */
@SpringBootApplication(scanBasePackages = {"com.rental.saas.common", "com.rental.saas.basedata"})
@EnableDiscoveryClient
@MapperScan("com.rental.saas.basedata.mapper")
public class BaseDataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseDataServiceApplication.class, args);
        System.out.println("租车SaaS系统基础数据服务启动成功！");
    }
}
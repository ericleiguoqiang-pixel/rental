package com.rental.saas.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户服务启动类
 * 
 * @author Rental SaaS Team
 */
@SpringBootApplication(scanBasePackages = {"com.rental.saas.common", "com.rental.saas.user"})
@EnableDiscoveryClient
@MapperScan("com.rental.saas.user.mapper")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("租车SaaS系统用户服务启动成功！");
    }
}
package com.rental.saas.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 订单服务启动类
 * 
 * @author Rental SaaS Team
 */
@SpringBootApplication(scanBasePackages = {"com.rental.saas.common", "com.rental.saas.order"})
@EnableDiscoveryClient
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("租车SaaS系统订单服务启动成功！");
    }
}
package com.rental.saas.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 支付服务启动类
 * 
 * @author Rental SaaS Team
 */
@SpringBootApplication(scanBasePackages = {"com.rental.saas.common", "com.rental.saas.payment"})
@EnableDiscoveryClient
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
        System.out.println("租车SaaS系统支付服务启动成功！");
    }
}
package com.rental.saas.pricing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 定价服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.rental.api")
public class PricingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PricingServiceApplication.class, args);
    }
}
package com.rental.saas.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 商品服务启动类
 * 
 * @author Rental SaaS Team
 */
@SpringBootApplication(scanBasePackages = {"com.rental.saas.common", "com.rental.saas.product"})
@EnableDiscoveryClient
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
        System.out.println("租车SaaS系统商品服务启动成功！");
    }
}
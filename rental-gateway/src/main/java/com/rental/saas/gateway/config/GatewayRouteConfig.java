package com.rental.saas.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关路由配置类
 * 
 * @author Rental SaaS Team
 */
@Configuration
public class GatewayRouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 用户服务路由
                .route("user-service", r -> r
                        .path("/api/auth/**", "/api/users/**", "/api/employees/**", "/api/merchants/**")
                        .uri("lb://rental-user-service"))
                
                // 基础数据服务路由
                .route("base-data-service", r -> r
                        .path("/api/stores/**", "/api/vehicles/**", "/api/car-models/**")
                        .uri("lb://rental-base-data-service"))
                
                // 商品服务路由
                .route("product-service", r -> r
                        .path("/api/products/**", "/api/pricing/**", "/api/templates/**")
                        .uri("lb://rental-product-service"))
                
                // 订单服务路由
                .route("order-service", r -> r
                        .path("/api/orders/**", "/api/rentals/**")
                        .uri("lb://rental-order-service"))
                
                // 支付服务路由
                .route("payment-service", r -> r
                        .path("/api/payments/**", "/api/refunds/**")
                        .uri("lb://rental-payment-service"))
                
                .build();
    }
}
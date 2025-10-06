package com.rental.saas.gateway.config;

import com.rental.saas.common.utils.JwtUtil;
import com.rental.saas.gateway.filter.JwtAuthenticationFilter;
import com.rental.saas.gateway.filter.UserJwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service1", r -> r
                        .path("/api/user/**").filters(f -> f.filter(new UserJwtAuthenticationFilter(jwtUtil)))
                        .uri("lb://rental-user-service")
                        )
                .route("rental-pricing", r -> r
                        .path("/api/pricing/**").filters(f -> f.filter(new UserJwtAuthenticationFilter(jwtUtil)))
                        .uri("lb://rental-pricing")
                )
                // 用户服务路由
                .route("user-service2", r -> r
                        .path("/api/auth/**", "/api/employees/**", "/api/merchants/**").filters(f -> f.filter(new JwtAuthenticationFilter(jwtUtil)))
                        .uri("lb://rental-user-service"))
                
                // 商品服务路由
                .route("product-service", r -> r
                        .path("/api/products/**", "/api/templates/**").filters(f -> f.filter(new JwtAuthenticationFilter(jwtUtil)))
                        .uri("lb://rental-product-service"))
                
                // 订单服务路由
                .route("order-service1", r -> r
                        .path("/api/user-orders/**").filters(f -> f.filter(new UserJwtAuthenticationFilter(jwtUtil)).rewritePath("/api/user-orders(?<segment>.*)", "/api/orders${segment}"))
                        .uri("lb://rental-order-service"))
                .route("order-service2", r -> r
                        .path("/api/orders/**", "/api/rentals/**").filters(f -> f.filter(new JwtAuthenticationFilter(jwtUtil)))
                        .uri("lb://rental-order-service"))
                
                // 支付服务路由
                .route("payment-service", r -> r
                        .path("/api/payments/**", "/api/refunds/**").filters(f -> f.filter(new JwtAuthenticationFilter(jwtUtil)))
                        .uri("lb://rental-payment-service"))
                
                .build();
    }
}
package com.rental.saas.gateway.filter;

import com.rental.saas.common.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * 用户端JWT认证全局过滤器
 * 专门处理用户端的JWT令牌验证
 * 
 * @author Rental SaaS Team
 */
@Slf4j
public class UserJwtAuthenticationFilter implements GatewayFilter, Ordered {

    private JwtUtil jwtUtil;

    public UserJwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户端白名单路径，无需认证
     */
    private static final List<String> USER_WHITE_LIST = Arrays.asList(
            "/api/user/login",
            "/api/auth/captcha",
            "/api/car-models",  // 车型查询API - 基础数据，无需认证
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        System.out.println("处理用户端请求: " + path);
        
        // 检查是否为用户端API路径
//        if (!path.startsWith("/api/user") && !path.startsWith("/api/auth/captcha")) {
//            // 不是用户端API，跳过此过滤器
//            return chain.filter(exchange);
//        }
        
        // 检查是否在白名单中
        if (isWhiteListPath(path)) {
            return chain.filter(exchange);
        }
        
        // 获取Authorization头
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.warn("用户端请求缺少有效的Authorization头: {}", path);
            return unauthorizedResponse(exchange);
        }
        
        // 提取JWT令牌
        String token = authHeader.substring(7);

        System.out.println("处理用户端请求: " + path + ", token: " + token);
        
        // 验证令牌
        if (!jwtUtil.validateToken(token)) {
            log.warn("用户端JWT令牌验证失败: {}", path);
            return unauthorizedResponse(exchange);
        }
        
        // 检查是否为访问令牌
        if (!jwtUtil.isAccessToken(token)) {
            log.warn("用户端令牌类型错误，需要访问令牌: {}", path);
            return unauthorizedResponse(exchange);
        }
        
        // 从令牌中提取用户信息并设置到请求头
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        
        // 构建新的请求，添加用户信息到请求头
        ServerHttpRequest newRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-Username", username)
                .build();
        
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        
        log.debug("用户端JWT认证成功，用户ID: {}, 用户名: {}", userId, username);
        return chain.filter(newExchange);
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhiteListPath(String path) {
        return USER_WHITE_LIST.stream().anyMatch(path::startsWith);
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        
        String body = "{\"code\": 401, \"message\": \"未授权访问\", \"timestamp\": " + System.currentTimeMillis() + "}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int getOrder() {
        return -99; // 设置稍低的优先级，确保在其他过滤器之前但低于商户端过滤器执行
    }
}
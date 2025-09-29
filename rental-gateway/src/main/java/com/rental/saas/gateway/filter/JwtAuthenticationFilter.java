package com.rental.saas.gateway.filter;

import com.rental.saas.common.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
 * JWT认证全局过滤器
 * 对所有请求进行JWT令牌验证
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 白名单路径，无需认证
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/merchants/apply",
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // 检查是否在白名单中
        if (isWhiteListPath(path)) {
            return chain.filter(exchange);
        }
        
        // 获取Authorization头
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.warn("请求缺少有效的Authorization头: {}", path);
            return unauthorizedResponse(exchange);
        }
        
        // 提取JWT令牌
        String token = authHeader.substring(7);
        
        // 验证令牌
        if (!jwtUtil.validateToken(token)) {
            log.warn("JWT令牌验证失败: {}", path);
            return unauthorizedResponse(exchange);
        }
        
        // 检查是否为访问令牌
        if (!jwtUtil.isAccessToken(token)) {
            log.warn("令牌类型错误，需要访问令牌: {}", path);
            return unauthorizedResponse(exchange);
        }
        
        // 从令牌中提取用户信息并设置到请求头
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        Long tenantId = jwtUtil.getTenantIdFromToken(token);
        
        // 构建新的请求，添加用户信息到请求头
        ServerHttpRequest newRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-Username", username)
                .header("X-Tenant-Id", String.valueOf(tenantId))
                .build();
        
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        
        log.debug("JWT认证成功，用户ID: {}, 用户名: {}, 租户ID: {}", userId, username, tenantId);
        return chain.filter(newExchange);
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhiteListPath(String path) {
        return WHITE_LIST.stream().anyMatch(path::startsWith);
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
        return -100; // 设置高优先级，确保在其他过滤器之前执行
    }
}
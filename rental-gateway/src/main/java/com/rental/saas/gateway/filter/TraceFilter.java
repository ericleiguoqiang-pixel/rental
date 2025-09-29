package com.rental.saas.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 请求追踪过滤器
 * 为每个请求生成唯一的追踪ID
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Component
public class TraceFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 检查请求头中是否已有追踪ID
        String traceId = request.getHeaders().getFirst(TRACE_ID_HEADER);
        if (traceId == null) {
            // 生成新的追踪ID
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        
        // 将追踪ID添加到请求头
        ServerHttpRequest newRequest = request.mutate()
                .header(TRACE_ID_HEADER, traceId)
                .build();
        
        // 将追踪ID添加到响应头
        exchange.getResponse().getHeaders().add(TRACE_ID_HEADER, traceId);
        
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        
        // 记录请求日志
        log.info("请求开始 - TraceId: {}, Method: {}, Path: {}, RemoteAddr: {}", 
                traceId, 
                request.getMethod(), 
                request.getURI().getPath(),
                getClientIp(request));
        
        return chain.filter(newExchange);
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddress() != null ? 
                request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    @Override
    public int getOrder() {
        return -200; // 设置最高优先级，确保最先执行
    }
}
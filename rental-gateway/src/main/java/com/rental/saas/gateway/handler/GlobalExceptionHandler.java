package com.rental.saas.gateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局异常处理器
 * 统一处理网关层的异常
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        // 设置响应头
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        
        // 获取追踪ID
        String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        if (traceId != null) {
            response.getHeaders().add("X-Trace-Id", traceId);
        }
        
        String message;
        HttpStatus status;
        
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            status = HttpStatus.resolve(rse.getStatusCode().value());
            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            message = rse.getReason();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "网关内部错误";
            log.error("网关异常 - TraceId: {}", traceId, ex);
        }
        
        response.setStatusCode(status);
        
        String body = String.format(
                "{\"code\": %d, \"message\": \"%s\", \"traceId\": \"%s\", \"timestamp\": %d}",
                status.value(),
                message != null ? message : status.getReasonPhrase(),
                traceId != null ? traceId : "",
                System.currentTimeMillis()
        );
        
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
package com.rental.saas.mcp.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * 简单的SSE控制器，用于测试SSE端点
 */
@RestController
@RequestMapping("/mcp123")
public class SseController {

    /**
     * SSE测试端点
     * @return Flux of String events
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sse() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "data: SSE测试消息 " + sequence + "\n\n");
    }
}
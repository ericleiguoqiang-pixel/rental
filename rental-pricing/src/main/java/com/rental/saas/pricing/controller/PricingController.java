package com.rental.saas.pricing.controller;

import com.rental.saas.pricing.dto.QuoteRequest;
import com.rental.saas.pricing.dto.QuoteResponse;
import com.rental.saas.pricing.dto.QuoteDetailResponse;
import com.rental.saas.pricing.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 定价控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/pricing")
@Tag(name = "定价服务", description = "报价计算、报价管理等相关接口")
public class PricingController {
    
    @Autowired
    private PricingService pricingService;
    
    @PostMapping("/search")
    @Operation(summary = "搜索报价", description = "根据位置和时间搜索报价")
    public QuoteResponse searchQuotes(@RequestBody QuoteRequest request) {
        log.info("搜索报价请求: date={}, time={}, longitude={}, latitude={}", 
                request.getDate(), request.getTime(), request.getLongitude(), request.getLatitude());
        return pricingService.searchQuotes(request);
    }
    
    @GetMapping("/quote/{id}")
    @Operation(summary = "获取报价详情", description = "根据报价ID获取报价详情")
    public QuoteDetailResponse getQuoteDetail(@PathVariable String id) {
        log.info("获取报价详情请求: id={}", id);
        return pricingService.getQuoteDetail(id);
    }
}
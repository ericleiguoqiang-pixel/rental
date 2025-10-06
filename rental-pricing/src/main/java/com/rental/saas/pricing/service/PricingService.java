package com.rental.saas.pricing.service;

import com.rental.saas.pricing.dto.QuoteRequest;
import com.rental.saas.pricing.dto.QuoteResponse;
import com.rental.api.pricing.dto.QuoteDetailResponse;
import com.rental.api.pricing.entity.Quote;

/**
 * 定价服务接口
 */
public interface PricingService {
    
    /**
     * 根据位置和时间搜索报价
     * @param request 报价请求参数
     * @return 报价响应
     */
    QuoteResponse searchQuotes(QuoteRequest request);
    
    /**
     * 获取报价详情
     * @param quoteId 报价ID
     * @return 报价详情响应
     */
    QuoteDetailResponse getQuoteDetail(String quoteId);
    
    /**
     * 生成报价ID
     * @return 报价ID
     */
    String generateQuoteId();
    
    /**
     * 保存报价到Redis缓存
     * @param quote 报价信息
     */
    void saveQuoteToCache(Quote quote);
    
    /**
     * 从Redis缓存获取报价
     * @param quoteId 报价ID
     * @return 报价信息
     */
    Quote getQuoteFromCache(String quoteId);
}
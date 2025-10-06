package com.rental.api.pricing;

import com.rental.api.pricing.dto.QuoteDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "rental-pricing")
public interface PriceClient {

    /**
     * 获取报价详情
     * @param id
     * @return
     */
    @GetMapping("/api/pricing/quote/{id}")
    QuoteDetailResponse getQuoteDetail(@PathVariable("id") String id);
}

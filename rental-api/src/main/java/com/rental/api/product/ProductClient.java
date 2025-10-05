package com.rental.api.product;

import com.rental.api.product.response.CarModelProductResponse;
import com.rental.saas.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 商品服务Feign客户端
 * 
 * @author Rental SaaS Team
 */
@FeignClient(name = "rental-product-service")
public interface ProductClient {
    
    /**
     * 根据门店ID获取商品列表
     */
    @GetMapping("/api/car-model-products/store/{storeId}")
    ApiResponse<List<CarModelProductResponse>> getProductsByStore(
            @PathVariable("storeId") Long storeId);
    
    /**
     * 根据门店ID和车型ID获取商品
     */
    @GetMapping("/api/car-model-products/store/{storeId}/model/{modelId}")
    ApiResponse<CarModelProductResponse> getProductByStoreAndModel(
            @PathVariable("storeId") Long storeId,
            @PathVariable("modelId") Long modelId);
}
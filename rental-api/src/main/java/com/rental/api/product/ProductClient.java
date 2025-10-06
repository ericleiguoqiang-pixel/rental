package com.rental.api.product;

import com.rental.api.product.response.CarModelProductResponse;
import com.rental.api.product.response.ValueAddedServiceTemplateResponse;
import com.rental.api.product.response.CancellationRuleTemplateResponse;
import com.rental.api.product.response.ServicePolicyTemplateResponse;
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
    @GetMapping("/api/feign/car-model-products/store/{storeId}")
    ApiResponse<List<CarModelProductResponse>> getProductsByStore(
            @PathVariable("storeId") Long storeId);
    
    /**
     * 根据门店ID和车型ID获取商品
     */
    @GetMapping("/api/feign/car-model-products/store/{storeId}/model/{modelId}")
    ApiResponse<CarModelProductResponse> getProductByStoreAndModel(
            @PathVariable("storeId") Long storeId,
            @PathVariable("modelId") Long modelId);
            
    /**
     * 根据模板ID获取增值服务模板详情
     */
    @GetMapping("/api/feign/value-added-service-templates/{id}")
    ApiResponse<ValueAddedServiceTemplateResponse> getValueAddedServiceTemplateById(
            @PathVariable("id") Long id);
            
    /**
     * 根据模板ID获取取消规则模板详情
     */
    @GetMapping("/api/feign/cancellation-rule-templates/{id}")
    ApiResponse<CancellationRuleTemplateResponse> getCancellationRuleTemplateById(
            @PathVariable("id") Long id);
            
    /**
     * 根据模板ID获取服务政策模板详情
     */
    @GetMapping("/api/feign/service-policy-templates/{id}")
    ApiResponse<ServicePolicyTemplateResponse> getServicePolicyTemplateById(
            @PathVariable("id") Long id);
            
    /**
     * 获取所有增值服务模板
     */
    @GetMapping("/api/feign/value-added-service-templates/all")
    ApiResponse<List<ValueAddedServiceTemplateResponse>> getAllValueAddedServiceTemplates();
}
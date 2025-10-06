package com.rental.saas.product.feign;

import com.rental.api.product.ProductClient;
import com.rental.api.product.response.CarModelProductResponse;
import com.rental.api.product.response.ValueAddedServiceTemplateResponse;
import com.rental.api.product.response.CancellationRuleTemplateResponse;
import com.rental.api.product.response.ServicePolicyTemplateResponse;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.product.entity.CarModelProduct;
import com.rental.saas.product.entity.ValueAddedServiceTemplate;
import com.rental.saas.product.entity.CancellationRuleTemplate;
import com.rental.saas.product.entity.ServicePolicyTemplate;
import com.rental.saas.product.service.CarModelProductService;
import com.rental.saas.product.service.ValueAddedServiceTemplateService;
import com.rental.saas.product.service.CancellationRuleTemplateService;
import com.rental.saas.product.service.ServicePolicyTemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductController implements ProductClient {

    @Autowired
    private CarModelProductService carModelProductService;
    
    @Autowired
    private ValueAddedServiceTemplateService valueAddedServiceTemplateService;
    
    @Autowired
    private CancellationRuleTemplateService cancellationRuleTemplateService;
    
    @Autowired
    private ServicePolicyTemplateService servicePolicyTemplateService;

    @Override
    @GetMapping("/api/feign/car-model-products/store/{storeId}")
    public ApiResponse<List<CarModelProductResponse>> getProductsByStore(@PathVariable("storeId") Long storeId) {
        // 查询门店的所有上架商品
        List<CarModelProduct> products = carModelProductService.getOnlineProductsByStore(storeId);
        List<CarModelProductResponse> responses = products.stream().map(product -> {
            CarModelProductResponse response = new CarModelProductResponse();
            BeanUtils.copyProperties(product, response);
            response.setId(product.getId());
            return response;
        }).collect(Collectors.toList());
        
        return ApiResponse.success("查询成功", responses);
    }
    
    @Override
    @GetMapping("/api/feign/car-model-products/store/{storeId}/model/{modelId}")
    public ApiResponse<CarModelProductResponse> getProductByStoreAndModel(
            @PathVariable("storeId") Long storeId,
            @PathVariable("modelId") Long modelId) {
        // 查询门店的指定车型商品（这里简化处理，实际应该根据门店ID和车型ID查询）
        List<CarModelProduct> products = carModelProductService.getOnlineProductsByStore(storeId);
        CarModelProduct product = products.stream()
                .filter(p -> modelId.equals(p.getCarModelId()))
                .findFirst()
                .orElse(null);
        
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        
        CarModelProductResponse response = new CarModelProductResponse();
        BeanUtils.copyProperties(product, response);
        response.setId(product.getId());
        
        return ApiResponse.success("查询成功", response);
    }
    
    @Override
    @GetMapping("/api/feign/value-added-service-templates/{id}")
    public ApiResponse<ValueAddedServiceTemplateResponse> getValueAddedServiceTemplateById(@PathVariable("id") Long id) {
        ValueAddedServiceTemplate template = valueAddedServiceTemplateService.getTemplateById(id);
        if (template == null) {
            return ApiResponse.error("模板不存在");
        }
        
        ValueAddedServiceTemplateResponse response = new ValueAddedServiceTemplateResponse();
        BeanUtils.copyProperties(template, response);
        return ApiResponse.success("查询成功", response);
    }
    
    @Override
    @GetMapping("/api/feign/cancellation-rule-templates/{id}")
    public ApiResponse<CancellationRuleTemplateResponse> getCancellationRuleTemplateById(@PathVariable("id") Long id) {
        CancellationRuleTemplate template = cancellationRuleTemplateService.getTemplateById(id);
        if (template == null) {
            return ApiResponse.error("模板不存在");
        }
        
        CancellationRuleTemplateResponse response = new CancellationRuleTemplateResponse();
        BeanUtils.copyProperties(template, response);
        return ApiResponse.success("查询成功", response);
    }
    
    @Override
    @GetMapping("/api/feign/service-policy-templates/{id}")
    public ApiResponse<ServicePolicyTemplateResponse> getServicePolicyTemplateById(@PathVariable("id") Long id) {
        ServicePolicyTemplate template = servicePolicyTemplateService.getTemplateById(id);
        if (template == null) {
            return ApiResponse.error("模板不存在");
        }
        
        ServicePolicyTemplateResponse response = new ServicePolicyTemplateResponse();
        BeanUtils.copyProperties(template, response);
        return ApiResponse.success("查询成功", response);
    }
    
    @Override
    @GetMapping("/api/feign/value-added-service-templates/all")
    public ApiResponse<List<ValueAddedServiceTemplateResponse>> getAllValueAddedServiceTemplates() {
        List<ValueAddedServiceTemplate> templates = valueAddedServiceTemplateService.getAllTemplates();
        List<ValueAddedServiceTemplateResponse> responses = templates.stream().map(template -> {
            ValueAddedServiceTemplateResponse response = new ValueAddedServiceTemplateResponse();
            BeanUtils.copyProperties(template, response);
            return response;
        }).collect(Collectors.toList());
        
        return ApiResponse.success("查询成功", responses);
    }
}
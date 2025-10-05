package com.rental.saas.product.feign;

import com.rental.api.product.ProductClient;
import com.rental.api.product.response.CarModelProductResponse;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.product.entity.CarModelProduct;
import com.rental.saas.product.service.CarModelProductService;
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

    @Override
    @GetMapping("/api/car-model-products/store/{storeId}")
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
    @GetMapping("/api/car-model-products/store/{storeId}/model/{modelId}")
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
}
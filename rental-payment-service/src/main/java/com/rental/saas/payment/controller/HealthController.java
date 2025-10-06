package com.rental.saas.payment.controller;

import com.rental.saas.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/health")
@Tag(name = "健康检查接口", description = "服务健康检查相关接口")
public class HealthController {

    /**
     * 健康检查接口
     */
    @GetMapping
    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    public ApiResponse<String> healthCheck() {
        log.info("健康检查接口被调用");
        return ApiResponse.success("Payment Service is running");
    }
}
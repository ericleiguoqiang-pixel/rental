package com.rental.saas.mcp.controller;

import com.rental.api.basedata.BaseDataClient;
import com.rental.api.basedata.response.StoreResponse;
import com.rental.saas.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * MCP测试控制器
 * 用于验证MCP工具是否正常工作
 *
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/test/mcp")
@RequiredArgsConstructor
public class McpTestController {

    private final BaseDataClient baseDataClient;

    /**
     * 测试获取门店列表
     */
    @GetMapping("/stores")
    public ApiResponse<List<StoreResponse>> testGetStores() {
        try {
            log.info("测试获取门店列表");
            ApiResponse<List<StoreResponse>> response = baseDataClient.getAllStores(1L);
            return response;
        } catch (Exception e) {
            log.error("测试获取门店列表异常: ", e);
            return ApiResponse.error("测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    public String healthCheck() {
        return "MCP Gateway is running";
    }
}
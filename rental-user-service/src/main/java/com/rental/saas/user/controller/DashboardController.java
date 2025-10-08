package com.rental.saas.user.controller;

import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.user.dto.DashboardStats;
import com.rental.saas.user.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 商户端仪表盘控制器
 * 提供商户首页的统计数据
 *
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "商户仪表盘接口", description = "商户端仪表盘相关接口")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取仪表盘统计数据", description = "获取商户首页的统计数据，包括订单、收入、车辆等信息")
    public ApiResponse<DashboardStats> getDashboardStats(@RequestHeader("X-Tenant-Id") Long tenantId) {
        log.info("获取仪表盘统计数据: tenantId={}", tenantId);
        
        try {
            DashboardStats stats = dashboardService.getDashboardStats(tenantId);
            log.info("获取仪表盘统计数据成功: tenantId={}", tenantId);
            return ApiResponse.success(stats);
        } catch (Exception e) {
            log.error("获取仪表盘统计数据失败: tenantId={}, error={}", tenantId, e.getMessage(), e);
            return ApiResponse.error("获取仪表盘统计数据失败");
        }
    }
}
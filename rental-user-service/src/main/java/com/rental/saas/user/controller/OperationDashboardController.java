package com.rental.saas.user.controller;

import com.rental.api.basedata.BaseDataClient;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.user.dto.DashboardOverviewStats;
import com.rental.saas.user.dto.DashboardAuditStats;
import com.rental.saas.user.service.MerchantApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 运营仪表盘控制器
 * 提供运营后台首页的统计数据
 *
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/operation/dashboard")
@Tag(name = "运营仪表盘接口", description = "运营后台仪表盘相关接口")
@RequiredArgsConstructor
public class OperationDashboardController {

    private final MerchantApplicationService merchantApplicationService;
    private final BaseDataClient baseDataClient;

    /**
     * 获取运营概览数据
     */
    @GetMapping("/overview")
    @Operation(summary = "获取运营概览数据", description = "获取运营后台首页的概览统计数据")
    public ApiResponse<DashboardOverviewStats> getOverviewStats() {
        log.info("获取运营概览数据");
        
        try {
            DashboardOverviewStats stats = new DashboardOverviewStats();
            
            // 获取待审核商户数
            int pendingMerchants = merchantApplicationService.countPendingApplications();
            stats.setPendingMerchants(pendingMerchants);
            
            // 获取待审核车辆数
            int pendingVehicles = baseDataClient.countPendingVehicles().getData();
            stats.setPendingVehicles(pendingVehicles);
            
            // 获取待审核门店数
            int pendingStores = baseDataClient.countPendingStores().getData();
            stats.setPendingStores(pendingStores);
            
            // 获取总审核数（这里简化处理，实际应该统计历史审核总数）
            int totalAudits = pendingMerchants + pendingVehicles + pendingStores;
            stats.setTotalAudits(totalAudits);
            
            log.info("获取运营概览数据成功");
            return ApiResponse.success(stats);
        } catch (Exception e) {
            log.error("获取运营概览数据失败: error={}", e.getMessage(), e);
            return ApiResponse.error("获取运营概览数据失败");
        }
    }
    
    /**
     * 获取审核统计数据
     */
    @GetMapping("/audit-stats")
    @Operation(summary = "获取审核统计数据", description = "获取运营后台的审核统计数据")
    public ApiResponse<DashboardAuditStats> getAuditStats() {
        log.info("获取审核统计数据");
        
        try {
            DashboardAuditStats stats = new DashboardAuditStats();
            
            // 获取各状态商户申请数
            Map<String, Integer> merchantStats = merchantApplicationService.countApplicationsByStatus();
            stats.setMerchantStats(merchantStats);
            
            // 获取各状态车辆数
            Map<String, Integer> vehicleStats = baseDataClient.countVehiclesByAuditStatus().getData();
            stats.setVehicleStats(vehicleStats);
            
            // 获取各状态门店数
            Map<String, Integer> storeStats = baseDataClient.countStoresByAuditStatus().getData();
            stats.setStoreStats(storeStats);
            
            log.info("获取审核统计数据成功");
            return ApiResponse.success(stats);
        } catch (Exception e) {
            log.error("获取审核统计数据失败: error={}", e.getMessage(), e);
            return ApiResponse.error("获取审核统计数据失败");
        }
    }
}
package com.rental.saas.user.service;

import com.rental.saas.user.dto.DashboardStats;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {
    
    /**
     * 获取仪表盘统计数据
     * @param tenantId 租户ID
     * @return 仪表盘统计数据
     */
    DashboardStats getDashboardStats(Long tenantId);
}
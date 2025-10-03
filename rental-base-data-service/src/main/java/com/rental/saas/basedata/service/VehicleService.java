package com.rental.saas.basedata.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.basedata.dto.request.VehicleCreateRequest;
import com.rental.saas.basedata.dto.request.VehicleUpdateRequest;
import com.rental.api.basedata.response.VehicleResponse;
import com.rental.saas.basedata.entity.Vehicle;

import java.util.List;
import java.util.Map;

/**
 * 车辆服务接口
 * 
 * @author Rental SaaS Team
 */
public interface VehicleService {

    /**
     * 创建车辆
     */
    Long createVehicle(VehicleCreateRequest request, Long tenantId);

    /**
     * 更新车辆信息
     */
    void updateVehicle(Long id, VehicleUpdateRequest request, Long tenantId);

    /**
     * 删除车辆
     */
    void deleteVehicle(Long id, Long tenantId);

    /**
     * 根据ID查询车辆
     */
    VehicleResponse getVehicleById(Long id, Long tenantId);

    /**
     * 分页查询车辆列表
     */
    Page<VehicleResponse> getVehicleList(int current, int size, Long storeId, Long carModelId,
                                         Integer vehicleStatus, Integer auditStatus,
                                         Integer onlineStatus, Long tenantId);

    /**
     * 查询门店车辆列表
     */
    List<VehicleResponse> getVehiclesByStore(Long storeId, Long tenantId);

    /**
     * 查询可用车辆列表
     */
    List<VehicleResponse> getAvailableVehicles(Long tenantId, Long storeId);

    /**
     * 根据车型查询车辆
     */
    List<VehicleResponse> getVehiclesByCarModel(Long carModelId);

    /**
     * 根据车牌号查询车辆
     */
    VehicleResponse getVehicleByLicensePlate(String licensePlate, Long tenantId);

    /**
     * 车辆上架
     */
    void onlineVehicle(Long id, Long tenantId);

    /**
     * 车辆下架
     */
    void offlineVehicle(Long id, Long tenantId);

    /**
     * 车辆审核
     */
    void auditVehicle(Long id, Integer auditStatus, String auditRemark, Long auditorId);

    /**
     * 更新车辆状态
     */
    void updateVehicleStatus(Long id, Integer vehicleStatus, Long tenantId);

    /**
     * 更新车辆里程
     */
    void updateMileage(Long id, Integer mileage, Long tenantId);

    /**
     * 统计门店车辆数量
     */
    int countVehiclesByStore(Long storeId);

    /**
     * 统计租户车辆数量
     */
    int countVehicles(Long tenantId);

    /**
     * 统计各状态车辆数量
     */
    Map<String, Integer> countVehiclesByStatus(Long tenantId);

    /**
     * 检查车牌号是否重复
     */
    boolean checkLicensePlateExists(String licensePlate, Long excludeId);

    /**
     * 检查车架号是否重复
     */
    boolean checkVinExists(String vin, Long excludeId);
    
    /**
     * 获取待审核车辆列表（运营）
     */
    IPage<Vehicle> getPendingVehicles(Integer current, Integer size);
    
    /**
     * 获取所有车辆列表（运营）
     */
    IPage<Vehicle> getAllVehicles(Integer current, Integer size, String status);
    
    /**
     * 根据ID获取车辆（运营）
     */
    Vehicle getById(Long id);
    
    /**
     * 审核车辆（运营）
     */
    boolean auditVehicle(Long id, String status, String reason);
}
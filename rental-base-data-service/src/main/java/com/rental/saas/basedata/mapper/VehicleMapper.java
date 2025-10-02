package com.rental.saas.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.saas.basedata.entity.Vehicle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车辆数据访问层
 * 
 * @author Rental SaaS Team
 */
@Mapper
public interface VehicleMapper extends BaseMapper<Vehicle> {

    /**
     * 根据租户ID查询车辆列表
     */
    List<Vehicle> findByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 根据门店ID查询车辆列表
     */
    List<Vehicle> findByStoreId(@Param("storeId") Long storeId);

    /**
     * 根据车型ID查询车辆列表
     */
    List<Vehicle> findByCarModelId(@Param("carModelId") Long carModelId);

    /**
     * 查询可用车辆列表
     */
    List<Vehicle> findAvailableVehicles(@Param("tenantId") Long tenantId, @Param("storeId") Long storeId);

    /**
     * 根据车牌号查询车辆
     */
    Vehicle findByLicensePlate(@Param("licensePlate") String licensePlate, @Param("tenantId") Long tenantId);

    /**
     * 根据车架号查询车辆
     */
    Vehicle findByVin(@Param("vin") String vin);

    /**
     * 统计门店车辆数量
     */
    int countByStoreId(@Param("storeId") Long storeId);

    /**
     * 统计租户车辆数量
     */
    int countByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 统计各状态车辆数量
     */
    List<Object> countByVehicleStatus(@Param("tenantId") Long tenantId);

    /**
     * 检查车牌号是否重复
     */
    int checkLicensePlateExists(@Param("licensePlate") String licensePlate, 
                                @Param("excludeId") Long excludeId);

    /**
     * 检查车架号是否重复
     */
    int checkVinExists(@Param("vin") String vin, @Param("excludeId") Long excludeId);
}
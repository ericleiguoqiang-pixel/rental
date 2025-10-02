package com.rental.saas.basedata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.basedata.dto.request.VehicleCreateRequest;
import com.rental.saas.basedata.dto.request.VehicleUpdateRequest;
import com.rental.saas.basedata.dto.response.VehicleResponse;
import com.rental.saas.basedata.entity.CarModel;
import com.rental.saas.basedata.entity.Store;
import com.rental.saas.basedata.entity.Vehicle;
import com.rental.saas.basedata.mapper.CarModelMapper;
import com.rental.saas.basedata.mapper.StoreMapper;
import com.rental.saas.basedata.mapper.VehicleMapper;
import com.rental.saas.basedata.service.VehicleService;
import com.rental.saas.common.exception.BusinessException;
import com.rental.saas.common.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 车辆服务实现类
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper vehicleMapper;
    private final StoreMapper storeMapper;
    private final CarModelMapper carModelMapper;

    @Override
    @Transactional
    public Long createVehicle(VehicleCreateRequest request, Long tenantId) {
        log.info("创建车辆，租户ID: {}, 车牌号: {}", tenantId, request.getLicensePlate());

        // 验证门店存在并属于当前租户
        validateStoreExists(request.getStoreId(), tenantId);

        // 验证车型存在
        validateCarModelExists(request.getCarModelId());

        // 检查车牌号是否重复
        if (checkLicensePlateExists(request.getLicensePlate(), null)) {
            throw new BusinessException(ResponseCode.LICENSE_PLATE_DUPLICATE);
        }

        // 检查车架号是否重复
        if (checkVinExists(request.getVin(), null)) {
            throw new BusinessException(ResponseCode.VIN_DUPLICATE);
        }

        // 创建车辆实体
        Vehicle vehicle = new Vehicle();
        BeanUtils.copyProperties(request, vehicle);
        vehicle.setTenantId(tenantId);
        vehicle.setAuditStatus(0); // 待审核
        vehicle.setOnlineStatus(0); // 下架状态
        vehicle.setVehicleStatus(1); // 空闲状态
        vehicle.setCreatedTime(LocalDateTime.now());
        vehicle.setUpdatedTime(LocalDateTime.now());

        // 保存车辆
        vehicleMapper.insert(vehicle);

        log.info("车辆创建成功，ID: {}", vehicle.getId());
        return vehicle.getId();
    }

    @Override
    @Transactional
    public void updateVehicle(Long id, VehicleUpdateRequest request, Long tenantId) {
        log.info("更新车辆信息，ID: {}, 租户ID: {}", id, tenantId);

        // 查询车辆
        Vehicle vehicle = getVehicleEntity(id, tenantId);

        // 验证门店
        if (request.getStoreId() != null) {
            validateStoreExists(request.getStoreId(), tenantId);
            vehicle.setStoreId(request.getStoreId());
        }

        // 验证车型
        if (request.getCarModelId() != null) {
            validateCarModelExists(request.getCarModelId());
            vehicle.setCarModelId(request.getCarModelId());
        }

        // 检查车牌号重复
        if (StringUtils.hasText(request.getLicensePlate()) && 
            checkLicensePlateExists(request.getLicensePlate(), id)) {
            throw new BusinessException(ResponseCode.LICENSE_PLATE_DUPLICATE);
        }

        // 更新车辆信息
        if (StringUtils.hasText(request.getLicensePlate())) {
            vehicle.setLicensePlate(request.getLicensePlate());
        }
        if (request.getLicenseType() != null) {
            vehicle.setLicenseType(request.getLicenseType());
        }
        if (request.getRegisterDate() != null) {
            vehicle.setRegisterDate(request.getRegisterDate());
        }
        if (StringUtils.hasText(request.getEngineNo())) {
            vehicle.setEngineNo(request.getEngineNo());
        }
        if (request.getUsageNature() != null) {
            vehicle.setUsageNature(request.getUsageNature());
        }
        if (request.getVehicleStatus() != null) {
            vehicle.setVehicleStatus(request.getVehicleStatus());
        }
        if (request.getMileage() != null) {
            vehicle.setMileage(request.getMileage());
        }
        vehicle.setUpdatedTime(LocalDateTime.now());

        vehicleMapper.updateById(vehicle);
        log.info("车辆信息更新成功");
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id, Long tenantId) {
        log.info("删除车辆，ID: {}, 租户ID: {}", id, tenantId);

        // 查询车辆
        Vehicle vehicle = getVehicleEntity(id, tenantId);

        // 检查车辆状态
        if (vehicle.getVehicleStatus() == 2) {
            throw new BusinessException(ResponseCode.VEHICLE_ALREADY_RENTED);
        }

        // 逻辑删除车辆
        vehicleMapper.deleteById(id);
        log.info("车辆删除成功");
    }

    @Override
    public VehicleResponse getVehicleById(Long id, Long tenantId) {
        Vehicle vehicle = getVehicleEntity(id, tenantId);
        return convertToResponse(vehicle);
    }

    @Override
    public Page<VehicleResponse> getVehicleList(int current, int size, Long storeId, Long carModelId,
                                                Integer vehicleStatus, Integer auditStatus, 
                                                Integer onlineStatus, Long tenantId) {
        log.info("分页查询车辆列表，当前页: {}, 页大小: {}, 租户ID: {}", current, size, tenantId);

        LambdaQueryWrapper<Vehicle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vehicle::getTenantId, tenantId)
               .eq(Vehicle::getDeleted, 0)
               .eq(storeId != null, Vehicle::getStoreId, storeId)
               .eq(carModelId != null, Vehicle::getCarModelId, carModelId)
               .eq(vehicleStatus != null, Vehicle::getVehicleStatus, vehicleStatus)
               .eq(auditStatus != null, Vehicle::getAuditStatus, auditStatus)
               .eq(onlineStatus != null, Vehicle::getOnlineStatus, onlineStatus)
               .orderByDesc(Vehicle::getCreatedTime);

        Page<Vehicle> page = new Page<>(current, size);
        Page<Vehicle> result = vehicleMapper.selectPage(page, wrapper);

        // 转换为响应对象
        List<VehicleResponse> responseList = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        Page<VehicleResponse> responsePage = new Page<>(current, size, result.getTotal());
        responsePage.setRecords(responseList);
        return responsePage;
    }

    @Override
    public List<VehicleResponse> getVehiclesByStore(Long storeId, Long tenantId) {
        List<Vehicle> vehicles = vehicleMapper.findByStoreId(storeId);
        return vehicles.stream()
                .filter(v -> v.getTenantId().equals(tenantId))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleResponse> getAvailableVehicles(Long tenantId, Long storeId) {
        List<Vehicle> vehicles = vehicleMapper.findAvailableVehicles(tenantId, storeId);
        return vehicles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleResponse> getVehiclesByCarModel(Long carModelId) {
        List<Vehicle> vehicles = vehicleMapper.findByCarModelId(carModelId);
        return vehicles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponse getVehicleByLicensePlate(String licensePlate, Long tenantId) {
        Vehicle vehicle = vehicleMapper.findByLicensePlate(licensePlate, tenantId);
        if (vehicle == null) {
            throw new BusinessException(ResponseCode.VEHICLE_NOT_FOUND);
        }
        return convertToResponse(vehicle);
    }

    @Override
    @Transactional
    public void onlineVehicle(Long id, Long tenantId) {
        log.info("车辆上架，ID: {}", id);
        
        Vehicle vehicle = getVehicleEntity(id, tenantId);
        
        // 检查审核状态
        if (vehicle.getAuditStatus() != 1) {
            throw new BusinessException(ResponseCode.VEHICLE_NOT_AVAILABLE);
        }
        
        vehicle.setOnlineStatus(1);
        vehicle.setUpdatedTime(LocalDateTime.now());
        vehicleMapper.updateById(vehicle);
        
        log.info("车辆上架成功");
    }

    @Override
    @Transactional
    public void offlineVehicle(Long id, Long tenantId) {
        log.info("车辆下架，ID: {}", id);
        
        Vehicle vehicle = getVehicleEntity(id, tenantId);
        vehicle.setOnlineStatus(0);
        vehicle.setUpdatedTime(LocalDateTime.now());
        vehicleMapper.updateById(vehicle);
        
        log.info("车辆下架成功");
    }

    @Override
    @Transactional
    public void auditVehicle(Long id, Integer auditStatus, String auditRemark, Long auditorId) {
        log.info("车辆审核，ID: {}, 审核状态: {}", id, auditStatus);
        
        Vehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw new BusinessException(ResponseCode.VEHICLE_NOT_FOUND);
        }
        
        vehicle.setAuditStatus(auditStatus);
        vehicle.setUpdatedTime(LocalDateTime.now());
        vehicleMapper.updateById(vehicle);
        
        log.info("车辆审核完成");
    }

    @Override
    @Transactional
    public void updateVehicleStatus(Long id, Integer vehicleStatus, Long tenantId) {
        log.info("更新车辆状态，ID: {}, 状态: {}", id, vehicleStatus);
        
        Vehicle vehicle = getVehicleEntity(id, tenantId);
        vehicle.setVehicleStatus(vehicleStatus);
        vehicle.setUpdatedTime(LocalDateTime.now());
        vehicleMapper.updateById(vehicle);
        
        log.info("车辆状态更新成功");
    }

    @Override
    @Transactional
    public void updateMileage(Long id, Integer mileage, Long tenantId) {
        log.info("更新车辆里程，ID: {}, 里程: {}", id, mileage);
        
        Vehicle vehicle = getVehicleEntity(id, tenantId);
        vehicle.setMileage(mileage);
        vehicle.setUpdatedTime(LocalDateTime.now());
        vehicleMapper.updateById(vehicle);
        
        log.info("车辆里程更新成功");
    }

    @Override
    public int countVehiclesByStore(Long storeId) {
        return vehicleMapper.countByStoreId(storeId);
    }

    @Override
    public int countVehicles(Long tenantId) {
        return vehicleMapper.countByTenantId(tenantId);
    }

    @Override
    public Map<String, Integer> countVehiclesByStatus(Long tenantId) {
        List<Object> statusCounts = vehicleMapper.countByVehicleStatus(tenantId);
        Map<String, Integer> result = new HashMap<>();
        
        // 初始化所有状态计数
        result.put("idle", 0);      // 空闲
        result.put("rented", 0);    // 租出
        result.put("maintenance", 0); // 维修
        result.put("scrapped", 0);  // 报废
        
        // TODO: 处理数据库返回的状态统计结果
        
        return result;
    }

    @Override
    public boolean checkLicensePlateExists(String licensePlate, Long excludeId) {
        int count = vehicleMapper.checkLicensePlateExists(licensePlate, excludeId);
        return count > 0;
    }

    @Override
    public boolean checkVinExists(String vin, Long excludeId) {
        int count = vehicleMapper.checkVinExists(vin, excludeId);
        return count > 0;
    }

    /**
     * 验证门店存在并属于当前租户
     */
    private void validateStoreExists(Long storeId, Long tenantId) {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getId, storeId)
               .eq(Store::getTenantId, tenantId)
               .eq(Store::getDeleted, 0);
        
        Store store = storeMapper.selectOne(wrapper);
        if (store == null) {
            throw new BusinessException(ResponseCode.STORE_NOT_FOUND);
        }
    }

    /**
     * 验证车型存在
     */
    private void validateCarModelExists(Long carModelId) {
        CarModel carModel = carModelMapper.selectById(carModelId);
        if (carModel == null) {
            throw new BusinessException(ResponseCode.CAR_MODEL_NOT_FOUND);
        }
    }

    /**
     * 获取车辆实体
     */
    private Vehicle getVehicleEntity(Long id, Long tenantId) {
        LambdaQueryWrapper<Vehicle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Vehicle::getId, id)
               .eq(Vehicle::getTenantId, tenantId)
               .eq(Vehicle::getDeleted, 0);
        
        Vehicle vehicle = vehicleMapper.selectOne(wrapper);
        if (vehicle == null) {
            throw new BusinessException(ResponseCode.VEHICLE_NOT_FOUND);
        }
        return vehicle;
    }

    /**
     * 转换为响应对象
     */
    private VehicleResponse convertToResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        BeanUtils.copyProperties(vehicle, response);
        
        // 设置状态描述
        response.setLicenseTypeDesc(getLicenseTypeDesc(vehicle.getLicenseType()));
        response.setUsageNatureDesc(getUsageNatureDesc(vehicle.getUsageNature()));
        response.setAuditStatusDesc(getAuditStatusDesc(vehicle.getAuditStatus()));
        response.setOnlineStatusDesc(getOnlineStatusDesc(vehicle.getOnlineStatus()));
        response.setVehicleStatusDesc(getVehicleStatusDesc(vehicle.getVehicleStatus()));
        
        // 设置门店信息
        setStoreInfo(response, vehicle.getStoreId());
        
        // 设置车型信息
        setCarModelInfo(response, vehicle.getCarModelId());
        
        return response;
    }

    /**
     * 设置门店信息
     */
    private void setStoreInfo(VehicleResponse response, Long storeId) {
        Store store = storeMapper.selectById(storeId);
        if (store != null) {
            response.setStoreName(store.getStoreName());
        }
    }

    /**
     * 设置车型信息
     */
    private void setCarModelInfo(VehicleResponse response, Long carModelId) {
        CarModel carModel = carModelMapper.selectById(carModelId);
        if (carModel != null) {
            VehicleResponse.CarModelInfo carModelInfo = new VehicleResponse.CarModelInfo();
            carModelInfo.setBrand(carModel.getBrand());
            carModelInfo.setSeries(carModel.getSeries());
            carModelInfo.setModel(carModel.getModel());
            carModelInfo.setYear(carModel.getYear());
            carModelInfo.setSeatCount(carModel.getSeatCount());
            carModelInfo.setTransmission(carModel.getTransmission());
            carModelInfo.setTransmissionDesc(getTransmissionDesc(carModel.getTransmission()));
            carModelInfo.setDriveType(carModel.getDriveType());
            carModelInfo.setDriveTypeDesc(getDriveTypeDesc(carModel.getDriveType()));
            response.setCarModel(carModelInfo);
        }
    }

    private String getLicenseTypeDesc(Integer type) {
        switch (type) {
            case 1: return "普通";
            case 2: return "京牌";
            case 3: return "沪牌";
            case 4: return "深牌";
            case 5: return "粤A牌";
            case 6: return "杭州牌";
            default: return "未知";
        }
    }

    private String getUsageNatureDesc(Integer nature) {
        switch (nature) {
            case 1: return "营运";
            case 2: return "非营运";
            default: return "未知";
        }
    }

    private String getAuditStatusDesc(Integer status) {
        switch (status) {
            case 0: return "待审核";
            case 1: return "审核通过";
            case 2: return "审核拒绝";
            default: return "未知状态";
        }
    }

    private String getOnlineStatusDesc(Integer status) {
        switch (status) {
            case 0: return "下架";
            case 1: return "上架";
            default: return "未知状态";
        }
    }

    private String getVehicleStatusDesc(Integer status) {
        switch (status) {
            case 1: return "空闲";
            case 2: return "租出";
            case 3: return "维修";
            case 4: return "报废";
            default: return "未知状态";
        }
    }

    private String getTransmissionDesc(Integer transmission) {
        switch (transmission) {
            case 1: return "自动";
            case 2: return "手动";
            default: return "未知";
        }
    }

    private String getDriveTypeDesc(Integer driveType) {
        switch (driveType) {
            case 1: return "燃油";
            case 2: return "纯电";
            case 3: return "混动";
            default: return "未知";
        }
    }
    
    // =============== 运营相关方法 ===============
    
    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<Vehicle> getPendingVehicles(Integer current, Integer size) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Vehicle> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        // 只查询待审核的车辆（审核状态为0）
        wrapper.eq(Vehicle::getAuditStatus, 0);
        wrapper.orderByDesc(Vehicle::getId);
        
        com.baomidou.mybatisplus.core.metadata.IPage<Vehicle> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        return vehicleMapper.selectPage(page, wrapper);
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<Vehicle> getAllVehicles(Integer current, Integer size, String status) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Vehicle> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        // 根据状态筛选
        if (status != null && !status.isEmpty()) {
            try {
                Integer statusValue = Integer.valueOf(status);
                wrapper.eq(Vehicle::getAuditStatus, statusValue);
            } catch (NumberFormatException e) {
                log.warn("状态参数格式错误: {}", status);
            }
        }
        wrapper.orderByDesc(Vehicle::getId);
        
        com.baomidou.mybatisplus.core.metadata.IPage<Vehicle> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        return vehicleMapper.selectPage(page, wrapper);
    }

    @Override
    public Vehicle getById(Long id) {
        return vehicleMapper.selectById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean auditVehicle(Long id, String status, String reason) {
        // 查询车辆
        Vehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            log.warn("车辆不存在: id={}", id);
            return false;
        }

        // 更新状态
        Integer statusValue;
        switch (status) {
            case "APPROVED":  // 审核通过
                statusValue = 1;
                break;
            case "REJECTED":  // 审核拒绝
                statusValue = 2;
                break;
            default:
                log.warn("无效的审核状态: {}", status);
                return false;
        }

        // 更新车辆审核状态
        vehicle.setAuditStatus(statusValue);
        vehicle.setAuditRemark(reason);
        vehicle.setAuditTime(java.time.LocalDateTime.now());
        // 这里应该设置审核人ID，但运营管理员没有具体ID，暂时设为0
        vehicle.setAuditorId(0L);

        int result = vehicleMapper.updateById(vehicle);
        return result > 0;
    }
}
package com.rental.saas.basedata.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.basedata.dto.request.CarModelCreateRequest;
import com.rental.saas.basedata.dto.response.CarModelResponse;
import com.rental.saas.basedata.entity.CarModel;
import com.rental.saas.basedata.mapper.CarModelMapper;
import com.rental.saas.basedata.service.CarModelService;
import com.rental.saas.common.exception.BusinessException;
import com.rental.saas.common.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 车型服务实现类
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CarModelServiceImpl implements CarModelService {

    private final CarModelMapper carModelMapper;

    @Override
    @Transactional
    public Long createCarModel(CarModelCreateRequest request) {
        log.info("创建车型，品牌: {}, 车系: {}, 车型: {}", 
                request.getBrand(), request.getSeries(), request.getModel());

        // 检查车型是否重复
        if (checkCarModelExists(request.getBrand(), request.getSeries(), 
                                request.getModel(), request.getYear(), null)) {
            throw new BusinessException(ResponseCode.DATA_ALREADY_EXISTS, "车型已存在");
        }

        // 创建车型实体
        CarModel carModel = new CarModel();
        BeanUtils.copyProperties(request, carModel);
        carModel.setCreatedTime(LocalDateTime.now());
        carModel.setUpdatedTime(LocalDateTime.now());

        // 保存车型
        carModelMapper.insert(carModel);

        log.info("车型创建成功，ID: {}", carModel.getId());
        return carModel.getId();
    }

    @Override
    @Transactional
    public void updateCarModel(Long id, CarModelCreateRequest request) {
        log.info("更新车型信息，ID: {}", id);

        // 查询车型
        CarModel carModel = getCarModelEntity(id);

        // 检查车型是否重复
        if (checkCarModelExists(request.getBrand(), request.getSeries(), 
                                request.getModel(), request.getYear(), id)) {
            throw new BusinessException(ResponseCode.DATA_ALREADY_EXISTS, "车型已存在");
        }

        // 更新车型信息
        BeanUtils.copyProperties(request, carModel);
        carModel.setUpdatedTime(LocalDateTime.now());

        carModelMapper.updateById(carModel);
        log.info("车型信息更新成功");
    }

    @Override
    @Transactional
    public void deleteCarModel(Long id) {
        log.info("删除车型，ID: {}", id);

        // 查询车型
        CarModel carModel = getCarModelEntity(id);

        // 检查是否有关联的车辆
        // TODO: 调用车辆服务检查

        // 逻辑删除车型
        carModelMapper.deleteById(id);
        log.info("车型删除成功");
    }

    @Override
    public CarModelResponse getCarModelById(Long id) {
        CarModel carModel = getCarModelEntity(id);
        return convertToResponse(carModel);
    }

    @Override
    public Page<CarModelResponse> getCarModelList(int current, int size, String brand, String series,
                                                  Integer seatCount, Integer transmission, Integer driveType,
                                                  Integer startYear, Integer endYear) {
        log.info("分页查询车型列表，当前页: {}, 页大小: {}", current, size);

        LambdaQueryWrapper<CarModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarModel::getDeleted, 0)
               .like(StringUtils.hasText(brand), CarModel::getBrand, brand)
               .like(StringUtils.hasText(series), CarModel::getSeries, series)
               .eq(seatCount != null, CarModel::getSeatCount, seatCount)
               .eq(transmission != null, CarModel::getTransmission, transmission)
               .eq(driveType != null, CarModel::getDriveType, driveType)
               .ge(startYear != null, CarModel::getYear, startYear)
               .le(endYear != null, CarModel::getYear, endYear)
               .orderByDesc(CarModel::getCreatedTime);

        Page<CarModel> page = new Page<>(current, size);
        Page<CarModel> result = carModelMapper.selectPage(page, wrapper);

        // 转换为响应对象
        List<CarModelResponse> responseList = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        Page<CarModelResponse> responsePage = new Page<>(current, size, result.getTotal());
        responsePage.setRecords(responseList);
        return responsePage;
    }

    @Override
    public List<CarModelResponse> getAllCarModels() {
        LambdaQueryWrapper<CarModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarModel::getDeleted, 0)
               .orderByAsc(CarModel::getBrand)
               .orderByAsc(CarModel::getSeries)
               .orderByDesc(CarModel::getYear);

        List<CarModel> carModels = carModelMapper.selectList(wrapper);
        return carModels.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarModelResponse> getCarModelsByBrand(String brand) {
        List<CarModel> carModels = carModelMapper.findByBrand(brand);
        return carModels.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarModelResponse> getCarModelsBySeries(String series) {
        List<CarModel> carModels = carModelMapper.findBySeries(series);
        return carModels.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarModelResponse> getCarModelsByBrandAndSeries(String brand, String series) {
        List<CarModel> carModels = carModelMapper.findByBrandAndSeries(brand, series);
        return carModels.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarModelResponse> getCarModelsBySeatCount(Integer seatCount) {
        List<CarModel> carModels = carModelMapper.findBySeatCount(seatCount);
        return carModels.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarModelResponse> getCarModelsByTransmission(Integer transmission) {
        List<CarModel> carModels = carModelMapper.findByTransmission(transmission);
        return carModels.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarModelResponse> getCarModelsByDriveType(Integer driveType) {
        List<CarModel> carModels = carModelMapper.findByDriveType(driveType);
        return carModels.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllBrands() {
        return carModelMapper.findAllBrands();
    }

    @Override
    public List<String> getSeriesByBrand(String brand) {
        return carModelMapper.findSeriesByBrand(brand);
    }

    @Override
    public List<CarModelResponse> getCarModelsByConditions(String brand, String series, Integer seatCount,
                                                           Integer transmission, Integer driveType,
                                                           Integer startYear, Integer endYear) {
        List<CarModel> carModels = carModelMapper.findByConditions(
                brand, series, seatCount, transmission, driveType, startYear, endYear);
        return carModels.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkCarModelExists(String brand, String series, String model, Integer year, Long excludeId) {
        int count = carModelMapper.checkCarModelExists(brand, series, model, year, excludeId);
        return count > 0;
    }

    @Override
    @Transactional
    public void batchImportCarModels(List<CarModelCreateRequest> requests) {
        log.info("批量导入车型，数量: {}", requests.size());

        for (CarModelCreateRequest request : requests) {
            try {
                // 检查车型是否已存在
                if (!checkCarModelExists(request.getBrand(), request.getSeries(), 
                                         request.getModel(), request.getYear(), null)) {
                    createCarModel(request);
                } else {
                    log.warn("车型已存在，跳过导入: {} {} {} {}", 
                            request.getBrand(), request.getSeries(), request.getModel(), request.getYear());
                }
            } catch (Exception e) {
                log.error("导入车型失败: {} {} {} {}, 错误: {}", 
                         request.getBrand(), request.getSeries(), request.getModel(), request.getYear(), e.getMessage());
            }
        }

        log.info("批量导入车型完成");
    }

    /**
     * 获取车型实体
     */
    private CarModel getCarModelEntity(Long id) {
        CarModel carModel = carModelMapper.selectById(id);
        if (carModel == null || carModel.getDeleted() == 1) {
            throw new BusinessException(ResponseCode.CAR_MODEL_NOT_FOUND);
        }
        return carModel;
    }

    /**
     * 转换为响应对象
     */
    private CarModelResponse convertToResponse(CarModel carModel) {
        CarModelResponse response = new CarModelResponse();
        BeanUtils.copyProperties(carModel, response);
        
        // 设置状态描述
        response.setTransmissionDesc(getTransmissionDesc(carModel.getTransmission()));
        response.setDriveTypeDesc(getDriveTypeDesc(carModel.getDriveType()));
        
        // TODO: 设置车辆数量统计
        // response.setVehicleCount(vehicleService.countVehiclesByCarModel(carModel.getId()));
        
        return response;
    }

    /**
     * 获取档位类型描述
     */
    private String getTransmissionDesc(Integer transmission) {
        switch (transmission) {
            case 1: return "自动";
            case 2: return "手动";
            default: return "未知";
        }
    }

    /**
     * 获取驱动类型描述
     */
    private String getDriveTypeDesc(Integer driveType) {
        switch (driveType) {
            case 1: return "燃油";
            case 2: return "纯电";
            case 3: return "混动";
            default: return "未知";
        }
    }
}
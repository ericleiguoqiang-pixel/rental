package com.rental.saas.basedata.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.basedata.dto.request.CarModelCreateRequest;
import com.rental.api.basedata.response.CarModelResponse;

import java.util.List;

/**
 * 车型服务接口
 * 
 * @author Rental SaaS Team
 */
public interface CarModelService {

    /**
     * 创建车型
     */
    Long createCarModel(CarModelCreateRequest request);

    /**
     * 更新车型信息
     */
    void updateCarModel(Long id, CarModelCreateRequest request);

    /**
     * 删除车型
     */
    void deleteCarModel(Long id);

    /**
     * 根据ID查询车型
     */
    CarModelResponse getCarModelById(Long id);

    List<CarModelResponse> getCarModelsByIds(String ids);

    /**
     * 分页查询车型列表
     */
    Page<CarModelResponse> getCarModelList(int current, int size, String brand, String series,
                                           Integer seatCount, Integer transmission, Integer driveType,
                                           Integer startYear, Integer endYear);

    /**
     * 查询所有车型
     */
    List<CarModelResponse> getAllCarModels();

    /**
     * 根据品牌查询车型
     */
    List<CarModelResponse> getCarModelsByBrand(String brand);

    /**
     * 根据车系查询车型
     */
    List<CarModelResponse> getCarModelsBySeries(String series);

    /**
     * 根据品牌和车系查询车型
     */
    List<CarModelResponse> getCarModelsByBrandAndSeries(String brand, String series);

    /**
     * 根据座位数查询车型
     */
    List<CarModelResponse> getCarModelsBySeatCount(Integer seatCount);

    /**
     * 根据档位类型查询车型
     */
    List<CarModelResponse> getCarModelsByTransmission(Integer transmission);

    /**
     * 根据驱动类型查询车型
     */
    List<CarModelResponse> getCarModelsByDriveType(Integer driveType);

    /**
     * 查询所有品牌
     */
    List<String> getAllBrands();

    /**
     * 根据品牌查询车系
     */
    List<String> getSeriesByBrand(String brand);

    /**
     * 多条件查询车型
     */
    List<CarModelResponse> getCarModelsByConditions(String brand, String series, Integer seatCount,
                                                    Integer transmission, Integer driveType,
                                                    Integer startYear, Integer endYear);

    /**
     * 检查车型是否重复
     */
    boolean checkCarModelExists(String brand, String series, String model, Integer year, Long excludeId);

    /**
     * 批量导入车型
     */
    void batchImportCarModels(List<CarModelCreateRequest> requests);
}
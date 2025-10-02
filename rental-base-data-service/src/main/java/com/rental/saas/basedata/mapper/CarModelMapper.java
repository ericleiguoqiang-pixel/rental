package com.rental.saas.basedata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.saas.basedata.entity.CarModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 车型数据访问层
 * 
 * @author Rental SaaS Team
 */
@Mapper
public interface CarModelMapper extends BaseMapper<CarModel> {

    /**
     * 根据品牌查询车型
     */
    List<CarModel> findByBrand(@Param("brand") String brand);

    /**
     * 根据车系查询车型
     */
    List<CarModel> findBySeries(@Param("series") String series);

    /**
     * 根据品牌和车系查询车型
     */
    List<CarModel> findByBrandAndSeries(@Param("brand") String brand, @Param("series") String series);

    /**
     * 根据座位数查询车型
     */
    List<CarModel> findBySeatCount(@Param("seatCount") Integer seatCount);

    /**
     * 根据档位类型查询车型
     */
    List<CarModel> findByTransmission(@Param("transmission") Integer transmission);

    /**
     * 根据驱动类型查询车型
     */
    List<CarModel> findByDriveType(@Param("driveType") Integer driveType);

    /**
     * 根据年款范围查询车型
     */
    List<CarModel> findByYearRange(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear);

    /**
     * 查询所有品牌
     */
    List<String> findAllBrands();

    /**
     * 根据品牌查询车系
     */
    List<String> findSeriesByBrand(@Param("brand") String brand);

    /**
     * 多条件查询车型
     */
    List<CarModel> findByConditions(@Param("brand") String brand,
                                    @Param("series") String series,
                                    @Param("seatCount") Integer seatCount,
                                    @Param("transmission") Integer transmission,
                                    @Param("driveType") Integer driveType,
                                    @Param("startYear") Integer startYear,
                                    @Param("endYear") Integer endYear);

    /**
     * 检查车型是否重复
     */
    int checkCarModelExists(@Param("brand") String brand,
                            @Param("series") String series,
                            @Param("model") String model,
                            @Param("year") Integer year,
                            @Param("excludeId") Long excludeId);
}
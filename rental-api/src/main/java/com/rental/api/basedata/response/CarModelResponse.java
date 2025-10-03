package com.rental.api.basedata.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 车型响应DTO
 * 
 * @author Rental SaaS Team
 */
@Data
@Schema(description = "车型信息响应")
public class CarModelResponse {

    /**
     * 车型ID
     */
    @Schema(description = "车型ID", example = "1")
    private Long id;

    /**
     * 品牌
     */
    @Schema(description = "品牌", example = "奔驰")
    private String brand;

    /**
     * 车系
     */
    @Schema(description = "车系", example = "C级")
    private String series;

    /**
     * 车型
     */
    @Schema(description = "车型", example = "C 260 L 豪华型")
    private String model;

    /**
     * 年款
     */
    @Schema(description = "年款", example = "2023")
    private Integer year;

    /**
     * 档位：1-自动，2-手动
     */
    @Schema(description = "档位类型", example = "1")
    private Integer transmission;

    /**
     * 档位类型描述
     */
    @Schema(description = "档位类型描述", example = "自动")
    private String transmissionDesc;

    /**
     * 驱动类型：1-燃油，2-纯电，3-混动
     */
    @Schema(description = "驱动类型", example = "1")
    private Integer driveType;

    /**
     * 驱动类型描述
     */
    @Schema(description = "驱动类型描述", example = "燃油")
    private String driveTypeDesc;

    /**
     * 座位数
     */
    @Schema(description = "座位数", example = "5")
    private Integer seatCount;

    /**
     * 车门数
     */
    @Schema(description = "车门数", example = "4")
    private Integer doorCount;

    /**
     * 车型分类(多选,逗号分隔)
     */
    @Schema(description = "车型分类", example = "豪华型,商务型")
    private String category;

    /**
     * 车型图片URLs(JSON格式)
     */
    @Schema(description = "车型图片URLs", example = "[\"https://oss.example.com/car1.jpg\"]")
    private String imageUrls;

    /**
     * 车型描述
     */
    @Schema(description = "车型描述", example = "豪华品牌中型轿车，配置丰富")
    private String description;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-01-01 12:00:00")
    private LocalDateTime updatedTime;

    /**
     * 车辆数量统计
     */
    @Schema(description = "使用此车型的车辆数量", example = "5")
    private Integer vehicleCount;
}
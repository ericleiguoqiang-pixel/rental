package com.rental.saas.basedata.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rental.saas.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.*;

/**
 * 车型实体
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("car_model")
@Schema(description = "车型")
public class CarModel extends BaseEntity {

    /**
     * 品牌
     */
    @TableField("brand")
    @NotBlank(message = "品牌不能为空")
    @Size(max = 50, message = "品牌长度不能超过50个字符")
    @Schema(description = "品牌", example = "奔驰")
    private String brand;

    /**
     * 车系
     */
    @TableField("series")
    @NotBlank(message = "车系不能为空")
    @Size(max = 50, message = "车系长度不能超过50个字符")
    @Schema(description = "车系", example = "C级")
    private String series;

    /**
     * 车型
     */
    @TableField("model")
    @NotBlank(message = "车型不能为空")
    @Size(max = 100, message = "车型长度不能超过100个字符")
    @Schema(description = "车型", example = "C 260 L 豪华型")
    private String model;

    /**
     * 年款
     */
    @TableField("year")
    @NotNull(message = "年款不能为空")
    @Min(value = 2000, message = "年款不能早于2000年")
    @Max(value = 2030, message = "年款不能晚于2030年")
    @Schema(description = "年款", example = "2023")
    private Integer year;

    /**
     * 档位：1-自动，2-手动
     */
    @TableField("transmission")
    @NotNull(message = "档位类型不能为空")
    @Min(value = 1, message = "档位类型值错误")
    @Max(value = 2, message = "档位类型值错误")
    @Schema(description = "档位类型", example = "1")
    private Integer transmission;

    /**
     * 驱动类型：1-燃油，2-纯电，3-混动
     */
    @TableField("drive_type")
    @NotNull(message = "驱动类型不能为空")
    @Min(value = 1, message = "驱动类型值错误")
    @Max(value = 3, message = "驱动类型值错误")
    @Schema(description = "驱动类型", example = "1")
    private Integer driveType;

    /**
     * 座位数
     */
    @TableField("seat_count")
    @NotNull(message = "座位数不能为空")
    @Min(value = 2, message = "座位数不能少于2座")
    @Max(value = 9, message = "座位数不能超过9座")
    @Schema(description = "座位数", example = "5")
    private Integer seatCount;

    /**
     * 车门数
     */
    @TableField("door_count")
    @NotNull(message = "车门数不能为空")
    @Min(value = 2, message = "车门数不能少于2门")
    @Max(value = 5, message = "车门数不能超过5门")
    @Schema(description = "车门数", example = "4")
    private Integer doorCount;

    /**
     * 车型分类(多选,逗号分隔)
     */
    @TableField("category")
    @Schema(description = "车型分类", example = "豪华型,商务型")
    private String category;

    /**
     * 车型图片URLs(JSON格式)
     */
    @TableField("image_urls")
    @Schema(description = "车型图片URLs", example = "[\"https://oss.example.com/car1.jpg\"]")
    private String imageUrls;

    /**
     * 车型描述
     */
    @TableField("description")
    @Size(max = 1000, message = "车型描述长度不能超过1000个字符")
    @Schema(description = "车型描述", example = "豪华品牌中型轿车，配置丰富")
    private String description;
}
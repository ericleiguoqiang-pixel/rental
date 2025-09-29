package com.rental.saas.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 多租户基础实体类
 * 继承BaseEntity，增加租户ID字段
 * 
 * @author Rental SaaS Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "多租户基础实体")
public class TenantBaseEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @TableField(value = "tenant_id")
    @Schema(description = "租户ID", example = "1")
    private Long tenantId;
}
package com.rental.saas.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("rental_user")
@Schema(description = "用户表")
public class User {
    
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Long id;
    
    @TableField("phone")
    @Schema(description = "手机号")
    private String phone;
    
    @TableField("nickname")
    @Schema(description = "昵称")
    private String nickname;
    
    @TableField("avatar")
    @Schema(description = "头像URL")
    private String avatar;
    
    @TableField("status")
    @Schema(description = "用户状态 0-正常 1-禁用")
    private Integer status;
    
    @TableField("created_at")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
package com.rental.saas.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户分页查询请求DTO
 */
@Data
@Schema(description = "用户分页查询请求")
public class UserPageRequest {
    
    @Schema(description = "当前页码", example = "1")
    private Integer current = 1;
    
    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "用户状态 0-正常 1-禁用")
    private Integer status;
}
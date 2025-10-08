package com.rental.saas.user.dto.response;

import com.rental.saas.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户分页查询响应DTO
 */
@Data
@Schema(description = "用户分页查询响应")
public class UserPageResponse {
    
    @Schema(description = "当前页码")
    private Integer current;
    
    @Schema(description = "每页条数")
    private Integer size;
    
    @Schema(description = "总记录数")
    private Long total;
    
    @Schema(description = "用户列表")
    private List<User> records;
    
    public UserPageResponse() {}
    
    public UserPageResponse(Integer current, Integer size, Long total, List<User> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.records = records;
    }
}
package com.rental.saas.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.user.dto.request.UserPageRequest;
import com.rental.saas.user.dto.response.UserPageResponse;
import com.rental.saas.user.entity.User;
import com.rental.saas.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 运营用户管理控制器
 * 提供运营人员对用户信息的管理功能
 *
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/operation/users")
@Tag(name = "运营用户管理接口", description = "运营MIS系统用户管理相关接口")
@RequiredArgsConstructor
public class OperationUserController {

    private final UserService userService;

    /**
     * 分页获取用户列表
     */
    @GetMapping
    @Operation(summary = "分页获取用户列表", description = "分页获取用户列表，支持手机号、昵称、状态筛选")
    public ApiResponse<UserPageResponse> getUserList(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "手机号") @RequestParam(required = false) String phone,
            @Parameter(description = "昵称") @RequestParam(required = false) String nickname,
            @Parameter(description = "用户状态 0-正常 1-禁用") @RequestParam(required = false) Integer status) {
        
        log.info("分页获取用户列表: current={}, size={}, phone={}, nickname={}, status={}", 
                current, size, phone, nickname, status);
        
        try {
            IPage<User> page = userService.getUserList(current, size, phone, nickname, status);
            
            UserPageResponse response = new UserPageResponse(
                current, 
                size, 
                page.getTotal(), 
                page.getRecords()
            );
            
            log.info("分页获取用户列表成功，共{}条记录", page.getTotal());
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("分页获取用户列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("分页获取用户列表失败");
        }
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详情")
    public ApiResponse<User> getUserDetail(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        
        log.info("获取用户详情: id={}", id);
        
        try {
            User user = userService.getById(id);
            if (user == null) {
                log.warn("用户不存在: id={}", id);
                return ApiResponse.error("用户不存在");
            }
            log.info("获取用户详情成功: id={}", id);
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("获取用户详情失败: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("获取用户详情失败");
        }
    }

    /**
     * 禁用/启用用户
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态", description = "禁用或启用用户")
    public ApiResponse<Boolean> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "用户状态 0-正常 1-禁用") @RequestParam Integer status) {
        
        log.info("更新用户状态: id={}, status={}", id, status);
        
        try {
            User user = userService.getById(id);
            if (user == null) {
                log.warn("用户不存在: id={}", id);
                return ApiResponse.error("用户不存在");
            }
            
            user.setStatus(status);
            boolean result = userService.updateById(user);
            
            if (result) {
                log.info("更新用户状态成功: id={}", id);
                return ApiResponse.success(true);
            } else {
                log.warn("更新用户状态失败: id={}", id);
                return ApiResponse.error("更新用户状态失败");
            }
        } catch (Exception e) {
            log.error("更新用户状态异常: id={}, error={}", id, e.getMessage(), e);
            return ApiResponse.error("更新用户状态异常: " + e.getMessage());
        }
    }
}
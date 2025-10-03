package com.rental.saas.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.rental.saas.common.response.ApiResponse;
import com.rental.saas.common.response.PageResponse;
import com.rental.saas.user.dto.request.EmployeeRequest;
import com.rental.saas.user.dto.request.EmployeeUpdateRequest;
import com.rental.saas.user.dto.response.EmployeeResponse;
import com.rental.saas.user.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 员工管理控制器
 * 
 * @author Rental SaaS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "员工管理", description = "员工管理接口")
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * 创建员工
     */
    @PostMapping
    @Operation(summary = "创建员工", description = "创建新员工")
    public ApiResponse<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        log.info("创建员工请求: {}, tenantID: {}", request, tenantId);
        EmployeeResponse response = employeeService.createEmployee(request, tenantId);
        return ApiResponse.success(response);
    }

    /**
     * 更新员工
     */
    @PutMapping
    @Operation(summary = "更新员工", description = "更新员工信息")
    public ApiResponse<EmployeeResponse> updateEmployee(
            @Valid @RequestBody EmployeeUpdateRequest request) {
        log.info("更新员工请求: {}", request);
        EmployeeResponse response = employeeService.updateEmployee(request);
        return ApiResponse.success(response);
    }

    /**
     * 删除员工
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除员工", description = "删除员工")
    public ApiResponse<Boolean> deleteEmployee(
            @Parameter(description = "员工ID") @PathVariable("id") Long id) {
        log.info("删除员工请求: id={}", id);
        boolean result = employeeService.deleteEmployee(id);
        return ApiResponse.success(result);
    }

    /**
     * 根据ID获取员工详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取员工详情", description = "根据ID获取员工详情")
    public ApiResponse<EmployeeResponse> getEmployeeById(
            @Parameter(description = "员工ID") @PathVariable("id") Long id) {
        log.info("获取员工详情请求: id={}", id);
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ApiResponse.success(response);
    }

    /**
     * 分页查询员工列表
     */
    @GetMapping
    @Operation(summary = "分页查询员工列表", description = "分页查询员工列表")
    public ApiResponse<PageResponse<EmployeeResponse>> getEmployeePage(
            @Parameter(description = "页码") @RequestParam(value = "current", defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Parameter(description = "员工姓名") @RequestParam(value = "employeeName", required = false) String employeeName,
            @Parameter(description = "手机号") @RequestParam(value = "phone", required = false) String phone,
            @Parameter(description = "状态") @RequestParam(value = "status", required = false) Integer status,
            @RequestHeader("X-Tenant-Id") Long tenantId) {
        log.info("分页查询员工列表请求: current={}, size={}, employeeName={}, phone={}, status={}", 
                current, size, employeeName, phone, status);
        
        IPage<EmployeeResponse> page = employeeService.getEmployeePage(current, size, employeeName, phone, status, tenantId);
        return ApiResponse.success(PageResponse.of(page));
    }

    /**
     * 重置员工密码
     */
    @PutMapping("/{id}/reset-password")
    @Operation(summary = "重置员工密码", description = "重置员工密码为默认密码")
    public ApiResponse<Boolean> resetPassword(
            @Parameter(description = "员工ID") @PathVariable("id") Long id) {
        log.info("重置员工密码请求: id={}", id);
        boolean result = employeeService.resetPassword(id);
        return ApiResponse.success(result);
    }
}
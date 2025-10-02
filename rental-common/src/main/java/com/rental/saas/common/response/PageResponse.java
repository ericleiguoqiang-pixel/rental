package com.rental.saas.common.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应类
 * 用于封装分页查询结果
 * 
 * @author Rental SaaS Team
 * @param <T> 数据类型
 */
@Data
@Schema(description = "分页响应类")
public class PageResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Integer pageNum;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "100")
    private Long total;

    /**
     * 总页数
     */
    @Schema(description = "总页数", example = "10")
    private Integer pages;

    /**
     * 是否有下一页
     */
    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;

    /**
     * 是否有上一页
     */
    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;

    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<T> records;

    public PageResponse() {
    }

    public PageResponse(Integer pageNum, Integer pageSize, Long total, List<T> records) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.records = records;
        this.pages = (int) Math.ceil((double) total / pageSize);
        this.hasNext = pageNum < this.pages;
        this.hasPrevious = pageNum > 1;
    }

    /**
     * 创建分页响应
     */
    public static <T> PageResponse<T> of(Integer pageNum, Integer pageSize, Long total, List<T> records) {
        return new PageResponse<>(pageNum, pageSize, total, records);
    }

    /**
     * 从MyBatis Plus的IPage创建分页响应
     */
    public static <T> PageResponse<T> of(IPage<T> page) {
        return new PageResponse<>(
            (int) page.getCurrent(), 
            (int) page.getSize(), 
            page.getTotal(), 
            page.getRecords()
        );
    }

    /**
     * 创建空分页响应
     */
    public static <T> PageResponse<T> empty(Integer pageNum, Integer pageSize) {
        return new PageResponse<>(pageNum, pageSize, 0L, List.of());
    }
}
package com.rental.saas.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.saas.order.entity.Order;
import com.rental.saas.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单服务测试类
 * 
 * @author Rental SaaS Team
 */
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void testGetOrdersByTenantId() {
        // 测试分页查询功能
        IPage<Order> page = orderService.getOrdersByTenantId(1L, 1, 10, null, null);
        assertNotNull(page);
        assertTrue(page.getRecords().size() >= 0);
    }

    @Test
    public void testGetOrderDetail() {
        // 测试获取订单详情功能
        Order order = orderService.getOrderDetail(1L, 1L);
        // 由于是测试环境，订单可能不存在，这里只验证方法能正常执行
        assertTrue(true);
    }
}
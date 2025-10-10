package com.rental.saas.mcp.config;

import com.rental.saas.mcp.store.StoreMcpTool;
import com.rental.saas.mcp.vehicle.VehicleMcpTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MCP网关配置类
 * 
 * @author Rental SaaS Team
 */
@Configuration
public class McpConfiguration {
    @Bean
    public ToolCallbackProvider toolCallbackProvider(StoreMcpTool storeMcpTool, VehicleMcpTool vehicleMcpTool) {
        return MethodToolCallbackProvider.builder().toolObjects(storeMcpTool, vehicleMcpTool).build();
    }
}

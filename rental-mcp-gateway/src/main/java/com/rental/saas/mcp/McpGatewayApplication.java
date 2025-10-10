package com.rental.saas.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

/**
 * Rental MCP网关应用启动类
 * 
 * @author Rental SaaS Team
 */
@SpringBootApplication(scanBasePackages = {"com.rental.saas.mcp", "com.rental.api", "com.rental.saas.common"}, exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.rental.api")
public class McpGatewayApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(McpGatewayApplication.class, args);
        System.out.println("MCP Gateway started successfully.");
    }



//    public ToolCallbackProvider toolCallbackProvider(McpService mcpService) {
//        return MethodToolCallbackProvider.builder().toolObjects(mcpService).build();
//    }
}
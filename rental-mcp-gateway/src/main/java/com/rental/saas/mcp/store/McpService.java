package com.rental.saas.mcp.store;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class McpService {

    @Tool(name = "天气查询", description = "通过这个接口可以获取到中国城市的天气")
    public String mcp(@ToolParam(description = "请输入城市中文名称") String city) {
        return city + "的天气是晴朗";
    }
}

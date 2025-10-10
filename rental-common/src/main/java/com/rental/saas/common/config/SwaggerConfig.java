package com.rental.saas.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @author rongrong
 * @version 1.0
 * @description Swagger3配置
 * @date 2021/01/12 21:00
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rental SaaS")
                        .version("1.0")
                        .description("Rental SaaS DESC")
                        .contact(new Contact()
                                .email("lgq@vip.qq.com")
                                .name("leiguoqiang"))
                        .license(new License()
                                .name("MIT")
                                .url("https://github.com/ericleiguoqiang-pixel/rental-saas")));
    }

//    @Bean
//    public Docket docket() {
//        // 创建一个 swagger 的 bean 实例
//        return new Docket(DocumentationType.OAS_30).apiInfo(apiInfo());
//    }
//
//    // 基本信息设置
//    private ApiInfo apiInfo() {
//        Contact contact = new Contact(
//                "leiguoqiang", // 作者姓名
//                "https://github.com/ericleiguoqiang-pixel", // 作者主页
//                "lgq@vip.qq.com"); // 作者邮箱
//        return new ApiInfoBuilder()
//                .title("Eric-接口文档") // 标题
//                .description("哈哈哈哈哈") // 描述
//                .termsOfServiceUrl("https://www.baidu.com") // 跳转连接
//                .version("1.0") // 版本
//                .license("Rental SaaS & Mis")
//                .licenseUrl("https://www.baidu.com")
//                .contact(contact)
//                .build();
//    }



}
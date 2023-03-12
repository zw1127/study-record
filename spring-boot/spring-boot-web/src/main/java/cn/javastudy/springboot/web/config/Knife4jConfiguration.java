package cn.javastudy.springboot.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfiguration {

    @Bean(value = "defaultDocket")
    public Docket defaultDocket() {
        // 联系人信息
        Contact contact = new Contact("Java Demo", "https://127.0.0.1:8443", "xx@test.cn");

        // 创建 Docket
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(new ApiInfoBuilder()
                .title("Knife4j 测试")
                .description("Knife4j Test")
                .termsOfServiceUrl("https://127.0.0.1:8443")
                .contact(contact)
                .version("1.0")
                .build())
            .groupName("1.x")
            .select()
            .apis(RequestHandlerSelectors.basePackage("cn.javastudy.springboot.web.controller"))
            .paths(PathSelectors.any())
            .build();
        return docket;
    }
}

package com.grepp.spring.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openApiSpec() {
        return new OpenAPI()
                   .info(new Info()
                             .title("API 문서")
                             .description("API 명세입니다.")
                             .version("v1.0.0"))
                   .addServersItem(new Server().url("https://titae.cedartodo.uk"))
                   .components(
                       new Components()
                           .addSecuritySchemes("bearerAuth"
                               , new SecurityScheme()
                                     .name("bearerAuth")
                                     .type(
                                         SecurityScheme.Type.HTTP)
                                     .scheme(
                                         "bearer")
                                     .bearerFormat(
                                         "JWT")
                                     .description(
                                         "JWT 토큰을 입력하세요. Bearer 는 생략하세요")
                           ))
                   .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        
    }
}

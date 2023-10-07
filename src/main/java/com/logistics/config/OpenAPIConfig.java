package com.logistics.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class OpenAPIConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(getInfo());
    }

    private Info getInfo() {
        return new Info()
                .title("Logistics API")
                .description("Logistics to create and place order")
                .version("v0.0.1")
                .contact(getContacts());
    }


    private Contact getContacts() {
        return new Contact()
                .name("Mugeesh Husain")
                .email("mugeesh.husain@gmail.com");
    }

}
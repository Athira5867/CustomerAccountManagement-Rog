package com.example.rogersapi.Config;

import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;


@Configuration
public class OpenApiConfig {
	
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Rogers Customer Account Management API")
						.version("1.0.0")
						.description("API for managing customer accounts, locations , and statuses.")
				);
				
	}

}

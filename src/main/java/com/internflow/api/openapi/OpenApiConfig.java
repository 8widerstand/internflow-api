package com.internflow.api.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "InternFlow API",
                version = "v1",
                description = "REST API for managing internships, students, mentors and tasks"
        )
)
public class OpenApiConfig {
}
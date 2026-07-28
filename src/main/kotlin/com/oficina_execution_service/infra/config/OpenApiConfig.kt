package com.oficina_execution_service.infra.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun executionOpenApi(): OpenAPI = OpenAPI().info(
        Info()
            .title("Oficina Execution Service API")
            .description("Fila e ciclo de execução das ordens de serviço")
            .version("v1")
    )
}

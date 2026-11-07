package com.ejemplo.laboratorio11.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API de Productos y Ventas UPN",
        version = "1.2",
        description = "Laboratorio 12: JPQL, named parameters, transacciones y SOA"
    ),
    servers = { @Server(url = "/", description = "Servidor actual de Codespaces") }
)
public class OpenApiConfig {
}

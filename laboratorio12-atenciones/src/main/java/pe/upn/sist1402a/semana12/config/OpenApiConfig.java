package pe.upn.sist1402a.semana12.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "Laboratorio 12 - Atenciones", version = "1.0",
                 description = "Transacciones, rollback y consultas JPQL"),
    servers = @Server(url = "/", description = "Servidor actual (Codespaces o local)")
)
public class OpenApiConfig {
}

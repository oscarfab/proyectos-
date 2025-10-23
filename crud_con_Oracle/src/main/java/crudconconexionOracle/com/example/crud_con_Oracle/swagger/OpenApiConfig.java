package crudconconexionOracle.com.example.crud_con_Oracle.swagger;


import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI crudOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión de Empleados")
                        .description("""
                            ##  Descripción
                            API REST completa para la gestión de empleados con operaciones CRUD.
                            
                            ##  Funcionalidades
                            -  Crear nuevos empleados
                            -  Consultar lista completa de empleados
                            - Buscar empleado por ID
                            -  Actualizar información de empleados
                            -  Eliminar empleados
                            -CREADO POR OSCAR FABAIN OCANA VILLAGRAN
                            ##  Validaciones
                            - Nombre: obligatorio, 2-100 caracteres
                            - Edad: 18-100 años
                            - Email: formato válido
                            - Sueldo: no negativo
                            """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("desarrollo@empresa.com")
                                .url("https://empresa.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8088")
                                .description("Servidor de Desarrollo Local"),
                        new Server()
                                .url("https://api-prod.empresa.com")
                                .description("Servidor de Producción (ejemplo)")))
                .externalDocs(new ExternalDocumentation()
                        .description(" Documentación completa del proyecto")
                        .url("https://github.com/oscarfab/proyectos-"));
    }
}
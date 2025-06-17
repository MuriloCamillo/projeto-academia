package br.com.gymtime.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração para a documentação da API usando SpringDoc e OpenAPI 3.
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GymTime API")
                        .version("v1.0")
                        .description("API RESTful para o sistema GymTime, permitindo o gerenciamento completo de alunos, treinos e exercícios.")
                        .contact(new Contact()
                                .name("Letícia Leme & Murilo Camillo")
                                .url("https://github.com/leticiaaleme/projeto-academia")
                                .email("suporte.gymtime@exemplo.com"))
                        .license(new License()
                                .name("GymTime © 2025 - Todos os direitos reservados.")
                                .url("https://github.com/leticiaaleme/projeto-academia")));
    }
}
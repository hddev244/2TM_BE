package store.chikendev._2tm.configuration;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OpenAPI openAPI(
            @Value("${open.api.title}") String title,
            @Value("${open.api.version}") String version,
            @Value("${open.api.description}") String description,
            @Value("${open.api.serverUrl}") String serverUrl,
            @Value("${open.api.serverName}") String serverName) {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description)
                        .license(new License().name("2tm.store")
                                .url("http://2tm.store")))
                .servers(List.of(new Server()
                        .url(serverUrl)
                        .description(serverName)))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearer-jwt", new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .name("Authorization")))
                .security(List.of(
                        new io.swagger.v3.oas.models.security.SecurityRequirement()
                                .addList("bearer-jwt")));
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("api-service")
                .packagesToScan("store.chikendev._2tm.controller")
                .build();
    }
}

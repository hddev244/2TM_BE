package store.chikendev._2tm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    private CustomJWTDecoder customJWTDecoder;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:3002",//giao hàng
                        "http://localhost:3003",// Của hàng
                        "http://localhost:3004", // admin
                        "http://localhost:3001",
                        "https://192.168.1.197:3001",
                        "http://192.168.1.197:3001",
                        "https://admin.2tm.store",
                        "https://delivery-person.2tm.store",
                        "https://delivery.2tm.store",
                        "http://delivery.2tm.store",
                        "https://store-manager.2tm.store",
                        "http://test-onserver.2tm.store",
                        "https://api.2tm.store",
                        "http://api.2tm.store",
                        "https://2tm.store",
                        "http://127.0.0.1:5501",
                        "http://2tm.store")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                .allowedHeaders("*", "Authorization", "accessToken", "refreshToken", "test", "Content-Type", "Accept","Aceess-Control-Allow-Credentials")
                .exposedHeaders("Authorization","accessToken","refreshToken","test")
                .maxAge(3600)
                .allowCredentials(true);
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(c -> c.disable());
        
        httpSecurity.authorizeHttpRequests(request -> {
            request.anyRequest().permitAll();
        });

        httpSecurity.oauth2ResourceServer(oauth2 -> {
            oauth2.jwt(jwtConfig -> jwtConfig.decoder(customJWTDecoder)
                    .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint);
        });

 

        return httpSecurity.build();
    }
}

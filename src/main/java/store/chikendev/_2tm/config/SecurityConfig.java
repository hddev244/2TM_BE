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
                        "https://2tm.store",
                        "http://2tm.store")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                .allowedHeaders("*")
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
        httpSecurity.cors(c -> c.disable());
        
        httpSecurity.authorizeHttpRequests(request -> {
            // request.requestMatchers("/register", "/login", "/profile/**", "district/**",
            // "/upload/cloud", "/files/**",
            // "province/**", "ward/**", "/account/logout", "/account/refresh",
            // "exportToExcel")
            // .permitAll()
            // .requestMatchers(HttpMethod.GET, "file/**").permitAll()
            // .requestMatchers(HttpMethod.GET, "/articleCategory/**",
            // "courseCategories/**", "QuestionType/**",
            // "role/**")
            // .hasRole("ADMIN")
            // .anyRequest().authenticated();
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

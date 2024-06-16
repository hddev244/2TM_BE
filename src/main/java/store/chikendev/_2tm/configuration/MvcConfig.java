package store.chikendev._2tm.configuration;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("images/**", "/files/**","/files_dev/**")
                .addResourceLocations("file:images\\")
                .addResourceLocations("file:images_dev\\")
                .addResourceLocations("file:files\\")
                .addResourceLocations("file:files_dev\\")
                .addResourceLocations("classpath:/static/pdf/")
                .resourceChain(true).addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        String decodedResourcePath = URLDecoder.decode(resourcePath, StandardCharsets.UTF_8.name());
                        return super.getResource(decodedResourcePath, location);
                    }
                });
    }
}

package store.chikendev._2tm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "twilio")
@Data
public class TwilioConfig {
    private String ACCOUNT_SID;
    private String AUTH_TOKEN;
}

package festival.dev.global.security.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "custom.cookie")
public class CookieProperties {
    private String domain;

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
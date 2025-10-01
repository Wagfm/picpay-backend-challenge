package picpay.challenge.api.infra.spring.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.services")
@Getter
@Setter
@NoArgsConstructor
public class GatewayProperties {
    private Authorization authorization;
    private Notification notification;

    @Getter
    @Setter
    public static class Authorization {
        private String url;
    }

    @Getter
    @Setter
    public static class Notification {
        private String url;
    }
}

package picpay.challenge.api.infra.spring.gateway;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import picpay.challenge.api.application.gateway.INotificationGateway;
import picpay.challenge.api.application.gateway.dto.NotificationInputDto;
import picpay.challenge.api.application.gateway.dto.NotificationOutputDto;
import picpay.challenge.api.infra.spring.config.GatewayProperties;

@Component
@AllArgsConstructor
public class NotificationGateway implements INotificationGateway<NotificationInputDto, NotificationOutputDto>{
    private final RestTemplate restTemplate;
    private final GatewayProperties gatewayProperties;

    @Override
    public NotificationOutputDto notify(NotificationInputDto input) {
        String url = this.gatewayProperties.getNotification().getUrl();
        return restTemplate.postForObject(url, input, NotificationOutputDto.class);
    }
}

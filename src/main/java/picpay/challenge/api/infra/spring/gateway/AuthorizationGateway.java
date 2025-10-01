package picpay.challenge.api.infra.spring.gateway;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import picpay.challenge.api.application.gateway.IAuthorizationGateway;
import picpay.challenge.api.application.gateway.dto.AuthorizationInputDto;
import picpay.challenge.api.application.gateway.dto.AuthorizationOutputDto;
import picpay.challenge.api.infra.spring.config.GatewayProperties;

@Component
@AllArgsConstructor
public class AuthorizationGateway implements IAuthorizationGateway<AuthorizationInputDto, AuthorizationOutputDto> {
    private final RestTemplate restTemplate;
    private final GatewayProperties gatewayProperties;

    @Override
    public AuthorizationOutputDto getAuthorization(AuthorizationInputDto input) {
        try {
            String url = this.gatewayProperties.getAuthorization().getUrl();
            ResponseEntity<AuthorizationOutputDto> response = restTemplate.getForEntity(url, AuthorizationOutputDto.class);
            return response.getBody();
        } catch (HttpClientErrorException.Forbidden e) {
            return e.getResponseBodyAs(AuthorizationOutputDto.class);
        }
    }
}

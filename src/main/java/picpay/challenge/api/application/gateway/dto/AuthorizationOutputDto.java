package picpay.challenge.api.application.gateway.dto;

import lombok.Builder;

@Builder
public record AuthorizationOutputDto(String status, Data data) {
}

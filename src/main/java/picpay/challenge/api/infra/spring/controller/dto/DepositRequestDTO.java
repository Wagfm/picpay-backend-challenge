package picpay.challenge.api.infra.spring.controller.dto;

import java.math.BigDecimal;

public record DepositRequestDTO(BigDecimal amount) {
}

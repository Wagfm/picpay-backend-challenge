package picpay.challenge.api.application.usecase.dto;

import java.math.BigDecimal;

public record DepositDTO(Long id, BigDecimal amount) {
}

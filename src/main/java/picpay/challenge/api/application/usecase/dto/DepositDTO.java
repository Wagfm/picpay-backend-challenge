package picpay.challenge.api.application.usecase.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositDTO(UUID id, BigDecimal amount) {
}

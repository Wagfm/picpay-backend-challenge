package picpay.challenge.api.application.usecase.dto;

import java.math.BigDecimal;

public record TransferDTO(Long payer, Long payee, BigDecimal amount) {
}



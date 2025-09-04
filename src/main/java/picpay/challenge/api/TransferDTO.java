package picpay.challenge.api;

import java.math.BigDecimal;

public record TransferDTO(Long payer, Long payee, BigDecimal amount) {
}



package picpay.challenge.api.domain.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import picpay.challenge.api.domain.enums.TransactionStatus;
import picpay.challenge.api.domain.enums.TransactionType;
import picpay.challenge.api.domain.exception.ValidationException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
public class Transaction {
    @NotNull(message = "ID cannot be null")
    private final UUID id;

    @NotNull(message = "Operation ID cannot be null")
    private final UUID operationId;

    private final UUID sourceWalletId;

    private final UUID destinationWalletId;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private final BigDecimal amount;

    @NotNull(message = "Status cannot be null")
    private final TransactionStatus status;

    @NotNull(message = "Type cannot be null")
    private final TransactionType type;

    @NotNull(message = "Timestamp cannot be null")
    private final ZonedDateTime timestamp;

    private Transaction(UUID id, UUID operationId, UUID sourceWalletId, UUID destinationWalletId, BigDecimal amount, TransactionStatus status, TransactionType type, ZonedDateTime timestamp) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.operationId = operationId;
        this.sourceWalletId = sourceWalletId;
        this.destinationWalletId = destinationWalletId;
        this.amount = amount;
        this.status = status;
        this.type = type;
        this.timestamp = timestamp;
        EntityValidator.validate(this);
        this.validate();
    }

    private void validate() {
        if (this.type == null) throw new ValidationException("Type cannot be null");
        type.validate(this.sourceWalletId, this.destinationWalletId);
    }
}
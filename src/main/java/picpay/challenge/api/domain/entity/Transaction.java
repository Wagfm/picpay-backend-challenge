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

    private final Wallet sourceWallet;

    private final Wallet destinationWallet;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private final BigDecimal amount;

    @NotNull(message = "Status cannot be null")
    private final TransactionStatus status;

    @NotNull(message = "Type cannot be null")
    private final TransactionType transactionType;

    @NotNull(message = "Timestamp cannot be null")
    private final ZonedDateTime timestamp;

    private Transaction(UUID id, UUID operationId, Wallet sourceWallet, Wallet destinationWallet, BigDecimal amount, TransactionStatus status, TransactionType transactionType, ZonedDateTime timestamp) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.operationId = operationId == null ? UUID.randomUUID() : operationId;
        this.sourceWallet = sourceWallet;
        this.destinationWallet = destinationWallet;
        this.amount = amount;
        this.status = status;
        this.transactionType = transactionType;
        this.timestamp = timestamp;
        EntityValidator.validate(this);
        this.validate();
    }

    private void validate() {
        if (this.transactionType == null) throw new ValidationException("Type cannot be null");
        transactionType.validate(this.sourceWallet, this.destinationWallet);
    }
}
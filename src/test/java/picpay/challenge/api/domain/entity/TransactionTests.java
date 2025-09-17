package picpay.challenge.api.domain.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import picpay.challenge.api.domain.enums.TransactionStatus;
import picpay.challenge.api.domain.enums.TransactionType;
import picpay.challenge.api.domain.exception.ValidationException;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class TransactionTests {
    @Test
    public void shouldCreateTransactionWithValidData() {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .sourceWalletId(UUID.randomUUID())
                .destinationWalletId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(100.00))
                .status(TransactionStatus.COMPLETED)
                .transactionType(TransactionType.TRANSFER)
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")));
        Assertions.assertDoesNotThrow(builder::build);
    }

    @Test
    public void shouldThrowExceptionWithInvalidWalletIdsDeposit() {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(100.00))
                .status(TransactionStatus.COMPLETED)
                .transactionType(TransactionType.DEPOSIT)
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")));
        Assertions.assertThrows(ValidationException.class, builder::build);
        builder.destinationWalletId(UUID.randomUUID());
        Assertions.assertDoesNotThrow(builder::build);
        builder.sourceWalletId(UUID.randomUUID());
        Assertions.assertThrows(ValidationException.class, builder::build);
        builder.destinationWalletId(null);
        Assertions.assertThrows(ValidationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWithInvalidWalletIdsWithdrawal() {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(100.00))
                .status(TransactionStatus.COMPLETED)
                .transactionType(TransactionType.WITHDRAW)
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")));
        Assertions.assertThrows(ValidationException.class, builder::build);
        builder.sourceWalletId(UUID.randomUUID());
        Assertions.assertDoesNotThrow(builder::build);
        builder.destinationWalletId(UUID.randomUUID());
        Assertions.assertThrows(ValidationException.class, builder::build);
        builder.sourceWalletId(null);
        Assertions.assertThrows(ValidationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWithInvalidWalletIdsTransfer() {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(100.00))
                .status(TransactionStatus.COMPLETED)
                .transactionType(TransactionType.TRANSFER)
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")));
        Assertions.assertThrows(ValidationException.class, builder::build);
        builder.destinationWalletId(UUID.randomUUID());
        Assertions.assertThrows(ValidationException.class, builder::build);
        builder.sourceWalletId(UUID.randomUUID());
        Assertions.assertDoesNotThrow(builder::build);
        builder.destinationWalletId(null);
        Assertions.assertThrows(ValidationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWithInvalidWalletIds() {
        UUID duplicateId = UUID.randomUUID();
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .sourceWalletId(duplicateId)
                .destinationWalletId(duplicateId)
                .amount(BigDecimal.valueOf(100.00))
                .status(TransactionStatus.COMPLETED)
                .transactionType(TransactionType.TRANSFER)
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")));
        Assertions.assertThrows(ValidationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWithInvalidAmount() {
        List<BigDecimal> invalidValues = List.of(BigDecimal.valueOf(-100.00), BigDecimal.valueOf(0.00));
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .sourceWalletId(UUID.randomUUID())
                .destinationWalletId(UUID.randomUUID())
                .status(TransactionStatus.COMPLETED)
                .transactionType(TransactionType.TRANSFER)
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")));
        invalidValues.forEach(value -> Assertions.assertThrows(ValidationException.class, () -> builder.amount(value).build()));
    }

    @Test
    public void shouldThrowExceptionWithInvalidStatus() {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .sourceWalletId(UUID.randomUUID())
                .destinationWalletId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(100.00))
                .transactionType(TransactionType.TRANSFER)
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")));
        Assertions.assertThrows(ValidationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWithInvalidType() {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .sourceWalletId(UUID.randomUUID())
                .destinationWalletId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(100.00))
                .status(TransactionStatus.COMPLETED)
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")));
        Assertions.assertThrows(ValidationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWithInvalidTimestamp() {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .sourceWalletId(UUID.randomUUID())
                .destinationWalletId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(100.00))
                .status(TransactionStatus.COMPLETED)
                .transactionType(TransactionType.TRANSFER);
        Assertions.assertThrows(ValidationException.class, builder::build);
    }
}

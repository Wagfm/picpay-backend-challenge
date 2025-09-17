package picpay.challenge.api.domain.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picpay.challenge.api.domain.enums.TransactionStatus;
import picpay.challenge.api.domain.enums.TransactionType;
import picpay.challenge.api.domain.enums.WalletType;
import picpay.challenge.api.domain.exception.ValidationException;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class TransactionTests {
    private static Wallet wallet1;
    private static Wallet wallet2;

    @BeforeAll
    public static void setUp() {
        wallet1 = Wallet.builder()
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234")
                .walletType(WalletType.CUSTOMER)
                .build();
        wallet2 = Wallet.builder()
                .fullName("Amanda Maciel")
                .cpfCnpj("09876543211")
                .email("amanda.maciel@email.com")
                .password("4321")
                .walletType(WalletType.CUSTOMER)
                .build();
    }

    @Test
    public void shouldCreateTransactionWithValidData() {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .sourceWallet(wallet1)
                .destinationWallet(wallet2)
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
        builder.destinationWallet(wallet1);
        Assertions.assertDoesNotThrow(builder::build);
        builder.sourceWallet(wallet2);
        Assertions.assertThrows(ValidationException.class, builder::build);
        builder.destinationWallet(null);
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
        builder.sourceWallet(wallet1);
        Assertions.assertDoesNotThrow(builder::build);
        builder.destinationWallet(wallet2);
        Assertions.assertThrows(ValidationException.class, builder::build);
        builder.sourceWallet(null);
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
        builder.destinationWallet(wallet1);
        Assertions.assertThrows(ValidationException.class, builder::build);
        builder.sourceWallet(wallet2);
        Assertions.assertDoesNotThrow(builder::build);
        builder.destinationWallet(null);
        Assertions.assertThrows(ValidationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWithInvalidWalletIds() {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .sourceWallet(wallet1)
                .destinationWallet(wallet1)
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
                .sourceWallet(wallet1)
                .destinationWallet(wallet2)
                .status(TransactionStatus.COMPLETED)
                .transactionType(TransactionType.TRANSFER)
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")));
        invalidValues.forEach(value -> Assertions.assertThrows(ValidationException.class, () -> builder.amount(value).build()));
    }

    @Test
    public void shouldThrowExceptionWithInvalidStatus() {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .sourceWallet(wallet1)
                .destinationWallet(wallet2)
                .amount(BigDecimal.valueOf(100.00))
                .transactionType(TransactionType.TRANSFER)
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")));
        Assertions.assertThrows(ValidationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWithInvalidType() {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .sourceWallet(wallet1)
                .destinationWallet(wallet2)
                .amount(BigDecimal.valueOf(100.00))
                .status(TransactionStatus.COMPLETED)
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")));
        Assertions.assertThrows(ValidationException.class, builder::build);
    }

    @Test
    public void shouldThrowExceptionWithInvalidTimestamp() {
        Transaction.TransactionBuilder builder = Transaction.builder()
                .operationId(UUID.randomUUID())
                .sourceWallet(wallet1)
                .destinationWallet(wallet2)
                .amount(BigDecimal.valueOf(100.00))
                .status(TransactionStatus.COMPLETED)
                .transactionType(TransactionType.TRANSFER);
        Assertions.assertThrows(ValidationException.class, builder::build);
    }
}

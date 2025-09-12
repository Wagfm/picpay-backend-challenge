package picpay.challenge.api.domain.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import picpay.challenge.api.domain.exception.InsufficientBalanceException;
import picpay.challenge.api.domain.exception.ValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Getter
@Builder
public class Wallet {
    @NotNull(message = "Id cannot be null")
    private final UUID id;

    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    @NotBlank(message = "CPF/CNPJ cannot be blank")
    @Length(min = 11, max = 14, message = "CPF/CNPJ must be valid")
    private String cpfCnpj;

    @NotBlank(message = "Email cannot be blank")
    @Length(min = 5, max = 50, message = "Email must have between 5 and 50 characters")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotNull(message = "Balance cannot be null")
    @DecimalMin(value = "0.00", message = "Balance must be greater than or equal to zero")
    private BigDecimal balance;

    public void deposit(BigDecimal amount) {
        if (amount == null) throw new ValidationException("Amount to deposit cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Amount to deposit cannot be negative");
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null) throw new ValidationException("Amount to withdraw cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Amount to withdraw cannot be negative");
        if (amount.compareTo(this.balance) > 0)
            throw new InsufficientBalanceException("Amount to withdraw cannot be greater than the current balance");
        this.balance = this.balance.subtract(amount);
    }

    public String getBalance(int decimalPlaces) {
        return balance.setScale(decimalPlaces, RoundingMode.HALF_DOWN).toString();
    }

    private Wallet(UUID id, String fullName, String cpfCnpj, String email, String password, BigDecimal balance) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.fullName = fullName;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.password = password;
        this.balance = balance == null ? BigDecimal.ZERO : balance;
        EntityValidator.validate(this);
    }
}

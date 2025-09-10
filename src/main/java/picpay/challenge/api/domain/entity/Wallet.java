package picpay.challenge.api.domain.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import picpay.challenge.api.domain.exception.InsufficientBalanceException;
import picpay.challenge.api.domain.exception.ValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class Wallet {
    private final Long id;

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

    public static Builder builder() {
        return new Builder();
    }

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

    public static class Builder {
        private Long id;
        private String fullName;
        private String cpfCnpj;
        private String email;
        private String password;
        private BigDecimal balance;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder cpfCnpj(String cpfCnpj) {
            this.cpfCnpj = cpfCnpj;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Wallet build() {
            if (balance == null) balance = BigDecimal.ZERO;
            return new Wallet(this);
        }

        private Builder() {
        }
    }

    private Wallet(Builder builder) {
        this.id = builder.id;
        this.fullName = builder.fullName;
        this.cpfCnpj = builder.cpfCnpj;
        this.email = builder.email;
        this.password = builder.password;
        this.balance = builder.balance;
        EntityValidator.validate(this);
    }
}

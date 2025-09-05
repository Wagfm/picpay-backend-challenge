package picpay.challenge.api.domain.entity;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class Wallet {
    private final Long id;
    private String fullName;
    private String cpfCnpj;
    private String email;
    private String password;
    private BigDecimal balance;

    public static Builder builder() {
        return new Builder();
    }

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
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
        validate();
    }

    private void validate() {
    }
}

package picpay.challenge.api.domain.enums;

import lombok.Getter;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.domain.exception.ValidationException;

@Getter
public enum TransactionType {
    DEPOSIT(1, "deposit") {
        @Override
        public void validate(Wallet sourceWallet, Wallet destinationWallet) {
            if (sourceWallet != null)
                throw new ValidationException("Source wallet must be null for deposit");
            if (destinationWallet == null)
                throw new ValidationException("Destination wallet must not be null for deposit");
        }
    },

    WITHDRAW(2, "withdrawal") {
        @Override
        public void validate(Wallet sourceWallet, Wallet destinationWallet) {
            if (sourceWallet == null)
                throw new ValidationException("Source wallet must not be null for withdrawal");
            if (destinationWallet != null)
                throw new ValidationException("Destination wallet must be null for withdrawal");
        }
    },

    TRANSFER(3, "transfer") {
        @Override
        public void validate(Wallet sourceWallet, Wallet destinationWallet) {
            if (sourceWallet == null) throw new ValidationException("Source wallet must not be null for transfer");
            if (destinationWallet == null)
                throw new ValidationException("Destination wallet must not be null for transfer");
            if (sourceWallet.equals(destinationWallet))
                throw new ValidationException("Source and destination wallet must be different for transfer");
        }
    };

    private final int id;
    private final String name;

    TransactionType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract void validate(Wallet sourceWallet, Wallet destinationWallet);
}
package picpay.challenge.api.domain.enums;

import lombok.Getter;
import picpay.challenge.api.domain.exception.ValidationException;

import java.util.UUID;

@Getter
public enum TransactionType {
    DEPOSIT(1, "deposit") {
        @Override
        public void validate(UUID sourceWalletId, UUID destinationWalletId) {
            if (sourceWalletId != null)
                throw new ValidationException("Source wallet ID must be null for deposit");
            if (destinationWalletId == null)
                throw new ValidationException("Destination wallet ID must not be null for deposit");
        }
    },

    WITHDRAW(2, "withdrawal") {
        @Override
        public void validate(UUID sourceWalletId, UUID destinationWalletId) {
            if (sourceWalletId == null)
                throw new ValidationException("Source wallet ID must not be null for withdrawal");
            if (destinationWalletId != null)
                throw new ValidationException("Destination wallet ID must be null for withdrawal");
        }
    },

    TRANSFER(3, "transfer") {
        @Override
        public void validate(UUID sourceWalletId, UUID destinationWalletId) {
            if (sourceWalletId == null) throw new ValidationException("Source wallet ID must not be null for transfer");
            if (destinationWalletId == null)
                throw new ValidationException("Destination wallet ID must not be null for transfer");
            if (sourceWalletId.equals(destinationWalletId))
                throw new ValidationException("Source and destination wallet IDs must be different for transfer");
        }
    };

    private final int id;
    private final String name;

    TransactionType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract void validate(UUID sourceWalletId, UUID destinationWalletId);
}
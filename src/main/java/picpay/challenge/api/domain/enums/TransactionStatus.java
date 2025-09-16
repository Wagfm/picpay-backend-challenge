package picpay.challenge.api.domain.enums;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    PENDING(1, "pending"),
    COMPLETED(2, "completed"),
    FAILED(3, "failed");

    private final int id;
    private final String name;

    TransactionStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
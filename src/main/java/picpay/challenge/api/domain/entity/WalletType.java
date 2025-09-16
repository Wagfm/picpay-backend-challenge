package picpay.challenge.api.domain.entity;

import lombok.Getter;

@Getter
public enum WalletType {
    CUSTOMER(1, "customer"),
    MERCHANT(2, "merchant");

    private final int id;
    private final String name;

    WalletType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

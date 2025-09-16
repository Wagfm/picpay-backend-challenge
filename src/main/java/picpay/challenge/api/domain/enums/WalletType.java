package picpay.challenge.api.domain.enums;

import lombok.Getter;

@Getter
public enum WalletType {
    CUSTOMER(1, "customer"),
    MERCHANT(2, "merchant");

    private final int id;
    private final String name;

    @Override
    public String toString() {
        return name;
    }

    public static WalletType fromName(String name) {
        for (WalletType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    WalletType(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

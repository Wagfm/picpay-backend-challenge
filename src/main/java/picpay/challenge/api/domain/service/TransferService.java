package picpay.challenge.api.domain.service;

import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.domain.entity.WalletType;
import picpay.challenge.api.domain.exception.ValidationException;

import java.math.BigDecimal;

public final class TransferService {
    private TransferService() {
    }

    public static void transfer(Wallet payer, Wallet payee, BigDecimal amount) {
        if (payer.getId().equals(payee.getId())) throw new ValidationException("Cannot transfer to same wallet");
        if (payer.getWalletType() == WalletType.MERCHANT)
            throw new ValidationException("Cannot transfer from a merchant wallet");
        payer.withdraw(amount);
        payee.deposit(amount);
    }
}

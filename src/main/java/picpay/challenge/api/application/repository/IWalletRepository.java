package picpay.challenge.api.application.repository;

import picpay.challenge.api.domain.entity.Wallet;

public interface IWalletRepository {
    Wallet findById(Long id);
    Wallet save(Wallet wallet);
    Wallet update(Wallet wallet);
}

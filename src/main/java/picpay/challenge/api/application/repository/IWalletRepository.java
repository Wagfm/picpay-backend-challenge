package picpay.challenge.api.application.repository;

import picpay.challenge.api.domain.entity.Wallet;

import java.util.List;

public interface IWalletRepository {
    Wallet findById(Long id);
    Wallet save(Wallet wallet);
    Wallet update(Wallet wallet);
    List<Wallet> findBy(String cpfCnpj, String email);
}

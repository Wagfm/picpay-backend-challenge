package picpay.challenge.api.application.repository;

import picpay.challenge.api.domain.entity.Wallet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IWalletRepository {
    Optional<Wallet> findById(UUID id);

    Wallet save(Wallet wallet);

    Optional<Wallet> update(Wallet wallet);

    List<Wallet> findBy(String cpfCnpj, String email);
}

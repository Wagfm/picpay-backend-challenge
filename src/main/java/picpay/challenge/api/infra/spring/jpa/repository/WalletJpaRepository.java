package picpay.challenge.api.infra.spring.jpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.infra.spring.jpa.entity.WalletJpa;
import picpay.challenge.api.infra.spring.jpa.mapper.JpaMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WalletJpaRepository implements IWalletRepository {
    private final IWalletJpaRepository jpaRepository;

    @Override
    public Optional<Wallet> findById(UUID id) {
        if (id == null) return Optional.empty();
        return jpaRepository.findByWalletId(id).map(JpaMapper::toWallet);
    }

    @Override
    public Wallet save(Wallet wallet) {
        return JpaMapper.toWallet(jpaRepository.save(JpaMapper.toWalletJpa(wallet)));
    }

    @Override
    public Optional<Wallet> update(Wallet wallet) {
        WalletJpa updatedWallet = JpaMapper.toWalletJpa(wallet);
        return jpaRepository.updateByWalletId(updatedWallet).map(JpaMapper::toWallet);
    }

    @Override
    public List<Wallet> findBy(String cpfCnpj, String email) {
        return jpaRepository.findByCpfCnpjOrEmail(cpfCnpj, email)
                .stream()
                .map(JpaMapper::toWallet)
                .toList();
    }
}

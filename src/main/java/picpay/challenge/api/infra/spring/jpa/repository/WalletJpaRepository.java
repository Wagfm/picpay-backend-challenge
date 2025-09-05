package picpay.challenge.api.infra.spring.jpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.infra.spring.jpa.mapper.JpaMapper;

@Repository
@RequiredArgsConstructor
public class WalletJpaRepository implements IWalletRepository {
    private final IWalletJpaRepository jpaRepository;

    @Override
    public Wallet findById(Long id) {
        return JpaMapper.toWallet(jpaRepository.findById(id).orElseThrow(RuntimeException::new));
    }

    @Override
    public Wallet save(Wallet wallet) {
        return JpaMapper.toWallet(jpaRepository.save(JpaMapper.toWalletJpa(wallet)));
    }

    @Override
    public Wallet update(Wallet wallet) {
        return save(wallet);
    }
}

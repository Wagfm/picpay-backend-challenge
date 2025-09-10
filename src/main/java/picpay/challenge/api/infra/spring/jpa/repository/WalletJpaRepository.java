package picpay.challenge.api.infra.spring.jpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import picpay.challenge.api.application.exception.NotFoundException;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.infra.spring.jpa.mapper.JpaMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WalletJpaRepository implements IWalletRepository {
    private final IWalletJpaRepository jpaRepository;

    @Override
    public Wallet findById(Long id) {
        if (id == null) throw new NotFoundException("Id not found");
        return JpaMapper.toWallet(jpaRepository.findById(id).orElseThrow(() -> new NotFoundException("Wallet not found")));
    }

    @Override
    public Wallet save(Wallet wallet) {
        return JpaMapper.toWallet(jpaRepository.save(JpaMapper.toWalletJpa(wallet)));
    }

    @Override
    public Wallet update(Wallet wallet) {
        return JpaMapper.toWallet(jpaRepository.save(JpaMapper.toWalletJpa(wallet)));
    }

    @Override
    public List<Wallet> findBy(String cpfCnpj, String email) {
        return jpaRepository.findByCpfCnpjOrEmail(cpfCnpj, email)
                .stream()
                .map(JpaMapper::toWallet)
                .toList();
    }
}

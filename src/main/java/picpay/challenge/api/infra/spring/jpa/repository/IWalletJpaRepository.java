package picpay.challenge.api.infra.spring.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import picpay.challenge.api.infra.spring.jpa.entity.WalletJpa;

import java.util.List;

public interface IWalletJpaRepository extends JpaRepository<WalletJpa, Long> {
    List<WalletJpa> findByCpfCnpjOrEmail(String cpfCnpj, String email);
}

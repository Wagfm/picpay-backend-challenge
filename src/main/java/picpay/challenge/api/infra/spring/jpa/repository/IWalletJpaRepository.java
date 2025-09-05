package picpay.challenge.api.infra.spring.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import picpay.challenge.api.infra.spring.jpa.entity.WalletJpa;

public interface IWalletJpaRepository extends JpaRepository<WalletJpa, Long> {
}

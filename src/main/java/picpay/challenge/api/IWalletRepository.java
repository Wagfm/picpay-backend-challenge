package picpay.challenge.api;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IWalletRepository extends JpaRepository<Wallet, Long> {
}

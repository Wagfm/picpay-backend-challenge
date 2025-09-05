package picpay.challenge.api.infra.spring.jpa.mapper;

import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.infra.spring.jpa.entity.WalletJpa;

public final class JpaMapper {
    private JpaMapper() {
    }

    public static Wallet toWallet(WalletJpa walletJpa) {
        return Wallet.builder()
                .id(walletJpa.getId())
                .fullName(walletJpa.getFullName())
                .cpfCnpj(walletJpa.getCpfCnpj())
                .email(walletJpa.getEmail())
                .password(walletJpa.getPassword())
                .balance(walletJpa.getBalance())
                .build();
    }

    public static WalletJpa toWalletJpa(Wallet wallet) {
        WalletJpa walletJpa = new WalletJpa();
        walletJpa.setId(wallet.getId());
        walletJpa.setFullName(wallet.getFullName());
        walletJpa.setCpfCnpj(wallet.getCpfCnpj());
        walletJpa.setEmail(wallet.getEmail());
        walletJpa.setPassword(wallet.getPassword());
        walletJpa.setBalance(wallet.getBalance());
        return walletJpa;
    }
}

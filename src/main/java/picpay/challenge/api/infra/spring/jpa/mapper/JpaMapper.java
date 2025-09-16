package picpay.challenge.api.infra.spring.jpa.mapper;

import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.domain.entity.WalletType;
import picpay.challenge.api.infra.spring.jpa.entity.WalletJpa;

public final class JpaMapper {
    private JpaMapper() {
    }

    public static Wallet toWallet(WalletJpa walletJpa) {
        return Wallet.builder()
                .id(walletJpa.getWalletId())
                .fullName(walletJpa.getFullName())
                .cpfCnpj(walletJpa.getCpfCnpj())
                .email(walletJpa.getEmail())
                .password(walletJpa.getPassword())
                .balance(walletJpa.getBalance())
                .walletType(WalletType.valueOf(walletJpa.getWalletType()))
                .build();
    }

    public static WalletJpa toWalletJpa(Wallet wallet) {
        return WalletJpa.builder()
                .walletId(wallet.getId())
                .fullName(wallet.getFullName())
                .cpfCnpj(wallet.getCpfCnpj())
                .email(wallet.getEmail())
                .password(wallet.getPassword())
                .balance(wallet.getBalance())
                .walletType(wallet.getWalletType().toString())
                .build();
    }
}

package picpay.challenge.api.infra.spring.jpa.mapper;

import picpay.challenge.api.domain.entity.Transaction;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.domain.enums.WalletType;
import picpay.challenge.api.infra.spring.jpa.entity.TransactionJpa;
import picpay.challenge.api.infra.spring.jpa.entity.WalletJpa;

public final class JpaMapper {
    private JpaMapper() {
    }

    public static Wallet toWallet(WalletJpa walletJpa) {
        if (walletJpa == null) return null;
        return Wallet.builder()
                .id(walletJpa.getWalletId())
                .fullName(walletJpa.getFullName())
                .cpfCnpj(walletJpa.getCpfCnpj())
                .email(walletJpa.getEmail())
                .password(walletJpa.getPassword())
                .balance(walletJpa.getBalance())
                .walletType(WalletType.fromName(walletJpa.getWalletType()))
                .build();
    }

    public static WalletJpa toWalletJpa(Wallet wallet) {
        if (wallet == null) return null;
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

    public static Transaction toTransaction(TransactionJpa transactionJpa) {
        if (transactionJpa == null) return null;
        return Transaction.builder()
                .id(transactionJpa.getTransactionId())
                .operationId(transactionJpa.getOperationId())
                .sourceWallet(toWallet(transactionJpa.getSourceWallet()))
                .destinationWallet(toWallet(transactionJpa.getDestinationWallet()))
                .amount(transactionJpa.getAmount())
                .transactionType(transactionJpa.getTransactionType())
                .status(transactionJpa.getStatus())
                .timestamp(transactionJpa.getTimestamp())
                .build();
    }

    public static TransactionJpa toTransactionJpa(Transaction transaction) {
        if (transaction == null) return null;
        return TransactionJpa.builder()
                .transactionId(transaction.getId())
                .operationId(transaction.getOperationId())
                .sourceWallet(toWalletJpa(transaction.getSourceWallet()))
                .destinationWallet(toWalletJpa(transaction.getDestinationWallet()))
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .status(transaction.getStatus())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}

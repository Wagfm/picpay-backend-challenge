package picpay.challenge.api.infra.spring.jpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import picpay.challenge.api.application.repository.ITransactionRepository;
import picpay.challenge.api.domain.entity.Transaction;
import picpay.challenge.api.infra.spring.jpa.entity.TransactionJpa;
import picpay.challenge.api.infra.spring.jpa.mapper.JpaMapper;

@Repository
@RequiredArgsConstructor
public class TransactionJpaRepository implements ITransactionRepository {
    private final ITransactionJpaRepository transactionJpaRepository;
    private final IWalletJpaRepository walletJpaRepository;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionJpa transactionJpa = JpaMapper.toTransactionJpa(transaction);
        if (transaction.getSourceWallet() != null)
            walletJpaRepository.findByWalletId(transaction.getSourceWallet().getId())
                    .ifPresent(transactionJpa::setSourceWallet);
        if (transaction.getDestinationWallet() != null)
            walletJpaRepository.findByWalletId(transaction.getDestinationWallet().getId())
                    .ifPresent(transactionJpa::setDestinationWallet);
        return JpaMapper.toTransaction(transactionJpaRepository.save(transactionJpa));
    }
}

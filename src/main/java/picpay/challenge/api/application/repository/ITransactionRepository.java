package picpay.challenge.api.application.repository;

import picpay.challenge.api.domain.entity.Transaction;

public interface ITransactionRepository {
    Transaction save(Transaction transaction);
}

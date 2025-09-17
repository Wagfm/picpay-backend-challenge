package picpay.challenge.api.application.usecase.dto;

import picpay.challenge.api.domain.entity.Transaction;

import java.math.RoundingMode;

public final class TransactionMapper {
    public static TransactionDTO toDto(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .operationId(transaction.getOperationId())
                .sourceWallet(transaction.getSourceWalletId())
                .destinationWallet(transaction.getDestinationWalletId())
                .amount(transaction.getAmount().setScale(2, RoundingMode.HALF_UP).toString())
                .operationType(transaction.getTransactionType().name())
                .status(transaction.getStatus().name())
                .timestamp(transaction.getTimestamp().toString())
                .build();
    }
}

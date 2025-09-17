package picpay.challenge.api.application.usecase.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TransactionDTO(
        UUID id,
        UUID operationId,
        UUID sourceWallet,
        UUID destinationWallet,
        String amount,
        String status,
        String operationType,
        String timestamp
) { }
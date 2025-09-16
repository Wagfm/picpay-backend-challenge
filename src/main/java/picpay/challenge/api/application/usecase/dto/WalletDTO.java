package picpay.challenge.api.application.usecase.dto;

import picpay.challenge.api.domain.enums.WalletType;

import java.util.UUID;

public record WalletDTO(UUID id, String fullName, String email, String balance, WalletType walletType) {
}

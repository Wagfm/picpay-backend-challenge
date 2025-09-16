package picpay.challenge.api.application.usecase.dto;

import lombok.Builder;
import picpay.challenge.api.domain.entity.WalletType;

@Builder
public record CreateWalletDTO(String fullName, String cpfCnpj, String email, String password, WalletType walletType) {
}

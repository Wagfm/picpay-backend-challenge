package picpay.challenge.api.application.usecase.dto;

import lombok.Builder;

@Builder
public record CreateWalletDTO(String fullName, String cpfCnpj, String email, String password) {
}

package picpay.challenge.api.application.usecase.dto;

import java.util.UUID;

public record WalletDTO(UUID id, String fullName, String email, String balance) {
}

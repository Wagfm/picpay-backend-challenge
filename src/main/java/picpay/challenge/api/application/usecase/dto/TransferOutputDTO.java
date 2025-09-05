package picpay.challenge.api.application.usecase.dto;

public record TransferOutputDTO(WalletDTO payer, WalletDTO payee) {
}

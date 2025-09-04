package picpay.challenge.api;

public record CreateWalletDTO(String fullName, String cpfCnpj, String email, String password) {
}

package picpay.challenge.api.application.usecase;

import lombok.RequiredArgsConstructor;
import picpay.challenge.api.application.usecase.dto.CreateWalletDTO;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.application.usecase.dto.WalletDTO;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CreateWallet implements ICommand<CreateWalletDTO, WalletDTO> {
    private final IWalletRepository walletRepository;

    @Override
    public WalletDTO execute(CreateWalletDTO input) {
        Wallet walletToCreate = Wallet.builder()
                .fullName(input.fullName())
                .cpfCnpj(input.cpfCnpj())
                .email(input.email())
                .password(input.password())
                .balance(BigDecimal.ZERO)
                .build();
        Wallet wallet = walletRepository.save(walletToCreate);
        return new WalletDTO(wallet.getId(), wallet.getFullName(), wallet.getEmail(), wallet.getBalance(2));
    }
}

package picpay.challenge.api.application.usecase;

import lombok.RequiredArgsConstructor;
import picpay.challenge.api.application.exception.ConflictException;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.application.usecase.dto.CreateWalletDTO;
import picpay.challenge.api.application.usecase.dto.WalletDTO;
import picpay.challenge.api.domain.entity.Wallet;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class CreateWallet implements ICommand<CreateWalletDTO, WalletDTO> {
    private final IWalletRepository walletRepository;

    @Override
    public WalletDTO execute(CreateWalletDTO input) {
        List<Wallet> conflicts = this.walletRepository.findBy(input.cpfCnpj(), input.email());
        if (!conflicts.isEmpty()) {
            Wallet conflictingWallet = conflicts.getFirst();
            if (conflictingWallet.getCpfCnpj().equals(input.cpfCnpj()))
                throw new ConflictException("CPF/CNPJ already exists");
            throw new ConflictException("Email already exists");
        }
        Wallet walletToCreate = Wallet.builder()
                .fullName(input.fullName())
                .cpfCnpj(input.cpfCnpj())
                .email(input.email())
                .password(input.password())
                .balance(BigDecimal.ZERO)
                .walletType(input.walletType())
                .build();
        Wallet wallet = walletRepository.save(walletToCreate);
        return new WalletDTO(wallet.getId(), wallet.getFullName(), wallet.getEmail(), wallet.getBalance(2), wallet.getWalletType());
    }
}

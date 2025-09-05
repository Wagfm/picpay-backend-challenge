package picpay.challenge.api.application.usecase;

import lombok.RequiredArgsConstructor;
import picpay.challenge.api.application.usecase.dto.DepositDTO;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.application.usecase.dto.WalletDTO;

@RequiredArgsConstructor
public class Deposit implements ICommand<DepositDTO, WalletDTO> {
    private final IWalletRepository walletRepository;

    @Override
    public WalletDTO execute(DepositDTO input) {
        Wallet walletToUpdate = walletRepository.findById(input.id());
        walletToUpdate.deposit(input.amount());
        Wallet wallet = walletRepository.update(walletToUpdate);
        return new WalletDTO(wallet.getId(), wallet.getFullName(), wallet.getEmail(), wallet.getBalance(2));
    }
}

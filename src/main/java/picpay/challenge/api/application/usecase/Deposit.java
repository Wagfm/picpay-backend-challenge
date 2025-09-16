package picpay.challenge.api.application.usecase;

import lombok.RequiredArgsConstructor;
import picpay.challenge.api.application.exception.NotFoundException;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.application.usecase.dto.DepositDTO;
import picpay.challenge.api.application.usecase.dto.WalletDTO;
import picpay.challenge.api.domain.entity.Wallet;

import java.util.Optional;

@RequiredArgsConstructor
public class Deposit implements ICommand<DepositDTO, WalletDTO> {
    private final IWalletRepository walletRepository;

    @Override
    public WalletDTO execute(DepositDTO input) {
        Wallet walletToUpdate = walletRepository.findById(input.id()).orElseThrow(() -> new NotFoundException("Wallet not found"));
        walletToUpdate.deposit(input.amount());
        Optional<Wallet> optionalWallet = walletRepository.update(walletToUpdate);
        if (optionalWallet.isEmpty()) throw new NotFoundException("Wallet not found");
        Wallet wallet = optionalWallet.get();
        return new WalletDTO(wallet.getId(), wallet.getFullName(), wallet.getEmail(), wallet.getBalance(2), wallet.getWalletType());
    }
}

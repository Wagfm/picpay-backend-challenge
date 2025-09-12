package picpay.challenge.api.application.usecase;

import lombok.RequiredArgsConstructor;
import picpay.challenge.api.application.exception.NotFoundException;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.application.usecase.dto.TransferDTO;
import picpay.challenge.api.application.usecase.dto.TransferOutputDTO;
import picpay.challenge.api.application.usecase.dto.WalletDTO;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.domain.service.TransferService;

@RequiredArgsConstructor
public class Transfer implements ICommand<TransferDTO, TransferOutputDTO> {
    private final IWalletRepository walletRepository;

    @Override
    public TransferOutputDTO execute(TransferDTO input) {
        Wallet payer = walletRepository.findById(input.payer()).orElseThrow(() -> new NotFoundException("Payer wallet not found"));
        Wallet payee = walletRepository.findById(input.payee()).orElseThrow(() -> new NotFoundException("Payee wallet not found"));
        TransferService.transfer(payer, payee, input.amount());
        walletRepository.update(payer);
        walletRepository.update(payee);
        return new TransferOutputDTO(
                new WalletDTO(payer.getId(), payer.getFullName(), payer.getEmail(), payer.getBalance(2)),
                new WalletDTO(payee.getId(), payee.getFullName(), payee.getEmail(), payee.getBalance(2))
        );
    }
}

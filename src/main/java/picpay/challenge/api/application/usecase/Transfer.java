package picpay.challenge.api.application.usecase;

import lombok.RequiredArgsConstructor;
import picpay.challenge.api.application.exception.NotFoundException;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.application.usecase.dto.TransactionDTO;
import picpay.challenge.api.application.usecase.dto.TransferDTO;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.domain.service.TransferService;

import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class Transfer implements ICommand<TransferDTO, TransactionDTO> {
    private final IWalletRepository walletRepository;

    @Override
    public TransactionDTO execute(TransferDTO input) {
        Wallet payer = walletRepository.findById(input.payer())
                .orElseThrow(() -> new NotFoundException("Payer wallet not found"));
        Wallet payee = walletRepository.findById(input.payee())
                .orElseThrow(() -> new NotFoundException("Payee wallet not found"));
        TransferService.transfer(payer, payee, input.amount());
        walletRepository.update(payer);
        walletRepository.update(payee);
        return TransactionDTO.builder()
                .operationId(UUID.randomUUID())
                .sourceWallet(payer.getId())
                .destinationWallet(payee.getId())
                .amount(input.amount().setScale(2, RoundingMode.HALF_UP).toString())
                .status("COMPLETED")
                .operationType("TRANSFER")
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")).toString())
                .build();
    }
}

package picpay.challenge.api.application.usecase;

import lombok.RequiredArgsConstructor;
import picpay.challenge.api.application.exception.NotFoundException;
import picpay.challenge.api.application.repository.ITransactionRepository;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.application.usecase.dto.TransactionDTO;
import picpay.challenge.api.application.usecase.dto.TransactionMapper;
import picpay.challenge.api.application.usecase.dto.TransferDTO;
import picpay.challenge.api.domain.entity.Transaction;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.domain.enums.TransactionStatus;
import picpay.challenge.api.domain.enums.TransactionType;
import picpay.challenge.api.domain.service.TransferService;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RequiredArgsConstructor
public class Transfer implements ICommand<TransferDTO, TransactionDTO> {
    private final IWalletRepository walletRepository;
    private final ITransactionRepository transactionRepository;

    @Override
    public TransactionDTO execute(TransferDTO input) {
        Wallet payer = walletRepository.findById(input.payer())
                .orElseThrow(() -> new NotFoundException("Payer wallet not found"));
        Wallet payee = walletRepository.findById(input.payee())
                .orElseThrow(() -> new NotFoundException("Payee wallet not found"));
        TransferService.transfer(payer, payee, input.amount());
        walletRepository.update(payer);
        walletRepository.update(payee);
        Transaction transaction = Transaction.builder()
                .sourceWallet(payer)
                .destinationWallet(payee)
                .amount(input.amount())
                .status(TransactionStatus.COMPLETED)
                .transactionType(TransactionType.TRANSFER)
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")))
                .build();
        return TransactionMapper.toDto(transactionRepository.save(transaction));
    }
}

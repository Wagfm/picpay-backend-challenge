package picpay.challenge.api.application.usecase;

import lombok.RequiredArgsConstructor;
import picpay.challenge.api.application.exception.NotFoundException;
import picpay.challenge.api.application.repository.ITransactionRepository;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.application.usecase.dto.DepositDTO;
import picpay.challenge.api.application.usecase.dto.TransactionDTO;
import picpay.challenge.api.application.usecase.dto.TransactionMapper;
import picpay.challenge.api.domain.entity.Transaction;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.domain.enums.TransactionStatus;
import picpay.challenge.api.domain.enums.TransactionType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public class Deposit implements ICommand<DepositDTO, TransactionDTO> {
    private final IWalletRepository walletRepository;
    private final ITransactionRepository transactionRepository;

    @Override
    public TransactionDTO execute(DepositDTO input) {
        Wallet walletToUpdate = walletRepository.findById(input.id()).orElseThrow(() -> new NotFoundException("Wallet not found"));
        walletToUpdate.deposit(input.amount());
        Optional<Wallet> optionalWallet = walletRepository.update(walletToUpdate);
        if (optionalWallet.isEmpty()) throw new NotFoundException("Wallet not found");
        Wallet wallet = optionalWallet.get();
        Transaction transaction = Transaction.builder()
                .destinationWallet(wallet)
                .amount(input.amount())
                .transactionType(TransactionType.DEPOSIT)
                .status(TransactionStatus.COMPLETED)
                .timestamp(ZonedDateTime.now(ZoneId.of("GMT")))
                .build();
        return TransactionMapper.toDto(transactionRepository.save(transaction));
    }
}

package picpay.challenge.api.application.usecase;

import lombok.RequiredArgsConstructor;
import picpay.challenge.api.application.exception.NotFoundException;
import picpay.challenge.api.application.exception.OperationNotAuthorizedException;
import picpay.challenge.api.application.gateway.IAuthorizationGateway;
import picpay.challenge.api.application.gateway.INotificationGateway;
import picpay.challenge.api.application.gateway.dto.AuthorizationInputDto;
import picpay.challenge.api.application.gateway.dto.AuthorizationOutputDto;
import picpay.challenge.api.application.gateway.dto.NotificationInputDto;
import picpay.challenge.api.application.gateway.dto.NotificationOutputDto;
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
    private final IAuthorizationGateway<AuthorizationInputDto, AuthorizationOutputDto> authorizationGateway;
    private final INotificationGateway<NotificationInputDto, NotificationOutputDto> notificationGateway;

    @Override
    public TransactionDTO execute(TransferDTO input) {
        Wallet payer = walletRepository.findById(input.payer()).orElseThrow(() -> new NotFoundException("Payer wallet not found"));
        Wallet payee = walletRepository.findById(input.payee()).orElseThrow(() -> new NotFoundException("Payee wallet not found"));
        AuthorizationOutputDto dto = authorizationGateway.getAuthorization(new AuthorizationInputDto());
        if (!dto.data().authorization()) throw new OperationNotAuthorizedException("Transfer operation not authorized");
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
        notificationGateway.notify(new NotificationInputDto("whatsapp"));
        return TransactionMapper.toDto(transactionRepository.save(transaction));
    }
}

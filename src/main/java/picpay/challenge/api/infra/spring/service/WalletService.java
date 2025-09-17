package picpay.challenge.api.infra.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import picpay.challenge.api.application.usecase.ICommand;
import picpay.challenge.api.application.usecase.dto.CreateWalletDTO;
import picpay.challenge.api.application.usecase.dto.DepositDTO;
import picpay.challenge.api.application.usecase.dto.TransactionDTO;
import picpay.challenge.api.application.usecase.dto.TransferDTO;
import picpay.challenge.api.application.usecase.dto.WalletDTO;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final ICommand<CreateWalletDTO, WalletDTO> createWalletCommand;
    private final ICommand<DepositDTO, TransactionDTO> depositCommand;
    private final ICommand<TransferDTO, TransactionDTO> transferCommand;

    public WalletDTO createWallet(CreateWalletDTO dto) {
        return createWalletCommand.execute(dto);
    }

    public TransactionDTO deposit(DepositDTO dto) {
        return depositCommand.execute(dto);
    }

    public TransactionDTO transfer(TransferDTO dto) {
        return transferCommand.execute(dto);
    }
}

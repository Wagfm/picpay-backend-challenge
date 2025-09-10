package picpay.challenge.api.infra.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import picpay.challenge.api.application.usecase.ICommand;
import picpay.challenge.api.application.usecase.dto.CreateWalletDTO;
import picpay.challenge.api.application.usecase.dto.DepositDTO;
import picpay.challenge.api.application.usecase.dto.TransferDTO;
import picpay.challenge.api.application.usecase.dto.TransferOutputDTO;
import picpay.challenge.api.application.usecase.dto.WalletDTO;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final ICommand<CreateWalletDTO, WalletDTO> createWalletCommand;
    private final ICommand<DepositDTO, WalletDTO> depositCommand;
    private final ICommand<TransferDTO, TransferOutputDTO> transferCommand;

    public WalletDTO createWallet(CreateWalletDTO dto) {
        return createWalletCommand.execute(dto);
    }

    public void deposit(DepositDTO dto) {
        depositCommand.execute(dto);
    }

    public TransferOutputDTO transfer(TransferDTO dto) {
        return transferCommand.execute(dto);
    }
}

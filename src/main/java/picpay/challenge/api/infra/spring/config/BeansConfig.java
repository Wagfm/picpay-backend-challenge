package picpay.challenge.api.infra.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import picpay.challenge.api.application.usecase.dto.CreateWalletDTO;
import picpay.challenge.api.application.usecase.dto.DepositDTO;
import picpay.challenge.api.application.usecase.ICommand;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.application.usecase.dto.TransferDTO;
import picpay.challenge.api.application.usecase.dto.TransferOutputDTO;
import picpay.challenge.api.application.usecase.dto.WalletDTO;
import picpay.challenge.api.application.usecase.CreateWallet;
import picpay.challenge.api.application.usecase.Deposit;
import picpay.challenge.api.application.usecase.Transfer;

@Configuration
public class BeansConfig {
    @Bean
    public ICommand<CreateWalletDTO, WalletDTO> createWalletCommand(IWalletRepository walletRepository) {
        return new CreateWallet(walletRepository);
    }

    @Bean
    public ICommand<DepositDTO, WalletDTO> depositCommand(IWalletRepository walletRepository) {
        return new Deposit(walletRepository);
    }

    @Bean
    public ICommand<TransferDTO, TransferOutputDTO> transferCommand(IWalletRepository walletRepository) {
        return new Transfer(walletRepository);
    }
}

package picpay.challenge.api.infra.spring.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import picpay.challenge.api.application.gateway.IAuthorizationGateway;
import picpay.challenge.api.application.gateway.INotificationGateway;
import picpay.challenge.api.application.gateway.dto.AuthorizationInputDto;
import picpay.challenge.api.application.gateway.dto.AuthorizationOutputDto;
import picpay.challenge.api.application.gateway.dto.NotificationInputDto;
import picpay.challenge.api.application.gateway.dto.NotificationOutputDto;
import picpay.challenge.api.application.repository.ITransactionRepository;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.application.usecase.CreateWallet;
import picpay.challenge.api.application.usecase.Deposit;
import picpay.challenge.api.application.usecase.ICommand;
import picpay.challenge.api.application.usecase.Transfer;
import picpay.challenge.api.application.usecase.dto.CreateWalletDTO;
import picpay.challenge.api.application.usecase.dto.DepositDTO;
import picpay.challenge.api.application.usecase.dto.TransactionDTO;
import picpay.challenge.api.application.usecase.dto.TransferDTO;
import picpay.challenge.api.application.usecase.dto.WalletDTO;

@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class BeansConfig {
    @Bean
    public ICommand<CreateWalletDTO, WalletDTO> createWalletCommand(IWalletRepository walletRepository) {
        return new CreateWallet(walletRepository);
    }

    @Bean
    public ICommand<DepositDTO, TransactionDTO> depositCommand(IWalletRepository walletRepository, ITransactionRepository transactionRepository) {
        return new Deposit(walletRepository, transactionRepository);
    }

    @Bean
    public ICommand<TransferDTO, TransactionDTO> transferCommand(IWalletRepository walletRepository, ITransactionRepository transactionRepository, IAuthorizationGateway<AuthorizationInputDto, AuthorizationOutputDto> authorizationGateway, INotificationGateway<NotificationInputDto, NotificationOutputDto> notificationGateway) {
        return new Transfer(walletRepository, transactionRepository, authorizationGateway, notificationGateway);
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        builder.defaultHeader("Content-Type", "application/json");
        builder.defaultHeader("Accept", "application/json");
        builder.defaultHeader("User-Agent", "PicpayChallengeApi");
        return builder.build();
    }
}

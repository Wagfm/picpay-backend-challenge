package picpay.challenge.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import picpay.challenge.api.application.exception.NotFoundException;
import picpay.challenge.api.application.exception.OperationNotAuthorizedException;
import picpay.challenge.api.application.gateway.IAuthorizationGateway;
import picpay.challenge.api.application.gateway.INotificationGateway;
import picpay.challenge.api.application.gateway.dto.AuthorizationInputDto;
import picpay.challenge.api.application.gateway.dto.AuthorizationOutputDto;
import picpay.challenge.api.application.gateway.dto.Data;
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
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.domain.enums.WalletType;
import picpay.challenge.api.domain.exception.InsufficientBalanceException;
import picpay.challenge.api.domain.exception.ValidationException;
import picpay.challenge.api.infra.spring.config.GatewayProperties;
import picpay.challenge.api.infra.spring.gateway.AuthorizationGateway;
import picpay.challenge.api.infra.spring.gateway.NotificationGateway;
import picpay.challenge.api.infra.spring.jpa.repository.ITransactionJpaRepository;
import picpay.challenge.api.infra.spring.jpa.repository.IWalletJpaRepository;
import picpay.challenge.api.infra.spring.jpa.repository.TransactionJpaRepository;
import picpay.challenge.api.infra.spring.jpa.repository.WalletJpaRepository;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@Transactional
@DataJpaTest
public class TransferTests {
    private final ICommand<CreateWalletDTO, WalletDTO> createWalletCommand;
    private final ICommand<DepositDTO, TransactionDTO> depositCommand;
    private final ICommand<TransferDTO, TransactionDTO> transferCommand;
    private final IWalletRepository walletRepository;
    private final MockRestServiceServer mockServer;
    private final ObjectMapper objectMapper;
    private WalletDTO payer;
    private WalletDTO payee;
    @Autowired
    private GatewayProperties gatewayProperties;

    @Autowired
    public TransferTests(ICommand<CreateWalletDTO, WalletDTO> createWalletCommand, ICommand<DepositDTO, TransactionDTO> depositCommand, ICommand<TransferDTO, TransactionDTO> transferCommand, IWalletRepository walletRepository, RestTemplate restTemplate) {
        this.createWalletCommand = createWalletCommand;
        this.depositCommand = depositCommand;
        this.transferCommand = transferCommand;
        this.walletRepository = walletRepository;
        this.mockServer = MockRestServiceServer.createServer(restTemplate);
        this.objectMapper = new ObjectMapper();
    }

    @TestConfiguration
    @EnableConfigurationProperties(GatewayProperties.class)
    public static class Config {
        @Bean
        public IWalletRepository walletRepository(IWalletJpaRepository walletJpaRepository) {
            return new WalletJpaRepository(walletJpaRepository);
        }

        @Bean
        public ITransactionRepository transactionRepository(ITransactionJpaRepository transactionJpaRepository, IWalletJpaRepository walletJpaRepository) {
            return new TransactionJpaRepository(transactionJpaRepository, walletJpaRepository);
        }

        @Bean
        public ICommand<CreateWalletDTO, WalletDTO> createWalletCommand(IWalletRepository walletRepository) {
            return new CreateWallet(walletRepository);
        }

        @Bean
        public ICommand<DepositDTO, TransactionDTO> depositCommand(IWalletRepository walletRepository, ITransactionRepository transactionRepository) {
            return new Deposit(walletRepository, transactionRepository);
        }

        @Bean
        public RestTemplate restTemplate() {
            RestTemplateBuilder builder = new RestTemplateBuilder();
            builder.defaultHeader("Content-Type", "application/json");
            builder.defaultHeader("Accept", "application/json");
            builder.defaultHeader("User-Agent", "PicpayChallengeApi");
            return builder.build();
        }

        @Bean
        public IAuthorizationGateway<AuthorizationInputDto, AuthorizationOutputDto> authorizationGateway(RestTemplate restTemplate, GatewayProperties gatewayProperties) {
            return new AuthorizationGateway(restTemplate, gatewayProperties);
        }

        @Bean
        public INotificationGateway<NotificationInputDto, NotificationOutputDto> notificationGateway(RestTemplate restTemplate, GatewayProperties gatewayProperties) {
            return new NotificationGateway(restTemplate, gatewayProperties);
        }

        @Bean
        public ICommand<TransferDTO, TransactionDTO> transferCommand(IWalletRepository walletRepository, ITransactionRepository transactionRepository, IAuthorizationGateway<AuthorizationInputDto, AuthorizationOutputDto> authorizationGateway, INotificationGateway<NotificationInputDto, NotificationOutputDto> notificationGateway) {
            return new Transfer(walletRepository, transactionRepository, authorizationGateway, notificationGateway);
        }
    }

    @BeforeEach
    public void setUp() {
        mockServer.reset();
        CreateWalletDTO payerDto = CreateWalletDTO.builder()
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234")
                .walletType(WalletType.CUSTOMER)
                .build();
        this.payer = createWalletCommand.execute(payerDto);
        depositCommand.execute(new DepositDTO(payer.id(), BigDecimal.valueOf(50.00)));
        CreateWalletDTO payeeDto = CreateWalletDTO.builder()
                .fullName("Amanda Maciel")
                .cpfCnpj("09876543211")
                .email("amanda.maciel@email.com")
                .password("4321")
                .walletType(WalletType.CUSTOMER)
                .build();
        this.payee = createWalletCommand.execute(payeeDto);
        depositCommand.execute(new DepositDTO(payee.id(), BigDecimal.valueOf(25.00)));
    }

    @AfterEach
    public void tearDown() {
        mockServer.verify();
    }

    @Test
    public void shouldTransferWithValidData() throws Exception {
        AuthorizationOutputDto authDto = new AuthorizationOutputDto("success", new Data(true));
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(gatewayProperties.getAuthorization().getUrl())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(authDto), MediaType.APPLICATION_JSON));
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(gatewayProperties.getNotification().getUrl())))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));
        BigDecimal payerBalance = this.walletRepository.findById(this.payer.id()).orElseThrow().getBalance();
        BigDecimal payeeBalance = this.walletRepository.findById(this.payee.id()).orElseThrow().getBalance();
        BigDecimal amount = BigDecimal.valueOf(25.00);
        TransferDTO dto = new TransferDTO(payer.id(), payee.id(), amount);
        TransactionDTO output = transferCommand.execute(dto);
        Assertions.assertNotNull(output);
        Assertions.assertEquals(payer.id(), output.sourceWallet());
        Assertions.assertEquals(payee.id(), output.destinationWallet());
        Assertions.assertEquals("25.00", output.amount());
        Assertions.assertEquals("COMPLETED", output.status());
        Assertions.assertEquals("TRANSFER", output.transactionType());
        Assertions.assertNotNull(output.timestamp());
        Wallet payerWallet = this.walletRepository.findById(this.payer.id()).orElseThrow();
        Wallet payeeWallet = this.walletRepository.findById(this.payee.id()).orElseThrow();
        Assertions.assertEquals(0, payerBalance.subtract(amount).compareTo(payerWallet.getBalance()));
        Assertions.assertEquals(0, payeeBalance.add(amount).compareTo(payeeWallet.getBalance()));
    }

    @Test
    public void shouldNotTransferToNonExistingWallet() {
        TransferDTO dto = new TransferDTO(payer.id(), UUID.randomUUID(), BigDecimal.valueOf(25.00));
        Assertions.assertThrows(NotFoundException.class, () -> transferCommand.execute(dto));
    }

    @Test
    public void shouldNotTransferWithInvalidData() throws Exception {
        AuthorizationOutputDto authDto = new AuthorizationOutputDto("success", new Data(true));
        mockServer.expect(ExpectedCount.manyTimes(), requestTo(new URI(gatewayProperties.getAuthorization().getUrl())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(authDto), MediaType.APPLICATION_JSON));
        TransferDTO invalid1 = new TransferDTO(payer.id(), payee.id(), BigDecimal.valueOf(-25.00));
        Assertions.assertThrows(ValidationException.class, () -> transferCommand.execute(invalid1));
        TransferDTO invalid2 = new TransferDTO(payer.id(), payee.id(), BigDecimal.valueOf(0.00));
        Assertions.assertThrows(ValidationException.class, () -> transferCommand.execute(invalid2));
        TransferDTO invalid3 = new TransferDTO(payer.id(), payee.id(), null);
        Assertions.assertThrows(ValidationException.class, () -> transferCommand.execute(invalid3));
        TransferDTO invalid4 = new TransferDTO(null, payee.id(), BigDecimal.valueOf(10.00));
        Assertions.assertThrows(NotFoundException.class, () -> transferCommand.execute(invalid4));
        TransferDTO invalid5 = new TransferDTO(payer.id(), null, BigDecimal.valueOf(10.00));
        Assertions.assertThrows(NotFoundException.class, () -> transferCommand.execute(invalid5));
    }

    @Test
    public void shouldNotTransferWithInsufficientFunds() throws Exception {
        AuthorizationOutputDto authDto = new AuthorizationOutputDto("success", new Data(true));
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(gatewayProperties.getAuthorization().getUrl())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(authDto), MediaType.APPLICATION_JSON));
        TransferDTO dto = new TransferDTO(payer.id(), payee.id(), BigDecimal.valueOf(1000.00));
        Assertions.assertThrows(InsufficientBalanceException.class, () -> transferCommand.execute(dto));
    }

    @Test
    public void shouldThrowExceptionWhenNotAuthorized() throws Exception {
        AuthorizationOutputDto authDto = new AuthorizationOutputDto("fail", new Data(false));
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(gatewayProperties.getAuthorization().getUrl())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.FORBIDDEN).body(objectMapper.writeValueAsString(authDto)).contentType(MediaType.APPLICATION_JSON));
        BigDecimal amount = BigDecimal.valueOf(25.00);
        TransferDTO dto = new TransferDTO(payer.id(), payee.id(), amount);
        Assertions.assertThrows(OperationNotAuthorizedException.class, () -> transferCommand.execute(dto));
    }
}

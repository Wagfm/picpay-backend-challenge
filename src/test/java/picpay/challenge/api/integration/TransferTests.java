package picpay.challenge.api.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import picpay.challenge.api.application.exception.NotFoundException;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.application.usecase.CreateWallet;
import picpay.challenge.api.application.usecase.Deposit;
import picpay.challenge.api.application.usecase.ICommand;
import picpay.challenge.api.application.usecase.Transfer;
import picpay.challenge.api.application.usecase.dto.CreateWalletDTO;
import picpay.challenge.api.application.usecase.dto.DepositDTO;
import picpay.challenge.api.application.usecase.dto.TransferDTO;
import picpay.challenge.api.application.usecase.dto.TransferOutputDTO;
import picpay.challenge.api.application.usecase.dto.WalletDTO;
import picpay.challenge.api.domain.exception.InsufficientBalanceException;
import picpay.challenge.api.domain.exception.ValidationException;
import picpay.challenge.api.infra.spring.jpa.repository.IWalletJpaRepository;
import picpay.challenge.api.infra.spring.jpa.repository.WalletJpaRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Transactional
@DataJpaTest
public class TransferTests {
    private final ICommand<CreateWalletDTO, WalletDTO> createWalletCommand;
    private final ICommand<DepositDTO, WalletDTO> depositCommand;
    private final ICommand<TransferDTO, TransferOutputDTO> transferCommand;
    private WalletDTO payer;
    private WalletDTO payee;

    @Autowired
    public TransferTests(ICommand<CreateWalletDTO, WalletDTO> createWalletCommand, ICommand<DepositDTO, WalletDTO> depositCommand, ICommand<TransferDTO, TransferOutputDTO> transferCommand) {
        this.createWalletCommand = createWalletCommand;
        this.depositCommand = depositCommand;
        this.transferCommand = transferCommand;
    }

    @TestConfiguration
    public static class Config {
        @Bean
        public IWalletRepository walletRepository(IWalletJpaRepository walletJpaRepository) {
            return new WalletJpaRepository(walletJpaRepository);
        }

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

    @BeforeEach
    public void setUp() {
        CreateWalletDTO payerDto = CreateWalletDTO.builder()
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234")
                .build();
        this.payer = createWalletCommand.execute(payerDto);
        depositCommand.execute(new DepositDTO(payer.id(), BigDecimal.valueOf(50.00)));
        CreateWalletDTO payeeDto = CreateWalletDTO.builder()
                .fullName("Amanda Maciel")
                .cpfCnpj("09876543211")
                .email("amanda.maciel@email.com")
                .password("4321")
                .build();
        this.payee = createWalletCommand.execute(payeeDto);
        depositCommand.execute(new DepositDTO(payee.id(), BigDecimal.valueOf(25.00)));
    }

    @Test
    public void shouldTransferWithValidData() {
        TransferDTO dto = new TransferDTO(payer.id(), payee.id(), BigDecimal.valueOf(25.00));
        TransferOutputDTO output = transferCommand.execute(dto);
        Assertions.assertNotNull(output);
        Assertions.assertEquals("25.00", output.payer().balance());
        Assertions.assertEquals("50.00", output.payee().balance());
    }

    @Test
    public void shouldNotTransferToNonExistingWallet() {
        TransferDTO dto = new TransferDTO(payer.id(), UUID.randomUUID(), BigDecimal.valueOf(25.00));
        Assertions.assertThrows(NotFoundException.class, () -> transferCommand.execute(dto));
    }

    @Test
    public void shouldNotTransferWithInvalidData() {
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
    public void shouldNotTransferWithInsufficientFunds() {
        TransferDTO dto = new TransferDTO(payer.id(), payee.id(), BigDecimal.valueOf(1000.00));
        Assertions.assertThrows(InsufficientBalanceException.class, () -> transferCommand.execute(dto));
    }
}

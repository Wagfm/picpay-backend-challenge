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
import picpay.challenge.api.application.usecase.dto.CreateWalletDTO;
import picpay.challenge.api.application.usecase.dto.DepositDTO;
import picpay.challenge.api.application.usecase.dto.WalletDTO;
import picpay.challenge.api.domain.exception.ValidationException;
import picpay.challenge.api.infra.spring.jpa.repository.IWalletJpaRepository;
import picpay.challenge.api.infra.spring.jpa.repository.WalletJpaRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Transactional
@DataJpaTest
public class DepositTests {
    private final ICommand<CreateWalletDTO, WalletDTO> createWalletCommand;
    private final ICommand<DepositDTO, WalletDTO> depositCommand;
    private WalletDTO wallet;

    @Autowired
    public DepositTests(ICommand<CreateWalletDTO, WalletDTO> createWalletCommand, ICommand<DepositDTO, WalletDTO> depositCommand) {
        this.createWalletCommand = createWalletCommand;
        this.depositCommand = depositCommand;
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
    }

    @BeforeEach
    public void setUp() {
        CreateWalletDTO dto = CreateWalletDTO.builder()
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234")
                .build();
        this.wallet = createWalletCommand.execute(dto);
    }

    @Test
    public void shouldDepositWithValidData() {
        DepositDTO dto = new DepositDTO(wallet.id(), BigDecimal.valueOf(50.00));
        WalletDTO output = depositCommand.execute(dto);
        Assertions.assertEquals(dto.id(), output.id());
        Assertions.assertEquals(BigDecimal.valueOf(Double.parseDouble(output.balance())), BigDecimal.valueOf(50.00));
    }

    @Test
    public void shouldNotDepositWithInvalidData() {
        DepositDTO invalid1 = new DepositDTO(wallet.id(), BigDecimal.valueOf(-50.00));
        Assertions.assertThrows(ValidationException.class, () -> depositCommand.execute(invalid1));
        DepositDTO valid = new DepositDTO(wallet.id(), BigDecimal.valueOf(50.00));
        Assertions.assertDoesNotThrow(() -> depositCommand.execute(valid));
        DepositDTO invalid2 = new DepositDTO(wallet.id(), BigDecimal.valueOf(-25.00));
        Assertions.assertThrows(ValidationException.class, () -> depositCommand.execute(invalid2));
        DepositDTO invalid3 = new DepositDTO(wallet.id(), null);
        Assertions.assertThrows(ValidationException.class, () -> depositCommand.execute(invalid3));
        DepositDTO invalid4 = new DepositDTO(null, BigDecimal.valueOf(50.00));
        Assertions.assertThrows(NotFoundException.class, () -> depositCommand.execute(invalid4));
    }

    @Test
    public void shouldNotDepositToNonExistingWallet() {
        DepositDTO dto = new DepositDTO(UUID.randomUUID(), BigDecimal.valueOf(50.00));
        Assertions.assertThrows(NotFoundException.class, () -> depositCommand.execute(dto));
    }
}

package picpay.challenge.api.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import picpay.challenge.api.application.exception.ConflictException;
import picpay.challenge.api.application.repository.IWalletRepository;
import picpay.challenge.api.application.usecase.CreateWallet;
import picpay.challenge.api.application.usecase.ICommand;
import picpay.challenge.api.application.usecase.dto.CreateWalletDTO;
import picpay.challenge.api.application.usecase.dto.WalletDTO;
import picpay.challenge.api.domain.enums.WalletType;
import picpay.challenge.api.domain.exception.ValidationException;
import picpay.challenge.api.infra.spring.jpa.repository.IWalletJpaRepository;
import picpay.challenge.api.infra.spring.jpa.repository.WalletJpaRepository;

@Transactional
@DataJpaTest
public class CreateWalletTests {
    private final ICommand<CreateWalletDTO, WalletDTO> createWalletCommand;

    @Autowired
    public CreateWalletTests(ICommand<CreateWalletDTO, WalletDTO> createWalletCommand) {
        this.createWalletCommand = createWalletCommand;
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
    }

    @Test
    public void shouldCreateWalletWithValidData() {
        CreateWalletDTO dto = CreateWalletDTO.builder()
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234")
                .walletType(WalletType.CUSTOMER)
                .build();
        WalletDTO createdWallet = createWalletCommand.execute(dto);
        Assertions.assertNotNull(createdWallet);
    }

    @Test
    public void shouldNotCreateWalletWithInvalidData() {
        CreateWalletDTO dto = CreateWalletDTO.builder()
                .fullName("Wagner Maciel")
                .cpfCnpj("")
                .email("wagner.maciel@email.com")
                .password("1234")
                .walletType(WalletType.CUSTOMER)
                .build();
        Assertions.assertThrows(ValidationException.class, () -> createWalletCommand.execute(dto));
    }

    @Test
    public void shouldNotRegisterDuplicate() {
        CreateWalletDTO dto1 = CreateWalletDTO.builder()
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234")
                .walletType(WalletType.CUSTOMER)
                .build();
        WalletDTO createdWallet = createWalletCommand.execute(dto1);
        Assertions.assertNotNull(createdWallet);
        Assertions.assertThrows(ConflictException.class, () -> createWalletCommand.execute(dto1));
        CreateWalletDTO dto2 = CreateWalletDTO.builder()
                .fullName("Wagner F. Maciel")
                .cpfCnpj("09876543211")
                .email("wagner.maciel@email.com")
                .password("4321")
                .walletType(WalletType.CUSTOMER)
                .build();
        Assertions.assertThrows(ConflictException.class, () -> createWalletCommand.execute(dto2));
    }
}
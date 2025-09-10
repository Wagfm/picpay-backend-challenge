package picpay.challenge.api.domain.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.domain.exception.ValidationException;

import java.math.BigDecimal;

public class TransferServiceTests {
    @Test
    public void shouldTransferWithValidData() {
        Wallet payer = Wallet.builder()
                .id(1L)
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234")
                .build();
        Wallet payee = Wallet.builder()
                .id(2L)
                .fullName("Amanda Maciel")
                .cpfCnpj("09876543211")
                .email("amanda.maciel@email.com")
                .password("4321")
                .build();
        payer.deposit(BigDecimal.valueOf(50.00));
        payee.deposit(BigDecimal.valueOf(100.00));
        TransferService.transfer(payer, payee, BigDecimal.valueOf(50.00));
        Assertions.assertEquals(0, payer.getBalance().compareTo(BigDecimal.valueOf(0.00)));
        Assertions.assertEquals(0, payee.getBalance().compareTo(BigDecimal.valueOf(150.00)));
    }

    @Test
    public void shouldNotTransferToItself() {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234")
                .build();
        BigDecimal amount = BigDecimal.valueOf(50.00);
        Assertions.assertThrows(ValidationException.class, () -> TransferService.transfer(wallet, wallet, amount));
    }
}

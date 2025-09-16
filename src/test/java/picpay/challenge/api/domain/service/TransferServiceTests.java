package picpay.challenge.api.domain.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.domain.entity.WalletType;
import picpay.challenge.api.domain.exception.ValidationException;

import java.math.BigDecimal;

public class TransferServiceTests {
    @Test
    public void shouldTransferWithValidData() {
        Wallet payer = Wallet.builder()
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234")
                .walletType(WalletType.CUSTOMER)
                .build();
        Wallet payee = Wallet.builder()
                .fullName("Amanda Maciel")
                .cpfCnpj("09876543211")
                .email("amanda.maciel@email.com")
                .password("4321")
                .walletType(WalletType.CUSTOMER)
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
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234")
                .walletType(WalletType.CUSTOMER)
                .build();
        BigDecimal amount = BigDecimal.valueOf(50.00);
        Assertions.assertThrows(ValidationException.class, () -> TransferService.transfer(wallet, wallet, amount));
    }

    @Test
    public void shouldNotTransferFromMerchantWallet() {
        Wallet merchant = Wallet.builder()
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234")
                .walletType(WalletType.MERCHANT)
                .build();
        Wallet customer = Wallet.builder()
                .fullName("Amanda Maciel")
                .cpfCnpj("09876543211")
                .email("amanda.maciel@email.com")
                .password("4321")
                .walletType(WalletType.CUSTOMER)
                .build();
        merchant.deposit(BigDecimal.valueOf(50.00));
        customer.deposit(BigDecimal.valueOf(100.00));
        Assertions.assertThrows(ValidationException.class, () -> TransferService.transfer(merchant, customer, BigDecimal.valueOf(50.00)));
        Assertions.assertEquals(0, merchant.getBalance().compareTo(BigDecimal.valueOf(50.00)));
        Assertions.assertEquals(0, customer.getBalance().compareTo(BigDecimal.valueOf(100.00)));
        Assertions.assertDoesNotThrow(() -> TransferService.transfer(customer, merchant, BigDecimal.valueOf(50.00)));
        Assertions.assertEquals(0, merchant.getBalance().compareTo(BigDecimal.valueOf(100.00)));
        Assertions.assertEquals(0, customer.getBalance().compareTo(BigDecimal.valueOf(50.00)));
    }
}

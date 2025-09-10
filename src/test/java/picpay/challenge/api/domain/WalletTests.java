
package picpay.challenge.api.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import picpay.challenge.api.domain.entity.Wallet;
import picpay.challenge.api.domain.exception.ValidationException;

import java.util.List;

public class WalletTests {
    @Test
    public void shouldThrowValidationExceptionWithInvalidFullName() {
        List<String> invalidValues = List.of("", "   ");
        Wallet.Builder builder = Wallet.builder()
                .id(1L)
                .cpfCnpj("12345678900")
                .email("maciel@email.com")
                .password("1234");
        Assertions.assertThrows(ValidationException.class, builder::build);
        invalidValues.forEach(value -> {
            Assertions.assertThrows(ValidationException.class, () -> builder.fullName(value).build());
        });
    }

    @Test
    public void shouldThrowExceptionWithInvalidCpfCnpj() {
        List<String> invalidValues = List.of("", "   ", "123", "1234567890012345678900");
        Wallet.Builder builder = Wallet.builder()
                .id(1L)
                .fullName("Wagner Maciel")
                .email("maciel@email.com")
                .password("1234");
        Assertions.assertThrows(ValidationException.class, builder::build);
        invalidValues.forEach(value -> {
            Assertions.assertThrows(ValidationException.class, () -> builder.cpfCnpj(value).build());
        });
    }

    @Test
    public void shouldThrowExceptionWithInvalidEmail() {
        List<String> invalidValues = List.of("", "   ", "abc", "a".repeat(50) + "@email.com");
        Wallet.Builder builder = Wallet.builder()
                .id(1L)
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .password("1234");
        Assertions.assertThrows(ValidationException.class, builder::build);
        invalidValues.forEach(value -> {
            Assertions.assertThrows(ValidationException.class, () -> builder.email(value).build());
        });
    }

    @Test
    public void shouldThrowExceptionWithInvalidPassword(){
        List<String> invalidValues = List.of("", "   ");
        Wallet.Builder builder = Wallet.builder()
                .id(1L)
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com");
        Assertions.assertThrows(ValidationException.class, builder::build);
        invalidValues.forEach(value -> {
            Assertions.assertThrows(ValidationException.class, () -> builder.password(value).build());
        });
    }

    @Test
    public void shouldInstantiateWalletWithValidData() {
        Wallet.Builder builder = Wallet.builder()
                .id(1L)
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234");
        Assertions.assertDoesNotThrow(builder::build);
    }
}

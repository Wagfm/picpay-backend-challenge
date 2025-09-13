package picpay.challenge.api.infra.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import picpay.challenge.api.application.exception.ConflictException;
import picpay.challenge.api.application.usecase.dto.CreateWalletDTO;
import picpay.challenge.api.domain.exception.ValidationException;
import picpay.challenge.api.infra.spring.service.WalletService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
public class WalletControllerTests {
    private static final String BASE_URL = "/api/v0/wallets";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockitoBean
    private WalletService walletService;

    @Autowired
    public WalletControllerTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void shouldReturn422WhenCreatingWalletWithInvalidData() throws Exception {
        CreateWalletDTO invalidData = CreateWalletDTO.builder()
                .fullName("   ")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234")
                .build();
        when(walletService.createWallet(any(CreateWalletDTO.class))).thenThrow(new ValidationException("Name must not be empty"));
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldReturn409WhenCreatingDuplicateWallet() throws Exception {
        CreateWalletDTO duplicateWallet = CreateWalletDTO.builder()
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("1234")
                .build();
        when(walletService.createWallet(any(CreateWalletDTO.class))).thenThrow(new ConflictException("CPF/CNPJ already exists"));
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateWallet)))
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldReturn400WhenCreatingWalletWithWrongDataType() throws Exception {
        String invalidJson = "{\"fullName\": [], \"cpfCnpj\": \"12345678901\", \"email\": \"test@test.com\", \"password\": \"password\"}";
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}

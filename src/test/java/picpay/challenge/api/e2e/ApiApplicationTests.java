package picpay.challenge.api.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import picpay.challenge.api.application.usecase.dto.CreateWalletDTO;
import picpay.challenge.api.application.usecase.dto.TransferDTO;
import picpay.challenge.api.infra.spring.controller.dto.DepositRequestDTO;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiApplicationTests {
    private final String BASE_URL = "/api/v0/wallets";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public ApiApplicationTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void shouldRegisterWalletWithValidData() throws Exception {
        CreateWalletDTO payload = CreateWalletDTO.builder()
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("123")
                .build();
        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        String location = result.getResponse().getHeader("Location");
        Assertions.assertNotNull(location);
        Assertions.assertTrue(location.startsWith(BASE_URL));
    }

    @Test
    public void shouldDepositToExistingWallet() throws Exception {
        CreateWalletDTO payload = CreateWalletDTO.builder()
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("123")
                .build();
        MvcResult result = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        String location = result.getResponse().getHeader("Location");
        Assertions.assertNotNull(location);
        String id = location.substring(location.lastIndexOf("/") + 1);
        DepositRequestDTO depositPayload = new DepositRequestDTO(BigDecimal.valueOf(50.00));
        mockMvc.perform(patch(BASE_URL + "/deposit/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositPayload)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDepositToExistingWalletAndTransferFunds() throws Exception {
        CreateWalletDTO payloadWallet1 = CreateWalletDTO.builder()
                .fullName("Wagner Maciel")
                .cpfCnpj("12345678900")
                .email("wagner.maciel@email.com")
                .password("123")
                .build();
        MvcResult resultPost1 = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadWallet1)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        String location1 = resultPost1.getResponse().getHeader("Location");
        Assertions.assertNotNull(location1);
        String payerId = location1.substring(location1.lastIndexOf("/") + 1);
        CreateWalletDTO payloadWallet2 = CreateWalletDTO.builder()
                .fullName("Amanda Maciel")
                .cpfCnpj("09876543211")
                .email("amanda.maciel@email.com")
                .password("321")
                .build();
        MvcResult resultPost2 = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadWallet2)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        String location2 = resultPost2.getResponse().getHeader("Location");
        Assertions.assertNotNull(location2);
        String payeeId = location2.substring(location2.lastIndexOf("/") + 1);
        DepositRequestDTO depositPayload = new DepositRequestDTO(BigDecimal.valueOf(50.00));
        mockMvc.perform(patch(BASE_URL + "/deposit" + "/" + payerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositPayload)))
                .andExpect(status().isOk());
        TransferDTO transferDTO = new TransferDTO(UUID.fromString(payerId), UUID.fromString(payeeId), BigDecimal.valueOf(25.00));
        mockMvc.perform(patch(BASE_URL + "/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDTO)))
                .andExpect(status().isOk());
    }

}

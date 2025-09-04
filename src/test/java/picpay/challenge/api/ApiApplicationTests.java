package picpay.challenge.api;

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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
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
        CreateWalletDTO payload = new CreateWalletDTO("Wagner Maciel", "12345", "wagner.maciel@email.com", "123");
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
        CreateWalletDTO payload = new CreateWalletDTO("Wagner Maciel", "12345", "wagner.maciel@email.com", "123");
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
        String id = location.substring(location.lastIndexOf("/") + 1);
        DepositDTO depositPayload = new DepositDTO(BigDecimal.valueOf(50.00));
        mockMvc.perform(patch(BASE_URL + "/deposit/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositPayload)))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDepositToExistingWalletAndTransferFunds() throws Exception {
        CreateWalletDTO payloadWallet1 = new CreateWalletDTO("Wagner Maciel", "12345", "wagner.maciel@email.com", "123");
        MvcResult resultPost1 = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadWallet1)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        String location1 = resultPost1.getResponse().getHeader("Location");
        Assertions.assertNotNull(location1);
        Assertions.assertTrue(location1.startsWith(BASE_URL));
        String payerId = location1.substring(location1.lastIndexOf("/") + 1);
        CreateWalletDTO payloadWallet2 = new CreateWalletDTO("Amanda Maciel", "67890", "amanda.maciel@email.com", "321");
        MvcResult resultPost2 = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payloadWallet2)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        String location2 = resultPost2.getResponse().getHeader("Location");
        Assertions.assertNotNull(location2);
        Assertions.assertTrue(location2.startsWith(BASE_URL));
        String payeeId = location2.substring(location2.lastIndexOf("/") + 1);
        DepositDTO depositPayload = new DepositDTO(BigDecimal.valueOf(50.00));
        mockMvc.perform(patch(BASE_URL + "/deposit" + "/" + payerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositPayload)))
                .andExpect(status().isOk());
        TransferDTO transferDTO = new TransferDTO(Long.valueOf(payerId), Long.valueOf(payeeId), BigDecimal.valueOf(25.00));
        mockMvc.perform(patch(BASE_URL + "/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDTO)))
                .andExpect(status().isOk());
    }

}

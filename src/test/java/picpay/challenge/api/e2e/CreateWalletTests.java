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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreateWalletTests {
    private static final String BASE_URL = "/api/v0/wallets";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public CreateWalletTests(MockMvc mockMvc) {
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
}

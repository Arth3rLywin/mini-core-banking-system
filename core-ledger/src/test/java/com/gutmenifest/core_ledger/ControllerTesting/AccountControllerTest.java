package com.gutmenifest.core_ledger.ControllerTesting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gutmenifest.core_ledger.AccountDTO.AccountResponse;
import com.gutmenifest.core_ledger.AccountDTO.CreateAccountRequest;
import com.gutmenifest.core_ledger.Controller.AccountController;
import com.gutmenifest.core_ledger.Enum.AccountType;
import com.gutmenifest.core_ledger.Service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAccount_Success() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(1L);
        request.setAccountType(AccountType.SAVING);

        AccountResponse response = new AccountResponse();
        response.setId(1L);
        response.setAccountNumber("ACC123");
        response.setUserId(1L);
        response.setAccountType(AccountType.SAVING);
        response.setBalance(BigDecimal.ZERO);

        when(accountService.createAccount(any(CreateAccountRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("ACC123"))
                .andExpect(jsonPath("$.userId").value(1));
    }
}
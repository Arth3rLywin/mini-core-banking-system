package com.gutmenifest.core_ledger.ControllerTesting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gutmenifest.core_ledger.Controller.TransactionController;
import com.gutmenifest.core_ledger.Enum.TransactionStatus;
import com.gutmenifest.core_ledger.Enum.TransactionType;
import com.gutmenifest.core_ledger.Service.TransactionService;
import com.gutmenifest.core_ledger.TransactionDTO.DepositRequest;
import com.gutmenifest.core_ledger.TransactionDTO.TransactionResponse;
import com.gutmenifest.core_ledger.TransactionDTO.TransferRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void transfer_Success() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC001");
        request.setToAccountNumber("ACC002");
        request.setAmount(new BigDecimal("100.00"));

        TransactionResponse response = new TransactionResponse();
        response.setTransactionReference("TXN123");
        response.setAmount(new BigDecimal("100.00"));
        response.setType(TransactionType.TRANSFER);
        response.setStatus(TransactionStatus.SUCCESS);

        when(transactionService.transfer(any(TransferRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("TRANSFER"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void deposit_Success() throws Exception {
        DepositRequest request = new DepositRequest();
        request.setAccountNumber("ACC001");
        request.setAmount(new BigDecimal("50.00"));

        TransactionResponse response = new TransactionResponse();
        response.setType(TransactionType.DEPOSIT);
        response.setStatus(TransactionStatus.SUCCESS);
        response.setAmount(new BigDecimal("50.00"));

        when(transactionService.deposit(any(DepositRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("DEPOSIT"));
    }
}
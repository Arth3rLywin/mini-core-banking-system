package com.gutmenifest.core_ledger.UnitTesting;

import com.gutmenifest.core_ledger.AccountDTO.AccountResponse;
import com.gutmenifest.core_ledger.AccountDTO.CreateAccountRequest;
import com.gutmenifest.core_ledger.Entity.Account;
import com.gutmenifest.core_ledger.Enum.AccountType;
import com.gutmenifest.core_ledger.Repository.AccountRepository;
import com.gutmenifest.core_ledger.Service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccount_Success() {
        // Given
        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(1L);
        request.setAccountType(AccountType.SAVING);

        Account savedAccount = new Account("ACC1234567890", 1L, AccountType.SAVING);
        savedAccount.setId(1L);
        savedAccount.setBalance(BigDecimal.ZERO);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        // When
        AccountResponse response = accountService.createAccount(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(AccountType.SAVING, response.getAccountType());
        assertEquals(BigDecimal.ZERO, response.getBalance());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void getByAccountNumber_WhenAccountExists_ReturnAccount() {
        // Given
        Account account = new Account("ACC123", 1L, AccountType.SAVING);
        account.setId(1L);

        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));

        // When
        AccountResponse response = accountService.getByAccountNumber("ACC123");

        // Then
        assertNotNull(response);
        assertEquals("ACC123", response.getAccountNumber());
    }

    @Test
    void getByAccountNumber_WhenAccountNotFound_ThrowException() {
        // Given
        when(accountRepository.findByAccountNumber("INVALID")).thenReturn(Optional.empty());

        // When + Then
        assertThrows(RuntimeException.class, () -> {
            accountService.getByAccountNumber("INVALID");
        });
    }
}
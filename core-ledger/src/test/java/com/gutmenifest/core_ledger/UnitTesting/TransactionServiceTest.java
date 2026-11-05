package com.gutmenifest.core_ledger.UnitTesting;

import com.gutmenifest.core_ledger.Enum.AccountStatus;
import com.gutmenifest.core_ledger.Enum.TransactionStatus;
import com.gutmenifest.core_ledger.Enum.TransactionType;
import com.gutmenifest.core_ledger.Repository.TransactionRepository;
import com.gutmenifest.core_ledger.Service.TransactionService;
import com.gutmenifest.core_ledger.TransactionDTO.DepositRequest;
import com.gutmenifest.core_ledger.TransactionDTO.TransactionResponse;
import com.gutmenifest.core_ledger.TransactionDTO.TransferRequest;
import com.gutmenifest.core_ledger.TransactionDTO.WithdrawRequest;
import com.gutmenifest.core_ledger.Entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gutmenifest.core_ledger.Entity.Account;
import com.gutmenifest.core_ledger.Enum.AccountType;
import com.gutmenifest.core_ledger.Repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        fromAccount = new Account("ACC001", 1L, AccountType.SAVING);
        fromAccount.setId(1L);
        fromAccount.setBalance(new BigDecimal("1000.00"));
        fromAccount.setStatus(AccountStatus.ACTIVE);

        toAccount = new Account("ACC002", 2L, AccountType.SAVING);
        toAccount.setId(2L);
        toAccount.setBalance(new BigDecimal("500.00"));
        toAccount.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    void deposit_Success() {
        DepositRequest request = new DepositRequest();
        request.setAccountNumber("ACC001");
        request.setAmount(new BigDecimal("200.00"));

        when(accountRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.save(any())).thenReturn(fromAccount);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.deposit(request);

        assertNotNull(response);
        assertEquals(TransactionType.DEPOSIT, response.getType());
        assertEquals(TransactionStatus.SUCCESS, response.getStatus());
        assertEquals(new BigDecimal("1200.00"), fromAccount.getBalance());
    }

    @Test
    void withdraw_Success() {
        WithdrawRequest request = new WithdrawRequest();
        request.setAccountNumber("ACC001");
        request.setAmount(new BigDecimal("300.00"));

        when(accountRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(fromAccount);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionResponse response = transactionService.withdraw(request);

        assertEquals(TransactionType.WITHDRAW, response.getType());
        assertEquals(new BigDecimal("700.00"), fromAccount.getBalance());
    }

    @Test
    void withdraw_InsufficientBalance_ShouldThrow() {
        WithdrawRequest request = new WithdrawRequest();
        request.setAccountNumber("ACC001");
        request.setAmount(new BigDecimal("2000.00"));

        when(accountRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(fromAccount));

        assertThrows(RuntimeException.class, () -> transactionService.withdraw(request));
    }

    @Test
    void transfer_Success() {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC001");
        request.setToAccountNumber("ACC002");
        request.setAmount(new BigDecimal("400.00"));

        when(accountRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC002")).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.transfer(request);

        assertEquals(TransactionType.TRANSFER, response.getType());
        assertEquals(new BigDecimal("600.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("900.00"), toAccount.getBalance());
    }

    @Test
    void transfer_InsufficientBalance_ShouldThrow() {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC001");
        request.setToAccountNumber("ACC002");
        request.setAmount(new BigDecimal("1500.00"));

        when(accountRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC002")).thenReturn(Optional.of(toAccount));

        assertThrows(RuntimeException.class, () -> transactionService.transfer(request));
    }
}

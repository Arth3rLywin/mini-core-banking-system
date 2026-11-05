package com.gutmenifest.core_ledger.Service;

import com.gutmenifest.core_ledger.CustomException.BusinessException;
import com.gutmenifest.core_ledger.CustomException.ResourceNotFoundException;
import com.gutmenifest.core_ledger.Entity.Account;
import com.gutmenifest.core_ledger.Entity.Transaction;
import com.gutmenifest.core_ledger.Enum.AccountStatus;
import com.gutmenifest.core_ledger.Enum.TransactionStatus;
import com.gutmenifest.core_ledger.Enum.TransactionType;
import com.gutmenifest.core_ledger.Repository.AccountRepository;
import com.gutmenifest.core_ledger.Repository.TransactionRepository;
import com.gutmenifest.core_ledger.TransactionDTO.DepositRequest;
import com.gutmenifest.core_ledger.TransactionDTO.TransactionResponse;
import com.gutmenifest.core_ledger.TransactionDTO.TransferRequest;
import com.gutmenifest.core_ledger.TransactionDTO.WithdrawRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse deposit(DepositRequest request) {
        Account account = getActiveAccount(request.getAccountNumber());

        account.setBalance(account.getBalance().add(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction(
                generateTransactionReference(),
                null,
                account.getId(),
                request.getAmount(),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCESS,
                request.getDescription()
        );

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    @Transactional
    public TransactionResponse withdraw (WithdrawRequest request) {

        Account account = getActiveAccount(request.getAccountNumber());

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction(
                generateTransactionReference(),
                account.getId(),
                null,
                request.getAmount(),
                TransactionType.WITHDRAW,
                TransactionStatus.SUCCESS,
                request.getDescription()
        );

        Transaction saved = transactionRepository.save(transaction);

        return mapToResponse(saved);
    }

    @Transactional
    public TransactionResponse transfer (TransferRequest request) {
        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        Account fromAccount = getActiveAccount(request.getFromAccountNumber());
        Account toAccount = getActiveAccount(request.getToAccountNumber());

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient balance");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction(
                generateTransactionReference(),
                fromAccount.getId(),
                toAccount.getId(),
                request.getAmount(),
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS,
                request.getDescription()
        );

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    public TransactionResponse getByReference(String reference) {
        Transaction transaction = transactionRepository.findByTransactionReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found" + reference));
        return mapToResponse(transaction);
    }

    public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Account getActiveAccount (String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ResourceNotFoundException("Account is not active!");
        }
        return account;
    }

    public String generateTransactionReference() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setTransactionReference(transaction.getTransactionReference());
        response.setFromAccountId(transaction.getFromAccountId());
        response.setToAccountId(transaction.getToAccountId());
        response.setAmount(transaction.getAmount());
        response.setType(transaction.getType());
        response.setStatus(transaction.getStatus());
        response.setDescription(transaction.getDescription());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }
}

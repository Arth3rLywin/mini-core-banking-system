package com.gutmenifest.core_ledger.Service;

import com.gutmenifest.core_ledger.AccountDTO.AccountResponse;
import com.gutmenifest.core_ledger.AccountDTO.CreateAccountRequest;
import com.gutmenifest.core_ledger.CustomException.ResourceNotFoundException;
import com.gutmenifest.core_ledger.Entity.Account;
import com.gutmenifest.core_ledger.Enum.AccountStatus;
import com.gutmenifest.core_ledger.Repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;


    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponse createAccount (CreateAccountRequest request) {
        String accountNumber = generateAccountNumber();

        Account account = new Account(
                accountNumber,
                request.getUserId(),
                request.getAccountType()
        );

        Account saved = accountRepository.save(account);
        return mapToResponse(account);
    }

    public AccountResponse getByAccountNumber (String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account Not Found." + accountNumber));
        return mapToResponse(account);
    }

    public List<AccountResponse> getAccountByUserId (long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponse freezeAccount (String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account Not Found" + accountNumber));

        account.setStatus(AccountStatus.FROZEN);
        return mapToResponse(accountRepository.save(account));
    }

    public AccountResponse unfreezeAccount (String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account Not Found" + accountNumber));

        account.setStatus(AccountStatus.ACTIVE);
        return mapToResponse(accountRepository.save(account));
    }

    private String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private AccountResponse mapToResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setUserId(account.getUserId());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance());
        response.setStatus(account.getStatus());
        response.setCreatedAt(account.getCreatedAt());
        return response;
    }

}

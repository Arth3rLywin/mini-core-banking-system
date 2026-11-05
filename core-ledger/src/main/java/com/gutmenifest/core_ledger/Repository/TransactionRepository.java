package com.gutmenifest.core_ledger.Repository;

import com.gutmenifest.core_ledger.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionReference (String transactionReference);

    List<Transaction> findByFromAccountIdOrToAccountId (Long fromAccountId, Long toAccountId);
}

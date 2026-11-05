package com.gutmenifest.core_ledger.Repository;

import com.gutmenifest.core_ledger.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditLogRepository extends JpaRepository <Account, Long> {

}

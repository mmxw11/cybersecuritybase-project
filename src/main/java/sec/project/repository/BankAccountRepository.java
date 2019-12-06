package sec.project.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import sec.project.domain.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    /*
     * BankAccount findByAccountNumber(String accountNumber);
     * 
     * BankAccount findByAccountNumberIgnoreCase(String accountNumber);
     */
}
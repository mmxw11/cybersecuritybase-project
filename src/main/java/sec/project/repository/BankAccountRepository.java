package sec.project.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import sec.project.domain.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    
    List<BankAccount> findAllByIdNot(UUID id);
}
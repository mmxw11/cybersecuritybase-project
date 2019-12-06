package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sec.project.domain.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}
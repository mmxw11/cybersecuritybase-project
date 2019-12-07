package sec.project.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import sec.project.domain.BankAccount;
import sec.project.domain.TransactionRecord;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {

    List<TransactionRecord> findAllBySourceOrTarget(BankAccount source, BankAccount target, Sort sort);
}
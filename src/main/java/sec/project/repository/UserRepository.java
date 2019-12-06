package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sec.project.domain.BankUser;

public interface UserRepository extends JpaRepository<BankUser, Long> {

    BankUser findByUsername(String username);
}
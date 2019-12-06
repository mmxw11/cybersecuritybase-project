package sec.project.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sec.project.domain.BankAccount;
import sec.project.domain.BankUser;
import sec.project.repository.BankAccountRepository;
import sec.project.repository.UserRepository;

@Service
public class BankService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;

    public BankAccount createBankAccount(String iban, long ownerId) {
        BankUser owner = userRepository.findById(ownerId).orElse(null);
        if (owner == null) {
            throw new NullPointerException("owner");
        }
        BankAccount bankAccount = new BankAccount(iban, owner, 100D);
        return bankAccountRepository.save(bankAccount);
    }

    @Transactional
    public void transferFunds(UUID idTo, UUID idFrom, double amount, String message) {
        BankAccount toAccount = bankAccountRepository.findById(idTo).orElse(null);
        BankAccount fromAccount = bankAccountRepository.findById(idFrom).orElse(null);
        if (toAccount == null || fromAccount == null) {
            throw new NullPointerException("Bank account not found");
        }
        System.out.println("transfer message: " + message);
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
    }
}
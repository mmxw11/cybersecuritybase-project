package sec.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sec.project.domain.BankAccount;
import sec.project.domain.BankUser;
import sec.project.domain.TransactionRecord;
import sec.project.repository.BankAccountRepository;
import sec.project.repository.TransactionRecordRepository;
import sec.project.repository.UserRepository;

@Service
public class BankService {

    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private TransactionRecordRepository trecordRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;

    @Transactional
    public BankAccount createBankAccount(String iban, double balance, long ownerId) {
        BankUser owner = userRepository.findById(ownerId).orElse(null);
        if (owner == null) {
            throw new NullPointerException("owner");
        }
        BankAccount bankAccount = bankAccountRepository.save(new BankAccount(iban, owner, balance));
      //  String actionMessage = "A new bank account created with the balance " + balance + "€.";
      //  TransactionRecord record = new TransactionRecord(null, bankAccount, balance, actionMessage, "", LocalDateTime.now());
      //  trecordRepository.save(record);
        return bankAccount;
    }

    @Transactional
    public void transferFunds(UUID idTo, UUID idFrom, double amount, String message) {
        BankAccount toAccount = bankAccountRepository.findById(idTo).orElse(null);
        BankAccount fromAccount = bankAccountRepository.findById(idFrom).orElse(null);
        if (toAccount == null || fromAccount == null) {
            throw new NullPointerException("Bank account not found");
        }
        String actionMessage = authService.getAuthenticatedUser().getUsername() + " transfered " + amount + "€ from " + fromAccount.getIban() + " to " + toAccount.getIban();
        TransactionRecord record = new TransactionRecord(fromAccount, toAccount, amount, actionMessage, message, LocalDateTime.now());
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
        trecordRepository.save(record);
    }

    public List<TransactionRecord> getBankAccountTransactionHistory(BankAccount bankAccount) {
        return trecordRepository.findAllBySourceOrTarget(bankAccount, bankAccount, Sort.by("date").descending());
    }
}
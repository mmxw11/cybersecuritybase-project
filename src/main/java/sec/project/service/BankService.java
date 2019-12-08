package sec.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sec.project.domain.BankAccount;
import sec.project.domain.BankUser;
import sec.project.domain.TransactionRecord;
import sec.project.repository.BankAccountRepository;
import sec.project.repository.TransactionRecordRepository;
import sec.project.repository.UserRepository;

/**
 * This service is responsible for handling all the banking actions.
 */
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

    public BankAccount createBankAccount(String iban, String ownerUsername, double balance, RedirectAttributes rdAttributes) {
        BankUser owner = userRepository.findByUsername(ownerUsername);
        if (owner == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (bankAccountRepository.findByIbanIgnoreCase(iban) != null) {
            rdAttributes.addAttribute("bankAccountCreateFail", true);
            rdAttributes.addFlashAttribute("bankAccountCreateFailIban", "IBAN is already used!");
            return new BankAccount(null, owner, balance);
        }
        return bankAccountRepository.save(new BankAccount(iban, owner, balance));
    }

    public BankAccount updateBalance(UUID bankAccountId, double balance) {
        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId).orElse(null);
        if (bankAccount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank Account not found");
        }
        bankAccount.setBalance(balance);
        return bankAccountRepository.save(bankAccount);
    }

    @Transactional
    public void transferFunds(UUID idTo, UUID idFrom, double amount, String message, RedirectAttributes rdAttributes) {
        BankAccount toAccount = bankAccountRepository.findById(idTo).orElse(null);
        BankAccount fromAccount = bankAccountRepository.findById(idFrom).orElse(null);
        if (toAccount == null || fromAccount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank Account not found");
        }
        checBankAccountAccessPermission(fromAccount);
        if (fromAccount.getBalance() - amount < 0) {
            rdAttributes.addAttribute("transferFail", true);
            rdAttributes.addFlashAttribute("transferFailAmount", "You don't have enough money!");
            return;
        }
        String actionMessage = authService.getAuthenticatedUser().getUsername() + " transfered " + amount + "€ from " + fromAccount.getIban() + " ("
                + fromAccount.getOwner().getUsername() + ") to " + toAccount.getIban() + " (" + toAccount.getOwner().getUsername() + ")";
        trecordRepository.save(new TransactionRecord(fromAccount, toAccount, amount, actionMessage, message, LocalDateTime.now()));
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
    }

    public void checBankAccountAccessPermission(BankAccount bankAccount) {
        // Check if admin or the user is also the owner of this bank account.
        if (!authService.isAuthenticatedUserAdmin() && !bankAccount.getOwner().equals(authService.getAuthenticatedBankUser())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    public List<TransactionRecord> getBankAccountTransactionHistory(BankAccount bankAccount) {
        return trecordRepository.findAllBySourceOrTarget(bankAccount, bankAccount, Sort.by("date").descending());
    }
}
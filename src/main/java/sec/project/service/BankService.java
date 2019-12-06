package sec.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sec.project.domain.Account;
import sec.project.domain.BankAccount;
import sec.project.repository.AccountRepository;
import sec.project.repository.BankAccountRepository;

@Service
public class BankService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;

    public BankAccount createBankAccount(String accountNumber, long ownerId) {
        Account account = accountRepository.findOne(ownerId);
        if (account == null) {
            throw new NullPointerException("account");
        }
        BankAccount bankAccount = new BankAccount(accountNumber, account, 100D);
        return bankAccountRepository.save(bankAccount);
    }
}

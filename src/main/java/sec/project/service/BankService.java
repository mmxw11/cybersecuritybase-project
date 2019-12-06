package sec.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public BankAccount createBankAccount(String accountNumber, long ownerId) {
        BankUser owner = userRepository.findOne(ownerId);
        if (owner == null) {
            throw new NullPointerException("owner");
        }
        BankAccount bankAccount = new BankAccount(accountNumber, owner, 100D);
        return bankAccountRepository.save(bankAccount);
    }
}

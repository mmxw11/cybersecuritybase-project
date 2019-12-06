package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sec.project.domain.BankAccount;
import sec.project.repository.BankAccountRepository;
import sec.project.service.AuthService;
import sec.project.service.BankService;

@Controller
public class BankAccountController {

    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private BankService bankService;

    @RequestMapping(value = "/createbankaccount", method = RequestMethod.POST)
    public String createBankAccount(@RequestParam String accountNumber, @RequestParam long owner) {
        BankAccount bankAccount = bankService.createBankAccount(accountNumber, owner);
        return "redirect:/bankaccount/" + bankAccount.getAccountNumber();
    }

    @GetMapping("/bankaccount/{accountNumber}")
    public String viewBankAccount(@PathVariable String accountNumber, Model model) {
        BankAccount bankAccount = bankAccountRepository.findByAccountNumberIgnoreCase(accountNumber);
        model.addAttribute("auser", authService.getAuthenticatedUser());
        model.addAttribute("bankAccount", bankAccount);
        return "bankaccount";
    }
}

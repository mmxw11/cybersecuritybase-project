package sec.project.controller;

import java.util.UUID;

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

    @RequestMapping(value = "/createbankaccount", method = RequestMethod.POST) // TODO: UPDATE
    public String createBankAccount(@RequestParam String accountNumber, @RequestParam long owner) {
        if (accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account Number cannot be empty!");
        }
        BankAccount bankAccount = bankService.createBankAccount(accountNumber, owner);
        return "redirect:/bankaccount/" + bankAccount.getId();
    }

    @RequestMapping(value = "/transferfunds", method = RequestMethod.POST)
    public String transferFunds(@RequestParam UUID to, @RequestParam UUID from, @RequestParam double amount, @RequestParam String message) {
        bankService.transferFunds(to, from, amount, message);
        return "redirect:/bankaccount/" + from;
    }

    @GetMapping("/bankaccount/{uuid}")
    public String viewBankAccount(@PathVariable UUID uuid, Model model) {
        BankAccount bankAccount = bankAccountRepository.findById(uuid).orElse(null);
        if (bankAccount == null) {
            throw new NullPointerException("Bank Account not found");
        }
        model.addAttribute("auser", authService.getAuthenticatedUser());
        model.addAttribute("bankAccount", bankAccount);
        model.addAttribute("bankAccounts", bankAccountRepository.findAll());
        return "bankaccount";
    }
}

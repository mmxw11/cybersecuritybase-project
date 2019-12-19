package sec.project.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sec.project.domain.BankAccount;
import sec.project.repository.BankAccountRepository;
import sec.project.service.AuthService;
import sec.project.service.BankService;

/**
 * This controller handles all banking account related requests. (fund transfers etc)
 */
@Controller
public class BankAccountController {

    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private BankService bankService;

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/createbankaccount", method = RequestMethod.POST)
    public String createBankAccount(@RequestParam String iban, @RequestParam double balance, @RequestParam String ownerUsername, RedirectAttributes rdAttributes) {
        if (iban.trim().isEmpty() || ownerUsername.isEmpty()) {
            throw new IllegalArgumentException("IBAN or owner cannot be empty!");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative!");
        }
        BankAccount bankAccount = bankService.createBankAccount(iban, ownerUsername, balance, rdAttributes);
        if (bankAccount.isNew()) {
            return "redirect:/user/" + bankAccount.getOwner().getUsername();
        }
        return "redirect:/bankaccount/" + bankAccount.getId();
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/updatebankaccountbalance", method = RequestMethod.POST)
    public String updateBalance(@RequestParam UUID bankAccountId, @RequestParam double balance) {
        BankAccount bankAccount = bankService.updateBalance(bankAccountId, balance);
        return "redirect:/bankaccount/" + bankAccount.getId();
    }

    @RequestMapping(value = "/transferfunds", method = RequestMethod.POST)
    public String transferFunds(@RequestParam UUID idTo, @RequestParam UUID idFrom, @RequestParam double amount, @RequestParam String message,
            RedirectAttributes rdAttributes) {
        if (amount <= 0) {
            rdAttributes.addAttribute("transferFail", true);
            rdAttributes.addFlashAttribute("transferFailAmount", "Amount must be more than 0!");
        } else {
            bankService.transferFunds(idTo, idFrom, amount, message, rdAttributes);
        }
        return "redirect:/bankaccount/" + idFrom;
    }

    @GetMapping("/bankaccount/{id}")
    public String viewBankAccount(@PathVariable UUID id, Model model) {
        BankAccount bankAccount = bankAccountRepository.findById(id).orElse(null);
        if (bankAccount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank Account not found");
        }
        // bankService.checBankAccountAccessPermission(bankAccount);
        model.addAttribute("auser", authService.getAuthenticatedUser());
        model.addAttribute("bankAccount", bankAccount);
        model.addAttribute("transactionHistory", bankService.getBankAccountTransactionHistory(bankAccount));
        model.addAttribute("bankAccounts", bankAccountRepository.findAllByIdNot(bankAccount.getId()));
        return "bankaccount";
    }
}

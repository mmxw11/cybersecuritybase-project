package sec.project.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import sec.project.domain.Account;
import sec.project.repository.AccountRepository;
import sec.project.service.AccountService;

@Controller
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;

    @PostConstruct
    public void init() {}

    @GetMapping("/")
    public String index() {
        return "redirect:/account/" + accountService.getAuthenticatedUser().getUsername();
    }

    @GetMapping("/account/{username}")
    public String index(@PathVariable String username, Model model) {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new RuntimeException("Account not found!");
        }
        model.addAttribute("account", account);
        return "account";
    }
}
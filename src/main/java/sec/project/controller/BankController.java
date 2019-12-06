package sec.project.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import sec.project.domain.Account;
import sec.project.repository.AccountRepository;

@Controller
public class BankController {

    @Autowired
    private AccountRepository accountRepository;

    @PostConstruct
    public void init() {}

    @RequestMapping("/")
    public String index() {
        return "redirect:/account/"+ getAuthenticatedUser().getUsername();
    }

    @RequestMapping("/account/{username}")
    public String index(@PathVariable String username, Model model) {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new RuntimeException("Account not found!");
        }
        model.addAttribute("account", account);
        return "account";
    }

    public boolean isUserAuthenticated() {
        return !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }

    public User getAuthenticatedUser() {
        if (!isUserAuthenticated()) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (User) principal;
    }
}
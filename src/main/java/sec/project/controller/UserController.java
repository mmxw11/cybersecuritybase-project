package sec.project.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import sec.project.domain.BankUser;
import sec.project.repository.UserRepository;
import sec.project.service.AuthService;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;

    @PostConstruct
    public void init() {}

    @GetMapping("/")
    public String index() {
        return "redirect:/user/" + authService.getAuthenticatedUser().getUsername();
    }

    @GetMapping("/user/{username}")
    public String viewUser(@PathVariable String username, Model model) {
        BankUser user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        model.addAttribute("auser", authService.getAuthenticatedUser());
        model.addAttribute("user", user);
        return "user";
    }
}
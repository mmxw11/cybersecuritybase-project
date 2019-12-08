package sec.project.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/")
    public String index(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/adminpanel";
        }
        return "redirect:/user/" + authService.getAuthenticatedUser().getUsername();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.username==#username")
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
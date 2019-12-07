package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import sec.project.repository.UserRepository;
import sec.project.service.AuthService;

@Controller
public class AdminPanelController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/adminpanel")
    public String adminPanel(Model model) {
        model.addAttribute("auser", authService.getAuthenticatedUser());
        model.addAttribute("registeredUsers", userRepository.findAll());
        return "adminpanel";
    }
}
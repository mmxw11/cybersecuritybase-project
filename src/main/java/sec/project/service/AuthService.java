package sec.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import sec.project.configuration.DbUserDetailsService;
import sec.project.domain.BankUser;
import sec.project.repository.UserRepository;

/**
 * This service provides access to the currently authenticated Spring user.
 * I.e. the user we logged in with. Created in {@link DbUserDetailsService#loadUserByUsername(String)}
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public boolean isUserAuthenticated() {
        return !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }

    public BankUser getAuthenticatedBankUser() {
        return userRepository.findByUsername(getAuthenticatedUser().getUsername());
    }

    public boolean isAuthenticatedUserAdmin() {
        return getAuthenticatedUser().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    public User getAuthenticatedUser() {
        if (!isUserAuthenticated()) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (User) principal;
    }
}
package sec.project.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import sec.project.configuration.DbUserDetailsService;

/**
 * This service provides access to the currently authenticated Spring user.
 * I.e. the user we logged in with. Created in {@link DbUserDetailsService#loadUserByUsername(String)}
 */
@Service
public class AuthService {

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
package sec.project.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sec.project.domain.BankUser;
import sec.project.repository.UserRepository;

/**
 * An unused secure implementation of {@link DbAuthenticationProvider}.
 */
@Service
public class DbUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BankUser bankUser = userRepository.findByUsername(username);
        if (bankUser == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (bankUser.getUsername().equals("admin")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return new User(username, bankUser.getPassword(), true, true, true, true, authorities);
    }
}
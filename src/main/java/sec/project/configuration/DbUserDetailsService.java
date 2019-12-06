package sec.project.configuration;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sec.project.domain.BankUser;
import sec.project.repository.UserRepository;

@Service
public class DbUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        userRepository.save(new BankUser("admin", passwordEncoder.encode("testi")));
        userRepository.save(new BankUser("ted", passwordEncoder.encode("testi")));
        userRepository.save(new BankUser("jack", passwordEncoder.encode("testi")));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BankUser bankUser = userRepository.findByUsername(username);
        if (bankUser == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }
        return new User(
                username,
                bankUser.getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
}
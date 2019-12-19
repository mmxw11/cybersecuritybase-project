package sec.project.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import sec.project.domain.BankAccount;
import sec.project.domain.BankUser;
import sec.project.repository.BankAccountRepository;
import sec.project.repository.UserRepository;

/**
 * THIS IS AN UNSAFE component that provides database authentication.
 * A secure version of can be found at {@link DbUserDetailsService}.
 */
@Component
public class DbAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;

    @PostConstruct
    public void init() {
        BankUser admin = new BankUser("admin", "admin");
        userRepository.save(admin);
        bankAccountRepository.save(new BankAccount("FI29 000 000 0001", admin, 62D));
        bankAccountRepository.save(new BankAccount("FI29 000 000 0002", admin, 22D));
        bankAccountRepository.save(new BankAccount("FI29 000 000 0003", admin, 928D));
        BankUser ted = new BankUser("Ted", "Ted123");
        userRepository.save(ted);
        bankAccountRepository.save(new BankAccount("FI29 0230 0301 000", ted, 4D));
        bankAccountRepository.save(new BankAccount("FI29 000 000 23", ted, .5D));
        BankUser jack = new BankUser("Jack", "password");
        userRepository.save(jack);
        bankAccountRepository.save(new BankAccount("FI29 0000 01 0022", jack, 10000D));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        List<BankUser> bankUsers = jdbcTemplate.query("SELECT * FROM bank_user WHERE username='" + username + "' AND password ='" + password + "'",
                new BeanPropertyRowMapper<>(BankUser.class));
        if (bankUsers.isEmpty()) {
            throw new UsernameNotFoundException("Wrong username or password");
        }
        BankUser bankUser = bankUsers.get(0);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (bankUser.getUsername().equals("admin")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        User springUser = new User(bankUser.getUsername(), bankUser.getPassword(), true, true, true, true, authorities);
        return new UsernamePasswordAuthenticationToken(springUser, springUser.getPassword(), authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
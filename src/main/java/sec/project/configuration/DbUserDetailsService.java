package sec.project.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sec.project.domain.BankAccount;
import sec.project.domain.BankUser;
import sec.project.repository.BankAccountRepository;
import sec.project.repository.UserRepository;

@Service
public class DbUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    /*@Autowired
    private PasswordEncoder passwordEncoder;*/

    @PostConstruct
    public void init() {
        BankUser admin = new BankUser("admin", /*passwordEncoder.encode(*/"testi");//);
        userRepository.save(admin);
        bankAccountRepository.save(new BankAccount("FI29 000 000 0001", admin, 62D));
        bankAccountRepository.save(new BankAccount("FI29 000 000 0002", admin, 22D));
        bankAccountRepository.save(new BankAccount("FI29 000 000 0003", admin, 928D));
        BankUser ted = new BankUser("ted", /*passwordEncoder.encode(*/"testi");//);
        userRepository.save(ted);
        bankAccountRepository.save(new BankAccount("FI29 0230 0301 000", ted, 4D));
        bankAccountRepository.save(new BankAccount("FI29 000 000 23", ted, 10000.23D));
        BankUser jack = new BankUser("jack", /*passwordEncoder.encode(*/"testi");//);
        userRepository.save(jack);
        bankAccountRepository.save(new BankAccount("FI29 0000 01 0022", jack, .54D));
    }

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
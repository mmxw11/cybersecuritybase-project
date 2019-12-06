package sec.project.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class BankUser extends AbstractPersistable<Long> {

    private String username;
    private String password;
    @OneToMany(mappedBy = "owner")
    private List<BankAccount> bankAccounts;

    public BankUser() {}

    public BankUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }
}
package sec.project.domain;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class BankAccount extends AbstractPersistable<UUID> {

    private String iban;
    @ManyToOne
    private BankUser owner;
    private Double balance;

    public BankAccount() {}

    public BankAccount(String iban, BankUser owner, Double balance) {
        this.iban = iban;
        this.owner = owner;
        this.balance = balance;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public BankUser getOwner() {
        return owner;
    }

    public void setOwner(BankUser owner) {
        this.owner = owner;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
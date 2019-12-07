package sec.project.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * This class is used to store transaction information
 * i.e. transfer funds from a bank account to another.
 */
@Entity
public class TransactionRecord extends AbstractPersistable<Long> {

    @ManyToOne
    private BankAccount source;
    @ManyToOne
    private BankAccount target;
    private Double amount;
    private String actionMessage;
    @Type(type = "org.hibernate.type.TextType")
    private String userMessage;
    private LocalDateTime date;

    public TransactionRecord() {}

    public TransactionRecord(BankAccount source, BankAccount target, Double amount, String actionMessage, String userMessage, LocalDateTime date) {
        this.source = source;
        this.target = target;
        this.amount = amount;
        this.actionMessage = actionMessage;
        this.userMessage = userMessage;
        this.date = date;
    }

    public BankAccount getSource() {
        return source;
    }

    public void setSource(BankAccount source) {
        this.source = source;
    }

    public BankAccount getTarget() {
        return target;
    }

    public void setTarget(BankAccount target) {
        this.target = target;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getActionMessage() {
        return actionMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setActionMessage(String actionMessage) {
        this.actionMessage = actionMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
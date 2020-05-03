package ovh.flw.quarkusfilters.model.filterjointable;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;

@Entity
@Table(name = "accounts")
public class Account extends PanacheEntity {

    @Column(name = "account_type")
    @Enumerated(EnumType.STRING)
    public AccountType type;
    public Double amount;
    public Double rate;
    @ManyToOne
    @JoinColumn(name = "client_id")
    public Client client;


    public Account amount(Double amount) {
        this.amount = amount;
        return this;
    }

    public Account type(AccountType type) {
        this.type = type;
        return this;
    }

    public Account rate(Double rate) {
        this.rate = rate;
        return this;
    }

}

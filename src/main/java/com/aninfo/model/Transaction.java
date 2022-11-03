package com.aninfo.model;

import org.springframework.context.annotation.Primary;

import javax.persistence.*;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long transactionId;

    private Long doerCbu;

    private Long amount;

    public Transaction(){}

    public Transaction(Long doerCbu, Long amount){
        this.doerCbu = doerCbu;
        this.amount = amount;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getAmount() {
        return amount;
    }

    public Long getDoerCbu() {
        return doerCbu;
    }
}

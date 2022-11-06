package com.aninfo.model;

import javax.persistence.*;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long transactionId;

    private Long accountCbu;

    private Double amount;

    public Transaction(){}

    public void getTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void asWithdrawal(){
        this.amount = - this.amount;
    }


    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getAccountCbu() {
        return accountCbu;
    }
}

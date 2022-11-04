package com.aninfo.model;

import javax.persistence.*;
import java.util.Optional;

@Entity
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long operationId;

    private Long accountCbu;

    private Double amount;

    public Operation(){}

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public Long getOperationId() {
        return operationId;
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

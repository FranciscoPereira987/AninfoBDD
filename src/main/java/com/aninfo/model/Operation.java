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

    public Operation(Long accountCbu, Double amount){
        this.accountCbu = accountCbu;
        this.amount = amount;
    }

    public static Optional<Operation> tryCreatePromo(Long accountCbu, Double amount){
        if (amount >= 2000) {
            Double promotional = amount * 0.1;

            if (promotional > 500) promotional = 500.00;

            return Optional.of(new Operation(accountCbu, promotional));
        }

        return Optional.empty();
    }

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

    public Long getAccountCbu() {
        return accountCbu;
    }
}

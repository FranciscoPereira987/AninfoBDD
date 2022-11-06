package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.WithdrawalNegativeSumException;
import com.aninfo.model.Transaction;
import com.aninfo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

        @Autowired
        private TransactionRepository operationsCollection;

        public Transaction createDeposit(Transaction transaction){
                transaction.setAmount(this.applyPromo(transaction.getAmount()));
                if (transaction.getAmount() <= 0){
                        throw new DepositNegativeSumException("Cannot deposit negative sum");
                }

                return executeTransaction(transaction);

        }

        public Double applyPromo(Double sum){
                if (sum >= 2000){
                        Double promotional = sum * 0.1;
                        if (promotional > 500) promotional = 500.00;
                        sum += promotional;
                }
                return sum;
        }



        public Transaction createWithdrawal(Transaction transaction){
                if (transaction.getAmount() <= 0){
                        throw new WithdrawalNegativeSumException("Cannot withdraw a negative ammount");
                }

                transaction.asWithdrawal();

                return executeTransaction(transaction);
        }

        public List<Transaction> getTransactionsFrom(Long cbu){
                return this.operationsCollection.findAllByAccountCbu(cbu);
        }


        public Optional<Transaction> getTransaction(Long transactionId){
                return this.operationsCollection.findById(transactionId);
        }


        private Transaction executeTransaction(Transaction transaction){

                return this.operationsCollection.save(transaction);
        }

        public void deleteTransaction(Long transactionId){

                operationsCollection.deleteById(transactionId);
        }

}

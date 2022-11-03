package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.WithdrawalNegativeSumException;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

        @Autowired
        private TransactionRepository transactionsCollection;

        public Transaction createDeposit(Transaction transaction, Account account){
                if (transaction.getAmount() <= 0){
                        throw new DepositNegativeSumException("Cannot deposit negative sum");
                }

                return executeTransaction(transaction, account);

        }

        public Transaction createWithdrawal(Transaction transaction, Account account){
                if (transaction.getAmount() <= 0){
                        throw new WithdrawalNegativeSumException("Cannot withdraw a negative ammount");
                }

                return executeTransaction(transaction, account);
        }

        public List<Transaction> getTransactions(){
                return this.transactionsCollection.findAll();
        }


        public Optional<Transaction> getTransaction(Long transactionId){
                return this.transactionsCollection.findById(transactionId);
        }


        private Transaction executeTransaction(Transaction transaction, Account account){
                Double accountBalance = account.getBalance();
                account.setBalance(accountBalance + transaction.getAmount());

                return this.transactionsCollection.save(transaction);
        }


}

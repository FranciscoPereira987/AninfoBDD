package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.InsufficientFundsException;
import com.aninfo.exceptions.InvalidTransactionTypeException;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Collection<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long cbu) {
        return accountRepository.findById(cbu);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public void deleteById(Long cbu) {
        accountRepository.deleteById(cbu);
    }

    @Transactional
    public Account withdraw(Long cbu, Double sum) {
        Account account = accountRepository.findAccountByCbu(cbu);

        if (account.getBalance() < sum) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        account.setBalance(account.getBalance() - sum);
        accountRepository.save(account);

        return account;
    }

    @Transactional
    public Account deposit(Long cbu, Double sum) {

        if (sum <= 0) {
            throw new DepositNegativeSumException("Cannot deposit negative sums");
        }

        Account account = accountRepository.findAccountByCbu(cbu);
        sum = transactionService.applyPromo(sum);
        account.setBalance(account.getBalance() + sum);
        accountRepository.save(account);

        return account;
    }


    @Transactional
    public Transaction createDeposit(Transaction transaction){
        Optional<Account> optionalAccount = accountRepository.findById(transaction.getAccountCbu());

        if(!optionalAccount.isPresent()){
            throw new InvalidTransactionTypeException("Invalid operation");
        }

        Double sum = transaction.getAmount();
        Transaction executedTransaction = transactionService.createDeposit(transaction);
        Account account = deposit(optionalAccount.get().getCbu(),  sum);

        return executedTransaction;
    }


    @Transactional
    public Transaction createWithdrawal(Transaction transaction){
        Optional<Account> optionalAccount = accountRepository.findById(transaction.getAccountCbu());

        if(!optionalAccount.isPresent()){
            throw new InvalidTransactionTypeException("Invalid operation");
        }
        Double sum = transaction.getAmount();
        Transaction executedTransaction = transactionService.createWithdrawal(transaction);
        Account account = withdraw(optionalAccount.get().getCbu(), sum);


        return executedTransaction;
    }

    public Collection<Transaction> getTransactionsFrom(Long cbu){
        return transactionService.getTransactionsFrom(cbu);
    }

    public Optional<Transaction> getTransaction(Long transactionId){
        return transactionService.getTransaction(transactionId);
    }

    public void deleteTransaction(Long transactionId){
        Transaction toBeDeleted = transactionService.getTransaction(transactionId).get();
        rollBackTransaction(toBeDeleted);
        transactionService.deleteTransaction(transactionId);

    }

    private void rollBackTransaction(Transaction transaction){
        Double rollbackSum = transaction.getAmount();
        Long rollbackAccount = transaction.getAccountCbu();

        Account account = accountRepository.findAccountByCbu(rollbackAccount);
        Double newBalance = account.getBalance() - rollbackSum;

        if (newBalance < 0){
            throw new InvalidTransactionTypeException("Cannot delete transaction");
        }

        account.setBalance(newBalance);
        accountRepository.save(account);
    }
}

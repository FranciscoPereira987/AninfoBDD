package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.InsufficientFundsException;
import com.aninfo.exceptions.InvalidTransactionTypeException;
import com.aninfo.model.Account;
import com.aninfo.model.Operation;
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
    private OperationService operationService;

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
        sum = operationService.applyPromo(sum);
        account.setBalance(account.getBalance() + sum);
        accountRepository.save(account);

        return account;
    }


    @Transactional
    public Operation createDeposit(Operation operation){
        Optional<Account> optionalAccount = accountRepository.findById(operation.getAccountCbu());

        if(!optionalAccount.isPresent()){
            throw new InvalidTransactionTypeException("Invalid operation");
        }

        Double sum = operation.getAmount();
        Operation executedOperation = operationService.createDeposit(operation);
        Account account = deposit(optionalAccount.get().getCbu(),  sum);

        return executedOperation;
    }


    @Transactional
    public Operation createWithdrawal(Operation operation){
        Optional<Account> optionalAccount = accountRepository.findById(operation.getAccountCbu());

        if(!optionalAccount.isPresent()){
            throw new InvalidTransactionTypeException("Invalid operation");
        }
        Double sum = operation.getAmount();
        Operation executedOperation = operationService.createWithdrawal(operation);
        Account account = withdraw(optionalAccount.get().getCbu(), sum);


        return executedOperation;
    }

    public Collection<Operation> getTransactionsFrom(Long cbu){
        return operationService.getTransactionsFrom(cbu);
    }

    public Optional<Operation> getTransaction(Long operationId){
        return operationService.getTransaction(operationId);
    }

    public void deleteTransaction(Long operationId){
        Operation toBeDeleted = operationService.getTransaction(operationId).get();
        rollBackOperation(toBeDeleted);
        operationService.deleteTransaction(operationId);

    }

    private void rollBackOperation(Operation operation){
        Double rollbackSum = operation.getAmount();
        Long rollbackAccount = operation.getAccountCbu();

        Account account = accountRepository.findAccountByCbu(rollbackAccount);
        Double newBalance = account.getBalance() - rollbackSum;

        if (newBalance < 0){
            throw new InvalidTransactionTypeException("Cannot delete transaction");
        }

        account.setBalance(newBalance);
        accountRepository.save(account);
    }
}

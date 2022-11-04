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
        account.setBalance(account.getBalance() + sum);
        accountRepository.save(account);

        return tryApplyPromo(account, sum);
    }

    @Transactional
    public Account tryApplyPromo(Account account, Double depositedSum){
        Optional<Operation> optionalPromoDeposit = Operation.tryCreatePromo(account.getCbu(), depositedSum);

        if (optionalPromoDeposit.isPresent()){
            Operation promoDeposit = optionalPromoDeposit.get();
            operationService.createDeposit(promoDeposit);
            account.setBalance(account.getBalance() + promoDeposit.getAmount());
            accountRepository.save(account);
        }

        return account;
    }

    @Transactional
    public Operation createDeposit(Operation operation){
        Optional<Account> optionalAccount = accountRepository.findById(operation.getAccountCbu());

        if(!optionalAccount.isPresent()){
            throw new InvalidTransactionTypeException("Invalid operation");
        }

        Operation executedOperation = operationService.createDeposit(operation);
        Account account = deposit(optionalAccount.get().getCbu(),  operation.getAmount());

        return executedOperation;
    }


    @Transactional
    public Operation createWithdrawal(Operation operation){
        Optional<Account> optionalAccount = accountRepository.findById(operation.getAccountCbu());

        if(!optionalAccount.isPresent()){
            throw new InvalidTransactionTypeException("Invalid operation");
        }

        Operation executedOperation = operationService.createWithdrawal(operation);
        Account account = withdraw(optionalAccount.get().getCbu(), operation.getAmount());


        return executedOperation;
    }

    public Collection<Operation> getTransactionsFrom(Long cbu){
        return operationService.getTransactionsFrom(cbu);
    }

    public Optional<Operation> getTransaction(Long operationId){
        return operationService.getTransaction(operationId);
    }




}

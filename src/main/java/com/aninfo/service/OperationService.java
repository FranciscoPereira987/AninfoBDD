package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.WithdrawalNegativeSumException;
import com.aninfo.model.Account;
import com.aninfo.model.Operation;
import com.aninfo.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transaction;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class OperationService {

        @Autowired
        private OperationRepository operationsCollection;

        public Operation createDeposit(Operation operation){
                operation.setAmount(this.applyPromo(operation.getAmount()));
                if (operation.getAmount() <= 0){
                        throw new DepositNegativeSumException("Cannot deposit negative sum");
                }

                return executeTransaction(operation);

        }

        public Double applyPromo(Double sum){
                if (sum >= 2000){
                        Double promotional = sum * 0.1;
                        if (promotional > 500) promotional = 500.00;
                        sum += promotional;
                }
                return sum;
        }



        public Operation createWithdrawal(Operation operation){
                if (operation.getAmount() <= 0){
                        throw new WithdrawalNegativeSumException("Cannot withdraw a negative ammount");
                }

                operation.asWithdrawal();

                return executeTransaction(operation);
        }

        public List<Operation> getTransactionsFrom(Long cbu){
                return this.operationsCollection.findAllByAccountCbu(cbu);
        }


        public Optional<Operation> getTransaction(Long operationId){
                return this.operationsCollection.findById(operationId);
        }


        private Operation executeTransaction(Operation operation){

                return this.operationsCollection.save(operation);
        }

        public void deleteTransaction(Long operationId){

                operationsCollection.deleteById(operationId);
        }

}

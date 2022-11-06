package com.aninfo.repository;

import com.aninfo.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    Transaction findOperationByTransactionId(Long operationId);
    List<Transaction> findAllByAccountCbu(Long accountCbu);

    @Override
    List<Transaction> findAll();

}

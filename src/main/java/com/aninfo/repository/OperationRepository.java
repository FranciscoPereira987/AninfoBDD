package com.aninfo.repository;

import com.aninfo.model.Operation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface OperationRepository extends CrudRepository<Operation, Long> {

    Operation findOperationByOperationId(Long operationId);
    List<Operation> findAllByAccountCbu(Long accountCbu);

    @Override
    List<Operation> findAll();

}

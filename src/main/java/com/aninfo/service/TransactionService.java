package com.aninfo.service;

import com.aninfo.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TransactionService {

        @Autowired
        private Collection<Transaction> transactionsCollection;


}

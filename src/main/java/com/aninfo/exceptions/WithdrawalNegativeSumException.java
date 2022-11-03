package com.aninfo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WithdrawalNegativeSumException extends RuntimeException{

    public WithdrawalNegativeSumException(String message){
        super(message);
    }
}

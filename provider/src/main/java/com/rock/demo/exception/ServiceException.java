package com.rock.demo.exception;

import lombok.Data;

@Data
public class ServiceException extends RuntimeException {

    private String errorMsg;

    public ServiceException(String errorMsg){
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public ServiceException(String errorMsg, Throwable cause){
        super(errorMsg, cause);
        this.errorMsg = errorMsg;
    }
}

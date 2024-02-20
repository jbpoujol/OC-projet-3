package com.openclassrooms.projet3.excepton;


public class CustomAlreadyExistsException extends RuntimeException {

    public CustomAlreadyExistsException(String message) {
        super(message);
    }

}

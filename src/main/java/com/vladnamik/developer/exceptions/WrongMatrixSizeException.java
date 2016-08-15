package com.vladnamik.developer.exceptions;

@SuppressWarnings("unused")
public class WrongMatrixSizeException extends RuntimeException {
    public WrongMatrixSizeException(String message) {
        super(message);
    }

    public WrongMatrixSizeException() {
        super();
    }

    public WrongMatrixSizeException(Exception e) {
        super(e);
    }
}

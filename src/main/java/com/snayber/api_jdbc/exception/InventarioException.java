package com.snayber.api_jdbc.exception;

public class InventarioException extends RuntimeException {
    public InventarioException(String message) {
        super(message);
    }

    public InventarioException(String message, Throwable cause) {
        super(message, cause);
    }
}
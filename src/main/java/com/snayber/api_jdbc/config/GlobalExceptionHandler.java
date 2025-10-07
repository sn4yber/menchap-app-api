package com.snayber.api_jdbc.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.snayber.api_jdbc.exception.InventarioException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InventarioException.class)
    public ResponseEntity<String> handleInventarioException(InventarioException ex) {
        logger.error("Error de inventario: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Error en la operación de inventario: " + ex.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex) {
        logger.error("Error de concurrencia: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Error de concurrencia: La operación no pudo completarse porque los datos fueron modificados por otro usuario. Por favor, intente nuevamente.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.error("Error inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor. Por favor, contacte al administrador.");
    }
}
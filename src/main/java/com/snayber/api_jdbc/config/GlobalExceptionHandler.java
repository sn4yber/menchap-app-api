package com.snayber.api_jdbc.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import com.snayber.api_jdbc.exception.InventarioException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InventarioException.class)
    public ResponseEntity<Map<String, Object>> handleInventarioException(InventarioException ex) {
        logger.error("Error de inventario: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Error de inventario");
        error.put("mensaje", ex.getMessage());
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex) {
        logger.error("Error de concurrencia: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Error de concurrencia");
        error.put("mensaje", "La operación no pudo completarse porque los datos fueron modificados por otro usuario. Por favor, intente nuevamente.");
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.error("Entidad no encontrada: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Recurso no encontrado");
        error.put("mensaje", ex.getMessage());
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
        logger.error("Registro no encontrado: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Registro no encontrado");
        error.put("mensaje", "El registro solicitado no existe");
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Violación de integridad de datos: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Error de integridad de datos");
        error.put("mensaje", "La operación viola las restricciones de integridad de datos. Verifique que no existan duplicados o referencias faltantes.");
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        logger.error("Error de validación: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Error de validación");
        error.put("mensaje", "Los datos proporcionados no son válidos");
        error.put("timestamp", System.currentTimeMillis());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        error.put("errores", fieldErrors);
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.error("Error de tipo de argumento: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Error de tipo de dato");
        error.put("mensaje", "El valor proporcionado no es del tipo correcto");
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        logger.error("Argumento inválido: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Argumento inválido");
        error.put("mensaje", ex.getMessage());
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointer(NullPointerException ex) {
        logger.error("Error de referencia nula: {}", ex.getMessage(), ex);
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Error interno");
        error.put("mensaje", "Se encontró un valor nulo inesperado. Por favor, verifique los datos enviados.");
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Error inesperado: {}", ex.getMessage(), ex);
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Error interno del servidor");
        error.put("mensaje", "Ha ocurrido un error inesperado. Por favor, contacte al administrador.");
        error.put("timestamp", System.currentTimeMillis());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
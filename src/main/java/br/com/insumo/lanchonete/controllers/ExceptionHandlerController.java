package br.com.insumo.lanchonete.controllers;

import br.com.insumo.lanchonete.dtos.ExceptionDto;
import br.com.insumo.lanchonete.exceptions.EntityNotExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionHandlerController {

	@ExceptionHandler(EntityNotExistsException.class)
	public ResponseEntity<ExceptionDto> handleEntityNotFound(EntityNotExistsException ex, HttpServletRequest request) {
		ExceptionDto dto = new ExceptionDto(
				HttpStatus.NOT_FOUND.value(),
				ex.getMessage(),
				LocalDateTime.now(),
				request != null ? request.getRequestURI() : null,
				ex.getClass().getSimpleName()
		);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto);
	}

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionDto> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        ExceptionDto dto = new ExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                "Resource not found"+(ex.getMessage() != null ? ": " + ex.getMessage() : ""),
                LocalDateTime.now(),
                request != null ? request.getRequestURI() : null,
                ex.getClass().getSimpleName()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(dto);
    }

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionDto> handleGenericException(Exception ex, HttpServletRequest request) {
		ExceptionDto dto = new ExceptionDto(
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ex.getMessage(),
				LocalDateTime.now(),
				request != null ? request.getRequestURI() : null,
				ex.getClass().getSimpleName()
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
	}

}

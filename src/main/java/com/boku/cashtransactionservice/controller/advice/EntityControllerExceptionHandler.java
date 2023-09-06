package com.boku.cashtransactionservice.controller.advice;


import com.boku.cashtransactionservice.service.exception.HttpParametersException;
import com.boku.cashtransactionservice.service.exception.InsufficientAccountBalanceException;
import com.boku.cashtransactionservice.service.exception.UserAccountNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps exceptions to HTTP codes
 */
@RestControllerAdvice
public class EntityControllerExceptionHandler {
	
  @ExceptionHandler(UserAccountNotFoundException.class)
  public ResponseEntity<Void> entityNotFound(UserAccountNotFoundException ex)
  {
      return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
  }
  
  @ExceptionHandler(HttpParametersException.class)
  public ResponseEntity<Void> httpParametersNotFound(HttpParametersException ex)
  {
      return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST); 
  }
  
  @ExceptionHandler(InsufficientAccountBalanceException.class)
  public ResponseEntity<Void>insufficientAccountBalance(InsufficientAccountBalanceException ex)
  {
      return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);  
  }
  
  

  
   
}

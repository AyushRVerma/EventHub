package com.eventbook.EventHub.controller;

import com.eventbook.EventHub.domain.DTOs.ErrorDto;
import com.eventbook.EventHub.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {




    @ExceptionHandler(TicketSoldOutException.class)
    public ResponseEntity<ErrorDto> handleTicketSoldOutException(
            TicketSoldOutException ex){
        log.error("Event TicketSoldOutException", ex);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError("No ticket available");
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(QrCodeNotFoundException.class)
    public ResponseEntity<ErrorDto> handleQrCodeNotFoundException(
            QrCodeNotFoundException ex){
        log.error("Event QrCodeNotFoundException", ex);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError("Unable to find QR Code");
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(QrCodeGenerationException.class)
    public ResponseEntity<ErrorDto> handleQrCodeGenerationException(
            QrCodeGenerationException ex){
        log.error("Event QrCodeGenerationException", ex);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError("Unable to genrate QR Code");
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EventUpdateException.class)
    public ResponseEntity<ErrorDto> handleEventUpdateException(
            EventUpdateException ex){
        log.error("Event Update exception", ex);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError("Unable to update event");
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(InvalidEventDatesException.class)
    public ResponseEntity<ErrorDto> invalidEventDatesException (
            InvalidEventDatesException ex){
        log.error("Invalid Event Dates", ex);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrganizerCannotPurchaseTicketException.class)
    public ResponseEntity<ErrorDto> organizerCannotPurchaseTicketException (
            OrganizerCannotPurchaseTicketException ex){
        log.error("Only Attendee Can Purchase", ex);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError(ex.getMessage());
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

     @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEventNotFoundException(
            EventNotFoundException ex){
        log.error("EventNotFound exception", ex);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError("Event not found");
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TicketTypeNotFoundException.class)
    public ResponseEntity<ErrorDto> handleTicketTypeNotFoundException(
            TicketTypeNotFoundException ex){
        log.error("TicketTypeNotFound exception", ex);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError("TicketType not found");
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorDto> handleTicketNotFoundException(
            TicketNotFoundException ex){
        log.error("TicketNotFound exception", ex);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError("Ticket not found");
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserNotFoundException(
            UserNotFoundException ex){
        log.error("UserNotFound exception", ex);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError("User not found");
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Caught MethodArgumentNotValidException", ex);
        ErrorDto errorDto = new ErrorDto();

       BindingResult bindingResult= ex.getBindingResult()  ;
       List<FieldError> fieldErrors = bindingResult.getFieldErrors() ;
       String errorMessage= fieldErrors.stream()
               .findFirst()
               .map( fieldError -> fieldError.getField()
                       +": "+fieldError.getDefaultMessage())
               .orElse("Validation error occurred");


        errorDto.setError(errorMessage);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleConstraintViolation(
            ConstraintViolationException ex){
        ErrorDto errorDto = new ErrorDto();

        String errorMessage= ex.getConstraintViolations()
                .stream()
                .findFirst()
                     .map(violation -> violation.getPropertyPath()
                +": "+violation.getMessage()
        ).orElse("Constraint violation occured");

        errorDto.setError(errorMessage);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        log.error("Caught exception", ex);
        ErrorDto errorDto = new ErrorDto();
        errorDto.setError("An unknown error occured");

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

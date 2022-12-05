package org.ttn.ecommerce.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<String> errors = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {

            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add("password" + ": " + error.getDefaultMessage());
        }

        Collections.sort(errors);
        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, "Validation Errors", errors);
        return handleExceptionInternal(ex, apiError, headers, apiError.getStatus(), request);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {


        Details errorDetail = new Details(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<Object>(errorDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }
/* Exception Handler for custom exception that I have created **/
  @ExceptionHandler(CategoryNotFoundException.class)
  public final ResponseEntity<Object> handleCategoryNotFoundException(Exception ex, WebRequest request){
    Details errorDetail = new Details(
        LocalDateTime.now(),
        ex.getMessage(),
        request.getDescription(false)
    );
    return new ResponseEntity<Object>(errorDetail, HttpStatus.NOT_FOUND);
  }

    @ExceptionHandler(NotFoundRequestException.class)
    public final ResponseEntity<Object> handleNotFoundRequestException(Exception ex, WebRequest request){
        Details errorDetail = new Details(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<Object>(errorDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<Object> handleResourceNotFoundException(Exception ex, WebRequest request){
        Details errorDetail = new Details(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<Object>(errorDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public final ResponseEntity<Object> handleTokenExpiredException(Exception ex, WebRequest request){
        Details errorDetail = new Details(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<Object>(errorDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public final ResponseEntity<Object> handleUserAlreadyExistsException(Exception ex, WebRequest request){
        Details errorDetail = new Details(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<Object>(errorDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotActiveException.class)
    public final ResponseEntity<Object> handleUserNotActiveException(Exception ex, WebRequest request){
        Details errorDetail = new Details(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<Object>(errorDetail, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(Exception ex, WebRequest request){
        Details errorDetail = new Details(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<Object>(errorDetail, HttpStatus.NOT_FOUND);
    }

//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//        String myMsg = "";
//        String errMsg = String.valueOf(ex.getFieldError());
//        if (errMsg == null) {
//            myMsg = ex.getMessage();
//        } else {
//            myMsg = ex.getFieldError().getDefaultMessage();
//        }
//        Details errorDetail = new Details(
//                LocalDateTime.now(),
//                myMsg,
//                request.getDescription(false)
//        );
//        return new ResponseEntity<Object>(errorDetail, HttpStatus.BAD_REQUEST);
//    }
}

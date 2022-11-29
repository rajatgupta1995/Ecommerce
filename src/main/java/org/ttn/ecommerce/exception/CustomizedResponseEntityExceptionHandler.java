//package org.ttn.ecommerce.exception;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//import java.time.LocalDateTime;
//
//@ControllerAdvice
//public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
//
//    @ExceptionHandler(Exception.class)
//    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
//
//
//        Details errorDetail = new Details(
//                LocalDateTime.now(),
//                ex.getMessage(),
//                request.getDescription(false)
//        );
//        return new ResponseEntity<Object>(errorDetail, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//
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
//}

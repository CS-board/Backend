package com.chip.board.global.base.exception;

import com.chip.board.global.base.dto.ResponseBody;
import com.chip.board.global.base.dto.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
//@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ResponseBody<Void>> handleServiceException(ServiceException e, HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        log.debug("ServiceException handled. method={}, uri={}, errorCode={}, status={}",
                request.getMethod(), request.getRequestURI(), errorCode.getCode(), errorCode.getStatus().value());
        return ResponseEntity.status(errorCode.getStatus())
                .body(ResponseUtils.createFailureResponse(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseBody<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                                   HttpServletRequest request){
        String customMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.debug("Validation failed. method={}, uri={}, errorCode={}",
                request.getMethod(), request.getRequestURI(), ErrorCode.BINDING_ERROR.getCode());
        return ResponseEntity
                .status(ErrorCode.BINDING_ERROR.getStatus())
                .body(ResponseUtils.createFailureResponse(ErrorCode.BINDING_ERROR, customMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseBody<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e,
                                                                                   HttpServletRequest request) {
        log.warn("Request body unreadable. method={}, uri={}, errorCode={}",
                request.getMethod(), request.getRequestURI(), ErrorCode.BINDING_ERROR.getCode(), e);
        return ResponseEntity
                .status(ErrorCode.BINDING_ERROR.getStatus())
                .body(ResponseUtils.createFailureResponse(ErrorCode.BINDING_ERROR));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseBody<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                                       HttpServletRequest request) {
        log.debug("HTTP method not supported. method={}, uri={}, errorCode={}",
                request.getMethod(), request.getRequestURI(), ErrorCode.INVALID_HTTP_METHOD.getCode());
        return ResponseEntity
                .status(ErrorCode.INVALID_HTTP_METHOD.getStatus())
                .body(ResponseUtils.createFailureResponse(ErrorCode.INVALID_HTTP_METHOD));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseBody<Void>> handleNotFound(NoResourceFoundException e, HttpServletRequest request) {
        log.debug("Endpoint not found. method={}, uri={}, errorCode={}",
                request.getMethod(), request.getRequestURI(), ErrorCode.INVALID_ENDPOINT.getCode());
        return ResponseEntity
                .status(ErrorCode.INVALID_ENDPOINT.getStatus())
                .body(ResponseUtils.createFailureResponse(ErrorCode.INVALID_ENDPOINT));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseBody<Void>> handleAccessDeniedException(AccessDeniedException e,
                                                                         HttpServletRequest request) {
        log.warn("Access denied. method={}, uri={}, errorCode={}",
                request.getMethod(), request.getRequestURI(), ErrorCode.ACCESS_DENIED.getCode());
        return ResponseEntity
                .status(ErrorCode.ACCESS_DENIED.getStatus())
                .body(ResponseUtils.createFailureResponse(ErrorCode.ACCESS_DENIED));
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ResponseBody<Void>> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException e,
                                                                                              HttpServletRequest request) {
        log.debug("Authentication credentials missing. method={}, uri={}, errorCode={}",
                request.getMethod(), request.getRequestURI(), ErrorCode.NEED_AUTHORIZED.getCode());
        return ResponseEntity
                .status(ErrorCode.NEED_AUTHORIZED.getStatus())
                .body(ResponseUtils.createFailureResponse(ErrorCode.NEED_AUTHORIZED));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseBody<Void>> handleException(Exception e, HttpServletRequest request){
        log.error("Unhandled exception. method={}, uri={}, errorCode={}",
                request.getMethod(), request.getRequestURI(), ErrorCode.UNEXPECTED_SERVER_ERROR.getCode(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtils.createFailureResponse(ErrorCode.UNEXPECTED_SERVER_ERROR));
    }
}

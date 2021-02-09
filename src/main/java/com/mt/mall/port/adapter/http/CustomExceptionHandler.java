package com.mt.mall.port.adapter.http;

import com.mt.common.logging.ErrorMessage;
import com.mt.mall.application.product.exception.AttributeNameNotFoundException;
import com.mt.mall.application.product.exception.NoLowestPriceFoundException;
import com.mt.mall.application.product.exception.SkuAlreadyExistException;
import com.mt.mall.application.product.exception.SkuNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.mt.common.CommonConstant.HTTP_HEADER_ERROR_ID;


@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            SkuAlreadyExistException.class,
            SkuNotExistException.class,
    })
    protected ResponseEntity<?> handle400Exception(RuntimeException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HTTP_HEADER_ERROR_ID, errorMessage.getErrorId());
        return handleExceptionInternal(ex, errorMessage, httpHeaders, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {
            NoLowestPriceFoundException.class,
            AttributeNameNotFoundException.class,
    })
    protected ResponseEntity<Object> handle500Exception(RuntimeException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HTTP_HEADER_ERROR_ID, errorMessage.getErrorId());
        return handleExceptionInternal(ex, errorMessage, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}

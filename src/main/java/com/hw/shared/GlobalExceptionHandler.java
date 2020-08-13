package com.hw.shared;

import com.hw.aggregate.product.exception.HangingTransactionException;
import com.hw.aggregate.product.exception.RollbackNotSupportedException;
import com.hw.shared.rest.exception.EntityPatchException;
import com.hw.shared.rest.exception.UnsupportedPatchOperationException;
import com.hw.shared.rest.exception.UpdateFiledValueException;
import com.hw.shared.rest.exception.EntityNotExistException;
import com.hw.shared.sql.exception.EmptyWhereClauseException;
import com.hw.shared.sql.exception.MaxPageSizeExceedException;
import com.hw.shared.sql.exception.PatchCommandExpectNotMatchException;
import com.hw.shared.sql.exception.UnsupportedQueryException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.hw.shared.AppConstant.HTTP_HEADER_ERROR_ID;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {
            BadRequestException.class,
            TransactionSystemException.class,
            IllegalArgumentException.class,
            DataIntegrityViolationException.class,
            ObjectOptimisticLockingFailureException.class,
            JwtTokenExtractException.class,
            UnsupportedQueryException.class,
            MaxPageSizeExceedException.class,
            EmptyWhereClauseException.class,
            UnsupportedPatchOperationException.class,
            UpdateFiledValueException.class,
            HangingTransactionException.class,
            RollbackNotSupportedException.class,
            PatchCommandExpectNotMatchException.class,
            EntityNotExistException.class,
            EntityPatchException.class,
    })
    protected ResponseEntity<Object> handle400Exception(RuntimeException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HTTP_HEADER_ERROR_ID, errorMessage.getErrorId());
        return handleExceptionInternal(ex, errorMessage, httpHeaders, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {
            InternalServerException.class,
            RuntimeException.class,
            JwtTokenRetrievalException.class,
            DeepCopyException.class
    })
    protected ResponseEntity<Object> handle500Exception(RuntimeException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(ex);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HTTP_HEADER_ERROR_ID, errorMessage.getErrorId());
        return handleExceptionInternal(ex, errorMessage, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}

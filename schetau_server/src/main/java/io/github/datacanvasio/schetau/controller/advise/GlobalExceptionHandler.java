/*
 * Copyright 2020 DataCanvas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.datacanvasio.schetau.controller.advise;

import io.github.datacanvasio.schetau.controller.NodeController;
import io.github.datacanvasio.schetau.exception.SchetauException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestControllerAdvice(basePackageClasses = {NodeController.class})
public class GlobalExceptionHandler {
    private static ResourceBundle errorMessages = null;

    private static void loadMessages() {
        if (errorMessages == null || !errorMessages.getLocale().equals(Locale.getDefault())) {
            errorMessages = ResourceBundle.getBundle("messages/error");
        }
    }

    @Nonnull
    private static SchetauResponse getApiResponse(String errorCode, @Nullable Object... args) {
        loadMessages();
        String message;
        try {
            message = String.format(errorMessages.getString(errorCode), args);
        } catch (MissingResourceException e) {
            message = "Unknown error!";
        }
        return new SchetauResponse(errorCode, message);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public SchetauResponse requestExceptionHandler(
        HttpServletRequest request,
        Exception exception,
        HttpServletResponse response
    ) {
        if (log.isErrorEnabled()) {
            log.error("Exception thrown: ", exception);
        }
        return getApiResponse("030301");
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public SchetauResponse validationExceptionHandler(
        HttpServletRequest request,
        Exception exception,
        HttpServletResponse response
    ) {
        if (log.isWarnEnabled()) {
            log.warn("Exception thrown: ", exception);
        }
        BindingResult result = ((MethodArgumentNotValidException) exception).getBindingResult();
        String message = result.getAllErrors().stream()
            .map(error -> {
                if (error instanceof FieldError) {
                    return "\"" + ((FieldError) error).getField() + ("\" ") + error.getDefaultMessage();
                } else {
                    return error.getObjectName() + error.getDefaultMessage();
                }
            })
            .collect(Collectors.joining(", "));
        return getApiResponse("030302", message);
    }

    @ExceptionHandler(value = {SchetauException.class})
    public SchetauResponse streamTauExceptionHandler(
        HttpServletRequest request,
        Exception exception,
        HttpServletResponse response
    ) {
        if (log.isErrorEnabled()) {
            log.error("Exception thrown: ", exception);
        }
        String errorCode = ((SchetauException) exception).getErrorCode();
        Object[] args = ((SchetauException) exception).getArgs();
        return getApiResponse(errorCode, args);
    }

    @ExceptionHandler(value = {Exception.class})
    public SchetauResponse globalExceptionHandler(
        HttpServletRequest request,
        Exception exception,
        HttpServletResponse response
    ) {
        if (log.isErrorEnabled()) {
            log.error("Exception thrown: ", exception);
        }
        return getApiResponse("030901");
    }
}

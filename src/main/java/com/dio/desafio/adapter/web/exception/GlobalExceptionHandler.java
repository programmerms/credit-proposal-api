package com.dio.desafio.adapter.web.exception;

import com.dio.desafio.application.exception.FieldValidationException;
import com.dio.desafio.application.exception.ProposalException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    /** Single place where business codes meet HTTP; the application layer knows nothing about status codes. */
    private static final Map<String, HttpStatus> BUSINESS_STATUS = Map.of(
            "PROPOSTA_NAO_ENCONTRADA", HttpStatus.NOT_FOUND,
            "CONTRATACAO_NAO_ENCONTRADA", HttpStatus.NOT_FOUND,
            "PRODUTO_NAO_ENCONTRADO", HttpStatus.NOT_FOUND,
            "PROPOSTA_JA_CONTRATADA", HttpStatus.CONFLICT,
            "PRODUTO_INCOMPATIVEL", HttpStatus.UNPROCESSABLE_ENTITY,
            "CATALOGO_INDISPONIVEL", HttpStatus.SERVICE_UNAVAILABLE);
    private static final String VALIDATION_MESSAGE = "Falha na validação da requisição";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        List<FieldErrorResponse> fields = exception.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError).toList();
        return response(HttpStatus.BAD_REQUEST, VALIDATION_MESSAGE, fields);
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleFieldValidation(FieldValidationException exception) {
        List<FieldErrorResponse> fields = exception.violations().stream()
                .map(v -> new FieldErrorResponse(v.field(), v.reason())).toList();
        return response(HttpStatus.BAD_REQUEST, VALIDATION_MESSAGE, fields);
    }

    @ExceptionHandler(ProposalException.class)
    public ResponseEntity<ApiErrorResponse> handleProposal(ProposalException exception) {
        return response(statusOf(exception.code()), exception.getMessage(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadable(HttpMessageNotReadableException exception) {
        return response(HttpStatus.BAD_REQUEST, "Corpo da requisição malformado ou ilegível", null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return response(HttpStatus.BAD_REQUEST, VALIDATION_MESSAGE,
                List.of(new FieldErrorResponse(exception.getName(), "valor inválido")));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        if (exception instanceof ErrorResponse framework) {
            HttpStatusCode code = framework.getStatusCode();
            HttpStatus status = HttpStatus.resolve(code.value());
            if (status != null && code.is4xxClientError()) {
                return response(status, frameworkMessage(status), null);
            }
        }
        LOG.error("Unexpected error handling {}", request.getRequestURI(), exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível processar a requisição", null);
    }

    private HttpStatus statusOf(String code) {
        HttpStatus status = BUSINESS_STATUS.get(code);
        if (status == null) {
            LOG.warn("Business code without HTTP mapping: {}", code);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return status;
    }

    private String frameworkMessage(HttpStatus status) {
        return switch (status) {
            case NOT_FOUND -> "Recurso não encontrado";
            case METHOD_NOT_ALLOWED -> "Método HTTP não permitido";
            case UNSUPPORTED_MEDIA_TYPE -> "Tipo de mídia não suportado";
            default -> "Requisição inválida";
        };
    }

    private FieldErrorResponse toFieldError(FieldError error) {
        String message = error.getDefaultMessage() == null ? "valor inválido" : error.getDefaultMessage();
        return new FieldErrorResponse(error.getField(), message);
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, String message,
                                                       List<FieldErrorResponse> fields) {
        ApiErrorResponse body = new ApiErrorResponse(Instant.now(), status.value(), message, fields);
        return ResponseEntity.status(status).body(body);
    }
}

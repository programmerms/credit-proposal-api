package com.dio.desafio.adapter.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        Instant dataHora,
        int status,
        String mensagem,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<FieldErrorResponse> errosCampos) { }

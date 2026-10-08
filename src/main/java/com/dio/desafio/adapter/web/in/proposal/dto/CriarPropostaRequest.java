package com.dio.desafio.adapter.web.in.proposal.dto;

import com.dio.desafio.adapter.web.validation.Mensagens;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CriarPropostaRequest(
        @NotBlank(message = Mensagens.OBRIGATORIO) String documento,
        @NotBlank(message = Mensagens.OBRIGATORIO) String produto,
        @NotNull(message = Mensagens.OBRIGATORIO)
        @DecimalMin(value = "0.0", inclusive = false, message = Mensagens.MAIOR_QUE_ZERO)
        @DecimalMax(value = "1000000000.00", message = Mensagens.VALOR_MAXIMO) BigDecimal valor,
        @NotNull(message = Mensagens.OBRIGATORIO)
        @Min(value = 1, message = Mensagens.PRAZO_MINIMO)
        @Max(value = 600, message = Mensagens.PRAZO_MAXIMO) Integer prazoMeses) { }

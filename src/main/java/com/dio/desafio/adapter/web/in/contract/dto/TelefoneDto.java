package com.dio.desafio.adapter.web.in.contract.dto;

import com.dio.desafio.adapter.web.validation.Mensagens;
import jakarta.validation.constraints.NotBlank;

public record TelefoneDto(
        @NotBlank(message = Mensagens.OBRIGATORIO) String tipo,
        @NotBlank(message = Mensagens.OBRIGATORIO) String ddd,
        @NotBlank(message = Mensagens.OBRIGATORIO) String numero) { }

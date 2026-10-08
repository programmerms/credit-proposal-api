package com.dio.desafio.adapter.web.in.contract.dto;

import com.dio.desafio.adapter.web.validation.Mensagens;
import jakarta.validation.constraints.NotBlank;

public record EnderecoDto(
        @NotBlank(message = Mensagens.OBRIGATORIO) String tipo,
        @NotBlank(message = Mensagens.OBRIGATORIO) String logradouro,
        @NotBlank(message = Mensagens.OBRIGATORIO) String numero,
        @NotBlank(message = Mensagens.OBRIGATORIO) String cep,
        @NotBlank(message = Mensagens.OBRIGATORIO) String cidade,
        @NotBlank(message = Mensagens.OBRIGATORIO) String estado) { }

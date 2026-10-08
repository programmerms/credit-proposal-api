package com.dio.desafio.adapter.web.in.contract.dto;

import com.dio.desafio.adapter.web.validation.Mensagens;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ContatoDto(
        @NotBlank(message = Mensagens.OBRIGATORIO) @Email(message = Mensagens.EMAIL_INVALIDO) String email,
        @NotEmpty(message = Mensagens.OBRIGATORIO) List<@Valid TelefoneDto> telefones) { }

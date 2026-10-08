package com.dio.desafio.adapter.web.in.contract.dto;

import com.dio.desafio.adapter.web.validation.Mensagens;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ContratarPropostaRequest(
        @NotNull(message = Mensagens.OBRIGATORIO) UUID numeroProposta,
        @NotNull(message = Mensagens.OBRIGATORIO) @Valid ClienteDto cliente,
        @NotNull(message = Mensagens.OBRIGATORIO) @Valid EnderecoDto endereco,
        @NotNull(message = Mensagens.OBRIGATORIO) @Valid ContatoDto contato) { }

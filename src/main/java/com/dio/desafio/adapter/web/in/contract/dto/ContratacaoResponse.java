package com.dio.desafio.adapter.web.in.contract.dto;

import java.time.Instant;
import java.util.UUID;

public record ContratacaoResponse(UUID numero, UUID numeroProposta, String documento, String tipoPessoa,
                                  ClienteDto cliente, EnderecoDto endereco, ContatoDto contato,
                                  Instant contratadaEm) { }

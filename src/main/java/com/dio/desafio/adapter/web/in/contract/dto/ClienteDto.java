package com.dio.desafio.adapter.web.in.contract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;

/**
 * Dados da pessoa. PF usa nome, dataNascimento, sexo e estadoCivil; PJ usa razaoSocial e nomeFantasia.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClienteDto(String nome, LocalDate dataNascimento, String sexo, String estadoCivil,
                         String razaoSocial, String nomeFantasia) { }

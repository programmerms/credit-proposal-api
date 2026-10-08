package com.dio.desafio.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProposalStatus {
    GERADA("Gerada"),
    CONTRATADA("Contratada");

    private final String description;
}

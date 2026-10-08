package com.dio.desafio.domain.proposal.person.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GenderEnum {

    MALE(1,  "MASCULINO"),
    FEMALE(2, "FEMININO"),
    UNKNOWN(3, "NAO_INFORMADO");

    private final Integer id;
    private final String code;

    public static GenderEnum fromCode(final String code) {
        for (GenderEnum gender : GenderEnum.values()) {
            if (gender.getCode().equalsIgnoreCase(code)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid gender code: " + code);
    }
}

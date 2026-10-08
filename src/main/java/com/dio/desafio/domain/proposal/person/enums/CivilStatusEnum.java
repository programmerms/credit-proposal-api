package com.dio.desafio.domain.proposal.person.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CivilStatusEnum {

    SINGLE(1, "SOLTEIRO"),
    MARRIED(2, "CASADO"),
    DIVORCED(3, "DIVORCIADO"),
    WIDOWED(4, "VIUVO");


    private final Integer id;
    private final String code;

    public static CivilStatusEnum fromDescription(final String description) {
        for (CivilStatusEnum status : CivilStatusEnum.values()) {
            if (status.getCode().equalsIgnoreCase(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid civil status description: " + description);
    }

}

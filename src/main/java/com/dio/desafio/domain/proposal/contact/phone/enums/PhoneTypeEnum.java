package com.dio.desafio.domain.proposal.contact.phone.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PhoneTypeEnum {
    RESIDENTIAL(1, "RESIDENCIAL"),
    COMMERCIAL(2, "COMERCIAL"),
    MOBILE(3, "CELULAR");

    private final Integer id;
    private final String code;

    public static PhoneTypeEnum fromCode(final String code) {
        for (PhoneTypeEnum type : PhoneTypeEnum.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid phone type code: " + code);
    }
}

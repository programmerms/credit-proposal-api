package com.dio.desafio.domain.proposal.address.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AddressTypeEnum {
    RESIDENTIAL(1, "RESIDENCIAL"),
    COMMERCIAL(2, "COMERCIAL");

    private final Integer id;
    private final String code;

    public static AddressTypeEnum fromCode(final String code) {
        for (AddressTypeEnum type : AddressTypeEnum.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid address type code: " + code);
    }
}

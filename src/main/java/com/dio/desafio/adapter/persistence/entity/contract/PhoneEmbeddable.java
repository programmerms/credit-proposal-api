package com.dio.desafio.adapter.persistence.entity.contract;

import com.dio.desafio.domain.proposal.contact.phone.enums.PhoneTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class PhoneEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(name = "phone_type")
    private PhoneTypeEnum type;
    @Column(name = "area_code")
    private String areaCode;
    @Column(name = "phone_number")
    private String number;

    protected PhoneEmbeddable() { }

    public PhoneEmbeddable(PhoneTypeEnum type, String areaCode, String number) {
        this.type = type;
        this.areaCode = areaCode;
        this.number = number;
    }

    public PhoneTypeEnum getType() { return type; }
    public String getAreaCode() { return areaCode; }
    public String getNumber() { return number; }
}

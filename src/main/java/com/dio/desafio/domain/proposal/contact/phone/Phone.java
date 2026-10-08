package com.dio.desafio.domain.proposal.contact.phone;

import com.dio.desafio.domain.proposal.contact.phone.enums.PhoneTypeEnum;

public record Phone(PhoneTypeEnum type, String areaCode, String number) { }

package com.dio.desafio.domain.proposal.address;

import com.dio.desafio.domain.proposal.address.enums.AddressTypeEnum;

public record Address(AddressTypeEnum type, String streetName, String streetNumber, String postalCode,
                      String city, String state) { }

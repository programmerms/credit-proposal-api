package com.dio.desafio.support.domain;

import com.dio.desafio.domain.proposal.address.Address;
import com.dio.desafio.domain.proposal.address.enums.AddressTypeEnum;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static org.instancio.Select.field;

public final class AddressSupport {

    private AddressSupport() { }

    public static InstancioApi<Address> defaultAddress() {
        return Instancio.of(Address.class)
                .set(field(Address::type), AddressTypeEnum.RESIDENTIAL)
                .set(field(Address::streetName), "Rua A")
                .set(field(Address::streetNumber), "10")
                .set(field(Address::postalCode), "01001000")
                .set(field(Address::city), "São Paulo")
                .set(field(Address::state), "SP");
    }
}

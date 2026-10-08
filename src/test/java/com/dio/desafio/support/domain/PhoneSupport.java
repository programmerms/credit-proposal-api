package com.dio.desafio.support.domain;

import com.dio.desafio.domain.proposal.contact.phone.Phone;
import com.dio.desafio.domain.proposal.contact.phone.enums.PhoneTypeEnum;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static org.instancio.Select.field;

public final class PhoneSupport {

    private PhoneSupport() { }

    public static InstancioApi<Phone> defaultPhone() {
        return Instancio.of(Phone.class)
                .set(field(Phone::type), PhoneTypeEnum.MOBILE)
                .set(field(Phone::areaCode), "11")
                .set(field(Phone::number), "999990000");
    }
}

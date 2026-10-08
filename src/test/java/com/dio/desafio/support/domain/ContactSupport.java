package com.dio.desafio.support.domain;

import com.dio.desafio.domain.proposal.contact.Contact;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static com.dio.desafio.support.domain.PhoneSupport.defaultPhone;
import static org.instancio.Select.field;

public final class ContactSupport {

    private ContactSupport() { }

    public static InstancioApi<Contact> defaultContact() {
        return Instancio.of(Contact.class)
                .set(field(Contact::email), "cliente@email.com")
                .supply(field(Contact::phones), () -> List.of(defaultPhone().create()));
    }
}

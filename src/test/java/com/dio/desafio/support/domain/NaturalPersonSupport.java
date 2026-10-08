package com.dio.desafio.support.domain;

import com.dio.desafio.domain.proposal.person.NaturalPerson;
import com.dio.desafio.domain.proposal.person.enums.CivilStatusEnum;
import com.dio.desafio.domain.proposal.person.enums.GenderEnum;
import java.time.LocalDate;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static com.dio.desafio.support.TestConstants.CPF;
import static com.dio.desafio.support.domain.AddressSupport.defaultAddress;
import static com.dio.desafio.support.domain.ContactSupport.defaultContact;
import static org.instancio.Select.field;

public final class NaturalPersonSupport {

    private NaturalPersonSupport() { }

    public static InstancioApi<NaturalPerson> defaultNaturalPerson() {
        return Instancio.of(NaturalPerson.class)
                .set(field(NaturalPerson::name), "Maria Silva")
                .set(field(NaturalPerson::cpfNumber), CPF)
                .set(field(NaturalPerson::birthDate), LocalDate.of(1990, 5, 20))
                .set(field(NaturalPerson::gender), GenderEnum.FEMALE)
                .set(field(NaturalPerson::civilStatus), CivilStatusEnum.SINGLE)
                .supply(field(NaturalPerson::address), () -> defaultAddress().create())
                .supply(field(NaturalPerson::contact), () -> defaultContact().create());
    }
}

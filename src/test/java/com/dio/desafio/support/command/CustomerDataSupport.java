package com.dio.desafio.support.command;

import com.dio.desafio.application.port.in.ContractProposalCommand.CustomerData;
import com.dio.desafio.domain.proposal.person.enums.CivilStatusEnum;
import com.dio.desafio.domain.proposal.person.enums.GenderEnum;
import java.time.LocalDate;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static org.instancio.Select.field;

public final class CustomerDataSupport {

    private CustomerDataSupport() { }

    /** Data of a natural person: PJ-only fields are null. */
    public static InstancioApi<CustomerData> defaultNaturalCustomerData() {
        return Instancio.of(CustomerData.class)
                .set(field(CustomerData::name), "Maria Silva")
                .set(field(CustomerData::birthDate), LocalDate.of(1990, 5, 20))
                .set(field(CustomerData::gender), GenderEnum.FEMALE)
                .set(field(CustomerData::civilStatus), CivilStatusEnum.SINGLE)
                .set(field(CustomerData::businessName), null)
                .set(field(CustomerData::tradeName), null);
    }

    /** Data of a legal person: PF-only fields are null. */
    public static InstancioApi<CustomerData> defaultLegalCustomerData() {
        return Instancio.of(CustomerData.class)
                .set(field(CustomerData::name), null)
                .set(field(CustomerData::birthDate), null)
                .set(field(CustomerData::gender), null)
                .set(field(CustomerData::civilStatus), null)
                .set(field(CustomerData::businessName), "Empresa Ltda")
                .set(field(CustomerData::tradeName), "Empresa");
    }
}

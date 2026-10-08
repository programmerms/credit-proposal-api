package com.dio.desafio.support.domain;

import com.dio.desafio.domain.proposal.installment.Installment;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static org.instancio.Select.field;

public final class InstallmentSupport {

    private InstallmentSupport() { }

    public static InstancioApi<Installment> defaultInstallment() {
        return Instancio.of(Installment.class)
                .set(field(Installment::dueDate), LocalDate.of(2026, 2, 1))
                .set(field(Installment::principal), new BigDecimal("30.00"))
                .set(field(Installment::interest), new BigDecimal("5.00"))
                .set(field(Installment::amount), new BigDecimal("35.00"))
                .set(field(Installment::balance), new BigDecimal("70.00"));
    }
}

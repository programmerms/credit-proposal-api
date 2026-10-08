package com.dio.desafio.application.strategy;

import com.dio.desafio.domain.PersonType;
import org.springframework.stereotype.Component;

@Component
public class CustomerStrategySelector {

    private final NaturalPersonCustomerStrategy natural;
    private final LegalPersonCustomerStrategy legal;

    public CustomerStrategySelector(NaturalPersonCustomerStrategy natural, LegalPersonCustomerStrategy legal) {
        this.natural = natural;
        this.legal = legal;
    }

    public CustomerStrategy select(PersonType type) {
        return type == PersonType.PF ? natural : legal;
    }
}

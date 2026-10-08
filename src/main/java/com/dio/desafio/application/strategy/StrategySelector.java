package com.dio.desafio.application.strategy;

import com.dio.desafio.domain.PersonType;
import org.springframework.stereotype.Component;

@Component
public class StrategySelector {

    private final PfRateStrategy pf;
    private final PjRateStrategy pj;

    public StrategySelector(PfRateStrategy pf, PjRateStrategy pj) {
        this.pf = pf;
        this.pj = pj;
    }

    public RateStrategy select(PersonType type) {
        return type == PersonType.PF ? pf : pj;
    }
}

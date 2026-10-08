package com.dio.desafio.application.strategy;

import com.dio.desafio.application.port.out.ProductCatalogPort;
import com.dio.desafio.domain.PersonType;


public final class PfRateStrategy extends AbstractCatalogStrategy {

    public PfRateStrategy(ProductCatalogPort catalog) {
        super(PersonType.PF, catalog);
    }
}

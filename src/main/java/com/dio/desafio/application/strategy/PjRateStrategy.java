package com.dio.desafio.application.strategy;

import com.dio.desafio.application.port.out.ProductCatalogPort;
import com.dio.desafio.domain.PersonType;


public final class PjRateStrategy extends AbstractCatalogStrategy {

    public PjRateStrategy(ProductCatalogPort catalog) {
        super(PersonType.PJ, catalog);
    }
}

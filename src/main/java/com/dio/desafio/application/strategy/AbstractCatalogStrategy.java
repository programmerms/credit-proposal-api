package com.dio.desafio.application.strategy;

import com.dio.desafio.application.exception.ProposalException;
import com.dio.desafio.application.port.out.ProductCatalogPort;
import com.dio.desafio.domain.PersonType;
import com.dio.desafio.domain.Product;

abstract class AbstractCatalogStrategy implements RateStrategy {

    private final PersonType personType;
    private final PersonType otherType;
    private final ProductCatalogPort catalog;

    AbstractCatalogStrategy(PersonType personType, ProductCatalogPort catalog) {
        this.personType = personType;
        this.otherType = personType == PersonType.PF ? PersonType.PJ : PersonType.PF;
        this.catalog = catalog;
    }

    @Override
    public Product resolve(String productCode) {
        if (!catalog.isAvailable(personType)) {
            throw new ProposalException("CATALOGO_INDISPONIVEL",
                    "Catálogo de produtos " + personType + " indisponível ou inválido");
        }
        return catalog.find(personType, productCode).orElseThrow(() -> {
            if (catalog.isAvailable(otherType) && catalog.find(otherType, productCode).isPresent()) {
                return new ProposalException("PRODUTO_INCOMPATIVEL",
                        "Produto não disponível para o tipo de pessoa " + personType);
            }
            return new ProposalException("PRODUTO_NAO_ENCONTRADO", "Produto não encontrado");
        });
    }
}

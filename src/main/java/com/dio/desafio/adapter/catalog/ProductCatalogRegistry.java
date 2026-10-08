package com.dio.desafio.adapter.catalog;

import com.dio.desafio.application.port.out.ProductCatalogPort;
import com.dio.desafio.domain.PersonType;
import com.dio.desafio.domain.Product;
import java.util.Map;
import java.util.Optional;

/**
 * Explicit singleton that holds both PF and PJ product catalogs.
 * Loads catalogs once from classpath resources on first access.
 */
public final class ProductCatalogRegistry implements ProductCatalogPort {

    private static final ProductCatalogRegistry INSTANCE = new ProductCatalogRegistry();

    private final CatalogLoadResult pfCatalog;
    private final CatalogLoadResult pjCatalog;

    private ProductCatalogRegistry() {
        ProductCatalogLoader loader = new ProductCatalogLoader();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        this.pfCatalog = loader.load(cl, "catalogs/pf.json");
        this.pjCatalog = loader.load(cl, "catalogs/pj.json");
    }

    public static ProductCatalogRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the catalog state for the given person type.
     */
    public CatalogLoadResult state(PersonType personType) {
        return personType == PersonType.PF ? pfCatalog : pjCatalog;
    }

    /**
     * Convenience accessor for the product map of a given person type.
     */
    public Map<String, Product> products(PersonType personType) {
        return state(personType).products();
    }

    @Override
    public boolean isAvailable(PersonType personType) {
        return state(personType).available();
    }

    @Override
    public Optional<Product> find(PersonType personType, String productCode) {
        return Optional.ofNullable(state(personType).products().get(productCode));
    }
}

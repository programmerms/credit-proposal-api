package com.dio.desafio.adapter.catalog;

import com.dio.desafio.domain.Product;
import java.util.Map;

/**
 * Immutable snapshot of a loaded catalog. If the catalog was invalid or missing,
 * {@code available()} is {@code false} and {@code products()} is empty.
 */
public record CatalogLoadResult(boolean available, Map<String, Product> products) {

    public CatalogLoadResult {
        products = products == null ? Map.of() : Map.copyOf(products);
    }

    public static CatalogLoadResult unavailable() {
        return new CatalogLoadResult(false, Map.of());
    }
}

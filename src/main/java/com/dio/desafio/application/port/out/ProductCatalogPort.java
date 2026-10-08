package com.dio.desafio.application.port.out;

import com.dio.desafio.domain.PersonType;
import com.dio.desafio.domain.Product;
import java.util.Optional;

/**
 * Outbound port for reading the product catalog of each person type.
 */
public interface ProductCatalogPort {

    /** Whether the catalog of the given person type was loaded successfully. */
    boolean isAvailable(PersonType personType);

    /** Looks up a product by its stable code inside the catalog of the given person type. */
    Optional<Product> find(PersonType personType, String productCode);
}

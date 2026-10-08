package com.dio.desafio.support.domain;

import com.dio.desafio.domain.Product;
import java.math.BigDecimal;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static org.instancio.Select.field;

public final class ProductSupport {

    private ProductSupport() { }

    public static InstancioApi<Product> defaultProduct() {
        return Instancio.of(Product.class)
                .set(field(Product::code), "CREDITO_PESSOAL")
                .set(field(Product::description), "Crédito Pessoal")
                .set(field(Product::monthlyRate), new BigDecimal("0.05"));
    }
}

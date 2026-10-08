package com.dio.desafio.adapter.catalog;

import com.dio.desafio.domain.PersonType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ProductCatalogRegistryTest {

    @Test
    @DisplayName("Given the registry is requested repeatedly When returned Then one registry instance with independent PF and PJ catalogs is shared")
    void givenRegistryRequestedRepeatedlyWhenReturnedThenOneRegistryWithIndependentCatalogsIsShared() {
        final var first = ProductCatalogRegistry.getInstance();
        final var second = ProductCatalogRegistry.getInstance();

        verifyGivenRegistryRequestedRepeatedlyWhenReturnedThenOneRegistryWithIndependentCatalogsIsShared(
                first, second);
    }

    private void verifyGivenRegistryRequestedRepeatedlyWhenReturnedThenOneRegistryWithIndependentCatalogsIsShared(
            final ProductCatalogRegistry first, final ProductCatalogRegistry second) {
        assertThat(first).isSameAs(second);
        assertThat(first.state(PersonType.PF).available()).isTrue();
        assertThat(first.state(PersonType.PJ).available()).isTrue();
        assertThat(first.products(PersonType.PF).get("CREDITO_PESSOAL").monthlyRate()).hasToString("0.05");
        assertThat(first.products(PersonType.PJ).get("CREDITO_VERDE").monthlyRate()).hasToString("0.04");
    }
}

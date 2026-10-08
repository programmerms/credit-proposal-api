package com.dio.desafio.application.strategy;

import com.dio.desafio.adapter.catalog.ProductCatalogRegistry;
import com.dio.desafio.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class RateStrategyTest {

    private final ProductCatalogRegistry registry = ProductCatalogRegistry.getInstance();

    @Test
    @DisplayName("Given a PF catalog product When the PF Strategy resolves Then it uses the PF rate")
    void givenAPfCatalogProductWhenThePfStrategyResolvesThenItUsesThePfRate() {
        final var result = new PfRateStrategy(registry).resolve("CREDITO_PESSOAL");

        verifyGivenAPfCatalogProductWhenThePfStrategyResolvesThenItUsesThePfRate(result);
    }

    @Test
    @DisplayName("Given a PJ catalog product When the PJ Strategy resolves Then it uses the PJ rate")
    void givenAPjCatalogProductWhenThePjStrategyResolvesThenItUsesThePjRate() {
        final var result = new PjRateStrategy(registry).resolve("CREDITO_VERDE");

        verifyGivenAPjCatalogProductWhenThePjStrategyResolvesThenItUsesThePjRate(result);
    }

    @Test
    @DisplayName("Given the personal credit product When PJ requests it Then it is incompatible")
    void givenThePersonalCreditProductWhenPjRequestsItThenItIsIncompatible() {
        final var thrown = catchThrowable(() -> new PjRateStrategy(registry).resolve("CREDITO_PESSOAL"));

        verifyGivenThePersonalCreditProductWhenPjRequestsItThenItIsIncompatible(thrown);
    }

    @Test
    @DisplayName("Given a product exclusive to PJ When PF requests it Then it is incompatible")
    void givenAProductExclusiveToPjWhenPfRequestsItThenItIsIncompatible() {
        final var thrown = catchThrowable(() -> new PfRateStrategy(registry).resolve("CAPITAL_GIRO"));

        verifyGivenAProductExclusiveToPjWhenPfRequestsItThenItIsIncompatible(thrown);
    }

    @Test
    @DisplayName("Given an unknown product code When a catalog Strategy resolves it Then it is not found")
    void givenAnUnknownProductCodeWhenACatalogStrategyResolvesItThenItIsNotFound() {
        final var thrown = catchThrowable(() -> new PfRateStrategy(registry).resolve("UNKNOWN"));

        verifyGivenAnUnknownProductCodeWhenACatalogStrategyResolvesItThenItIsNotFound(thrown);
    }

    private void verifyGivenAPfCatalogProductWhenThePfStrategyResolvesThenItUsesThePfRate(final Product actual) {
        assertThat(actual.monthlyRate()).hasToString("0.05");
    }

    private void verifyGivenAPjCatalogProductWhenThePjStrategyResolvesThenItUsesThePjRate(final Product actual) {
        assertThat(actual.monthlyRate()).hasToString("0.04");
        assertThat(actual.description()).isEqualTo("Crédito Verde / Sustentável");
    }

    private void verifyGivenThePersonalCreditProductWhenPjRequestsItThenItIsIncompatible(final Throwable actual) {
        assertThat(actual).hasMessageContaining("não disponível para o tipo de pessoa PJ");
    }

    private void verifyGivenAProductExclusiveToPjWhenPfRequestsItThenItIsIncompatible(final Throwable actual) {
        assertThat(actual).hasMessageContaining("não disponível para o tipo de pessoa PF");
    }

    private void verifyGivenAnUnknownProductCodeWhenACatalogStrategyResolvesItThenItIsNotFound(
            final Throwable actual) {
        assertThat(actual).hasMessageContaining("não encontrado");
    }
}

package com.dio.desafio.adapter.catalog;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class ProductCatalogLoaderTest {

    private static final String RESOURCE = "fixture.json";

    private ClassLoader classLoader;
    private ProductCatalogLoader loader;

    @BeforeEach
    void setUp() {
        classLoader = mock(ClassLoader.class);
        loader = new ProductCatalogLoader();
    }

    @Test
    @DisplayName("Given a zero monthly rate When the catalog loads Then it is invalid")
    void givenZeroMonthlyRateWhenTheCatalogLoadsThenItIsInvalid() {
        final var json = "[{\"code\":\"X\",\"description\":\"X\",\"monthlyRate\":0}]";
        setupGivenZeroMonthlyRateWhenTheCatalogLoadsThenItIsInvalid(json);

        final var result = loader.load(classLoader, RESOURCE);

        verifyGivenZeroMonthlyRateWhenTheCatalogLoadsThenItIsInvalid(result);
    }

    @Test
    @DisplayName("Given a negative monthly rate When the catalog loads Then it is invalid")
    void givenNegativeMonthlyRateWhenTheCatalogLoadsThenItIsInvalid() {
        final var json = "[{\"code\":\"X\",\"description\":\"X\",\"monthlyRate\":-0.1}]";
        setupGivenNegativeMonthlyRateWhenTheCatalogLoadsThenItIsInvalid(json);

        final var result = loader.load(classLoader, RESOURCE);

        verifyGivenNegativeMonthlyRateWhenTheCatalogLoadsThenItIsInvalid(result);
    }

    @Test
    @DisplayName("Given a positive monthly rate When the catalog loads Then the product is available")
    void givenPositiveMonthlyRateWhenTheCatalogLoadsThenTheProductIsAvailable() {
        final var json = "[{\"code\":\"X\",\"description\":\"Friendly name\",\"monthlyRate\":1000}]";
        setupGivenPositiveMonthlyRateWhenTheCatalogLoadsThenTheProductIsAvailable(json);

        final var result = loader.load(classLoader, RESOURCE);

        verifyGivenPositiveMonthlyRateWhenTheCatalogLoadsThenTheProductIsAvailable(result);
    }

    @Test
    @DisplayName("Given a product with a friendly description When the catalog loads Then lookup uses its stable code")
    void givenProductWithFriendlyDescriptionWhenTheCatalogLoadsThenLookupUsesItsStableCode() {
        final var json = "[{\"code\":\"STABLE_CODE\",\"description\":\"Friendly name\",\"monthlyRate\":0.01}]";
        setupGivenProductWithFriendlyDescriptionWhenTheCatalogLoadsThenLookupUsesItsStableCode(json);

        final var result = loader.load(classLoader, RESOURCE);

        verifyGivenProductWithFriendlyDescriptionWhenTheCatalogLoadsThenLookupUsesItsStableCode(result);
    }

    @Test
    @DisplayName("Given malformed JSON When the catalog loads Then the catalog is unavailable")
    void givenMalformedJsonWhenTheCatalogLoadsThenItIsUnavailable() {
        final var json = "{";
        setupGivenMalformedJsonWhenTheCatalogLoadsThenItIsUnavailable(json);

        final var result = loader.load(classLoader, RESOURCE);

        verifyGivenMalformedJsonWhenTheCatalogLoadsThenItIsUnavailable(result);
    }

    @Test
    @DisplayName("Given duplicate product codes When the catalog loads Then the catalog is unavailable")
    void givenDuplicateProductCodesWhenTheCatalogLoadsThenItIsUnavailable() {
        final var json = "[{\"code\":\"X\",\"description\":\"X\",\"monthlyRate\":0.01},{\"code\":\"X\",\"description\":\"Y\",\"monthlyRate\":0.02}]";
        setupGivenDuplicateProductCodesWhenTheCatalogLoadsThenItIsUnavailable(json);

        final var result = loader.load(classLoader, RESOURCE);

        verifyGivenDuplicateProductCodesWhenTheCatalogLoadsThenItIsUnavailable(result);
    }

    @Test
    @DisplayName("Given a product without a rate When the catalog loads Then the catalog is unavailable")
    void givenProductWithoutRateWhenTheCatalogLoadsThenItIsUnavailable() {
        final var json = "[{\"code\":\"X\",\"description\":\"X\"}]";
        setupGivenProductWithoutRateWhenTheCatalogLoadsThenItIsUnavailable(json);

        final var result = loader.load(classLoader, RESOURCE);

        verifyGivenProductWithoutRateWhenTheCatalogLoadsThenItIsUnavailable(result);
    }

    @Test
    @DisplayName("Given a rate written as a string When the catalog loads Then the catalog is unavailable")
    void givenRateAsStringWhenTheCatalogLoadsThenItIsUnavailable() {
        final var json = "[{\"code\":\"X\",\"description\":\"X\",\"monthlyRate\":\"0.01\"}]";
        setupGivenRateAsStringWhenTheCatalogLoadsThenItIsUnavailable(json);

        final var result = loader.load(classLoader, RESOURCE);

        verifyGivenRateAsStringWhenTheCatalogLoadsThenItIsUnavailable(result);
    }

    @Test
    @DisplayName("Given a description containing a closing brace When the catalog loads Then the product is parsed correctly")
    void givenDescriptionWithClosingBraceWhenTheCatalogLoadsThenTheProductIsParsedCorrectly() {
        final var json = "[{\"code\":\"X\",\"description\":\"a } b\",\"monthlyRate\":0.01}]";
        setupGivenDescriptionWithClosingBraceWhenTheCatalogLoadsThenTheProductIsParsedCorrectly(json);

        final var result = loader.load(classLoader, RESOURCE);

        verifyGivenDescriptionWithClosingBraceWhenTheCatalogLoadsThenTheProductIsParsedCorrectly(result);
    }

    @Test
    @DisplayName("Given a rate with trailing garbage When the catalog loads Then the catalog is unavailable")
    void givenRateWithTrailingGarbageWhenTheCatalogLoadsThenItIsUnavailable() {
        final var json = "[{\"code\":\"X\",\"description\":\"X\",\"monthlyRate\":0.05abc}]";
        setupGivenRateWithTrailingGarbageWhenTheCatalogLoadsThenItIsUnavailable(json);

        final var result = loader.load(classLoader, RESOURCE);

        verifyGivenRateWithTrailingGarbageWhenTheCatalogLoadsThenItIsUnavailable(result);
    }

    @Test
    @DisplayName("Given a missing catalog resource When loaded Then the catalog is unavailable")
    void givenMissingCatalogResourceWhenLoadedThenTheCatalogIsUnavailable() {
        setupGivenMissingCatalogResourceWhenLoadedThenTheCatalogIsUnavailable();

        final var result = loader.load(classLoader, RESOURCE);

        verifyGivenMissingCatalogResourceWhenLoadedThenTheCatalogIsUnavailable(result);
    }

    private void setupGivenZeroMonthlyRateWhenTheCatalogLoadsThenItIsInvalid(final String json) {
        doReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                .when(classLoader).getResourceAsStream(RESOURCE);
    }

    private void verifyGivenZeroMonthlyRateWhenTheCatalogLoadsThenItIsInvalid(final CatalogLoadResult actual) {
        assertThat(actual.available()).isFalse();
    }

    private void setupGivenNegativeMonthlyRateWhenTheCatalogLoadsThenItIsInvalid(final String json) {
        doReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                .when(classLoader).getResourceAsStream(RESOURCE);
    }

    private void verifyGivenNegativeMonthlyRateWhenTheCatalogLoadsThenItIsInvalid(final CatalogLoadResult actual) {
        assertThat(actual.available()).isFalse();
    }

    private void setupGivenPositiveMonthlyRateWhenTheCatalogLoadsThenTheProductIsAvailable(final String json) {
        doReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                .when(classLoader).getResourceAsStream(RESOURCE);
    }

    private void verifyGivenPositiveMonthlyRateWhenTheCatalogLoadsThenTheProductIsAvailable(final CatalogLoadResult actual) {
        assertThat(actual.available()).isTrue();
        assertThat(actual.products()).containsKey("X");
    }

    private void setupGivenProductWithFriendlyDescriptionWhenTheCatalogLoadsThenLookupUsesItsStableCode(final String json) {
        doReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                .when(classLoader).getResourceAsStream(RESOURCE);
    }

    private void verifyGivenProductWithFriendlyDescriptionWhenTheCatalogLoadsThenLookupUsesItsStableCode(final CatalogLoadResult actual) {
        assertThat(actual.products()).containsKey("STABLE_CODE");
        assertThat(actual.products()).doesNotContainKey("Friendly name");
    }

    private void setupGivenMalformedJsonWhenTheCatalogLoadsThenItIsUnavailable(final String json) {
        doReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                .when(classLoader).getResourceAsStream(RESOURCE);
    }

    private void verifyGivenMalformedJsonWhenTheCatalogLoadsThenItIsUnavailable(final CatalogLoadResult actual) {
        assertThat(actual.available()).isFalse();
    }

    private void setupGivenDuplicateProductCodesWhenTheCatalogLoadsThenItIsUnavailable(final String json) {
        doReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                .when(classLoader).getResourceAsStream(RESOURCE);
    }

    private void verifyGivenDuplicateProductCodesWhenTheCatalogLoadsThenItIsUnavailable(final CatalogLoadResult actual) {
        assertThat(actual.available()).isFalse();
    }

    private void setupGivenProductWithoutRateWhenTheCatalogLoadsThenItIsUnavailable(final String json) {
        doReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                .when(classLoader).getResourceAsStream(RESOURCE);
    }

    private void verifyGivenProductWithoutRateWhenTheCatalogLoadsThenItIsUnavailable(final CatalogLoadResult actual) {
        assertThat(actual.available()).isFalse();
    }

    private void setupGivenRateAsStringWhenTheCatalogLoadsThenItIsUnavailable(final String json) {
        doReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                .when(classLoader).getResourceAsStream(RESOURCE);
    }

    private void verifyGivenRateAsStringWhenTheCatalogLoadsThenItIsUnavailable(final CatalogLoadResult actual) {
        assertThat(actual.available()).isFalse();
    }

    private void setupGivenDescriptionWithClosingBraceWhenTheCatalogLoadsThenTheProductIsParsedCorrectly(final String json) {
        doReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                .when(classLoader).getResourceAsStream(RESOURCE);
    }

    private void verifyGivenDescriptionWithClosingBraceWhenTheCatalogLoadsThenTheProductIsParsedCorrectly(final CatalogLoadResult actual) {
        assertThat(actual.available()).isTrue();
        assertThat(actual.products().get("X").description()).isEqualTo("a } b");
    }

    private void setupGivenRateWithTrailingGarbageWhenTheCatalogLoadsThenItIsUnavailable(final String json) {
        doReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                .when(classLoader).getResourceAsStream(RESOURCE);
    }

    private void verifyGivenRateWithTrailingGarbageWhenTheCatalogLoadsThenItIsUnavailable(final CatalogLoadResult actual) {
        assertThat(actual.available()).isFalse();
    }

    private void setupGivenMissingCatalogResourceWhenLoadedThenTheCatalogIsUnavailable() {
        doReturn(null).when(classLoader).getResourceAsStream(RESOURCE);
    }

    private void verifyGivenMissingCatalogResourceWhenLoadedThenTheCatalogIsUnavailable(
            final CatalogLoadResult actual) {
        assertThat(actual.available()).isFalse();
    }
}

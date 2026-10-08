package com.dio.desafio.adapter.catalog;

import com.dio.desafio.domain.Product;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Loads a product catalog from a JSON resource on the classpath.
 * Validates that every product has a unique code and a positive monthly rate.
 * Returns {@link CatalogLoadResult#unavailable()} when the resource is missing, malformed,
 * or contains invalid entries; the reason is logged with the affected resource.
 */
public class ProductCatalogLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCatalogLoader.class);

    private final JsonMapper mapper = JsonMapper.builder().build();

    /**
     * Loads and validates a catalog JSON array from the classpath via the given class loader.
     *
     * @param classLoader class loader to resolve the resource
     * @param resource    classpath resource path (e.g. "catalogs/pf.json")
     * @return a valid {@link CatalogLoadResult} or an unavailable one
     */
    public CatalogLoadResult load(ClassLoader classLoader, String resource) {
        try (InputStream stream = classLoader.getResourceAsStream(resource)) {
            if (stream == null) {
                return reject(resource, "resource not found");
            }
            return parse(resource, mapper.readTree(stream));
        } catch (Exception e) {
            return reject(resource, "unreadable or malformed JSON (" + e.getClass().getSimpleName() + ")");
        }
    }

    private CatalogLoadResult parse(String resource, JsonNode root) {
        if (root == null || !root.isArray()) {
            return reject(resource, "root must be a JSON array");
        }
        Map<String, Product> products = new HashMap<>();
        for (JsonNode entry : root) {
            Product product = toProduct(entry);
            if (product == null) {
                return reject(resource, "invalid entry " + entry);
            }
            if (products.putIfAbsent(product.code(), product) != null) {
                return reject(resource, "duplicate code " + product.code());
            }
        }
        if (products.isEmpty()) {
            return reject(resource, "catalog has no products");
        }
        return new CatalogLoadResult(true, products);
    }

    private Product toProduct(JsonNode entry) {
        JsonNode code = entry.get("code");
        JsonNode description = entry.get("description");
        JsonNode rate = entry.get("monthlyRate");
        if (code == null || !code.isString() || code.asString().isBlank()
                || description == null || !description.isString()
                || rate == null || !rate.isNumber()) {
            return null;
        }
        BigDecimal monthlyRate = rate.decimalValue();
        if (monthlyRate.signum() <= 0) {
            return null;
        }
        return new Product(code.asString(), description.asString(), monthlyRate);
    }

    private CatalogLoadResult reject(String resource, String reason) {
        LOG.warn("Product catalog '{}' is unavailable: {}", resource, reason);
        return CatalogLoadResult.unavailable();
    }
}

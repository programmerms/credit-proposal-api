package com.dio.desafio.config;

import com.dio.desafio.adapter.catalog.ProductCatalogRegistry;
import com.dio.desafio.application.port.out.ProductCatalogPort;
import com.dio.desafio.application.strategy.PfRateStrategy;
import com.dio.desafio.application.strategy.PjRateStrategy;
import com.dio.desafio.domain.DocumentValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    ProductCatalogPort productCatalogPort() {
        return ProductCatalogRegistry.getInstance();
    }

    @Bean
    DocumentValidator documentValidator() {
        return new DocumentValidator();
    }

    @Bean
    PfRateStrategy pfRateStrategy(ProductCatalogPort catalog) {
        return new PfRateStrategy(catalog);
    }

    @Bean
    PjRateStrategy pjRateStrategy(ProductCatalogPort catalog) {
        return new PjRateStrategy(catalog);
    }
}

package com.dio.desafio.application.strategy;

import com.dio.desafio.domain.Product;

public interface RateStrategy {
    Product resolve(String productCode);
}

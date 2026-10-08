package com.dio.desafio.domain;

import java.math.BigDecimal;

public record Product(String code, String description, BigDecimal monthlyRate) { }

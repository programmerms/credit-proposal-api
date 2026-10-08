package com.dio.desafio.adapter.web.validation;

/**
 * Mensagens de validação em português, fixas, para não depender do idioma do servidor ou do cliente.
 */
public final class Mensagens {

    public static final String OBRIGATORIO = "é obrigatório";
    public static final String MAIOR_QUE_ZERO = "deve ser maior que zero";
    public static final String VALOR_MAXIMO = "deve ser no máximo 1.000.000.000,00";
    public static final String PRAZO_MINIMO = "deve ser no mínimo 1";
    public static final String PRAZO_MAXIMO = "deve ser no máximo 600";
    public static final String EMAIL_INVALIDO = "deve ser um e-mail válido";

    private Mensagens() { }
}

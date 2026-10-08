package com.dio.desafio.domain;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import java.util.Locale;

/**
 * Validates and normalizes CPF and CNPJ documents using Caelum Stella.
 * Determines whether a document is PF (CPF) or PJ (CNPJ).
 */
public final class DocumentValidator {

    private final CPFValidator cpfValidator;
    private final CNPJValidator cnpjValidator;

    public DocumentValidator() {
        this.cpfValidator = new CPFValidator(false);
        this.cnpjValidator = new CNPJValidator(false);
    }

    public DocumentIdentity identify(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("documento é obrigatório");
        }
        String normalized = normalize(input);

        if (normalized.length() == 11 && normalized.matches("\\d{11}")) {
            validateCpf(normalized);
            return new DocumentIdentity(normalized, PersonType.PF);
        }

        if (normalized.length() == 14) {
            validateCnpj(normalized);
            return new DocumentIdentity(normalized, PersonType.PJ);
        }

        throw new IllegalArgumentException("documento deve ser um CPF ou CNPJ válido");
    }

    private String normalize(String input) {
        return input.replaceAll("[./\\-\\s]", "").toUpperCase(Locale.ROOT);
    }

    private void validateCpf(String value) {
        try {
            cpfValidator.assertValid(value);
        } catch (InvalidStateException e) {
            throw new IllegalArgumentException("documento deve ser um CPF ou CNPJ válido");
        }
    }

    private void validateCnpj(String value) {
        try {
            cnpjValidator.assertValid(value);
        } catch (InvalidStateException e) {
            throw new IllegalArgumentException("documento deve ser um CPF ou CNPJ válido");
        }
    }
}

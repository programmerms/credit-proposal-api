package com.dio.desafio.application.exception;

/**
 * Business failure identified by a stable {@code code}. Knows nothing about HTTP: the web adapter decides
 * which status each code maps to.
 */
public class ProposalException extends RuntimeException {

    private final String code;

    public ProposalException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() { return code; }
}

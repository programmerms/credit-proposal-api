package com.dio.desafio.application.exception;

/**
 * Raised by the persistence port when a proposal already has a contract. Technology-neutral: the adapter
 * translates the storage constraint violation into it and the service turns it into a business error.
 */
public class DuplicateContractException extends RuntimeException {

    public DuplicateContractException(Throwable cause) {
        super("contract already exists for the proposal", cause);
    }
}

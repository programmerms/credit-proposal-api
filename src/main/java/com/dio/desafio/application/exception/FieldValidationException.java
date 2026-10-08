package com.dio.desafio.application.exception;

import java.util.List;

/**
 * Validation failure on one or more request fields. Carries every violation found so the client can fix
 * them all at once.
 */
public class FieldValidationException extends RuntimeException {

    public record Violation(String field, String reason) { }

    private final List<Violation> violations;

    public FieldValidationException(String field, String reason) {
        this(List.of(new Violation(field, reason)));
    }

    public FieldValidationException(List<Violation> violations) {
        super(violations.get(0).reason());
        this.violations = List.copyOf(violations);
    }

    public List<Violation> violations() { return violations; }

    /** The first violated field. */
    public String field() { return violations.get(0).field(); }
}

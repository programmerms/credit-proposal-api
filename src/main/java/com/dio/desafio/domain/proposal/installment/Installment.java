package com.dio.desafio.domain.proposal.installment;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * One scheduled payment of a proposal. {@code amount = principal + interest}; {@code balance} is the
 * outstanding principal after this installment is paid.
 */
public record Installment(int number, LocalDate dueDate, BigDecimal principal, BigDecimal interest,
                          BigDecimal amount, BigDecimal balance) { }


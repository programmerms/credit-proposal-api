package com.dio.desafio.domain.proposal.installment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Pure domain calculator for the Price table (fixed installments):
 * {@code PMT = PV * i / (1 - (1 + i)^-n)}. Monetary values use scale 2 and HALF_EVEN rounding; the last
 * installment absorbs rounding differences so that the principal amortized equals the requested amount.
 */
public final class InstallmentPlanCalculator {
    private static final int MONEY_SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    private InstallmentPlanCalculator() { }

    public static List<Installment> price(BigDecimal principal, BigDecimal monthlyRate, int termMonths,
                                          LocalDate referenceDate) {
        if (principal == null || principal.signum() <= 0) {
            throw new IllegalArgumentException("principal must be greater than zero");
        }
        if (monthlyRate == null || monthlyRate.signum() < 0) {
            throw new IllegalArgumentException("monthlyRate must not be negative");
        }
        if (termMonths <= 0) {
            throw new IllegalArgumentException("termMonths must be greater than zero");
        }
        BigDecimal payment = payment(principal, monthlyRate, termMonths);
        BigDecimal balance = principal.setScale(MONEY_SCALE, ROUNDING);
        List<Installment> installments = new ArrayList<>(termMonths);
        for (int number = 1; number <= termMonths; number++) {
            BigDecimal interest = balance.multiply(monthlyRate).setScale(MONEY_SCALE, ROUNDING);
            BigDecimal amortization;
            BigDecimal amount;
            if (number == termMonths) {
                amortization = balance;
                amount = amortization.add(interest);
            } else {
                amount = payment;
                amortization = payment.subtract(interest);
            }
            balance = balance.subtract(amortization);
            installments.add(new Installment(number, referenceDate.plusMonths(number), amortization, interest,
                    amount, balance));
        }
        return List.copyOf(installments);
    }

    private static BigDecimal payment(BigDecimal principal, BigDecimal monthlyRate, int termMonths) {
        if (monthlyRate.signum() == 0) {
            return principal.divide(BigDecimal.valueOf(termMonths), MONEY_SCALE, ROUNDING);
        }
        BigDecimal growth = BigDecimal.ONE.add(monthlyRate).pow(termMonths, MATH_CONTEXT);
        return principal.multiply(monthlyRate).multiply(growth)
                .divide(growth.subtract(BigDecimal.ONE), MATH_CONTEXT)
                .setScale(MONEY_SCALE, ROUNDING);
    }
}


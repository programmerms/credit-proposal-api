package com.dio.desafio.domain.proposal;

import com.dio.desafio.domain.proposal.installment.Installment;
import com.dio.desafio.domain.proposal.installment.InstallmentPlanCalculator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.dio.desafio.support.TestConstants.INTEGER_THREE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class InstallmentPlanCalculatorTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 31);
    private static final BigDecimal RATE = new BigDecimal("0.05");

    @Test
    @DisplayName("Given amount, rate and term When the Price plan is calculated Then it has one installment per month and ends with zero balance")
    void givenAmountRateAndTermWhenThePricePlanIsCalculatedThenItHasOneInstallmentPerMonthAndEndsWithZeroBalance() {
        final var amount = new BigDecimal("1000.00");

        final var result = InstallmentPlanCalculator.price(amount, RATE, 12, START);

        verifyAmortizesToZero(result, amount, 12);
    }

    @Test
    @DisplayName("Given a known case When the Price plan is calculated Then the fixed installment matches the formula")
    void givenAKnownCaseWhenThePricePlanIsCalculatedThenTheFixedInstallmentMatchesTheFormula() {
        final var amount = new BigDecimal("1000.00");

        final var result = InstallmentPlanCalculator.price(amount, RATE, 12, START);

        verifyGivenAKnownCaseWhenThePricePlanIsCalculatedThenTheFixedInstallmentMatchesTheFormula(result);
    }

    @Test
    @DisplayName("Given a start date at month end When the plan is calculated Then due dates are monthly from the reference date")
    void givenAStartDateAtMonthEndWhenThePlanIsCalculatedThenDueDatesAreMonthlyFromTheReferenceDate() {
        final var amount = new BigDecimal("300.00");

        final var result = InstallmentPlanCalculator.price(amount, new BigDecimal("0.025"), INTEGER_THREE, START);

        verifyGivenAStartDateAtMonthEndWhenThePlanIsCalculatedThenDueDatesAreMonthlyFromTheReferenceDate(result);
    }

    @Test
    @DisplayName("Given a zero rate When the plan is calculated Then installments split the principal evenly")
    void givenAZeroRateWhenThePlanIsCalculatedThenInstallmentsSplitThePrincipalEvenly() {
        final var amount = new BigDecimal("100.00");

        final var result = InstallmentPlanCalculator.price(amount, BigDecimal.ZERO, INTEGER_THREE, START);

        verifyGivenAZeroRateWhenThePlanIsCalculatedThenInstallmentsSplitThePrincipalEvenly(result, amount);
    }

    @Test
    @DisplayName("Given a zero principal When the plan is calculated Then it is rejected")
    void givenAZeroPrincipalWhenThePlanIsCalculatedThenItIsRejected() {
        final var thrown = catchThrowable(() -> InstallmentPlanCalculator.price(BigDecimal.ZERO, RATE, 12, START));

        verifyGivenAZeroPrincipalWhenThePlanIsCalculatedThenItIsRejected(thrown);
    }

    @Test
    @DisplayName("Given a zero term When the plan is calculated Then it is rejected")
    void givenAZeroTermWhenThePlanIsCalculatedThenItIsRejected() {
        final var thrown = catchThrowable(() -> InstallmentPlanCalculator.price(BigDecimal.TEN, RATE, 0, START));

        verifyGivenAZeroTermWhenThePlanIsCalculatedThenItIsRejected(thrown);
    }

    private void verifyGivenAKnownCaseWhenThePricePlanIsCalculatedThenTheFixedInstallmentMatchesTheFormula(
            final List<Installment> actual) {
        assertThat(actual.get(0).amount()).isEqualByComparingTo("112.83");
        assertThat(actual.get(0).interest()).isEqualByComparingTo("50.00");
        assertThat(actual.get(0).principal()).isEqualByComparingTo("62.83");
    }

    private void verifyGivenAStartDateAtMonthEndWhenThePlanIsCalculatedThenDueDatesAreMonthlyFromTheReferenceDate(
            final List<Installment> actual) {
        assertThat(actual).extracting(Installment::dueDate)
                .containsExactly(LocalDate.of(2026, 2, 28), LocalDate.of(2026, 3, 31), LocalDate.of(2026, 4, 30));
    }

    private void verifyGivenAZeroRateWhenThePlanIsCalculatedThenInstallmentsSplitThePrincipalEvenly(
            final List<Installment> actual, final BigDecimal principal) {
        verifyAmortizesToZero(actual, principal, INTEGER_THREE);
        assertThat(actual).allSatisfy(i -> assertThat(i.interest()).isEqualByComparingTo("0.00"));
    }

    private void verifyGivenAZeroPrincipalWhenThePlanIsCalculatedThenItIsRejected(final Throwable actual) {
        assertThat(actual).isInstanceOf(IllegalArgumentException.class);
    }

    private void verifyGivenAZeroTermWhenThePlanIsCalculatedThenItIsRejected(final Throwable actual) {
        assertThat(actual).isInstanceOf(IllegalArgumentException.class);
    }

    private void verifyAmortizesToZero(final List<Installment> plan, final BigDecimal principal, final int term) {
        assertThat(plan).hasSize(term);
        assertThat(plan.stream().map(Installment::principal).reduce(BigDecimal.ZERO, BigDecimal::add))
                .isEqualByComparingTo(principal);
        assertThat(plan.get(term - 1).balance()).isEqualByComparingTo("0.00");
        assertThat(plan).allSatisfy(i -> assertThat(i.amount())
                .isEqualByComparingTo(i.principal().add(i.interest())));
    }
}

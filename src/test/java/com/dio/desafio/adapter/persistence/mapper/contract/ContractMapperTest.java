package com.dio.desafio.adapter.persistence.mapper.contract;

import com.dio.desafio.domain.contract.Contract;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.dio.desafio.support.domain.ContractSupport.defaultContract;
import static com.dio.desafio.support.domain.ContractSupport.defaultLegalContract;
import static org.assertj.core.api.Assertions.assertThat;

class ContractMapperTest {

    private final ContractMapper mapper = new ContractMapper();

    @Test
    @DisplayName("Given a PF contract When mapped to persistence and back Then every field is retained")
    void givenPfContractWhenMappedToPersistenceAndBackThenEveryFieldIsRetained() {
        final var expected = defaultContract().create();

        final var actual = mapper.toDomain(mapper.toEntity(expected));

        verifyGivenPfContractWhenMappedToPersistenceAndBackThenEveryFieldIsRetained(actual, expected);
    }

    @Test
    @DisplayName("Given a PJ contract When mapped to persistence and back Then every field is retained")
    void givenPjContractWhenMappedToPersistenceAndBackThenEveryFieldIsRetained() {
        final var expected = defaultLegalContract().create();

        final var actual = mapper.toDomain(mapper.toEntity(expected));

        verifyGivenPjContractWhenMappedToPersistenceAndBackThenEveryFieldIsRetained(actual, expected);
    }

    private void verifyGivenPfContractWhenMappedToPersistenceAndBackThenEveryFieldIsRetained(
            final Contract actual, final Contract expected) {
        assertThat(actual).isEqualTo(expected);
    }

    private void verifyGivenPjContractWhenMappedToPersistenceAndBackThenEveryFieldIsRetained(
            final Contract actual, final Contract expected) {
        assertThat(actual).isEqualTo(expected);
    }
}

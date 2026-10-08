package com.dio.desafio.adapter.persistence.adapter;

import com.dio.desafio.adapter.persistence.entity.contract.ContractEntity;
import com.dio.desafio.adapter.persistence.mapper.contract.ContractMapper;
import com.dio.desafio.adapter.persistence.repository.ContractJpaRepository;
import com.dio.desafio.application.exception.DuplicateContractException;
import com.dio.desafio.domain.contract.Contract;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import static com.dio.desafio.support.TestConstants.INTEGER_ONE;
import static com.dio.desafio.support.domain.ContractSupport.defaultContract;
import static com.dio.desafio.support.entity.ContractEntitySupport.defaultContractEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ContractPersistenceAdapterTest {

    private ContractJpaRepository repository;
    private ContractMapper mapper;
    private ContractPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(ContractJpaRepository.class);
        mapper = mock(ContractMapper.class);
        adapter = new ContractPersistenceAdapter(repository, mapper);
    }

    @Test
    @DisplayName("Given a contract When saved Then it is mapped to an entity, flushed and mapped back to the domain")
    void givenContractWhenSavedThenItIsMappedFlushedAndMappedBack() {
        final var contract = defaultContract().create();
        final var entity = defaultContractEntity().create();
        final var savedEntity = defaultContractEntity().create();
        final var expected = defaultContract().create();
        setupGivenContractWhenSavedThenItIsMappedFlushedAndMappedBack(contract, entity, savedEntity, expected);

        final var result = adapter.save(contract);

        verifyGivenContractWhenSavedThenItIsMappedFlushedAndMappedBack(result, expected, entity);
    }

    @Test
    @DisplayName("Given a proposal already contracted When a second contract is saved Then a DuplicateContractException is thrown")
    void givenProposalAlreadyContractedWhenSecondContractIsSavedThenDuplicateContractExceptionIsThrown() {
        final var contract = defaultContract().create();
        final var entity = defaultContractEntity().create();
        final var violation = new DataIntegrityViolationException("unique proposal_id");
        setupGivenProposalAlreadyContractedWhenSecondContractIsSavedThenDuplicateContractExceptionIsThrown(
                contract, entity, violation);

        final var thrown = catchThrowable(() -> adapter.save(contract));

        verifyGivenProposalAlreadyContractedWhenSecondContractIsSavedThenDuplicateContractExceptionIsThrown(
                thrown, violation);
    }

    @Test
    @DisplayName("Given an unexpected repository failure When a contract is saved Then an IllegalStateException wraps the cause")
    void givenUnexpectedRepositoryFailureWhenContractIsSavedThenIllegalStateExceptionWrapsCause() {
        final var contract = defaultContract().create();
        final var entity = defaultContractEntity().create();
        final var failure = new RuntimeException("database down");
        setupGivenUnexpectedRepositoryFailureWhenContractIsSavedThenIllegalStateExceptionWrapsCause(
                contract, entity, failure);

        final var thrown = catchThrowable(() -> adapter.save(contract));

        verifyGivenUnexpectedRepositoryFailureWhenContractIsSavedThenIllegalStateExceptionWrapsCause(thrown, failure);
    }

    @Test
    @DisplayName("Given an existing id When findById is called Then the stored entity is returned as a domain contract")
    void givenExistingIdWhenFindByIdIsCalledThenStoredEntityIsReturnedAsDomainContract() {
        final var id = UUID.randomUUID();
        final var entity = defaultContractEntity().create();
        final var expected = defaultContract().create();
        setupGivenExistingIdWhenFindByIdIsCalledThenStoredEntityIsReturnedAsDomainContract(id, entity, expected);

        final var result = adapter.findById(id);

        verifyGivenExistingIdWhenFindByIdIsCalledThenStoredEntityIsReturnedAsDomainContract(result, expected);
    }

    @Test
    @DisplayName("Given an unknown id When findById is called Then an empty result is returned")
    void givenUnknownIdWhenFindByIdIsCalledThenEmptyResultIsReturned() {
        final var id = UUID.randomUUID();
        setupGivenUnknownIdWhenFindByIdIsCalledThenEmptyResultIsReturned(id);

        final var result = adapter.findById(id);

        verifyGivenUnknownIdWhenFindByIdIsCalledThenEmptyResultIsReturned(result);
    }

    @Test
    @DisplayName("Given a repository failure When findById is called Then an IllegalStateException wraps the cause")
    void givenRepositoryFailureWhenFindByIdIsCalledThenIllegalStateExceptionWrapsCause() {
        final var id = UUID.randomUUID();
        final var failure = new RuntimeException("database down");
        setupGivenRepositoryFailureWhenFindByIdIsCalledThenIllegalStateExceptionWrapsCause(id, failure);

        final var thrown = catchThrowable(() -> adapter.findById(id));

        verifyGivenRepositoryFailureWhenFindByIdIsCalledThenIllegalStateExceptionWrapsCause(thrown, failure);
    }

    private void setupGivenContractWhenSavedThenItIsMappedFlushedAndMappedBack(
            final Contract contract, final ContractEntity entity, final ContractEntity savedEntity,
            final Contract expected) {
        doReturn(entity).when(mapper).toEntity(contract);
        doReturn(savedEntity).when(repository).saveAndFlush(entity);
        doReturn(expected).when(mapper).toDomain(savedEntity);
    }

    private void verifyGivenContractWhenSavedThenItIsMappedFlushedAndMappedBack(
            final Contract actual, final Contract expected, final ContractEntity entity) {
        assertThat(actual).isSameAs(expected);
        verify(repository, times(INTEGER_ONE)).saveAndFlush(entity);
    }

    private void setupGivenProposalAlreadyContractedWhenSecondContractIsSavedThenDuplicateContractExceptionIsThrown(
            final Contract contract, final ContractEntity entity, final DataIntegrityViolationException violation) {
        doReturn(entity).when(mapper).toEntity(contract);
        doThrow(violation).when(repository).saveAndFlush(entity);
    }

    private void verifyGivenProposalAlreadyContractedWhenSecondContractIsSavedThenDuplicateContractExceptionIsThrown(
            final Throwable actual, final DataIntegrityViolationException expectedCause) {
        assertThat(actual).isInstanceOf(DuplicateContractException.class).hasCause(expectedCause);
    }

    private void setupGivenUnexpectedRepositoryFailureWhenContractIsSavedThenIllegalStateExceptionWrapsCause(
            final Contract contract, final ContractEntity entity, final RuntimeException failure) {
        doReturn(entity).when(mapper).toEntity(contract);
        doThrow(failure).when(repository).saveAndFlush(entity);
    }

    private void verifyGivenUnexpectedRepositoryFailureWhenContractIsSavedThenIllegalStateExceptionWrapsCause(
            final Throwable actual, final RuntimeException expectedCause) {
        assertThat(actual).isInstanceOf(IllegalStateException.class).hasCause(expectedCause);
    }

    private void setupGivenExistingIdWhenFindByIdIsCalledThenStoredEntityIsReturnedAsDomainContract(
            final UUID id, final ContractEntity entity, final Contract expected) {
        doReturn(Optional.of(entity)).when(repository).findById(id);
        doReturn(expected).when(mapper).toDomain(entity);
    }

    private void verifyGivenExistingIdWhenFindByIdIsCalledThenStoredEntityIsReturnedAsDomainContract(
            final Optional<Contract> actual, final Contract expected) {
        assertThat(actual).containsSame(expected);
    }

    private void setupGivenUnknownIdWhenFindByIdIsCalledThenEmptyResultIsReturned(final UUID id) {
        doReturn(Optional.empty()).when(repository).findById(id);
    }

    private void verifyGivenUnknownIdWhenFindByIdIsCalledThenEmptyResultIsReturned(
            final Optional<Contract> actual) {
        assertThat(actual).isEmpty();
    }

    private void setupGivenRepositoryFailureWhenFindByIdIsCalledThenIllegalStateExceptionWrapsCause(
            final UUID id, final RuntimeException failure) {
        doThrow(failure).when(repository).findById(id);
    }

    private void verifyGivenRepositoryFailureWhenFindByIdIsCalledThenIllegalStateExceptionWrapsCause(
            final Throwable actual, final RuntimeException expectedCause) {
        assertThat(actual).isInstanceOf(IllegalStateException.class).hasCause(expectedCause);
    }
}

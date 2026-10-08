package com.dio.desafio.adapter.persistence.adapter;

import com.dio.desafio.adapter.persistence.entity.proposal.ProposalEntity;
import com.dio.desafio.adapter.persistence.mapper.proposal.ProposalMapper;
import com.dio.desafio.adapter.persistence.repository.ProposalJpaRepository;
import com.dio.desafio.domain.proposal.Proposal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.dio.desafio.support.TestConstants.INTEGER_ONE;
import static com.dio.desafio.support.domain.ProposalSupport.defaultProposal;
import static com.dio.desafio.support.entity.ProposalEntitySupport.defaultProposalEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ProposalPersistenceAdapterTest {

    private ProposalJpaRepository repository;
    private ProposalMapper mapper;
    private ProposalPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(ProposalJpaRepository.class);
        mapper = mock(ProposalMapper.class);
        adapter = new ProposalPersistenceAdapter(repository, mapper);
    }

    @Test
    @DisplayName("Given a proposal When saved Then it is mapped to an entity, persisted and mapped back to the domain")
    void givenProposalWhenSavedThenItIsMappedPersistedAndMappedBack() {
        final var proposal = defaultProposal().create();
        final var entity = defaultProposalEntity().create();
        final var savedEntity = defaultProposalEntity().create();
        final var expected = defaultProposal().create();
        setupGivenProposalWhenSavedThenItIsMappedPersistedAndMappedBack(proposal, entity, savedEntity, expected);

        final var result = adapter.save(proposal);

        verifyGivenProposalWhenSavedThenItIsMappedPersistedAndMappedBack(result, expected, entity);
    }

    @Test
    @DisplayName("Given a repository failure When a proposal is saved Then an IllegalStateException wraps the cause")
    void givenRepositoryFailureWhenProposalIsSavedThenIllegalStateExceptionWrapsCause() {
        final var proposal = defaultProposal().create();
        final var entity = defaultProposalEntity().create();
        final var failure = new RuntimeException("database down");
        setupGivenRepositoryFailureWhenProposalIsSavedThenIllegalStateExceptionWrapsCause(proposal, entity, failure);

        final var thrown = catchThrowable(() -> adapter.save(proposal));

        verifyGivenRepositoryFailureWhenProposalIsSavedThenIllegalStateExceptionWrapsCause(thrown, failure);
    }

    @Test
    @DisplayName("Given an existing id When findById is called Then the stored entity is returned as a domain proposal")
    void givenExistingIdWhenFindByIdIsCalledThenStoredEntityIsReturnedAsDomainProposal() {
        final var id = UUID.randomUUID();
        final var entity = defaultProposalEntity().create();
        final var expected = defaultProposal().create();
        setupGivenExistingIdWhenFindByIdIsCalledThenStoredEntityIsReturnedAsDomainProposal(id, entity, expected);

        final var result = adapter.findById(id);

        verifyGivenExistingIdWhenFindByIdIsCalledThenStoredEntityIsReturnedAsDomainProposal(result, expected);
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

    private void setupGivenProposalWhenSavedThenItIsMappedPersistedAndMappedBack(
            final Proposal proposal, final ProposalEntity entity, final ProposalEntity savedEntity,
            final Proposal expected) {
        doReturn(entity).when(mapper).toEntity(proposal);
        doReturn(savedEntity).when(repository).save(entity);
        doReturn(expected).when(mapper).toDomain(savedEntity);
    }

    private void verifyGivenProposalWhenSavedThenItIsMappedPersistedAndMappedBack(
            final Proposal actual, final Proposal expected, final ProposalEntity entity) {
        assertThat(actual).isSameAs(expected);
        verify(repository, times(INTEGER_ONE)).save(entity);
    }

    private void setupGivenRepositoryFailureWhenProposalIsSavedThenIllegalStateExceptionWrapsCause(
            final Proposal proposal, final ProposalEntity entity, final RuntimeException failure) {
        doReturn(entity).when(mapper).toEntity(proposal);
        doThrow(failure).when(repository).save(entity);
    }

    private void verifyGivenRepositoryFailureWhenProposalIsSavedThenIllegalStateExceptionWrapsCause(
            final Throwable actual, final RuntimeException expectedCause) {
        assertThat(actual).isInstanceOf(IllegalStateException.class).hasCause(expectedCause);
    }

    private void setupGivenExistingIdWhenFindByIdIsCalledThenStoredEntityIsReturnedAsDomainProposal(
            final UUID id, final ProposalEntity entity, final Proposal expected) {
        doReturn(Optional.of(entity)).when(repository).findById(id);
        doReturn(expected).when(mapper).toDomain(entity);
    }

    private void verifyGivenExistingIdWhenFindByIdIsCalledThenStoredEntityIsReturnedAsDomainProposal(
            final Optional<Proposal> actual, final Proposal expected) {
        assertThat(actual).containsSame(expected);
    }

    private void setupGivenUnknownIdWhenFindByIdIsCalledThenEmptyResultIsReturned(final UUID id) {
        doReturn(Optional.empty()).when(repository).findById(id);
    }

    private void verifyGivenUnknownIdWhenFindByIdIsCalledThenEmptyResultIsReturned(
            final Optional<Proposal> actual) {
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

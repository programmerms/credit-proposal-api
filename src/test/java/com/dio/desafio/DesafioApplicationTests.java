package com.dio.desafio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestConstructor;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class DesafioApplicationTests {

    private final ApplicationContext context;

    DesafioApplicationTests(final ApplicationContext context) {
        this.context = context;
    }

    @Test
    @DisplayName("Given the application When the Spring context starts Then it loads with the adapters wired")
    void givenTheApplicationWhenTheSpringContextStartsThenItLoadsWithTheAdaptersWired() {
        final var result = context.getBeanNamesForType(com.dio.desafio.application.port.out.ProposalPersistencePort.class);

        verifyGivenTheApplicationWhenTheSpringContextStartsThenItLoadsWithTheAdaptersWired(result);
    }

    private void verifyGivenTheApplicationWhenTheSpringContextStartsThenItLoadsWithTheAdaptersWired(
            final String[] actual) {
        assertThat(actual).hasSize(1);
    }
}

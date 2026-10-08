package com.dio.desafio.adapter.web.in.proposal;

import com.dio.desafio.adapter.persistence.repository.ProposalJpaRepository;
import com.dio.desafio.adapter.web.in.proposal.dto.CriarPropostaRequest;
import com.jayway.jsonpath.JsonPath;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;
import static com.dio.desafio.support.TestConstants.CPF;
import static com.dio.desafio.support.dto.CriarPropostaRequestSupport.defaultCriarPropostaRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ProposalApiIntegrationTest {

    private final MockMvc mvc;
    private final ProposalJpaRepository repository;
    private final JsonMapper jsonMapper;

    ProposalApiIntegrationTest(final WebApplicationContext context, final ProposalJpaRepository repository,
                               final JsonMapper jsonMapper) {
        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
        this.repository = repository;
        this.jsonMapper = jsonMapper;
    }

    @Test
    @DisplayName("Given a valid CPF proposal request When posted Then matching rate is persisted and returned")
    void givenValidCpfProposalRequestWhenPostedThenMatchingRateIsPersistedAndReturned() throws Exception {
        final var request = defaultCriarPropostaRequest().set(field(CriarPropostaRequest::documento), "529.982.247-25")
                .set(field(CriarPropostaRequest::valor), new BigDecimal("2500.00"))
                .set(field(CriarPropostaRequest::prazoMeses), 12).create();

        final var result = postRequest(request);

        verifyGivenValidCpfProposalRequestWhenPostedThenMatchingRateIsPersistedAndReturned(result);
    }

    @Test
    @DisplayName("Given a product exclusive to PJ When a CPF request uses it Then the API rejects it centrally")
    void givenProductExclusiveToPjWhenCpfRequestUsesItThenTheApiRejectsItCentrally() throws Exception {
        final var request = defaultCriarPropostaRequest().set(field(CriarPropostaRequest::produto), "CAPITAL_GIRO")
                .create();

        final var result = postRequest(request);

        verifyGivenProductExclusiveToPjWhenCpfRequestUsesItThenTheApiRejectsItCentrally(result);
    }

    @Test
    @DisplayName("Given a zero amount When posted Then the API returns centralized field validation in Portuguese")
    void givenZeroAmountWhenPostedThenTheApiReturnsCentralizedFieldValidationInPortuguese() throws Exception {
        final var request = defaultCriarPropostaRequest().set(field(CriarPropostaRequest::valor), BigDecimal.ZERO)
                .create();

        final var result = postRequest(request);

        verifyGivenZeroAmountWhenPostedThenTheApiReturnsCentralizedFieldValidationInPortuguese(result);
    }

    @Test
    @DisplayName("Given a term above the maximum When posted Then the API rejects the prazoMeses field")
    void givenTermAboveTheMaximumWhenPostedThenTheApiRejectsThePrazoMesesField() throws Exception {
        final var request = defaultCriarPropostaRequest().set(field(CriarPropostaRequest::prazoMeses), 1_000_000)
                .create();

        final var result = postRequest(request);

        verifyGivenTermAboveTheMaximumWhenPostedThenTheApiRejectsThePrazoMesesField(result);
    }

    @Test
    @DisplayName("Given an invalid document When posted Then the API reports the documento field in Portuguese")
    void givenInvalidDocumentWhenPostedThenTheApiReportsTheDocumentoFieldInPortuguese() throws Exception {
        final var request = defaultCriarPropostaRequest().set(field(CriarPropostaRequest::documento), "111.111.111-11")
                .create();

        final var result = postRequest(request);

        verifyGivenInvalidDocumentWhenPostedThenTheApiReportsTheDocumentoFieldInPortuguese(result);
    }

    @Test
    @DisplayName("Given a created proposal When fetched by number Then the same data is returned")
    void givenCreatedProposalWhenFetchedByNumberThenTheSameDataIsReturned() throws Exception {
        final var numero = setupGivenCreatedProposalWhenFetchedByNumber();

        final var result = mvc.perform(get("/api/propostas/" + numero));

        verifyGivenCreatedProposalWhenFetchedByNumber(result, numero);
    }

    @Test
    @DisplayName("Given an unknown number When fetched Then the API returns 404")
    void givenUnknownNumberWhenFetchedThenTheApiReturns404() throws Exception {
        final var unknownNumber = UUID.randomUUID();

        final var result = mvc.perform(get("/api/propostas/" + unknownNumber));

        verifyGivenUnknownNumberWhenFetchedThenTheApiReturns404(result);
    }

    @Test
    @DisplayName("Given a malformed number When fetched Then the API returns 400")
    void givenMalformedNumberWhenFetchedThenTheApiReturns400() throws Exception {
        final var malformedNumber = "not-a-uuid";

        final var result = mvc.perform(get("/api/propostas/" + malformedNumber));

        verifyGivenMalformedNumberWhenFetchedThenTheApiReturns400(result);
    }

    @Test
    @DisplayName("Given an unmapped path When requested Then the API answers 404 instead of 500")
    void givenUnmappedPathWhenRequestedThenTheApiAnswers404InsteadOf500() throws Exception {
        final var unmappedPath = "/does-not-exist";

        final var result = mvc.perform(get(unmappedPath));

        verifyGivenUnmappedPathWhenRequestedThenTheApiAnswers404InsteadOf500(result);
    }

    private ResultActions postRequest(final CriarPropostaRequest request) throws Exception {
        return mvc.perform(post("/api/propostas").contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)));
    }

    private String setupGivenCreatedProposalWhenFetchedByNumber() throws Exception {
        final var request = defaultCriarPropostaRequest().set(field(CriarPropostaRequest::prazoMeses), 2).create();
        final var body = postRequest(request).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.numero");
    }

    private void verifyGivenValidCpfProposalRequestWhenPostedThenMatchingRateIsPersistedAndReturned(
            final ResultActions actual) throws Exception {
        final var body = actual
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documento").value(CPF))
                .andExpect(jsonPath("$.descricao").value("Crédito Pessoal"))
                .andExpect(jsonPath("$.taxaMensal").value(0.05))
                .andExpect(jsonPath("$.status").value("GERADA"))
                .andExpect(jsonPath("$.parcelas.length()").value(12))
                .andExpect(jsonPath("$.parcelas[0].numero").value(1))
                .andReturn().getResponse().getContentAsString();
        assertThat(body).doesNotContain("personType").doesNotContain("createdAt");
        assertThat(repository.findAll()).anySatisfy(saved -> {
            assertThat(saved.getDocument()).isEqualTo(CPF);
            assertThat(saved.getMonthlyRate()).isEqualByComparingTo("0.05");
        });
    }

    private void verifyGivenProductExclusiveToPjWhenCpfRequestUsesItThenTheApiRejectsItCentrally(
            final ResultActions actual) throws Exception {
        actual.andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem").value("Produto não disponível para o tipo de pessoa PF"))
                .andExpect(jsonPath("$.codigo").doesNotExist())
                .andExpect(jsonPath("$.caminho").doesNotExist());
    }

    private void verifyGivenZeroAmountWhenPostedThenTheApiReturnsCentralizedFieldValidationInPortuguese(
            final ResultActions actual) throws Exception {
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Falha na validação da requisição"))
                .andExpect(jsonPath("$.errosCampos[0].campo").value("valor"));
    }

    private void verifyGivenTermAboveTheMaximumWhenPostedThenTheApiRejectsThePrazoMesesField(
            final ResultActions actual) throws Exception {
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errosCampos[0].campo").value("prazoMeses"));
    }

    private void verifyGivenInvalidDocumentWhenPostedThenTheApiReportsTheDocumentoFieldInPortuguese(
            final ResultActions actual) throws Exception {
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errosCampos[0].campo").value("documento"))
                .andExpect(jsonPath("$.errosCampos[0].motivo").value("documento deve ser um CPF ou CNPJ válido"));
    }

    private void verifyGivenCreatedProposalWhenFetchedByNumber(
            final ResultActions actual, final String numero) throws Exception {
        actual.andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(numero))
                .andExpect(jsonPath("$.parcelas.length()").value(2));
    }

    private void verifyGivenUnknownNumberWhenFetchedThenTheApiReturns404(final ResultActions actual) throws Exception {
        actual.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Proposta não encontrada"));
    }

    private void verifyGivenMalformedNumberWhenFetchedThenTheApiReturns400(final ResultActions actual)
            throws Exception {
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errosCampos[0].campo").value("numero"));
    }

    private void verifyGivenUnmappedPathWhenRequestedThenTheApiAnswers404InsteadOf500(final ResultActions actual)
            throws Exception {
        actual.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Recurso não encontrado"));
    }
}

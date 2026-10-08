package com.dio.desafio.adapter.web.in.contract;

import com.dio.desafio.adapter.web.in.contract.dto.ClienteDto;
import com.dio.desafio.adapter.web.in.contract.dto.ContatoDto;
import com.dio.desafio.adapter.web.in.contract.dto.ContratarPropostaRequest;
import com.dio.desafio.adapter.web.in.contract.dto.EnderecoDto;
import com.dio.desafio.adapter.web.in.proposal.dto.CriarPropostaRequest;
import com.jayway.jsonpath.JsonPath;
import java.util.List;
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
import tools.jackson.databind.node.ObjectNode;
import static com.dio.desafio.support.TestConstants.CNPJ;
import static com.dio.desafio.support.TestConstants.CPF;
import static com.dio.desafio.support.dto.ClienteDtoSupport.defaultClienteDto;
import static com.dio.desafio.support.dto.ClienteDtoSupport.defaultLegalClienteDto;
import static com.dio.desafio.support.dto.ContatoDtoSupport.defaultContatoDto;
import static com.dio.desafio.support.dto.ContratarPropostaRequestSupport.defaultContratarPropostaRequest;
import static com.dio.desafio.support.dto.CriarPropostaRequestSupport.defaultCriarPropostaRequest;
import static com.dio.desafio.support.dto.EnderecoDtoSupport.defaultEnderecoDto;
import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ContractApiIntegrationTest {

    private final MockMvc mvc;
    private final JsonMapper jsonMapper;

    ContractApiIntegrationTest(final WebApplicationContext context, final JsonMapper jsonMapper) {
        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
        this.jsonMapper = jsonMapper;
    }

    @Test
    @DisplayName("Given a generated PF proposal When it is contracted Then the contract is created and the proposal becomes CONTRATADA")
    void givenGeneratedPfProposalWhenItIsContractedThenContractIsCreatedAndProposalBecomesContracted()
            throws Exception {
        final var numero = setupGivenGeneratedProposal(defaultCriarPropostaRequest().create());
        final var request = defaultContratarPropostaRequest()
                .set(field(ContratarPropostaRequest::numeroProposta), UUID.fromString(numero)).create();

        final var result = postContract(request);

        verifyGivenGeneratedPfProposalWhenItIsContractedThenContractIsCreatedAndProposalBecomesContracted(
                result, numero);
    }

    @Test
    @DisplayName("Given a generated PJ proposal When it is contracted Then the legal person is stored")
    void givenGeneratedPjProposalWhenItIsContractedThenTheLegalPersonIsStored() throws Exception {
        final var numero = setupGivenGeneratedProposal(defaultCriarPropostaRequest()
                .set(field(CriarPropostaRequest::documento), "11.222.333/0001-81")
                .set(field(CriarPropostaRequest::produto), "CAPITAL_GIRO").create());
        final var request = defaultContratarPropostaRequest()
                .set(field(ContratarPropostaRequest::numeroProposta), UUID.fromString(numero))
                .set(field(ContratarPropostaRequest::cliente), defaultLegalClienteDto().create()).create();

        final var result = postContract(request);

        verifyGivenGeneratedPjProposalWhenItIsContractedThenTheLegalPersonIsStored(result);
    }

    @Test
    @DisplayName("Given a contracted proposal When it is contracted again Then the API answers 409")
    void givenContractedProposalWhenItIsContractedAgainThenTheApiAnswers409() throws Exception {
        final var numero = setupGivenGeneratedProposal(defaultCriarPropostaRequest().create());
        final var request = defaultContratarPropostaRequest()
                .set(field(ContratarPropostaRequest::numeroProposta), UUID.fromString(numero)).create();
        setupGivenContractedProposal(request);

        final var result = postContract(request);

        verifyGivenContractedProposalWhenItIsContractedAgainThenTheApiAnswers409(result);
    }

    @Test
    @DisplayName("Given a PF proposal When contracting with PJ customer data Then the proposal type wins and PF fields are required")
    void givenPfProposalWhenContractingWithPjCustomerDataThenProposalTypeWinsAndPfFieldsAreRequired()
            throws Exception {
        final var numero = setupGivenGeneratedProposal(defaultCriarPropostaRequest().create());
        final var request = defaultContratarPropostaRequest()
                .set(field(ContratarPropostaRequest::numeroProposta), UUID.fromString(numero))
                .set(field(ContratarPropostaRequest::cliente), defaultLegalClienteDto().create()).create();

        final var result = postContract(request);

        verifyGivenPfProposalWhenContractingWithPjCustomerDataThenPfFieldsAreRequired(result);
    }

    @Test
    @DisplayName("Given a document sent in the contracting body When contracting Then it is ignored and the proposal document is used")
    void givenDocumentSentInTheContractingBodyWhenContractingThenItIsIgnoredAndTheProposalDocumentIsUsed()
            throws Exception {
        final var numero = setupGivenGeneratedProposal(defaultCriarPropostaRequest().create());
        final var request = defaultContratarPropostaRequest()
                .set(field(ContratarPropostaRequest::numeroProposta), UUID.fromString(numero)).create();
        final var body = (ObjectNode) jsonMapper.valueToTree(request);
        body.put("documento", CNPJ);

        final var result = mvc.perform(post("/api/contratacoes").contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(body)));

        verifyGivenDocumentSentInTheContractingBodyWhenContractingThenProposalDocumentIsUsed(result);
    }

    @Test
    @DisplayName("Given an unknown proposal number When contracting Then the API answers 404")
    void givenUnknownProposalNumberWhenContractingThenTheApiAnswers404() throws Exception {
        final var request = defaultContratarPropostaRequest().create();

        final var result = postContract(request);

        verifyGivenUnknownProposalNumberWhenContractingThenTheApiAnswers404(result);
    }

    @Test
    @DisplayName("Given missing required address fields When contracting Then the API lists the Portuguese field errors")
    void givenMissingRequiredAddressFieldsWhenContractingThenTheApiListsThePortugueseFieldErrors()
            throws Exception {
        final var endereco = defaultEnderecoDto().set(field(EnderecoDto::cep), null)
                .set(field(EnderecoDto::cidade), null).create();
        final var contato = defaultContatoDto().set(field(ContatoDto::email), "x")
                .set(field(ContatoDto::telefones), List.of()).create();
        final var request = defaultContratarPropostaRequest()
                .set(field(ContratarPropostaRequest::endereco), endereco)
                .set(field(ContratarPropostaRequest::contato), contato).create();

        final var result = postContract(request);

        verifyGivenMissingRequiredAddressFieldsWhenContractingThenTheApiListsThePortugueseFieldErrors(result);
    }

    @Test
    @DisplayName("Given a PF proposal with an empty customer block When contracting Then all missing customer fields are returned at once")
    void givenPfProposalWithAnEmptyCustomerBlockWhenContractingThenAllMissingCustomerFieldsAreReturnedAtOnce()
            throws Exception {
        final var numero = setupGivenGeneratedProposal(defaultCriarPropostaRequest().create());
        final var emptyCliente = defaultClienteDto().set(field(ClienteDto::nome), null)
                .set(field(ClienteDto::dataNascimento), null).set(field(ClienteDto::sexo), null)
                .set(field(ClienteDto::estadoCivil), null).create();
        final var request = defaultContratarPropostaRequest()
                .set(field(ContratarPropostaRequest::numeroProposta), UUID.fromString(numero))
                .set(field(ContratarPropostaRequest::cliente), emptyCliente).create();

        final var result = postContract(request);

        verifyGivenPfProposalWithAnEmptyCustomerBlockWhenContractingThenAllMissingCustomerFieldsAreReturned(result);
    }

    private ResultActions postContract(final ContratarPropostaRequest request) throws Exception {
        return mvc.perform(post("/api/contratacoes").contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)));
    }

    private String setupGivenGeneratedProposal(final CriarPropostaRequest request) throws Exception {
        final var body = mvc.perform(post("/api/propostas").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.numero");
    }

    private void setupGivenContractedProposal(final ContratarPropostaRequest request) throws Exception {
        postContract(request).andExpect(status().isCreated());
    }

    private void verifyGivenGeneratedPfProposalWhenItIsContractedThenContractIsCreatedAndProposalBecomesContracted(
            final ResultActions actual, final String numero) throws Exception {
        final var body = actual
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroProposta").value(numero))
                .andExpect(jsonPath("$.documento").value(CPF))
                .andExpect(jsonPath("$.tipoPessoa").value("PF"))
                .andExpect(jsonPath("$.cliente.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.endereco.cidade").value("São Paulo"))
                .andExpect(jsonPath("$.contato.telefones[0].tipo").value("CELULAR"))
                .andReturn().getResponse().getContentAsString();
        final String contratacao = JsonPath.read(body, "$.numero");
        mvc.perform(get("/api/propostas/" + numero))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CONTRATADA"));
        mvc.perform(get("/api/contratacoes/" + contratacao))
                .andExpect(status().isOk()).andExpect(jsonPath("$.numero").value(contratacao));
    }

    private void verifyGivenGeneratedPjProposalWhenItIsContractedThenTheLegalPersonIsStored(
            final ResultActions actual) throws Exception {
        actual.andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoPessoa").value("PJ"))
                .andExpect(jsonPath("$.cliente.razaoSocial").value("Empresa Ltda"));
    }

    private void verifyGivenContractedProposalWhenItIsContractedAgainThenTheApiAnswers409(
            final ResultActions actual) throws Exception {
        actual.andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("A proposta já foi contratada"));
    }

    private void verifyGivenPfProposalWhenContractingWithPjCustomerDataThenPfFieldsAreRequired(
            final ResultActions actual) throws Exception {
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errosCampos[?(@.campo=='cliente.nome')]").exists());
    }

    private void verifyGivenDocumentSentInTheContractingBodyWhenContractingThenProposalDocumentIsUsed(
            final ResultActions actual) throws Exception {
        actual.andExpect(status().isCreated())
                .andExpect(jsonPath("$.documento").value(CPF))
                .andExpect(jsonPath("$.tipoPessoa").value("PF"));
    }

    private void verifyGivenUnknownProposalNumberWhenContractingThenTheApiAnswers404(
            final ResultActions actual) throws Exception {
        actual.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Proposta não encontrada"));
    }

    private void verifyGivenMissingRequiredAddressFieldsWhenContractingThenTheApiListsThePortugueseFieldErrors(
            final ResultActions actual) throws Exception {
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Falha na validação da requisição"))
                .andExpect(jsonPath("$.errosCampos[?(@.campo=='endereco.cep')].motivo").value("é obrigatório"))
                .andExpect(jsonPath("$.errosCampos[?(@.campo=='contato.email')].motivo")
                        .value("deve ser um e-mail válido"));
    }

    private void verifyGivenPfProposalWithAnEmptyCustomerBlockWhenContractingThenAllMissingCustomerFieldsAreReturned(
            final ResultActions actual) throws Exception {
        actual.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errosCampos.length()").value(4))
                .andExpect(jsonPath("$.errosCampos[?(@.campo=='cliente.nome')]").exists())
                .andExpect(jsonPath("$.errosCampos[?(@.campo=='cliente.estadoCivil')]").exists());
    }
}

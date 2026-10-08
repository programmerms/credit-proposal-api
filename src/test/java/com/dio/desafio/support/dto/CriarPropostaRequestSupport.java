package com.dio.desafio.support.dto;

import com.dio.desafio.adapter.web.in.proposal.dto.CriarPropostaRequest;
import java.math.BigDecimal;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static com.dio.desafio.support.TestConstants.CPF;
import static org.instancio.Select.field;

public final class CriarPropostaRequestSupport {

    private CriarPropostaRequestSupport() { }

    public static InstancioApi<CriarPropostaRequest> defaultCriarPropostaRequest() {
        return Instancio.of(CriarPropostaRequest.class)
                .set(field(CriarPropostaRequest::documento), CPF)
                .set(field(CriarPropostaRequest::produto), "CREDITO_PESSOAL")
                .set(field(CriarPropostaRequest::valor), new BigDecimal("200.00"))
                .set(field(CriarPropostaRequest::prazoMeses), 10);
    }
}

package com.dio.desafio.support.dto;

import com.dio.desafio.adapter.web.in.proposal.dto.PropostaResponse;
import com.dio.desafio.domain.ProposalStatus;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static org.instancio.Select.field;

public final class PropostaResponseSupport {

    private PropostaResponseSupport() { }

    public static InstancioApi<PropostaResponse> defaultPropostaResponse() {
        return Instancio.of(PropostaResponse.class)
                .set(field(PropostaResponse::descricao), "Crédito Pessoal")
                .set(field(PropostaResponse::status), ProposalStatus.GERADA);
    }
}

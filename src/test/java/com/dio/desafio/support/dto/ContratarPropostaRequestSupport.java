package com.dio.desafio.support.dto;

import com.dio.desafio.adapter.web.in.contract.dto.ContratarPropostaRequest;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static com.dio.desafio.support.dto.ClienteDtoSupport.defaultClienteDto;
import static com.dio.desafio.support.dto.ContatoDtoSupport.defaultContatoDto;
import static com.dio.desafio.support.dto.EnderecoDtoSupport.defaultEnderecoDto;
import static org.instancio.Select.field;

public final class ContratarPropostaRequestSupport {

    private ContratarPropostaRequestSupport() { }

    public static InstancioApi<ContratarPropostaRequest> defaultContratarPropostaRequest() {
        return Instancio.of(ContratarPropostaRequest.class)
                .supply(field(ContratarPropostaRequest::cliente), () -> defaultClienteDto().create())
                .supply(field(ContratarPropostaRequest::endereco), () -> defaultEnderecoDto().create())
                .supply(field(ContratarPropostaRequest::contato), () -> defaultContatoDto().create());
    }
}

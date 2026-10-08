package com.dio.desafio.support.dto;

import com.dio.desafio.adapter.web.in.contract.dto.ContratacaoResponse;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static com.dio.desafio.support.dto.ClienteDtoSupport.defaultClienteDto;
import static com.dio.desafio.support.dto.ContatoDtoSupport.defaultContatoDto;
import static com.dio.desafio.support.dto.EnderecoDtoSupport.defaultEnderecoDto;
import static org.instancio.Select.field;

public final class ContratacaoResponseSupport {

    private ContratacaoResponseSupport() { }

    public static InstancioApi<ContratacaoResponse> defaultContratacaoResponse() {
        return Instancio.of(ContratacaoResponse.class)
                .set(field(ContratacaoResponse::tipoPessoa), "PF")
                .supply(field(ContratacaoResponse::cliente), () -> defaultClienteDto().create())
                .supply(field(ContratacaoResponse::endereco), () -> defaultEnderecoDto().create())
                .supply(field(ContratacaoResponse::contato), () -> defaultContatoDto().create());
    }
}

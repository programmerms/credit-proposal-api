package com.dio.desafio.support.dto;

import com.dio.desafio.adapter.web.in.contract.dto.EnderecoDto;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static org.instancio.Select.field;

public final class EnderecoDtoSupport {

    private EnderecoDtoSupport() { }

    public static InstancioApi<EnderecoDto> defaultEnderecoDto() {
        return Instancio.of(EnderecoDto.class)
                .set(field(EnderecoDto::tipo), "RESIDENCIAL")
                .set(field(EnderecoDto::logradouro), "Rua A")
                .set(field(EnderecoDto::numero), "10")
                .set(field(EnderecoDto::cep), "01001000")
                .set(field(EnderecoDto::cidade), "São Paulo")
                .set(field(EnderecoDto::estado), "SP");
    }
}

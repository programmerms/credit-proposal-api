package com.dio.desafio.support.dto;

import com.dio.desafio.adapter.web.in.contract.dto.ContatoDto;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static com.dio.desafio.support.dto.TelefoneDtoSupport.defaultTelefoneDto;
import static org.instancio.Select.field;

public final class ContatoDtoSupport {

    private ContatoDtoSupport() { }

    public static InstancioApi<ContatoDto> defaultContatoDto() {
        return Instancio.of(ContatoDto.class)
                .set(field(ContatoDto::email), "cliente@email.com")
                .supply(field(ContatoDto::telefones), () -> List.of(defaultTelefoneDto().create()));
    }
}

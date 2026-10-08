package com.dio.desafio.support.dto;

import com.dio.desafio.adapter.web.in.contract.dto.TelefoneDto;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static org.instancio.Select.field;

public final class TelefoneDtoSupport {

    private TelefoneDtoSupport() { }

    public static InstancioApi<TelefoneDto> defaultTelefoneDto() {
        return Instancio.of(TelefoneDto.class)
                .set(field(TelefoneDto::tipo), "CELULAR")
                .set(field(TelefoneDto::ddd), "11")
                .set(field(TelefoneDto::numero), "999990000");
    }
}

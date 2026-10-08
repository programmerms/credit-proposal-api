package com.dio.desafio.support.dto;

import com.dio.desafio.adapter.web.in.contract.dto.ClienteDto;
import java.time.LocalDate;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import static org.instancio.Select.field;

public final class ClienteDtoSupport {

    private ClienteDtoSupport() { }

    /** Natural person (PF): PJ-only fields are null. */
    public static InstancioApi<ClienteDto> defaultClienteDto() {
        return Instancio.of(ClienteDto.class)
                .set(field(ClienteDto::nome), "Maria Silva")
                .set(field(ClienteDto::dataNascimento), LocalDate.of(1990, 5, 20))
                .set(field(ClienteDto::sexo), "FEMININO")
                .set(field(ClienteDto::estadoCivil), "SOLTEIRO")
                .set(field(ClienteDto::razaoSocial), null)
                .set(field(ClienteDto::nomeFantasia), null);
    }

    /** Legal person (PJ): PF-only fields are null. */
    public static InstancioApi<ClienteDto> defaultLegalClienteDto() {
        return Instancio.of(ClienteDto.class)
                .set(field(ClienteDto::nome), null)
                .set(field(ClienteDto::dataNascimento), null)
                .set(field(ClienteDto::sexo), null)
                .set(field(ClienteDto::estadoCivil), null)
                .set(field(ClienteDto::razaoSocial), "Empresa Ltda")
                .set(field(ClienteDto::nomeFantasia), "Empresa");
    }
}

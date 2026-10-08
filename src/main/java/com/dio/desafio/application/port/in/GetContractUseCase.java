package com.dio.desafio.application.port.in;

import com.dio.desafio.domain.contract.Contract;
import java.util.UUID;

public interface GetContractUseCase {
    Contract get(UUID id);
}

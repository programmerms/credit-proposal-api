package com.dio.desafio.adapter.web.in.contract;

import com.dio.desafio.adapter.web.in.contract.dto.ContratacaoResponse;
import com.dio.desafio.adapter.web.in.contract.dto.ContratarPropostaRequest;
import com.dio.desafio.adapter.web.in.contract.mapper.ContractWebMapper;
import com.dio.desafio.application.port.in.ContractProposalUseCase;
import com.dio.desafio.application.port.in.GetContractUseCase;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contratacoes")
public class ContractController {

    private final ContractProposalUseCase contractProposalUseCase;
    private final GetContractUseCase getContractUseCase;
    private final ContractWebMapper mapper;

    public ContractController(ContractProposalUseCase contractProposalUseCase,
                              GetContractUseCase getContractUseCase,
                              ContractWebMapper mapper) {
        this.contractProposalUseCase = contractProposalUseCase;
        this.getContractUseCase = getContractUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ContratacaoResponse> contract(@Valid @RequestBody ContratarPropostaRequest request) {
        var contract = contractProposalUseCase.contract(mapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(contract));
    }

    @GetMapping("/{numero}")
    public ContratacaoResponse get(@PathVariable UUID numero) {
        return mapper.toResponse(getContractUseCase.get(numero));
    }
}

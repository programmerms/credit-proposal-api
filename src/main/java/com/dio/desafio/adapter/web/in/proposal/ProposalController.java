package com.dio.desafio.adapter.web.in.proposal;

import com.dio.desafio.adapter.web.in.proposal.dto.CriarPropostaRequest;
import com.dio.desafio.adapter.web.in.proposal.dto.PropostaResponse;
import com.dio.desafio.adapter.web.in.proposal.mapper.ProposalWebMapper;
import com.dio.desafio.application.port.in.GenerateProposalUseCase;
import com.dio.desafio.application.port.in.GetProposalUseCase;
import com.dio.desafio.domain.proposal.Proposal;
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
@RequestMapping("/api/propostas")
public class ProposalController {

    private final GenerateProposalUseCase generateProposalUseCase;
    private final GetProposalUseCase getProposalUseCase;
    private final ProposalWebMapper mapper;

    public ProposalController(GenerateProposalUseCase generateProposalUseCase,
                              GetProposalUseCase getProposalUseCase,
                              ProposalWebMapper mapper) {
        this.generateProposalUseCase = generateProposalUseCase;
        this.getProposalUseCase = getProposalUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<PropostaResponse> create(@Valid @RequestBody CriarPropostaRequest request) {
        Proposal proposal = generateProposalUseCase.generate(
                request.documento(), request.produto(), request.valor(), request.prazoMeses());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(proposal));
    }

    @GetMapping("/{numero}")
    public PropostaResponse get(@PathVariable UUID numero) {
        return mapper.toResponse(getProposalUseCase.get(numero));
    }
}

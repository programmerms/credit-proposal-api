package com.dio.desafio.adapter.persistence.entity.proposal;

import com.dio.desafio.domain.PersonType;
import com.dio.desafio.domain.ProposalStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "credit_proposals")
public class ProposalEntity {

    @Id
    private UUID id;
    private Instant createdAt;
    private String document;

    @Enumerated(EnumType.STRING)
    private PersonType personType;

    private String productCode;
    private String productDescription;
    private BigDecimal amount;
    private int termMonths;
    private BigDecimal monthlyRate;

    @Enumerated(EnumType.STRING)
    private ProposalStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "credit_proposal_installments", joinColumns = @JoinColumn(name = "proposal_id"))
    @OrderBy("number ASC")
    private List<InstallmentEmbeddable> installments = new ArrayList<>();

    protected ProposalEntity() { }

    public ProposalEntity(UUID id, Instant createdAt, String document, PersonType personType,
                          String productCode, String productDescription, BigDecimal amount,
                          int termMonths, BigDecimal monthlyRate, ProposalStatus status,
                          List<InstallmentEmbeddable> installments) {
        this.id = id;
        this.createdAt = createdAt;
        this.document = document;
        this.personType = personType;
        this.productCode = productCode;
        this.productDescription = productDescription;
        this.amount = amount;
        this.termMonths = termMonths;
        this.monthlyRate = monthlyRate;
        this.status = status;
        this.installments = new ArrayList<>(installments);
    }

    public UUID getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }
    public String getDocument() { return document; }
    public PersonType getPersonType() { return personType; }
    public String getProductCode() { return productCode; }
    public String getProductDescription() { return productDescription; }
    public BigDecimal getAmount() { return amount; }
    public int getTermMonths() { return termMonths; }
    public BigDecimal getMonthlyRate() { return monthlyRate; }
    public ProposalStatus getStatus() { return status; }
    public List<InstallmentEmbeddable> getInstallments() { return installments; }
}

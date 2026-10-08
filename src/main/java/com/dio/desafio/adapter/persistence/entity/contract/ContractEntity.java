package com.dio.desafio.adapter.persistence.entity.contract;

import com.dio.desafio.domain.PersonType;
import com.dio.desafio.domain.proposal.person.enums.CivilStatusEnum;
import com.dio.desafio.domain.proposal.person.enums.GenderEnum;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Flat persistence model of a contract: PF-only and PJ-only columns are nullable; {@code proposal_id} is
 * unique so a proposal can be contracted only once.
 */
@Entity
@Table(name = "contracts")
public class ContractEntity {

    @Id
    private UUID id;
    @Column(name = "proposal_id", nullable = false, unique = true)
    private UUID proposalId;
    private Instant contractedAt;

    @Enumerated(EnumType.STRING)
    private PersonType personType;
    private String document;

    private String name;
    private LocalDate birthDate;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    @Enumerated(EnumType.STRING)
    private CivilStatusEnum civilStatus;

    private String businessName;
    private String tradeName;

    @Embedded
    private AddressEmbeddable address;
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "contract_phones", joinColumns = @JoinColumn(name = "contract_id"))
    private List<PhoneEmbeddable> phones = new ArrayList<>();

    protected ContractEntity() { }

    public ContractEntity(UUID id, UUID proposalId, Instant contractedAt, PersonType personType, String document,
                          String name, LocalDate birthDate, GenderEnum gender, CivilStatusEnum civilStatus,
                          String businessName, String tradeName, AddressEmbeddable address, String email,
                          List<PhoneEmbeddable> phones) {
        this.id = id;
        this.proposalId = proposalId;
        this.contractedAt = contractedAt;
        this.personType = personType;
        this.document = document;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.civilStatus = civilStatus;
        this.businessName = businessName;
        this.tradeName = tradeName;
        this.address = address;
        this.email = email;
        this.phones = new ArrayList<>(phones);
    }

    public UUID getId() { return id; }
    public UUID getProposalId() { return proposalId; }
    public Instant getContractedAt() { return contractedAt; }
    public PersonType getPersonType() { return personType; }
    public String getDocument() { return document; }
    public String getName() { return name; }
    public LocalDate getBirthDate() { return birthDate; }
    public GenderEnum getGender() { return gender; }
    public CivilStatusEnum getCivilStatus() { return civilStatus; }
    public String getBusinessName() { return businessName; }
    public String getTradeName() { return tradeName; }
    public AddressEmbeddable getAddress() { return address; }
    public String getEmail() { return email; }
    public List<PhoneEmbeddable> getPhones() { return phones; }
}

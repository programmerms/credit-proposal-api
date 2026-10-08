package com.dio.desafio.adapter.persistence.entity.contract;

import com.dio.desafio.domain.proposal.address.enums.AddressTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class AddressEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type")
    private AddressTypeEnum type;
    @Column(name = "street_name")
    private String streetName;
    @Column(name = "street_number")
    private String streetNumber;
    @Column(name = "postal_code")
    private String postalCode;
    @Column(name = "city")
    private String city;
    @Column(name = "address_state")
    private String state;

    protected AddressEmbeddable() { }

    public AddressEmbeddable(AddressTypeEnum type, String streetName, String streetNumber, String postalCode,
                             String city, String state) {
        this.type = type;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.postalCode = postalCode;
        this.city = city;
        this.state = state;
    }

    public AddressTypeEnum getType() { return type; }
    public String getStreetName() { return streetName; }
    public String getStreetNumber() { return streetNumber; }
    public String getPostalCode() { return postalCode; }
    public String getCity() { return city; }
    public String getState() { return state; }
}

package com.dio.desafio.adapter.persistence.entity.proposal;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
public class InstallmentEmbeddable {
    private int number;
    private LocalDate dueDate;
    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal amount;
    private BigDecimal balance;

    protected InstallmentEmbeddable() { }

    public InstallmentEmbeddable(int number, LocalDate dueDate, BigDecimal principal,
                                 BigDecimal interest, BigDecimal amount, BigDecimal balance) {
        this.number = number;
        this.dueDate = dueDate;
        this.principal = principal;
        this.interest = interest;
        this.amount = amount;
        this.balance = balance;
    }

    public int getNumber() { return number; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getPrincipal() { return principal; }
    public BigDecimal getInterest() { return interest; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalance() { return balance; }
}

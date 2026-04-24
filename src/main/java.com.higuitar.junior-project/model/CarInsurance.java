package model;

public record CarInsurance(String policyId, Client client,
                           Double amount, Integer carYear) implements Insurance {}

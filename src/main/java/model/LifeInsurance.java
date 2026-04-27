package model;

public record LifeInsurance(String policyId, Client client,
                                  Double amount) implements Insurance {}

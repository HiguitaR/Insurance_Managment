package model;

public record HomeInsurance(String policyId, Client client, Double amount,
                            Boolean isHighRiskZone) implements Insurance {
}

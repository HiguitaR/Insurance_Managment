package model;

public sealed interface Insurance permits CarInsurance, HomeInsurance, LifeInsurance {
    String policyId();
    Client client();
    Double amount();
}

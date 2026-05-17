package service;

import exception.BusinessRuleException;
import model.CarInsurance;
import model.HomeInsurance;
import model.Insurance;
import model.LifeInsurance;


public class InsuranceQuoter {
    public double calculatePremium(Insurance insurance){
        double base;

        if(insurance.client().age() <= 0){
            throw new BusinessRuleException("Client age must be greater than 0: " +
                    insurance.client().age());
        }

        return switch (insurance){
            case LifeInsurance life -> {
                base = life.amount() * 0.05;
                yield life.client().age() > 60 ? (base * 0.2) + base : base;
            }
            case CarInsurance car -> {
                base = car.amount() * 0.1;
                yield car.carYear() < 2015 ? base + 50 : base;
            }
            case HomeInsurance home -> {
                base = home.amount() * 0.02;
                yield (home.isHighRiskZone()) ?  base * 2 : base;
            }
        };
    }
}

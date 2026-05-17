package com.javainsure;

import model.CarInsurance;
import model.Client;
import model.HomeInsurance;
import model.LifeInsurance;
import org.junit.jupiter.api.Test;
import service.InsuranceQuoter;

import exception.BusinessRuleException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsuranceQuoterTest {
    @Test
    void lifeInsurance_underAge_returnsBaseOnly(){
        Client client = new Client("C001", "Ana Garcia", 45);
        LifeInsurance policy = new LifeInsurance("POL-001", client, 100_000.0);
        InsuranceQuoter quoter = new InsuranceQuoter();

        double premium = quoter.calculatePremium(policy);

        assertEquals(5_000.0, premium, 0.001);
    }

    @Test
    void lifeInsurance_overAge_appliesSurcharge(){
        Client client = new Client("C001", "Ana Garcia", 65);
        LifeInsurance policy = new LifeInsurance("POL-001", client, 200_000.0);
        InsuranceQuoter quoter = new InsuranceQuoter();

        double premium = quoter.calculatePremium(policy);

        assertEquals(12_000.0, premium, 0.001);
    }

    @Test
    void carInsurance_recentCar_returnsBaseOnly(){
        Client client = new Client("C003", "Maria Lopez", 38);
        CarInsurance policy = new CarInsurance("POL-0003", client, 30_000.0,
                2018);
        InsuranceQuoter quoter = new InsuranceQuoter();

        double premium = quoter.calculatePremium(policy);

        assertEquals(3_000.0, premium, 0.001);
    }

    @Test
    void carInsurance_oldCar_appliesFixedSurcharge(){
        Client client = new Client("C004", "Maria Lopez", 52);
        CarInsurance policy = new CarInsurance("POL-0004", client, 15_000.0,
                2012);
        InsuranceQuoter quoter = new InsuranceQuoter();

        double premium = quoter.calculatePremium(policy);

        assertEquals(1_550.0, premium, 0.001);
    }

    @Test
    void homeInsurance_normalZone_returnsBaseOnly(){
        Client client = new Client("C005", "Laura Mora", 29);
        HomeInsurance policy = new HomeInsurance("POL-005", client,
                250_000.0, false);
        InsuranceQuoter quoter = new InsuranceQuoter();

        double premium = quoter.calculatePremium(policy);

        assertEquals(5_000.0, premium, 0.001);
    }

    @Test
    void homeInsurance_riskZone_doubleBase(){
        Client client = new Client("C006", "Jorge Castro", 41);
        HomeInsurance policy = new HomeInsurance("POL-005", client,
                180_000.0, true);
        InsuranceQuoter quoter = new InsuranceQuoter();

        double premium = quoter.calculatePremium(policy);

        assertEquals(7_200.0, premium, 0.001);
    }

    @Test
    void lifeInsurance_exactlyAge60_noSurcharge(){
        Client client = new Client("C001", "Ana Garcia", 60);
        LifeInsurance policy = new LifeInsurance("POL-001", client, 100_000.0);
        InsuranceQuoter quoter = new InsuranceQuoter();

        double premium = quoter.calculatePremium(policy);

        assertEquals(5_000.0, premium, 0.001);
    }

    @Test
    void businessRuleException_negativeAge(){
        Client client = new Client("C999", "Erika Brumn", -12);
        LifeInsurance policy = new LifeInsurance("POL-999", client, 100_000.0);
        InsuranceQuoter quoter = new InsuranceQuoter();

        assertThrows(BusinessRuleException.class, () -> quoter.calculatePremium(policy));
    }
}

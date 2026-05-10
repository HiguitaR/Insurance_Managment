package service;

import exception.BussinessRuleException;
import model.*;


import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class PolicyManager {
    private final List<Insurance> insurances;
    private final Scanner input = new Scanner(System.in);


    public PolicyManager(List<Insurance> insurances) {
        this.insurances = insurances;
    }

    public Object listAll(){
        return insurances.isEmpty() ? "Empty List" : insurances;
    }

    public void newPolicy (String policyType) {

        String type = policyType.strip().toUpperCase(Locale.ROOT);

        System.out.println("Enter a Customer name: ");
        String name = input.nextLine().strip();
        input.nextLine();
        int age = promptInt("Enter the customer's age: ",
                "The age client couldn't be negative or zero");

        Client client = new Client(getNewClientId(), name, age);

        Insurance insurance = switch (type){
            case "LIFE" -> createLifeInsurance(client);
            case "CAR" -> createCarInsurance(client);
            case "HOME" -> createHomeInsurance(client);
            default -> null;
        };

        if(insurance != null){
            insurances.add(insurance);
            System.out.println("The Policy :" + insurance.policyId() + " Added Successful");
        }else{
            throw new BussinessRuleException("Wrong insurance type");
        }
    }

    public List<Insurance> findPolicyById(String clientId){
        return insurances.stream()
                .filter(p -> p.client().clientId().equals(clientId))
                .toList();
    }
    private String getNewClientId(){
        String clientId = getLastClientId();

        if(!clientId.equals("C000")){
            System.out.println("The last customer id: " + clientId +
                    "Enter a consecutive customer id: ");
            clientId = input.nextLine();
            input.nextLine();
        }
        return clientId;
    }

    private String getNewPolicyId(){
        String policyId = getLastPolicyId();

        if(!policyId.equals("POL-000")){
            System.out.println("The last policy id: " + policyId +
                    "Enter a consecutive policy id: ");
            policyId = input.nextLine();
            input.nextLine();
        }
        return policyId;
    }

    private String getLastClientId(){
        return insurances.stream()
                .map(c -> c.client().clientId())
                .max(String::compareTo)
                .orElse("C000");
    }

    private String getLastPolicyId(){
        return insurances.stream()
                .map(Insurance::policyId)
                .max(String::compareTo)
                .orElse("POL-000");
    }

    private int promptInt(String message, String errorMessage){
        System.out.println(message);
        int value = input.nextInt();
        input.nextLine();
        if(errorMessage != null && value <= 0){
            throw new BussinessRuleException(errorMessage);
        }
        return value;
    }

    private Double promptDouble(String message, String errorMessage){
        System.out.println(message);
        double value = input.nextDouble();
        input.nextLine();
        if(errorMessage != null && value <= 0.0){
            throw new BussinessRuleException(errorMessage);
        }
        return value;
    }

    private LifeInsurance createLifeInsurance(Client client){
        double amount = promptDouble("Enter a life insurance amount (USD): ",
                "The policy amount couldn't be negative or zero");

        return new LifeInsurance(getNewPolicyId(), client, amount);
    }

    private CarInsurance createCarInsurance(Client client){
        double amount = promptDouble("Enter a car insurance amount (USD): ",
                "The policy amount couldn't be negative or zero");

        int carModel = promptInt("Enter the car model (year): ", null);

        return new CarInsurance(getNewPolicyId(), client, amount, carModel);
    }

    private HomeInsurance createHomeInsurance(Client client){
        double amount = promptDouble("Enter a home insurance amount (USD): ",
                "The policy amount couldn't be negative or zero");

        System.out.println("Enter the house risk zone (true/false): ");
        boolean riskZone = input.nextBoolean();
        input.nextLine();

        return new HomeInsurance(getNewPolicyId(), client, amount, riskZone);
    }
}

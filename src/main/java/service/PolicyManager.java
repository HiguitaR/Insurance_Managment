package service;

import model.*;

import java.util.List;
import java.util.Scanner;

public class PolicyManager {
    private final List<Insurance> insurances;

    public PolicyManager(List<Insurance> insurances) {
        this.insurances = insurances;
    }

    public List<Insurance> listAll(){
        return insurances;
    }

    public void newPolicy (String policyType){
        Scanner input = new Scanner(System.in);
        String name;
        int age;
        double amount;
        String clientId;
        String policyId;
        int carModel;
        boolean riskZone;
        switch (policyType.strip()) {
            case "life" -> {
                System.out.println("Enter a costumer name: ");
                name = input.nextLine().strip();
                input.nextLine();
                System.out.println("Enter the costumer's age: ");
                age = input.nextInt();
                input.nextLine();
                System.out.println("Enter a insurance life amount (USD): ");
                amount = input.nextDouble();
                input.nextLine();
                clientId = generateNewId();
                policyId = generateNewPolicyId();
                insurances.add(new LifeInsurance(policyId, new Client(clientId, name, age), amount));
                System.out.println("Life Policy: " + policyId + " Added Successful");
            }
            case "car" -> {
                System.out.println("Enter a costumer name: ");
                name = input.nextLine().strip();
                input.nextLine();
                System.out.println("Enter the costumer's age: ");
                age = input.nextInt();
                input.nextLine();
                System.out.println("Enter the car model: ");
                carModel = input.nextInt();
                input.nextLine();
                System.out.println("Enter a insurance car amount (USD): ");
                amount = input.nextDouble();
                input.nextLine();
                clientId = generateNewId();
                policyId = generateNewPolicyId();
                insurances.add(new CarInsurance(policyId, new Client(clientId, name, age),
                        amount, carModel));
                System.out.println("Car Policy: " + policyId + " Added Successful");
            }
            case "house" -> {
                System.out.println("Enter a costumer name: ");
                name = input.nextLine().strip();
                input.nextLine();
                System.out.println("Enter the costumer's age: ");
                age = input.nextInt();
                input.nextLine();
                System.out.println("Enter the house risk zone: ");
                riskZone = input.nextBoolean();
                input.nextLine();
                System.out.println("Enter a insurance car amount (USD): ");
                amount = input.nextDouble();
                input.nextLine();
                clientId = generateNewId();
                policyId = generateNewPolicyId();
                insurances.add(new HomeInsurance(policyId, new Client(clientId, name, age),
                        amount, riskZone));
            }
            default -> System.out.println("Wrong insurance type!");
        }
    }

    private String generateNewId(){
        String lastClientId = insurances.stream()
                .map(id -> id.client().clientId())
                .max(String::compareTo)
                .orElse("C000");
        int newID = Integer.parseInt(lastClientId.substring(1)) + 1;
        return String.format("C%03d", newID);
    }

    private String generateNewPolicyId(){
        String lastClientId = insurances.stream()
                .map(Insurance::policyId)
                .max(String::compareTo)
                .orElse("POL-000");
        int newID = Integer.parseInt(lastClientId.replace("POL-", "").strip()) + 1;
        return String.format("POL-%03d", newID);
    }


}

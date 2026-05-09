import exception.InvalidPolicyDataException;
import io.FileHandler;
import model.Insurance;


import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Insurance Management System v1.0");
        System.out.println("========================================");

        FileHandler fileHandler = new FileHandler();
        String csvFile = "policies.csv";

        try {
            System.out.println("Loading data from: " + csvFile);
            List<Insurance> policies = fileHandler.loadFile(csvFile);

            System.out.println("Success: " + policies.size() + " policies loaded.");
            System.out.println();

            System.out.println("--- Preview: First 5 Policies ---");
            policies.stream()
                    .limit(5)
                    .forEach(p -> System.out.printf("[%s] Type: %-13s | Holder: %-15s | Amount: $%,10.2f%n",
                            p.policyId(),
                            p.getClass().getSimpleName(),
                            p.client().name(),
                            p.amount()));
            System.out.println("----------------------------------------");

        } catch (IOException e) {
            System.err.println("ERROR: Could not read the file: " + e.getMessage());
        } catch (InvalidPolicyDataException e) {
            System.err.println("DATA WARNING: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
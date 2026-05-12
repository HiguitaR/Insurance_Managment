import exception.InvalidPolicyDataException;
import io.FileHandler;
import model.Insurance;
import service.PolicyManager;
import service.ReportService;


import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("   Insurance Management System v1.0");
        System.out.println("========================================");

        FileHandler fileHandler = new FileHandler();
        String csvFile = "policies.csv";

        try {
            System.out.println("Loading data...... " + csvFile);
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
            PolicyManager policyManager = new PolicyManager(policies);
            ReportService reportService = new ReportService(policies);

            menuShow();

            System.out.println("Enter an Option: ");
            var option = sc.nextLine();
            do{
                switch (option){
                    case "1":
                        policyManager.listAll();
                        break;
                    case "2":
                        policyManager.newPolicy(sc.nextLine());
                        break;
                    case "3":
                        policyManager.findPolicyById(sc.nextLine());
                        break;
                    case "4":
                        reportService.generaterReport();
                        break;
                    case "5":
                        System.out.println("Exiting the application!");
                        break;
                    default:
                        System.out.println("Enter a valid Option");
                }
                menuShow();
                System.out.println("Enter an Option: ");
                option = sc.nextLine();
            }while (!option.equals("5"));
            sc.close();

        } catch (IOException e) {
            System.err.println("ERROR: Could not read the file: " + e.getMessage());
        } catch (InvalidPolicyDataException e) {
            System.err.println("DATA WARNING: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private static void menuShow(){
        System.out.println("======= JavaInsure Core =======");
        System.out.println(" ");
        System.out.println("1. List All Policies ");
        System.out.println("2. Add New Policy ");
        System.out.println("3. Search For Policy By Client ID ");
        System.out.println("4. View Management Reports ");
        System.out.println("5. Exit ");
        System.out.println("---------------------------------");
    }

}
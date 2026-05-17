import exception.InvalidPolicyDataException;
import io.FileHandler;
import model.CarInsurance;
import model.HomeInsurance;
import model.Insurance;
import model.LifeInsurance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.PolicyManager;
import service.ReportService;


import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

        System.out.println("========================================");
        System.out.println("   Insurance Management System v1.0");
        System.out.println("========================================");

        FileHandler fileHandler = new FileHandler();
        String csvFile = "policies.csv";

        try {
            logger.info("Loading data from {} ",  csvFile);
            List<Insurance> policies = fileHandler.loadFile(csvFile);

            logger.info("Loaded {} Policies Successfully", policies.size());

            System.out.println("--- Preview: First 5 Policies ---");
            policies.stream()
                    .limit(5)
                    .forEach(p -> System.out.printf("[%s] %s | Holder: %s " +
                                    "| Amount: %s%n",
                            p.policyId(),
                            getType(p),
                            p.client().name(),
                            currency.format(p.amount())));
            System.out.println("----------------------------------------");

            PolicyManager policyManager = new PolicyManager(policies, sc);
            ReportService reportService = new ReportService(policies);



            String option;
            do{
                menuShow();
                System.out.println("Enter an Option: ");
                option = sc.nextLine();
                switch (option){
                    case "1":
                        policyManager.listAll();
                        break;
                    case "2":
                        policyManager.newPolicy();
                        break;
                    case "3":
                        System.out.println("Enter client Id: ");
                        String clientId = sc.nextLine();
                        List<Insurance> results = policyManager.findPolicyById(clientId);
                        if(results.isEmpty()){
                            System.out.println("No policies found." + clientId + ".");
                        }else {
                            results.forEach(p -> System.out.printf("[%s] %s |" +
                                    " Client: %s (%s) | Amount: %s%n",
                                    p.policyId(), getType(p), p.client().name(),
                                    p.client().clientId(), currency.format(p.amount())));
                        }
                        break;
                    case "4":
                        reportService.generateReport();
                        break;
                    case "5":
                        System.out.println("Closing App see you soon!...");
                        break;
                    default:
                        System.out.println("Enter a valid Option");
                }
            }while (!option.equals("5"));
            sc.close();

        } catch (Exception e) {
          logger.error("Unexpected Error! {}", e.getMessage());
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

    private static String getType(Insurance policy){
        return switch (policy) {
            case LifeInsurance l -> "LIFE";
            case CarInsurance c -> "CAR";
            case HomeInsurance h -> "HOME";
        };
    }

}
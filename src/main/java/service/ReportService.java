package service;

import model.CarInsurance;
import model.HomeInsurance;
import model.Insurance;
import model.LifeInsurance;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportService {
    public void generaterReport(List<Insurance> insurance){
        if(insurance == null || insurance.isEmpty()){
            System.out.println("There are no policies to generate a report!");
            return;
        }

        System.out.println("============ Management Reports ===============");
        System.out.println();

        //Report 1. Average Premium
        double average = insurance.stream()
                .filter(i -> i.amount() != null)
                .mapToDouble(Insurance::amount)
                .average()
                .orElse(0.0);
        System.out.println("1. Average premium of all policies: $" + average);
        System.out.println();

        //Report 2. Policies by Type
        System.out.println("2. Policies by Type: ");
        System.out.println();
        Map<String, Long> byType = insurance.stream()
                .collect(Collectors.groupingBy(this::getType, Collectors.counting()));
        byType.forEach((type, count) -> System.out.println("   - " + type
                + ": " + count + " pólicies"));
        System.out.println();

        //Report 3. Top 3 Most Expensive
        int[] counter = {1};
        insurance.stream()
                .filter(i -> i.amount() != null)
                .sorted(Comparator.comparingDouble(Insurance::amount).reversed())
                .limit(3)
                .forEach(i -> {
                    System.out.println("   #" + counter[0]++ + " [" + i.policyId() + "] " +
                            getType(i) + " | " + i.client().name() + " | Prima: $" + i.amount());
                });
    }

    private String getType(Insurance insurance){
        return switch (insurance){
            case LifeInsurance life -> "LIFE";
            case CarInsurance car -> "CAR";
            case HomeInsurance home -> "HOME";
            default -> "UNKNOW";
        };
    }
}

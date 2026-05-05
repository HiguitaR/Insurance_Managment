package io;

import exception.InvalidPolicyDataException;
import model.*;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;


public class FileHandler {
    private static final String SEPARATOR = ",";

    public List<Insurance> loadFile(String filePath) throws IOException {
        try (var lines = Files.lines(Path.of(filePath))) {
            return lines.map(this::parseLine)
                    .collect(Collectors.toList());
        }
    }

    @NonNull
    private Insurance parseLine(String line) {

        String[] parts = line.split(SEPARATOR);
        if (parts.length >= 6) {
            if(parts[0].trim().isEmpty()){
                throw new InvalidPolicyDataException("Missing data about policy in the line: "
                        + line);
            }
            String policyType = parts[0].trim();
            if(parts[1].trim().isEmpty()){
                throw new InvalidPolicyDataException("Missing data about policy in the line: "
                        + line);
            }
            String policyId = parts[1].trim();
            if(parts[2].trim().isEmpty()){
                throw new InvalidPolicyDataException("Missing data about policy in the line: "
                        + line);
            }
            String clientId = parts[2].trim();
            if(parts[3].trim().isEmpty()){
                throw new InvalidPolicyDataException("Missing data about policy in the line: "
                        + line);
            }
            String clientName = parts[3].trim();
            if(parts[4].trim().isEmpty()){
                throw new InvalidPolicyDataException("Missing data about policy in the line: "
                        + line);
            }
            int clientAge = Integer.parseInt(parts[4].trim());
            if(clientAge <= 0){
                throw new InvalidPolicyDataException("Wrong data about policy in the line: "
                        + line);
            }
            if(parts[5].trim().isEmpty()){
                throw new InvalidPolicyDataException("Missing data about policy in the line: "
                        + line);
            }
            Double policyAmount = Double.parseDouble(parts[5].trim());
            if(policyAmount <= 0){
                throw new InvalidPolicyDataException("Wrong data about policy in the line: "
                        + line);
            }

            Client client = new Client(clientId, clientName, clientAge);

            return switch (policyType) {
                case "LIFE" -> new LifeInsurance(policyId, client, policyAmount);
                case "CAR" -> {
                    if(Integer.parseInt(parts[6].trim()) < 0){
                        throw new InvalidPolicyDataException("Wrong data about policy in the line: "
                                + line);
                    }
                    int carYear = Integer.parseInt(parts[6].trim());
                    if(carYear < 1000 || carYear > 9999){
                        throw new InvalidPolicyDataException("Wrong data about policy in the line: "
                                + line);
                    }
                    yield new CarInsurance(policyId, client, policyAmount, carYear);

                }
                case "HOME" -> {
                    if(parts.length > 6) {
                        String riskValue = parts[7].trim();
                        if (!riskValue.equals("true") && !riskValue.equals("false")){
                            throw new InvalidPolicyDataException("Wrong data about policy in the line: "
                                    + line);
                        }
                        Boolean isHighRisk = Boolean.parseBoolean(parts[7].trim());
                        yield new HomeInsurance(policyId, client, policyAmount, isHighRisk);
                    } else{
                        throw new InvalidPolicyDataException(
                                "Missing data about policy in the line: " + line);
                    }
                }
                default ->
                        throw new InvalidPolicyDataException(
                                "Unknown insurance type: " + policyType);
            };
        }else{
            throw new InvalidPolicyDataException(
                    "Missing data about policy in the line: " + line);
        }
    }
}


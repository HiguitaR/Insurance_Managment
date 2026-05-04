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
            String policyType = parts[0].trim();
            String policyId = parts[1].trim();
            String clientId = parts[2].trim();
            String clientName = parts[3].trim();
            Integer clientAge = Integer.parseInt(parts[4].trim());
            Double policyAmount = Double.parseDouble(parts[5].trim());

            Client client = new Client(clientId, clientName, clientAge);

            return switch (policyType) {
                case "LIFE" -> new LifeInsurance(policyId, client, policyAmount);
                case "CAR" -> {
                    if(parts.length > 6) {
                        Integer carYear = Integer.parseInt(parts[6].trim());
                        yield new CarInsurance(policyId, client, policyAmount, carYear);
                    }else{
                        throw new InvalidPolicyDataException(
                                "Missing data about policy in the line: " + line);
                    }
                }
                case "HOME" -> {
                    if(parts.length > 6) {
                        Boolean isHighRisk = Boolean.parseBoolean(parts[6].trim());
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


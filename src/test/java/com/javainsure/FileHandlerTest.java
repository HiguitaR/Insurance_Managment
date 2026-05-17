package com.javainsure;


import io.FileHandler;
import model.CarInsurance;
import model.HomeInsurance;
import model.Insurance;
import model.LifeInsurance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileHandlerTest {

    @Test
    void readPolicies_validLine_returnsList(@TempDir Path tempDir) throws Exception {
        Path csvFile = tempDir.resolve("policies.csv");
        Files.writeString(csvFile, "LIFE, POL-001, C001, Ana Garcia, 45, 100000\n");

        FileHandler handler = new FileHandler();
        List<Insurance> policies = handler.loadFile(csvFile.toString());

        assertEquals(1, policies.size());
        assertInstanceOf(LifeInsurance.class, policies.getFirst());
        assertEquals("POL-001", policies.getFirst().policyId());
    }

    @Test
    void readPolicies_multipleTypes_returnsAllTypes(@TempDir Path tempDir) throws Exception {
        Path csvFile = tempDir.resolve("policies.csv");
        Files.writeString(csvFile, """
                LIFE,POL-001,C001,Ana Garcia,45,100000
                CAR,POL-002,C002,Maria Lopez,38,30000,2018
                HOME,POL-003,C003,Laura Mora,29,250000,false
                """);

        FileHandler handler = new FileHandler();
        List<Insurance> policies = handler.loadFile(csvFile.toString());

        assertEquals(3, policies.size());
        assertInstanceOf(LifeInsurance.class, policies.get(0));
        assertInstanceOf(CarInsurance.class, policies.get(1));
        assertInstanceOf(HomeInsurance.class, policies.get(2));
    }

    @Test
    void readPolicies_invalidType_throwsException(@TempDir Path tempDir) throws Exception {
        Path csvFile = tempDir.resolve("policies.csv");
        Files.writeString(csvFile, "UNKNOWN,POL-001,C001,Test,30,50000\n");
        FileHandler handler = new FileHandler();
        List<Insurance> policies = handler.loadFile(csvFile.toString());
        assertTrue(policies.isEmpty());
    }

    @Test
    void readPolicies_negativeAge_returnsEmptyList(@TempDir Path tempDir) throws Exception {
        Path csvFile = tempDir.resolve("policies.csv");
        Files.writeString(csvFile, "LIFE,POL-001,C001,Test,-5,50000\n");
        FileHandler handler = new FileHandler();
        List<Insurance> policies = handler.loadFile(csvFile.toString());
        assertTrue(policies.isEmpty());
    }

    @Test
    void readPolicies_emptyFile_returnsEmptyList(@TempDir Path tempDir) throws Exception {
        Path csvFile = tempDir.resolve("policies.csv");
        Files.writeString(csvFile, "");
        FileHandler handler = new FileHandler();
        List<Insurance> policies = handler.loadFile(csvFile.toString());
        assertTrue(policies.isEmpty());
    }
}

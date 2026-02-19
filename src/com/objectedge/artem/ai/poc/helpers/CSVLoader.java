package com.objectedge.artem.ai.poc.helpers;

import com.objectedge.artem.ai.poc.models.Armwrestler;
import java.io.*;
import java.util.*;

/**
 * Utility class for reading armwrestler data from CSV files
 */
public class CSVLoader {

    /**
     * Load armwrestlers from a CSV file
     * Format: Name,Surname,Age,Hand
     *
     * @param filePath Path to the CSV file
     * @return List of Armwrestler objects
     * @throws IOException If file cannot be read
     * @throws IllegalArgumentException If CSV format is invalid
     */
    public static List<Armwrestler> loadFromCSV(String filePath) throws IOException {
        List<Armwrestler> armwrestlers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip empty lines and header lines
                if (line.isEmpty() || line.toLowerCase().equals("name,surname,age,hand")) {
                    continue;
                }

                try {
                    Armwrestler armwrestler = parseCSVLine(line);
                    armwrestlers.add(armwrestler);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Error on line " + lineNumber + ": " + e.getMessage());
                }
            }
        }

        return armwrestlers;
    }

    /**
     * Parse a single CSV line into an Armwrestler object
     * Format: Name,Surname,Age,Hand
     *
     * @param line CSV line to parse
     * @return Armwrestler object
     * @throws IllegalArgumentException If line format is invalid
     */
    private static Armwrestler parseCSVLine(String line) {
        String[] parts = line.split(",");

        if (parts.length != 4) {
            throw new IllegalArgumentException("Expected 4 fields (Name,Surname,Age,Hand), got " + parts.length);
        }

        String name = parts[0].trim();
        String surname = parts[1].trim();
        String ageStr = parts[2].trim();
        String hand = parts[3].trim().toLowerCase();

        // Validate name and surname
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (surname.isEmpty()) {
            throw new IllegalArgumentException("Surname cannot be empty");
        }

        // Validate age
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 1 || age > 150) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Age must be a number between 1 and 150, got: " + ageStr);
        }

        // Validate hand
        if (!hand.equals("left") && !hand.equals("right")) {
            throw new IllegalArgumentException("Hand must be 'left' or 'right', got: " + hand);
        }

        // Capitalize names
        name = capitalizeFirstLetter(name);
        surname = capitalizeFirstLetter(surname);

        return new Armwrestler(name, surname, age, hand);
    }

    /**
     * Capitalize first letter of a string
     *
     * @param text Text to capitalize
     * @return Capitalized text
     */
    private static String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    /**
     * Save armwrestlers to a CSV file
     * Format: Name,Surname,Age,Hand
     *
     * @param filePath Path to save the CSV file
     * @param armwrestlers List of armwrestlers to save
     * @throws IOException If file cannot be written
     */
    public static void saveToCSV(String filePath, List<Armwrestler> armwrestlers) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println("Name,Surname,Age,Hand");

            // Write data rows
            for (Armwrestler w : armwrestlers) {
                writer.printf("%s,%s,%d,%s%n", w.getName(), w.getSurname(), w.getAge(), w.getHand());
            }
        }
    }
}


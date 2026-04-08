/**
 * =====================================================
 * Student Name    : Alidio, Jacob A.
 * Course          : Programming 2
 * Machine Problem : MP06 – Display Unique Values
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : April 8, 2026
 * Runtime         : Java (compile: javac MP06_UniqueValues.java)
 *                           (run:     java  MP06_UniqueValues)
 *
 * Description:
 *   Prompts the user for a CSV file path and a column name, then
 *   collects every distinct (unique) value found in that column
 *   using a LinkedHashSet to preserve insertion order. The unique
 *   values are printed in a numbered list followed by a total count.
 *   Empty/blank values are included and labelled "(empty)".
 * =====================================================
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class MP06_UniqueValues {

    // ── SECTION 1: Entry Point ───────────────────────────────────────────
    public static void main(String[] args) {

        // Scanner reads user input from the terminal
        Scanner scanner = new Scanner(System.in);

        System.out.println("=".repeat(52));
        System.out.println("  MP06 – Display Unique Values");
        System.out.println("=".repeat(52));
        System.out.print("  Enter CSV file path  : ");
        String filePath  = scanner.nextLine().trim();   // path to the CSV file

        System.out.print("  Enter column name    : ");
        String targetCol = scanner.nextLine().trim();   // column to inspect
        scanner.close();

        displayUniqueValues(filePath, targetCol);
    }

    // ── SECTION 2: Unique Value Extractor ───────────────────────────────
    /**
     * Opens the CSV, locates the target column, and collects all distinct
     * values encountered across every data row into a LinkedHashSet.
     * Results are then printed in the order they were first seen.
     *
     * @param filePath   path to the CSV file
     * @param targetCol  name of the column to inspect (case-insensitive)
     */
    static void displayUniqueValues(String filePath, String targetCol) {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // ── Step 1: Parse the header ──
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.out.println("[ERROR] File is empty.");
                return;
            }

            // headers contains every column name found on the first line
            String[] headers = headerLine.split(",", -1);

            // colIndex tracks which position the target column occupies
            int colIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase(targetCol)) {
                    colIndex = i;
                    break;
                }
            }

            // ── Step 2: Handle column-not-found case ──
            if (colIndex == -1) {
                System.out.println("[ERROR] Column \"" + targetCol + "\" not found.");
                System.out.println("  Available columns:");
                for (String h : headers) {
                    System.out.println("    - " + h.trim());
                }
                return;
            }

            // ── Step 3: Collect unique values ──
            // LinkedHashSet keeps insertion order and automatically rejects duplicates
            Set<String> uniqueValues = new LinkedHashSet<>();

            String line;       // current data line from the file
            int totalRows = 0; // tracks how many data rows were processed

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;  // skip blank lines

                totalRows++;

                // Split each row; -1 prevents dropping trailing empty fields
                String[] fields = line.split(",", -1);

                // Safely read the column value; use empty string if row is short
                String value = (colIndex < fields.length)
                               ? fields[colIndex].trim()
                               : "";

                // Add to the set; duplicates are silently ignored by LinkedHashSet
                uniqueValues.add(value.isEmpty() ? "(empty)" : value);
            }

            // ── Step 4: Print results ──
            System.out.println();
            System.out.println("=".repeat(52));
            System.out.printf("  Unique values in column : %s%n",
                              headers[colIndex].trim());
            System.out.println("=".repeat(52));

            // number keeps track of the display index shown to the user
            int number = 1;
            for (String val : uniqueValues) {
                System.out.printf("  %3d. %s%n", number++, val);
            }

            System.out.println("=".repeat(52));
            System.out.printf("  Total data rows scanned : %d%n", totalRows);
            System.out.printf("  Unique values found     : %d%n", uniqueValues.size());
            System.out.println("=".repeat(52));

        } catch (IOException e) {
            System.out.println("[ERROR] Could not read file: " + e.getMessage());
        }
    }
}
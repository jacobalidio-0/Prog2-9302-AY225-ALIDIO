/**
 * =====================================================
 * Student Name    : Alidio, Jacob A.
 * Course          : Programming 2
 * Machine Problem : MP05 – Extract Values from a Column
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : April 8, 2026
 * Runtime         : Java (compile: javac MP05_ExtractColumn.java)
 *                           (run:     java  MP05_ExtractColumn)
 *
 * Description:
 *   Prompts the user for a CSV file path and a column name. The
 *   program locates that column in the header row, then extracts
 *   and prints every value found in that column across all data
 *   rows. Row numbers are shown alongside each value for easy
 *   reference. Empty values are flagged as "(empty)".
 * =====================================================
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class MP05_ExtractColumn {

    // ── SECTION 1: Entry Point ───────────────────────────────────────────
    public static void main(String[] args) {

        // Scanner captures both the file path and column name from the user
        Scanner scanner = new Scanner(System.in);

        System.out.println("=".repeat(52));
        System.out.println("  MP05 – Extract Values from a Column");
        System.out.println("=".repeat(52));
        System.out.print("  Enter CSV file path  : ");
        String filePath  = scanner.nextLine().trim();   // path to the CSV

        System.out.print("  Enter column name    : ");
        String targetCol = scanner.nextLine().trim();   // column the user wants
        scanner.close();

        extractColumn(filePath, targetCol);
    }

    // ── SECTION 2: Column Extraction ────────────────────────────────────
    /**
     * Finds the target column by name in the header, then prints every
     * value from that column in each data row.
     *
     * @param filePath   path to the CSV file
     * @param targetCol  the column name to extract (case-insensitive match)
     */
    static void extractColumn(String filePath, String targetCol) {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // ── Step 1: Read and parse the header line ──
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.out.println("[ERROR] File is empty.");
                return;
            }

            // headers array holds each column name from the first CSV line
            String[] headers = headerLine.split(",", -1);

            // colIndex is the position (0-based) of the target column; -1 = not found
            int colIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                // Case-insensitive trim comparison so "Name" matches "name"
                if (headers[i].trim().equalsIgnoreCase(targetCol)) {
                    colIndex = i;
                    break;
                }
            }

            // ── Step 2: Abort if column not found ──
            if (colIndex == -1) {
                System.out.println("[ERROR] Column \"" + targetCol + "\" not found.");
                System.out.println("  Available columns:");
                for (String h : headers) {
                    System.out.println("    - " + h.trim());
                }
                return;
            }

            // ── Step 3: Print header for the results table ──
            System.out.println();
            System.out.println("=".repeat(52));
            System.out.printf("  Column : %s%n", headers[colIndex].trim());
            System.out.println("=".repeat(52));
            System.out.printf("  %-6s  %s%n", "Row", "Value");
            System.out.println("  " + "-".repeat(48));

            // ── Step 4: Read data rows and print the target column value ──
            String line;        // current data line
            int rowNum = 1;     // 1-based row counter shown to the user
            int extracted = 0;  // count of values successfully extracted

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;  // skip blank lines

                // Split each data row by comma; -1 preserves trailing empties
                String[] fields = line.split(",", -1);

                // Safely retrieve the value; guard against short rows
                String value = (colIndex < fields.length)
                               ? fields[colIndex].trim()
                               : "";

                // Display "(empty)" when the field has no content
                String display = value.isEmpty() ? "(empty)" : value;

                System.out.printf("  %-6d  %s%n", rowNum, display);
                rowNum++;
                extracted++;
            }

            // ── Step 5: Summary footer ──
            System.out.println("=".repeat(52));
            System.out.printf("  Total values extracted : %d%n", extracted);
            System.out.println("=".repeat(52));

        } catch (IOException e) {
            System.out.println("[ERROR] Could not read file: " + e.getMessage());
        }
    }
}
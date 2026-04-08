/**
 * =====================================================
 * Student Name    : Alidio, Jacob A.
 * Course          : Programming 2
 * Machine Problem : MP05 – Extract Values from a Column
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : April 8, 2026
 * Runtime         : Node.js  (run with: node MP05_ExtractColumn.js)
 *
 * Description:
 *   Prompts the user for a CSV file path and a column name. The
 *   program reads the file with Node.js fs, locates the column in
 *   the header, then prints every value in that column row by row.
 *   Row numbers accompany each value for easy reference. Empty
 *   fields are labelled "(empty)" instead of printing blank lines.
 * =====================================================
 */

// ── SECTION 1: Module Imports ────────────────────────────────────────
const fs       = require('fs');       // fs reads the CSV from disk
const readline = require('readline'); // readline lets us prompt the user

// ── SECTION 2: User Input ────────────────────────────────────────────
// rl connects the terminal's input/output so we can ask questions
const rl = readline.createInterface({
    input : process.stdin,
    output: process.stdout
});

const line = '='.repeat(52);

console.log(line);
console.log('  MP05 – Extract Values from a Column');
console.log(line);

// Ask for the file path first, then ask for the column name inside the callback
rl.question('  Enter CSV file path  : ', (filePath) => {
    rl.question('  Enter column name    : ', (targetCol) => {
        rl.close();
        extractColumn(filePath.trim(), targetCol.trim());
    });
});

// ── SECTION 3: Column Extraction Function ───────────────────────────
/**
 * Reads the CSV, finds the target column by name, and prints its value
 * for every data row. Handles missing columns and read errors gracefully.
 *
 * @param {string} filePath   - path to the CSV file
 * @param {string} targetCol  - name of the column to extract
 */
function extractColumn(filePath, targetCol) {

    // ── Step 1: Read the file ──
    let rawContent;
    try {
        rawContent = fs.readFileSync(filePath, 'utf8');
    } catch (err) {
        console.log('[ERROR] Could not read file: ' + err.message);
        return;
    }

    // ── Step 2: Split into non-empty lines ──
    const lines = rawContent
        .split('\n')
        .map(l => l.trim())
        .filter(l => l.length > 0);

    if (lines.length === 0) {
        console.log('[ERROR] File is empty.');
        return;
    }

    // ── Step 3: Locate the column index in the header ──
    // headers is an array of column name strings from the first line
    const headers = lines[0].split(',').map(h => h.trim());

    // colIndex will hold the 0-based position of the target column
    // findIndex returns -1 if no match is found
    const colIndex = headers.findIndex(
        h => h.toLowerCase() === targetCol.toLowerCase()
    );

    if (colIndex === -1) {
        console.log(`[ERROR] Column "${targetCol}" not found.`);
        console.log('  Available columns:');
        headers.forEach(h => console.log(`    - ${h}`));
        return;
    }

    // ── Step 4: Print column values from each data row ──
    console.log();
    console.log(line);
    console.log(`  Column : ${headers[colIndex]}`);
    console.log(line);
    console.log(`  ${'Row'.padEnd(6)}  Value`);
    console.log('  ' + '-'.repeat(48));

    let extracted = 0;  // counts how many values were printed

    // Data rows start at index 1 (index 0 is the header)
    for (let i = 1; i < lines.length; i++) {

        const fields = lines[i].split(',');  // split row into individual field values
        const rowNum = i;                    // 1-based row number for the user

        // Safely access the column; fall back to empty string if row is short
        const value   = colIndex < fields.length ? fields[colIndex].trim() : '';
        const display = value.length === 0 ? '(empty)' : value;

        console.log(`  ${String(rowNum).padEnd(6)}  ${display}`);
        extracted++;
    }

    // ── Step 5: Summary footer ──
    console.log(line);
    console.log(`  Total values extracted : ${extracted}`);
    console.log(line);
}
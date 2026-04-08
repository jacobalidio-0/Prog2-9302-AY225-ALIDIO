/**
 * =====================================================
 * Student Name    : Alidio, Jacob A.
 * Course          : Programming 2
 * Machine Problem : MP06 – Display Unique Values
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : April 8, 2026
 * Runtime         : Node.js  (run with: node MP06_UniqueValues.js)
 *
 * Description:
 *   Prompts the user for a CSV file path and a column name. The
 *   program reads the file with Node.js fs, locates the specified
 *   column, then uses a JavaScript Set to collect only distinct
 *   values from that column across all data rows. The unique values
 *   are printed in a numbered list with a count summary at the end.
 * =====================================================
 */

// ── SECTION 1: Module Imports ────────────────────────────────────────
const fs       = require('fs');       // fs provides file reading from disk
const readline = require('readline'); // readline handles terminal user prompts

// ── SECTION 2: User Input ────────────────────────────────────────────
// rl is the interactive interface connected to the terminal
const rl = readline.createInterface({
    input : process.stdin,
    output: process.stdout
});

const line = '='.repeat(52);

console.log(line);
console.log('  MP06 – Display Unique Values');
console.log(line);

// Chain two questions: file path first, column name second
rl.question('  Enter CSV file path  : ', (filePath) => {
    rl.question('  Enter column name    : ', (targetCol) => {
        rl.close();
        displayUniqueValues(filePath.trim(), targetCol.trim());
    });
});

// ── SECTION 3: Unique Value Display Function ─────────────────────────
/**
 * Reads the CSV at filePath, finds the named column, and collects all
 * distinct values using a Set (which automatically ignores duplicates).
 * Values are printed in the order they first appeared in the file.
 *
 * @param {string} filePath   - path to the CSV file
 * @param {string} targetCol  - column name whose unique values are needed
 */
function displayUniqueValues(filePath, targetCol) {

    // ── Step 1: Read the file ──
    let rawContent;
    try {
        // readFileSync blocks until the entire file is read into rawContent
        rawContent = fs.readFileSync(filePath, 'utf8');
    } catch (err) {
        console.log('[ERROR] Could not read file: ' + err.message);
        return;
    }

    // ── Step 2: Split into clean, non-empty lines ──
    const lines = rawContent
        .split('\n')
        .map(l => l.trim())           // remove \r and surrounding whitespace
        .filter(l => l.length > 0);  // drop lines that are completely blank

    if (lines.length === 0) {
        console.log('[ERROR] File is empty.');
        return;
    }

    // ── Step 3: Parse the header and find the target column ──
    // headers is an array of trimmed column name strings
    const headers  = lines[0].split(',').map(h => h.trim());

    // colIndex is the 0-based index of the target column; -1 means not found
    const colIndex = headers.findIndex(
        h => h.toLowerCase() === targetCol.toLowerCase()
    );

    if (colIndex === -1) {
        console.log(`[ERROR] Column "${targetCol}" not found.`);
        console.log('  Available columns:');
        headers.forEach(h => console.log(`    - ${h}`));
        return;
    }

    // ── Step 4: Collect unique values using a Set ──
    // A Set only stores each value once; duplicates are automatically dropped
    const uniqueSet = new Set();
    let totalRows   = 0;  // counts data rows processed

    for (let i = 1; i < lines.length; i++) {  // i = 1 skips the header

        totalRows++;

        // fields holds the comma-separated values for the current row
        const fields = lines[i].split(',');

        // Safely read the target column value; default to empty string
        const value  = colIndex < fields.length ? fields[colIndex].trim() : '';

        // Label blank fields as "(empty)" so they appear in the output
        uniqueSet.add(value.length === 0 ? '(empty)' : value);
    }

    // ── Step 5: Print the unique values in a numbered list ──
    console.log();
    console.log(line);
    console.log(`  Unique values in column : ${headers[colIndex]}`);
    console.log(line);

    let number = 1;  // display index shown next to each unique value
    for (const val of uniqueSet) {
        console.log(`  ${String(number++).padStart(3)}. ${val}`);
    }

    // ── Step 6: Summary footer ──
    console.log(line);
    console.log(`  Total data rows scanned : ${totalRows}`);
    console.log(`  Unique values found     : ${uniqueSet.size}`);
    console.log(line);
}
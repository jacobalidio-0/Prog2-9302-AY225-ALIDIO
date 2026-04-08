/**
 * =====================================================
 * Student Name    : Alidio, Jacob A.
 * Course          : Programming 2
 * Machine Problem : MP04 – Count Valid Dataset Rows
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : April 8, 2026
 * Runtime         : Node.js  (run with: node MP04_CountValidRows.js)
 *
 * Description:
 *   Prompts the user for a CSV file path using Node.js readline,
 *   reads the file synchronously with the fs module, then counts
 *   how many data rows are "valid" (every field is non-empty).
 *   The header row is excluded from counting. A summary showing
 *   total, valid, and invalid row counts is printed at the end.
 * =====================================================
 */

// ── SECTION 1: Module Imports ────────────────────────────────────────
const fs       = require('fs');       // File System module for reading the CSV
const readline = require('readline'); // readline creates an interactive prompt

// ── SECTION 2: User Input ────────────────────────────────────────────
// rl is the readline interface that connects to the terminal (stdin/stdout)
const rl = readline.createInterface({
    input : process.stdin,
    output: process.stdout
});

const line = '='.repeat(52);

console.log(line);
console.log('  MP04 – Count Valid Dataset Rows');
console.log(line);

// Ask the user to type the CSV file path, then process their answer
rl.question('  Enter CSV file path: ', (filePath) => {
    rl.close();                        // close the prompt after one answer
    countValidRows(filePath.trim());   // hand off to the processing function
});

// ── SECTION 3: Core Processing Function ─────────────────────────────
/**
 * Reads the CSV file at filePath, skips the header, and counts
 * valid rows (all fields non-empty) vs invalid rows (has empty field).
 *
 * @param {string} filePath  - absolute or relative path to the CSV file
 */
function countValidRows(filePath) {

    // ── Step 1: Read the file ──
    // Wrap in try/catch so a bad path shows a friendly error instead of crashing
    let rawContent;
    try {
        // readFileSync returns the full file as a string (utf8 encoding)
        rawContent = fs.readFileSync(filePath, 'utf8');
    } catch (err) {
        console.log('[ERROR] Could not read file: ' + err.message);
        return;
    }

    // ── Step 2: Split into lines ──
    // Split on newline; filter removes any completely blank lines
    const lines = rawContent
        .split('\n')
        .map(l => l.trim())           // trim carriage-returns / whitespace
        .filter(l => l.length > 0);  // discard empty lines

    if (lines.length === 0) {
        console.log('[ERROR] File is empty.');
        return;
    }

    // ── Step 3: Parse the header ──
    // The first non-blank line is treated as the header row
    const headerColumns = lines[0].split(',');   // array of column name strings

    // ── Step 4: Count valid and invalid data rows ──
    let totalRows   = 0;  // all data rows (excludes header)
    let validRows   = 0;  // rows where every field has content
    let invalidRows = 0;  // rows with at least one empty field

    // Start from index 1 to skip the header line
    for (let i = 1; i < lines.length; i++) {

        totalRows++;

        // fields is an array of the comma-separated values for this row
        // -1 equivalent: split with a limit keeps trailing empty strings
        const fields = lines[i].split(',');

        // Check every field; rowIsValid flips to false on first empty field
        const rowIsValid = fields.every(field => field.trim().length > 0);

        if (rowIsValid) {
            validRows++;
        } else {
            invalidRows++;
        }
    }

    // ── SECTION 4: Output ────────────────────────────────────────────
    console.log();
    console.log(line);
    console.log('  RESULTS');
    console.log(line);
    console.log(`  Columns detected : ${headerColumns.length}`);
    console.log(`  Total data rows  : ${totalRows}`);
    console.log(`  Valid rows       : ${validRows}`);
    console.log(`  Invalid rows     : ${invalidRows}`);
    console.log(line);
}
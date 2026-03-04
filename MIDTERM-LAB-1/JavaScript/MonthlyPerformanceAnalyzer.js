'use strict';

// ─────────────────────────────────────────────────────────────────────────────
//  📈  Monthly Performance Analyzer  —  VGChartz 2024 Dataset
//      Columns used:  release_date (col 12)  |  total_sales in millions (col 7)
// ─────────────────────────────────────────────────────────────────────────────

const fs       = require('fs');
const readline = require('readline');

const MONTH_NAMES = [
    'January','February','March','April','May','June',
    'July','August','September','October','November','December'
];

const rl = readline.createInterface({ input: process.stdin, output: process.stdout });

// ─── STEP 1: Loop until valid file path ──────────────────────────────────────
function askFilePath() {
    rl.question('Enter dataset file path: ', function (inputPath) {
        const trimmed = inputPath.trim();
        if (fs.existsSync(trimmed) && fs.statSync(trimmed).isFile()) {
            console.log('File found. Processing...\n');
            rl.close();
            processFile(trimmed);
        } else {
            console.log('Invalid file path. Please try again.');
            askFilePath();
        }
    });
}

// ─── STEP 2: Parse & load dataset ────────────────────────────────────────────
function processFile(filePath) {
    const lines = fs.readFileSync(filePath, 'utf8').split(/\r?\n/);

    const monthlySales = {};
    const monthLabels  = {};
    const monthCounts  = {};
    let linesRead    = 0;
    let linesSkipped = 0;

    // Skip header (line 0)
    for (let i = 1; i < lines.length; i++) {
        const line = lines[i].trim();
        if (!line) continue;

        const col = line.split(',');
        if (col.length < 13) { linesSkipped++; continue; }

        const dateStr  = col[12].trim();   // release_date  →  YYYY-MM-DD
        const salesStr = col[7].trim();    // total_sales   →  millions

        if (!dateStr || !salesStr) { linesSkipped++; continue; }

        const dp = dateStr.split('-');
        if (dp.length < 2) { linesSkipped++; continue; }

        const year  = dp[0];
        const month = dp[1].padStart(2, '0');
        const mi    = parseInt(month, 10);

        if (isNaN(mi) || mi < 1 || mi > 12) { linesSkipped++; continue; }

        const sales = parseFloat(salesStr);
        if (isNaN(sales)) { linesSkipped++; continue; }

        const key   = `${year}-${month}`;
        const label = `${MONTH_NAMES[mi - 1]} ${year}`;

        monthlySales[key] = (monthlySales[key] || 0) + sales;
        monthCounts[key]  = (monthCounts[key]  || 0) + 1;
        monthLabels[key]  = label;
        linesRead++;
    }

    if (Object.keys(monthlySales).length === 0) {
        console.log('No valid data found. Check file format.');
        return;
    }

    // ─── STEP 3: Sort ascending by YYYY-MM key ───────────────────────────────
    const sortedKeys = Object.keys(monthlySales).sort();

    // ─── STEP 4: Find best month & overall total ─────────────────────────────
    let bestKey  = sortedKeys[0];
    let totalAll = 0;

    for (const k of sortedKeys) {
        totalAll += monthlySales[k];
        if (monthlySales[k] > monthlySales[bestKey]) bestKey = k;
    }

    // ─── STEP 5: Display formatted results ───────────────────────────────────
    const pad  = (s, n) => String(s).padEnd(n);
    const rpad = (s, n) => String(s).padStart(n);
    const fmt  = (n)    => n.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    const W    = 70;
    const line = '═'.repeat(W);

    console.log(`╔${line}╗`);
    console.log(`║${pad('        📈  MONTHLY PERFORMANCE ANALYZER — VGChartz 2024', W)}║`);
    console.log(`╠${line}╣`);
    console.log(`║  ${pad('Month', 22)} ${rpad('#', 4)}  ${rpad('Total Sales (M)', 16)}  ${rpad('Share %', 8)}  ${rpad('Avg/Game', 8)}  ║`);
    console.log(`╠${line}╣`);

    for (const k of sortedKeys) {
        const label = monthLabels[k];
        const sales = monthlySales[k];
        const count = monthCounts[k];
        const share = totalAll > 0 ? (sales / totalAll * 100).toFixed(2) : '0.00';
        const avg   = count   > 0 ? (sales / count).toFixed(2) : '0.00';
        const star  = k === bestKey ? '★' : ' ';

        console.log(`║ ${star} ${pad(label, 22)} ${rpad(count, 4)}  ${rpad(fmt(sales), 16)}  ${rpad(share + '%', 8)}  ${rpad(avg, 8)}  ║`);
    }

    console.log(`╠${line}╣`);
    console.log(`║  ${pad('TOTAL', 22)} ${rpad(linesRead, 4)}  ${rpad(fmt(totalAll), 16)}  ${rpad('100.00%', 8)}  ${rpad('—', 8)}  ║`);
    console.log(`╠${line}╣`);
    console.log(`║  🏆 Best Month  : ${pad(monthLabels[bestKey], 52)}║`);
    console.log(`║     Total Sales : ${pad(fmt(monthlySales[bestKey]) + ' M units  |  ' + monthCounts[bestKey] + ' titles', 52)}║`);
    console.log(`╠${line}╣`);
    console.log(`║  Records Processed : ${rpad(linesRead, 49)}║`);
    console.log(`║  Records Skipped   : ${rpad(linesSkipped, 49)}║`);
    console.log(`║  Months Tracked    : ${rpad(sortedKeys.length, 49)}║`);
    console.log(`╚${line}╝`);
}

// ─── Entry point ─────────────────────────────────────────────────────────────
askFilePath();

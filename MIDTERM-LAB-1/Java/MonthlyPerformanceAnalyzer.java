import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class MonthlyPerformanceAnalyzer extends JFrame {

    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color BG_DARK    = new Color(15,  17,  26);
    private static final Color BG_PANEL   = new Color(24,  27,  40);
    private static final Color BG_ROW_ALT = new Color(30,  34,  50);
    private static final Color ACCENT     = new Color(99,  179, 237);
    private static final Color GOLD       = new Color(255, 200,  80);
    private static final Color TEXT_MAIN  = new Color(220, 225, 240);
    private static final Color TEXT_MUTED = new Color(110, 120, 150);
    private static final Color GREEN      = new Color( 72, 199, 142);
    private static final Color BORDER_COL = new Color( 45,  50,  75);

    // ── UI components ─────────────────────────────────────────────────────────
    private JTextField  pathField;
    private JButton     browseBtn, analyzeBtn;
    private JTable      table;
    private DefaultTableModel tableModel;
    private JLabel      bestMonthVal, bestSalesVal, totalSalesVal, recordsVal, monthsVal;
    private JLabel      statusLabel;
    private JProgressBar progressBar;

    private static final String[] MONTH_NAMES = {
        "January","February","March","April","May","June",
        "July","August","September","October","November","December"
    };

    public MonthlyPerformanceAnalyzer() {
        setTitle("Monthly Performance Analyzer — VGChartz");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 700);
        setMinimumSize(new Dimension(800, 580));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setBackground(BG_PANEL);
        top.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COL),
            new EmptyBorder(16, 20, 16, 20)
        ));

        JLabel title = new JLabel("Monthly Performance Analyzer");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("VGChartz 2024 Dataset  •  Grouped by Release Month");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(ACCENT);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(3));
        titleBox.add(subtitle);

        pathField = new JTextField(32);
        styleTextField(pathField);

        browseBtn  = makeButton("Browse",  ACCENT, BG_DARK);
        analyzeBtn = makeButton("Analyze", GOLD,   BG_DARK);
        analyzeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        browseBtn.addActionListener(e -> browseFile());
        analyzeBtn.addActionListener(e -> runAnalysis());

        JLabel fileLabel = new JLabel("Dataset:  ");
        fileLabel.setForeground(TEXT_MUTED);
        fileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel fileRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        fileRow.setOpaque(false);
        fileRow.add(fileLabel);
        fileRow.add(pathField);
        fileRow.add(browseBtn);
        fileRow.add(analyzeBtn);

        top.add(titleBox, BorderLayout.WEST);
        top.add(fileRow,  BorderLayout.EAST);
        return top;
    }

    // ── Center ────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(BG_DARK);
        center.add(buildStatCards(), BorderLayout.NORTH);

        String[] cols = { "#", "Month", "Year", "Titles Released", "Total Sales (M)", "Market Share %", "Avg Sales/Game" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setBackground(BG_PANEL);
        table.setForeground(TEXT_MAIN);
        table.setSelectionBackground(new Color(50, 70, 110));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(BORDER_COL);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(28, 32, 52));
        header.setForeground(ACCENT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COL));
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(col >= 3 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                if (!sel) {
                    String marker = String.valueOf(t.getModel().getValueAt(row, 0));
                    if (marker.contains("★")) {
                        setBackground(new Color(55, 45, 15));
                        setForeground(GOLD);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setBackground(row % 2 == 0 ? BG_PANEL : BG_ROW_ALT);
                        setForeground(TEXT_MAIN);
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        });

        int[] widths = {55, 115, 60, 130, 150, 140, 145};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG_PANEL);
        scroll.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COL));
        scroll.getVerticalScrollBar().setBackground(BG_DARK);

        center.add(scroll, BorderLayout.CENTER);
        return center;
    }

    // ── Stat cards ────────────────────────────────────────────────────────────
    private JPanel buildStatCards() {
        JPanel cards = new JPanel(new GridLayout(1, 5, 10, 0));
        cards.setBackground(BG_DARK);
        cards.setBorder(new EmptyBorder(14, 16, 14, 16));

        bestMonthVal  = new JLabel("—");
        bestSalesVal  = new JLabel("—");
        totalSalesVal = new JLabel("—");
        recordsVal    = new JLabel("—");
        monthsVal     = new JLabel("—");

        cards.add(makeCard("🏆  Best Month",  bestMonthVal,  GOLD));
        cards.add(makeCard("💰  Best Sales",  bestSalesVal,  GREEN));
        cards.add(makeCard("📊  Total Sales", totalSalesVal, ACCENT));
        cards.add(makeCard("📁  Records",     recordsVal,    TEXT_MAIN));
        cards.add(makeCard("📅  Months",      monthsVal,     TEXT_MAIN));
        return cards;
    }

    private JPanel makeCard(String label, JLabel valueLabel, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(BG_PANEL);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COL, 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valueLabel.setForeground(valueColor);
        card.add(lbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ── Status bar ────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(new Color(18, 20, 32));
        bar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_COL),
            new EmptyBorder(7, 16, 7, 16)
        ));
        statusLabel = new JLabel("Ready  —  browse for a CSV file to begin.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_MUTED);

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(150, 8));
        progressBar.setVisible(false);
        progressBar.setBackground(BG_PANEL);
        progressBar.setForeground(ACCENT);
        progressBar.setBorderPainted(false);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(progressBar, BorderLayout.EAST);
        return bar;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private JButton makeButton(String text, Color fg, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
            new LineBorder(fg.darker(), 1, true),
            new EmptyBorder(6, 18, 6, 18)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 25)); }
            public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void styleTextField(JTextField f) {
        f.setBackground(new Color(30, 34, 50));
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COL, 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
    }

    // ── Browse ────────────────────────────────────────────────────────────────
    private void browseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        chooser.setDialogTitle("Select VGChartz Dataset");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            pathField.setText(chooser.getSelectedFile().getAbsolutePath());
    }

    // ── Analyze ───────────────────────────────────────────────────────────────
    private void runAnalysis() {
        String path = pathField.getText().trim();
        File file   = new File(path);

        if (path.isEmpty())              { setStatus("⚠  Please enter or browse for a file path.", Color.ORANGE); return; }
        if (!file.exists()||!file.isFile()){ setStatus("✗  File not found — check the path.",      Color.RED);    return; }

        analyzeBtn.setEnabled(false);
        browseBtn.setEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        tableModel.setRowCount(0);
        setStatus("Processing dataset...", ACCENT);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            final Map<String, Double>  monthlySales = new TreeMap<>();
            final Map<String, Integer> monthCounts  = new TreeMap<>();
            final Map<String, String>  monthLabels  = new HashMap<>();
            int linesRead = 0, linesSkipped = 0;
            String error = null;

            @Override protected Void doInBackground() {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line; boolean first = true;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) continue;
                        if (first) { first = false; continue; }

                        String[] col = line.split(",", -1);
                        if (col.length < 13) { linesSkipped++; continue; }

                        String dateStr  = col[12].trim();
                        String salesStr = col[7].trim();
                        if (dateStr.isEmpty() || salesStr.isEmpty()) { linesSkipped++; continue; }

                        String[] dp = dateStr.split("-");
                        if (dp.length < 2) { linesSkipped++; continue; }

                        int mi;
                        try { mi = Integer.parseInt(dp[1]); }
                        catch (NumberFormatException ex) { linesSkipped++; continue; }
                        if (mi < 1 || mi > 12) { linesSkipped++; continue; }

                        double sales;
                        try { sales = Double.parseDouble(salesStr); }
                        catch (NumberFormatException ex) { linesSkipped++; continue; }

                        String key   = dp[0] + "-" + String.format("%02d", mi);
                        String label = MONTH_NAMES[mi - 1] + " " + dp[0];
                        monthlySales.merge(key, sales, Double::sum);
                        monthCounts.merge(key, 1, Integer::sum);
                        monthLabels.put(key, label);
                        linesRead++;
                    }
                } catch (IOException ex) { error = ex.getMessage(); }
                return null;
            }

            @Override protected void done() {
                progressBar.setVisible(false);
                analyzeBtn.setEnabled(true);
                browseBtn.setEnabled(true);

                if (error != null)            { setStatus("✗  Error: " + error, Color.RED); return; }
                if (monthlySales.isEmpty())   { setStatus("⚠  No valid data found.", Color.ORANGE); return; }

                // Find best month
                String bestKey = null; double bestSales = -1, totalAll = 0;
                for (Map.Entry<String, Double> e : monthlySales.entrySet()) {
                    totalAll += e.getValue();
                    if (e.getValue() > bestSales) { bestSales = e.getValue(); bestKey = e.getKey(); }
                }

                // Populate table
                int rank = 1;
                for (Map.Entry<String, Double> e : monthlySales.entrySet()) {
                    String  k     = e.getKey();
                    double  sales = e.getValue();
                    int     count = monthCounts.get(k);
                    String[] dp   = k.split("-");
                    double  share = totalAll > 0 ? (sales / totalAll * 100) : 0;
                    double  avg   = count > 0 ? (sales / count) : 0;
                    String  mark  = k.equals(bestKey) ? "★ " + rank : String.valueOf(rank);
                    tableModel.addRow(new Object[]{
                        mark,
                        MONTH_NAMES[Integer.parseInt(dp[1]) - 1],
                        dp[0],
                        String.format("%,d", count),
                        String.format("%,.2f", sales),
                        String.format("%.3f%%", share),
                        String.format("%,.2f", avg)
                    });
                    rank++;
                }

                // Stat cards
                String[] bk = bestKey.split("-");
                bestMonthVal.setText(MONTH_NAMES[Integer.parseInt(bk[1]) - 1] + " " + bk[0]);
                bestSalesVal.setText(String.format("%,.2f M", bestSales));
                totalSalesVal.setText(String.format("%,.0f M", totalAll));
                recordsVal.setText(String.format("%,d", linesRead));
                monthsVal.setText(String.format("%,d", monthlySales.size()));

                setStatus("✓  Complete — " + String.format("%,d", linesRead) + " records across "
                    + monthlySales.size() + " months  |  Skipped: " + linesSkipped, GREEN);
            }
        };
        worker.execute();
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    // ── Entry point ───────────────────────────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MonthlyPerformanceAnalyzer().setVisible(true));
    }
}

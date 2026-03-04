

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AttendanceTracker extends JFrame {
    
    // UI Components
    private JTextField nameField, courseField, timeInField;
    private SignaturePanel sigPanel; // Custom drawable component
    private JButton submitButton, clearButton;
    private Color maroon = new Color(122, 12, 12);

    public AttendanceTracker() {
        // 1. Display a Window
        setTitle("Attendance Tracker (Drawable Signature)");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 2. Labeled Fields
        mainPanel.add(new JLabel("Attendance Name:"));
        nameField = new JTextField();
        mainPanel.add(nameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(new JLabel("Course / Year:"));
        courseField = new JTextField();
        mainPanel.add(courseField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        mainPanel.add(new JLabel("Time In:"));
        timeInField = new JTextField();
        timeInField.setEditable(false);
        mainPanel.add(timeInField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // --- DRAWABLE SIGNATURE SECTION ---
        mainPanel.add(new JLabel("E-Signature (Draw below):"));
        sigPanel = new SignaturePanel();
        mainPanel.add(sigPanel);

        clearButton = new JButton("Clear Signature");
        clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearButton.addActionListener(e -> sigPanel.clear());
        mainPanel.add(clearButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. Obtain System Date
        updateTime();

        // 5. Submit Button
        submitButton = new JButton("Submit Attendance");
        submitButton.setBackground(maroon);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.setMaximumSize(new Dimension(400, 40));
        mainPanel.add(submitButton);

        add(mainPanel);

        // Submit Button Action
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameField.getText().trim().isEmpty() || courseField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                } else if (sigPanel.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please provide a signature.");
                } else {
                    JOptionPane.showMessageDialog(null, "Attendance Submitted Successfully!");
                    // Reset fields
                    nameField.setText("");
                    courseField.setText("");
                    sigPanel.clear();
                    updateTime();
                }
            }
        });
    }

    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timeInField.setText(now.format(formatter));
    }

    // --- CUSTOM INNER CLASS FOR DRAWING ---
    class SignaturePanel extends JPanel {
        private final List<List<Point>> paths = new ArrayList<>();
        private List<Point> currentPath;

        public SignaturePanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));
            setPreferredSize(new Dimension(350, 150));
            setMaximumSize(new Dimension(350, 150));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    currentPath = new ArrayList<>();
                    currentPath.add(e.getPoint());
                    paths.add(currentPath);
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    currentPath.add(e.getPoint());
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(Color.BLACK);

            for (List<Point> path : paths) {
                for (int i = 0; i < path.size() - 1; i++) {
                    Point p1 = path.get(i);
                    Point p2 = path.get(i + 1);
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        public void clear() {
            paths.clear();
            repaint();
        }

        public boolean isEmpty() {
            return paths.isEmpty();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AttendanceTracker().setVisible(true));
    }
}
package x;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WelcomeApp extends JFrame {
    private JLabel liveCounterLabel;
    private JLabel totalEntriesLabel;
    private JTextField fullNameField, emailField, phoneField, addressField;
    private JTextField collegeField, courseField;
    private DefaultTableModel model;

    public WelcomeApp() {
        setTitle("स्वागत (Swagat)");
        setSize(1280, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setBackground(new Color(245, 247, 252));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        header.setOpaque(false);
        JLabel title = new JLabel("स्वागत (Swagat)");
        title.setFont(new Font("SansSerif", Font.BOLD, 34));
        header.add(title);
        root.add(header, BorderLayout.NORTH);
        root.add(createSidebar(), BorderLayout.WEST);

        root.add(createMainContent(), BorderLayout.CENTER);
        add(root);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(new EmptyBorder(20,15,20,15));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        JLabel navTitle = new JLabel("Navigation");
        navTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        sidebar.add(navTitle);
        sidebar.add(Box.createVerticalStrut(20));
        for (String item : new String[]{"Dashboard", "Register", "Students", "Export"}) {
            JButton btn = new JButton(item);
            styleButton(btn);
            btn.setMaximumSize(new Dimension(180, 45));
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(10));
        }
        return sidebar;
    }

    private JPanel createDashboardPanel() {
        JPanel dash = new JPanel(new GridLayout(1, 2, 15, 15));
        dash.setOpaque(false);
        dash.add(createStatCard("Live Students", "0"));
        dash.add(createStatCard("Total Entries Today", "0"));
        return dash;
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(new LineBorder(new Color(230,230,240),1,true), new EmptyBorder(18,18,18,18)));
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.BOLD, 30));
        if (title.contains("Live")) liveCounterLabel = v;
        if (title.contains("Total")) totalEntriesLabel = v;
        card.add(t, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMainContent() {
        JPanel container = new JPanel(new BorderLayout(20, 20));
        container.setOpaque(false);
        container.add(createDashboardPanel(), BorderLayout.NORTH);
        container.add(createFormPanel(), BorderLayout.CENTER);
        container.add(createTablePanel(), BorderLayout.SOUTH);
        return container;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel section = new JLabel("Student Information");
        section.setFont(new Font("SansSerif", Font.BOLD, 28));
        panel.add(section);
        panel.add(Box.createVerticalStrut(15));

        JPanel infoGrid = new JPanel(new GridLayout(3, 2, 12, 12));
        infoGrid.setOpaque(false);

        fullNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();
        collegeField = new JTextField();
        courseField = new JTextField();

        infoGrid.add(createField("Full Name", fullNameField));
        infoGrid.add(createField("Email", emailField));
        infoGrid.add(createField("Phone", phoneField));
        infoGrid.add(createField("Address", addressField));
        infoGrid.add(createField("College", collegeField));
        infoGrid.add(createField("Course", courseField));

        panel.add(infoGrid);
        panel.add(Box.createVerticalStrut(25));

        JLabel qrTitle = new JLabel("College QR References");
        qrTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        panel.add(qrTitle);
        panel.add(Box.createVerticalStrut(15));

        JPanel qrPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        qrPanel.setOpaque(false);
        qrPanel.add(createQRCard("College WiFi", new Color(45, 108, 223), "WIFI-REF-8472"));
        qrPanel.add(createQRCard("Instagram QR", new Color(150, 60, 255), "IG-REF-3921"));
        qrPanel.add(createQRCard("Facebook QR", new Color(10, 180, 70), "FB-REF-5508"));
        panel.add(qrPanel);

        panel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        buttonPanel.setOpaque(false);

        JButton addButton = new JButton("Register Student");
        JButton viewButton = new JButton("View Students");
        JButton exportButton = new JButton("Export to Excel");

        styleButton(addButton);
        styleButton(viewButton);
        styleButton(exportButton);

        addButton.addActionListener(e -> addEntry());
        viewButton.addActionListener(e -> showStudentsWindow());
        exportButton.addActionListener(e -> exportToExcel());

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(exportButton);
        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createField(String label, JTextField field) {
        field.setPreferredSize(new Dimension(320, 48));
        field.setFont(new Font("SansSerif", Font.PLAIN, 18));
        field.setBorder(new CompoundBorder(new LineBorder(new Color(210,210,230),1,true), new EmptyBorder(8,12,8,12)));
        field.setBackground(new Color(250,252,255));
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createQRCard(String title, Color color, String code) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(color);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel qrMock = new JLabel("QR", SwingConstants.CENTER);
        qrMock.setOpaque(true);
        qrMock.setBackground(Color.WHITE);
        qrMock.setPreferredSize(new Dimension(150, 150));
        qrMock.setFont(new Font("SansSerif", Font.BOLD, 40));

        JLabel codeLabel = new JLabel(code);
        codeLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(qrMock, BorderLayout.CENTER);
        card.add(codeLabel, BorderLayout.SOUTH);
        return card;
    }

    private JScrollPane createTablePanel() {
        model = new DefaultTableModel(new String[]{
                "Entry Time", "Full Name", "Email", "Phone", "Address", "College", "Course"
        }, 0);

        JTable table = new JTable(model);
        table.setRowHeight(28);
        JScrollPane pane = new JScrollPane(table);
        pane.setBorder(BorderFactory.createTitledBorder("Latest Student Entries"));
        pane.setPreferredSize(new Dimension(1000, 220));
        return pane;
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(180, 45));
        button.setBackground(new Color(88, 101, 242));
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10,18,10,18));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void addEntry() {
        // time is auto-saved silently with each new student entry
        model.insertRow(0, new Object[]{
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                fullNameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                addressField.getText(),
                collegeField.getText(),
                courseField.getText()
        });

        fullNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        collegeField.setText("");
        courseField.setText("");
        updateCounters();
    }

    private void updateCounters() {
        if (liveCounterLabel != null) liveCounterLabel.setText(String.valueOf(model.getRowCount()));
        if (totalEntriesLabel != null) totalEntriesLabel.setText(String.valueOf(model.getRowCount()));
    }

    private void showStudentsWindow() {
        JFrame frame = new JFrame("View Students - Excel Style");
        JTable table = new JTable(model);
        table.setRowHeight(30);
        frame.add(new JScrollPane(table));
        frame.setSize(1000, 400);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    private void exportToExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Swagat Entries");

            Row header = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                header.createCell(i).setCellValue(model.getColumnName(i));
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    row.createCell(j).setCellValue(String.valueOf(model.getValueAt(i, j)));
                }
            }

            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            File file = new File("swagat_entries.xlsx");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

            JDialog dialog = new JDialog(this, "Export Success", true);
            JLabel success = new JLabel("Excel Export Successful", SwingConstants.CENTER);
            success.setFont(new Font("SansSerif", Font.BOLD, 24));
            success.setBorder(new EmptyBorder(30,30,30,30));
            dialog.add(success);
            dialog.setSize(420,180);
            dialog.setLocationRelativeTo(this);
            javax.swing.Timer timer = new javax.swing.Timer(1200, e -> dialog.dispose());
            timer.setRepeats(false);
            timer.start();
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeApp().setVisible(true));
    }
}

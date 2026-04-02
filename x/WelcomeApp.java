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

        root.add(createMainContent(), BorderLayout.CENTER);
        add(root);
    }

    private JPanel createMainContent() {
        JPanel container = new JPanel(new BorderLayout(20, 20));
        container.setOpaque(false);
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

            JOptionPane.showMessageDialog(this, "Excel exported successfully to: " + file.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeApp().setVisible(true));
    }
}

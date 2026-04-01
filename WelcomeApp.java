// WelcomeApp.java
// College Event Entry Management App - "Welcome"
// GUI: Java Swing
// Excel Support: Apache POI

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WelcomeApp extends JFrame {
    private JTextField nameField, idField, departmentField, phoneField;
    private DefaultTableModel model;

    public WelcomeApp() {
        setTitle("Welcome - College Event Entry System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        formPanel.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Student ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Department:"));
        departmentField = new JTextField();
        formPanel.add(departmentField);

        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        JButton addButton = new JButton("Add Entry");
        JButton exportButton = new JButton("Export to Excel");

        formPanel.add(addButton);
        formPanel.add(exportButton);

        add(formPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Name", "ID", "Department", "Phone"}, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addButton.addActionListener(e -> addEntry());
        exportButton.addActionListener(e -> exportToExcel());
    }

    private void addEntry() {
        model.addRow(new Object[]{
                nameField.getText(),
                idField.getText(),
                departmentField.getText(),
                phoneField.getText()
        });

        nameField.setText("");
        idField.setText("");
        departmentField.setText("");
        phoneField.setText("");
    }

    private void exportToExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Event Entries");

            Row header = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                header.createCell(i).setCellValue(model.getColumnName(i));
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    row.createCell(j).setCellValue(model.getValueAt(i, j).toString());
                }
            }

            FileOutputStream fos = new FileOutputStream(new File("welcome_entries.xlsx"));
            workbook.write(fos);
            fos.close();

            JOptionPane.showMessageDialog(this, "Excel file exported successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeApp().setVisible(true));
    }
}

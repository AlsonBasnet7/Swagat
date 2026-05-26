// WelcomeApp.java
// College Event Entry Management App - "Welcome - स्वागत"
// GUI: Advanced Java Swing (Midnight Slate Premium Theme)
// Excel Support: Apache POI

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WelcomeApp extends JFrame {
    
    // ==========================================
    // DESIGN SYSTEM & THEME TOKENS (DYNAMIC HSL)
    // ==========================================
    public static Color backgroundColor;
    public static Color cardBg;
    public static Color secondaryCardBg;
    public static Color borderColor;
    public static Color inputBg;
    public static Color primaryAccent;
    public static Color secondaryAccent;
    public static Color textPrimary;
    public static Color textMuted;
    public static Color tableRowAlt;
    
    public static final Color successColor = new Color(16, 185, 129); // Emerald Green
    public static final Color dangerColor = new Color(239, 68, 68);    // Coral Red
    
    static {
        // Default to Midnight Slate (Premium Dark)
        applyTheme("Midnight Slate");
    }

    public static void applyTheme(String themeName) {
        if (themeName.equalsIgnoreCase("Midnight Slate")) {
            backgroundColor = new Color(15, 23, 42);      // Tailwind slate-900
            cardBg = new Color(30, 41, 59);               // Tailwind slate-800
            secondaryCardBg = new Color(21, 30, 48);      // Tailwind slate-850
            borderColor = new Color(51, 65, 85);          // Tailwind slate-700
            inputBg = new Color(15, 23, 42);              // Inset Dark
            primaryAccent = new Color(99, 102, 241);      // Indigo 500
            secondaryAccent = new Color(6, 182, 212);     // Cyan 500
            textPrimary = new Color(248, 250, 252);       // slate-50
            textMuted = new Color(148, 163, 184);         // slate-400
            tableRowAlt = new Color(38, 50, 72);          // Slate Row
        } else if (themeName.equalsIgnoreCase("Sapphire Blue")) {
            backgroundColor = new Color(10, 15, 30);      // Deep Sapphire Dark
            cardBg = new Color(20, 30, 55);
            secondaryCardBg = new Color(15, 22, 42);
            borderColor = new Color(35, 50, 85);
            inputBg = new Color(10, 15, 30);
            primaryAccent = new Color(59, 130, 246);      // Blue 500
            secondaryAccent = new Color(139, 92, 246);     // Purple 500
            textPrimary = new Color(240, 245, 255);
            textMuted = new Color(120, 140, 180);
            tableRowAlt = new Color(24, 38, 68);
        } else { // Ice Light
            backgroundColor = new Color(243, 244, 246);   // Gray 100
            cardBg = new Color(255, 255, 255);            // White
            secondaryCardBg = new Color(249, 250, 251);   // Gray 50
            borderColor = new Color(229, 231, 235);       // Gray 200
            inputBg = new Color(243, 244, 246);           // Inset Gray
            primaryAccent = new Color(79, 70, 229);       // Indigo 600
            secondaryAccent = new Color(13, 148, 136);     // Teal 600
            textPrimary = new Color(17, 24, 39);          // Gray 900
            textMuted = new Color(107, 114, 128);         // Gray 500
            tableRowAlt = new Color(243, 244, 246);
        }
    }

    // ==========================================
    // UI ELEMENTS & FIELDS
    // ==========================================
    private String collegeName = "Itahari International College";
    private String excelFileName = "swagat_entries.xlsx";
    
    private JLabel activeTitleLabel;
    private JLabel collegeTitleLabel;
    private JPanel mainContentArea;
    private CardLayout cardLayout;
    
    // Sidebar items
    private SidebarButton[] sidebarButtons;
    
    // Stats labels
    private JLabel statTotalCount;
    private JLabel statTodayCount;
    private JLabel statLatestName;
    
    // Fields
    private ModernTextField fullNameField, emailField, phoneField, addressField;
    private ModernTextField collegeField, courseField;
    private JComboBox<String> yearBox;
    
    // Error helper labels
    private JLabel errName, errEmail, errPhone;
    
    // Table
    private DefaultTableModel tableModel;
    private JTable studentTable;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JLabel footerCountLabel;
    
    // QR Codes
    private QRComponent wifiQR, instaQR, fbQR;
    private JLabel wifiLabel, instaLabel, fbLabel;
    
    // Dynamic analytics chart
    private RegistrationChart analyticsChart;
    
    // Recent feed timeline
    private JPanel timelineContainer;

    // ==========================================
    // CONSTRUCTOR & INITIALIZER
    // ==========================================
    public WelcomeApp() {
        setTitle("स्वागत (Swagat) - College Event Registration System");
        setSize(1320, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Root container
        JPanel rootPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(backgroundColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        // Assemble parts
        rootPanel.add(createSidebarPanel(), BorderLayout.WEST);
        
        JPanel mainWrapper = new JPanel(new BorderLayout(0, 0));
        mainWrapper.setOpaque(false);
        mainWrapper.setBorder(new EmptyBorder(25, 25, 25, 25));
        mainWrapper.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Setup CardLayout main view
        cardLayout = new CardLayout();
        mainContentArea = new JPanel(cardLayout);
        mainContentArea.setOpaque(false);
        mainContentArea.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Initialize Table Model (Shared across views)
        tableModel = new DefaultTableModel(new String[]{
            "Entry Time", "Full Name", "Email", "Phone", "Address", "College", "Course", "Year/Sem"
        }, 0);
        
        // Add pages
        mainContentArea.add(createDashboardView(), "DASHBOARD");
        mainContentArea.add(createRegisterView(), "REGISTER");
        mainContentArea.add(createDirectoryView(), "DIRECTORY");
        mainContentArea.add(createQRDeskView(), "QRDESK");
        mainContentArea.add(createSettingsView(), "SETTINGS");
        
        mainWrapper.add(mainContentArea, BorderLayout.CENTER);
        rootPanel.add(mainWrapper, BorderLayout.CENTER);
        
        add(rootPanel);
        
        // Initialize listener for table updates
        tableModel.addTableModelListener(e -> {
            updateStats();
            updateTimelineFeed();
        });
        
        // Switch to default card
        switchTab(0, "DASHBOARD");
    }

    // ==========================================
    // SIDEBAR NAVIGATION VIEW
    // ==========================================
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(secondaryCardBg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(borderColor);
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 0, 30, 0));
        
        // System Brand Logo
        JLabel logoIcon = new JLabel("स्वागत", SwingConstants.CENTER);
        logoIcon.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoIcon.setForeground(Color.WHITE);
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logoSub = new JLabel("SWAGAT REGISTRATION", SwingConstants.CENTER);
        logoSub.setFont(new Font("Segoe UI", Font.BOLD, 11));
        logoSub.setForeground(successColor);
        logoSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        sidebar.add(logoIcon);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(logoSub);
        sidebar.add(Box.createVerticalStrut(45));
        
        // Sidebar Switchers
        String[] labels = {"Dashboard", "Register Student", "Directory List", "QR Reference", "Settings"};
        int[] iconTypes = {0, 1, 2, 3, 4};
        String[] cardNames = {"DASHBOARD", "REGISTER", "DIRECTORY", "QRDESK", "SETTINGS"};
        
        sidebarButtons = new SidebarButton[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            sidebarButtons[i] = new SidebarButton(labels[i], new VectorIcon(iconTypes[i]));
            sidebarButtons[i].setMaximumSize(new Dimension(260, 50));
            sidebarButtons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebarButtons[i].addActionListener(e -> switchTab(index, cardNames[index]));
            
            sidebar.add(sidebarButtons[i]);
            sidebar.add(Box.createVerticalStrut(8));
        }
        
        // Sidebar footer info
        sidebar.add(Box.createVerticalGlue());
        
        JLabel authorLabel = new JLabel("v1.2.0 • Alson Basnet", SwingConstants.CENTER);
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        authorLabel.setForeground(textMuted);
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        sidebar.add(authorLabel);
        return sidebar;
    }
    
    private void switchTab(int index, String cardName) {
        for (int i = 0; i < sidebarButtons.length; i++) {
            sidebarButtons[i].setActive(i == index);
        }
        activeTitleLabel.setText(cardName + " / " + getNepaliTitle(cardName));
        cardLayout.show(mainContentArea, cardName);
    }
    
    private String getNepaliTitle(String name) {
        switch(name) {
            case "DASHBOARD": return "ड्यासबोर्ड";
            case "REGISTER": return "दर्ता फारम";
            case "DIRECTORY": return "विद्यार्थी विवरण";
            case "QRDESK": return "क्युआर डेस्क";
            case "SETTINGS": return "सेटिङ Configuration";
            default: return "";
        }
    }

    // ==========================================
    // TOP HEADER PANEL
    // ==========================================
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        // Active panel label
        activeTitleLabel = new JLabel("DASHBOARD / ड्यासबोर्ड");
        activeTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        activeTitleLabel.setForeground(textPrimary);
        
        // College Name Banner
        collegeTitleLabel = new JLabel(collegeName);
        collegeTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        collegeTitleLabel.setForeground(primaryAccent);
        collegeTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        header.add(activeTitleLabel, BorderLayout.WEST);
        header.add(collegeTitleLabel, BorderLayout.EAST);
        
        return header;
    }

    // ==========================================
    // VIEW 1: DUST-BOARD (DASHBOARD)
    // ==========================================
    private JPanel createDashboardView() {
        JPanel view = new JPanel(new BorderLayout(20, 20));
        view.setOpaque(false);
        
        // 1. Stats Row at the top
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        statsGrid.setOpaque(false);
        
        statTotalCount = new JLabel("0", SwingConstants.LEFT);
        statTodayCount = new JLabel("0", SwingConstants.LEFT);
        statLatestName = new JLabel("None", SwingConstants.LEFT);
        
        statsGrid.add(createStatCard("Total Registrations", statTotalCount, new Color(99, 102, 241)));
        statsGrid.add(createStatCard("Check-ins Today", statTodayCount, new Color(6, 182, 212)));
        statsGrid.add(createStatCard("Latest Check-in", statLatestName, new Color(139, 92, 246)));
        
        // System Pulse card
        ModernCard pulseCard = new ModernCard();
        pulseCard.setLayout(new BorderLayout());
        JLabel pTitle = new JLabel("System Status");
        pTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pTitle.setForeground(textMuted);
        
        PulsingStatusLabel statusPulse = new PulsingStatusLabel();
        
        pulseCard.add(pTitle, BorderLayout.NORTH);
        pulseCard.add(statusPulse, BorderLayout.CENTER);
        statsGrid.add(pulseCard);
        
        view.add(statsGrid, BorderLayout.NORTH);
        
        // 2. Middle Row: Analytics Chart (Left) + Recent Timeline Feed (Right)
        JPanel middleSplit = new JPanel(new GridBagLayout());
        middleSplit.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        
        // Chart Card
        ModernCard chartCard = new ModernCard();
        chartCard.setLayout(new BorderLayout());
        JLabel chartTitle = new JLabel("Course Enrollment Trends");
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chartTitle.setForeground(textPrimary);
        chartTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        analyticsChart = new RegistrationChart(tableModel);
        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(analyticsChart, BorderLayout.CENTER);
        
        // Timeline Card
        ModernCard timelineCard = new ModernCard();
        timelineCard.setLayout(new BorderLayout());
        JLabel timelineTitle = new JLabel("Recent Registration Feed");
        timelineTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timelineTitle.setForeground(textPrimary);
        timelineTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        timelineContainer = new JPanel();
        timelineContainer.setOpaque(false);
        timelineContainer.setLayout(new BoxLayout(timelineContainer, BoxLayout.Y_AXIS));
        
        JScrollPane timelineScroll = new JScrollPane(timelineContainer);
        timelineScroll.setOpaque(false);
        timelineScroll.getViewport().setOpaque(false);
        timelineScroll.setBorder(null);
        
        timelineCard.add(timelineTitle, BorderLayout.NORTH);
        timelineCard.add(timelineScroll, BorderLayout.CENTER);
        
        // Set weights
        gbc.gridx = 0;
        gbc.weightx = 0.6; // Chart takes 60%
        middleSplit.add(chartCard, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.4; // Timeline takes 40%
        gbc.insets = new Insets(0, 20, 0, 0);
        middleSplit.add(timelineCard, gbc);
        
        view.add(middleSplit, BorderLayout.CENTER);
        
        return view;
    }
    
    private ModernCard createStatCard(String title, JLabel valueLabel, Color iconColor) {
        ModernCard card = new ModernCard() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconColor);
                g2.fillRect(0, 0, 6, getHeight());
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(5, 5));
        
        JLabel tLabel = new JLabel(title);
        tLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tLabel.setForeground(textMuted);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(textPrimary);
        
        card.add(tLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    // ==========================================
    // VIEW 2: REGISTRATION FORM VIEW
    // ==========================================
    private JPanel createRegisterView() {
        JPanel view = new JPanel(new BorderLayout());
        view.setOpaque(false);
        
        ModernCard formCard = new ModernCard();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 12, 8, 12);
        
        // Section Title: Personal Info
        JLabel section1 = new JLabel("व्यक्तिगत विवरण / Personal Details");
        section1.setFont(new Font("Segoe UI", Font.BOLD, 18));
        section1.setForeground(secondaryAccent);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formCard.add(section1, gbc);
        
        // Separator line
        JPanel sep1 = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(borderColor);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        sep1.setPreferredSize(new Dimension(0, 1));
        gbc.gridy = 1;
        formCard.add(sep1, gbc);
        
        // Personal Fields Grid (2 Columns)
        gbc.gridwidth = 1;
        
        // Row 1: Full Name & Email
        fullNameField = new ModernTextField("Enter full name");
        errName = new JLabel("");
        errName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errName.setForeground(dangerColor);
        
        JPanel namePanel = createFieldContainer("Student Full Name *", fullNameField, errName);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.5;
        formCard.add(namePanel, gbc);
        
        emailField = new ModernTextField("example@college.edu");
        errEmail = new JLabel("");
        errEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errEmail.setForeground(dangerColor);
        
        JPanel emailPanel = createFieldContainer("Email Address *", emailField, errEmail);
        gbc.gridx = 1; gbc.weightx = 0.5;
        formCard.add(emailPanel, gbc);
        
        // Row 2: Phone & Address
        phoneField = new ModernTextField("10-digit phone number");
        errPhone = new JLabel("");
        errPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errPhone.setForeground(dangerColor);
        
        JPanel phonePanel = createFieldContainer("Mobile Phone Number *", phoneField, errPhone);
        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(phonePanel, gbc);
        
        addressField = new ModernTextField("City, Country");
        JPanel addrPanel = createFieldContainer("Permanent Address", addressField, null);
        gbc.gridx = 1;
        formCard.add(addrPanel, gbc);
        
        // Section Title: Academic
        JLabel section2 = new JLabel("शैक्षिक विवरण / Academic Information");
        section2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        section2.setForeground(secondaryAccent);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formCard.add(section2, gbc);
        
        JPanel sep2 = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(borderColor);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        sep2.setPreferredSize(new Dimension(0, 1));
        gbc.gridy = 5;
        formCard.add(sep2, gbc);
        
        gbc.gridwidth = 1;
        
        // Row 3: College Name & Course
        collegeField = new ModernTextField("Itahari International College");
        collegeField.setText(collegeName); // Default
        JPanel colPanel = createFieldContainer("Institution / College Name", collegeField, null);
        gbc.gridx = 0; gbc.gridy = 6;
        formCard.add(colPanel, gbc);
        
        courseField = new ModernTextField("e.g. BCA, BBA, CSIT");
        JPanel coursePanel = createFieldContainer("Course / Major Stream", courseField, null);
        gbc.gridx = 1;
        formCard.add(coursePanel, gbc);
        
        // Row 4: Year / Semester Selector
        yearBox = new JComboBox<>(new String[]{
            "1st Year / 1st Sem", "1st Year / 2nd Sem",
            "2nd Year / 3rd Sem", "2nd Year / 4th Sem",
            "3rd Year / 5th Sem", "3rd Year / 6th Sem",
            "4th Year / 7th Sem", "4th Year / 8th Sem"
        }) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        yearBox.setBackground(inputBg);
        yearBox.setForeground(Color.WHITE);
        yearBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        yearBox.setBorder(new EmptyBorder(5, 5, 5, 5));
        yearBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = super.createArrowButton();
                button.setBackground(cardBg);
                button.setBorder(BorderFactory.createEmptyBorder());
                return button;
            }
        });
        
        JPanel yearPanel = createFieldContainer("Year & Semester", yearBox, null);
        gbc.gridx = 0; gbc.gridy = 7;
        formCard.add(yearPanel, gbc);
        
        // Row 5: Action buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonRow.setOpaque(false);
        
        ModernButton btnClear = new ModernButton("Reset Fields");
        btnClear.setCustomGradient(new Color(100, 116, 139), new Color(71, 85, 105)); // Slate colors
        btnClear.addActionListener(e -> resetFormFields());
        
        ModernButton btnRegister = new ModernButton("Register Student");
        btnRegister.addActionListener(e -> registerFormSubmit());
        
        buttonRow.add(btnClear);
        buttonRow.add(btnRegister);
        
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 12, 10, 12);
        formCard.add(buttonRow, gbc);
        
        view.add(formCard, BorderLayout.CENTER);
        return view;
    }
    
    private JPanel createFieldContainer(String label, JComponent field, JLabel errorLabel) {
        JPanel c = new JPanel(new BorderLayout(5, 5));
        c.setOpaque(false);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(textPrimary);
        
        c.add(lbl, BorderLayout.NORTH);
        c.add(field, BorderLayout.CENTER);
        
        if (errorLabel != null) {
            c.add(errorLabel, BorderLayout.SOUTH);
        }
        return c;
    }
    
    private void resetFormFields() {
        fullNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        collegeField.setText(collegeName);
        courseField.setText("");
        yearBox.setSelectedIndex(0);
        
        errName.setText("");
        errEmail.setText("");
        errPhone.setText("");
    }
    
    private void registerFormSubmit() {
        String name = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String col = collegeField.getText().trim();
        String course = courseField.getText().trim();
        String yearSem = (String) yearBox.getSelectedItem();
        
        boolean valid = true;
        
        // Name Validation
        if (name.isEmpty()) {
            errName.setText("Full name is required.");
            valid = false;
        } else {
            errName.setText("");
        }
        
        // Email Validation
        if (email.isEmpty()) {
            errEmail.setText("Email is required.");
            valid = false;
        } else if (!email.contains("@") || !email.contains(".")) {
            errEmail.setText("Please enter a valid email address.");
            valid = false;
        } else {
            errEmail.setText("");
        }
        
        // Phone Validation
        if (phone.isEmpty()) {
            errPhone.setText("Phone number is required.");
            valid = false;
        } else if (phone.length() < 9) {
            errPhone.setText("Phone number must be at least 9 digits.");
            valid = false;
        } else {
            errPhone.setText("");
        }
        
        if (!valid) {
            showNotification("Validation Errors", "Please correct the highlighted inputs to proceed.", dangerColor);
            return;
        }
        
        // Insert entry at index 0 (latest first)
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        tableModel.insertRow(0, new Object[]{
            timeStr, name, email, phone, address, col, course, yearSem
        });
        
        showNotification("Registration Successful", name + " registered successfully!", successColor);
        
        // Reset form
        resetFormFields();
        
        // Jump back to Dashboard automatically to show updated stats and charts!
        switchTab(0, "DASHBOARD");
    }

    // ==========================================
    // VIEW 3: STUDENT DIRECTORY VIEW
    // ==========================================
    private JPanel createDirectoryView() {
        JPanel view = new JPanel(new BorderLayout(20, 20));
        view.setOpaque(false);
        
        // Top Filter bar
        ModernCard topCard = new ModernCard();
        topCard.setLayout(new BorderLayout(15, 0));
        topCard.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel searchLabel = new JLabel("Live Search / खोज: ");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        searchLabel.setForeground(textPrimary);
        
        ModernTextField searchField = new ModernTextField("Type to search student by name, phone, course, college...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        topCard.add(searchLabel, BorderLayout.WEST);
        topCard.add(searchField, BorderLayout.CENTER);
        
        view.add(topCard, BorderLayout.NORTH);
        
        // Table Panel
        ModernCard tableCard = new ModernCard();
        tableCard.setLayout(new BorderLayout(0, 10));
        
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(38);
        studentTable.setShowGrid(false);
        studentTable.setIntercellSpacing(new Dimension(0, 0));
        studentTable.setBackground(cardBg);
        studentTable.setForeground(textPrimary);
        studentTable.setSelectionBackground(primaryAccent);
        studentTable.setSelectionForeground(Color.WHITE);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Table Header Styling
        JTableHeader header = studentTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 42));
        header.setOpaque(false);
        header.setBackground(secondaryCardBg);
        header.setForeground(textPrimary);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Apply cell renderer for zebra strips and cell padding
        ModernTableCellRenderer cellRenderer = new ModernTableCellRenderer();
        for (int i = 0; i < studentTable.getColumnCount(); i++) {
            studentTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        
        // Row sorter for search feature
        tableSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(tableSorter);
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
            
            private void filterTable() {
                String query = searchField.getText().trim();
                if (query.isEmpty()) {
                    tableSorter.setRowFilter(null);
                } else {
                    tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
                }
                updateFooterCount();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
        scrollPane.setBackground(cardBg);
        scrollPane.getViewport().setBackground(cardBg);
        
        // Table Actions Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        
        footerCountLabel = new JLabel("Showing 0 entries of 0 total");
        footerCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        footerCountLabel.setForeground(textMuted);
        
        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonGroup.setOpaque(false);
        
        ModernButton btnDelete = new ModernButton("Delete Entry");
        btnDelete.setCustomGradient(new Color(225, 29, 72), new Color(190, 18, 60)); // Crimson gradients
        btnDelete.addActionListener(e -> deleteSelectedStudent());
        
        ModernButton btnExport = new ModernButton("Export Excel");
        btnExport.setCustomGradient(new Color(16, 185, 129), new Color(4, 120, 87));  // Emerald gradients
        btnExport.addActionListener(e -> exportToExcel());
        
        buttonGroup.add(btnDelete);
        buttonGroup.add(btnExport);
        
        footerPanel.add(footerCountLabel, BorderLayout.WEST);
        footerPanel.add(buttonGroup, BorderLayout.EAST);
        
        tableCard.add(scrollPane, BorderLayout.CENTER);
        tableCard.add(footerPanel, BorderLayout.SOUTH);
        
        view.add(tableCard, BorderLayout.CENTER);
        
        // Inital Count update
        updateFooterCount();
        
        return view;
    }
    
    private void updateFooterCount() {
        int showing = studentTable.getRowCount();
        int total = tableModel.getRowCount();
        footerCountLabel.setText("Showing " + showing + " entries of " + total + " total records");
    }
    
    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            showNotification("Selection Required", "Please select a student row from the directory to delete.", dangerColor);
            return;
        }
        
        // Convert to model index in case sorting is active
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        String studentName = String.valueOf(tableModel.getValueAt(modelRow, 1));
        
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to permanently delete registration log for " + studentName + "?",
            "Confirm Delete Action",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            tableModel.removeRow(modelRow);
            showNotification("Entry Deleted", studentName + "'s registration has been removed.", successColor);
            updateFooterCount();
        }
    }

    // ==========================================
    // VIEW 4: COLLEGE QR DESK VIEW
    // ==========================================
    private JPanel createQRDeskView() {
        JPanel view = new JPanel(new BorderLayout(20, 20));
        view.setOpaque(false);
        
        // 1. QR Cards Grid (Three Cards)
        JPanel qrGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        qrGrid.setOpaque(false);
        
        wifiQR = new QRComponent("WIFI:S:IIC_Events;T:WPA;P:Welcome2026;H:false;;");
        instaQR = new QRComponent("https://www.instagram.com/itahariinternationalcollege");
        fbQR = new QRComponent("https://www.facebook.com/itahariinternationalcollege");
        
        wifiLabel = new JLabel("IIC Events", SwingConstants.CENTER);
        instaLabel = new JLabel("@itahariinternationalcollege", SwingConstants.CENTER);
        fbLabel = new JLabel("Itahari International College", SwingConstants.CENTER);
        
        qrGrid.add(createQRCardPanel("College Event WiFi Connection", wifiQR, wifiLabel, new Color(37, 99, 235)));
        qrGrid.add(createQRCardPanel("Instagram Official Page", instaQR, instaLabel, new Color(219, 39, 119)));
        qrGrid.add(createQRCardPanel("Facebook Official Page", fbQR, fbLabel, new Color(13, 148, 136)));
        
        view.add(qrGrid, BorderLayout.CENTER);
        
        // 2. Interactive QR Configurator Panel
        ModernCard configCard = new ModernCard();
        configCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        JLabel configTitle = new JLabel("Live QR Reference Configurator (क्युआर कोड व्यवस्थापन)");
        configTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        configTitle.setForeground(secondaryAccent);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        configCard.add(configTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        
        // Selection
        JLabel selectLabel = new JLabel("Select Target QR Card:");
        selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selectLabel.setForeground(textPrimary);
        gbc.gridx = 0; gbc.gridy = 1;
        configCard.add(selectLabel, gbc);
        
        JComboBox<String> targetCombo = new JComboBox<>(new String[]{"College Event WiFi", "Instagram Page", "Facebook Page"});
        targetCombo.setBackground(inputBg);
        targetCombo.setForeground(Color.WHITE);
        targetCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        targetCombo.setBorder(BorderFactory.createLineBorder(borderColor));
        gbc.gridx = 1;
        configCard.add(targetCombo, gbc);
        
        // Content Edit Field
        JLabel editLabel = new JLabel("SSID / Handle / URL Link:");
        editLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        editLabel.setForeground(textPrimary);
        gbc.gridx = 0; gbc.gridy = 2;
        configCard.add(editLabel, gbc);
        
        ModernTextField valField = new ModernTextField("Enter credentials...");
        valField.setText("WIFI:S:MidValley_Events;T:WPA;P:Welcome2026;H:false;;"); // Wifi by default
        gbc.gridx = 1;
        configCard.add(valField, gbc);
        
        // Apply Button
        ModernButton btnApply = new ModernButton("Update QR Code");
        btnApply.addActionListener(e -> {
            int selectedIdx = targetCombo.getSelectedIndex();
            String newVal = valField.getText().trim();
            if (newVal.isEmpty()) {
                showNotification("Input Required", "Value string cannot be empty.", dangerColor);
                return;
            }
            if (selectedIdx == 0) { // WiFi
                wifiQR.setContent(newVal);
                // Parse SSID if formatted correctly
                if (newVal.contains("S:") && newVal.contains(";")) {
                    int sStart = newVal.indexOf("S:") + 2;
                    int sEnd = newVal.indexOf(";", sStart);
                    wifiLabel.setText(newVal.substring(sStart, sEnd));
                } else {
                    wifiLabel.setText(newVal);
                }
            } else if (selectedIdx == 1) { // Instagram
                instaQR.setContent(newVal);
                instaLabel.setText(newVal.substring(newVal.lastIndexOf("/") + 1));
            } else { // Facebook
                fbQR.setContent(newVal);
                fbLabel.setText(newVal.substring(newVal.lastIndexOf("/") + 1));
            }
            showNotification("QR Refreshed", "QR Code pattern regenerated instantly!", successColor);
        });
        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.2;
        configCard.add(btnApply, gbc);
        
        targetCombo.addActionListener(e -> {
            int idx = targetCombo.getSelectedIndex();
            if (idx == 0) valField.setText(wifiQR.getContent());
            else if (idx == 1) valField.setText(instaQR.getContent());
            else valField.setText(fbQR.getContent());
        });
        
        view.add(configCard, BorderLayout.SOUTH);
        
        return view;
    }
    
    private ModernCard createQRCardPanel(String heading, QRComponent qr, JLabel label, Color cardAccent) {
        ModernCard card = new ModernCard() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardAccent);
                g2.fillRect(0, 0, getWidth(), 6); // Accent top border
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout(15, 15));
        card.setBorder(new EmptyBorder(25, 20, 20, 20));
        
        JLabel headingLabel = new JLabel(heading, SwingConstants.CENTER);
        headingLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        headingLabel.setForeground(textPrimary);
        
        // Wrap QR component to align it center
        JPanel qrWrapper = new JPanel(new GridBagLayout());
        qrWrapper.setOpaque(false);
        qr.setQRColor(backgroundColor); // Match background
        qrWrapper.add(qr);
        
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(textMuted);
        
        card.add(headingLabel, BorderLayout.NORTH);
        card.add(qrWrapper, BorderLayout.CENTER);
        card.add(label, BorderLayout.SOUTH);
        
        return card;
    }

    // ==========================================
    // VIEW 5: CONFIGURATION & SETTINGS
    // ==========================================
    private JPanel createSettingsView() {
        JPanel view = new JPanel(new GridBagLayout());
        view.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        
        // Theme & Config Card
        ModernCard c1 = new ModernCard();
        c1.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(12, 18, 12, 18);
        gc.weightx = 1.0;
        
        JLabel sysTitle = new JLabel("System Branding & Customizations");
        sysTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sysTitle.setForeground(secondaryAccent);
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        c1.add(sysTitle, gc);
        
        // Separator
        JPanel divider = new JPanel() {
            @Override protected void paintComponent(Graphics g) { g.setColor(borderColor); g.fillRect(0,0,getWidth(),1); }
        };
        divider.setPreferredSize(new Dimension(0, 1));
        gc.gridy = 1;
        c1.add(divider, gc);
        
        gc.gridwidth = 1;
        
        // College setting
        JLabel clgL = new JLabel("Display Institution Name:");
        clgL.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clgL.setForeground(textPrimary);
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0.3;
        c1.add(clgL, gc);
        
        ModernTextField clgInput = new ModernTextField("Institution name");
        clgInput.setText(collegeName);
        gc.gridx = 1; gc.weightx = 0.7;
        c1.add(clgInput, gc);
        
        // Excel Filename setting
        JLabel excL = new JLabel("Excel Export File Name:");
        excL.setFont(new Font("Segoe UI", Font.BOLD, 14));
        excL.setForeground(textPrimary);
        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0.3;
        c1.add(excL, gc);
        
        ModernTextField excInput = new ModernTextField("welcome_entries.xlsx");
        excInput.setText(excelFileName);
        gc.gridx = 1; gc.weightx = 0.7;
        c1.add(excInput, gc);
        
        // Theme Select Buttons
        JLabel thmL = new JLabel("Select UI Color Profile:");
        thmL.setFont(new Font("Segoe UI", Font.BOLD, 14));
        thmL.setForeground(textPrimary);
        gc.gridx = 0; gc.gridy = 4; gc.weightx = 0.3;
        c1.add(thmL, gc);
        
        JPanel thmGrid = new JPanel(new GridLayout(1, 3, 10, 0));
        thmGrid.setOpaque(false);
        
        JButton thmSlate = new JButton("Midnight Slate (Dark)");
        JButton thmSapphire = new JButton("Sapphire Blue (Night)");
        JButton thmLight = new JButton("Ice Light (Day)");
        
        styleSimpleButton(thmSlate);
        styleSimpleButton(thmSapphire);
        styleSimpleButton(thmLight);
        
        thmSlate.addActionListener(e -> switchTheme("Midnight Slate"));
        thmSapphire.addActionListener(e -> switchTheme("Sapphire Blue"));
        thmLight.addActionListener(e -> switchTheme("Ice Light"));
        
        thmGrid.add(thmSlate);
        thmGrid.add(thmSapphire);
        thmGrid.add(thmLight);
        
        gc.gridx = 1; gc.weightx = 0.7;
        c1.add(thmGrid, gc);
        
        // Reset Database & Save config buttons
        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsRow.setOpaque(false);
        
        ModernButton btnWipe = new ModernButton("Wipe All Logs");
        btnWipe.setCustomGradient(new Color(239, 68, 68), new Color(185, 28, 28)); // Dangerous red
        btnWipe.addActionListener(e -> wipeAllData());
        
        ModernButton btnSaveConfig = new ModernButton("Apply Configurations");
        btnSaveConfig.addActionListener(e -> {
            collegeName = clgInput.getText().trim();
            excelFileName = excInput.getText().trim();
            if (collegeName.isEmpty()) collegeName = "Itahari International College";
            if (excelFileName.isEmpty()) excelFileName = "swagat_entries.xlsx";
            
            // Apply updates
            collegeTitleLabel.setText(collegeName);
            collegeField.setText(collegeName);
            showNotification("Config Saved", "Branding configurations updated successfully!", successColor);
        });
        
        actionsRow.add(btnWipe);
        actionsRow.add(btnSaveConfig);
        
        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2;
        gc.insets = new Insets(30, 18, 12, 18);
        c1.add(actionsRow, gc);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weighty = 0.7;
        view.add(c1, gbc);
        
        return view;
    }
    
    private void styleSimpleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(30, 41, 59));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(new LineBorder(new Color(71, 85, 105), 1, true), new EmptyBorder(8, 10, 8, 10)));
    }
    
    private void switchTheme(String themeName) {
        applyTheme(themeName);
        // Trigger swing frame redraw completely to change theme styles instantly!
        SwingUtilities.updateComponentTreeUI(this);
        showNotification("Theme Changed", "Color profile switched to: " + themeName, successColor);
    }
    
    private void wipeAllData() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "CRITICAL ACTION: This will permanently wipe all registered student logs and reset dashboards.\nDo you wish to proceed?",
            "Wipe Database Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            tableModel.setRowCount(0);
            showNotification("Logs Wiped", "All student check-in logs successfully cleared.", dangerColor);
        }
    }

    // ==========================================
    // BACKEND BUSINESS & EXCEL LOGIC (POI)
    // ==========================================
    private void updateStats() {
        int total = tableModel.getRowCount();
        statTotalCount.setText(String.valueOf(total));
        
        // Count entries today
        String todayPrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int todayCount = 0;
        for (int i = 0; i < total; i++) {
            String time = String.valueOf(tableModel.getValueAt(i, 0));
            if (time.startsWith(todayPrefix)) {
                todayCount++;
            }
        }
        statTodayCount.setText(String.valueOf(todayCount));
        
        // Latest Registrant
        if (total > 0) {
            String latest = String.valueOf(tableModel.getValueAt(0, 1));
            // Cap character length if name is too long for stat box
            if (latest.length() > 14) latest = latest.substring(0, 12) + "..";
            statLatestName.setText(latest);
        } else {
            statLatestName.setText("None");
        }
    }
    
    private void updateTimelineFeed() {
        timelineContainer.removeAll();
        int total = tableModel.getRowCount();
        int displayCount = Math.min(total, 4); // Show maximum last 4 registrations
        
        if (displayCount == 0) {
            JLabel emptyFeed = new JLabel("Timeline is empty. Waiting for records...", SwingConstants.CENTER);
            emptyFeed.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyFeed.setForeground(textMuted);
            emptyFeed.setBorder(new EmptyBorder(40, 0, 0, 0));
            emptyFeed.setAlignmentX(Component.CENTER_ALIGNMENT);
            timelineContainer.add(emptyFeed);
        } else {
            for (int i = 0; i < displayCount; i++) {
                String name = String.valueOf(tableModel.getValueAt(i, 1));
                String course = String.valueOf(tableModel.getValueAt(i, 6));
                String time = String.valueOf(tableModel.getValueAt(i, 0));
                
                // Strip dates for time display in timeline
                if (time.contains(" ")) {
                    time = time.substring(time.indexOf(" ") + 1);
                }
                
                timelineContainer.add(createTimelineItem(name, course, time));
                if (i < displayCount - 1) {
                    timelineContainer.add(Box.createVerticalStrut(10));
                }
            }
        }
        timelineContainer.revalidate();
        timelineContainer.repaint();
    }
    
    private JPanel createTimelineItem(String name, String course, String time) {
        JPanel item = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(secondaryCardBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(8, 12, 8, 12));
        
        // Initial Avatar circle
        String initials = "";
        if (!name.isEmpty()) {
            String[] parts = name.split(" ");
            initials += parts[0].substring(0, 1).toUpperCase();
            if (parts.length > 1 && !parts[1].isEmpty()) {
                initials += parts[1].substring(0, 1).toUpperCase();
            }
        }
        final String finalInitials = initials;
        
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(primaryAccent);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(finalInitials)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(finalInitials, x, y);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(34, 34));
        avatar.setOpaque(false);
        
        // Center text info
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        textPanel.setOpaque(false);
        
        JLabel nameL = new JLabel(name);
        nameL.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameL.setForeground(textPrimary);
        
        JLabel courseL = new JLabel(course != null && !course.isEmpty() ? course : "No Major Course Listed");
        courseL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courseL.setForeground(textMuted);
        
        textPanel.add(nameL);
        textPanel.add(courseL);
        
        // Right Timestamp
        JLabel timeL = new JLabel(time);
        timeL.setFont(new Font("Segoe UI", Font.BOLD, 12));
        timeL.setForeground(successColor);
        
        item.add(avatar, BorderLayout.WEST);
        item.add(textPanel, BorderLayout.CENTER);
        item.add(timeL, BorderLayout.EAST);
        
        return item;
    }
    
    private void exportToExcel() {
        int rowCount = tableModel.getRowCount();
        if (rowCount == 0) {
            showNotification("No Data", "There are no registration entries to export into Excel.", dangerColor);
            return;
        }
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Swagat Entries");
            
            // Stylish header cells in Excel sheet
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);
            
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.INDIGO.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(tableModel.getColumnName(i));
                cell.setCellStyle(headerStyle);
            }
            
            // Write records
            for (int r = 0; r < rowCount; r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    Object val = tableModel.getValueAt(r, c);
                    row.createCell(c).setCellValue(val != null ? val.toString() : "");
                }
            }
            
            // Auto size
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                sheet.autoSizeColumn(col);
            }
            
            File file = new File(excelFileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            
            showNotification("Excel Exported", "Report written to: " + file.getName(), successColor);
        } catch (Exception ex) {
            showNotification("Export Error", "Export failed: " + ex.getMessage(), dangerColor);
        }
    }
    
    // ==========================================
    // TOAST FLOATING PREMIUM NOTIFICATION
    // ==========================================
    public void showNotification(String title, String message, Color tintColor) {
        JDialog dialog = new JDialog(this, false);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0)); // Transparent JDialog
        
        ModernCard panel = new ModernCard() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded container
                g2.setColor(cardBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Draw left indicator bar
                g2.setColor(tintColor);
                g2.fillRect(0, 0, 8, getHeight());
                
                // Draw border outline
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                
                g2.dispose();
            }
        };
        panel.setLayout(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(12, 18, 12, 18));
        
        JLabel titleL = new JLabel(title);
        titleL.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleL.setForeground(tintColor);
        
        JLabel msgL = new JLabel(message);
        msgL.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgL.setForeground(textPrimary);
        
        panel.add(titleL, BorderLayout.NORTH);
        panel.add(msgL, BorderLayout.CENTER);
        
        dialog.add(panel);
        dialog.setSize(400, 85);
        
        // Position at top-middle of active frame
        dialog.setLocation(
            getX() + (getWidth() - dialog.getWidth()) / 2,
            getY() + 45
        );
        
        dialog.setVisible(true);
        
        // Dismiss automatically after 2.2 seconds
        Timer timer = new Timer(2200, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    // ==========================================
    // CUSTOM VECTOR RENDER OBJECTS (NESTED CLASSES)
    // ==========================================
    
    // Custom painted button subclass
    public static class ModernButton extends JButton {
        private boolean hovered = false;
        private boolean pressed = false;
        private Color customBgStart = null;
        private Color customBgEnd = null;

        public ModernButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 20, 10, 20));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { hovered = false; pressed = false; repaint(); }
                public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
                public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
            });
        }

        public void setCustomGradient(Color start, Color end) {
            this.customBgStart = start;
            this.customBgEnd = end;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            Color start = customBgStart != null ? customBgStart : primaryAccent;
            Color end = customBgEnd != null ? customBgEnd : secondaryAccent;

            if (pressed) {
                start = start.darker();
                end = end.darker();
            } else if (hovered) {
                start = start.brighter();
                end = end.brighter();
            }

            GradientPaint gp = new GradientPaint(0, 0, start, w, h, end);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h, 12, 12);

            if (hovered) {
                g2.setColor(new Color(255, 255, 255, 45));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 10, 10);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Custom painted input subclass
    public static class ModernTextField extends JTextField {
        private final String placeholder;
        private boolean isFocused = false;

        public ModernTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setCaretColor(Color.WHITE);
            setSelectedTextColor(Color.WHITE);
            setSelectionColor(new Color(99, 102, 241, 140));
            setBorder(new EmptyBorder(8, 14, 8, 14));
            setFont(new Font("Segoe UI", Font.PLAIN, 15));
            setForeground(Color.WHITE);

            addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent e) { isFocused = true; repaint(); }
                public void focusLost(FocusEvent e) { isFocused = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Set dynamic background color based on active theme
            g2.setColor(inputBg);
            g2.fillRoundRect(0, 0, w, h, 10, 10);

            if (isFocused) {
                g2.setColor(primaryAccent);
                g2.setStroke(new BasicStroke(1.6f));
            } else {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
            g2.dispose();

            super.paintComponent(g);

            if (getText().isEmpty() && placeholder != null) {
                Graphics2D gText = (Graphics2D) g.create();
                gText.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                gText.setColor(textMuted);
                gText.setFont(getFont().deriveFont(Font.ITALIC));
                FontMetrics fm = gText.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                gText.drawString(placeholder, 14, y);
                gText.dispose();
            }
        }
    }

    // Custom painted Card panel
    public static class ModernCard extends JPanel {
        private final int arc = 16;
        public ModernCard() {
            setOpaque(false);
            setBorder(new EmptyBorder(18, 18, 18, 18));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cardBg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Sidebar navigation button subclass
    public static class SidebarButton extends JButton {
        private boolean active = false;
        private boolean hovered = false;

        public SidebarButton(String text, Icon icon) {
            super("  " + text);
            setIcon(icon);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 22, 12, 22));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (active) {
                g2.setColor(new Color(primaryAccent.getRed(), primaryAccent.getGreen(), primaryAccent.getBlue(), 35));
                g2.fillRect(0, 0, w, h);
                g2.setColor(primaryAccent);
                g2.fillRect(0, 0, 5, h);
                setForeground(primaryAccent);
            } else if (hovered) {
                g2.setColor(new Color(255, 255, 255, 8));
                g2.fillRect(0, 0, w, h);
                setForeground(Color.WHITE);
            } else {
                setForeground(textMuted);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Vector Icon programatic drawings
    public static class VectorIcon implements Icon {
        private final int type;
        public VectorIcon(int type) { this.type = type; }
        
        @Override public int getIconWidth() { return 18; }
        @Override public int getIconHeight() { return 18; }
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getForeground());
            g2.setStroke(new BasicStroke(1.8f));
            
            switch(type) {
                case 0: // Dashboard
                    g2.drawRect(x, y, 6, 6);
                    g2.drawRect(x + 10, y, 6, 6);
                    g2.drawRect(x, y + 10, 6, 6);
                    g2.drawRect(x + 10, y + 10, 6, 6);
                    break;
                case 1: // User plus
                    g2.drawOval(x + 2, y + 1, 8, 8);
                    g2.drawArc(x, y + 11, 12, 7, 0, 180);
                    g2.drawLine(x + 14, y + 5, x + 18, y + 5);
                    g2.drawLine(x + 16, y + 3, x + 16, y + 7);
                    break;
                case 2: // Directory list
                    g2.drawLine(x, y + 3, x + 18, y + 3);
                    g2.drawLine(x, y + 9, x + 18, y + 9);
                    g2.drawLine(x, y + 15, x + 18, y + 15);
                    g2.fillRect(x, y + 2, 2, 2);
                    g2.fillRect(x, y + 8, 2, 2);
                    g2.fillRect(x, y + 14, 2, 2);
                    break;
                case 3: // QR Code grid
                    g2.drawRect(x, y, 7, 7);
                    g2.drawRect(x + 11, y, 7, 7);
                    g2.drawRect(x, y + 11, 7, 7);
                    g2.fillRect(x + 2, y + 2, 3, 3);
                    g2.fillRect(x + 13, y + 2, 3, 3);
                    g2.fillRect(x + 2, y + 13, 3, 3);
                    g2.fillRect(x + 12, y + 12, 4, 4);
                    break;
                case 4: // Settings
                    g2.drawOval(x + 5, y + 5, 8, 8);
                    for (int a = 0; a < 360; a += 45) {
                        double rad = Math.toRadians(a);
                        int x1 = (int) (x + 9 + Math.cos(rad) * 4);
                        int y1 = (int) (y + 9 + Math.sin(rad) * 4);
                        int x2 = (int) (x + 9 + Math.cos(rad) * 8);
                        int y2 = (int) (y + 9 + Math.sin(rad) * 8);
                        g2.drawLine(x1, y1, x2, y2);
                    }
                    break;
            }
            g2.dispose();
        }
    }

    // Dynamic registrations statistics chart
    public static class RegistrationChart extends JComponent {
        private final DefaultTableModel model;
        
        public RegistrationChart(DefaultTableModel model) {
            this.model = model;
            model.addTableModelListener(e -> repaint());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Background grids
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{4.0f}, 0.0f));
            for (int y = 25; y < h - 45; y += 38) {
                g2.drawLine(40, y, w - 20, y);
            }

            // Gather dynamic counts by course
            Map<String, Integer> counts = new HashMap<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                String crs = String.valueOf(model.getValueAt(i, 6)); // Course is column index 6
                if (crs != null && !crs.trim().isEmpty()) {
                    counts.put(crs.toUpperCase(), counts.getOrDefault(crs.toUpperCase(), 0) + 1);
                }
            }

            if (counts.isEmpty()) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                g2.setColor(textMuted);
                FontMetrics fm = g2.getFontMetrics();
                String empty = "Waiting for student registration records to build dynamic analytics...";
                g2.drawString(empty, (w - fm.stringWidth(empty)) / 2, h / 2);
                g2.dispose();
                return;
            }

            // Find maximum count
            int max = 0;
            for (int c : counts.values()) {
                if (c > max) max = c;
            }

            int chartH = h - 65;
            int chartW = w - 85;
            int gap = 20;
            int size = counts.size();
            int barW = (chartW - gap * (size + 1)) / size;
            if (barW > 85) barW = 85;

            int idx = 0;
            int startX = 45 + (chartW - (barW * size + gap * (size - 1))) / 2;

            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                String name = entry.getKey();
                int val = entry.getValue();

                int barH = (int) (((double) val / max) * chartH);
                int x = startX + idx * (barW + gap);
                int y = h - 40 - barH;

                // Draw solid bar with rounded top corners
                GradientPaint barGrad = new GradientPaint(x, y, primaryAccent, x + barW, h - 40, secondaryAccent);
                g2.setPaint(barGrad);
                g2.fillRoundRect(x, y, barW, barH, 10, 10);
                // Flatten bottom corners
                g2.fillRect(x, h - 46, barW, 6);

                // Draw counts value
                g2.setColor(textPrimary);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fmVal = g2.getFontMetrics();
                String valS = String.valueOf(val);
                g2.drawString(valS, x + (barW - fmVal.stringWidth(valS)) / 2, y - 6);

                // Draw course labels
                g2.setColor(textMuted);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String labelStr = name.length() > 9 ? name.substring(0, 8) + "." : name;
                FontMetrics fmLbl = g2.getFontMetrics();
                g2.drawString(labelStr, x + (barW - fmLbl.stringWidth(labelStr)) / 2, h - 20);

                idx++;
            }
            g2.dispose();
        }
    }

    // Programmatic high-fidelity QR drawing
    public static class QRComponent extends JComponent {
        private String content;
        private Color qrColor = new Color(15, 23, 42);

        public QRComponent(String content) {
            this.content = content;
            setPreferredSize(new Dimension(170, 170));
        }

        public void setContent(String content) {
            this.content = content;
            repaint();
        }

        public String getContent() {
            return content;
        }

        public void setQRColor(Color color) {
            this.qrColor = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // White QR background canvas
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w, h, 14, 14);

            // Subtle border
            g2.setColor(new Color(220, 225, 235));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);

            // Realistic QR binary matrix grid
            int matrixSize = 25; // 25x25 grid standard
            int padding = 16;
            int qrW = w - padding * 2;
            int qrH = h - padding * 2;
            double cellW = (double) qrW / matrixSize;
            double cellH = (double) qrH / matrixSize;

            boolean[][] matrix = new boolean[matrixSize][matrixSize];

            // Corner finders (7x7 blocks at top-left, top-right, bottom-left)
            drawFinderSquare(matrix, 0, 0);
            drawFinderSquare(matrix, matrixSize - 7, 0);
            drawFinderSquare(matrix, 0, matrixSize - 7);

            // Seed binary generation based on content String
            long seed = content != null ? content.hashCode() : 1337;
            Random rand = new Random(seed);

            for (int r = 0; r < matrixSize; r++) {
                for (int c = 0; c < matrixSize; c++) {
                    if (isFinderArea(r, c, matrixSize)) continue;
                    // Draw patterns and standard noise
                    if ((r == matrixSize - 8 && c == 6) || (r == 6 && c == matrixSize - 8)) {
                        matrix[r][c] = true; // timing line blocks
                    } else {
                        matrix[r][c] = rand.nextBoolean();
                    }
                }
            }

            // Draw matrix squares
            g2.setColor(qrColor);
            for (int r = 0; r < matrixSize; r++) {
                for (int c = 0; c < matrixSize; c++) {
                    if (matrix[r][c]) {
                        int cx = (int) (padding + c * cellW);
                        int cy = (int) (padding + r * cellH);
                        int cw = (int) Math.ceil(cellW);
                        int ch = (int) Math.ceil(cellH);
                        g2.fillRect(cx, cy, cw, ch);
                    }
                }
            }
            g2.dispose();
        }

        private void drawFinderSquare(boolean[][] matrix, int row, int col) {
            for (int r = 0; r < 7; r++) {
                for (int c = 0; c < 7; c++) {
                    if (r == 0 || r == 6 || c == 0 || c == 6) {
                        matrix[row + r][col + c] = true;
                    } else if (r >= 2 && r <= 4 && c >= 2 && c <= 4) {
                        matrix[row + r][col + c] = true;
                    }
                }
            }
        }

        private boolean isFinderArea(int r, int c, int size) {
            if (r < 8 && c < 8) return true; // top-left
            if (r < 8 && c > size - 9) return true; // top-right
            if (r > size - 9 && c < 8) return true; // bottom-left
            return false;
        }
    }

    // Live pulsing status label
    public static class PulsingStatusLabel extends JPanel {
        private double rippleRadius = 0.0;
        private float rippleAlpha = 1.0f;

        public PulsingStatusLabel() {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            
            JLabel statusLbl = new JLabel("ONLINE");
            statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            statusLbl.setForeground(successColor);
            statusLbl.setBorder(new EmptyBorder(10, 0, 10, 24)); // Space for pulsing dot
            
            add(statusLbl);
            
            // Pulse timer (repaints every 60ms)
            Timer pulseTimer = new Timer(60, e -> {
                rippleRadius += 0.04;
                if (rippleRadius > 1.0) {
                    rippleRadius = 0.0;
                }
                rippleAlpha = (float) (1.0 - rippleRadius);
                repaint();
            });
            pulseTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = getWidth() - 15;
            int y = getHeight() / 2;

            // Draw expanding soft green ring
            g2.setColor(new Color(16, 185, 129, (int) (rippleAlpha * 140)));
            int r = (int) (6 + rippleRadius * 16);
            g2.fillOval(x - r / 2, y - r / 2, r, r);

            // Draw solid green core
            g2.setColor(successColor);
            g2.fillOval(x - 4, y - 4, 8, 8);

            g2.dispose();
        }
    }

    // Custom cell padding and alternating colors renderer for JTable
    public static class ModernTableCellRenderer extends DefaultTableCellRenderer {
        public ModernTableCellRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(new EmptyBorder(0, 14, 0, 14));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));

            if (isSelected) {
                setBackground(primaryAccent);
                setForeground(Color.WHITE);
            } else {
                setForeground(textPrimary);
                if (row % 2 == 0) {
                    setBackground(cardBg);
                } else {
                    setBackground(tableRowAlt);
                }
            }
            return this;
        }
    }

    // ==========================================
    // MAIN EXECUTABLE ENTRY
    // ==========================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomeApp app = new WelcomeApp();
            app.setVisible(true);
        });
    }
}

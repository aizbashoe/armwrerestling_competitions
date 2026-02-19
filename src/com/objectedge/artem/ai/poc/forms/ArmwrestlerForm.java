package com.objectedge.artem.ai.poc.forms;

import com.objectedge.artem.ai.poc.helpers.CSVLoader;
import com.objectedge.artem.ai.poc.models.Armwrestler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArmwrestlerForm extends JFrame {
    private List<Armwrestler> armwrestlers;
    private JTextField nameField;
    private JTextField surnameField;
    private JSpinner ageSpinner;
    private JComboBox<String> handComboBox;
    private JTable armwrestlersTable;
    private DefaultTableModel tableModel;
    private CompetitionForm competitionForm;

    public ArmwrestlerForm(CompetitionForm competitionForm) {
        this.competitionForm = competitionForm;
        setTitle("Armwrestler Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        armwrestlers = new ArrayList<>();

        // Load pre-defined armwrestlers
        loadPredefinedArmwrestlers();

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input Panel
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadPredefinedArmwrestlers() {
        String[][] predefinedData = {
            {"Artem", "Izbash"},
            {"Serhii", "Gonenko"},
            {"Vadim", "Larin"},
            {"Volodymy", "Loginov"},
            {"Mykola", "Kovbasa"},
            {"Dmitro", "Ionov"},
            {"Oleksii", "Lupiychuk"},
            {"Ivan", "Brezdin"},
            {"Misha", "Batsev"}
        };

        for (String[] data : predefinedData) {
            int randomAge = 25 + (int) (Math.random() * 16); // Random age between 25-40
            Armwrestler armwrestler = new Armwrestler(data[0], data[1], randomAge, "right");
            armwrestlers.add(armwrestler);
        }
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add New Armwrestler"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name label and field
        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);

        nameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Surname label and field
        JLabel surnameLabel = new JLabel("Surname:");
        gbc.gridx = 2;
        panel.add(surnameLabel, gbc);

        surnameField = new JTextField(15);
        gbc.gridx = 3;
        panel.add(surnameField, gbc);

        // Age label and spinner
        JLabel ageLabel = new JLabel("Age:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(ageLabel, gbc);

        ageSpinner = new JSpinner(new SpinnerNumberModel(20, 16, 100, 1));
        gbc.gridx = 1;
        panel.add(ageSpinner, gbc);

        // Hand label and combo box
        JLabel handLabel = new JLabel("Hand:");
        gbc.gridx = 2;
        panel.add(handLabel, gbc);

        handComboBox = new JComboBox<>(new String[]{"Right", "Left"});
        gbc.gridx = 3;
        panel.add(handComboBox, gbc);

        // Add button
        JButton addButton = new JButton("Add Armwrestler");
        addButton.addActionListener(e -> addArmwrestler());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(addButton, gbc);

        // Load from CSV button
        JButton loadCSVButton = new JButton("Load from CSV");
        loadCSVButton.addActionListener(e -> loadFromCSV());
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        panel.add(loadCSVButton, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Armwrestlers List"));

        // Create table model
        String[] columnNames = {"Name", "Surname", "Age", "Hand"};
        tableModel = new DefaultTableModel(columnNames, 0);
        armwrestlersTable = new JTable(tableModel);
        armwrestlersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Populate table with pre-defined armwrestlers
        for (Armwrestler wrestler : armwrestlers) {
            tableModel.addRow(new Object[]{wrestler.getName(), wrestler.getSurname(), wrestler.getAge(), wrestler.getHand()});
        }

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(armwrestlersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Delete button
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteSelectedArmwrestler());
        panel.add(deleteButton);

        // Clear all button
        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> clearAllArmwrestlers());
        panel.add(clearButton);

        // Start Competition button
        JButton startCompetitionButton = new JButton("Start Competition");
        startCompetitionButton.addActionListener(e -> startCompetition());
        panel.add(startCompetitionButton);

        return panel;
    }

    private void addArmwrestler() {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        int age = (Integer) ageSpinner.getValue();
        String hand = ((String) handComboBox.getSelectedItem()).toLowerCase();

        if (name.isEmpty() || surname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Capitalize first letter of name and surname
        name = capitalizeFirstLetter(name);
        surname = capitalizeFirstLetter(surname);

        Armwrestler armwrestler = new Armwrestler(name, surname, age, hand);
        armwrestlers.add(armwrestler);

        // Add to table without ID
        tableModel.addRow(new Object[]{name, surname, age, hand});

        // Clear input fields
        nameField.setText("");
        surnameField.setText("");
        ageSpinner.setValue(20);
        handComboBox.setSelectedIndex(0);
        nameField.requestFocus();
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private void deleteSelectedArmwrestler() {
        int selectedRow = armwrestlersTable.getSelectedRow();
        if (selectedRow != -1) {
            armwrestlers.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an armwrestler to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearAllArmwrestlers() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all armwrestlers?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            armwrestlers.clear();
            tableModel.setRowCount(0);
        }
    }

    private void startCompetition() {
        if (armwrestlers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one armwrestler.", "No Armwrestlers", JOptionPane.WARNING_MESSAGE);
            return;
        }
        competitionForm.displayCompetitionPairs(new ArrayList<>(armwrestlers));
        dispose();
    }

    private void loadFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Armwrestlers from CSV");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }

            @Override
            public String getDescription() {
                return "CSV Files (*.csv)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                List<Armwrestler> loadedArmwrestlers = CSVLoader.loadFromCSV(selectedFile.getAbsolutePath());

                if (loadedArmwrestlers.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "CSV file is empty or contains no valid data.", "Empty File", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Add loaded armwrestlers to the list
                armwrestlers.addAll(loadedArmwrestlers);

                // Update table
                for (Armwrestler wrestler : loadedArmwrestlers) {
                    tableModel.addRow(new Object[]{wrestler.getName(), wrestler.getSurname(), wrestler.getAge(), wrestler.getHand()});
                }

                JOptionPane.showMessageDialog(this, "Successfully loaded " + loadedArmwrestlers.size() + " armwrestlers from CSV file.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "CSV format error: " + e.getMessage(), "Format Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public List<Armwrestler> getArmwrestlers() {
        return armwrestlers;
    }
}



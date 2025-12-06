package org.springtraining.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AutomationDashboard extends JFrame {

    private JTextField projectInput;
    private JTable projectTable;
    private ProjectTableModel projectTableModel;
    private JRadioButton forecastOption;
    private JRadioButton expenseOption;
    private JButton startButton;

    private ProcessListSidebar sidebar;
    private ProjectRepository projectRepository = new ProjectRepository();

    public AutomationDashboard() {
        setTitle("Web Automatisierungs-Steuerung");
        setSize(1000, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(12, 12));

        // Top: Eingabezeile
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        projectInput = new JTextField(22);
        JButton addButton = new JButton("Projekt hinzufügen");
        addButton.addActionListener(e -> addProject());
        topPanel.add(new JLabel("Projekt:"));
        topPanel.add(projectInput);
        topPanel.add(addButton);

        // Center: Tabelle
        projectTableModel = new ProjectTableModel();
        projectTable = new JTable(projectTableModel);
        projectTable.setRowHeight(22);
        projectTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(projectTable);

        // Left: Modus
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
        optionPanel.setBorder(BorderFactory.createTitledBorder("Modus"));
        forecastOption = new JRadioButton("Forecast");
        expenseOption = new JRadioButton("Expense Plan");
        ButtonGroup group = new ButtonGroup();
        group.add(forecastOption);
        group.add(expenseOption);
        optionPanel.add(forecastOption);
        optionPanel.add(expenseOption);

        // Bottom: Start
        startButton = new JButton("Prozess starten");
        startButton.addActionListener(this::startProcess);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(startButton);

        // Right: Sidebar
        sidebar = new ProcessListSidebar();
        sidebar.setPreferredSize(new Dimension(350, getHeight()));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(optionPanel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.EAST);
    }

    private boolean validateProjectName(String name) {
        // TODO: Hier deine eigene Validierungslogik implementieren
        return name != null && name.trim().length() >= 3;
    }

    private void addProject() {
        String name = projectInput.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Projektname darf nicht leer sein.");
            return;
        }

        if (!validateProjectName(name)) {
            JOptionPane.showMessageDialog(this, "Projektname ist ungültig (Validierung fehlgeschlagen).");
            return;
        }

        if (projectRepository.existsByName(name)) {
            JOptionPane.showMessageDialog(this, "Projekt existiert bereits in der Datenbank.");
            return;
        }

        Project project = new Project(name);
        projectRepository.save(project);
        projectTableModel.addProject(project);

        projectInput.setText("");
    }

    private void startProcess(ActionEvent e) {
        int[] rows = projectTable.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Bitte mindestens ein Projekt auswählen.");
            return;
        }
        if (!forecastOption.isSelected() && !expenseOption.isSelected()) {
            JOptionPane.showMessageDialog(this, "Bitte Forecast oder Expense Plan wählen.");
            return;
        }

        ProcessMode mode = forecastOption.isSelected()
                ? ProcessMode.FORECAST
                : ProcessMode.EXPENSE_PLAN;

        for (int viewRow : rows) {
            int modelRow = projectTable.convertRowIndexToModel(viewRow);
            Project project = projectTableModel.getProjectAt(modelRow);

            ProcessRun processRun = new ProcessRun(project, mode);
            ProcessItemPanel itemPanel = sidebar.addProcess(project.getName(), mode.name());

            ProcessTask task = new ProcessTask(processRun, 120, itemPanel);
            task.addPropertyChangeListener(itemPanel);
            task.execute();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AutomationDashboard().setVisible(true);
        });
    }
}

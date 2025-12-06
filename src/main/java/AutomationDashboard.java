package org.springtraining;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AutomationDashboard extends JFrame {

    private JTextField projectInput;
    private JTable projectTable;
    private JRadioButton forecastOption;
    private JRadioButton expenseOption;
    private JButton startButton;

    // Sidebar mit Liste von Prozessen
    private ProcessListSidebar sidebar;

    public AutomationDashboard() {
        setTitle("Web Automatisierungs-Steuerung");
        setSize(1000, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(12, 12));

        // ------- Top: Eingabezeile -------
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        projectInput = new JTextField(22);
        JButton addButton = new JButton("Projekt hinzufügen");
        addButton.addActionListener(e -> addProject());
        topPanel.add(new JLabel("Projekt:"));
        topPanel.add(projectInput);
        topPanel.add(addButton);

        // ------- Center: Tabelle -------
        DefaultTableModel model = new DefaultTableModel(new String[]{"Projekte"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        projectTable = new JTable(model);
        projectTable.setRowHeight(22);
        JScrollPane scrollPane = new JScrollPane(projectTable);

        // ------- Left: Modus -------
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

        // ------- Bottom: Start -------
        startButton = new JButton("Prozess starten");
        startButton.addActionListener(this::startProcess);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(startButton);

        // ------- Right: Sidebar mit Liste von Prozessen -------
        sidebar = new ProcessListSidebar();
        sidebar.setPreferredSize(new Dimension(350, getHeight()));

        // ------- Layout zusammensetzen -------
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(optionPanel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.EAST);
    }

    private void addProject() {
        String project = projectInput.getText().trim();
        if (!project.isEmpty()) {
            DefaultTableModel model = (DefaultTableModel) projectTable.getModel();
            model.addRow(new Object[]{project});
            projectInput.setText("");
        }
    }

    private void startProcess(ActionEvent e) {
        int row = projectTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Bitte ein Projekt in der Tabelle auswählen.");
            return;
        }
        if (!forecastOption.isSelected() && !expenseOption.isSelected()) {
            JOptionPane.showMessageDialog(this, "Bitte Forecast oder Expense Plan wählen.");
            return;
        }

        String project = projectTable.getValueAt(row, 0).toString();
        String mode = forecastOption.isSelected() ? "Forecast" : "Expense Plan";

        // Neues Prozess-Panel in der Sidebar hinzufügen
        ProcessItemPanel itemPanel = sidebar.addProcess(project, mode);

        // Deinen echten Automationsprozess hier einhängen:
        // Beispiel: Simulation mit 120 Schritten
        ProcessTask task = new ProcessTask(project, mode, 120, itemPanel);
        task.addPropertyChangeListener(itemPanel); // Panel reagiert auf Progress & Status
        task.execute(); // startet im Hintergrund (parallel zu anderen Prozessen)
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Wenn du FlatLaf benutzt, kannst du das aktivieren (Dependency nötig)
            // try { UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf()); } catch (Exception ignored) {}
            new AutomationDashboard().setVisible(true);
        });
    }
}

/** Sidebar, die mehrere Prozesse untereinander listet */
class ProcessListSidebar extends JPanel {

    private final JPanel listPanel;

    ProcessListSidebar() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));

        JLabel title = new JLabel("Prozesse");
        title.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /** Fügt ein neues Prozess-Panel hinzu und gibt es zurück */
    ProcessItemPanel addProcess(String project, String mode) {
        ProcessItemPanel item = new ProcessItemPanel(project, mode);
        listPanel.add(item);
        listPanel.add(Box.createVerticalStrut(8));
        revalidate();
        repaint();
        return item;
    }
}

/** Einzelnes Prozess-Item (eine Zeile) mit eigenem Timer */
class ProcessItemPanel extends JPanel implements PropertyChangeListener {

    private final JLabel projectLabel = new JLabel();
    private final JLabel modeLabel = new JLabel();
    private final JLabel statusLabel = new JLabel("läuft …");
    private final JLabel runtimeLabel = new JLabel("0s");
    private final JLabel etaLabel = new JLabel("Berechnung …");
    private final JProgressBar progressBar = new JProgressBar(0, 100);

    private long startMillis;
    private boolean finished = false;
    private final Timer timer;

    ProcessItemPanel(String project, String mode) {
        setLayout(new BorderLayout(6, 6));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));

        projectLabel.setText(project);
        modeLabel.setText(mode);

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(projectLabel);
        top.add(modeLabel);

        JPanel center = new JPanel(new GridLayout(3, 1));
        center.add(row("Status:", statusLabel));
        center.add(row("Laufzeit:", runtimeLabel));
        center.add(row("Restzeit:", etaLabel));

        progressBar.setStringPainted(true);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        startMillis = System.currentTimeMillis();

        // Timer für Laufzeit-Update (jede Sekunde)
        timer = new Timer(1000, e -> updateRuntime());
        timer.start();
    }

    private JPanel row(String label, JComponent value) {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(80, 20));
        p.add(l, BorderLayout.WEST);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private void updateRuntime() {
        if (finished) {
            // Laufzeit bleibt stehen, Timer wird beendet
            timer.stop();
            return;
        }

        long elapsed = (System.currentTimeMillis() - startMillis) / 1000;
        runtimeLabel.setText(elapsed + "s");

        int p = progressBar.getValue();
        if (p > 0 && p < 100) {
            long etaSec = elapsed * (100 - p) / p;
            etaLabel.setText(etaSec + "s");
        } else if (p >= 100) {
            etaLabel.setText("0s");
        } else {
            etaLabel.setText("Berechnung …");
        }
    }

    /** Reagiert auf Progress + eigenen "status"-PropertyChangeEvents des SwingWorkers */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "progress" -> {
                int p = (int) evt.getNewValue();
                progressBar.setValue(p);
                if (p >= 100) {
                    finished = true;
                    statusLabel.setText("abgeschlossen ✅");
                }
            }
            case "status" -> statusLabel.setText(String.valueOf(evt.getNewValue()));
        }
    }
}

/** Beispielprozess: Ersetze doWorkStep() durch deine Web-Automation */
class ProcessTask extends SwingWorker<Void, String> {

    private final String project;
    private final String mode;
    private final int steps;
    private final ProcessItemPanel panel;

    ProcessTask(String project, String mode, int steps, ProcessItemPanel panel) {
        this.project = project;
        this.mode = mode;
        this.steps = steps;
        this.panel = panel;
    }

    @Override
    protected Void doInBackground() {
        firePropertyChange("status", null, "Initialisiere …");

        for (int i = 1; i <= steps; i++) {
            // >>> HIER deine echte Logik / Web-Automation pro Schritt <<<
            doWorkStep(i);

            int progress = (i * 100) / steps;
            setProgress(progress);

            // optionale Statuswechsel
            if (i == 1) {
                firePropertyChange("status", null, "Verbinde …");
            } else if (i == steps / 3) {
                firePropertyChange("status", null, "Hole Daten …");
            } else if (i == 2 * steps / 3) {
                firePropertyChange("status", null, "Verarbeite …");
            } else if (i == steps - 1) {
                firePropertyChange("status", null, "Schließe ab …");
            }
        }
        return null;
    }

    @Override
    protected void done() {
        setProgress(100);
        firePropertyChange("status", null, "Fertig");
    }

    private void doWorkStep(int i) {
        try {
            // Simulation: 50–120 ms pro Schritt
            Thread.sleep(50 + (int) (Math.random() * 70));
            // Beispiel: je nach Modus unterscheiden
            // if ("Forecast".equals(mode)) { ... } else { ... }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}

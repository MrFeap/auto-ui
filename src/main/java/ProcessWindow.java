package org.springtraining;

import javax.swing.*;
import java.awt.*;

public class ProcessWindow extends JFrame {

    private JLabel projectLabel;
    private JLabel modeLabel;
    private JLabel statusLabel;
    private JLabel runtimeLabel;
    private JLabel remainingLabel;

    private long startTime;

    public ProcessWindow(String project, String mode) {
        setTitle("Laufender Prozess");
        setSize(400, 300);
        setLocationRelativeTo(null);

        startTime = System.currentTimeMillis();

        initComponents(project, mode);
        startTimer();
    }

    private void initComponents(String project, String mode) {
        setLayout(new GridLayout(6, 1));

        projectLabel = new JLabel("Projekt: " + project);
        modeLabel = new JLabel("Modus: " + mode);
        statusLabel = new JLabel("Status: läuft...");
        runtimeLabel = new JLabel("Laufzeit: 0s");
        remainingLabel = new JLabel("Restzeit (geschätzt): Berechnung...");

        add(projectLabel);
        add(modeLabel);
        add(statusLabel);
        add(runtimeLabel);
        add(remainingLabel);
    }

    private void startTimer() {
        Timer timer = new Timer(1000, e -> updateRuntime());
        timer.start();
    }

    private void updateRuntime() {
        long now = System.currentTimeMillis();
        long elapsedSec = (now - startTime) / 1000;

        runtimeLabel.setText("Laufzeit: " + elapsedSec + "s");

        // Dummy-Schätzung: Prozess dauert 60s
        long estimatedTotal = 60;
        long remaining = Math.max(0, estimatedTotal - elapsedSec);

        remainingLabel.setText("Restzeit (geschätzt): " + remaining + "s");

        if (remaining == 0) {
            statusLabel.setText("Status: abgeschlossen ✅");
        }
    }
}


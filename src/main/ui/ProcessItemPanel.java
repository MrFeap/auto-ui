package org.springtraining.ui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ProcessItemPanel extends JPanel implements PropertyChangeListener {

    private final JLabel projectLabel = new JLabel();
    private final JLabel modeLabel = new JLabel();
    private final JLabel statusLabel = new JLabel("läuft …");
    private final JLabel runtimeLabel = new JLabel("0s");
    private final JLabel etaLabel = new JLabel("Berechnung …");
    private final JProgressBar progressBar = new JProgressBar(0, 100);

    private long startMillis;
    private boolean finished = false;
    private final Timer timer;

    public ProcessItemPanel(String project, String mode) {
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

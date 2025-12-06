package org.springtraining.ui;

import javax.swing.*;

public class ProcessTask extends SwingWorker<Void, String> {

    private final ProcessRun processRun;
    private final int steps;
    private final ProcessItemPanel panel;

    public ProcessTask(ProcessRun processRun, int steps, ProcessItemPanel panel) {
        this.processRun = processRun;
        this.steps = steps;
        this.panel = panel;
    }

    @Override
    protected Void doInBackground() {
        firePropertyChange("status", null, "Initialisiere …");
        processRun.addLog("Prozess gestartet für " + processRun.getProject().getName());

        for (int i = 1; i <= steps; i++) {
            try {
                doWorkStep(i);
            } catch (Exception ex) {
                processRun.getProject().addError();
                processRun.addLog("Fehler in Schritt " + i + ": " + ex.getMessage());
            }

            int progress = (i * 100) / steps;
            setProgress(progress);

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
        processRun.addLog("Prozess abgeschlossen");
    }

    private void doWorkStep(int i) throws Exception {
        // TODO: Hier deine echte Automatisierungslogik einbauen (Selenium, HTTP, etc.)
        Thread.sleep(50 + (int) (Math.random() * 70));
    }
}

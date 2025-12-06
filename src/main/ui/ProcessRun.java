package org.springtraining.ui;

import java.util.ArrayList;
import java.util.List;

public class ProcessRun {
    private final Project project;
    private final ProcessMode mode;
    private final List<String> logs = new ArrayList<>();

    public ProcessRun(Project project, ProcessMode mode) {
        this.project = project;
        this.mode = mode;
    }

    public Project getProject() {
        return project;
    }

    public ProcessMode getMode() {
        return mode;
    }

    public void addLog(String message) {
        logs.add(message);
    }

    public List<String> getLogs() {
        return logs;
    }
}

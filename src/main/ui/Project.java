package org.springtraining.ui;

public class Project {
    private Long id;
    private String name;
    private int errorCount = 0;

    public Project(String name) {
        this.name = name;
    }

    public Project(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void addError() {
        errorCount++;
    }

    public int getErrorCount() {
        return errorCount;
    }

    @Override
    public String toString() {
        return name;
    }
}

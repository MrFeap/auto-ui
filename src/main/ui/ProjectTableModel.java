package org.springtraining.ui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ProjectTableModel extends AbstractTableModel {

    private final List<Project> projects = new ArrayList<>();
    private final String[] columns = {"Projekt"};

    @Override
    public int getRowCount() {
        return projects.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return projects.get(rowIndex).getName();
    }

    public Project getProjectAt(int row) {
        return projects.get(row);
    }

    public void addProject(Project project) {
        boolean exists = projects.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(project.getName()));
        if (!exists) {
            projects.add(project);
            int row = projects.size() - 1;
            fireTableRowsInserted(row, row);
        }
    }
}

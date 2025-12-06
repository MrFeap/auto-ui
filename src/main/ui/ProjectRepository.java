package org.springtraining.ui;

import java.sql.*;

public class ProjectRepository {

    private final String url = "jdbc:postgresql://localhost:5432/automationdb";
    private final String user = "postgres";
    private final String password = "postgres";

    public ProjectRepository() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver nicht gefunden", e);
        }
    }

    public Project save(Project project) {
        String sql = "INSERT INTO projects(name) VALUES (?) ON CONFLICT (name) DO NOTHING RETURNING id";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, project.getName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    project.setId(id);
                }
            }
            return project;
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern des Projekts", e);
        }
    }

    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM projects WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler bei existsByName()", e);
        }
    }
}

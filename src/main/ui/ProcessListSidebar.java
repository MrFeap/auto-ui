package org.springtraining.ui;

import javax.swing.*;
import java.awt.*;

public class ProcessListSidebar extends JPanel {

    private final JPanel listPanel;

    public ProcessListSidebar() {
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

    public ProcessItemPanel addProcess(String projectName, String modeName) {
        ProcessItemPanel item = new ProcessItemPanel(projectName, modeName);
        listPanel.add(item);
        listPanel.add(Box.createVerticalStrut(8));
        revalidate();
        repaint();
        return item;
    }
}

package me.crystal.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.nio.file.*;

public class AppCheckGUI {

    private boolean isDarkMode = true;

    public void createAndShowGUI() {
        // Create the frame
        JFrame frame = new JFrame("Modern Application Installer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); // Center window

        // Create a panel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout());

        // Create a sub-panel for the checkboxes and download button
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Vertical layout
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create checkboxes for apps
        JCheckBox jultiCheckBox = new JCheckBox("Julti");
        JCheckBox ahkCheckBox = new JCheckBox("AutoHotkey (AHK)");
        JCheckBox obsCheckBox = new JCheckBox("OBS");
        JCheckBox multiMcCheckBox = new JCheckBox("MultiMC");
        JCheckBox offlineMcCheckBox = new JCheckBox("MultiMC Offline");
        JCheckBox prismLauncherCheckBox = new JCheckBox("Prism Launcher");
        JCheckBox contariaCalcCheckBox = new JCheckBox("ContariaCalc");
        JCheckBox ninjabrainBotCheckBox = new JCheckBox("Ninjabrain-Bot");
        JCheckBox modCheckCheckBox = new JCheckBox("ModCheck");
        JCheckBox mapCheckCheckBox = new JCheckBox("Map Check");
        JCheckBox fabricUpdaterCheckBox = new JCheckBox("Fabric Updater");

        // Add checkboxes to the content panel
        contentPanel.add(jultiCheckBox);
        contentPanel.add(ahkCheckBox);
        contentPanel.add(obsCheckBox);
        contentPanel.add(multiMcCheckBox);
        contentPanel.add(offlineMcCheckBox);
        contentPanel.add(prismLauncherCheckBox);
        contentPanel.add(contariaCalcCheckBox);
        contentPanel.add(ninjabrainBotCheckBox);
        contentPanel.add(modCheckCheckBox);
        contentPanel.add(mapCheckCheckBox);
        contentPanel.add(fabricUpdaterCheckBox);

        // Button to select download folder
        JButton selectFolderButton = new JButton("Select Download Folder");
        JLabel folderLabel = new JLabel("No folder selected");

        // Add action listener for folder selection
        selectFolderButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                folderLabel.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        // Create a sub-panel for the folder selection and theme button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(selectFolderButton);
        topPanel.add(folderLabel);

        // Add a dark/light mode toggle button (sun icon)
        JButton themeToggleButton = new JButton("☀️");
        themeToggleButton.addActionListener(e -> toggleTheme(frame, contentPanel));
        topPanel.add(themeToggleButton);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create a download button
        JButton downloadButton = new JButton("Install Selected Apps");
        downloadButton.addActionListener(e -> {
            String downloadFolder = folderLabel.getText();
            if ("No folder selected".equals(downloadFolder)) {
                JOptionPane.showMessageDialog(frame, "Please select a download folder", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (jultiCheckBox.isSelected()) downloadApp("https://github.com/DuncanRuns/Julti/releases/latest/download/Julti.zip", downloadFolder, "Julti.zip");
                if (ahkCheckBox.isSelected()) downloadApp("https://www.autohotkey.com/download/ahk-install.exe", downloadFolder, "AutoHotkey.exe");
                if (obsCheckBox.isSelected()) downloadApp("https://cdn-fastly.obsproject.com/downloads/OBS-Studio-27.1.3-Full-Installer-x64.exe", downloadFolder, "OBS-Installer.exe");
                if (multiMcCheckBox.isSelected()) downloadApp("https://github.com/MultiMC/Launcher/releases/latest/download/MultiMC.zip", downloadFolder, "MultiMC.zip");
                if (offlineMcCheckBox.isSelected()) downloadApp("https://github.com/Ponywka/MultiMC5-with-offline/releases/latest/download/OfflineMultiMC.zip", downloadFolder, "OfflineMultiMC.zip");
                if (prismLauncherCheckBox.isSelected()) downloadApp("https://github.com/PrismLauncher/PrismLauncher/releases/latest/download/PrismLauncher.zip", downloadFolder, "PrismLauncher.zip");
                if (contariaCalcCheckBox.isSelected()) downloadApp("https://github.com/KingContaria/ContariaCalc/releases/latest/download/ContariaCalc.zip", downloadFolder, "ContariaCalc.zip");
                if (ninjabrainBotCheckBox.isSelected()) downloadApp("https://github.com/Ninjabrain1/Ninjabrain-Bot/releases/latest/download/Ninjabrain-Bot.zip", downloadFolder, "Ninjabrain-Bot.zip");
                if (modCheckCheckBox.isSelected()) downloadApp("https://github.com/tildejustin/modcheck/releases/latest/download/modcheck.zip", downloadFolder, "ModCheck.zip");
                if (mapCheckCheckBox.isSelected()) downloadApp("https://github.com/cylorun/Map-Check/releases/latest/download/MapCheck.zip", downloadFolder, "MapCheck.zip");
                if (fabricUpdaterCheckBox.isSelected()) downloadApp("https://github.com/tildejustin/fabric-updater/releases/latest/download/fabric-updater.zip", downloadFolder, "FabricUpdater.zip");

                JOptionPane.showMessageDialog(frame, "All selected applications have been installed.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(frame, "Error during download: " + ioException.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(downloadButton, BorderLayout.SOUTH);

        // Set dark mode by default
        applyDarkTheme(contentPanel);
        frame.add(panel);
        frame.setVisible(true);
    }

    // Helper function to download a file from a URL
    private void downloadApp(String urlString, String downloadFolder, String fileName) throws IOException {
        URL url = new URL(urlString);
        Path outputPath = Paths.get(downloadFolder, fileName);

        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(outputPath.toString())) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }

    // Function to toggle between light and dark mode
    private void toggleTheme(JFrame frame, JPanel contentPanel) {
        if (isDarkMode) {
            applyLightTheme(contentPanel);
        } else {
            applyDarkTheme(contentPanel);
        }
        isDarkMode = !isDarkMode;
        SwingUtilities.updateComponentTreeUI(frame);
    }

    // Apply dark theme
    private void applyDarkTheme(JPanel panel) {
        panel.setBackground(Color.DARK_GRAY);
        panel.setForeground(Color.WHITE);
        for (Component component : panel.getComponents()) {
            if (component instanceof JCheckBox) {
                component.setBackground(Color.DARK_GRAY);
                component.setForeground(Color.WHITE);
            }
        }
    }

    // Apply light theme
    private void applyLightTheme(JPanel panel) {
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setForeground(Color.BLACK);
        for (Component component : panel.getComponents()) {
            if (component instanceof JCheckBox) {
                component.setBackground(Color.LIGHT_GRAY);
                component.setForeground(Color.BLACK);
            }
        }
    }
}


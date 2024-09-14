package me.crystal.gui;

import me.crystal.logic.AppCheckJson;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.Map;

public class AppCheckGUI {

    private Map<String, String> downloadLinksMap;

    public void initializeAndRenderGUI() {
        JFrame frame = new JFrame("AppCheck");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        loadAndDisplayCheckBoxes(contentPanel, frame);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.WHITE);
        JButton selectFolderButton = new JButton("Select Download Folder");
        styleButton(selectFolderButton);
        JTextField folderTextField = new JTextField("No folder selected", 20);
        folderTextField.setEditable(true);
        folderTextField.setForeground(Color.BLACK);
        folderTextField.setBackground(Color.WHITE);

        selectFolderButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                folderTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        leftPanel.add(selectFolderButton);
        leftPanel.add(folderTextField);
        topPanel.add(leftPanel, BorderLayout.WEST);

        // Right part: Reconnect button
        JButton reconnectButton = new JButton("Reconnect");
        styleButton(reconnectButton);
        reconnectButton.addActionListener(e -> {
            try {
                contentPanel.removeAll();
                loadAndDisplayCheckBoxes(contentPanel, frame);
                contentPanel.revalidate();
                contentPanel.repaint();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(frame, "Error reconnecting: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(reconnectButton);
        topPanel.add(rightPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        JButton downloadButton = getDownloadButton(folderTextField, frame, contentPanel);
        styleButton(downloadButton);
        panel.add(downloadButton, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private JButton getDownloadButton(JTextField folderTextField, JFrame frame, JPanel contentPanel) {
        JButton downloadButton = new JButton("Install the Selected Apps");
        styleButton(downloadButton);
        downloadButton.addActionListener(e -> {
            String downloadFolder = folderTextField.getText();
            if ("No folder selected".equals(downloadFolder)) {
                JOptionPane.showMessageDialog(frame, "Please select a download folder", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean anyAppSelected = false;
            for (Component component : contentPanel.getComponents()) {
                if (component instanceof JCheckBox checkBox) {
                    if (checkBox.isSelected()) {
                        anyAppSelected = true;
                        break;
                    }
                }
            }

            if (!anyAppSelected) {
                JOptionPane.showMessageDialog(frame, "No apps were selected", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                JDialog progressDialog = createProgressDialog(frame);
                ProgressTask task = new ProgressTask(contentPanel, downloadLinksMap, downloadFolder, progressDialog, downloadButton);
                task.execute();
                progressDialog.setVisible(true);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(frame, "Error: " + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return downloadButton;
    }

    private void loadAndDisplayCheckBoxes(JPanel contentPanel, JFrame frame) {
        try {
            downloadLinksMap = AppCheckJson.getContentFromAppCheckJson();

            for (Map.Entry<String, String> entry : downloadLinksMap.entrySet()) {
                JCheckBox checkBox = new JCheckBox(entry.getKey());
                checkBox.setBackground(Color.WHITE);
                checkBox.setForeground(Color.GREEN);
                contentPanel.add(checkBox);
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(frame, "No connection could be established with the repository", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleButton(JButton button) {
        button.setBackground(Color.GREEN);
        button.setForeground(Color.WHITE);
    }

    private JDialog createProgressDialog(JFrame parentFrame) {
        JDialog progressDialog = new JDialog(parentFrame, "Downloading...", true);
        progressDialog.setSize(400, 150);
        progressDialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel fileLabel = new JLabel("Downloading: ");
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        panel.add(fileLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        progressDialog.add(panel);

        return progressDialog;
    }

    class ProgressTask extends SwingWorker<Void, Void> {
        private JPanel contentPanel;
        private Map<String, String> downloadLinksMap;
        private String downloadFolder;
        private JDialog progressDialog;
        private JButton downloadButton;

        ProgressTask(JPanel contentPanel, Map<String, String> downloadLinksMap, String downloadFolder, JDialog progressDialog, JButton downloadButton) {
            this.contentPanel = contentPanel;
            this.downloadLinksMap = downloadLinksMap;
            this.downloadFolder = downloadFolder;
            this.progressDialog = progressDialog;
            this.downloadButton = downloadButton;
        }

        @Override
        protected Void doInBackground() throws Exception {
            downloadButton.setEnabled(false);
            for (Component component : contentPanel.getComponents()) {
                if (component instanceof JCheckBox checkBox) {
                    if (checkBox.isSelected()) {
                        String downloadLink = downloadLinksMap.get(checkBox.getText());
                        String fileName = Paths.get(new URL(downloadLink).getPath()).getFileName().toString(); // Use original filename
                        downloadAppWithProgress(downloadLink, downloadFolder, fileName);
                    }
                }
            }
            return null;
        }

        @Override
        protected void done() {
            progressDialog.dispose();
            downloadButton.setEnabled(true);
            JOptionPane.showMessageDialog(progressDialog, "All selected applications have been installed.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        private void downloadAppWithProgress(String urlString, String downloadFolder, String fileName) throws IOException {
            URL url = new URL(urlString);
            Path outputPath = Paths.get(downloadFolder, fileName);
            try (BufferedInputStream in = new BufferedInputStream(url.openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(outputPath.toString())) {

                long totalBytes = url.openConnection().getContentLengthLong();
                long downloadedBytes = 0;
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                int progress;
                JProgressBar progressBar = (JProgressBar) ((JPanel) progressDialog.getContentPane().getComponent(0)).getComponent(1); // Get progress bar

                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    downloadedBytes += bytesRead;
                    progress = (int) ((downloadedBytes * 100) / totalBytes);
                    progressBar.setValue(progress); // Update progress
                    progressBar.setString("Downloaded: " + progress + "%");
                }
            }
        }
    }
}

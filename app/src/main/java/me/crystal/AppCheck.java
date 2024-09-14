package me.crystal;

import me.crystal.gui.AppCheckGUI;

import javax.swing.*;

public class AppCheck {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppCheckGUI appCheckGUI = new AppCheckGUI();
            appCheckGUI.createAndShowGUI();
        });
    }
}

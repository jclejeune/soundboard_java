// src/com/webradio/ui/DarkTheme.java
package com.soundboard.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class DarkTheme {
    public static final Color BACKGROUND = new Color(60, 63, 65);
    public static final Color FOREGROUND = new Color(187, 187, 187);
    public static final Color BUTTON_BG = new Color(77, 77, 77);
    public static final Color BUTTON_FG = new Color(220, 220, 220);
    public static final Color SELECTION_BG = new Color(75, 110, 175);
    public static final Color SELECTION_FG = Color.WHITE;
    public static final Color STATUS_BG = new Color(40, 40, 40);
    public static final Color STATUS_FG = new Color(250, 139, 1);
    public static final Color BORDER = new Color(100, 100, 100);
    
    public static void apply() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("List.background", BACKGROUND);
        UIManager.put("List.foreground", FOREGROUND);
        UIManager.put("List.selectionBackground", SELECTION_BG);
        UIManager.put("List.selectionForeground", SELECTION_FG);
        UIManager.put("ScrollPane.background", BACKGROUND);
        UIManager.put("Viewport.background", BACKGROUND);
        UIManager.put("Button.background", BUTTON_BG);
        UIManager.put("Button.foreground", BUTTON_FG);
        UIManager.put("Label.foreground", FOREGROUND);
    }
    
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(STATUS_BG);
        label.setForeground(STATUS_FG);
        
        // Police digitale/monospace
        try {
            Font digitalFont = new Font("Monospaced", Font.BOLD, 24);
            label.setFont(digitalFont);
        } catch (Exception e) {
            label.setFont(new Font("Monospaced", Font.BOLD, 24));
        }
        
        label.setBorder(new LineBorder(BORDER, 2));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(300, 200));
        return label;
    }
    
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_BG);
        button.setForeground(BUTTON_FG);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        return button;
    }
    
    public static JButton createControlButton(String symbol) {
        JButton button = new JButton(symbol);
        button.setBackground(BUTTON_BG);
        button.setForeground(BUTTON_FG);
        button.setFont(new Font("Dialog", Font.BOLD, 16));
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(50, 40));
        return button;
    }
}
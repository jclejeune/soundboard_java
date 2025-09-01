package com.soundboard;

import com.soundboard.ui.SoundBoardUI;
import javax.swing.SwingUtilities;

public class SoundboardApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SoundBoardUI().createUI());
    }
}

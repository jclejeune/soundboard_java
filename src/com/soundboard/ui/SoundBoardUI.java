package com.soundboard.ui;
import javax.swing.*;
import java.awt.*;
import com.soundboard.model.SoundPad;
import com.soundboard.audio.SoundPlayer;

public class SoundBoardUI {
    private JFrame frame;
    private SoundPlayer player = new SoundPlayer();
    public void createUI() {
        frame = new JFrame("🎵 Soundboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 3));
        SoundPad[] pads = {
            new SoundPad("Kick", "sounds/kick.wav"),
            new SoundPad("Snare", "sounds/snare.wav"),
            new SoundPad("Pluck", "sounds/pluck.wav"),
            new SoundPad("Slap", "sounds/slap.wav"),
            new SoundPad("HiHat", "sounds/hihat.wav"),
            new SoundPad("Clap", "sounds/clap.wav"),
            new SoundPad("Healing", "sounds/healing.wav"),
            new SoundPad("Netflix", "sounds/netflix.wav"),
            new SoundPad("Lazer", "sounds/lazer.wav"),
        };
        for (SoundPad pad : pads) {
            JButton btn = new JButton(pad.getName());
            btn.addActionListener(e -> player.play(pad.getFilePath()));
            frame.add(btn);
        }
        frame.setSize(400, 200);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SoundBoardUI().createUI());
    }
}

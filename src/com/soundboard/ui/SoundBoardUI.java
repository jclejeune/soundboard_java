package com.soundboard.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent; // ✅ utile car on utilise KeyEvent
import com.soundboard.model.SoundPad;
import com.soundboard.audio.SoundPlayer;
import javax.swing.border.Border;

public class SoundBoardUI {
    private JFrame frame;
    private SoundPlayer player = new SoundPlayer();

    public void createUI() {
        DarkTheme.apply();

        frame = new JFrame("WakoSound");
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

        // Boutons
        for (SoundPad pad : pads) {
            JButton btn = new JButton(pad.getName());

            // Désactiver le focus visuel
            btn.setFocusPainted(false);
            btn.setFocusable(false);


            // Désactiver le bouton “content area filled” pour gérer nous-même la couleur
            btn.setContentAreaFilled(false);

            // Rendre le fond opaque pour que la couleur de fond soit visible
            btn.setOpaque(true);

            // Couleur de fond par défaut
            btn.setBackground(UIManager.getColor("Button.background"));

            // Bordure noire interne
            Border blackBorder = BorderFactory.createLineBorder(Color.BLACK, 2); // 2 px d'épaisseur
            btn.setBorder(blackBorder);

            // Supprimer la bordure qui change au clic
            btn.setBorderPainted(true); // maintenant la bordure noire sera toujours visible

            // Ajouter le bouton au frame
            frame.add(btn);
        }
        // Ajout des raccourcis clavier
        addKeyBindings(pads);

        frame.setSize(400, 400);
        frame.setVisible(true);
    }

    private void addKeyBindings(SoundPad[] pads) {
        JPanel panel = (JPanel) frame.getContentPane();
        Component[] components = panel.getComponents();

        // Touches du pavé numérique (ordre 789 / 456 / 123)
        int[] keys = {
                KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9,
                KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6,
                KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3
        };

        for (int i = 0; i < pads.length; i++) {
            final int index = i;
            JButton btn = (JButton) components[i];

            // couleur "par défaut" selon ton thème
            final Color defaultColor = UIManager.getColor("Button.background");

            // Listener unique pour clic souris
            btn.addActionListener(e -> {
                player.play(pads[index].getFilePath());
                flashButton(btn, defaultColor, true);
            });

            // Raccourci clavier
            panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(keys[i], 0), "play" + i);

            panel.getActionMap()
                    .put("play" + i, new AbstractAction() {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            player.play(pads[index].getFilePath());
                            flashButton(btn, defaultColor, false);
                        }
                    });
        }
    }

    private void flashButton(JButton btn, Color defaultColor, boolean fromMouse) {
        if (fromMouse)
            btn.setFocusPainted(false);

        btn.setBackground(Color.ORANGE);

        Timer t = new Timer(200, ev -> {
            btn.setBackground(defaultColor);
            if (fromMouse)
                btn.setFocusPainted(true);
        });
        t.setRepeats(false);
        t.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SoundBoardUI().createUI());
    }
}

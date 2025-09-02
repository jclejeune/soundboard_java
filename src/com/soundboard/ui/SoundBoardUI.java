package com.soundboard.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import com.soundboard.model.SoundPad;
import com.soundboard.model.SoundKit;
import com.soundboard.model.KitManager;
import com.soundboard.audio.SoundPlayer;
import javax.swing.border.Border;
import java.util.List;

public class SoundBoardUI {
    private JFrame frame;
    private SoundPlayer player = new SoundPlayer();
    private KitManager kitManager = new KitManager();
    private JComboBox<String> kitSelector;
    private JButton[] padButtons = new JButton[9];
    private JLabel currentKitLabel;

    public void createUI() {
        DarkTheme.apply();

        frame = new JFrame("WakoSound");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Panel principal avec le sélecteur de kit en haut
        createTopPanel();
        
        // Panel central avec la grille de pads
        createPadGrid();
        
        // Label d'info en bas
        createBottomPanel();

        frame.setSize(450, 500);
        frame.setVisible(true);
        
        // Charger le kit par défaut
        loadCurrentKit();
    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(DarkTheme.BACKGROUND);
        
        // Label pour le sélecteur
        JLabel kitLabel = new JLabel("Kit:");
        kitLabel.setForeground(DarkTheme.FOREGROUND);
        topPanel.add(kitLabel);
        
        // Sélecteur de kit
        kitSelector = new JComboBox<>();
        kitSelector.setBackground(DarkTheme.BUTTON_BG);
        kitSelector.setForeground(DarkTheme.BUTTON_FG);
        
        // Remplir le sélecteur avec les kits disponibles
        for (String kitName : kitManager.getKitNames()) {
            kitSelector.addItem(kitName);
        }
        
        // Listener pour changement de kit
        kitSelector.addActionListener(e -> {
            String selectedKit = (String) kitSelector.getSelectedItem();
            if (selectedKit != null && kitManager.switchToKit(selectedKit)) {
                loadCurrentKit();
                updateCurrentKitLabel();
            }
        });
        
        topPanel.add(kitSelector);
        
        frame.add(topPanel, BorderLayout.NORTH);
    }

    private void createPadGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 2, 2));
        gridPanel.setBackground(DarkTheme.BACKGROUND);
        
        // Créer les 9 boutons de pads
        for (int i = 0; i < 9; i++) {
            JButton btn = new JButton();
            
            // Style des boutons (comme avant)
            btn.setFocusPainted(false);
            btn.setFocusable(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            btn.setBackground(DarkTheme.BUTTON_BG);
            btn.setForeground(DarkTheme.BUTTON_FG);
            
            // Bordure noire
            Border blackBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
            btn.setBorder(blackBorder);
            btn.setBorderPainted(true);
            
            padButtons[i] = btn;
            gridPanel.add(btn);
        }
        
        frame.add(gridPanel, BorderLayout.CENTER);
        
        // Ajouter les raccourcis clavier
        addKeyBindings();
    }

    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(DarkTheme.BACKGROUND);
        
        currentKitLabel = new JLabel();
        currentKitLabel.setForeground(DarkTheme.STATUS_FG);
        currentKitLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        bottomPanel.add(currentKitLabel);
        frame.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadCurrentKit() {
        SoundKit currentKit = kitManager.getCurrentKit();
        if (currentKit == null) {
            System.err.println("No current kit available!");
            return;
        }
        
        List<SoundPad> pads = currentKit.getPads();
        
        // Mettre à jour chaque bouton
        for (int i = 0; i < 9; i++) {
            JButton btn = padButtons[i];
            
            if (i < pads.size()) {
                // Il y a un pad pour cette position
                SoundPad pad = pads.get(i);
                btn.setText(pad.getName());
                btn.setEnabled(true);
                
                // Couleur personnalisée si disponible (pour la version enrichie de SoundPad)
                try {
                    if (pad.getClass().getMethod("hasCustomColor") != null && 
                        (Boolean) pad.getClass().getMethod("hasCustomColor").invoke(pad)) {
                        Color customColor = (Color) pad.getClass().getMethod("getCustomColor").invoke(pad);
                        btn.setBackground(customColor);
                    } else {
                        btn.setBackground(DarkTheme.BUTTON_BG);
                    }
                } catch (Exception e) {
                    // Fallback si SoundPad n'a pas les méthodes enrichies
                    btn.setBackground(DarkTheme.BUTTON_BG);
                }
                
            } else {
                // Pas de pad pour cette position
                btn.setText("Empty");
                btn.setEnabled(false);
                btn.setBackground(DarkTheme.BUTTON_BG.darker());
            }
        }
        
        updateCurrentKitLabel();
    }

    private void updateCurrentKitLabel() {
        SoundKit currentKit = kitManager.getCurrentKit();
        if (currentKit != null) {
            String info = String.format("Kit: %s | Pads: %d/9", 
                                      currentKit.getName(), 
                                      currentKit.getPadCount());
            currentKitLabel.setText(info);
        }
    }

    private void addKeyBindings() {
        JPanel panel = (JPanel) frame.getContentPane();

        // Touches du pavé numérique (ordre 789 / 456 / 123)
        int[] keys = {
                KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9,
                KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6,
                KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3
        };

        for (int i = 0; i < 9; i++) {
            final int index = i;
            JButton btn = padButtons[i];

            // Couleur par défaut
            final Color defaultColor = DarkTheme.BUTTON_BG;

            // Listener pour clic souris
            btn.addActionListener(e -> {
                playPad(index, btn, defaultColor, true);
            });

            // Raccourci clavier
            panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(keys[i], 0), "play" + i);

            panel.getActionMap()
                    .put("play" + i, new AbstractAction() {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            playPad(index, btn, defaultColor, false);
                        }
                    });
        }
    }

    private void playPad(int index, JButton btn, Color defaultColor, boolean fromMouse) {
        SoundKit currentKit = kitManager.getCurrentKit();
        if (currentKit == null) return;
        
        List<SoundPad> pads = currentKit.getPads();
        if (index >= pads.size()) return; // Pas de pad à cette position
        
        SoundPad pad = pads.get(index);
        if (pad == null) return;
        
        // Vérifier si le pad est activé (pour la version enrichie)
        try {
            Boolean enabled = (Boolean) pad.getClass().getMethod("isEnabled").invoke(pad);
            if (!enabled) return;
        } catch (Exception e) {
            // Fallback: considérer comme activé
        }
        
        // Valider le fichier
        try {
            Boolean valid = (Boolean) pad.getClass().getMethod("isFileValid").invoke(pad);
            if (!valid) {
                System.err.println("Invalid audio file: " + pad.getFilePath());
                return;
            }
        } catch (Exception e) {
            // Fallback: essayer de jouer quand même
        }
        
        // Jouer le son
        player.play(pad.getFilePath());
        flashButton(btn, defaultColor, fromMouse);
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

    // Méthodes publiques pour l'interaction externe
    public void switchToKit(String kitName) {
        if (kitManager.switchToKit(kitName)) {
            loadCurrentKit();
            kitSelector.setSelectedItem(kitName);
        }
    }
    
    public void refreshKits() {
        kitSelector.removeAllItems();
        for (String kitName : kitManager.getKitNames()) {
            kitSelector.addItem(kitName);
        }
        loadCurrentKit();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SoundBoardUI().createUI());
    }
}
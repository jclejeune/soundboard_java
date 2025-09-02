package com.soundboard.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import com.soundboard.model.SoundPad;
import com.soundboard.model.SoundKit;
import com.soundboard.model.KitManager;
import com.soundboard.audio.SoundPlayer;
import com.soundboard.sequencer.SequencerEngine;
import javax.swing.border.Border;
import java.util.List;

public class SoundBoardUI implements SequencerEngine.StepListener {
    private JFrame frame;
    private SoundPlayer player = new SoundPlayer();
    private KitManager kitManager = new KitManager();
    private SequencerEngine sequencer;
    
    // UI Components
    private JComboBox<String> kitSelector;
    private JButton[] padButtons = new JButton[9];
    private JLabel currentKitLabel;
    
    // Sequencer UI Components
    private JButton playStopButton;
    private JButton clearButton;
    private JSpinner bpmSpinner;
    private JSpinner lengthSpinner;
    private JLabel currentStepLabel;
    private JButton[][] stepButtons = new JButton[9][16]; // [pad][step]
    private JPanel sequencerPanel;
    
    // Mode toggle
    private JToggleButton modeToggle;
    private boolean sequencerMode = false;

    public void createUI() {
        DarkTheme.apply();
        
        // Initialiser le séquenceur
        sequencer = new SequencerEngine(player);
        sequencer.setStepListener(this);

        frame = new JFrame("WakoSound");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Panels
        createTopPanel();
        createCenterPanel();
        createBottomPanel();

        frame.setSize(800, 600); // Plus large pour le séquenceur
        frame.setVisible(true);
        
        // Charger le kit par défaut
        loadCurrentKit();
        updateSequencerKit();
    }

    // 🎨 Méthode pour rafraîchir toute la grille visuelle
    private void refreshStepGrid() {
        for (int pad = 0; pad < 9; pad++) {
            for (int step = 0; step < 16; step++) {
                updateStepButton(pad, step);
            }
        }
    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(DarkTheme.BACKGROUND);
        
        // Sélecteur de kit
        JLabel kitLabel = new JLabel("Kit:");
        kitLabel.setForeground(DarkTheme.FOREGROUND);
        topPanel.add(kitLabel);
        
        kitSelector = new JComboBox<>();
        kitSelector.setBackground(DarkTheme.BUTTON_BG);
        kitSelector.setForeground(DarkTheme.BUTTON_FG);
        
        for (String kitName : kitManager.getKitNames()) {
            kitSelector.addItem(kitName);
        }
        
        kitSelector.addActionListener(e -> {
            String selectedKit = (String) kitSelector.getSelectedItem();
            if (selectedKit != null && kitManager.switchToKit(selectedKit)) {
                loadCurrentKit();
                updateSequencerKit();
                updateCurrentKitLabel();
            }
        });
        
        topPanel.add(kitSelector);
        
        // Séparateur
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // Toggle Mode
        modeToggle = new JToggleButton("Sequencer Mode");
        modeToggle.setBackground(DarkTheme.BUTTON_BG);
        modeToggle.setForeground(DarkTheme.BUTTON_FG);
        modeToggle.setFocusPainted(false);
        modeToggle.addActionListener(e -> toggleMode());
        topPanel.add(modeToggle);
        
        frame.add(topPanel, BorderLayout.NORTH);
    }

    private void createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(DarkTheme.BACKGROUND);
        
        // Panel des pads (toujours visible)
        createPadGrid();
        centerPanel.add(createPadGridPanel(), BorderLayout.WEST);
        
        // Panel du séquenceur (initialement caché)
        createSequencerPanel();
        centerPanel.add(sequencerPanel, BorderLayout.CENTER);
        
        frame.add(centerPanel, BorderLayout.CENTER);
        
        // Ajouter les raccourcis clavier
        addKeyBindings();
    }

    private JPanel createPadGridPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 2, 2));
        gridPanel.setBackground(DarkTheme.BACKGROUND);
        gridPanel.setPreferredSize(new Dimension(300, 300));
        
        for (int i = 0; i < 9; i++) {
            JButton btn = new JButton();
            
            // Style des boutons
            btn.setFocusPainted(false);
            btn.setFocusable(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            btn.setBackground(DarkTheme.BUTTON_BG);
            btn.setForeground(DarkTheme.BUTTON_FG);
            
            Border blackBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
            btn.setBorder(blackBorder);
            btn.setBorderPainted(true);
            
            padButtons[i] = btn;
            gridPanel.add(btn);
        }
        
        return gridPanel;
    }

    private void createPadGrid() {
        // Déjà fait dans createPadGridPanel()
    }

    private void createSequencerPanel() {
        sequencerPanel = new JPanel(new BorderLayout());
        sequencerPanel.setBackground(DarkTheme.BACKGROUND);
        sequencerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(DarkTheme.BORDER), 
            "Sequencer",
            0, 0, null, DarkTheme.FOREGROUND));
        
        // Transport controls
        JPanel transportPanel = new JPanel(new FlowLayout());
        transportPanel.setBackground(DarkTheme.BACKGROUND);
        
        playStopButton = DarkTheme.createStyledButton("▶ Play");
        playStopButton.addActionListener(e -> togglePlayStop());
        transportPanel.add(playStopButton);
        
        clearButton = DarkTheme.createStyledButton("Clear");
        clearButton.addActionListener(e -> {
            System.out.println("Clear button clicked!");
            sequencer.clearPattern();
            System.out.println("About to refresh grid...");
            refreshStepGrid(); 
            System.out.println("Grid refreshed!");
        });
        transportPanel.add(clearButton);
        
        // BPM Control
        transportPanel.add(new JLabel("BPM:") {{ setForeground(DarkTheme.FOREGROUND); }});
        bpmSpinner = new JSpinner(new SpinnerNumberModel(120, 1, 999, 1));
        bpmSpinner.addChangeListener(e -> sequencer.setBpm((Integer) bpmSpinner.getValue()));
        transportPanel.add(bpmSpinner);
        
        // Length Control
        transportPanel.add(new JLabel("Steps:") {{ setForeground(DarkTheme.FOREGROUND); }});
        lengthSpinner = new JSpinner(new SpinnerNumberModel(16, 1, 16, 1));
        lengthSpinner.addChangeListener(e -> sequencer.setPatternLength((Integer) lengthSpinner.getValue()));
        transportPanel.add(lengthSpinner);
        
        // Current Step
        currentStepLabel = new JLabel("Step: 0");
        currentStepLabel.setForeground(DarkTheme.STATUS_FG);
        currentStepLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        transportPanel.add(currentStepLabel);
        
        sequencerPanel.add(transportPanel, BorderLayout.NORTH);
        
        // Step Grid
        createStepGrid();
        
        // Initialement caché
        sequencerPanel.setVisible(false);
    }

    private void createStepGrid() {
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(DarkTheme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Headers pour les steps
        gbc.gridy = 0;
        for (int step = 0; step < 16; step++) {
            gbc.gridx = step + 1;
            JLabel stepLabel = new JLabel(String.valueOf(step + 1));
            stepLabel.setForeground(DarkTheme.FOREGROUND);
            stepLabel.setHorizontalAlignment(JLabel.CENTER);
            stepLabel.setPreferredSize(new Dimension(30, 20));
            gridPanel.add(stepLabel, gbc);
        }
        
        // Rows pour chaque pad
        for (int pad = 0; pad < 9; pad++) {
            gbc.gridy = pad + 1;
            
            // Label du pad
            gbc.gridx = 0;
            JLabel padLabel = new JLabel("Pad " + (pad + 1));
            padLabel.setForeground(DarkTheme.FOREGROUND);
            padLabel.setPreferredSize(new Dimension(60, 25));
            gridPanel.add(padLabel, gbc);
            
            // Boutons steps
            for (int step = 0; step < 16; step++) {
                gbc.gridx = step + 1;
                JButton stepBtn = new JButton();
                stepBtn.setPreferredSize(new Dimension(25, 25));
                stepBtn.setBackground(DarkTheme.BUTTON_BG.darker());
                stepBtn.setFocusPainted(false);
                stepBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                
                final int padIndex = pad;
                final int stepIndex = step;
                
                stepBtn.addActionListener(e -> toggleStep(padIndex, stepIndex));
                
                stepButtons[pad][step] = stepBtn;
                gridPanel.add(stepBtn, gbc);
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBackground(DarkTheme.BACKGROUND);
        sequencerPanel.add(scrollPane, BorderLayout.CENTER);
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

    private void toggleMode() {
        sequencerMode = modeToggle.isSelected();
        sequencerPanel.setVisible(sequencerMode);
        
        if (sequencerMode) {
            modeToggle.setText("Soundboard Mode");
            frame.setSize(800, 600);
        } else {
            modeToggle.setText("Sequencer Mode");
            frame.setSize(450, 500);
            sequencer.stop(); // Arrêter le séquenceur si on retourne en mode soundboard
        }
        
        frame.revalidate();
        frame.repaint();
    }

    private void toggleStep(int padIndex, int stepIndex) {
        sequencer.toggleStep(padIndex, stepIndex);
        updateStepButton(padIndex, stepIndex);
    }

    private void updateStepButton(int padIndex, int stepIndex) {
        if (stepButtons[padIndex][stepIndex] != null) {
            boolean active = sequencer.getStep(padIndex, stepIndex);
            System.out.println("Updating button [" + padIndex + "][" + stepIndex + "] to " + active);
            stepButtons[padIndex][stepIndex].setBackground(
                active ? Color.ORANGE : DarkTheme.BUTTON_BG.darker()
            );
        }
    }

    private void togglePlayStop() {
        if (sequencer.isPlaying()) {
            sequencer.stop();
            playStopButton.setText("▶ Play");
        } else {
            sequencer.play();
            playStopButton.setText("⏸ Stop");
        }
    }

    private void updateSequencerKit() {
        sequencer.setCurrentKit(kitManager.getCurrentKit());
        updatePadLabels();
    }

    private void updatePadLabels() {
        SoundKit currentKit = kitManager.getCurrentKit();
        if (currentKit != null && sequencerPanel.isVisible()) {
            List<SoundPad> pads = currentKit.getPads();
            // Mettre à jour les labels des pads dans la grille du séquenceur
            // (Implémentation détaillée si nécessaire)
        }
    }

    // Implementation de StepListener
    @Override
    public void onStepChanged(int step) {
        SwingUtilities.invokeLater(() -> {
            currentStepLabel.setText("Step: " + (step + 1));
            
            // Highlight current step
            for (int pad = 0; pad < 9; pad++) {
                for (int s = 0; s < 16; s++) {
                    if (stepButtons[pad][s] != null) {
                        Border border = (s == step) ? 
                            BorderFactory.createLineBorder(Color.RED, 2) :
                            BorderFactory.createLineBorder(Color.BLACK, 1);
                        stepButtons[pad][s].setBorder(border);
                    }
                }
            }
        });
    }

    // Méthodes existantes adaptées
    private void loadCurrentKit() {
        SoundKit currentKit = kitManager.getCurrentKit();
        if (currentKit == null) {
            System.err.println("No current kit available!");
            return;
        }
        
        List<SoundPad> pads = currentKit.getPads();
        
        for (int i = 0; i < 9; i++) {
            JButton btn = padButtons[i];
            
            if (i < pads.size()) {
                SoundPad pad = pads.get(i);
                btn.setText(pad.getName());
                btn.setEnabled(true);
                btn.setBackground(DarkTheme.BUTTON_BG);
            } else {
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

        int[] keys = {
                KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9,
                KeyEvent.VK_NUMPAD4, KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6,
                KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3
        };

        for (int i = 0; i < 9; i++) {
            final int index = i;
            JButton btn = padButtons[i];

            final Color defaultColor = DarkTheme.BUTTON_BG;

            btn.addActionListener(e -> {
                playPad(index, btn, defaultColor, true);
            });

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
        if (index >= pads.size()) return;
        
        SoundPad pad = pads.get(index);
        if (pad == null) return;
        
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SoundBoardUI().createUI());
    }
}
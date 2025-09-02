package com.soundboard.sequencer;

import com.soundboard.model.SoundKit;
import com.soundboard.model.SoundPad;
import com.soundboard.audio.SoundPlayer;
import javax.swing.Timer;
import java.util.List;

public class SequencerEngine {
    private SoundPlayer player;
    private SoundKit currentKit;
    
    // Pattern state
    private boolean[][] patterns; // [padIndex][stepIndex]
    private int currentStep = 0;
    private int patternLength = 16;
    private int bpm = 120;
    
    // Playback control
    private Timer sequencerTimer;
    private boolean isPlaying = false;
    private boolean isRecording = false;
    
    public SequencerEngine(SoundPlayer player) {
        this.player = player;
        this.patterns = new boolean[9][16]; // 9 pads max, 16 steps max
        initTimer();
    }
    
    private void initTimer() {
        sequencerTimer = new Timer(calculateStepInterval(), e -> {
            if (isPlaying) {
                playCurrentStep();
                advanceStep();
            }
        });
    }
    
    // 🎵 COEUR DU SÉQUENCEUR : La boucle !
    private void playCurrentStep() {
        if (currentKit == null) return;
        
        List<SoundPad> pads = currentKit.getPads();
        
        // Jouer tous les pads actifs sur ce step
        for (int padIndex = 0; padIndex < Math.min(pads.size(), 9); padIndex++) {
            if (patterns[padIndex][currentStep]) {
                SoundPad pad = pads.get(padIndex);
                if (pad != null) {
                    player.play(pad.getFilePath());
                    System.out.println("Step " + currentStep + ": Playing " + pad.getName());
                }
            }
        }
    }
    
    private void advanceStep() {
        currentStep = (currentStep + 1) % patternLength;
        
        // Notify listeners (pour UI)
        fireStepChanged(currentStep);
    }
    
    // ⏱️ BPM Control
    public void setBpm(int bpm) {
        this.bpm = Math.max(1, Math.min(999, bpm));
        sequencerTimer.setDelay(calculateStepInterval());
    }
    
    private int calculateStepInterval() {
        // 4 steps par beat (16th notes)
        return (60 * 1000) / (bpm * 4);
    }
    
    // 🎛️ Pattern Control
    public void setPatternLength(int length) {
        this.patternLength = Math.max(1, Math.min(16, length));
        if (currentStep >= patternLength) {
            currentStep = 0;
        }
    }
    
    public void setStep(int padIndex, int stepIndex, boolean active) {
        if (padIndex >= 0 && padIndex < 9 && stepIndex >= 0 && stepIndex < 16) {
            patterns[padIndex][stepIndex] = active;
        }
    }
    
    public boolean getStep(int padIndex, int stepIndex) {
        if (padIndex >= 0 && padIndex < 9 && stepIndex >= 0 && stepIndex < 16) {
            return patterns[padIndex][stepIndex];
        }
        return false;
    }
    
    public void toggleStep(int padIndex, int stepIndex) {
        setStep(padIndex, stepIndex, !getStep(padIndex, stepIndex));
    }
    
    // ▶️ Transport Control
    public void play() {
        if (!isPlaying) {
            isPlaying = true;
            sequencerTimer.start();
            System.out.println("Sequencer started - BPM: " + bpm + ", Length: " + patternLength);
        }
    }
    
    public void stop() {
        isPlaying = false;
        sequencerTimer.stop();
        currentStep = 0;
        System.out.println("Sequencer stopped");
    }
    
    public void pause() {
        isPlaying = false;
        sequencerTimer.stop();
        System.out.println("Sequencer paused at step " + currentStep);
    }
    
    // 🔄 Pattern Management
    public void clearPattern() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 16; j++) {
                patterns[i][j] = false;
            }
        }
        System.out.println("Pattern cleared");
    }
    
    public void clearPadPattern(int padIndex) {
        if (padIndex >= 0 && padIndex < 9) {
            for (int j = 0; j < 16; j++) {
                patterns[padIndex][j] = false;
            }
        }
    }
    
    // 🎵 Kit Integration  
    public void setCurrentKit(SoundKit kit) {
        this.currentKit = kit;
    }
    
    // Getters
    public int getCurrentStep() { return currentStep; }
    public int getPatternLength() { return patternLength; }
    public int getBpm() { return bpm; }
    public boolean isPlaying() { return isPlaying; }
    
    // Event system (pour l'UI)
    public interface StepListener {
        void onStepChanged(int step);
    }
    
    private StepListener stepListener;
    
    public void setStepListener(StepListener listener) {
        this.stepListener = listener;
    }
    
    private void fireStepChanged(int step) {
        if (stepListener != null) {
            stepListener.onStepChanged(step);
        }
    }
    
    // Cleanup
    public void destroy() {
        stop();
        sequencerTimer = null;
    }
}
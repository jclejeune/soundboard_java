package com.soundboard.audio;

import javax.sound.sampled.*;
import java.io.File;

public class SoundPlayer {
    public void play(String filePath) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



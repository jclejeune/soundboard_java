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

        // Libération des ressources à la fin
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                clip.close();
                try {
                    audioIn.close();
                } catch (Exception ignored) {}
            }
        });
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}




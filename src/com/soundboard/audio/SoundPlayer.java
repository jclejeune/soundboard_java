package com.soundboard.audio;

import javazoom.jl.player.Player;
import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;

public class SoundPlayer {

    public void play(String filePath) {
        if (filePath.toLowerCase().endsWith(".mp3")) {
            playMP3(filePath);
        } else {
            playWAV(filePath);
        }
    }

    private void playWAV(String filePath) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

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

    private void playMP3(String filePath) {
        new Thread(() -> {
            try (FileInputStream fis = new FileInputStream(filePath)) {
                Player player = new Player(fis);
                player.play(); // bloquant → on le met dans un thread
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}





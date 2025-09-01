package com.soundboard.model;

public class SoundPad {
    private String name;
    private String filePath;

    public SoundPad(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
    }

    public String getName() { return name; }
    public String getFilePath() { return filePath; }
}


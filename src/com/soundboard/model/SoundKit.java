package com.soundboard.model;

import java.util.List;
import java.util.ArrayList;

public class SoundKit {
    private String name;
    private String description;
    private List<SoundPad> pads;
    private String kitPath; // Chemin vers le dossier du kit
    
    public SoundKit(String name, String description, String kitPath) {
        this.name = name;
        this.description = description;
        this.kitPath = kitPath;
        this.pads = new ArrayList<>();
    }
    
    // Constructeur avec liste de pads
    public SoundKit(String name, String description, String kitPath, List<SoundPad> pads) {
        this.name = name;
        this.description = description;
        this.kitPath = kitPath;
        this.pads = new ArrayList<>(pads);
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getKitPath() { return kitPath; }
    public List<SoundPad> getPads() { return new ArrayList<>(pads); }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setKitPath(String kitPath) { this.kitPath = kitPath; }
    
    // Gestion des pads
    public void addPad(SoundPad pad) {
        if (pads.size() < 9) { // Limite à 9 pads pour la grille 3x3
            pads.add(pad);
        }
    }
    
    public void removePad(int index) {
        if (index >= 0 && index < pads.size()) {
            pads.remove(index);
        }
    }
    
    public void replacePad(int index, SoundPad newPad) {
        if (index >= 0 && index < pads.size()) {
            pads.set(index, newPad);
        }
    }
    
    public SoundPad getPad(int index) {
        if (index >= 0 && index < pads.size()) {
            return pads.get(index);
        }
        return null;
    }
    
    public int getPadCount() {
        return pads.size();
    }
    
    // Méthodes utilitaires
    public boolean isFull() {
        return pads.size() >= 9;
    }
    
    public boolean isEmpty() {
        return pads.isEmpty();
    }
    
    // Pour le debugging
    @Override
    public String toString() {
        return String.format("SoundKit{name='%s', description='%s', pads=%d}", 
                           name, description, pads.size());
    }
    
    // Comparaison par nom
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SoundKit soundKit = (SoundKit) obj;
        return name.equals(soundKit.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
package com.soundboard.model;

import java.awt.Color;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SoundPad {
    private String name;
    private String filePath;
    private Color customColor;      // 🆕 Couleur personnalisée du pad
    private float volume;           // 🆕 Volume individuel (0.0 à 1.0)
    private boolean enabled;        // 🆕 Pad activé/désactivé
    private String description;     // 🆕 Description optionnelle
    
    // Couleur par défaut pour les pads
    public static final Color DEFAULT_COLOR = new Color(77, 77, 77);
    
    // Constructeur minimal (compatible avec votre code actuel)
    public SoundPad(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
        this.customColor = DEFAULT_COLOR;
        this.volume = 1.0f; // Volume max par défaut
        this.enabled = true;
        this.description = "";
    }
    
    // Constructeur complet
    public SoundPad(String name, String filePath, Color customColor, float volume, String description) {
        this.name = name;
        this.filePath = filePath;
        this.customColor = customColor != null ? customColor : DEFAULT_COLOR;
        this.volume = Math.max(0.0f, Math.min(1.0f, volume)); // Clamper entre 0 et 1
        this.enabled = true;
        this.description = description != null ? description : "";
    }
    
    // Getters existants
    public String getName() { return name; }
    public String getFilePath() { return filePath; }
    
    // Nouveaux getters
    public Color getCustomColor() { return customColor; }
    public float getVolume() { return volume; }
    public boolean isEnabled() { return enabled; }
    public String getDescription() { return description; }
    
    // Setters
    public void setName(String name) { 
        this.name = name != null ? name : "Unnamed"; 
    }
    
    public void setFilePath(String filePath) { 
        this.filePath = filePath; 
    }
    
    public void setCustomColor(Color customColor) { 
        this.customColor = customColor != null ? customColor : DEFAULT_COLOR; 
    }
    
    public void setVolume(float volume) { 
        this.volume = Math.max(0.0f, Math.min(1.0f, volume)); 
    }
    
    public void setEnabled(boolean enabled) { 
        this.enabled = enabled; 
    }
    
    public void setDescription(String description) { 
        this.description = description != null ? description : ""; 
    }
    
    // 🆕 Validation du fichier audio
    public boolean isFileValid() {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        return file.exists() && file.isFile() && isAudioFile(filePath);
    }
    
    // 🆕 Vérifier si c'est un fichier audio supporté
    private boolean isAudioFile(String path) {
        String extension = getFileExtension(path).toLowerCase();
        return extension.equals("wav") || extension.equals("mp3") || 
               extension.equals("aiff") || extension.equals("au");
    }
    
    // 🆕 Obtenir l'extension du fichier
    private String getFileExtension(String path) {
        int lastDot = path.lastIndexOf('.');
        return lastDot > 0 ? path.substring(lastDot + 1) : "";
    }
    
    // 🆕 Obtenir la taille du fichier (en bytes)
    public long getFileSize() {
        try {
            return Files.size(Paths.get(filePath));
        } catch (Exception e) {
            return -1;
        }
    }
    
    // 🆕 Obtenir la taille du fichier formatée
    public String getFormattedFileSize() {
        long size = getFileSize();
        if (size < 0) return "Unknown";
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }
    
    // 🆕 Clone/copier un pad
    public SoundPad copy() {
        return new SoundPad(this.name, this.filePath, this.customColor, this.volume, this.description);
    }
    
    // Méthodes utilitaires pour l'UI
    public boolean hasCustomColor() {
        return !customColor.equals(DEFAULT_COLOR);
    }
    
    public String getDisplayText() {
        return enabled ? name : "[" + name + "]"; // Crochets si désactivé
    }
    
    // Pour le debugging et logging
    @Override
    public String toString() {
        return String.format("SoundPad{name='%s', file='%s', enabled=%s, volume=%.2f}", 
                           name, filePath, enabled, volume);
    }
    
    // Comparaison (utile pour les listes)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SoundPad soundPad = (SoundPad) obj;
        return name.equals(soundPad.name) && filePath.equals(soundPad.filePath);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode() + filePath.hashCode();
    }
}
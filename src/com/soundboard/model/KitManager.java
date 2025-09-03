package com.soundboard.model;

import java.nio.file.*;
import java.util.*;

public class KitManager {
    private Map<String, SoundKit> kits;
    private SoundKit currentKit;
    private static final String KITS_DIRECTORY = "kits/";
    private static final String SOUNDS_DIRECTORY = "sounds/";
    
    public KitManager() {
        this.kits = new LinkedHashMap<>(); // Preserve order
        loadDefaultKits();
        loadKitsFromFiles();
    }
    
    // Getters
    public SoundKit getCurrentKit() { return currentKit; }
    public Set<String> getKitNames() { return kits.keySet(); }
    public Collection<SoundKit> getAllKits() { return kits.values(); }
    
    // Gestion des kits
    public SoundKit getKit(String name) {
        return kits.get(name);
    }
    
    public boolean addKit(SoundKit kit) {
        if (kit != null && !kits.containsKey(kit.getName())) {
            kits.put(kit.getName(), kit);
            return true;
        }
        return false;
    }
    
    public boolean removeKit(String name) {
        if (kits.containsKey(name) && kits.size() > 1) { // Garder au moins un kit
            kits.remove(name);
            // Si on supprime le kit actuel, changer vers le premier disponible
            if (currentKit != null && currentKit.getName().equals(name)) {
                currentKit = kits.values().iterator().next();
            }
            return true;
        }
        return false;
    }
    
    public boolean switchToKit(String name) {
        SoundKit kit = kits.get(name);
        if (kit != null) {
            currentKit = kit;
            System.out.println("Switched to kit: " + name);
            return true;
        }
        return false;
    }
    
    // Chargement des kits par défaut (celui qui existe déjà)
    private void loadDefaultKits() {
        // Kit par défaut basé sur votre soundboard actuel
        SoundKit defaultKit = new SoundKit("Default", "Kit de sons par défaut", SOUNDS_DIRECTORY);
        
        // Vos sons actuels
        String[] soundFiles = {
            "kick.wav", "snare.wav", "pluck.wav",
            "slap.wav", "hihat.wav", "clap.wav", 
            "healing.wav", "netflix.wav", "lazer.wav"
        };
        
        String[] padNames = {
            "Kick", "Snare", "Pluck",
            "Slap", "HiHat", "Clap",
            "Healing", "Netflix", "Lazer"
        };
        
        for (int i = 0; i < soundFiles.length; i++) {
            String fullPath = SOUNDS_DIRECTORY + soundFiles[i];
            SoundPad pad = new SoundPad(padNames[i], fullPath);
            defaultKit.addPad(pad);
        }
        
        addKit(defaultKit);
        
        // Kit Metal
        SoundKit metalKit = new SoundKit("Metal", "Kit de sons metal brutal", SOUNDS_DIRECTORY + "metal/");
        
        String[] metalSounds = {
            "metalkick.wav", "grindcoresnare.wav", "ride.wav", 
            "china.wav","note1.wav", "note2.wav", 
            "note3.wav", "note4.wav", "note5.wav"
        };
        
        String[] metalNames = {
            "MetalKick", "GrindSnare", "Ride", 
            "China", "Note 1", "Note 2", 
            "Note 3", "Note 4", "Note 5"
        };
        
        for (int i = 0; i < metalSounds.length; i++) {
            String fullPath = SOUNDS_DIRECTORY + "metal/" + metalSounds[i];
            SoundPad pad = new SoundPad(metalNames[i], fullPath);
            metalKit.addPad(pad);
        }
        addKit(metalKit);

        //-------
        // Kit test
        SoundKit testKit = new SoundKit("Test", "Kit Test", SOUNDS_DIRECTORY + "test/");

        String[] testSounds = {
            "a.wav", "b.wav", "c.wav", 
            "d.wav", "e.wav", "f.wav", 
            "g.wav", "h.wav", "i.wav"
        };
        
        String[] testNames = {
            "A", "B", "C", 
            "D", "E", "F", 
            "G", "H", "I"
        };
        
        
        for (int i = 0; i < testSounds.length; i++) {
            String fullPath = SOUNDS_DIRECTORY + "test/" + testSounds[i];
            SoundPad pad = new SoundPad(testNames[i], fullPath);
            testKit.addPad(pad);
        }
        addKit(testKit);

        //------
        currentKit = defaultKit; // Kit par défaut au démarrage
        //------
    }
    
    // Chargement des kits depuis fichiers (à implémenter plus tard avec JSON)
    private void loadKitsFromFiles() {
        try {
            Path kitsDir = Paths.get(KITS_DIRECTORY);
            if (Files.exists(kitsDir) && Files.isDirectory(kitsDir)) {
                // Future: Implémenter le chargement JSON
                System.out.println("Kits directory found: " + KITS_DIRECTORY);
            } else {
                System.out.println("Kits directory not found, using default kit only");
            }
        } catch (Exception e) {
            System.err.println("Error loading kits: " + e.getMessage());
        }
    }
    
    // Sauvegarde d'un kit (à implémenter plus tard avec JSON)
    public boolean saveKit(SoundKit kit) {
        try {
            // Créer le dossier kits s'il n'existe pas
            Path kitsDir = Paths.get(KITS_DIRECTORY);
            if (!Files.exists(kitsDir)) {
                Files.createDirectories(kitsDir);
            }
            
            // Future: Implémenter la sauvegarde JSON
            System.out.println("Kit saved: " + kit.getName());
            return true;
        } catch (Exception e) {
            System.err.println("Error saving kit: " + e.getMessage());
            return false;
        }
    }
    
    // Validation des fichiers audio d'un kit
    public boolean validateKit(SoundKit kit) {
        if (kit == null) return false;
        
        for (SoundPad pad : kit.getPads()) {
            String filePath = pad.getFilePath();
            if (!Files.exists(Paths.get(filePath))) {
                System.err.println("Missing audio file: " + filePath);
                return false;
            }
        }
        return true;
    }
    
    // Créer un nouveau kit vide
    public SoundKit createEmptyKit(String name, String description) {
        if (kits.containsKey(name)) {
            System.err.println("Kit already exists: " + name);
            return null;
        }
        
        String kitPath = SOUNDS_DIRECTORY + name.toLowerCase() + "/";
        SoundKit newKit = new SoundKit(name, description, kitPath);
        return newKit;
    }
    
    // Méthodes utilitaires
    public int getKitCount() {
        return kits.size();
    }
    
    public boolean hasKit(String name) {
        return kits.containsKey(name);
    }
    
    // Pour le debugging
    public void printKitInfo() {
        System.out.println("=== KIT MANAGER INFO ===");
        System.out.println("Total kits: " + kits.size());
        System.out.println("Current kit: " + (currentKit != null ? currentKit.getName() : "None"));
        
        for (SoundKit kit : kits.values()) {
            System.out.println("- " + kit.toString());
        }
    }
}
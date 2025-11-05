package Exterieur;

import Enum.Rang;
import Equipement.Pre_Equipement;
import main.Main;

import javax.json.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


public class Output {
    
    /**
     * Met à jour le json pour enregistrer la suppression des monstres nommés
     * @param nom la race à dismiss
     */
    public static void dismiss_race(String nom) {
        try {
            File infoFile = new File("Save" + Main.Path + "/info.json");
            JsonObject info;
            
            // Lire l'existant
            if (infoFile.exists()) {
                try (JsonReader reader = Json.createReader(new FileReader(infoFile))) {
                    info = reader.readObject();
                }
            } else {
                info = Json.createObjectBuilder().build(); // vide
            }
            
            // Récupérer ou créer le tableau
            JsonArray anciens = info.containsKey("monstres_nommes") ? info.getJsonArray("monstres_nommes") :
                    Json.createArrayBuilder().build();
            Set<String> noms = new LinkedHashSet<>();
            for (JsonValue val : anciens) {
                noms.add(((JsonString) val).getString());
            }
            
            // Ajouter le nouveau nom s'il n'existe pas déjà
            noms.add(nom);
            
            // Reconstruire le tableau
            JsonArrayBuilder builder = Json.createArrayBuilder();
            for (String n : noms) {
                builder.add(n);
            }
            
            // Reconstruire l'objet info
            JsonObjectBuilder infoBuilder = Json.createObjectBuilder();
            for (String key : info.keySet()) {
                if (!key.equals("monstres_nommes")) {
                    infoBuilder.add(key, info.get(key));
                }
            }
            infoBuilder.add("monstres_nommes", builder);
            
            // Écrire dans le fichier
            try (JsonWriter writer = Json.createWriter(new FileWriter(infoFile))) {
                writer.writeObject(infoBuilder.build());
            }
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ajout du monstre nommé : " + e.getMessage());
        }
    }
    
    /**
     * Met à jour le json pour enregistrer la suppression des items uniques
     * @param item l'objet a dismiss
     */
    public static void dismiss_item(Pre_Equipement item) {
        try {
            File infoFile = new File("Save" + Main.Path + "/info.json");
            JsonObject info = infoFile.exists() ? Json.createReader(new FileReader(infoFile)).readObject() :
                    Json.createObjectBuilder().build();
            
            JsonObject items = info.containsKey("items_uniques") ? info.getJsonObject("items_uniques") :
                    Json.createObjectBuilder().build();
            
            JsonObjectBuilder itemsBuilder = Json.createObjectBuilder();
            
            // Copier toutes les clés existantes
            for (String key : items.keySet()) {
                itemsBuilder.add(key, items.get(key));
            }
            
            if (item.rang != Rang.PROMOTION) {
                String rangKey = item.rang.name();
                recuperation(item, items, itemsBuilder, rangKey);
                
            } else {
                JsonObject promoObj = items.containsKey("PROMOTION") ? items.getJsonObject("PROMOTION") :
                        Json.createObjectBuilder().build();
                
                JsonObjectBuilder promoBuilder = Json.createObjectBuilder();
                
                // Copier les sous-clés existantes
                for (String k : promoObj.keySet()) {
                    promoBuilder.add(k, promoObj.get(k));
                }
                
                String promoKey = item.promo_type.name();
                recuperation(item, promoObj, promoBuilder, promoKey);
                itemsBuilder.add("PROMOTION", promoBuilder.build());
            }
            
            // Reconstruction de info.json
            JsonObjectBuilder infoBuilder = Json.createObjectBuilder();
            for (String k : info.keySet()) {
                if (!k.equals("items_uniques")) {
                    infoBuilder.add(k, info.get(k));
                }
            }
            infoBuilder.add("items_uniques", itemsBuilder.build());
            
            try (JsonWriter writer = Json.createWriter(new FileWriter(infoFile))) {
                writer.writeObject(infoBuilder.build());
            }
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ajout d'un item unique : " + e.getMessage());
        }
    }
    
    /**
     * Ajoute le nom d'un pré-équipment à une liste JSON associée à une clé promotionnelle.
     * <p>
     * Cette méthode vérifie si la clé {@code promoKey} existe déjà dans l'objet {@code promoObj}.
     * Si oui, elle récupère le tableau JSON existant, sinon elle initialise un tableau vide.
     * Elle ajoute ensuite le nom de l'objet {@code item} à l'ensemble des noms (sans doublons),
     * puis reconstruit le tableau JSON et l'ajoute au {@code promoBuilder}.
     * @param item         le pré-équipment à ajouter (son nom sera inséré dans le tableau).
     * @param promoObj     l'objet JSON source contenant éventuellement des entrées existantes
     * @param promoBuilder le constructeur JSON dans lequel le tableau mis à jour sera inséré
     * @param promoKey     la clé sous laquelle le tableau des noms est stocké dans le JSON
     */
    private static void recuperation(Pre_Equipement item, JsonObject promoObj, JsonObjectBuilder promoBuilder,
                                     String promoKey) {
        JsonArray existants = promoObj.containsKey(promoKey) ? promoObj.getJsonArray(promoKey) :
                Json.createArrayBuilder().build();
        
        Set<String> noms = new LinkedHashSet<>();
        for (JsonValue val : existants) {
            noms.add(((JsonString) val).getString());
        }
        noms.add(item.nom);
        
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (String nom : noms) arrayBuilder.add(nom);
        
        promoBuilder.add(promoKey, arrayBuilder);
    }
    
    /**
     * Joue un son de dés
     */
    public static void jouerSonDe() {
        jouerSon("Audio/son_des.wav");
    }
    
    /**
     * Joue un bruit de musique
     */
    public static void jouerSonOr(int nb_fois) {
        for (int i = 0; i <= nb_fois; i++) {
            jouerSon("Audio/money.wav");
            try {
                Thread.sleep(250); // pause de 0.25 seconde
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // bonne pratique : réinterrompre le thread
                System.err.println("Interruption pendant la pause : " + e.getMessage());
            }
        }
    }
    
    public static void jouerSonMarche() {
        System.out.println("Son momentanément indisponible.");
        //jouerSon("Audio/marche.wav");
    }
    
    /**
     * Joue le rugissement d'un ours
     */
    public static void jouerSonAttaque() {
        jouerSon("Audio/attaque.wav");
    }
    
    /**
     * Joue le son d'ouverture d'un coffre
     */
    public static void jouerSonCoffre() {
        jouerSon("Audio/coffre.wav");
    }
    
    /**
     * Joue un son encourageant
     */
    public static void JouerSonLvlUp() {
        jouerSon("Audio/level_up.wav");
    }
    
    /**
     * Joue un cri d'agonie
     */
    public static void JouerSonMortDef() {
        jouerSon("Audio/mort_def.wav");
    }
    
    /**
     * Joue un (autre) cri d'agonie
     */
    public static void JouerSonMort() {
        jouerSon("Audio/mort.wav");
    }
    
    /**
     * Joue le son d'un tir à l'arc
     */
    public static void JouerSonTir() {
        jouerSon("Audio/tir.wav");
    }
    
    /**
     * Joue le son d'une attaque
     */
    public static void JouerSonDommage() {
        jouerSon("Audio/degas.wav");
    }
    
    public static void jouerSonMonstreMort() {
        jouerSon("Audio/mort_monstre.wav");
    }
    
    /**
     * Joue un son
     * @param pathName le nom du fichier à jouer
     */
    static void jouerSon(String pathName) {
        {
            try {
                File fichierAudio = new File(pathName);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(fichierAudio);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
                
            } catch (Exception e) {
                System.err.println("Erreur lors de la lecture du son : " + e.getMessage());
            }
        }
    }
    
    public static void viderSauvegarde(int index) throws IOException {
        File dossier = new File("Save" + index);
        
        if (!dossier.exists()) {
            if (!dossier.mkdirs()) {
                throw new IOException("Impossible de créer le dossier : " + dossier.getAbsolutePath());
            }
        }
        
        for (File f : Objects.requireNonNull(dossier.listFiles())) {
            if (!f.delete()) {
                throw new IOException("Impossible de supprimer : " + f.getName());
            }
        }
    }
}
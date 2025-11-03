package Exterieur;

import Metiers.Joueur;
import main.Main;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SaveManager {
    
    public static final String[] SAVE_DIRS = {"Save0", "Save1", "Save2"};
    
    public static void afficherSauvegardes() {
        System.out.println("Choisissez une sauvegarde :");
        
        for (int i = 0; i < SAVE_DIRS.length; i++) {
            String path = SAVE_DIRS[i] + "/info.json";
            File infoFile = new File(path);
            
            if (!infoFile.exists()) {
                System.out.println(i + " (vide)");
                continue;
            }
            
            try (JsonReader reader = Json.createReader(new FileReader(infoFile))) {
                JsonObject info = reader.readObject();
                String titre = info.getString("titre", "Sans titre");
                int nb = info.getInt("nombre_joueurs", 0);
                System.out.print(i + " — " + titre + " (" + nb + " joueur" + (nb > 1 ? "s" : "") + ")");
                String version = info.getString("version", "inconnue");
                if (!version.equals(Main.Version.CURRENT)) {
                    System.err.print(" ⚠️ Sauvegarde créée sous version " + version + " (actuelle : " + Main.Version.CURRENT + ")");
                }
                System.out.println();
            } catch (IOException e) {
                System.out.println(i + " (erreur de lecture)");
            }
        }
    }
    
    /**
     * Charge les données d'une save
     * @return la liste des joueurs
     * @throws IOException toujours
     */
    public static Joueur[] chargerSauvegarde() throws IOException {
        String dossier = SAVE_DIRS[Main.Path];
        File infoFile = new File(dossier + "/info.json");
        
        if (!infoFile.exists()) throw new IOException("Sauvegarde introuvable.");
        
        try (JsonReader reader = Json.createReader(new FileReader(infoFile))) {
            JsonObject info = reader.readObject();
            int nb = info.getInt("nombre_joueurs");
            
            Joueur[] joueurs = new Joueur[nb];
            for (int i = 0; i < nb; i++) {
                String chemin = dossier + "/Joueur" + i + ".json";
                joueurs[i] = Joueur.chargerJoueur(chemin);
            }
            return joueurs;
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
    
    /**
     * Crée une sauvegarde de zéro
     * @param titre         le titre de la save
     * @param nombreJoueurs le nombre de joueur
     * @throws IOException toujours
     */
    public static void creerSauvegarde(String titre, int nombreJoueurs) throws IOException {
        String dossier = "Save" + Main.Path;
        File dir = new File(dossier);
        
        if (!dir.exists()) {
            System.out.println("📁 Création du dossier " + dossier);
            if (!dir.mkdir()) {
                throw new IOException("création dossier impossible.");
            }
        }
        
        // Préparation pour les objets
        JsonObjectBuilder itemsBuilder = Json.createObjectBuilder().add("O", Json.createArrayBuilder()).add("I",
                Json.createArrayBuilder()).add("II", Json.createArrayBuilder()).add("III", Json.createArrayBuilder()).add("IV", Json.createArrayBuilder()).add("PROMOTION", Json.createObjectBuilder().add("CREATURE", Json.createArrayBuilder()).add("ARTEFACT", Json.createArrayBuilder()).add("AMELIORATION", Json.createArrayBuilder()));
        
        // Création du fichier info.json
        JsonObject info = Json.createObjectBuilder().add("titre", titre).add("version", Main.Version.CURRENT).add(
                "nombre_joueurs", nombreJoueurs).add("monstres_nommes", Json.createArrayBuilder()).add("items_uniques"
                , itemsBuilder.build()).build();
        
        // Writer pretty-print
        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(config);
        
        try (Writer fileWriter = new FileWriter(dossier + "/info.json"); JsonWriter writer =
                writerFactory.createWriter(fileWriter)) {
            writer.writeObject(info);
        }
        
        // Création des fichiers JoueurX.json vides
        for (int i = 0; i < nombreJoueurs; i++) {
            Joueur joueur = Main.joueurs[i];
            JsonObject joueurVide = Json.createObjectBuilder().add("nom", joueur.getNom()).add("metier",
                    joueur.getMetier().name()).add("ob_f", joueur.get_ob_f()).add("position",
                    joueur.getPosition().name()).add("xp", joueur.getXp()).add("parent", joueur.getParent().name()).add("effets", Json.createArrayBuilder()).build();
            
            try (Writer fileWriter = new FileWriter(dossier + "/Joueur" + i + ".json"); JsonWriter writer =
                    writerFactory.createWriter(fileWriter)) {
                writer.writeObject(joueurVide);
            }
        }
        
        System.out.println("✅ Sauvegarde " + titre + " créée avec " + nombreJoueurs + " joueur(s).\n");
    }
    
    
    /**
     * Sauvegarde les données utilisateurs
     * @throws IOException toujours
     */
    public static void sauvegarder(boolean discret) throws IOException {
        String dossier = SaveManager.SAVE_DIRS[Main.Path];
        File infoFile = new File(dossier + "/info.json");
        
        if (!new File(dossier).exists()) {
            throw new IOException("Le dossier de sauvegarde n'existe pas : " + dossier);
        }
        
        // Charger l'ancien info.json
        JsonObject oldInfo = infoFile.exists() ? Json.createReader(new FileReader(infoFile)).readObject() :
                Json.createObjectBuilder().build();
        
        String titre = oldInfo.getString("titre", "Sauvegarde sans nom");
        
        // Sauvegarde des joueurs
        for (int i = 0; i < Main.joueurs.length; i++) {
            Main.joueurs[i].sauvegarder(dossier + "/Joueur" + i + ".json");
        }
        
        // Reconstruction de info.json
        JsonObjectBuilder builder = Json.createObjectBuilder();
        
        // Conserver les anciennes clés sauf "nombre_joueurs"
        for (String key : oldInfo.keySet()) {
            builder.add(key, oldInfo.get(key));
        }
        
        // Mettre à jour le titre et le nombre de joueurs
        builder.add("titre", titre);
        builder.add("version", Main.Version.CURRENT);
        builder.add("nombre_joueurs", Main.joueurs.length);
        
        // Writer pretty-print
        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(config);
        
        try (Writer fileWriter = new FileWriter(infoFile); JsonWriter writer = writerFactory.createWriter(fileWriter)) {
            writer.writeObject(builder.build());
        }
        
        if (!discret) {
            System.out.println("✅ Sauvegarde mise à jour : " + titre + " (" + Main.joueurs.length + " joueur(s))");
        }
    }
}
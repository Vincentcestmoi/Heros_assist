package Exterieur;

import Metiers.Joueur;
import main.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import javax.json.*;

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
                System.out.println(i + " — " + titre + " (" + nb + " joueur" + (nb > 1 ? "s" : "") + ")");
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
     * @param titre le titre de la save
     * @param nombreJoueurs le nombre de joueur
     * @throws IOException toujours
     */
    public static void creerSauvegarde(String titre, int nombreJoueurs) throws IOException {
        String dossier = "Save" + Main.Path;
        File dir = new File(dossier);

        if (!dir.exists()) {
            System.out.println("📁 Création du dossier " + dossier);
            if(!dir.mkdir()){
                throw new IOException("création dossier impossible.");
            }
        }

        // Préparation pour les objets
        JsonObjectBuilder itemsBuilder = Json.createObjectBuilder()
                .add("O", Json.createArrayBuilder().build())
                .add("I", Json.createArrayBuilder().build())
                .add("II", Json.createArrayBuilder().build())
                .add("III", Json.createArrayBuilder().build())
                .add("IV", Json.createArrayBuilder().build())
                .add("PROMOTION", Json.createObjectBuilder()
                        .add("MONTURE", Json.createArrayBuilder().build())
                        .add("ARTEFACT", Json.createArrayBuilder().build())
                        .add("AMELIORATION", Json.createArrayBuilder().build())
                        .build());

        // Création du fichier info.json
        JsonObject info = Json.createObjectBuilder()
                .add("titre", titre)
                .add("nombre_joueurs", nombreJoueurs)
                .add("monstres_nommes", Json.createArrayBuilder().build())
                .add("items_uniques", itemsBuilder.build())
                .build();

        try (JsonWriter writer = Json.createWriter(new FileWriter(dossier + "/info.json"))) {
            writer.writeObject(info);
        }

        // Création des fichiers JoueurX.json vides
        for (int i = 0; i < nombreJoueurs; i++) {
            Joueur joueur = Main.joueurs[i];
            JsonObject joueurVide = Json.createObjectBuilder()
                    .add("nom", joueur.getNom())
                    .add("metier", joueur.getMetier().name())
                    .add("ob_f", joueur.get_ob_f())
                    .add("position", joueur.getPosition().name())
                    .add("xp", joueur.getXp())
                    .add("parent", joueur.getParent().name())
                    .add("effets", Json.createArrayBuilder())
                    .build();

            try (JsonWriter writer = Json.createWriter(new FileWriter(dossier + "/Joueur" + i + ".json"))) {
                writer.writeObject(joueurVide);
            }
        }

        System.out.println("✅ Sauvegarde " + dossier + " créée avec " + nombreJoueurs + " joueur(s).");
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
        JsonObject oldInfo = infoFile.exists()
                ? Json.createReader(new FileReader(infoFile)).readObject()
                : Json.createObjectBuilder().build();

        String titre = oldInfo.getString("titre", "Sauvegarde sans nom");

        // Sauvegarde des joueurs
        for (int i = 0; i < Main.joueurs.length; i++) {
            Main.joueurs[i].sauvegarder(dossier + "/Joueur" + i + ".json");
        }

        // Reconstruction de info.json
        JsonObjectBuilder builder = Json.createObjectBuilder();

        // Conserver les anciennes clés sauf "nombre_joueurs"
        for (String key : oldInfo.keySet()) {
            if (!key.equals("nombre_joueurs")) {
                builder.add(key, oldInfo.get(key));
            }
        }

        // Mettre à jour le titre et le nombre de joueurs
        builder.add("titre", titre);
        builder.add("nombre_joueurs", Main.joueurs.length);

        try (JsonWriter writer = Json.createWriter(new FileWriter(infoFile))) {
            writer.writeObject(builder.build());
        }

        if (!discret) {
            System.out.println("✅ Sauvegarde mise à jour : " + titre + " (" + Main.joueurs.length + " joueur(s))");
        }
    }
}

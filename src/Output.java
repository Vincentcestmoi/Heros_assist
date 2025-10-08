import javax.sound.sampled.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import javax.json.*;
import java.util.Set;
import java.util.LinkedHashSet;


public class Output {

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
            JsonArray anciens = info.containsKey("monstres_nommes") ? info.getJsonArray("monstres_nommes") : Json.createArrayBuilder().build();
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
            JsonObject info = infoFile.exists()
                    ? Json.createReader(new FileReader(infoFile)).readObject()
                    : Json.createObjectBuilder().build();

            JsonObject items = info.containsKey("items_uniques")
                    ? info.getJsonObject("items_uniques")
                    : Json.createObjectBuilder().build();

            JsonObjectBuilder itemsBuilder = Json.createObjectBuilder();

            // Copier toutes les clés existantes
            for (String key : items.keySet()) {
                itemsBuilder.add(key, items.get(key));
            }

            if (item.rang != Rang.PROMOTION) {
                String rangKey = item.rang.name();
                JsonArray existants = items.containsKey(rangKey)
                        ? items.getJsonArray(rangKey)
                        : Json.createArrayBuilder().build();

                Set<String> noms = new LinkedHashSet<>();
                for (JsonValue val : existants) {
                    noms.add(((JsonString) val).getString());
                }
                noms.add(item.nom);

                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                for (String nom : noms) arrayBuilder.add(nom);

                itemsBuilder.add(rangKey, arrayBuilder);

            } else {
                JsonObject promoObj = items.containsKey("PROMOTION")
                        ? items.getJsonObject("PROMOTION")
                        : Json.createObjectBuilder().build();

                JsonObjectBuilder promoBuilder = Json.createObjectBuilder();

                // Copier les sous-clés existantes
                for (String k : promoObj.keySet()) {
                    promoBuilder.add(k, promoObj.get(k));
                }

                String promoKey = item.promo_type.name();
                JsonArray existants = promoObj.containsKey(promoKey)
                        ? promoObj.getJsonArray(promoKey)
                        : Json.createArrayBuilder().build();

                Set<String> noms = new LinkedHashSet<>();
                for (JsonValue val : existants) {
                    noms.add(((JsonString) val).getString());
                }
                noms.add(item.nom);

                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                for (String nom : noms) arrayBuilder.add(nom);

                promoBuilder.add(promoKey, arrayBuilder);
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
     * Joue un son de dés
     */
    static void jouerSonDe() {
        try {
            File fichierAudio = new File("Audio/son_des.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(fichierAudio);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("Erreur lors de la lecture du son : " + e.getMessage());
        }
    }

}

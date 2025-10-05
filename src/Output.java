import javax.sound.sampled.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Output {

    static boolean write(String fichier, String contenu) {
        try {
            FileWriter writer = new FileWriter(Main.Path + fichier);
            writer.write(contenu);
            writer.close();
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture : " + e.getMessage());
            return false;
        }
    }

    public static void dismiss_race(String fichier, String nom) {
        write((fichier), nom + "," + Input.read_log(fichier));
    }

    public static void dismiss_item(String fichier, Pre_Equipement pre_equipement) {
        write((fichier), pre_equipement.nom + "," + Input.read_log(fichier));
    }

    public static void save_data() throws IOException {
        for (int i = 0; i < Main.nbj; i++) {
            Main.joueurs[i].sauvegarder(Main.Path + "Joueur" + i + ".json");
        }
        Output.write(Main.Path + "nbj", String.valueOf(Main.nbj));
    }

    /**
     * supprime le contenue d'un fichier
     * @param nomFichier le chemin du fichier
     */
    public static void delete_fichier(String nomFichier) {
        if(write(Main.Path + nomFichier, ";")) {
            System.out.println(nomFichier + " écrasé avec succès");
        }
    }

    /**
     * Joue un son de dés
     */
    static void jouerSonDe() {
        try {
            File fichierAudio = new File(Main.Path + "../son_des.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(fichierAudio);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.err.println("Erreur lors de la lecture du son : " + e.getMessage());
        }
    }

    /**
     *
     * @param mot le mot d'entré
     * @return le même mot, mais barré
     */
    public static String barrer(String mot) {
        StringBuilder sb = new StringBuilder();
        for (char c : mot.toCharArray()) {
            sb.append(c).append('̶'); // ajoute le caractère de barré
        }
        return sb.toString();
    }

}

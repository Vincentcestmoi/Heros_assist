import javax.sound.sampled.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Output {

    static boolean write(String fichier, String contenu) {
        try {
            FileWriter writer = new FileWriter(Main.Path + fichier + Main.Ext);
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

    /**
     * Mets à jour les sauvegarde des joueurs
     * @param j l'indice du joueur dont on doit mettre à jour les données (0 à 3).
     */
    public static void write_data(int j) {
        String fichier = "Joueur ";
        fichier += switch (j) {
            case 0 -> "A";
            case 1 -> "B";
            case 2 -> "C";
            case 3 -> "D";
            default -> "ERROR";
        };
        String metier =" ";
        for(String m : new String[] {Main.necromancien, Main.guerriere, Main.archimage, Main.alchimiste}){
            if (m.equals(Main.nom[j])){
                metier = Main.nom[j];
                break;
            }
        }
        write(fichier, Main.nom[j] + "," + metier + "," + Main.positions[j].toString() + "," + Main.f[j] + ";");
    }

    /**
     * supprime le contenue d'un fichier
     * @param nomFichier le chemin du fichier
     */
    public static void delete_fichier(String nomFichier) {
        if(write(nomFichier, ";")) {
            System.out.println(nomFichier + " écrasé avec succès");
        }
    }

    /**
     * Joue un son de dés pendant 2,3 secondes (bloque le terminal durant ce temp)
     */
    static void jouerSonDe() {
        try {
            File fichierAudio = new File(Main.Path + "../son_des.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(fichierAudio);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

            // Attendre la durée souhaitée, puis arrêter le son
            Thread.sleep(2300);
            clip.stop();
            clip.close();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException | InterruptedException e) {
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

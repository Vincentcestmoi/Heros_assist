import java.io.FileWriter;
import java.io.IOException;

public class Output {

    static boolean write(String fichier, String contenu) {
        try {
            FileWriter writer = new FileWriter("Save/" + fichier);
            writer.write(contenu);
            writer.close();
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture : " + e.getMessage());
            return false;
        }
    }

    public static void dismiss_race(String fichier, String nom) {
        write((fichier + ".txt"), nom + "," + Input.read_log(fichier));
    }

    public static void dismiss_item(String fichier, Pre_Equipement pre_equipement) {
        write((fichier + ".txt"), pre_equipement.nom + "," + Input.read_log(fichier));
    }

    public static void write_data(String joueur) {
        switch (joueur) {
            case Main.Joueur_A -> write("Joueur A.txt", joueur + "," + Main.positions[0].toString() + "," + Main.f_a + ";");
            case Main.Joueur_C -> write("Joueur C.txt", joueur + "," + Main.positions[1].toString() + ","  + Main.f_b + ";");
            case Main.Joueur_B -> write("Joueur B.txt", joueur + "," + Main.positions[2].toString() + ","  + Main.f_c + ";");
            case Main.Joueur_D -> write("Joueur D.txt", joueur + "," + Main.positions[3].toString() + ","  + Main.f_d + ";");
            default -> System.out.println("Joueur non reconnu");
        }
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
}

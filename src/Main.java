import java.io.IOException;
import java.util.Random;


public class Main {

    static Input input = new Input();
    static Random rand = new Random();

    public static void main(String[] args) throws IOException {
        System.out.print("Entrez le nombre de joueur : ");
        int nbj = input.readInt();
        if(nbj < 1 || nbj > 4){
            System.out.println("Nombre de joueur invalide : 1 à 4 joueurs seulement.");
            return;
        }
        int f_a = 0, f_b = 0, f_c = 0, f_d = 0;
        String[] nom = {"Joueur A", "Joueur B", "Joueur C", "Joueur D"};
        boolean run = true;
        int i = 0;
        while(run){
            if (i == nbj){
                i = 0;
            }
            System.out.println(nom[i] + " c'est votre tour");
            int temp = 0;
            switch (input.tour()){
                case EXP_ENFERS -> temp = expedition_enfer(nbj, i, f_a, f_b, f_c, f_d);
                case EXP_PRAIRIE -> temp = expedition_prairie(nbj, i, f_a, f_b, f_c, f_d);
                case EXP_VIGNE -> temp = expedition_vigne(nbj, i, f_a, f_b, f_c, f_d);
                case EXP_TEMPLE -> temp = expedition_temple(nbj, i, f_a, f_b, f_c, f_d);
                case EXP_MER -> temp = expedition_mer(nbj, i, f_a, f_b, f_c, f_d);
                case EXP_MONT -> temp = expedition_mont(nbj, i, f_a, f_b, f_c, f_d);
                case EXP_OLYMPE -> temp = expedition_olympe(nbj, i, f_a, f_b, f_c, f_d);
                case DRESSER -> {
                    switch (nom[i]) {
                        case "Joueur A" -> {
                            if (f_a == 0) {
                                System.out.println("Erreur : aucun familier détecté.");
                            } else {
                                f_a += Monstre.entrainement();
                                if (f_a <= 0) {
                                    System.out.println("Votre familier vous a fuit de manière définitive");
                                    f_a = 0;
                                }
                                else if(f_a >= 3){
                                    System.out.println("Vous avez atteint le niveau maximal de loyauté de la part de votre familier");
                                    f_a = 3;
                                }
                            }
                        }
                        case "Joueur B" -> {
                            if (f_b == 0) {
                                System.out.println("Erreur : aucun familier détecté.");
                            } else {
                                f_b += Monstre.entrainement();
                                if (f_b <= 0) {
                                    System.out.println("Votre familier vous a fuit de manière définitive");
                                    f_b = 0;
                                }
                                else if(f_b >= 3){
                                    System.out.println("Vous avez atteint le niveau maximal de loyauté de la part de votre familier");
                                    f_b = 3;
                                }
                            }
                        }
                        case "Joueur C" -> {
                            if (f_c == 0) {
                                System.out.println("Erreur : aucun familier détecté.");
                            } else {
                                f_c += Monstre.entrainement();
                                if (f_c <= 0) {
                                    System.out.println("Votre familier vous a fuit de manière définitive");
                                    f_c = 0;
                                }
                                else if(f_c >= 3){
                                    System.out.println("Vous avez atteint le niveau maximal de loyauté de la part de votre familier");
                                    f_c = 3;
                                }
                            }
                        }
                        case "Joueur D" -> {
                            if (f_d == 0) {
                                System.out.println("Erreur : aucun familier détecté.");
                            } else {
                                f_d += Monstre.entrainement();
                                if (f_d <= 0) {
                                    System.out.println("Votre familier vous a fuit de manière définitive");
                                    f_d = 0;
                                }
                                else if(f_d >= 3){
                                    System.out.println("Vous avez atteint le niveau maximal de loyauté de la part de votre familier");
                                    f_d = 3;
                                }
                            }
                        }
                        default -> System.out.println("Erreur : joueur " + nom[i] + " non reconnu");
                    }
                }
                case ATTENDRE -> System.out.println(nom[i] + " passe son tour.");
                case QUITTER -> run = false;
                case FAMILIER_PLUS -> {
                    switch (nom[i]) {
                        case "Joueur A" -> {
                            f_a = 1;
                            System.out.println("Joueur A a bien reçu son nouveau familier");
                        }
                        case "Joueur B" -> {
                            f_b = 1;
                            System.out.println("Joueur B a bien reçu son nouveau familier");
                        }
                        case "Joueur C" -> {
                            f_c = 1;
                            System.out.println("Joueur C a bien reçu son nouveau familier");
                        }
                        case "Joueur D" -> {
                            f_d = 1;
                            System.out.println("Joueur D a bien reçu son nouveau familier");
                        }
                        default -> System.out.println("Erreur : joueur " + nom[i] + " non reconnu");
                    }
                }
                case FAMILIER_MOINS -> {
                    switch (nom[i]) {
                        case "Joueur A" -> {
                            f_a = 0;
                            System.out.println("Le familier du Joueur A a bien été supprimé");
                        }
                        case "Joueur B" -> {
                            f_b = 0;
                            System.out.println("Le familier du Joueur B a bien été supprimé");
                        }
                        case "Joueur C" -> {
                            f_c = 0;
                            System.out.println("Le familier du Joueur C a bien été supprimé");
                        }
                        case "Joueur D" -> {
                            f_d = 0;
                            System.out.println("Le familier du Joueur D a bien été supprimé");
                        }
                        default -> System.out.println("Erreur : joueur " + nom[i] + " non reconnu");
                    }
                }
                case RETOUR -> i = i == 0 ? nbj - 2 : i - 2;
            }
            switch(temp) {
                case 1 -> {
                    f_a = 1;
                    System.out.println("Le joueur A a un nouveau familier");
                }
                case 2 -> {
                    f_b = 1;
                    System.out.println("Le joueur B a un nouveau familier");
                }
                case 3 -> {
                    f_c = 1;
                    System.out.println("Le joueur C a un nouveau familier");
                }
                case 4 -> {
                    f_d = 1;
                    System.out.println("Le joueur D a un nouveau familier");
                }
                default -> { // dont 0
                }
            }
            i++;
        }
        System.out.print("Fin du programme");
    }

    static int expedition_enfer(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        switch(input.D4()){
            case 1, 2, 3 -> {
                Monstre monstre = Lieu.enfers();
                System.out.println("Vous appercevez un(e) " + monstre.nom);
                if(input.yn("Voulez vous l'attaquer ?")){
                    return Combat.affrontement(nbj, -1, f_a, f_b, f_c, f_d, monstre);
                }
                else{
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 4, 5 -> {
                if(rand.nextBoolean()){
                    System.out.println("Vous récuperez 1 équipement(s) I");
                }
                else{
                    System.out.println("Vous ne trouvez rien ni personne");
                }
            }
            default -> {
                Monstre monstre = Lieu.enfers();
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(nbj, meneur, f_a, f_b, f_c, f_d, monstre);
            }
        }
        return 0;
    }

    static int expedition_prairie(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d)  throws IOException {
        switch(input.D6()){
            case 2, 3, 4 -> {
                Monstre monstre = Lieu.prairie();
                System.out.println("Vous appercevez un(e) " + monstre.nom);
                if(input.yn("Voulez vous l'attaquer ?")){
                    return Combat.affrontement(nbj, -1, f_a, f_b, f_c, f_d, monstre);
                }
                else{
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 5, 6, 7 -> {
                if(rand.nextBoolean()){
                    System.out.println("Vous récuperez 1 équipement(s) I");
                }
                else{
                    System.out.println("Vous ne trouvez rien ni personne");
                }
            }
            default -> { // 1
                Monstre monstre = Lieu.prairie();
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(nbj, meneur, f_a, f_b, f_c, f_d, monstre);
            }
        }
        return 0;
    }

    static int expedition_vigne(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        switch(input.D6()){
            case 3, 4, 5 -> {
                Monstre monstre = Lieu.vigne();
                System.out.println("Vous appercevez un(e) " + monstre.nom);
                if(input.yn("Voulez vous l'attaquer ?")){
                    return Combat.affrontement(nbj, -1, f_a, f_b, f_c, f_d, monstre);
                }
                else{
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 6 -> {
                if(rand.nextBoolean()){
                    System.out.println("Vous récuperez " + rand.nextInt(2) + 1 + " équipement(s) I");
                }
                else{
                    System.out.println("Vous ne trouvez rien ni personne");
                }
            }
            case 7 -> {
                if(rand.nextBoolean()){
                    System.out.println("Vous récuperez 1 équipement(s) I");
                }
                else{
                    System.out.println("Vous trouvez une Promotion");
                }
            }
            default -> { // 1, 2
                Monstre monstre = Lieu.vigne();
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(nbj, meneur, f_a, f_b, f_c, f_d, monstre);
            }
        }
        return 0;
    }

    static int expedition_temple(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        switch(input.D8()){
            case 4, 5, 6 -> {
                Monstre monstre = Lieu.temple();
                System.out.println("Vous appercevez un(e) " + monstre.nom);
                if(input.yn("Voulez vous l'attaquer ?")){
                    return Combat.affrontement(nbj, -1, f_a, f_b, f_c, f_d, monstre);
                }
                else{
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 7, 8 -> {
                if(rand.nextBoolean()){
                    System.out.println("Vous récuperez " + rand.nextInt(2) + 1 + " équipement(s) I");
                }
                else{
                    System.out.println("Vous récuperez " + rand.nextInt(2) + " équipement(s) II");
                }
            }
            case 9 -> {
                if(rand.nextBoolean()){
                    System.out.println("Vous récuperez 1 équipement(s) I");
                }
                else{
                    System.out.println("Vous trouvez une Promotion");
                }
            }
            default -> { // 1, 2, 3
                Monstre monstre = Lieu.temple();
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(nbj, meneur, f_a, f_b, f_c, f_d, monstre);
            }
        }
        return 0;
    }

    static int expedition_mer(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        switch(input.D8()){
            case 5, 6 -> {
                Monstre monstre = Lieu.mer();
                System.out.println("Vous appercevez un(e) " + monstre.nom);
                if(input.yn("Voulez vous l'attaquer ?")){
                    return Combat.affrontement(nbj, -1, f_a, f_b, f_c, f_d, monstre);
                }
                else{
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 7, 8 -> {
                if(rand.nextBoolean()){
                    System.out.println("Vous récuperez " + rand.nextInt(3) + 1 + " équipement(s) I");
                }
                else{
                    System.out.println("Vous récuperez " + rand.nextInt(2) + 1 + " équipement(s) II");
                }
            }
            case 9 -> {
                if(rand.nextBoolean()){
                    System.out.println("Vous récuperez 1 équipement(s) II");
                }
                else{
                    System.out.println("Vous récuperez 1 équipement(s) III");
                }
            }
            default -> { // 1, 2, 3, 4
                Monstre monstre = Lieu.mer();
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(nbj, meneur, f_a, f_b, f_c, f_d, monstre);
            }
        }
        return 0;
    }

    static int expedition_mont(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        switch(input.D12()){
            case 7, 8, 9, 10, 11 -> {
                Monstre monstre = Lieu.mont();
                System.out.println("Vous appercevez un(e) " + monstre.nom);
                if(input.yn("Voulez vous l'attaquer ?")){
                    return Combat.affrontement(nbj, -1, f_a, f_b, f_c, f_d, monstre);
                }
                else{
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 12, 13 -> {
                if(rand.nextBoolean()){
                    System.out.println("Vous récuperez " + rand.nextInt(2) + 1 + " équipement(s) II");
                }
                else{
                    System.out.println("Vous récuperez " + rand.nextInt(1) + 1 + " équipement(s) III");
                }
            }
            default -> { // 1, 2, 3, 4, 5, 6
                Monstre monstre = Lieu.mont();
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(nbj, meneur, f_a, f_b, f_c, f_d, monstre);
            }
        }
        return 0;
    }

    static int expedition_olympe(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        switch(input.D20()){
            case 19, 20, 21 -> {
                Monstre monstre = Lieu.olympe();
                System.out.println("Vous appercevez un(e) " + monstre.nom);
                if(input.yn("Voulez vous l'attaquer ?")){
                    return Combat.affrontement(nbj, -1, f_a, f_b, f_c, f_d, monstre);
                }
                else{
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            default -> { // 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18
                Monstre monstre = Lieu.mont();
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(nbj, meneur, f_a, f_b, f_c, f_d, monstre);
            }
        }
        return 0;
    }
}
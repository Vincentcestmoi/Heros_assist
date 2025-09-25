import java.io.IOException;
import java.util.Random;


public class Main {

    static Input input = new Input();
    static Random rand = new Random();
    static String Path = "../Save/";
    static String Ext = ".txt";
    static public Position[] positions = {Position.PRAIRIE, Position.PRAIRIE, Position.PRAIRIE, Position.PRAIRIE};
    static public String Joueur_A = "Joueur A";
    static public String Joueur_B = "Joueur B";
    static public String Joueur_C = "Joueur C";
    static public String Joueur_D = "Joueur D";
    static public String necromancien, archimage, alchimiste, guerriere;
    public static int[] f;
    static final String[] nom = {Joueur_A, Joueur_B, Joueur_C, Joueur_D};
    static final int f_max = 7;
    static public int nbj;

    public static void main(String[] args) throws IOException {

        nbj = input.load();
        if (nbj == -1) {
            init_game();
        }
        
        boolean run = true;
        int i = 0;
        while (run) {
            if (i == nbj) {
                i = 0;
            }
            System.out.println(nom[i] + " c'est votre tour, vous êtes " + texte_pos(positions[i]) + ".");
            int temp = -1;
            switch (input.tour(positions[i], f[i], i)) {
                // action classique
                case EXPLORER -> temp = expedition(i);
                case MONTER -> temp = ascension(i);
                case DESCENDRE -> {
                    System.out.println(nom[i] + " retourne en des terres moins inhospitalières.");
                    positions[i] = reduire_pos(positions[i]);
                }
                case MARCHE -> marche(positions[i]);
                case DRESSER -> f[i] = gere_entrainement(f[i]);
                case ATTENDRE -> System.out.println(nom[i] + " passe son tour.");

                // action de classe
                case MEDITATION -> Sort.meditation();
                case NECROMANCIE -> {
                    if (Sort.necromancie(positions[i])) {
                        temp = i;
                    }
                }
                case FOUILLE -> Sort.fouille();
                case CONCOCTION -> Sort.concocter();

                // action caché
                case SUICIDE -> {
                    System.out.println(nom[i] + " est mort.");
                    positions[i] = Position.ENFERS;
                    f[i] = 0;
                }
                case QUITTER -> run = false;
                case FAMILIER_PLUS -> {
                    f[i] = new_fam(nom[i], f[i]);
                    i -= 1; //n'utilise pas le tour
                }
                case FAMILIER_MOINS -> {
                    f[i] = 0;
                    System.out.println("Le familier de " + nom[i] + " a bien été supprimé");
                    i -= 1; //n'utilise pas le tour
                }
                case RETOUR -> i = i == 0 ? nbj - 2 : i - 2;
                case ADD_GREAT_RUNE -> {
                    Sort.nb_great_fire_rune++;
                    System.out.println("Rune majeure de feu ajoutée.");
                    i--;
                }
                case DEL_GREAT_RUNE -> {
                    Sort.nb_great_fire_rune = Sort.nb_great_fire_rune <= 0 ? 0 : Sort.nb_great_fire_rune - 1;
                    System.out.println("Rune majeure de feu retirée.");
                    i--;
                }
                case ADD_MINOR_RUNE -> {
                    Sort.nb_fire_rune++;
                    System.out.println("Rune mineure de feu ajoutée.");
                    i--;
                }
                case DEL_MINOR_RUNE -> {
                    Sort.nb_fire_rune = Sort.nb_fire_rune <= 0 ? 0 : Sort.nb_fire_rune - 1;
                    System.out.println("Rune mineure de feu retirée.");
                    i--;
                }
            }
            // gestion familier
            if(temp != -1) {
                f[temp] = new_fam(Joueur_A, f[temp]);
            }
            for (int j = 0; j < nbj; j++) {
                Output.write_data(nom[j]);
            }
            i++;
        }
        System.out.println("Sauvegarde des données joueurs.");
        for (int j = 0; j < nbj; j++) {
            Output.write_data(nom[j]);
        }
        System.out.println("Fin du programme");
    }

    /**
     * Redirige vers le bon marché selon la position
     *
     * @param position la position du joueur
     */
    private static void marche(Position position) {
        switch (position) {
            case PRAIRIE -> Equipement.marche_prairie();
            case VIGNES -> Equipement.marche_vigne();
            case TEMPLE -> Equipement.marche_temple();
            case MER -> Equipement.marche_mer();
            case MONTS -> Equipement.marche_monts();
            case ENFERS, OLYMPE -> System.out.println("Erreur : Il n'y a pas de marché ici.");
            case ASCENDANT -> System.out.println("ERROR : DONOT");
        }
    }

    /**
     * Diminue d'un la position donné
     *
     * @param position la position à réduire
     * @return la position inférieure
     */
    private static Position reduire_pos(Position position) {
        return switch (position) {
            case VIGNES -> Position.PRAIRIE;
            case TEMPLE -> Position.VIGNES;
            case MER -> Position.TEMPLE;
            case MONTS -> Position.MER;
            case ASCENDANT -> {
                System.out.println("ERROR : DONOT");
                yield Position.ENFERS;
            }
            default -> { //ENFERS, PRAIRIES, OLYMPE
                System.out.println("Erreur : position " + position + " ne peut être descendue !");
                yield position;
            }
        };
    }

    /**
     * Augmente d'un la position donné
     *
     * @param position la position à augmenter
     * @return la position supérieure
     */
    private static Position augmenter_pos(Position position) {
        return switch (position) {
            case ENFERS -> Position.PRAIRIE;
            case PRAIRIE -> Position.VIGNES;
            case VIGNES -> Position.TEMPLE;
            case TEMPLE -> Position.MER;
            case MER -> Position.MONTS;
            case MONTS -> Position.OLYMPE;
            case OLYMPE -> {
                System.out.println("Erreur : position " + position + " ne peut être augmentée !");
                yield position;
            }
            case ASCENDANT -> {
                System.out.println("ERROR : DONOT");
                yield Position.ENFERS;
            }
        };
    }

    /**
     * Gère l'ascencion du joueur au lieu supérieur
     *
     * @param index l'indice du joueur qui fait l'ascension
     * @return l'indice du joueur qui gagne un nouveau familier
     */
    private static int ascension(int index) throws IOException {
        Position pos = positions[index];
        Position next_pos = augmenter_pos(pos);
        positions[index] = Position.ASCENDANT;  //on isole le joueur
        String text = nom[index];
        int lead = -1;
        Monstre m;
        switch (input.D4()) {
            case 1 -> {
                text += " rencontre un monstre vers la fin de son voyage.";
                m = true_monstre(next_pos);
            }
            case 2 -> {
                text += " est attaqué par un monstre à peine parti(e).";
                lead = index;
                m = true_monstre(pos);
            }
            case 3 -> {
                text += " rencontre un monstre à peine après le début de son voyage.";
                m = true_monstre(pos);
            }
            case 4, 5 -> {
                System.out.println(text + " parvient sans encombre " + texte_pos(next_pos) + ".\n");
                positions[index] = next_pos;
                return -1;
            }
            default -> {
                System.out.println("Erreur : résultat inatendu. Action annulée.\n");
                positions[index] = pos;
                return -1;
            }
        }
        System.out.println(text);
        int temp = Combat.affrontement(Position.ASCENDANT, lead, m);
        if (input.yn(nom[index] + " a-t-il vaincu le monstre ?")) {
            System.out.println(nom[index] + " arrive " + texte_pos(next_pos));
            positions[index] = next_pos;
        } else if (input.yn(nom[index] + " est-il mort ?")) {
            positions[index] = Position.ENFERS;
        } else {
            System.out.println(nom[index] + " est resté " + texte_pos(pos));
            positions[index] = pos;
        }
        System.out.println();
        return temp;
    }

    /**
     * Tente d'entrainer un familier
     *
     * @param f l'obéissance actuelle du familier
     * @return l'obéissance résultante du familier
     * @throws IOException pas de soucis
     */
    private static int gere_entrainement(int f) throws IOException {
        if (f == 0) {
            System.out.println("Erreur : aucun familier détecté.");
        } else {
            f += Monstre.entrainement();
            if (f <= 0) {
                System.out.println("Votre familier vous a fuit de manière définitive.");
                f = 0;
            } else if (f >= f_max) {
                System.out.println("Vous avez atteint le niveau maximal de loyauté de la part de votre familier.");
                f = f_max;
            }
        }
        System.out.println();
        return f;
    }

    /**
     * Gère le départ en expédition et ses conscéquences
     *
     * @param meneur le joueur principal
     * @return l'index + 1 d'un joueur qui obtient un nouveau familier (ou 0).
     * @throws IOException sans problème
     */
    static int expedition(int meneur) throws IOException {
        Position pos = positions[meneur];
        return switch (pos) {
            case ENFERS -> expedition_enfer(meneur);
            case PRAIRIE -> expedition_prairie(meneur);
            case VIGNES -> expedition_vigne(meneur);
            case TEMPLE -> expedition_temple(meneur);
            case MER -> expedition_mer(meneur);
            case MONTS -> expedition_mont(meneur);
            case OLYMPE -> expedition_olympe(meneur);
            case ASCENDANT -> {
                System.out.println("ERROR : DONOT");
                yield -1;
            }
        };
    }

    /**
     * Renvoie un texte du lieu
     *
     * @param p la position à donner
     * @return un String nommant le lieu (avec un déterminant)
     */
    static String texte_pos(Position p) {
        return switch (p) {
            case ENFERS -> "aux enfers";
            case PRAIRIE -> "dans la prairie";
            case VIGNES -> "dans la vigne";
            case TEMPLE -> "dans le temple d'Apollon";
            case MER -> "sur la mer";
            case MONTS -> "dans les montagnes";
            case OLYMPE -> "dans l'Olympe";
            case ASCENDANT -> "en plein déplacement (ERROR : DONOT)";
        };
    }

    static int expedition_enfer(int meneur) throws IOException {
        Monstre monstre = Lieu.enfers();
        switch (input.D4()) {
            case 1, 2, 3 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(Position.ENFERS, -1, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 4, 5 -> {
                if (rand.nextBoolean() || rand.nextBoolean()) {
                    Equipement.drop_0();
                } else {
                    System.out.println("Vous apercevez un(e) " + monstre.nom);
                    if (input.yn("Voulez vous l'attaquer ?")) {
                        return Combat.affrontement(Position.ENFERS, -1, monstre);
                    } else {
                        System.out.println("Vous vous éloignez discrètement");
                    }
                }
            }
            default -> {
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(Position.ENFERS, meneur, monstre);
            }
        }
        return -1;
    }

    static int expedition_prairie(int meneur) throws IOException {
        Monstre monstre = Lieu.prairie();
        switch (input.D6()) {
            case 2, 3, 4, 5 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(Position.PRAIRIE, -1, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 6, 7 -> {
                if (rand.nextBoolean()) {
                    Equipement.drop_1();
                }
                else if (rand.nextBoolean()) {
                    Equipement.drop_0();
                }
                else {
                    System.out.println("Vous apercevez un(e) " + monstre.nom);
                    if (input.yn("Voulez vous l'attaquer ?")) {
                        return Combat.affrontement(Position.PRAIRIE, -1, monstre);
                    } else {
                        System.out.println("Vous vous éloignez discrètement");
                    }
                }
            }
            default -> { // 1
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(Position.PRAIRIE, meneur, monstre);
            }
        }
        return -1;
    }

    static int expedition_vigne(int meneur) throws IOException {
        Monstre monstre = Lieu.vigne();
        switch (input.D6()) {
            case 3, 4, 5 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(Position.VIGNES, -1, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 6 -> {
                if (rand.nextBoolean()) {
                    int t = rand.nextInt(2) + 1;
                    for (int i = 0; i <= t; i++) {
                        Equipement.drop_1();
                    }
                }
                else if (rand.nextBoolean()) {
                    Equipement.drop_0();
                }
                else {
                    System.out.println("Vous apercevez un(e) " + monstre.nom);
                    if (input.yn("Voulez vous l'attaquer ?")) {
                        return Combat.affrontement(Position.VIGNES, -1, monstre);
                    } else {
                        System.out.println("Vous vous éloignez discrètement");
                    }
                }
            }
            case 7 -> {
                if (rand.nextBoolean()) {
                    Equipement.drop_1();
                } else {
                    Equipement.drop_promo();
                }
            }
            default -> { // 1, 2
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(Position.VIGNES, meneur, monstre);
            }
        }
        return -1;
    }

    static int expedition_temple(int meneur) throws IOException {
        Monstre monstre = Lieu.temple();
        switch (input.D8()) {
            case 4, 5, 6, 7 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(Position.TEMPLE, -1, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 8 -> {
                if (rand.nextBoolean()) {
                    int t = rand.nextInt(2) + 1;
                    for (int i = 0; i <= t; i++) {
                        Equipement.drop_1();
                    }
                } else {
                    int t = rand.nextInt(2) + 1;
                    for (int i = 0; i <= t; i++) {
                        Equipement.drop_2();
                    }
                }
            }
            case 9 -> {
                if (rand.nextBoolean() || rand.nextBoolean()) {
                    Equipement.drop_1();
                } else {
                    Equipement.drop_promo();
                }
            }
            default -> { // 1, 2, 3
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(Position.TEMPLE, meneur, monstre);
            }
        }
        return -1;
    }

    static int expedition_mer(int meneur) throws IOException {
        Monstre monstre = Lieu.mer();
        switch (input.D8()) {
            case 5, 6, 7 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(Position.MER, -1, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 8 -> {
                if (rand.nextBoolean()) {
                    int t = rand.nextInt(3) + 1;
                    for (int i = 0; i <= t; i++) {
                        Equipement.drop_1();
                    }
                } else {
                    int t = rand.nextInt(2) + 1;
                    for (int i = 0; i <= t; i++) {
                        Equipement.drop_2();
                    }
                }
            }
            case 9 -> {
                if (rand.nextBoolean()) {
                    Equipement.drop_2();
                } else {
                    Equipement.drop_3();
                }
            }
            default -> { // 1, 2, 3, 4
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(Position.MER, meneur, monstre);
            }
        }
        return -1;
    }

    static int expedition_mont(int meneur) throws IOException {
        Monstre monstre = Lieu.mont();
        switch (input.D12()) {
            case 7, 8, 9, 10, 11 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(Position.MONTS, -1, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 12, 13 -> {
                if (rand.nextBoolean()) {
                    int t = rand.nextInt(3) + 1;
                    for (int i = 0; i <= t; i++) {
                        Equipement.drop_2();
                    }
                } else {
                    int t = rand.nextInt(2) + 1;
                    for (int i = 0; i <= t; i++) {
                        Equipement.drop_3();
                    }
                }
            }
            default -> { // 1, 2, 3, 4, 5, 6
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(Position.MONTS, meneur, monstre);
            }
        }
        return -1;
    }

    static int expedition_olympe(int meneur) throws IOException {
        Monstre monstre = Lieu.olympe();
        switch (input.D20()) {
            case 19, 20, 21 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(Position.OLYMPE, -1, monstre);
                } else if (rand.nextBoolean()) {
                    System.out.println("Vous vous éloignez discrètement");
                } else {
                    System.out.println(monstre.nom + " vous remarque et vous fonce dessus !");
                    return Combat.affrontement(Position.OLYMPE, -1, monstre);
                }

            }
            default -> { // 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(Position.OLYMPE, meneur, monstre);
            }
        }
        return -1;
    }

    /**
     * Traite la venue d'un nouveau familier, traite le cas de double familier
     *
     * @param nom le nom du joueur qui obtient ce familier
     * @param obe l'obéissance du familier actuel
     * @return l'obéissance du familier conservé
     * @throws IOException mon vieux pote en input
     */
    static private int new_fam(String nom, int obe) throws IOException {
        if (obe != 0) {
            if (input.yn(nom + " possède déjà un familier, le remplacer ? ")) {
                obe = 1;
                System.out.println("Nouveau familier enregistré.\n");
            } else {
                System.out.println("Ancien familier conservé.\n");
            }
        } else {
            obe = 1;
            System.out.println(nom + " a un nouveau familier.\n");
        }
        return obe;
    }

    private static Monstre true_monstre(Position pos) {
        return switch (pos) {
            case ENFERS -> Lieu.true_enfers();
            case PRAIRIE -> Lieu.true_prairie();
            case VIGNES -> Lieu.true_vigne();
            case TEMPLE -> Lieu.true_temple();
            case MER -> Lieu.true_mer();
            case MONTS -> Lieu.true_mont();
            case OLYMPE -> Lieu.olympe();
            case ASCENDANT -> {
                System.out.println("ERROR : DONOT");
                yield Lieu.true_enfers();
            }
        };
    }

    private static void init_game() {
        Main.f = new int[]{0, 0, 0, 0};
        
    }
}

import java.io.IOException;
import java.util.Random;


public class Main {

    static Input input = new Input();
    static Random rand = new Random();
    static String Path = "../Save/";
    static String Ext = ".txt";
    static Position[] positions = {Position.PRAIRIE, Position.PRAIRIE, Position.PRAIRIE, Position.PRAIRIE};
    static final String Joueur_A = "Micky";
    static final String Joueur_B = "Vincent";
    static final String Joueur_C = "Lucien";
    static final String Joueur_D = "Aloyse";
    static final String necromancien = Joueur_A;
    static final String archimage = Joueur_C;
    static final String alchimiste = Joueur_B;
    static final String guerriere = Joueur_D;
    public static int f_a = 0, f_b = 0, f_c = 0, f_d = 0;
    static final int f_max = 7;
    static final String[] nom = {Joueur_A, Joueur_B, Joueur_C, Joueur_D};

    @SuppressWarnings({"ConstantValue", "DataFlowIssue"})
    public static void main(String[] args) throws IOException {

        int nbj = input.load();
        if(nbj == -1) {
            System.out.print("Entrez le nombre de joueur : ");
            nbj = input.readInt();
        }
        if (nbj < 1 || nbj > 4) {
            System.out.println("Nombre de joueur invalide : 1 à 4 joueurs seulement.");
            return;
        }

        // rappel des classes
        if(!necromancien.isEmpty()) {
            switch(necromancien){
                case Joueur_A -> System.out.println(necromancien + " est nécromancien.\n");
                case Joueur_B -> {
                    if(nbj >= 2){
                        System.out.println(necromancien + " est nécromancien.\n");
                    }
                }
                case Joueur_C -> {
                    if(nbj >= 3){
                        System.out.println(necromancien + " est nécromancien.\n");
                    }
                }
                case Joueur_D -> {
                    if(nbj == 4){
                        System.out.println(necromancien + " est nécromancien.\n");
                    }
                }
            }
        }
        if(!archimage.isEmpty()) {
            switch(archimage){
                case Joueur_A -> System.out.println(archimage + " est archimage.\n");
                case Joueur_B -> {
                    if(nbj >= 2) {
                        System.out.println(archimage + " est archimage.\n");
                    }
                }
                case Joueur_C -> {
                    if(nbj >= 3) {
                        System.out.println(archimage + " est archimage.\n");
                    }
                }
                case Joueur_D -> {
                    if(nbj == 4) {
                        System.out.println(archimage + " est archimage.\n");
                    }
                }
            }
        }
        if(!alchimiste.isEmpty()) {
            switch (alchimiste) {
                case Joueur_A -> System.out.println(alchimiste + " est alchimiste.\n");
                case Joueur_B -> {
                    if (nbj >= 2) {
                        System.out.println(alchimiste + " est alchimiste.\n");
                    }
                }
                case Joueur_C -> {
                    if (nbj >= 3) {
                        System.out.println(alchimiste + " est alchimiste.\n");
                    }
                }
                case Joueur_D -> {
                    if (nbj == 4) {
                        System.out.println(alchimiste + " est alchimiste.\n");
                    }
                }
            }
        }
        if(!guerriere.isEmpty()) {
            switch (guerriere) {
                case Joueur_A -> System.out.println(guerriere + " est guerrière.\n");
                case Joueur_B -> {
                    if (nbj >= 2) {
                        System.out.println(guerriere + " est guerrière.\n");
                    }
                }
                case Joueur_C -> {
                    if (nbj >= 3) {
                        System.out.println(guerriere + " est guerrière.\n");
                    }
                }
                case Joueur_D -> {
                    if (nbj == 4) {
                        System.out.println(guerriere + " est guerrière.\n");
                    }
                }
            }
        }

        boolean run = true;
        int i = 0;
        while (run) {
            if (i == nbj) {
                i = 0;
            }
            System.out.println(nom[i] + " c'est votre tour, vous êtes " + texte_pos(positions[i]) + ".");
            int temp = -1;
            switch (input.tour(positions[i], new int[]{f_a, f_b, f_c, f_d}[i], i)) {
                // action classique
                case EXPLORER -> temp = expedition(nbj, i, f_a, f_b, f_c, f_d);
                case MONTER -> temp = ascension(i);
                case DESCENDRE -> {
                    System.out.println(nom[i] + " retourne en des terres moins inhospitalières.");
                    positions[i] = reduire_pos(positions[i]);
                }
                case MARCHE -> marche(positions[i]);
                case DRESSER -> {
                    switch (nom[i]) {
                        case Joueur_A -> f_a = gere_entrainement(f_a);
                        case Joueur_B -> f_b = gere_entrainement(f_b);
                        case Joueur_C -> f_c = gere_entrainement(f_c);
                        case Joueur_D -> f_d = gere_entrainement(f_d);
                        default -> System.out.println("Erreur : joueur " + nom[i] + " non reconnu");
                    }
                }
                case ATTENDRE -> System.out.println(nom[i] + " passe son tour.");

                // action de classe
                case MEDITATION -> Sort.meditation();
                case NECROMANCIE -> {
                    if(Sort.necromancie(positions[i])){
                        temp = i;
                    }
                }
                case FOUILLE -> Sort.fouille();

                // action caché
                case SUICIDE -> {
                    System.out.println(nom[i] + " est mort.");
                    positions[i] = Position.ENFERS;
                    switch (i){
                        case 0 -> f_a = 0;
                        case 1 -> f_b = 0;
                        case 2 -> f_c = 0;
                        case 3 -> f_d = 0;
                    }
                }
                case QUITTER -> run = false;
                case FAMILIER_PLUS -> {
                    switch (nom[i]) {
                        case Joueur_A -> f_a = new_fam(Joueur_A, f_a);
                        case Joueur_B -> f_b = new_fam(Joueur_B, f_b);
                        case Joueur_C -> f_c = new_fam(Joueur_C, f_c);
                        case Joueur_D -> f_d = new_fam(Joueur_D, f_d);
                        default -> System.out.println("Erreur : joueur " + nom[i] + " non reconnu");
                    }
                    i -= 1; //n'utilise pas le tour
                }
                case FAMILIER_MOINS -> {
                    switch (nom[i]) {
                        case Joueur_A -> {
                            f_a = 0;
                            System.out.println("Le familier de " + Main.Joueur_A + " a bien été supprimé");
                        }
                        case Joueur_B -> {
                            f_b = 0;
                            System.out.println("Le familier de " + Main.Joueur_B + " a bien été supprimé");
                        }
                        case Joueur_C -> {
                            f_c = 0;
                            System.out.println("Le familier de " + Main.Joueur_C + " a bien été supprimé");
                        }
                        case Joueur_D -> {
                            f_d = 0;
                            System.out.println("Le familier du Joueur D a bien été supprimé");
                        }
                        default -> System.out.println("Erreur : joueur " + nom[i] + " non reconnu");
                    }
                    i -= 1; //n'utilise pas le tour
                }
                case RETOUR -> i = i == 0 ? nbj - 2 : i - 2;
            }
            switch (temp) {
                case 0 -> f_a = new_fam(Joueur_A, f_a);
                case 1 -> f_b = new_fam(Joueur_B, f_b);
                case 2 -> f_c = new_fam(Joueur_C, f_c);
                case 3 -> f_d = new_fam(Joueur_D, f_d);
                default -> { // dont -1
                }
            }
            for(int j = 0; j < nbj; j++) {
                Output.write_data(nom[j]);
            }
            i++;
        }
        for(int j = 0; j < nbj; j++) {
            Output.write_data(nom[j]);
        }
        System.out.println("Fin du programme");
    }

    /**
     * Redirige vers le bon marché selon la position
     * @param position la position du joueur
     */
    private static void marche(Position position) {
        switch (position){
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
        int temp = Combat.affrontement(4, Position.ASCENDANT, lead, f_a, f_b, f_c, f_d, m);
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
     * @return l'obéissance résultnte du familier
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
     * @param nbj    le nombre de joueurs actifs
     * @param meneur le joueur principal
     * @param f_a    l'obéissance du familier du joueur A (0 s'il n'en a pas).
     * @param f_b    l'obéissance du familier du joueur B (0 s'il n'en a pas).
     * @param f_c    l'obéissance du familier du joueur C (0 s'il n'en a pas).
     * @param f_d    l'obéissance du familier du joueur D (0 s'il n'en a pas).
     * @return l'index + 1 d'un joueur qui obtient un nouveau familier (ou 0).
     * @throws IOException sans problème
     */
    static int expedition(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        Position pos = positions[meneur];
        return switch (pos) {
            case ENFERS -> expedition_enfer(nbj, meneur, f_a, f_b, f_c, f_d);
            case PRAIRIE -> expedition_prairie(nbj, meneur, f_a, f_b, f_c, f_d);
            case VIGNES -> expedition_vigne(nbj, meneur, f_a, f_b, f_c, f_d);
            case TEMPLE -> expedition_temple(nbj, meneur, f_a, f_b, f_c, f_d);
            case MER -> expedition_mer(nbj, meneur, f_a, f_b, f_c, f_d);
            case MONTS -> expedition_mont(nbj, meneur, f_a, f_b, f_c, f_d);
            case OLYMPE -> expedition_olympe(nbj, meneur, f_a, f_b, f_c, f_d);
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

    static int expedition_enfer(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        Monstre monstre = Lieu.enfers();
        switch (input.D4()) {
            case 1, 2, 3 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(nbj, Position.ENFERS, -1, f_a, f_b, f_c, f_d, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 4, 5 -> {
                if (rand.nextBoolean()) {
                    Equipement.drop_0();
                } else {
                    System.out.println("Vous ne trouvez rien ni personne");
                }
            }
            default -> {
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(nbj, Position.ENFERS, meneur, f_a, f_b, f_c, f_d, monstre);
            }
        }
        return -1;
    }

    static int expedition_prairie(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        Monstre monstre = Lieu.prairie();
        switch (input.D6()) {
            case 2, 3, 4, 5 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(nbj, Position.PRAIRIE, -1, f_a, f_b, f_c, f_d, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 6, 7 -> {
                if (rand.nextBoolean()) {
                    Equipement.drop_0();
                } else {
                    System.out.println("Vous ne trouvez rien ni personne");
                }
            }
            default -> { // 1
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(nbj, Position.PRAIRIE, meneur, f_a, f_b, f_c, f_d, monstre);
            }
        }
        return -1;
    }

    static int expedition_vigne(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        Monstre monstre = Lieu.vigne();
        switch (input.D6()) {
            case 3, 4, 5 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(nbj, Position.VIGNES, -1, f_a, f_b, f_c, f_d, monstre);
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
                } else {
                    System.out.println("Vous ne trouvez rien ni personne");
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
                return Combat.affrontement(nbj, Position.VIGNES, meneur, f_a, f_b, f_c, f_d, monstre);
            }
        }
        return -1;
    }

    static int expedition_temple(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        Monstre monstre = Lieu.temple();
        switch (input.D8()) {
            case 4, 5, 6, 7 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(nbj, Position.TEMPLE, -1, f_a, f_b, f_c, f_d, monstre);
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
                if (rand.nextBoolean()) {
                    Equipement.drop_1();
                } else {
                    Equipement.drop_promo();
                }
            }
            default -> { // 1, 2, 3
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(nbj, Position.TEMPLE, meneur, f_a, f_b, f_c, f_d, monstre);
            }
        }
        return -1;
    }

    static int expedition_mer(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        Monstre monstre = Lieu.mer();
        switch (input.D8()) {
            case 5, 6, 7 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(nbj, Position.MER, -1, f_a, f_b, f_c, f_d, monstre);
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
                return Combat.affrontement(nbj, Position.MER, meneur, f_a, f_b, f_c, f_d, monstre);
            }
        }
        return -1;
    }

    static int expedition_mont(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        Monstre monstre = Lieu.mont();
        switch (input.D12()) {
            case 7, 8, 9, 10, 11 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(nbj, Position.MONTS, -1, f_a, f_b, f_c, f_d, monstre);
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
                return Combat.affrontement(nbj, Position.MONTS, meneur, f_a, f_b, f_c, f_d, monstre);
            }
        }
        return -1;
    }

    static int expedition_olympe(int nbj, int meneur, int f_a, int f_b, int f_c, int f_d) throws IOException {
        Monstre monstre = Lieu.olympe();
        switch (input.D20()) {
            case 19, 20, 21 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(nbj, Position.OLYMPE, -1, f_a, f_b, f_c, f_d, monstre);
                } else if (rand.nextBoolean()) {
                    System.out.println("Vous vous éloignez discrètement");
                } else {
                    System.out.println(monstre.nom + " vous remarque et vous fonce dessus !");
                    return Combat.affrontement(nbj, Position.OLYMPE, -1, f_a, f_b, f_c, f_d, monstre);
                }

            }
            default -> { // 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18
                System.out.println(monstre.nom + " vous attaque");
                return Combat.affrontement(nbj, Position.OLYMPE, meneur, f_a, f_b, f_c, f_d, monstre);
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
}
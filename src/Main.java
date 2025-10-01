import java.io.IOException;
import java.util.Random;


public class Main {

    static final Input input = new Input();
    static final Random rand = new Random();
    static final String Path = "../Save/";
    static final String Ext = ".txt";
    static final int f_max = 7;
    static final int nbj_max = 8;

    static public String[] nom;
    static public Position[] positions;
    static public Metier[] metier;
    public static int[] f;
    static public int nbj = 0;

    public static void main(String[] args) throws IOException {

        input.load();
        if (nbj == 0) {
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
                    new_fam(i);
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
            new_fam(temp);
            for (int j = 0; j < nbj; j++) {
                Output.write_data(j);
            }
            i++;
        }
        System.out.println("Sauvegarde des données joueurs.");
        for (int j = 0; j < nbj; j++) {
            Output.write_data(j);
        }
        Output.delete_fichier("Joueur "+ (char)('A' + nbj));
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
        int jet = input.D4();
        if (Main.metier[meneur] == Metier.RANGER){
            jet += rand.nextInt(3);
        }
        if(jet > 5){
            jet = 5;
        }
        switch (jet) {
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
        int jet = input.D6();
        if (Main.metier[meneur] == Metier.RANGER){
            jet += rand.nextInt(3);
        }
        if(jet > 7){
            jet = 7;
        }
        switch (jet) {
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
        int jet = input.D6();
        if (Main.metier[meneur] == Metier.RANGER){
            jet += rand.nextInt(3);
        }
        if(jet > 7){
            jet = 7;
        }
        switch (jet) {
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
        int jet = input.D8();
        if (Main.metier[meneur] == Metier.RANGER){
            jet += rand.nextInt(3);
        }
        if(jet > 9){
            jet = 9;
        }
        switch (jet) {
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
        int jet = input.D8();
        if (Main.metier[meneur] == Metier.RANGER){
            jet += rand.nextInt(3);
        }
        if(jet > 9){
            jet = 9;
        }
        switch (jet) {
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
        int jet = input.D12();
        if (Main.metier[meneur] == Metier.RANGER){
            jet += rand.nextInt(3);
        }
        if(jet > 13){
            jet = 13;
        }
        switch (jet) {
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
        int jet = input.D20();
        if (Main.metier[meneur] == Metier.RANGER){
            jet += rand.nextInt(3);
        }
        if(jet > 21){
            jet = 21;
        }
        switch (jet) {
            case 19, 20, 21 -> {
                System.out.println("Vous apercevez un(e) " + monstre.nom);
                if (input.yn("Voulez vous l'attaquer ?")) {
                    return Combat.affrontement(Position.OLYMPE, -1, monstre);
                } else if (rand.nextBoolean() || (jet > 20 && rand.nextBoolean())) {
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
     * @param  index l'index du joueur qui gagne un familier, ou -1 s'il faut ognorer cette fonction
     * @throws IOException mon vieux pote en input
     */
    static private void new_fam(int index) throws IOException {
        if(index == -1){
            return;
        }
        if (f[index] != 0) {
            if (input.yn(nom[index] + " possède déjà un familier, le remplacer ? ")) {
                System.out.println("Nouveau familier enregistré.\n");
                f[index] = 1;
            } else {
                System.out.println("Ancien familier conservé.\n");
            }
        } else {
            System.out.println(nom[index] + " a un nouveau familier.\n");
            f[index] = 1;
        }
    }

    /**
     * Renvoie un monstre de la position demandé en supprimant les chances qu'il vienne d'une position voisine
     * @param pos la position dont on veut le monstre
     * @return un Monstre
     */
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

    /**
     * Initialise les paramètres d'une partie à la place d'une sauvegarde
     * @throws IOException toujours
     */
    private static void init_game() throws IOException {
        System.out.print("Entrez le nombre de joueur :");
        Main.nbj = input.readInt();
        while(Main.nbj <= 0 || Main.nbj > Main.nbj_max) {
            System.out.println("Impossible, le nombre de joueur doit être comprit entre 1 et " + Main.nbj_max +".");
            Main.nbj = input.readInt();
        }
        Main.nom = new String[Main.nbj];
        Main.positions = new Position[Main.nbj];
        Main.f = new int[Main.nbj];
        Main.metier = new Metier[Main.nbj];
        String[] job = {"(ne)cromancien", "archi(ma)ge", "(al)chimiste", "(gu)erriere", "(ra)nger", "(sh)aman", "(au)cun"};
        for(int i = 0; i < Main.nbj; i++) {
            String temp;
            do{
                System.out.println("Joueur " + (i + 1) + ", entrez votre nom :");
                temp = input.read();
                if(temp.contains(",") || temp.contains(";")){
                    System.out.println("Le nom du joueur ne peut pas contenir les caractères ',' et ';' !");
                    temp = ";";
                }
                if(temp.isEmpty() || temp.equals("\n")){
                    temp = ";";
                }
            }while(temp.equals(";") || !input.yn("Voulez vous confirmer le pseudo " + temp + " ?"));
            Main.nom[i] = temp;
            Main.f[i] = 0;
            Main.positions[i] = Position.PRAIRIE;
            boolean run = true;
            while(run) {
                run = false;
                System.out.println(Main.nom[i] + ", choississez votre profession : ");
                for (String s : job) {
                    System.out.println(s + " ");
                }
                switch (input.read()) {
                    case "ne", "NE", "Ne", "nE" -> {
                        Main.metier[i] = Metier.NECROMANCIEN;
                        System.out.println("Base : Résistance : 4 ; attaque : 1 ; PP: 6/8");
                        System.out.println("Caractéristiques : Thaumaturge, Rite sacrificiel");
                        System.out.println("Pouvoir : Appel des morts, Résurrection, Zombification, Malédiction");
                    }
                    case "ma", "MA", "Ma", "mA" -> {
                        Main.metier[i] = Metier.ARCHIMAGE;
                        System.out.println("Base : Résistance : 4 ; attaque : 0 ; PP: 10/10");
                        System.out.println("Caractéristiques : Addiction au mana, Maitre de l'énergie, Double incantateur, Manchot");
                        System.out.println("Pouvoir : Méditation, Boule de feu, Armure de glace, Foudre, Onde de choc, Purge");
                    }
                    case "ra", "RA", "rA", "Ra" -> {
                        Main.metier[i] = Metier.RANGER;
                        System.out.println("Base : Résistance : 4 ; attaque : 2 ; PP: 4/4 ; tir : 3");
                        System.out.println("Caractéristiques : Explorateur, Eclaireur");
                        System.out.println("Pouvoir : Assassinat, Coup critique, Assaut");
                    }
                    case "al", "AL", "Al", "aL" -> {
                        Main.metier[i] = Metier.ALCHIMISTE;
                        System.out.println("Base : Résistance : 5 ; attaque : 1 ; PP: 0/0 ; ingrédient : 3/11");
                        System.out.println("Caractéristiques : Dextérité");
                        System.out.println("Pouvoir : Fouille, Dissection, Concoction");
                    }
                    case "gu", "GU", "Gu", "gU" -> {
                        Main.metier[i] = Metier.GUERRIERE;
                        System.out.println("Base : Résistance : 6 ; attaque : 3 ; PP: 1/5");
                        System.out.println("Caractéristiques : Invincible");
                        System.out.println("Pouvoir : Berserk, Lame d'aura");
                    }
                    case "sh", "SH", "Sh", "sH" -> {
                        Main.metier[i] = Metier.SHAMAN;
                        System.out.println("Base : Résistance : 4 ; attaque : 1 ; PP: 0/0");
                        System.out.println("Caractéristiques : Ancien esprit");
                        System.out.println("Pouvoir : Lien, Incantation, Paix intérieure");
                    }
                    case "au", "AU", "Au", "aU" -> {
                        Main.metier[i] = Metier.AUCUN;
                        System.out.println("Base : Résistance : 5 ; attaque : 1 ; PP: 6/6");
                        System.out.println("Caractéristiques : Aucune");
                        System.out.println("Pouvoir : Aucun");
                    }
                    default -> {
                        System.out.println("Unknow input");
                        run = true;
                    }
                }
            }
            System.out.println(Main.nom[i] + " est " + Output.texte_metier(Main.metier[i]) + ".");
            System.out.println(Main.nom[i] + " apparait dans la prairie.\n");
        }
    }
}

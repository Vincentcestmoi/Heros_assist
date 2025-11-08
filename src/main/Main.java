package main;

import Auxiliaire.Utilitaire;
import Enum.Choix;
import Enum.Dieux;
import Enum.Metier;
import Enum.Position;
import Equipement.Equipement;
import Exterieur.Input;
import Exterieur.Output;
import Exterieur.SaveManager;
import Metiers.Joueur;
import Monstre.Lieu;
import Monstre.Monstre;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.max;


public class Main {
    
    static final Random rand = new Random();
    static final int nbj_max = 8;
    
    static public int nbj;
    public static int Path = -1; //-1 = vide
    static public Joueur[] joueurs;
    
    public static class Version {
        //public static final String CURRENT_MAIN = "beta";
        public static final int CURRENT_MID = 0;
        public static final int CURRENT_FIX = 3;
        //public static final String CURRENT = "V%s.%d.%d".formatted(CURRENT_MAIN, CURRENT_MID, CURRENT_FIX);
        public static final String CURRENT = "%d.%d-beta".formatted(CURRENT_MID, CURRENT_FIX);
    }
    
    public static void main(String[] args) throws IOException {
        
        //pour les tests
        if (args.length == 1 && args[0].equals("--test")) {
            return;
        }
        
        if (args.length == 1 && (args[0].equals("--version") || args[0].equals("-v"))) {
            System.out.println("=== Heros_assist " + Version.CURRENT + " ===");
            return;
        }
        
        if (!Input.load()) {
            init_game();
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            String message = "\nSauvegarde des données joueurs.\n";
            String fin = "Fin du programme.\n";
            
            try {
                // Console
                System.out.print(message);
                System.out.flush();
                
                SaveManager.sauvegarder(false);
                
                System.out.print(fin);
                System.out.flush();
                
                // Log fichier
                try (FileWriter fw = new FileWriter("shutdown.log", true)) {
                    fw.write(message);
                    fw.write("Sauvegarde effectuée à " + java.time.LocalDateTime.now() + "\n");
                    fw.write(fin);
                }
                
            } catch (Exception e) {
                System.err.println("⚠️ Erreur lors de la sauvegarde : " + e.getMessage());
                System.err.flush();
                try (FileWriter fw = new FileWriter("shutdown.log", true)) {
                    fw.write("Erreur lors de la sauvegarde : " + e.getMessage() + "\n");
                } catch (Exception ignored) {
                }
            }
        }));
        
        
        boolean run = true;
        Utilitaire.LoopGuard MainGarde = new Utilitaire.LoopGuard(1_000_000, 5 * 60 * 1000); //save toutes les 5 minutes
        while (run) {
            
            //tour de jeu
            for (int i = 0; i < nbj && run; i++) {
                MainGarde.checkMain();
                Joueur joueur = joueurs[i];
                System.out.println(joueur.getNom() + " c'est votre tour, vous êtes " + texte_pos(joueur.getPosition()) + ".");
                Choix choix = Input.tour(i);
                switch (choix) {
                    // action classique
                    case EXPLORER -> expedition(i);
                    case MONTER -> joueur.ascend();
                    case DESCENDRE -> {
                        System.out.println(joueur.getNom() + " retourne en des terres moins inhospitalières.");
                        joueur.descendre();
                        joueur.check_bonus_lieux();
                    }
                    case MARCHE -> joueur.aller_au_marche();
                    case DUMMY -> frapper_pantin(i);
                    case DRESSER -> joueur.dresser();
                    case ATTENDRE -> System.out.println(joueur.getNom() + " passe son tour.");
                    case STAT -> {
                        joueur.presente_detail();
                        i--;
                    }
                    
                    // action caché
                    case SUICIDE -> joueur.mort_def();
                    case QUITTER -> run = false;
                    case FAMILIER_PLUS -> {
                        joueur.ajouter_familier();
                        i -= 1; //n'utilise pas le tour
                    }
                    case FAMILIER_MOINS -> {
                        joueur.perdre_familier();
                        i -= 1;
                    }
                    case ITEM_PLUS -> {
                        ajouter_item(choisir_joueur());
                        i -= 1;
                    }
                    case ITEM_MOINS -> {
                        retirer_item(choisir_joueur());
                        i -= 1;
                    }
                    case RETOUR -> i = i == 0 ? nbj - 2 : i - 2;
                }
                System.out.println();
            }
        }
    }
    
    private static Joueur choisir_joueur() {
        System.out.println("Indiquez le joueur a affecter.");
        for (int i = 0; i < nbj; i++) {
            System.out.printf("\n%d : %s", i, Main.joueurs[i].getNom());
        }
        int j;
        do {
            j = Input.readInt();
            if (j < 0 || j >= nbj) {
                System.out.println("Entrée invalide.");
            }
        } while (j >= nbj || j < 0);
        return Main.joueurs[j];
    }
    
    private static void ajouter_item(Joueur joueur) {
        System.out.println("Indiquez le numéro de l'item (format #xx). Écrivez #00 ou q pour quitter.");
        String temp = Input.read();
        if (temp.charAt(0) != '#') {
            System.out.println("Format invalide, l'identifiant doit commencer par #");
        }
        if (temp.length() != 3) {
            System.out.println("Format invalide, l'identifiant doit être de la forme #xx");
        }
        switch (temp) {
            case "#00", "q" -> {
            }
            case "#01" -> joueur.add_lame_infernale();
            case "#02" -> joueur.add_lame_vegetale();
            case "#03" -> joueur.add_trident();
            case "#04" -> joueur.add_lame_mont();
            case "#05" -> joueur.add_nectar();
            case "#06" -> joueur.add_ambroisie();
            case "#07" -> joueur.add_guerre();
            case "#08" -> joueur.add_lame_vent();
            case "#09" -> joueur.add_lame_fertile();
            case "#10" -> joueur.add_parch_feu();
            case "#11" -> joueur.add_parch_dodo();
            case "#12" -> joueur.add_parch_lumiere();
            case "#13" -> joueur.add_rune_croissance();
            case "#14" -> joueur.add_rune_pluie();
            case "#15" -> joueur.add_rune_haine();
            case "#16" -> joueur.add_rune_virale();
            case "#17" -> joueur.add_rune_ardente();
            case "#18" -> joueur.add_rune_ardente2();
            case "#19" -> joueur.add_rune_dodo();
            case "#20" -> joueur.add_rune_mortifere();
            case "#21" -> joueur.add_rune_orage();
            case "#22" -> joueur.add_rune_commerce();
            case "#23" -> joueur.add_soin();
            case "#24" -> joueur.add_bracelet_protec();
            case "#25" -> joueur.add_rune_noire();
            case "#26" -> joueur.add_absorption();
            case "#27" -> joueur.add_lunette();
            case "#28" -> joueur.add_dissec();
            case "#29" -> joueur.add_concoc();
            case "#30" -> joueur.add_bourdon();
            case "#31" -> joueur.add_parch_volcan();
            case "#32" -> joueur.add_absorption2();
            case "#33" -> joueur.add_pegase();
            case "#34" -> joueur.add_cheval();
            case "#35" -> joueur.add_pie();
            case "#36" -> joueur.add_sphinx();
            case "#37" -> joueur.add_fee();
            case "#38" -> joueur.add_rune_arca();
            case "#39" -> joueur.add_antidote();
            case "#40" -> joueur.add_rune_annihilation();
            case "#41" -> joueur.add_tatouage_resurection();
            case "#42" -> joueur.add_fuite();
            case "#43" -> joueur.add_grenade();
            case "#44" -> joueur.add_bateau();
            default -> {
                System.out.println("Input unknown.");
                ajouter_item(joueur);
            }
        }
    }
    
    private static void retirer_item(Joueur joueur) {
        //noinspection DuplicatedCode
        System.out.println("Indiquez le numéro de l'item (format #xx). Écrivez #00 ou q pour quitter, #99 pour " +
                "retirer " + "tous vos items spéciaux.");
        String temp = Input.read();
        if (temp.charAt(0) != '#') {
            System.out.println("Format invalide, l'identifiant doit commencer par #");
        }
        if (temp.length() != 3) {
            System.out.println("Format invalide, l'identifiant doit être de la forme #xx");
        }
        switch (temp) {
            case "#00", "q" -> {
            }
            case "#01" -> joueur.retire_lame_infernale();
            case "#02" -> joueur.retire_lame_vegetale();
            case "#03" -> joueur.retire_trident();
            case "#04" -> joueur.retire_lame_mont();
            case "#05" -> joueur.retire_nectar();
            case "#06" -> joueur.retire_ambroisie();
            case "#07" -> joueur.retire_guerre();
            case "#08" -> joueur.retire_lame_vent();
            case "#09" -> joueur.retire_lame_fertile();
            case "#10" -> joueur.retire_parch_feu();
            case "#11" -> joueur.retire_parch_dodo();
            case "#12" -> joueur.retire_parch_lumiere();
            case "#13" -> joueur.retire_rune_croissance();
            case "#14" -> joueur.retire_rune_pluie();
            case "#15" -> joueur.retire_rune_haine();
            case "#16" -> joueur.retire_rune_virale();
            case "#17" -> joueur.retire_rune_ardente();
            case "#18" -> joueur.retire_rune_ardente2();
            case "#19" -> joueur.retire_rune_dodo();
            case "#20" -> joueur.retire_rune_mortifere();
            case "#21" -> joueur.retire_rune_orage();
            case "#22" -> joueur.retire_rune_commerce();
            case "#23" -> joueur.retire_soin();
            case "#24" -> joueur.retire_bracelet_protec();
            case "#25" -> joueur.retire_rune_noire();
            case "#26" -> joueur.retire_absorption();
            case "#27" -> joueur.retire_lunette();
            case "#28" -> joueur.retire_dissec();
            case "#29" -> joueur.retire_concoc();
            case "#30" -> joueur.retire_bourdon();
            case "#31" -> joueur.retire_parch_volcan();
            case "#32" -> joueur.retire_absorption2();
            case "#33" -> joueur.retire_pegase();
            case "#34" -> joueur.retire_cheval();
            case "#35" -> joueur.retire_pie();
            case "#36" -> joueur.retire_sphinx();
            case "#37" -> joueur.retire_fee();
            case "#38" -> joueur.retire_rune_arca();
            case "#39" -> joueur.retire_antidote();
            case "#40" -> joueur.retire_rune_annihilation();
            case "#41" -> joueur.retire_tatouage_resurection();
            case "#42" -> joueur.retire_fuite();
            case "#43" -> joueur.retire_grenade();
            case "#44" -> joueur.retire_bateau();
            case "#99" -> joueur.retirer_tout(false);
            default -> {
                System.out.println("Input unknown.");
                retirer_item(joueur);
            }
        }
    }
    
    private static void frapper_pantin(int i) throws IOException {
        Monstre pantin = Lieu.get_dummy();
        System.out.println("Cela vous coutera 2PO par participants.");
        System.out.println("Vous vous trouvez dans une simulation. La mort n'est pas définitive, et vous" + " poss" + "édez des quantités illimités de mana, d'aura et d'ingrédients. Vous pouvez utiliser sans limites" + " tout objet que vous avez sur vous ou que vous fabriquerez sur place, mais rien d'autre.");
        Combat.affrontement(joueurs[i].getPosition(), -1, pantin);
    }
    
    /**
     * Gère le départ en expédition et ses conscéquences
     * @param meneur le joueur principal
     * @throws IOException sans problème
     */
    static void expedition(int meneur) throws IOException {
        switch (joueurs[meneur].getPosition()) {
            case ENFERS -> expedition_enfer(meneur);
            case PRAIRIE -> expedition_prairie(meneur);
            case VIGNES -> expedition_vigne(meneur);
            case TEMPLE -> expedition_temple(meneur);
            case MER -> expedition_mer(meneur);
            case MONTS -> expedition_mont(meneur);
            case OLYMPE -> expedition_olympe(meneur);
            case ASCENDANT -> System.out.println("ERROR : DONOT");
        }
    }
    
    static void expedition_enfer(int meneur) throws IOException {
        Monstre monstre = Lieu.enfers();
        int jet = Input.D4() + joueurs[meneur].bonus_exploration();
        if (jet == 1 && rand.nextBoolean()) {
            jet = 0;
        } else if (jet > 5) {
            jet = 5;
        }
        switch (jet) {
            case 1, 2, 3 -> {
                System.out.println("Vous apercevez " + monstre.nomme(true));
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.ENFERS, -1, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }
                
            }
            case 4, 5 -> {
                if (rand.nextInt(3) == 0) {
                    Output.jouerSonCoffre();
                    Equipement.drop_0();
                } else {
                    System.out.println("Vous apercevez " + monstre.nomme(true));
                    if (Input.yn("Voulez vous l'attaquer ?")) {
                        Combat.affrontement(Position.ENFERS, -1, monstre);
                    } else {
                        System.out.println("Vous vous éloignez discrètement");
                    }
                }
            }
            default -> {
                System.out.println(monstre.nomme(true) + " vous attaque");
                Combat.affrontement(Position.ENFERS, meneur, monstre);
            }
        }
    }
    
    static void expedition_prairie(int meneur) throws IOException {
        Monstre monstre = Lieu.prairie();
        int jet = Input.D6() + joueurs[meneur].bonus_exploration();
        if (jet > 7) {
            jet = 7;
        }
        switch (jet) {
            case 2, 3, 4, 5 -> {
                System.out.println("Vous apercevez " + monstre.nomme(true));
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.PRAIRIE, -1, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }
                
            }
            case 6, 7 -> {
                if (rand.nextBoolean()) {
                    Output.jouerSonCoffre();
                    Equipement.drop_1();
                } else if (rand.nextBoolean()) {
                    Output.jouerSonCoffre();
                    Equipement.drop_0();
                } else {
                    System.out.println("Vous apercevez " + monstre.nomme(true));
                    if (Input.yn("Voulez vous l'attaquer ?")) {
                        Combat.affrontement(Position.PRAIRIE, -1, monstre);
                    } else {
                        System.out.println("Vous vous éloignez discrètement");
                    }
                }
            }
            default -> { // 1
                System.out.println(monstre.nomme(true) + " vous attaque");
                Combat.affrontement(Position.PRAIRIE, meneur, monstre);
            }
        }
    }
    
    static void expedition_vigne(int meneur) throws IOException {
        Monstre monstre = Lieu.vigne();
        int jet = Input.D6() + joueurs[meneur].bonus_exploration();
        if (jet > 7) {
            jet = 7;
        }
        switch (jet) {
            case 3, 4, 5 -> {
                System.out.println("Vous apercevez " + monstre.nomme(true));
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.VIGNES, -1, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }
                
            }
            case 6 -> {
                if (rand.nextBoolean()) {
                    Output.jouerSonCoffre();
                    int t = rand.nextInt(2) + 1;
                    for (int i = 0; i <= t; i++) {
                        Equipement.drop_1();
                    }
                } else if (rand.nextBoolean()) {
                    Output.jouerSonCoffre();
                    Equipement.drop_0();
                } else {
                    System.out.println("Vous apercevez " + monstre.nomme(true));
                    if (Input.yn("Voulez vous l'attaquer ?")) {
                        Combat.affrontement(Position.VIGNES, -1, monstre);
                    } else {
                        System.out.println("Vous vous éloignez discrètement");
                    }
                }
            }
            case 7 -> {
                Output.jouerSonCoffre();
                if (rand.nextBoolean()) {
                    Equipement.drop_1();
                } else {
                    Equipement.drop_promo();
                }
            }
            default -> { // 1, 2
                System.out.println(monstre.nomme(true) + " vous attaque");
                Combat.affrontement(Position.VIGNES, meneur, monstre);
            }
        }
    }
    
    static void expedition_temple(int meneur) throws IOException {
        Monstre monstre = Lieu.temple();
        int jet = Input.D8() + joueurs[meneur].bonus_exploration();
        if (jet > 9) {
            jet = 9;
        }
        switch (jet) {
            case 4, 5, 6, 7 -> {
                System.out.println("Vous apercevez " + monstre.nomme(true));
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.TEMPLE, -1, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }
                
            }
            case 8 -> {
                if (rand.nextBoolean()) {
                    Output.jouerSonCoffre();
                    int t = rand.nextInt(2) + 1;
                    for (int i = 0; i <= t; i++) {
                        Output.jouerSonCoffre();
                    }
                } else {
                    Output.jouerSonCoffre();
                    int t = rand.nextInt(2) + 1;
                    for (int i = 0; i <= t; i++) {
                        Output.jouerSonCoffre();
                    }
                }
            }
            case 9 -> {
                Output.jouerSonCoffre();
                if (rand.nextBoolean() || rand.nextBoolean()) {
                    Equipement.drop_1();
                } else {
                    Equipement.drop_promo();
                }
            }
            default -> { // 1, 2, 3
                System.out.println(monstre.nomme(true) + " vous attaque");
                Combat.affrontement(Position.TEMPLE, meneur, monstre);
            }
        }
    }
    
    static void expedition_mer(int meneur) throws IOException {
        Monstre monstre = Lieu.mer();
        int jet = Input.D8() + joueurs[meneur].bonus_exploration();
        jet = joueurs[meneur].trajet_mer(jet);
        if (jet > 9) {
            jet = 9;
        }
        switch (jet) {
            case 5, 6, 7 -> {
                System.out.println("Vous apercevez " + monstre.nomme(true));
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.MER, -1, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }
                
            }
            case 8 -> {
                Output.jouerSonCoffre();
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
                Output.jouerSonCoffre();
                if (rand.nextBoolean()) {
                    Equipement.drop_2();
                } else {
                    Equipement.drop_3();
                }
            }
            default -> { // 1, 2, 3, 4
                System.out.println(monstre.nomme(true) + " vous attaque");
                Combat.affrontement(Position.MER, meneur, monstre);
            }
        }
    }
    
    static void expedition_mont(int meneur) throws IOException {
        Monstre monstre = Lieu.mont();
        int jet = Input.D12() + joueurs[meneur].bonus_exploration();
        if (jet > 13) {
            jet = 13;
        }
        switch (jet) {
            case 7, 8, 9, 10, 11 -> {
                System.out.println("Vous apercevez " + monstre.nomme(true));
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.MONTS, -1, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }
                
            }
            case 12, 13 -> {
                Output.jouerSonCoffre();
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
                System.out.println(monstre.nomme(true) + " vous attaque");
                Combat.affrontement(Position.MONTS, meneur, monstre);
            }
        }
    }
    
    static void expedition_olympe(int meneur) throws IOException {
        Monstre monstre = Lieu.olympe();
        int jet = Input.D20() + joueurs[meneur].bonus_exploration();
        if (jet > 21) {
            jet = 21;
        }
        switch (jet) {
            case 19, 20, 21 -> {
                System.out.println("Vous apercevez " + monstre.nomme(true));
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.OLYMPE, -1, monstre);
                } else if (rand.nextBoolean() || (jet > 20 && rand.nextBoolean())) {
                    System.out.println("Vous vous éloignez discrètement");
                } else {
                    System.out.println(monstre.nomme(false) + " vous remarque et vous fonce dessus !");
                    Combat.affrontement(Position.OLYMPE, -1, monstre);
                }
                
            }
            default -> { // 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18
                System.out.println(monstre.nomme(true) + " vous attaque");
                Combat.affrontement(Position.OLYMPE, meneur, monstre);
            }
        }
    }
    
    /**
     * Renvoie un texte du lieu
     * @param p la position à donner
     * @return un String nommant le lieu (avec un déterminant)
     */
    public static String texte_pos(Position p) {
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
    
    
    /**
     * Initialise les paramètres d'une partie de zéro
     * @throws IOException toujours
     */
    private static void init_game() throws IOException {
        //titre
        String titre;
        Utilitaire.LoopGuard garde = new Utilitaire.LoopGuard();
        do {
            System.out.print("Entrez le nom de la sauvegarde :");
            titre = Input.read();
            if (titre.contains(":") || titre.contains("\"") || titre.contains("}") || titre.contains("{")) {
                System.out.println("Le titre ne peut pas contenir les caractères ':', '\"', et '}' !");
                titre = ":";
            }
            if (titre.isEmpty() || titre.equals("\n")) {
                titre = ":";
            }
            garde.check();
        } while (titre.equals(":") || !Input.yn("Confirmez vous le titre : " + titre + "?"));
        
        //nbj
        System.out.print("Entrez le nombre de joueur :");
        Main.nbj = Input.readInt();
        while (Main.nbj <= 0 || Main.nbj > Main.nbj_max) {
            System.out.println("Impossible, le nombre de joueur doit être comprit entre 1 et " + Main.nbj_max + ".");
            Main.nbj = Input.readInt();
            garde.check();
        }
        
        joueurs = new Joueur[Main.nbj];
        for (int i = 0; i < Main.nbj; i++) { //pour chaque joueur
            
            // nom
            String nom;
            do {
                System.out.println("Joueur " + (i + 1) + ", entrez votre nom :");
                nom = Input.read();
                if (nom.contains(":") || nom.contains("\"") || nom.contains("}") || nom.contains("{")) {
                    System.out.println("Le nom du joueur ne peut pas contenir les caractères ':', '\"', et '}' !");
                    nom = ":";
                }
                if (nom.isEmpty() || nom.equals("\n")) {
                    nom = ":";
                }
            } while (nom.equals(":") || !Input.yn("Voulez vous confirmer le pseudo " + nom + " ?"));
            
            // metier
            boolean run = true;
            Metier metier = Metier.TRYHARDER;
            String[] job = {"mage (no)ir", "archi(ma)ge", "(al)chimiste", "(gu)errière", "(ra)nger", "(sh)aman",
                    "(au)cun"};
            while (run) {
                garde.check();
                run = false;
                System.out.println(nom + ", choisissez votre profession : ");
                for (String s : job) {
                    System.out.println(s + " ");
                }
                switch (Input.read().toLowerCase()) {
                    case "no" -> metier = Metier.NECROMANCIEN;
                    case "ma" -> metier = Metier.ARCHIMAGE;
                    case "ra" -> metier = Metier.RANGER;
                    case "al" -> metier = Metier.ALCHIMISTE;
                    case "gu" -> metier = Metier.GUERRIERE;
                    case "sh" -> metier = Metier.SHAMAN;
                    case "au" -> {
                    }
                    default -> {
                        System.out.println("Unknown Input");
                        run = true;
                    }
                }
            }
            
            joueurs[i] = Joueur.CreerJoueur(nom, Position.DEFAULT, metier, 0, get_parent(), 0);
            joueurs[i].presente_detail();
            System.out.println();
        }
        SaveManager.creerSauvegarde(titre, Main.nbj);
    }
    
    /**
     * Renvoie l'arrondit de la valeur donnée minorée par 1.
     * @param valeur le float à corriger
     */
    public static int corriger(float valeur) {
        return corriger(valeur, 1);
    }
    
    /**
     * Renvoie l'arrondit de la valeur donnée
     * @param min    un minorant au résultat
     * @param valeur le float à corriger
     */
    public static int corriger(float valeur, int min) {
        return max(Math.round(valeur), min);
    }
    
    /**
     * Renvoie un parent divin aléatoire
     * @return un dieu
     */
    public static Dieux get_parent() {
        Dieux[] t = {Dieux.POSEIDON, Dieux.ZEUS, Dieux.HADES, Dieux.ARES, Dieux.DIONYSOS, Dieux.APOLLON, Dieux.DEMETER};
        return t[rand.nextInt(t.length)];
    }
}

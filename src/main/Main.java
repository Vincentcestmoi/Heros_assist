package main;

import Exterieur.*;

import Metiers.Joueur;

import Enum.Metier;
import Enum.Position;
import Enum.Choix;
import Enum.Dieux;

import Monstre.Lieu;
import Monstre.Monstre;

import Equipement.Equipement;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.max;


public class Main {
    
    static final Random rand = new Random();
    static final int nbj_max = 8;

    static public int nbj;
    public static int Path = -1; //-1 = vide
    static public Joueur[] joueurs;

    public static void main(String[] args) throws IOException {

        if(!Arrays.equals(args, new String[]{}))
            Path = Integer.parseInt(args[0]);

        if (!Input.load()) {
            init_game();
        }
        
        boolean run = true;
        int i = nbj;
        while (run) {
            if (i == nbj) {
                SaveManager.sauvegarder(true); // sauvegarde auto
                i = 0;
            }
            
            //tour de jeu
            Joueur joueur = joueurs[i];
            System.out.println(joueur.getNom() + " c'est votre tour, vous êtes " + texte_pos(joueur.getPosition()) + ".");
            Choix choix = Input.tour(i);
            switch (choix) {
                // action classique
                case EXPLORER -> expedition(i);
                case MONTER -> joueur.ascendre(i);
                case DESCENDRE -> {
                    System.out.println(joueur.getNom() + " retourne en des terres moins inhospitalières.");
                    joueur.descendre();
                }
                case MARCHE -> joueur.aller_au_marche();
                case DUMMY -> frapper_pantin(i);
                case DRESSER -> joueur.dresser();
                case ATTENDRE -> System.out.println(joueur.getNom() + " passe son tour.");

                // action caché
                case SUICIDE -> joueur.mort_def();
                case QUITTER -> run = false;
                case FAMILIER_PLUS -> {
                    joueur.ajouter_familier();
                    i -= 1; //n'utilise pas le tour
                }
                case FAMILIER_MOINS -> {
                    joueur.perdre_familier();
                    i --;
                }
                case STAT -> {
                    joueur.presente_detail();
                    i--;
                }
                case RETOUR -> i = i == 0 ? nbj - 2 : i - 2;
            }
            System.out.println();
            i++;
        }
        
        //arrêt
        System.out.println("Sauvegarde des données joueurs.");
        SaveManager.sauvegarder(false);
        System.out.println("Fin du programme");
    }

    private static void frapper_pantin(int i) throws IOException {
        Monstre pantin = Lieu.get_dummy();
        System.out.println("Cela vous coutera 2PO par participants.");
        System.out.println("Vous vous trouvez dans une simulation. La mort n'est pas définitive, et vous" +
                " possédez des quantités illimités de mana, d'aura et d'ingrédients. Vous pouvez utiliser sans limites" +
                " tout objet que vous avez sur vous ou que vous fabriquerez sur place, mais rien d'autre.");
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
        if(jet == 1 && rand.nextBoolean()){
            jet = 0;
        } else if(jet > 5){
            jet = 5;
        }
        switch (jet) {
            case 1, 2, 3 -> {
                System.out.println("Vous apercevez un(e) " + monstre.getNom());
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.ENFERS, -1, monstre);
                } else {
                    System.out.println("Vous vous éloignez discrètement");
                }

            }
            case 4, 5 -> {
                if (rand.nextInt(3) == 0) {
                    Equipement.drop_0();
                } else {
                    System.out.println("Vous apercevez un(e) " + monstre.getNom());
                    if (Input.yn("Voulez vous l'attaquer ?")) {
                        Combat.affrontement(Position.ENFERS, -1, monstre);
                    } else {
                        System.out.println("Vous vous éloignez discrètement");
                    }
                }
            }
            default -> {
                System.out.println(monstre.getNom() + " vous attaque");
                Combat.affrontement(Position.ENFERS, meneur, monstre);
            }
        }
    }

    static void expedition_prairie(int meneur) throws IOException {
        Monstre monstre = Lieu.prairie();
        int jet = Input.D6() + joueurs[meneur].bonus_exploration();
        if(jet > 7){
            jet = 7;
        }
        switch (jet) {
            case 2, 3, 4, 5 -> {
                System.out.println("Vous apercevez un(e) " + monstre.getNom());
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.PRAIRIE, -1, monstre);
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
                    System.out.println("Vous apercevez un(e) " + monstre.getNom());
                    if (Input.yn("Voulez vous l'attaquer ?")) {
                        Combat.affrontement(Position.PRAIRIE, -1, monstre);
                    } else {
                        System.out.println("Vous vous éloignez discrètement");
                    }
                }
            }
            default -> { // 1
                System.out.println(monstre.getNom() + " vous attaque");
                Combat.affrontement(Position.PRAIRIE, meneur, monstre);
            }
        }
    }

    static void expedition_vigne(int meneur) throws IOException {
        Monstre monstre = Lieu.vigne();
        int jet = Input.D6() + joueurs[meneur].bonus_exploration();
        if(jet > 7){
            jet = 7;
        }
        switch (jet) {
            case 3, 4, 5 -> {
                System.out.println("Vous apercevez un(e) " + monstre.getNom());
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.VIGNES, -1, monstre);
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
                    System.out.println("Vous apercevez un(e) " + monstre.getNom());
                    if (Input.yn("Voulez vous l'attaquer ?")) {
                        Combat.affrontement(Position.VIGNES, -1, monstre);
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
                System.out.println(monstre.getNom() + " vous attaque");
                Combat.affrontement(Position.VIGNES, meneur, monstre);
            }
        }
    }

    static void expedition_temple(int meneur) throws IOException {
        Monstre monstre = Lieu.temple();
        int jet = Input.D8() + joueurs[meneur].bonus_exploration();
        if(jet > 9){
            jet = 9;
        }
        switch (jet) {
            case 4, 5, 6, 7 -> {
                System.out.println("Vous apercevez un(e) " + monstre.getNom());
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.TEMPLE, -1, monstre);
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
                System.out.println(monstre.getNom() + " vous attaque");
                Combat.affrontement(Position.TEMPLE, meneur, monstre);
            }
        }
    }

    static void expedition_mer(int meneur) throws IOException {
        Monstre monstre = Lieu.mer();
        int jet = Input.D8() + joueurs[meneur].bonus_exploration();
        if(jet > 9){
            jet = 9;
        }
        switch (jet) {
            case 5, 6, 7 -> {
                System.out.println("Vous apercevez un(e) " + monstre.getNom());
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.MER, -1, monstre);
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
                System.out.println(monstre.getNom() + " vous attaque");
                Combat.affrontement(Position.MER, meneur, monstre);
            }
        }
    }

    static void expedition_mont(int meneur) throws IOException {
        Monstre monstre = Lieu.mont();
        int jet = Input.D12() + joueurs[meneur].bonus_exploration();
        if(jet > 13){
            jet = 13;
        }
        switch (jet) {
            case 7, 8, 9, 10, 11 -> {
                System.out.println("Vous apercevez un(e) " + monstre.getNom());
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.MONTS, -1, monstre);
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
                System.out.println(monstre.getNom() + " vous attaque");
                Combat.affrontement(Position.MONTS, meneur, monstre);
            }
        }
    }

    static void expedition_olympe(int meneur) throws IOException {
        Monstre monstre = Lieu.olympe();
        int jet = Input.D20() + joueurs[meneur].bonus_exploration();
        if(jet > 21){
            jet = 21;
        }
        switch (jet) {
            case 19, 20, 21 -> {
                System.out.println("Vous apercevez un(e) " + monstre.getNom());
                if (Input.yn("Voulez vous l'attaquer ?")) {
                    Combat.affrontement(Position.OLYMPE, -1, monstre);
                } else if (rand.nextBoolean() || (jet > 20 && rand.nextBoolean())) {
                    System.out.println("Vous vous éloignez discrètement");
                } else {
                    System.out.println(monstre.getNom() + " vous remarque et vous fonce dessus !");
                    Combat.affrontement(Position.OLYMPE, -1, monstre);
                }

            }
            default -> { // 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18
                System.out.println(monstre.getNom() + " vous attaque");
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
        do {
            System.out.print("Entrez le nom de la sauvegarde :");
            titre = Input.read();
            if(titre.contains(":") || titre.contains("\"") || titre.contains("}") || titre.contains("{")){
                System.out.println("Le titre ne peut pas contenir les caractères ':', '\"', et '}' !");
                titre = ":";
            }
            if(titre.isEmpty() || titre.equals("\n")){
                titre = ":";
            }
        }while(titre.equals(":") || !Input.yn("Confirmez vous le titre : " + titre + "?"));

        //nbj
        System.out.print("Entrez le nombre de joueur :");
        Main.nbj = Input.readInt();
        while(Main.nbj <= 0 || Main.nbj > Main.nbj_max) {
            System.out.println("Impossible, le nombre de joueur doit être comprit entre 1 et " + Main.nbj_max +".");
            Main.nbj = Input.readInt();
        }

        joueurs = new Joueur[Main.nbj];
        for(int i = 0; i < Main.nbj; i++) { //pour chaque joueur

            // nom
            String nom;
            do{
                System.out.println("Joueur " + (i + 1) + ", entrez votre nom :");
                nom = Input.read();
                if(nom.contains(":") || nom.contains("\"") || nom.contains("}") || nom.contains("{")){
                    System.out.println("Le nom du joueur ne peut pas contenir les caractères ':', '\"', et '}' !");
                    nom = ":";
                }
                if(nom.isEmpty() || nom.equals("\n")){
                    nom = ":";
                }
            }while(nom.equals(":") || !Input.yn("Voulez vous confirmer le pseudo " + nom + " ?"));

            // metier
            boolean run = true;
            Metier metier = Metier.TRYHARDER;
            String[] job = {"mage (no)ir", "archi(ma)ge", "(al)chimiste", "(gu)erriere", "(ra)nger", "(sh)aman", "(au)cun"};
            while(run) {
                run = false;
                System.out.println(nom + ", choississez votre profession : ");
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
                    case "au" -> {}
                    default -> {
                        System.out.println("Unknow Exterieur.Input");
                        run = true;
                    }
                }
            }

            joueurs[i] = Joueur.CreerJoueur(nom, Position.DEFAULT, metier, 0, get_parent(), 0);
            joueurs[i].presente_detail();
            joueurs[i].presente();
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
     * @param min un minorant au résultat
     * @param valeur le float à corriger
     */
    public static int corriger(float valeur, int min){
        return max(Math.round(valeur), min);
    }

    /**
     * Renvoie un parent divin aléatoire
     * @return un dieu
     */
    public static Dieux get_parent(){
        Dieux[] t = {Dieux.POSEIDON, Dieux.ZEUX, Dieux.HADES, Dieux.ARES, Dieux.DYONISOS, Dieux.APOLLON, Dieux.DEMETER};
        return t[rand.nextInt(t.length)];
    }

}

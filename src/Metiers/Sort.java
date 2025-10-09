package Metiers;

import Exterieur.Input;
import Monstre.Monstre;

import Enum.Competence;

import main.Main;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Sort {
    
    static Random rand = new Random();

    static public int nb_fire_rune = 1;
    static public int nb_great_fire_rune = 0;

    /**
     * Affiche les bienfaits de la méditation
     * @throws IOException toujours
     */
    public static void meditation() throws IOException {
        int jet = Input.D8() + rand.nextInt(3) - 1;
        if (jet <= 2) {
            System.out.println("Vous récupérez 2PP.");
        }
        else if(jet <= 4) {
            System.out.println("Vous récupérez 3PP.");
        }
        else if(jet <= 7) {
            System.out.println("Vous récupérez 4PP.");
        }
        else{
            System.out.println("Vous récupérez 5PP.");
        }
    }

    /**
     * Calcule et applique les dommages de la compétence "boule de feu"
     * @param ennemi la cible du sort
     * @throws IOException toujours
     */
    public static void boule_de_feu(Monstre ennemi) throws IOException {
        System.out.println("Vous vous préparez à lancer une boule de feu.");
        System.out.println("Combien de PP mettez vous dans le sort ? (min " + (2 + nb_great_fire_rune) + ")");
        int mana = Input.readInt();
        int jet = Input.D4() + mana + rand.nextInt(3) - 1 + (nb_fire_rune / 2) + (nb_great_fire_rune / 2);
        int dmg;
        if (jet <= (2  + nb_great_fire_rune) || mana < (2  + nb_great_fire_rune)) {
            System.out.println("Le sort ne fonctionne pas .");
            return;
        }
        else if (jet == 3) {
            System.out.println("Vous lancez une pitoyable boule de feu sur " + ennemi.getNom() + ".");
            dmg = 3;
        }
        else if (jet == 4) {
            System.out.println("Vous lancez une boule de feu sur " + ennemi.getNom() + ".");
            dmg = 5;
        }
        else if (jet <= 6) {
            System.out.println("Vous lancez une boule de feu sur " + ennemi.getNom() + ".");
            dmg = 6;
        }
        else if (jet <= 8) {
            System.out.println("Vous lancez une impressionnante boule de feu sur " + ennemi.getNom() + ".");
            dmg = 8;
        }
        else if (jet <= 10) {
            System.out.println("Un brasier s'abat sur " + ennemi.getNom() + " !");
            dmg = 11;
        }
        else if (jet == 11) {
            System.out.println("Un brasier s'abat sur " + ennemi.getNom() + " !");
            dmg = 13;
            if(rand.nextBoolean()){
                ennemi.affecte();
            }
        }
        else if (jet == 12) {
            System.out.println("Une tornade de flamme s'abat violement sur " + ennemi.getNom() + " !");
            dmg = 15;
            if(rand.nextBoolean()){
                ennemi.affecte();
            }
        }
        else if (jet == 13) {
            System.out.println("Une tornade de flamme s'abat violement sur " + ennemi.getNom() + " !");
            dmg = 16;
            if(rand.nextBoolean()){
                ennemi.affecte();
            }
        }
        else{
            System.out.println("Les flammes de l'enfers brûlent intensemment " + ennemi.getNom() + ".");
            dmg = 18;
            ennemi.affecte();
        }
        dmg += nb_great_fire_rune * 4 + nb_fire_rune * 2;
        ennemi.dommage_magique(dmg);
    }

    /**
     * Indique l'efficacité de la compétence "armure de glace"
     * @throws IOException toujours
     */
    public static void armure_de_glace() throws IOException {
        System.out.println("Vous vous préparez à créer une armure de glace.");
        System.out.println("Combien de PP mettez vous dans le sort ? (min 3): ");
        int mana = Input.readInt();
        int jet = Input.D8() + mana + rand.nextInt(3) - 1;
        if (jet <= 3 || mana < 3) {
            System.out.println("Le sort ne fonctionne pas.");
        } else if (jet <= 6) {
            System.out.println("La cible gagne 3 points de résistance.");
        } else if (jet <= 9) {
            System.out.println("La cible gagne 5 points de résistance.");
        } else if (jet <= 12) {
            System.out.println("La cible gagne 6 points de résistance et 1 point d'armure.");
        } else if (jet == 15) {
            System.out.println("La cible gagne 8 points de résistance et 1 point d'armure.");
        } else if (jet == 16) {
            System.out.println("La cible gagne 9 points de résistance et 1 point d'armure.");
        } else if (jet == 17) {
            System.out.println("La cible gagne 10 points de résistance et 1 point d'armure.");
        } else {
            System.out.println("La cible gagne 10 points de résistance et 2 point d'armure.");
        }
    }

    /**
     * Calcule et applique les dommages de la compétence "foudre"
     * @param ennemi la cible du sort
     * @throws IOException toujours
     */
    public static void foudre(Monstre ennemi) throws IOException {
        System.out.println("Vous vous préparez à lancer un puissant éclair.");
        System.out.println("Combien de PP mettez vous dans le sort ? (min 7) : ");
        int mana = Input.readInt();
        int jet = Input.D12() + mana + rand.nextInt(3) - 1;
        int dmg;
        if (jet <= 7 || mana < 7) {
            System.out.println("Le sort ne fonctionne pas.");
            return;
        }
        else if (jet <= 10) {
            System.out.println("Un arc électrique vient frapper " + ennemi.getNom() + ".");
            dmg = 12;
        }
        else if (jet <= 12) {
            System.out.println("Un arc électrique vient frapper " + ennemi.getNom() + ".");
            dmg = 13;
        }
        else if (jet <= 14) {
            System.out.println("Un éclair s'abat sur " + ennemi.getNom() + ".");
            dmg = 16;
            if(rand.nextBoolean()){
                ennemi.affecte();
            }
        }
        else if (jet <= 16) {
            System.out.println("Un puissant éclair s'abat sur " + ennemi.getNom() + ".");
            dmg = 18;
            if(rand.nextBoolean()){
                ennemi.affecte();
            }
        }
        else if (jet <= 18) {
            System.out.println("Un puissant éclair s'abat sur " + ennemi.getNom() + ".");
            dmg = 20;
            ennemi.affecte();
        }
        else if (jet == 19){
            System.out.println("Le ciel s'illumine un instant et un gigantesque éclair s'abat sur  " + ennemi.getNom() + " dans un immense fracas.");
            dmg = 22;
            ennemi.affecte();
        }
        else if (jet == 20){
            System.out.println("Le ciel s'illumine un instant et un gigantesque éclair s'abat sur  " + ennemi.getNom() + " dans un immense fracas.");
            dmg = 24;
            ennemi.affecte();
        }
        else if (jet == 21) {
            System.out.println("Un déchainement de pure énergie fend l'espace entre les cieux et la terre et vient percuter " + ennemi.getNom() + " de plein fouet.");
            dmg = 25;
            if(rand.nextBoolean()){
                ennemi.affecte();
            }
            else {
                ennemi.do_assomme();
            }
        }
        else{
            System.out.println("Un déchainement de pure énergie fend l'espace entre les cieux et la terre et vient percuter " + ennemi.getNom() + " de plein fouet.");
            dmg = 27;
            ennemi.do_assomme();
        }
        ennemi.dommage_magique(dmg);
    }

    /**
     * Applique les compétences "addiction au mana" et "maitre du mana" de l'archimage
     * @throws IOException toujours
     */
    public static boolean addiction() throws IOException {
        int jet = Input.D4() + rand.nextInt(3) - 1;
        if (jet < 4) {
            System.out.println("Vous perdez connaissance.");
            return true;
        }
        else {
            jet = Input.D4();
            if (jet > 1) {
                System.out.println("Vous récuperez " + (jet - 1) + "PP.");
            } else {
                System.out.println("Vous récuperez 1PP.");
            }
        }
        return false;
    }

    /**
     * Indique le résultat de la compétence "fouille"
     */
    public static void fouille() throws IOException {
        System.out.println("Vous chercher autour de vous tout ce qui pourrait être utile pour vos potions.");
        int temp = Input.D20();
        if (temp <= 15 + rand.nextInt(10) - 5) {
            System.out.println("Vous ne trouvez rien.");
        }
        else if (temp <= 20 + rand.nextInt(5) - 2) {
            System.out.println("Vous trouvez 1 ingrédient.");
        }
        else {
            System.out.println("Vous récoltez 2 ingrédients.");
        }
    }

    /**
     * Indique le résultat de la compétence "dissection"
     * @param etat l'état du cadavre
     * @return le changement d'état du corps
     * @throws IOException toujours
     */
    public static int dissection(int etat) throws IOException {
        int temp = Input.D6();
        if(etat >= 25){
            temp += 2;
        }
        else if(etat >= 15){
            temp += 1;
        }
        else if(etat == 0){
            temp -= 3;
        }
        else if(etat < 5){
            temp -= 2;
        }
        else if(etat < 10){
            temp -= 1;
        }
        if (temp <= 1 + rand.nextInt(2) - 1) {
            System.out.println("Vous n'extrayez rien d'utile.");
            return -1;
        } else if (temp <= 4 + rand.nextInt(2) - 1) {
            System.out.println("Vous trouvez 1 ingrédient.");
            return -8 - rand.nextInt(4);
        }
        else if (temp <= 6){
            System.out.println("Vous récoltez 2 ingrédients.");
            return -13 - rand.nextInt(11);
        }
        else{
            System.out.println("Vous récoltez 3 ingrédients.");
            return -20 - rand.nextInt(25);
        }
    }

    /**
     * Permet à l'archimage de lancer ses sorts
     * @param actif une liste de boolean indiquant les participants encore en jeu
     * @param nom les noms des participants
     * @param assomme une liste de boolean indiquant les participants inconscients
     * @param reveil le tableau indiquand à quel point les participant sont près de se réveiller
     * @param ennemi le monstre ennemi
     * @param lanceur l'indice de l'archimage
     * @throws IOException toujours
     */
    public static void sort(boolean[] actif, String[] nom, boolean[] assomme, int[] reveil, Monstre ennemi, int lanceur) throws IOException {
        extracted(actif, nom, assomme, reveil, ennemi, lanceur);
        if(ennemi.est_mort()){
            return;
        }
        if(Input.yn("Votre mana est-il tombé à 0 ?")){
            if(addiction()){
                assomme[lanceur] = true;
                reveil[lanceur] = 0;
                return;
            }
        }
        System.out.println("Vous préparez votre second sort.");
        extracted(actif, nom, assomme, reveil, ennemi, lanceur);
        if(Input.yn("Votre mana est-il tombé à 0 ?")){
            if(addiction()){
                assomme[lanceur] = true;
                reveil[lanceur] = 0;
            }
        }
    }

    /**
     * Fonction auxiliaire de sort
     */
    private static void extracted(boolean[] actif, String[] nom, boolean[] assomme, int[] reveil, Monstre ennemi, int lanceur) throws IOException {
        switch (Input.sort()){
            case BDF -> boule_de_feu(ennemi);
            //case ONDE_CHOC -> onde_choc(ennemi);
            case ADG -> armure_de_glace();
            case FOUDRE -> foudre(ennemi);
            case AUTRE -> ennemi.dommage_magique(Input.magie());
        }
    }

    /**
     * Applique la compétence "coup critique" sur un tir classique
     * @param ennemi le monstre ennemi
     * @throws IOException toujours
     */
    public static void coup_critique(Monstre ennemi) throws IOException {
        switch(Input.D4()){
            case 1 -> {
                System.out.println("La pointe de votre flèche éclate en plein vol.");
                ennemi.tir(Input.tir(), 0.5F);
            }
            case 2, 3 -> ennemi.tir(Input.tir());
            case 4, 5 -> {
                System.out.println("Votre flèche file droit sur " + ennemi.getNom() + " et lui porte un coup puissant.");
                ennemi.tir(Input.tir(), 2F);
            }
            default -> {
                System.out.println("Entré invalide, tir classique appliqué.");
                ennemi.tir(Input.tir());
            }
        }
    }

    /**
     * Appliques les effets de la compétence "assassinat"
     * @param ennemi le monstre adverse
     * @return true si le joueur est passé en première ligne, false sinon
     * @throws IOException toujours
     */
    public static boolean assassinat(Monstre ennemi) throws IOException {
        if(Input.D6() + rand.nextInt(3) - 1 > 3){
            System.out.println("Vous vous faufilez derrière " + ennemi.getNom() + " sans qu'il ne vous remarque.");
            ennemi.dommage(Input.atk() * 2 + 7);
            return true;
        }
        System.out.println("Vous jugez plus prudent de ne pas engagez pour l'instant...");
        return false;
    }

    /**
     * Applique la compétence "assaut"
     * @param ennemi le monstre ennemi
     * @param berserk la force de frappe additionnelle
     * @throws IOException toujours
     */
    public static void assaut(Monstre ennemi, float berserk) throws IOException {
        System.out.println("Vous chargez brutalement " + ennemi.getNom());
        int jet = Input.D8() + rand.nextInt(3) - 1;
        ennemi.dommage(Input.atk(), 0.1f * jet + berserk);
        // ennemi.part_soin += berserk; TODO
    }

    /**
     * Laisse l'alchimiste concoter ses potions
     * @throws IOException toujours
     */
    public static void concocter() throws IOException {
        switch (Input.concoction()) {
            case RESISTANCE -> concoc_resi();
            case ALEATOIRE -> concoc_alea();
            case DIVINE -> concoc_divine();
            case SERIE -> concoc_serie();
            case ENERGIE -> concoc_energie();
            case FORCE -> concoc_force();
            case INSTABLE -> concoc_bombe();
            case MIRACLE -> concoc_miracle();
            case SOIN -> concoc_soin();
            case TOXIQUE -> concoc_toxique();
            case AUTRE -> System.out.println("Vous réalisez votre concoction.");
        }
    }

    /**
     * Réalise une potion de résistance
     * @throws IOException toujours
     */
    public static void concoc_resi() throws IOException {
        System.out.println("Combien d'ingrédient allez-vous utiliser ? (min 5): ");
        int ingre = Input.readInt();
        int jet = Input.D10() + ingre + rand.nextInt(3) - 1;
        if (jet < 11 || ingre < 5) {
            System.out.println("Vous avez produit une potion insipide (EX1PV).");
        }
        else if (jet < 14) {
            System.out.println("Vous avez produit une potion de vigeur (EX3RES).");
        }
        else if (jet < 19) {
            System.out.println("Vous avez produit une potion de résistance (EX4RES).");
        }
        else{
            System.out.println("Vous avez produit une potion de solidification (M4RES1DEF).");
        }
    }

    /**
     * Réalise une potion de difficulté 10 ou moins
     * @throws IOException toujours
     */
    public static void concoc_alea() throws IOException {

        int[] popo_cost = {1, 1, 5, 8, 4, 9, 9, 6, 10};
        String[] popo = {"potion douteuse (EXC1D)", "potion insipide (EX1PV)", "potion toxique (EXC2D)",
                "potion de poison (EXC3D)", "potion instable (EXD)", "potion de feu (EXD)", "de force (EX2ATK)",
                "potion de vie (EX4PV)", "potion énergétique (M2PP)"};
        
        System.out.println("Combien d'ingrédient allez-vous utiliser ? (max 4): ");
        int ingre = Input.readInt();
        int concoc = Input.D4() + ingre + 2 + rand.nextInt(3) - 1;
        int[] t = {-1, -1, -1, -1, -1, -1, -1, -1, -1};

        for (int i = 0; i < t.length; ) {
            int temp = rand.nextInt(t.length);
            if (t[temp] == -1) {
                t[temp] = i;
                i++;
            }
        }

        for (int j : t) {
            if (popo_cost[j] <= concoc) {
                System.out.println("Vous avez concocté une " + popo[j]);
                return;
            }
        }
    }

    /**
     * Tente de réaliser une potion divine
     * @throws IOException toujours
     */
    public static void concoc_divine() throws IOException {
        System.out.println("Combien d'ingrédient allez-vous utiliser ? (min 7): ");
        int ingre = Input.readInt();
        int jet = Input.D10() + ingre + rand.nextInt(3) - 1;
        if (jet < 10 || ingre < 7) {
            System.out.println("Vous avez produit une potion insipide (EX1PV).");
        } 
        else if (jet < 15) {
            System.out.println("Vous avez produit une potion de santé (EX6PV).");
        } 
        else {
            System.out.println("Vous avez produit une potion divine (ALC5PV7RES3ATK).");
        }
    }

    /**
     * Réalise des potions
     * @throws IOException toujours
     */
    public static void concoc_serie() throws IOException {

        int[] popo_cost = {1, 1, 5, 8, 4, 9, 9, 6, 10, 11, 13, 11, 14, 14, 11, 14, 15, 15};
        String[] popo = {"potion douteuse (EXC1D)", "potion insipide (EX1PV)", "potion toxique (EXC2D)", "potion de poison (EXC3D)",
                "potion instable (EXD)", "potion de feu (EXD)", "potion de force (EX2ATK)", "potion de vie (EX4PV)",
                "potion énergétique (M2PP)", "potion de santé (EX6PV)", "potion d'énergie (M4PP)", "potion de vigeur (EX3RES)",
                "potion de résistance (EX4RES)", "potion de puissance (EX3ATK)", "flasque nécrosé (EXC4D)", "potion nécrotyque (EXC5D)",
                "potion explosive (EXD)", "potion divine (ALC5PV7RES3ATK)"};


        System.out.println("Combien d'ingrédient allez-vous utiliser ? ");
        int ingre = Input.readInt();
        int concoc = Input.D6() + ingre + rand.nextInt(3) - 1;


        while (concoc > 0) {

            //tirage aléatoire
            int[] t = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
            for (int i = 0; i < t.length; ) {
                int temp = rand.nextInt(t.length);
                if (t[temp] == -1) {
                    t[temp] = i;
                    i++;
                }
            }

            for (int i = 0; i < popo.length; i++) {
                int j = t[i];
                if (popo_cost[j] <= concoc) {
                    System.out.println("Vous avez concocté une " + popo[j]);
                    concoc -= popo_cost[j];
                    break;
                }
            }
        }
    }

    /**
     * Réalise une potion d'énergie
     * @throws IOException toujours
     */
    public static void concoc_energie() throws IOException {
        System.out.println("Combien d'ingrédient allez-vous utiliser ? (min 5): ");
        int ingre = Input.readInt();
        int jet = Input.D10() + ingre + rand.nextInt(3) - 1;
        if (jet < 10 || ingre < 5) {
            System.out.println("Vous avez produit une potion insipide (EX1PV).");
        } 
        else if (jet < 13) {
            System.out.println("Vous avez produit une potion énergétique (M2PP).");
        } 
        else if (jet < 18) {
            System.out.println("Vous avez produit une potion d'énergie (M4PP).");
        } 
        else if (jet < 20) {
            System.out.println("Vous avez produit une potion de mana (M6PP).");
        } 
        else {
            System.out.println("Vous avez produit une potion ancestrale (MM+PP).");
        }
    }

    /**
     * Réalise une potion de force
     * @throws IOException toujours
     */
    public static void concoc_force() throws IOException {
        System.out.println("Combien d'ingrédient allez-vous utiliser ? (min 4): ");
        int ingre = Input.readInt();
        int jet = Input.D10() + ingre + rand.nextInt(3) - 1;
        if (jet < 9 || ingre < 4) {
            System.out.println("Vous avez produit une potion insipide (EX1PV).");
        }
        else if (jet < 14) {
            System.out.println("Vous avez produit une potion de force (EX2ATK).");
        }
        else if (jet < 16) {
            System.out.println("Vous avez produit une potion de puissance (EX3ATK).");
        }
        else {
            System.out.println("Vous avez produit une potion du colosse (EX4ATK).");
        }
    }

    /**
     * Réalise une potion explosive
     * @throws IOException toujours
     */
    public static void concoc_bombe() throws IOException {
        System.out.println("Combien d'ingrédient allez-vous utiliser ? (min 2): ");
        int ingre = Input.readInt();
        int jet = Input.D10() + ingre + rand.nextInt(3) - 1;
        if (jet < 4 || ingre < 2) {
            System.out.println("Vous avez produit une potion douteuse (EXC1D).");
        }
        else if (jet < 9) {
            System.out.println("Vous avez produit une potion instable (EXD).");
        }
        else if (jet < 15) {
            System.out.println("Vous avez produit une potion de feu (EXD).");
        }
        else if (jet < 18) {
            System.out.println("Vous avez produit une potion explosive (EXD).");
        }
        else {
            System.out.println("Vous avez produit une bombe (EXD).");
        }
    }

    /**
     * Réalise une potion de soin
     * @throws IOException toujours
     */
    public static void concoc_soin() throws IOException {
        System.out.println("Combien d'ingrédient allez-vous utiliser ? (min 3): ");
        int ingre = Input.readInt();
        int jet = Input.D10() + ingre + rand.nextInt(3) - 1;
        if (jet < 6 || ingre < 3) {
            System.out.println("Vous avez produit une potion insipide (EX1PV).");
        }
        else if (jet < 11) {
            System.out.println("Vous avez produit une potion de vie (EX4PV).");
        }
        else if (jet < 16) {
            System.out.println("Vous avez produit une potion de santé (EX6PV).");
        }
        else if (jet < 20) {
            System.out.println("Vous avez produit un fortifiant (EX8PV).");
        }
        else {
            System.out.println("Vous avez produit une potion de regénération (M10PV).");
        }
    }

    /**
     * Réalise une potion de toxique
     * @throws IOException toujours
     */
    public static void concoc_toxique() throws IOException {
        System.out.println("Combien d'ingrédient allez-vous utiliser ? (min 2): ");
        int ingre = Input.readInt();
        int jet = Input.D10() + ingre + rand.nextInt(3) - 1;
        if (jet < 5 || ingre < 2) {
            System.out.println("Vous avez produit une potion douteuse (EXC1D).");
        }
        else if (jet < 8) {
            System.out.println("Vous avez produit une potion toxique (EXC2D).");
        }
        else if (jet < 11) {
            System.out.println("Vous avez produit une potion de poison (EXC3D).");
        }
        else if (jet < 14) {
            System.out.println("Vous avez produit une flasque nécrosé (EXC4D).");
        }
        else {
            System.out.println("Vous avez produit une potion nécrotyque (EXC5D).");
        }
    }

    /**
     * Tente de réaliser un élixir
     * @throws IOException toujours
     */
    public static void concoc_miracle() throws IOException {
        System.out.println("Combien d'ingrédient allez-vous utiliser ? (min 10): ");
        int ingre = Input.readInt();
        int jet = Input.D10() + ingre + rand.nextInt(3) - 1;
        if (jet < 12 || ingre < 9) {
            System.out.println("Vous avez produit une potion insipide (EX1PV).");
        }
        else if (jet < 21) {
            System.out.println("Vous avez produit une potion de santé (EX6PV).");
        }
        else {
            System.out.println("Vous avez produit un élixir (ALCRESALTPVRES).");
        }
    }

    /**
     * Applique la compétence "lien" du shaman
     * @param i l'indice du shaman
     * @param ennemi le Monstre à lier
     * @param mort la liste de mortalité
     * @param actif la liste d'activités
     * @return si le combat s'arrète
     * @throws IOException toujours
     */
    public static boolean lien(int i, Monstre ennemi, boolean[] mort, boolean[] actif) throws IOException {
        if (ennemi.getCompetence() == Competence.CHRONOS) {
            System.out.println("Les esprits de vos ancêtres vous arretes avant que vous ne fassiez quelques choses de stupides.");
            return false;
        }
        int ratio = (int) ((float) ennemi.getVie() / ennemi.getVieMax() * 12);
        if (ennemi.getCompetence() == Competence.PRUDENT || ennemi.getCompetence() == Competence.MEFIANT || ennemi.getCompetence() == Competence.SUSPICIEUX) {
            ratio += 4 + rand.nextInt(3);
        }
        System.out.println(Main.joueurs[i].getNom() + " tente de lier son âme à " + ennemi.getNom());
        int result = Input.D12() - ratio + rand.nextInt(3) - 1;
        if (result <= 1) {
            System.out.println("L'âme de " + Main.joueurs[i].getNom() + " est violemment rejeté par celle de " + ennemi.getNom() + " !");
            mort[i] = true;
            actif[i] = false;
            return false;
        }
        if (result <= 4) {
            System.out.println(Main.joueurs[i].getNom() + " n'est pas parvenu à se lier à " + ennemi.getNom());
            return false;
        }
        else {
            System.out.println("Les âmes de " + ennemi.getNom() + " et de " + Main.joueurs[i].getNom() + " entre en communion !");
            Main.joueurs[i].setOb(min(7, rand.nextInt(result) + 1));
            return true;
        }
    }


    /**
     * Applique la compétence "incantation" du shaman
     * @param i l'index du shaman
     * @param ennemi le monstre adverse
     * @param berserk la liste de berserk
     * @param assomme la liste des entités assommée
     * @param reveil la liste de réveil
     * @param alter_attaque le modificateur d'attaque
     * @param alter_tir le modificateur de tir
     * @throws IOException toujours
     */
    public static void incantation(int i, Monstre ennemi, float[] berserk, boolean[] assomme, int[] reveil, int[] alter_tir, int[] alter_attaque) throws IOException {
        switch(Input.incantation()){
            case COLERE -> colere(i, ennemi, berserk);
            case NUAGE -> nuage(ennemi);
            case ELEMENTAIRE -> element(ennemi, assomme, reveil, alter_tir, alter_attaque);
            case BENIE -> benir();
        }
    }

    /**
     * La compétence "chant de colère" du shaman
     * @param i l'index du shaman
     * @param ennemi le monstre adverse
     * @param berserk la liste de berserk
     * @throws IOException toujours
     */
    public static void colere(int i, Monstre ennemi, float[] berserk) throws IOException {
        switch(rand.nextInt(5)){
            case 0 -> colere_boost();
            case 1, 2, 3 -> colere_attaque(ennemi);
            case 4 -> colere_berserk(i, berserk);
        }
    }

    /**
     * Chant de colère version attaque bonus
     * @throws IOException toujours
     */
    public static void colere_boost() throws IOException {
        int jet = Input.D8() + rand.nextInt(3) - 1;
        if(jet <= 3){
            System.out.println("Les esprits des ancients guerriers exalte vos actes.");
            System.out.println("Votre attaque augmente temporairement de 1 point.");
        }
        else if (jet <= 5){
            System.out.println("Les âmes de vos ancestres guerriers renforcent vos actes.");
            System.out.println("Votre attaque augmente temporairement de 2 points.");
        }
        else if (jet <= 8){
            System.out.println("L'âme d'un ancien guerrier guide votre main.");
            System.out.println("Votre attaque augmente temporairement de 4 points.");
        }
        else{
            System.out.println("Votre âme entre en symbiose avec celle de vos ancestres belliqueux.");
            System.out.println("Votre attaque augmente temporairement de 7 points.");
        }
    }

    /**
     * Chant de colère version dps
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    public static void colere_attaque(Monstre ennemi) throws IOException {
        System.out.println("Les esprits de vos ancêtres déchainent leur colère sur " + ennemi.getNom());
        int attaque;
        int jet = Input.D10() + rand.nextInt(3) - 1;
        if(jet <= 3){
            attaque = 2;
        }
        else if(jet <= 5){
            attaque = 4 + rand.nextInt(3);
        }
        else if(jet <= 7){
            attaque = 6 + rand.nextInt(4);
        }
        else{
            attaque = 9 + rand.nextInt(5);
            ennemi.affecte();
        }
        ennemi.dommage_magique(attaque);
    }

    /**
     * Chant de colère version berserk
     * @param i l'index du shaman
     * @param berserk la liste de berserk
     * @throws IOException toujours
     */
    public static void colere_berserk(int i, float[] berserk) throws IOException {
        int jet = Input.D10() + rand.nextInt(3) - 1;
        if (jet <= 3) {
            System.out.println("Un esprit vous emplie de colère.");
            berserk[i] += max(0.1f, jet * 0.1f);
        } else if (jet <= 5) {
            System.out.println("L'âme d'un guerrier emplie votre coeur de haine !");
            berserk[i] += jet * 0.2f + 0.1f;
        } else if (jet <= 7) {
            System.out.println("Une âme pleine de colère emplie votre coeur de rage !");
            berserk[i] += 1.4f + rand.nextInt(3) * 0.1f;
        } else {
            System.out.println("L'âme d'un ancien guerrier tombé au combat incruste votre coeur d'une haine féroce !");
            berserk[i] += 1.5f + rand.nextInt(3) * 0.2f;
        }
    }

    /**
     * Applique la compétence "appel des nuages" du shaman
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    public static void nuage(Monstre ennemi) throws IOException {
        switch(rand.nextInt(6)){
            case 0 -> nuage_pluie();
            case 1, 2 -> nuage_grele(ennemi);
            case 3, 4 -> nuage_brume(ennemi);
            case 5 -> nuage_foudre(ennemi);
        }
    }

    /**
     * Appel des nuages version soin
     * @throws IOException toujours
     */
    public static void nuage_pluie() throws IOException {
        System.out.println("Des nages apparaissent dans le ciel et une pluie légère commence à tomber.");
        int jet = Input.D10() + rand.nextInt(3) - 1;
        if (jet <= 2) {
            System.out.println("La pluie tombante recouvre vos blessures.");
            System.out.println("Chaque joueur et familier soigne de 2 points");
        }
        else if (jet <= 5) {
            System.out.println("Les goutes d'eau s'abatent sur vos blessures, qui commencent à se refermer.");
            System.out.println("Chaque joueur et familier soigne de " + jet + " points");
        }
        else if (jet <= 7) {
            System.out.println("Une pluie douce et appaissante vous recouvre.");
            System.out.println("Chaque joueur et familier soigne de " + (jet + 2) + " points");
        }
        else{
            System.out.println("Une force ancienne s'infiltre dans vos corps au travers les gouttes d'eau.");
            System.out.println("Chaque joueur et familier soigne de 10 points.");
            System.out.println("Chaque joueur et familier gagne temporairement 3 points de résistance.");
        }
    }

    /**
     * Appel des nuages version dps AOE
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    public static void nuage_grele(Monstre ennemi) throws IOException {
        System.out.println("De sombres nuages s'amoncèlent au dessus de vous");
        int jet = Input.D10() + rand.nextInt(3) - 1;
        if (jet <= 2) {
            System.out.println("Une fine grèle vous frappe.");
            System.out.println("Chaque joueur et familier subit 1 point de dommage.");
            ennemi.dommage(1);
        }
        else if (jet <= 5) {
            System.out.println("La grèle s'abat sur vous.");
            System.out.println("Chaque joueur et familier subit 2 points de dommage.");
            ennemi.dommage(jet - rand.nextInt(2));
        }
        else if (jet <= 7) {
            System.out.println("Une violente tempête se lève et la grèle vous frappe.");
            System.out.println("Chaque joueur et familier subit 4 points de dommage.");
            ennemi.dommage(jet + rand.nextInt(3) -1);
            if(rand.nextBoolean()){
                ennemi.do_etourdi();
            }
        }
        else{
            System.out.println("Une immense tempête de neige vous frappe de plein fouet.");
            System.out.println("Chaque joueur et familier subit 7 points de dommage.");
            ennemi.dommage(8 + rand.nextInt(5));
            if(rand.nextBoolean()){
                ennemi.affecte();
            }
        }
    }

    /**
     * Appel des nuages version debuff
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    public static void nuage_brume(Monstre ennemi) throws IOException {
        System.out.println("Un nuage apparait au dessus de vous et commence à se rapprocher du sol");
        int jet = Input.D10() + rand.nextInt(3) - 1;
        if (jet <= 2) {
            System.out.println("C'est... joli ?");
        }
        else if (jet <= 5) {
            System.out.println("La brûme commence à vous encercler.");
            System.out.println("Chaque joueur et familier perds temporairement 1 point d'attaque.");
            ennemi.bostAtk(-2, false);
        }
        else if (jet <= 7) {
            System.out.println("Un épais brouillard vous recouvre.");
            System.out.println("Chaque joueur et familier perds temporairement 3 points d'attaque.");
            ennemi.bostAtk(-5, false);
        }
        else{
            System.out.println("Une brûme vous entoure, si dense que vous ne vous voyez presque plus.");
            System.out.println("Il est désormais impossible de tirer.");
            System.out.println("Il est désormais impossible de lancer un sort ciblé sur une autre cible que soit-même.");
            System.out.println("Chaque joueur et familier perds temporairement 8 points d'attaque.");
            ennemi.bostAtk(-15, false);
        }
    }

    /**
     * Appel des nuages version dps debuff
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    public static void nuage_foudre(Monstre ennemi) throws IOException {
        System.out.println("Un nuage apparait au dessus de vous et commence à se rapprocher du sol");
        int jet = Input.D10() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("C'est... joli ?");
        }
        else if (jet <= 4) {
            System.out.println("Vous percevez un fugasse arc électrique.");
            ennemi.affecte();
        }
        else if (jet <= 6) {
            System.out.println("La foudre frappe l'ennemi.");
            ennemi.dommage(jet + rand.nextInt(5) - 3);
            ennemi.affecte();
        }
        else{
            System.out.println("Le nuage s'abat sur le monstre ennemi, suivi d'un éclair.");
            ennemi.dommage(jet + rand.nextInt(5) - 3);
            ennemi.affecte();
            ennemi.bostAtk(-2, false);
        }
    }

    /**
     * Applique la compétence "invocation des éléments" du shaman
     * @param ennemi le monstre adverse
     * @param assomme la liste des entités assommée
     * @param reveil la liste de réveil
     * @param alter_attaque le modificateur d'attaque
     * @param alter_tir le modificateur de tir
     * @throws IOException toujours
     */
    public static void element(Monstre ennemi, boolean[] assomme, int[] reveil, int[] alter_tir, int[] alter_attaque) throws IOException {
        switch(rand.nextInt(7)){
            case 0, 4 -> vent(ennemi, alter_tir, alter_attaque);
            case 1, 5 -> terre(ennemi);
            case 2, 6 -> feu(ennemi);
            case 3 -> eau(ennemi, assomme, reveil);
        }
    }

    /**
     * Invocation des éléments version buff tir
     * @param ennemi le monstre adverse
     * @param alter_attaque le modificateur d'attaque
     * @param alter_tir le modificateur de tir
     * @throws IOException toujours
     */
    public static void vent(Monstre ennemi, int[] alter_tir, int[] alter_attaque) throws IOException {
        System.out.println("Le vent se lève...");
        int jet = Input.D6() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("Une légère brise se fait sentir.");
        }
        else if (jet <= 4) {
            System.out.println("Une puissant vent souffle.");
            if(alter_tir[0] <= 2){
                alter_tir[0] = 2;
                alter_tir[1] = Main.nbj * 2;
            }
            System.out.println("Tous les tirs sont temporairement boostés.");
        }
        else if (jet <= 6) {
            System.out.println("De violente rafale se prononce.");
            if(alter_tir[0] <= 3){
                alter_tir[0] = 3;
                alter_tir[1] = Main.nbj * 2 + rand.nextInt(Main.nbj);
            }
            System.out.println("Tous les tirs sont temporairement boostés.");
            ennemi.dommage(2);
        }
        else{
            System.out.println("Le vent est si puissant que vous avez du mal à ne pas être emporté.");
            if(alter_tir[0] <= 5){
                alter_tir[0] = 5;
                alter_tir[1] = Main.nbj * 2 + rand.nextInt(Main.nbj * 3);
                alter_attaque[0] = -3;
                alter_attaque[1] = Main.nbj * 2;
            }
            System.out.println("Tous les tirs sont temporairement boostés.");
            System.out.println("Toutes les attaques sont temporairement baissés.");
            ennemi.dommage(5);
        }
    }

    /**
     * Invocation des éléments version debuff et dps
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    public static void terre(Monstre ennemi) throws IOException {
        System.out.println("La terre commence à trembler...");
        int jet = Input.D6() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("Vous entendez un léger grondement.");
        }
        else if (jet <= 4) {
            System.out.println("Des fragment de roches s'arrachent du sol et herte l'ennemi.");
            ennemi.dommage(3 + rand.nextInt(2));
            if(rand.nextBoolean()){
                ennemi.do_etourdi();
            }
        }
        else if (jet <= 6) {
            System.out.println("Le sol se fends sous l'ennemi !");
            ennemi.dommage(3 + rand.nextInt(2));
            if(rand.nextBoolean()){
                ennemi.do_etourdi();
            }
            else {
                ennemi.affecte();
            }
        }
        else{
            System.out.println("Le sol sous l'ennemi se soulève !");
            ennemi.dommage(4 + rand.nextInt(3));
            ennemi.affecte();
        }
    }

    /**
     * Invocation des éléments version dps
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    public static void feu(Monstre ennemi) throws IOException {
        System.out.println("Vous entendez de légers crépitements...");
        int jet = Input.D6() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("Must been the wind...");
        }
        else if (jet <= 3) {
            System.out.println("L'ennemi semble indisposé par quelque chose.");
            ennemi.dommage(2 + rand.nextInt(2));
        }
        else if (jet <= 5) {
            System.out.println("Le monstre prend feu !");
            ennemi.dommage(4 + rand.nextInt(3));
        }
        else if (jet <= 7) {
            System.out.println("Des flammes s'élève tout autour de l'adversaire !");
            ennemi.dommage(5 + rand.nextInt(5));
        }
        else{
            System.out.println("Un véritable brasier apparait aurour de l'ennemi !");
            ennemi.dommage(6 + rand.nextInt(7));
        }
    }

    /**
     * Invocation des éléments version buff/random
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    public static void eau(Monstre ennemi, boolean[] assomme, int[] reveil) throws IOException {
        System.out.println("Vous entendez un léger gargouillement...");
        int jet = Input.D6() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("Vous sentez quelques gouttes de pluie.");
        }
        else if (jet <= 3) {
            System.out.println("Une pluie purificatrice s'abat.");
            System.out.println("Tous les joueurs récupèrent 1PP.");
        }
        else if (jet <= 5) {
            System.out.println("De l'eau jaillit de sous l'adversaire, le faisant glisser.");
            ennemi.dommage(1 + rand.nextInt(2));
            ennemi.do_etourdi(); // glissade
        }
        else if (jet <= 7) {
            System.out.println("Une vague magique frappe l'ennemi !");
            ennemi.dommage_magique(2 + rand.nextInt(2));
            ennemi.affecte(); // affaiblissement magique
        }
        else {
            System.out.println("Un torrent mystique s'abat sur le terrain, emportant l'ennemi et réveillant les joueurs.");
            ennemi.dommage(4 + rand.nextInt(4));
            ennemi.affecte();
            Arrays.fill(assomme, false);
            Arrays.fill(reveil, 0);
        }
    }

    /**
     * Applique la compétence "bénédiction" du shaman
     * @throws IOException toujours
     */
    public static void benir() throws IOException {
        switch(rand.nextInt(7)){
            case 0, 1, 2 -> benir_soin();
            case 3, 4 -> benir_vie();
            case 5 -> benir_force();
            case 6 -> benir_def();
        }
    }

    /**
     * Bénédiction version soin
     * @throws IOException toujours
     */
    public static void benir_soin() throws IOException {
        int jet = Input.D8() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("La cible guérie de 2.");
        }
        else if (jet <= 4) {
            System.out.println("La cible guérie de " + (2 + jet) + ".");
        }
        else if (jet <= 6) {
            System.out.println("La cible guérie de 7.");
        }
        else if (jet <= 8) {
            System.out.println("La cible guérie de 9.");
        }
        else{
            System.out.println("La cible guérie de 11.");
        }
    }

    /**
     * Bénédiction version buff résistance
     * @throws IOException toujours
     */
    public static void benir_vie() throws IOException {
        int jet = Input.D8() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("La cible gagne temporairement 1 point de résistance.");
        }
        else if (jet <= 3) {
            System.out.println("La cible gagne temporairement 2 points de résistance.");
        }
        else if (jet <= 5) {
            System.out.println("La cible gagne temporairement 4 points de résistance.");
        }
        else if (jet <= 7) {
            System.out.println("La cible gagne temporairement 6 points de résistance.");
        }
        else{
            System.out.println("La cible gagne temporairement 8 points de résistance.");
        }
    }

    /**
     * Bénédiction version buff attaque
     * @throws IOException toujours
     */
    public static void benir_force() throws IOException {
        int jet = Input.D8() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("La cible gagne temporairement 1 point d'attaque.");
        }
        else if (jet <= 3) {
            System.out.println("La cible gagne temporairement 2 points d'attaque.");
        }
        else if (jet <= 5) {
            System.out.println("La cible gagne temporairement 3 points d'attaque.");
        }
        else if (jet <= 7) {
            System.out.println("La cible gagne temporairement 4 points d'attaque.");
        }
        else{
            System.out.println("La cible gagne temporairement 6 points d'attaque.");
        }
    }

    /**
     * Bénédiction version buff résistance/armure
     * @throws IOException toujours
     */
    public static void benir_def() throws IOException {
        int jet = Input.D8() + rand.nextInt(3) - 1;
        if(jet <= 3) {
            System.out.println("La cible gagne temporairement 1 point de résistance.");
        }
        else if (jet <= 6) {
            System.out.println("La cible gagne temporairement 3 points de résistance..");
        }
        else{
            System.out.println("La cible gagne temporairement 1 pont d'armure.");
        }
    }
}

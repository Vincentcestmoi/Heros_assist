package Metiers;

import Exterieur.Input;

import Enum.Metier;
import Enum.Position;
import Enum.Action;

import Monstre.Monstre;

import java.io.IOException;

public class Alchimiste extends Joueur {
    Metier metier = Metier.ALCHIMISTE;

    public Alchimiste(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
        vie = 4;
        attaque = 1;
        PP = "ingredient";
        PP_value = 3;
        PP_max = 11;
        caracteristique = "Dextérité";
        competences = "Fouille, Concoction";
    }

    public Metier getMetier() {
        return metier;
    }

    protected String nomMetier(){
        return "alchimiste";
    }

    @Override
    public String text_tour(){
        return "/(fo)uiller/(co)ncocter des potions";
    }

    @Override
    public boolean tour(String choix) throws IOException {
        if(choix.equalsIgnoreCase("fo")){
            fouille();
            return true;
        }
        if(choix.equalsIgnoreCase("co")){
            concocter();
            return true;
        }
        return false;
    }

    @Override
    public String text_action() {
        String text = super.text_action();
        if (!est_berserk()) {
            text += "/(co)ncocter des potions/(fo)uiller";
        }
        return text;
    }

    @Override
    public Action action(String choix, boolean est_familier) throws IOException {
        if(est_familier){
            return super.action(choix, true);
        }
        switch (choix) {
            case "fo" -> {
                if (!est_berserk()) {
                    return Action.FOUILLE;
                }
            }
            case "co" -> {
                if (!est_berserk()) {
                    return Action.CONCOCTION;
                }
            }
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi) throws IOException {
        switch (action) {
            case FOUILLE -> {
                if (!est_berserk()) {
                    fouille();
                    return false;
                }
            }
            case CONCOCTION -> {
                if (!est_berserk()) {
                    concocter();
                    return false;
                }
            }
        }
        return super.traite_action(action, ennemi);
    }

    public void fin_tour_combat(){

    }

    @Override
    public boolean peut_ressuciter() {
        return true;
    }

    @Override
    public boolean ressuciter(int malus) throws IOException {
        if(malus > 3){
            malus = 3;
        }
        if (Input.yn("Utilisez vous une potion divine ?")) {
            System.out.println("Résurection avec " + switch (Input.D6() - malus) {
                case 2 -> "2";
                case 3, 4 -> "4";
                case 5, 6 -> "6";
                case 7, 8 -> "8";
                default -> "1";
            } + " points de vie.");
            return true;
        }
        if (Input.yn("Utilisez vous un élixir ?")) {
            System.out.println("Résurection avec " + switch (Input.D20()) {
                case 4, 5, 6 -> "3 points de vie et 4";
                case 7, 8 -> "5 points de vie et 7";
                case 9, 10 -> "6 points de vie et 9";
                case 11, 12 -> "6 points de vie et 12";
                case 13, 14, 15 -> "7 points de vie et 13";
                case 16, 17 -> "7 points de vie et 14";
                case 18, 19 -> "9 points de vie et 14";
                case 20, 21, 22 -> "9 points de vie et 16";
                default -> "2 points de vie et 3";
            } + " points de résistance additionels.");
            return true;
        }
        System.out.println("Vous n'avez aucun moyen de ressuciter la cible.");
        return false;
    }

    /**
     * Indique le résultat de la compétence "fouille"
     */
    public static void fouille() throws IOException {
        System.out.println("Vous chercher autour de vous tout ce qui pourrait être utile pour vos potions.");
        int temp = Input.D20();
        if (temp <= 10 + rand.nextInt(10)) {
            System.out.println("Vous ne trouvez rien.");
        }
        else if (temp <= 18 + rand.nextInt(4)) {
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
    
    @Override
    protected void monstre_mort_perso(Monstre ennemi) throws IOException{
        if (ennemi.corps_utilisable() && est_actif() && est_vivant()) {
            if (Exterieur.Input.yn("Voulez vous dissequer le cadavre ?")) {
                ennemi.alterEtat(dissection(ennemi.getEtat()));
            }
        }
    }

}
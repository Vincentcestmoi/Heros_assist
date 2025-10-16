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
            text += "/(co)ncocter des potions/(fo)uiller/(de)xterité";
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
            case "de", "dé" -> {
                if (!est_berserk()) {
                    return Action.DEXTERITE;
                }
            }
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi, int bonus_popo) throws IOException {
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
            case DEXTERITE -> {
                if (!est_berserk()) {
                    dexterite(ennemi, bonus_popo);
                    return false;
                }
            }
        }
        return super.traite_action(action, ennemi, bonus_popo);
    }

    public boolean action_consomme_popo(Action action){
        if(action == Action.DEXTERITE){
            return true;
        }
        return super.action_consomme_popo(action);
    }
    
    @Override
    public int popo() throws IOException {
        System.out.println(nom + """
                ,quelle type de potion utilisez vous :
                1 : Soin (PV)
                2 : Résistance (RES)
                3 : Force (ATK)
                4 : Poison (P)
                5 : Explosive (E)
                6 : Energétique (PP)
                7 : Divine (Div)
                8 : Aucune/Custom""");
        int temp = Input.readInt();
        if(temp <= 0 || temp > 8){
            System.out.println("Unknow input.");
            return popo();
        }
        return switch (temp) {
            case 1 -> local_popo_soin();
            case 2 -> local_popo_res();
            case 3 -> local_popo_force();
            case 4 -> {
                System.out.println("Vous vous dites qu'il serait contre-productif d'enduire votre lame de poison et de lancer une" +
                        " potion explosive en même temps.");
                yield popo_cd() + super.popo();
            }
            case 5 -> {
                System.out.println("Vous vous dites qu'il serait contre-productif d'enduire votre lame de poison et de lancer une" +
                        " potion explosive en même temps.");
                yield popo_instable() + super.popo();
            }
            case 6 -> local_popo_mana();
            case 7 -> local_popo_divine();
            case 8 -> 0;
            default -> {
                System.out.println("Unknow input");
                yield popo();
            }
        };
    }

    /**
     * Calcule et traite les soin
     * @throws IOException toujours
     */
    protected int local_popo_soin() throws IOException {
        System.out.println("""
                Ciblez une joueur ou familier (ou vous même) et entrez la potion que vous utilisez :
                1 : potion insipide         (PV#1)
                2 : potion de vie           (PV#2)
                3 : potion de santé         (PV#3)
                4 : fortifiant              (PV#4)
                5 : potion de régénération  (PV#5)
                6 : aucune (reviens au choix des potions))""");
        int temp = Input.readInt();
        if (temp <= 0 || temp > 7) {
            System.out.println("Unknow input");
            return local_popo_soin();
        }
        int soin = 0;
        switch (temp) {
            case 1 -> soin = 1;
            case 2 -> soin = 3 + rand.nextInt(2);
            case 3 -> soin = 5 + rand.nextInt(3);
            case 4 -> soin = 7 + rand.nextInt(4);
            case 5 -> {
                System.out.println("La cible est soignée de " + (9 + rand.nextInt(5)) + ".");
                return 0;
            }
            case 6 -> {
                return popo();
            }
        }
        System.out.println("La cible est soignée de " + soin + ".");
        return super.popo();
    }
    
    /**
     * Calcule et traite les bonus de résistance
     * @throws IOException toujours
     */
    protected int local_popo_res() throws IOException {
        System.out.println("""
                Ciblez une joueur (ou vous même) et entrez la potion que vous utilisez :
                1 : potion de vigueur           (RES#1)
                2 : potion de résistance        (RES#2)
                3 : potion de solidification    (RES#3)
                4 : aucune (reviens au choix des potions))""");
        int temp = Input.readInt();
        if (temp <= 0 || temp > 4) {
            System.out.println("Unknow input");
            return local_popo_res();
        }
        int res = 0;
        switch (temp) {
            case 1 -> res = 3 + rand.nextInt(2);
            case 2 -> res = 4 + rand.nextInt(3);
            case 3 -> {
                System.out.println("La cible gagne temporairement " + (4 + rand.nextInt(2)) + " points de résistance" +
                        " et 1 point d'armure.");
                return 0;
            }
            case 4 -> {
                return popo();
            }
        }
        System.out.println("La cible gagne temporairement " + res + " points de résistance.");
        return super.popo();
    }
    
    /**
     * Calcule et traite les bonus d'attaque
     * @throws IOException toujours
     */
    protected int local_popo_force() throws IOException {
        System.out.println("""
                Ciblez une joueur (ou vous même) et entrez la potion que vous utilisez :
                1 : potion de force     (ATK#1)
                2 : potion de puissance (ATK#2)
                3 : potion du colosse   (ATK#3)
                4 : aucune (reviens au choix des potions))""");
        int temp = Input.readInt();
        if (temp <= 0 || temp > 4) {
            System.out.println("Unknow input");
            return local_popo_force();
        }
        int force = 0;
        switch (temp) {
            case 1 -> force = 2 + rand.nextInt(2);
            case 2 -> force = 3 + rand.nextInt(3);
            case 3 -> force = 4 + rand.nextInt(4);
            case 4 -> {
                return popo();
            }
        }
        System.out.println("La cible gagne temporairement " + force + " points d'attaque.");
        return super.popo();
    }

    /**
     * Calcule et traite la régénèration du mana
     * @throws IOException toujours
     */
    protected int local_popo_mana() throws IOException {
        System.out.println("""
                Ciblez une joueur et entrez la potion que vous utilisez :
                1 : potion énergétique  (PP#1)
                2 : potion d'énergie    (PP#2)
                3 : potion de mana      (PP#3)
                4 : potion ancestrale   (PP#4)
                5 : aucune (reviens au choix des potions))""");
        int temp = Input.readInt();
        if (temp <= 0 || temp > 5) {
            System.out.println("Unknow input");
            return local_popo_mana();
        }
        int PP = 0;
        switch (temp) {
            case 1 -> PP = 1 + rand.nextInt(3);
            case 2 -> PP = 3 + rand.nextInt(3);
            case 3 -> PP = 5 + rand.nextInt(3);
            case 4 -> {
                PP = 8 + rand.nextInt(7);
                System.out.println("Le système de mana de la cible s'agrandit temporairement et peut stocker 5PP supplémentaires.");
            }
            case 5 -> {
                return popo();
            }
        }
        System.out.println("La cible récupère " + PP + "PP.");
        return 0;
    }

    /**
     * Calcule et traite les effets des deux potions spéciale
     * @throws IOException toujours
     */
    protected int local_popo_divine() throws IOException {
        System.out.println("""
                Ciblez une joueur et entrez la potion que vous utilisez :
                1 : potion divine   (Div#A)
                2 : potion élixir   (Div#B)
                3 : aucune (reviens au choix des potions))""");
        int temp = Input.readInt();
        if (temp <= 0 || temp > 3) {
            System.out.println("Unknow input");
            return local_popo_divine();
        }
        switch (temp) {
            case 1 -> System.out.println("La cible est guérie de " + (3 + rand.nextInt(3)) + ", gagne temporairement " +
                    (5 + rand.nextInt(5)) + " points de résistance et " + (2 + rand.nextInt(3)) + " points d'attaques.");
            case 2 -> elixir();
            case 3 -> {
                return popo();
            }
        }
        return 0;
    }
    
    private void elixir() throws IOException {
        System.out.println("La cible est guérie de " + switch (Input.D20()) {
            case 4, 5, 6 -> "5 et gagne temporairement 6";
            case 7, 8 -> "6 et gagne temporairement 8";
            case 9, 10 -> "7 et gagne temporairement 10";
            case 11, 12 -> "8 et gagne temporairement 12";
            case 13, 14, 15 -> "9 et gagne temporairement 15";
            case 16, 17 -> "10 et gagne temporairement 17";
            case 18, 19 -> "11 et gagne temporairement 19";
            case 20, 21, 22 -> "12 et gagne temporairement 22";
            default -> "4 et gagne temporairement 3";
        } + " points de résistance additionels.");
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
     * Applique la compétence "dextérité" de l'alchimiste : utiliser 2 potions additionelles
     * @throws IOException toujours
     */
    private void dexterite(Monstre ennemi, int popo_bonus) throws IOException {
        popo_bonus += popo() + popo();
        ennemi.dommage(popo_bonus);
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
            System.out.println("Vous avez produit une potion insipide (PV#1).");
        }
        else if (jet < 14) {
            System.out.println("Vous avez produit une potion de vigeur (RES#1).");
        }
        else if (jet < 19) {
            System.out.println("Vous avez produit une potion de résistance (RES#2).");
        }
        else{
            System.out.println("Vous avez produit une potion de solidification (RES#3).");
        }
    }

    /**
     * Réalise une potion de difficulté 10 ou moins
     * @throws IOException toujours
     */
    public static void concoc_alea() throws IOException {

        int[] popo_cost = {1, 1, 5, 8, 4, 9, 9, 6, 10};
        String[] popo = {"potion douteuse (P#1)", "potion insipide (PV#1)", "potion toxique (P#2)",
                "potion de poison (P#3)", "potion instable (E#1)", "potion de feu (E#2)", "de force (ATK#1)",
                "potion de vie (PV#2)", "potion énergétique (PP#1)"};

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
            System.out.println("Vous avez produit une potion insipide (PV#1).");
        }
        else if (jet < 15) {
            System.out.println("Vous avez produit une potion de santé (PV#3).");
        }
        else {
            System.out.println("Vous avez produit une potion divine (Div#A).");
        }
    }

    /**
     * Réalise des potions
     * @throws IOException toujours
     */
    public static void concoc_serie() throws IOException {

        int[] popo_cost = {1, 1, 5, 8, 4, 9, 9, 6, 10, 11, 13, 11, 14, 14, 11, 14, 15, 15};
        String[] popo = {"potion douteuse (P#1)", "potion insipide (PV#1)", "potion toxique (P#2)", "potion de poison (P#3)",
                "potion instable (E#1)", "potion de feu (E#2)", "potion de force (ATK#1)", "potion de vie (PV#2)",
                "potion énergétique (PP#1)", "potion de santé (PV#3)", "potion d'énergie (PP#2)", "potion de vigeur (RES#1)",
                "potion de résistance (RES#2)", "potion de puissance (ATK#2)", "flasque nécrosé (P#4)", "potion nécrotyque (P#5)",
                "potion explosive (E#4)", "potion divine (Div#A)"};


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
            System.out.println("Vous avez produit une potion insipide (PV#1).");
        }
        else if (jet < 13) {
            System.out.println("Vous avez produit une potion énergétique (PP#1).");
        }
        else if (jet < 18) {
            System.out.println("Vous avez produit une potion d'énergie (PP#2).");
        }
        else if (jet < 20) {
            System.out.println("Vous avez produit une potion de mana (PP#3).");
        }
        else {
            System.out.println("Vous avez produit une potion ancestrale (PP#4).");
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
            System.out.println("Vous avez produit une potion insipide (PV#1).");
        }
        else if (jet < 14) {
            System.out.println("Vous avez produit une potion de force (ATK#1).");
        }
        else if (jet < 16) {
            System.out.println("Vous avez produit une potion de puissance (ATK#2).");
        }
        else {
            System.out.println("Vous avez produit une potion du colosse (ATK#3).");
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
            System.out.println("Vous avez produit une potion douteuse (P#1).");
        }
        else if (jet < 9) {
            System.out.println("Vous avez produit une potion instable (E#1).");
        }
        else if (jet < 15) {
            System.out.println("Vous avez produit une potion de feu (E#2).");
        }
        else if (jet < 18) {
            System.out.println("Vous avez produit une potion explosive (E#3).");
        }
        else {
            System.out.println("Vous avez produit une bombe (E#4).");
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
            System.out.println("Vous avez produit une potion insipide (PV#1).");
        }
        else if (jet < 11) {
            System.out.println("Vous avez produit une potion de vie (PV#2).");
        }
        else if (jet < 16) {
            System.out.println("Vous avez produit une potion de santé (PV#3).");
        }
        else if (jet < 20) {
            System.out.println("Vous avez produit un fortifiant (PV#4).");
        }
        else {
            System.out.println("Vous avez produit une potion de régénération (PV#5).");
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
            System.out.println("Vous avez produit une potion douteuse (P#1).");
        }
        else if (jet < 8) {
            System.out.println("Vous avez produit une potion toxique (P#2).");
        }
        else if (jet < 11) {
            System.out.println("Vous avez produit une potion de poison (P#3).");
        }
        else if (jet < 14) {
            System.out.println("Vous avez produit une flasque nécrosé (P#4).");
        }
        else {
            System.out.println("Vous avez produit une potion nécrotyque (P#5).");
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
            System.out.println("Vous avez produit une potion insipide (PV#1).");
        }
        else if (jet < 21) {
            System.out.println("Vous avez produit une potion de santé (PV#3).");
        }
        else {
            System.out.println("Vous avez produit un élixir (Div#B).");
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
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.max;

public class Sort {

    static Input input = new Input();
    static Random rand = new Random();

    static public int nb_fire_rune = 0;
    static public int nb_great_fire_rune = 0;

    /**
     * Applique l'effet de la compétence "onde de choc"
     * @param actif une liste de boolean indiquant les participants encore en jeu
     * @param nom les noms des participants
     * @param assomme une liste de boolean indiquant les participants inconscients
     * @param reveil le tableau de niveau de réveil des participants
     * @param ennemi le monstre ennemi
     * @param lanceur l'index du lanceur de sort
     * @throws IOException toujours
     */
    static public void onde_choc(boolean[] actif, String[] nom, boolean[] assomme, int[] reveil, Monstre ennemi, int lanceur) throws IOException {

        // sur les participants
        for(int i = 0; i < nom.length; i++){
            if(i != lanceur && actif[i]){
                System.out.println(nom[i] + " est frappé par l'onde de choc.");
                if(i <= 4){
                    if(input.D6() < 2 + rand.nextInt(5)){
                        System.out.println(nom[i] + " perd connaissance.");
                        assomme[i] = true;
                        reveil[i] = 0;
                    }
                    else{
                        System.out.println(nom[i] + " parvient à rester conscient.");
                    }
                }
                else{
                    if(input.D4() <= 3 + rand.nextInt(2)){
                        System.out.println(nom[i] + " perd connaissance.");
                        assomme[i] = true;
                        reveil[i] = 0;
                    }
                    else{
                        System.out.println(nom[i] + " parvient à rester conscient.");
                    }
                }
            }
        }

        // sur l'ennemi
        System.out.println(ennemi.nom + " est frappé par l'onde de choc.");
        System.out.print(Main.nom[lanceur] + " : ");
        switch (input.D6()){
            case 2 -> ennemi.do_etourdi();
            case 3, 4 -> ennemi.affecte();
            case 5, 6 -> ennemi.do_assomme();
            default -> System.out.println(ennemi.nom + " n'a pas l'air très affecté...");
        }
    }

    /**
     * Applique l'effet de la compétence "malédiction"
     * @param ennemi la cible de la malédiction
     * @throws IOException toujours
     */
    public static void maudir(Monstre ennemi) throws IOException {
        int boost = rand.nextInt(3);
        switch (input.D6()){
            case 2 -> {
                System.out.println("Vous maudissez faiblement " + ennemi.nom + ".");
                ennemi.vie_max -= 1 + boost;
                ennemi.vie -= 1 + boost;
            }
            case 3, 4 -> {
                System.out.println("Vous maudissez " + ennemi.nom + ".");
                ennemi.vie_max -= 2 + boost;
                ennemi.vie -= 2 + boost;
            }
            case 5 -> {
                System.out.println("Vous maudissez agressivement " + ennemi.nom + ".");
                ennemi.vie_max -= 3 + boost;
                ennemi.vie -= 3 + boost;
            }
            case 6 -> {
                System.out.println("Vous maudissez puissament " + ennemi.nom + ".");
                ennemi.vie_max -= 5 + boost;
                ennemi.vie -= 5 + boost;
            }
            default -> System.out.println("vous n'arrivez pas à maudir " + ennemi.nom + ".");
        }
    }

    /**
     * Applique l'effet de la compétence "malédiction"
     * @throws IOException toujours
     */
    public static void maudir() throws IOException {
        int boost = rand.nextInt(3);
        switch (input.D6()){
            case 2 -> System.out.println("Votre cible perds définitivement " + (1 + boost) + " point(s) de résistance.");
            case 3, 4 -> System.out.println("Votre cible perds définitivement " + (2 + boost) + " points de résistance.");
            case 5 -> System.out.println("Votre cible perds définitivement " + (3 + boost) + " points de résistance.");
            case 6, 7 -> System.out.println("Votre cible perds définitivement " + (5 + boost) + " points de résistance.");
            default -> System.out.println("vous n'arrivez pas à maudir votre cible.");
        }
    }

    /**
     * Affiche les bienfaits de la méditation
     * @throws IOException toujours
     */
    public static void meditation() throws IOException {
        int jet = input.D8() + rand.nextInt(3) - 1;
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
        int mana = input.readInt();
        int jet = input.D4() + mana + rand.nextInt(3) - 1 + (nb_fire_rune / 2) + (nb_great_fire_rune / 2);
        int dmg;
        if (jet <= (2  + nb_great_fire_rune) || mana < (2  + nb_great_fire_rune)) {
            System.out.println("Le sort ne fonctionne pas .");
            return;
        }
        else if (jet == 3) {
            System.out.println("Vous lancez une pitoyable boule de feu sur " + ennemi.nom + ".");
            dmg = 3;
        }
        else if (jet == 4) {
            System.out.println("Vous lancez une boule de feu sur " + ennemi.nom + ".");
            dmg = 5;
        }
        else if (jet <= 6) {
            System.out.println("Vous lancez une boule de feu sur " + ennemi.nom + ".");
            dmg = 6;
        }
        else if (jet <= 8) {
            System.out.println("Vous lancez une impressionnante boule de feu sur " + ennemi.nom + ".");
            dmg = 8;
        }
        else if (jet <= 10) {
            System.out.println("Un brasier s'abat sur " + ennemi.nom + " !");
            dmg = 11;
        }
        else if (jet == 11) {
            System.out.println("Un brasier s'abat sur " + ennemi.nom + " !");
            dmg = 13;
            if(rand.nextBoolean()){
                ennemi.affecte();
            }
        }
        else if (jet == 12) {
            System.out.println("Une tornade de flamme s'abat violement sur " + ennemi.nom + " !");
            dmg = 15;
            if(rand.nextBoolean()){
                ennemi.affecte();
            }
        }
        else if (jet == 13) {
            System.out.println("Une tornade de flamme s'abat violement sur " + ennemi.nom + " !");
            dmg = 16;
            if(rand.nextBoolean()){
                ennemi.affecte();
            }
        }
        else{
            System.out.println("Les flammes de l'enfers brûlent intensemment " + ennemi.nom + ".");
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
        int mana = input.readInt();
        int jet = input.D8() + mana + rand.nextInt(3) - 1;
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
        int mana = input.readInt();
        int jet = input.D12() + mana + rand.nextInt(3) - 1;
        int dmg;
        if (jet <= 7 || mana < 7) {
            System.out.println("Le sort ne fonctionne pas.");
            return;
        }
        else if (jet <= 10) {
            System.out.println("Un arc électrique vient frapper " + ennemi.nom + ".");
            dmg = 12;
        }
        else if (jet <= 12) {
            System.out.println("Un arc électrique vient frapper " + ennemi.nom + ".");
            dmg = 13;
        }
        else if (jet <= 14) {
            System.out.println("Un éclair s'abat sur " + ennemi.nom + ".");
            dmg = 16;
            if(rand.nextBoolean()){
                ennemi.affecte();
            }
        }
        else if (jet <= 16) {
            System.out.println("Un puissant éclair s'abat sur " + ennemi.nom + ".");
            dmg = 18;
            if(rand.nextBoolean()){
                ennemi.affecte();
            }
        }
        else if (jet <= 18) {
            System.out.println("Un puissant éclair s'abat sur " + ennemi.nom + ".");
            dmg = 20;
            ennemi.affecte();
        }
        else if (jet == 19){
            System.out.println("Le ciel s'illumine un instant et un gigantesque éclair s'abat sur  " + ennemi.nom + " dans un immense fracas.");
            dmg = 22;
            ennemi.affecte();
        }
        else if (jet == 20){
            System.out.println("Le ciel s'illumine un instant et un gigantesque éclair s'abat sur  " + ennemi.nom + " dans un immense fracas.");
            dmg = 24;
            ennemi.affecte();
        }
        else if (jet == 21) {
            System.out.println("Un déchainement de pure énergie fend l'espace entre les cieux et la terre et vient percuter " + ennemi.nom + " de plein fouet.");
            dmg = 25;
            if(rand.nextBoolean()){
                ennemi.affecte();
            }
            else {
                ennemi.do_assomme();
            }
        }
        else{
            System.out.println("Un déchainement de pure énergie fend l'espace entre les cieux et la terre et vient percuter " + ennemi.nom + " de plein fouet.");
            dmg = 27;
            ennemi.do_assomme();
        }
        ennemi.dommage_magique(dmg);
    }

    /**
     * Indique le résultat de la compétence "fouille"
     */
    public static void fouille() throws IOException {
        System.out.println("Vous chercher autour de vous tout ce qui pourrait être utile pour vos potions.");
        int temp = input.D20();
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
     * Indique le résultat de la compétence "appel des morts"
     * @param position la position du nécromancien
     * @return si le nécromancien a réussi à appeler un mort
     * @throws IOException toujours
     */
    public static boolean necromancie(Position position) throws IOException {
        Monstre l1, l2;
        //invocation selon le lieu
        switch (position) {
            case ENFERS -> {
                l1 = Lieu.true_enfers();
                l2 = Lieu.true_enfers();
            }
            case PRAIRIE -> {
                l1 = Lieu.true_prairie();
                l2 = Lieu.true_enfers();
            }
            case VIGNES -> {
                l1 = Lieu.true_vigne();
                l2 = Lieu.true_prairie();
            }
            case TEMPLE -> {
                l1 = Lieu.true_temple();
                l2 = Lieu.true_vigne();
            }
            case MER -> {
                l1 = Lieu.true_mer();
                l2 = Lieu.true_temple();
            }
            case MONTS -> {
                l1 = Lieu.true_mont();
                l2 = Lieu.true_mer();
            }
            case OLYMPE -> {
                l1 = Lieu.true_mont();
                l2 = Lieu.true_mont();
            }
            case ASCENDANT -> {
                System.out.println("ERROR :DONOT");
                return false;
            }
            default -> {
                l1 = Lieu.true_prairie();
                l2 = l1;
            }
        }
        System.out.println("Vous rappelez à la vie les cadavres de ces terres.");
        System.out.println("Combien de PP mettez vous dans le sort ? (min 3) : ");
        int mana = input.readInt();

        // selection du monstre a ressuciter
        int jet = input.D6() + mana + rand.nextInt(2) - 1;
        Monstre rez;
        if (jet <= 3 || mana < 3) {
            System.out.println("Le sort a échoué.");
            return false;
        } else if (jet <= 10) {
            rez = l2;
        } else {
            rez = l1;
        }
        System.out.println("Vous ressentez la réponse d'une âme à travers le sol.");

        //selection de la puissance du monstre
        jet = input.D8() + mana + rand.nextInt(2) - 1 + Monstre.corriger(jet * 0.4F);
        if (jet <= 7) {
            System.out.println("nouveau familier : carcasse réanimée");
            System.out.println("attaque : " + (int)max(rez.attaque *0.25, 1));
            System.out.println("vie : " + (int)max(rez.vie_max *0.25, 1));
            System.out.println("armure : " + (int)(rez.armure *0.25));
        }
        else if (jet <= 10) {
            System.out.println("nouveau familier : esprit désincarné");
            System.out.println("attaque : " + rez.attaque);
            System.out.println("vie : " + (int)max(rez.vie_max *0.25, 1));
            System.out.println("armure : 0");
        }
        else if (jet <= 12) {
            System.out.println("nouveau familier : zombie");
            System.out.println("attaque : " + max(rez.attaque / 3, 1));
            System.out.println("vie : " + rez.vie_max);
            System.out.println("armure : " + (int)(rez.armure * 0.75));
        }
        else if (jet <= 14) {
            System.out.println("nouveau familier : ancien squelette");
            System.out.println("attaque : " + (int)max(0.75 * rez.attaque, 1));
            System.out.println("vie : " + rez.vie_max);
            System.out.println("armure : " + rez.armure);
        }
        else {
            System.out.println("nouveau familier : ancien gardien");
            System.out.println("attaque : " + (int)(rez.attaque * 1.2));
            System.out.println("vie : " + (int)(rez.vie_max * 1.2));
            System.out.println("armure : " + (int)(max(rez.armure * 1.2, 1)));
        }
        return true;
    }

    /**
     * Indique le résultat de la compétence "dissection"
     * @param etat l'état du cadavre
     * @return le changement d'état du corps
     * @throws IOException toujours
     */
    public static int dissection(int etat) throws IOException {
        int temp = input.D6();
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
     * @param reveil le tableau indiquand à quel point les participant sont près de se réveillé
     * @param ennemi le monstre ennemi
     * @param lanceur l'indice de l'archimage
     * @throws IOException toujours
     */
    public static void sort(boolean[] actif, String[] nom, boolean[] assomme, int[] reveil, Monstre ennemi, int lanceur) throws IOException {
        extracted(actif, nom, assomme, reveil, ennemi, lanceur);
        if(ennemi.est_mort()){
            return;
        }
        System.out.println("Vous préparez votre second sort.");
        extracted(actif, nom, assomme, reveil, ennemi, lanceur);
    }

    /**
     * Fonction auxiliaire de sort
     */
    private static void extracted(boolean[] actif, String[] nom, boolean[] assomme, int[] reveil, Monstre ennemi, int lanceur) throws IOException {
        switch (input.sort()){
            case BDF -> boule_de_feu(ennemi);
            case ONDE_CHOC -> onde_choc(actif, nom, assomme, reveil, ennemi, lanceur);
            case ADG -> armure_de_glace();
            case FOUDRE -> foudre(ennemi);
            case AUTRE -> ennemi.dommage_magique(input.magie());
        }
    }

    /**
     * Laisse l'alchimiste concoter ses potions
     * @throws IOException toujours
     */
    public static void concocter() throws IOException {
        switch (input.concoction()) {
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
        int ingre = input.readInt();
        int jet = input.D10() + ingre + rand.nextInt(3) - 1;
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
        int ingre = input.readInt();
        int concoc = input.D4() + ingre + 2 + rand.nextInt(3) - 1;
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
        int ingre = input.readInt();
        int jet = input.D10() + ingre + rand.nextInt(3) - 1;
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
        int ingre = input.readInt();
        int concoc = input.D6() + ingre + rand.nextInt(3) - 1;


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
        int ingre = input.readInt();
        int jet = input.D10() + ingre + rand.nextInt(3) - 1;
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
        int ingre = input.readInt();
        int jet = input.D10() + ingre + rand.nextInt(3) - 1;
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
        int ingre = input.readInt();
        int jet = input.D10() + ingre + rand.nextInt(3) - 1;
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
        int ingre = input.readInt();
        int jet = input.D10() + ingre + rand.nextInt(3) - 1;
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
        int ingre = input.readInt();
        int jet = input.D10() + ingre + rand.nextInt(3) - 1;
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
        int ingre = input.readInt();
        int jet = input.D10() + ingre + rand.nextInt(3) - 1;
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
}

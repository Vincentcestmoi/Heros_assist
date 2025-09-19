import java.io.IOException;
import java.util.Random;

import static java.lang.Math.max;

public class Sort {

    static Input input = new Input();
    static Random rand = new Random();

    /**
     * Applique l'effet de la compétence "onde de choc"
     * @param actif une liste de boolean indiquant les participants encore en jeu
     * @param nom les noms des participants
     * @param assomme une liste de boolean indiquant les participants inconscients
     * @param ennemi le monstre ennemi
     * @throws IOException toujours
     */
    static public void onde_choc(boolean[] actif, String[] nom, boolean[] assomme, Monstre ennemi) throws IOException {

        // sur les participants
        for(int i = 0; i < nom.length; i++){
            if(!nom[i].equals(Main.archimage) && actif[i]){
                System.out.println(nom[i] + " est frappé par l'onde de choc.");
                if(i <= 4){
                    if(input.D6() < 2 + rand.nextInt(5)){
                        System.out.println(nom[i] + " perd connaissance.\n");
                        assomme[i] = true;
                    }
                    else{
                        System.out.println(nom[i] + " parvient à rester conscient.\n");
                    }
                }
                else{
                    if(input.D4() <= 3 + rand.nextInt(2)){
                        System.out.println(nom[i] + " perd connaissance.\n");
                        assomme[i] = true;
                    }
                    else{
                        System.out.println(nom[i] + " parvient à rester conscient.\n");
                    }
                }
            }
        }

        // sur l'ennemi
        System.out.println(ennemi.nom + " est frappé par l'onde de choc.");
        System.out.print(Main.archimage + " : ");
        switch (input.D6()){
            case 2 -> ennemi.do_etourdi();
            case 3, 4 -> ennemi.affecte();
            case 5, 6 -> ennemi.do_assomme();
            default -> System.out.println(ennemi.nom + " n'a pas l'air très affecté...\n");
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
                System.out.println("Vous maudissez faiblement " + ennemi.nom + ".\n");
                ennemi.vie_max -= 1 + boost;
                ennemi.vie -= 1 + boost;
            }
            case 3, 4 -> {
                System.out.println("Vous maudissez " + ennemi.nom + ".\n");
                ennemi.vie_max -= 2 + boost;
                ennemi.vie -= 2 + boost;
            }
            case 5 -> {
                System.out.println("Vous maudissez agressivement " + ennemi.nom + ".\n");
                ennemi.vie_max -= 3 + boost;
                ennemi.vie -= 3 + boost;
            }
            case 6 -> {
                System.out.println("Vous maudissez puissament " + ennemi.nom + ".\n");
                ennemi.vie_max -= 5 + boost;
                ennemi.vie -= 5 + boost;
            }
            default -> System.out.println("vous n'arrivez pas à maudir " + ennemi.nom + ".\n");
        }
    }

    /**
     * Applique l'effet de la compétence "malédiction"
     * @throws IOException toujours
     */
    public static void maudir() throws IOException {
        int boost = rand.nextInt(3);
        switch (input.D6()){
            case 2 -> System.out.println("Votre cible perds définitivement " + (1 + boost) + " point(s) de résistance.\n");
            case 3, 4 -> System.out.println("Votre cible perds définitivement " + (2 + boost) + " points de résistance.\n");
            case 5 -> System.out.println("Votre cible perds définitivement " + (3 + boost) + " points de résistance.\n");
            case 6, 7 -> System.out.println("Votre cible perds définitivement " + (5 + boost) + " points de résistance.\n");
            default -> System.out.println("vous n'arrivez pas à maudir votre cible.\n");
        }
    }

    /**
     * Affiche les bienfaits de la méditation
     * @throws IOException toujours
     */
    public static void meditation() throws IOException {
        int jet = input.D8() + rand.nextInt(3) - 1;
        if (jet <= 2) {
            System.out.println("Vous récupérez 2PP.\n");
        }
        else if(jet <= 4) {
            System.out.println("Vous récupérez 3PP.\n");
        }
        else if(jet <= 7) {
            System.out.println("Vous récupérez 4PP.\n");
        }
        else{
            System.out.println("Vous récupérez 5PP.\n");
        }
    }

    /**
     * Calcule et applique les dommages de la compétence "boule de feu"
     * @param ennemi la cible du sort
     * @throws IOException toujours
     */
    public static void boule_de_feu(Monstre ennemi) throws IOException {
        System.out.println("Vous vous préparez à lancer une boule de feu.");
        System.out.println("Combien de PP mettez vous dans le sort ? (min 2)");
        int mana = input.readInt();
        int jet = input.D4() + mana + rand.nextInt(3) - 1;
        int dmg;
        if (jet <= 2 || mana < 2) {
            System.out.println("Le sort ne fonctionne pas .\n");
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
            System.out.println("Vous lancez une impressionnante boule de feu sur" + ennemi.nom + ".");
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
            System.out.println("Le sort ne fonctionne pas.\n");
        } else if (jet <= 6) {
            System.out.println("La cible gagne 3 points de résistance.\n");
        } else if (jet <= 9) {
            System.out.println("La cible gagne 5 points de résistance.\n");
        } else if (jet <= 12) {
            System.out.println("La cible gagne 6 points de résistance et 1 point d'armure.\n");
        } else if (jet == 15) {
            System.out.println("La cible gagne 8 points de résistance et 1 point d'armure.\n");
        } else if (jet == 16) {
            System.out.println("La cible gagne 9 points de résistance et 1 point d'armure.\n");
        } else if (jet == 17) {
            System.out.println("La cible gagne 10 points de résistance et 1 point d'armure.\n");
        } else {
            System.out.println("La cible gagne 10 points de résistance et 2 point d'armure.\n");
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
            System.out.println("Le sort ne fonctionne pas.\n");
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
        int temp = input.readInt();
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
        Monstre l1, l2, l3;
        //invocation selon le lieu
        switch (position) {
            case ENFERS -> {
                l1 = Lieu.true_enfers();
                l2 = Lieu.true_enfers();
                l3 = Lieu.true_enfers();
            }
            case PRAIRIE -> {
                l1 = Lieu.true_prairie();
                l2 = Lieu.true_enfers();
                l3 = Lieu.true_enfers();
            }
            case VIGNES -> {
                l1 = Lieu.true_vigne();
                l2 = Lieu.true_prairie();
                l3 = Lieu.true_enfers();
            }
            case TEMPLE -> {
                l1 = Lieu.true_temple();
                l2 = Lieu.true_vigne();
                l3 = Lieu.true_prairie();
            }
            case MER -> {
                l1 = Lieu.true_mer();
                l2 = Lieu.true_temple();
                l3 = Lieu.true_vigne();
            }
            case MONTS -> {
                l1 = Lieu.true_mont();
                l2 = Lieu.true_mer();
                l3 = Lieu.true_temple();
            }
            case OLYMPE -> {
                l1 = Lieu.true_mont();
                l2 = Lieu.true_mont();
                l3 = Lieu.true_mer();
            }
            case ASCENDANT -> {
                System.out.println("ERROR :DONOT");
                return false;
            }
            default -> {
                l1 = Lieu.true_prairie();
                l2 = l1;
                l3 = l2;
            }
        }
        System.out.println("Vous rappelez à la vie les cadavres de ces terres.");
        System.out.println("Combien de PP mettez vous dans le sort ? (min 4) : ");
        int mana = input.readInt();

        // selection du monstre a ressuciter
        int jet = input.D6() + mana + rand.nextInt(2) - 1;
        Monstre rez;
        if (jet <= 5 || mana < 4) {
            System.out.println("Le sort a échoué.\n");
            return false;
        } else if (jet <= 7) {
            rez = l3;
        } else if (jet <= 12) {
            rez = l2;
        } else {
            rez = l1;
        }
        System.out.println("Vous ressentez la réponse d'une âme à travers le sol.");

        //selection de la puissance du monstre
        jet = input.D8() + mana + rand.nextInt(2) - 2 + jet / 3;
        if (jet <= 7) {
            System.out.println("nouveau familier : carcasse réanimée");
            System.out.println("attaque : " + (int)max(rez.attaque *0.25, 1));
            System.out.println("vie : " + (int)max(rez.vie_max *0.25, 1));
            System.out.println("armure : " + (int)(rez.armure *0.25) + "\n");
        }
        else if (jet <= 10) {
            System.out.println("nouveau familier : esprit désincarné");
            System.out.println("attaque : " + rez.attaque);
            System.out.println("vie : " + (int)max(rez.vie_max *0.25, 1));
            System.out.println("armure : 0\n");
        }
        else if (jet <= 12) {
            System.out.println("nouveau familier : zombie");
            System.out.println("attaque : " + max(rez.attaque / 3, 1));
            System.out.println("vie : " + rez.vie_max);
            System.out.println("armure : " + (int)(rez.armure * 0.75) + "\n");
        }
        else if (jet <= 14) {
            System.out.println("nouveau familier : ancien squelette");
            System.out.println("attaque : " + (int)max(0.75 * rez.attaque, 1));
            System.out.println("vie : " + rez.vie_max);
            System.out.println("armure : " + rez.armure + "\n");
        }
        else {
            System.out.println("nouveau familier : ancien gardien");
            System.out.println("attaque : " + (int)(rez.attaque * 1.2));
            System.out.println("vie : " + (int)(rez.vie_max * 1.2));
            System.out.println("armure : " + (int)(max(rez.armure * 1.2, 1)) + "\n");
        }
        return true;
    }

    /**
     * Indique le résultat de la compétence "dissection"
     * @throws IOException toujours
     */
    public static void dissection() throws IOException {
        int temp = input.D6();
        if (temp <= 1 + rand.nextInt(2) - 1) {
            System.out.println("Vous n'extrayez rien d'utile.");
        } else if (temp <= 5 + rand.nextInt(2) - 1) {
            System.out.println("Vous trouvez 1 ingrédient.");
        }
        else{
            System.out.println("Vous récoltez 2 ingrédients.");
        }
    }
}

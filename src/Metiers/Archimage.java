package Metiers;

import Exterieur.Input;

import Enum.Metier;
import Enum.Position;
import Enum.Action;

import Monstre.Monstre;
import main.Main;

import java.io.IOException;

public class Archimage extends Joueur {
    Metier metier = Metier.ARCHIMAGE;

    public Archimage(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
        vie = 4;
        attaque = 0;
        PP = "mana";
        PP_value = 8;
        PP_max = 10;
        caracteristique = "Double sort, Manchot, Bruyant";
        competences = "Sort, Méditation, Purge";
    }

    public Metier getMetier() {
        return metier;
    }

    protected String nomMetier(){
        return "archimage";
    }

    @Override
    public String text_tour(){
        return  "/(me)ditation";
    }

    @Override
    public boolean tour(String choix) throws IOException {
        if(choix.equalsIgnoreCase("me")){
            meditation();
            return true;
        }
        return false;
    }

    @Override
    public String text_action() {
        String text = super.text_action();
        if (!est_berserk()) {
            text += "/(me)ditation/(so)rt";
        }
        return text;
    }

    @Override
    public Action action(String choix, boolean est_familier) throws IOException {
        if(est_familier){
            return super.action(choix, true);
        }
        switch(choix) {
            case "me" -> {
                if (!est_berserk()) {
                    return Action.MEDITATION;
                }
            }
            case "so" -> {
                if (!est_berserk()) {
                    return Action.SORT;
                }
            }
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi) throws IOException {
        switch(action) {
            case MEDITATION -> {
                meditation();
                return false;
            }
            case SORT -> {
                sort(ennemi);
                return false;
            }
        }
        return super.traite_action(action, ennemi);
    }

    @Override
    public int bonus_exploration(){
        return rand.nextInt(2) - 1 /* bruyant */;
    }

    @Override
    public void fin_tour_combat(){

    }

    @Override
    public void essaie_reveil() throws IOException {
        // l'archimage peut se réveiller via un sort
        if (Input.yn("Utiliser purge (3PP) pour reprendre conscience ?")) {
            System.out.println(nom + " se réveille.\n");
            conscient = true;
            reveil = 0;
        }
        else{
            super.essaie_reveil();
        }
        if(est_assomme()){
            System.out.println(nom + " récupère 1 point de mana.");
        }
    }

    /**
     * Permet à l'archimage de lancer ses sorts
     * @param ennemi le monstre ennemi
     * @throws IOException toujours
     */
    private void sort(Monstre ennemi) throws IOException {
        extracted(ennemi);
        if(ennemi.est_mort()){
            return;
        }
        if(Input.yn("Votre mana est-il tombé à 0 ?")){
            if(addiction()){
                return;
            }
        }
        System.out.println("Vous préparez votre second sort.");
        extracted(ennemi);
        if(Input.yn("Votre mana est-il tombé à 0 ?")){
            addiction();
        }
    }

    /**
     * Fonction auxiliaire de sort
     * demande au joueur quel sort il veut lancer et le lance
     */
    private void extracted(Monstre ennemi) throws IOException {
        switch (Input.sort()){
            case BDF -> boule_de_feu(ennemi);
            case ONDE_CHOC -> onde_choc(ennemi);
            case ADG -> armure_de_glace();
            case FOUDRE -> foudre(ennemi);
            case AUTRE -> ennemi.dommage_magique(Input.magie());
        }
    }

    /**
     * Affiche les bienfaits de la méditation
     * @throws IOException toujours
     */
    public static void meditation() throws IOException {
        int jet = Input.D6() + rand.nextInt(3) - 1;
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
     * Applique les effets de la compétence "Onde de choc"
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void onde_choc(Monstre ennemi) throws IOException {

        // sur les participants sauf le lanceur
        for(int i = 0; i < Main.nbj; i++) {
            Joueur joueur = Main.joueurs[i];
            if (joueur != this && joueur.est_actif()) {
                joueur.choc();
            }
        }

        // sur l'ennemi
        System.out.println(ennemi.getNom() + " est frappé par l'onde de choc.");
        System.out.print(this.getNom() + " : ");
        switch (Input.D6()){
            case 2 -> ennemi.do_etourdi();
            case 3, 4 -> ennemi.affecte();
            case 5, 6 -> ennemi.do_assomme();
            default -> System.out.println(ennemi.getNom() + " n'a pas l'air très affecté...");
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
        int mana = Input.readInt();
        int jet = Input.D4() + mana + rand.nextInt(3) - 1 ;
        int dmg;
        if (jet <= 2 || mana < 2) {
            System.out.println("Le sort ne fonctionne pas.");
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
    private boolean addiction() throws IOException {
        int jet = Input.D4() + rand.nextInt(3) - 1;
        if (jet < 4) {
            System.out.println("Vous perdez connaissance.");
            assomme();
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

}

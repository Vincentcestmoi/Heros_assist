package Metiers;

import Exterieur.Input;

import Enum.Metier;
import Enum.Position;
import Enum.Action;
import Enum.Dieux;

import Monstre.Monstre;
import main.Main;

import java.io.IOException;

public class Archimage extends Joueur {
    Metier metier = Metier.ARCHIMAGE;
    int purge_cout;

    public Archimage(String nom, Position position, int ob_f, Dieux parent, int xp) {
        super(nom, position, ob_f, parent, xp);
        vie = 4;
        attaque = 0;
        PP = "mana";
        PP_value = 6;
        PP_max = 6;
        purge_cout = 4;
        caracteristique = "Manchot, Bruyant, Fier, Addiction au mana";
        competences = "Sort, Méditation";
        SetEffetParent();
    }

    @Override
    protected void actualiser_niveau() {
        if(this.niveau >= 2){
            this.competences += ", Purge";
        }
        if(this.niveau >= 3){
            this.caracteristique += ", Double sort";
        }
        if(this.niveau >= 4){
            this.PP_max += 1;
            this.PP_value += 1;
            this.caracteristique += ", Maitre du mana";
        }
        if(this.niveau >= 6){
            this.PP_max += 1;
            this.purge_cout -= 1;
        }
        if(this.niveau >= 7){
            this.PP_value += 1;
            this.PP_max += 2;
        }
        if(this.niveau >= 8){
            this.purge_cout -= 1;
        }
        if(this.niveau >= 9){
            this.PP_max += 1;
            this.PP_value += 1;
        }
        if(this.niveau >= 10){
            this.PP_max += 1;
        }
    }

    @Override
    protected void presente_caracteristique(){
        System.out.println("Manchot : N'a qu'un bras.");
        System.out.println("Bruyant : Inflige un malus aux jets d'explorations.");
        System.out.println("Fier : N'utilise aucune magie autre que ses sorts d'archimage.");
        System.out.println("Addiction au mana : Votre corps ne peut se passer de mana.");
        if(this.niveau >= 3) {
            System.out.println("Double sort : Permet de lancer deux sorts avec l'action Sort.");
        }
    }

    @Override
    protected void presente_pouvoir(){
        System.out.println("Sort : Lance un puissant sort. L'intensité des sorts varie selon la quantité de mana utilisée.");
        System.out.println("Méditation : Se repose pour récuperer du mana.");
        if(this.niveau >= 2) {
            System.out.println("Purge : Un sort particulier qui permet de retirer les altérations d'états.");
        }
    }

    public Metier getMetier() {
        return metier;
    }

    protected String nomMetier(){
        return "archimage";
    }

    @Override
    void lvl_up() {
        int temp = this.niveau;
        if(temp < 0){
            temp = 0;
        }
        if(temp > 11){
            temp = 11;
        }
        String text = switch(temp){
            case 0 -> "Error : this function is not suposed to be called at level 0.";
            case 1 -> "Nouveau sort débloqué !"; //AdG
            case 2 -> {
                this.competences += ", Purge";
                yield "Nouvelle compétence débloquée !";
            }
            case 3 -> {
                this.caracteristique += ", Double sort";
                yield "Nouvelle capacité débloquée !";
            }
            case 4 -> {
                PP_max += 1;
                PP_value += 1;
                this.caracteristique = ", Maitre du mana";
                yield """
                        Votre réserve de mana a légèrement augmentée.
                        Vos sorts de feu ont été légèrement améliorés.
                        Nouvelle capacité débloquée !
                        """;
            }
            case 5 -> "Nouveau sort débloqué !"; //F
            case 6 -> {
                PP_max += 1;
                purge_cout -= 1;
                yield """
                        Votre réserve de mana a légèrement augmentée.
                        Vos sorts de glace ont été légèrement améliorés.
                        Vos sorts curatifs ont été légèrement améliorés.
                        Votre méditation est légèrement plus efficace.
                        """;
            }
            case 7 -> {
                PP_max += 2;
                PP_value += 1;
                yield """
                        Votre réserve de mana a augmentée.
                        Vos sorts de feu ont été légèrement améliorés.
                        Vos sorts sonores ont été améliorés.
                        """;
            }
            case 8 -> {
                purge_cout -= 1;
                yield """
                        Vos sorts de glace ont été légèrement améliorés.
                        Vos sorts curatifs ont été légèrement améliorés.
                        Vos maitrise du mana s'est accrue.
                        Votre dépendance au mana s'est agravée.
                        """;
            }
            case 9 -> {
                PP_max += 1;
                PP_value += 1;
                yield """
                        Votre réserve de mana a légèrement augmentée.
                        Votre méditation est légèrement plus efficace.
                        """;
            }
            case 10 -> {
                PP_max += 1;
                yield """
                    Votre réserve de mana a légèrement augmentée.
                    Vos sorts de feu ont été légèrement améliorés.
                    Vos sorts de glace ont été légèrement améliorés.
                    Vos sorts de foudre ont été améliorés.
                    """;
            }
            case 11 -> "Vous avez atteint le niveau max (frappe le dev c'est sa faute).";
            default -> throw new IllegalStateException("Unexpected value: " + temp);
        };
        System.out.println(text);
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
    public void addiction() throws IOException {
        if(Input.yn("Votre mana est-il descendu à 0 ?")){
            mana_addiction();
        }
    }

    @Override
    public String text_action() {
        String text = super.text_action();
        if (!est_berserk()) {
            text += "/(me)ditation/(so)rt";
            if(this.niveau >= 2 && (a_cecite() || poison1 || poison2)){
                text += "/(pu)rge";
            }
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
            case "pu" -> {
                if(!est_berserk() && (a_cecite() || poison1 || poison2)){
                    return Action.PURGE;
                }
            }
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi, int bonus_popo) throws IOException {
        switch(action) {
            case MEDITATION -> {
                meditation();
                return false;
            }
            case SORT -> {
                sort(ennemi);
                return false;
            }
            case PURGE -> {
                purge();
                return false;
            }
        }
        return super.traite_action(action, ennemi, bonus_popo);
    }

    @Override
    public int bonus_exploration(){
        return rand.nextInt(2) - 1 /* bruyant */;
    }

    @Override
    public void essaie_reveil() throws IOException {
        // l'archimage peut se réveiller via un sort
        if (this.niveau >= 2 && Input.yn("Utiliser purge ( " + purge_cout + "PP) pour reprendre conscience ?")) {
            purge();
        }
        else{
            super.essaie_reveil();
        }
        if(est_assomme()){
            if(this.niveau >= 8) {
                System.out.println(nom + " récupère 2 points de mana.");
            }
            else if(this.niveau >= 4) {
                System.out.println(nom + " récupère 1 point de mana.");
            }
        }
    }

    /**
     * Utilise la compétence purge, retire ou
     * diminue les altértions d'états.
     */
    void purge(){
        if(est_assomme()) {
            System.out.println(nom + " se réveille.\n");
            conscient = true;
            reveil = 0;
        }
        if(cecite){
            System.out.println(nom + " recouvre la vue.");
            cecite = false;
        }
        if(this.poison2 && this.niveau < 6){
            System.out.println("Le poison dans le corps de " + nom + " s'affaiblit.");
            this.poison2 = false;
            this.poison1 = true;
        }
        else if(this.poison2 || this.poison1){
            System.out.println("Le poison dans le corps dr " + nom + " se dissipe.");
            this.poison2 = false;
            this.poison1 = false;
        }
    }

    /**
     * Permet à l'archimage de lancer ses sorts
     * @param ennemi le monstre ennemi
     * @throws IOException toujours
     */
    private void sort(Monstre ennemi) throws IOException {
        lancer_sort(ennemi);
        if(ennemi.est_mort()){
            return;
        }
        if(Input.yn("Votre mana est-il tombé à 0 ?")){
            if(mana_addiction()){
                return;
            }
        }
        if(this.niveau < 3){ //single casting
            return;
        }
        System.out.println("Vous préparez votre second sort.");
        lancer_sort(ennemi);
        if(Input.yn("Votre mana est-il tombé à 0 ?")){
            mana_addiction();
        }
    }

    /**
     * Fonction auxiliaire de sort
     * demande au joueur quel sort il veut lancer et le lance
     */
    private void lancer_sort(Monstre ennemi) throws IOException {
        String text = "Quel sort voulez vous lancer : (bo)ule de feu/(on)de de choc";
        if(this.niveau >= 1) {
            text += "/(ar)mure de glace";
        }
        if(this.niveau >= 5){
            text += "/(fo)udre";
        }
        text += " ?";
        System.out.println(text);
        switch(Input.read().toLowerCase()){
            case "bo" -> boule_de_feu(ennemi);
            case "on" -> onde_choc(ennemi);
            case "ar" -> armure_de_glace();
            case "fo" -> foudre(ennemi);
            default -> {
                System.out.println("Input unknow");
                sort(ennemi);
            }
        }
    }

    /**
     * Affiche les bienfaits de la méditation
     * @throws IOException toujours
     */
    void meditation() throws IOException {
        int jet = Input.D6() + rand.nextInt(3) - 1;
        int recup = 0;
        if(this.niveau >= 6){
            recup += 1;
        }
        if(this.niveau >= 9){
            recup += 1;
        }
        if (jet <= 2) {
            recup += 1;
        }
        else if(jet <= 4) {
            recup += 2;
        }
        else if(jet <= 7) {
            recup += 3;
        }
        else{
            recup += 4;
        }
        System.out.println("Vous récupérez " + recup + "PP.");
    }

    /**
     * Applique les effets de la compétence "Onde de choc"
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void onde_choc(Monstre ennemi) throws IOException {

        int bonus = 0;
        if(this.niveau >= 7){
            bonus = 1;
        }
        // sur les participants sauf le lanceur
        for(int i = 0; i < Main.nbj; i++) {
            Joueur joueur = Main.joueurs[i];
            if (joueur != this && joueur.est_actif()) {
                joueur.choc(bonus);
            }
        }

        // sur l'ennemi
        System.out.println(ennemi.getNom() + " est frappé par l'onde de choc.");
        System.out.print(this.getNom() + " : ");
        switch (Input.D6() + bonus){
            case 2 -> ennemi.do_etourdi();
            case 3, 4 -> ennemi.affecte();
            case 5, 6, 7 -> ennemi.do_assomme();
            default -> System.out.println(ennemi.getNom() + " n'a pas l'air très affecté...");
        }
    }

    /**
     * Calcule et applique les dommages de la compétence "boule de feu"
     * @param ennemi la cible du sort
     * @throws IOException toujours
     */
    public void boule_de_feu(Monstre ennemi) throws IOException {
        System.out.println("Vous vous préparez à lancer une boule de feu.");
        System.out.println("Combien de PP mettez vous dans le sort ? (min 2)");
        int mana = Input.readInt();
        int jet = Input.D4() + mana + rand.nextInt(3) - 1 ;
        int[] paliers = {4, 7, 10};
        for(int palier : paliers){
            if(this.niveau >= palier){
                jet += 1;
            }
        }
        int dmg;
        if (jet <= 2 || mana < 2) {
            System.out.println("Le sort ne fonctionne pas.");
            return;
        }
        else if (jet <= 4) {
            System.out.println("Vous lancez une pitoyable boule de feu sur " + ennemi.getNom() + ".");
            dmg = 3;
        }
        else if (jet <= 6) {
            System.out.println("Vous lancez une boule de feu sur " + ennemi.getNom() + ".");
            dmg = 6;
        }
        else if (jet <= 9) {
            System.out.println("Vous lancez une impressionnante boule de feu sur " + ennemi.getNom() + ".");
            dmg = 8;
        }
        else if (jet == 10) {
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
            System.out.println("Une torent de flamme percute " + ennemi.getNom() + " brutalement !");
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
    public void armure_de_glace() throws IOException {
        System.out.println("Vous vous préparez à créer une armure de glace.");
        System.out.println("Combien de PP mettez vous dans le sort ? (min 3): ");
        int mana = Input.readInt();
        int jet = Input.D8() + mana + rand.nextInt(3) - 1;
        int[] paliers = {6, 8, 10};
        for(int palier : paliers){
            if(this.niveau >= palier){
                jet += 1;
            }
        }
        if (jet <= 3 || mana < 3) {
            System.out.println("Le sort ne fonctionne pas.");
        } else if (jet <= 6) {
            System.out.println("La cible gagne 3 points de résistance.");
        } else if (jet <= 8) {
            System.out.println("La cible gagne 5 points de résistance.");
        } else if (jet <= 10) {
            System.out.println("La cible gagne 6 points de résistance.");
        } else if (jet <= 12) {
            System.out.println("La cible gagne 6 points de résistance et 1 point d'armure.");
        } else if (jet <= 14) {
            System.out.println("La cible gagne 8 points de résistance et 1 point d'armure.");
        } else if (jet <= 16) {
            System.out.println("La cible gagne 9 points de résistance et 1 point d'armure.");
        } else if (jet == 17) {
            System.out.println("La cible gagne 10 points de résistance et 1 point d'armure.");
        } else if (jet == 18) {
            System.out.println("La cible gagne 10 points de résistance et 2 point d'armure.");
        } else {
            System.out.println("La cible gagne 12 points de résistance et 2 point d'armure.");
        }
    }

    /**
     * Calcule et applique les dommages de la compétence "foudre"
     * @param ennemi la cible du sort
     * @throws IOException toujours
     */
    public void foudre(Monstre ennemi) throws IOException {
        System.out.println("Vous vous préparez à lancer un puissant éclair.");
        System.out.println("Combien de PP mettez vous dans le sort ? (min 7) : ");
        int mana = Input.readInt();
        int jet = Input.D12() + mana + rand.nextInt(3) - 1;
        if (this.niveau >= 10) {
            jet += 2;
        }
        int dmg;
        if (jet <= 7 || mana < 7) {
            System.out.println("Le sort ne fonctionne pas.");
            return;
        } else if (jet <= 10) {
            System.out.println("Un arc électrique vient frapper " + ennemi.getNom() + ".");
            dmg = 12;
        } else if (jet <= 12) {
            System.out.println("Un arc électrique vient frapper " + ennemi.getNom() + ".");
            dmg = 13;
        } else if (jet <= 14) {
            System.out.println("Un éclair s'abat sur " + ennemi.getNom() + ".");
            dmg = 16;
            if (rand.nextBoolean()) {
                ennemi.affecte();
            }
        } else if (jet <= 16) {
            System.out.println("Un puissant éclair s'abat sur " + ennemi.getNom() + ".");
            dmg = 18;
            if (rand.nextBoolean()) {
                ennemi.affecte();
            }
        } else if (jet <= 18) {
            System.out.println("Un puissant éclair s'abat sur " + ennemi.getNom() + ".");
            dmg = 20;
            ennemi.affecte();
        } else if (jet == 19) {
            System.out.println("Un gigantesque éclair frappe  " + ennemi.getNom() + " de plein fouet !");
            dmg = 22;
            ennemi.affecte();
        } else if (jet == 20) {
            System.out.println("Un gigantesque éclair frappe  " + ennemi.getNom() + " de plein fouet !");
            dmg = 24;
            ennemi.affecte();
        } else if (jet <= 22) {
            System.out.println("L'espace d'un instant, les cieux s'illuminent et une miriade d'éclairs vient percuter " + ennemi.getNom() + " avec grand fracas.");
            dmg = 25;
            if (rand.nextBoolean()) {
                ennemi.affecte();
            } else {
                ennemi.do_assomme();
            }
        } else if (jet == 23) {
            System.out.println("L'espace d'un instant, les cieux s'illuminent et une miriade d'éclairs vient percuter " + ennemi.getNom() + " avec grand fracas.");
            dmg = 27;
            ennemi.do_assomme();
        } else {
            System.out.println("Un déchainement de pure énergie fend l'espace entre le ciel et la terre, transpersant " + ennemi.getNom() + " sur son passage.");
            dmg = 30;
            ennemi.do_assomme();
        }
        ennemi.dommage_magique(dmg);
    }

    /**
     * Applique les compétences "addiction au mana" et "maitre du mana" de l'archimage
     * @return si le joueur perd connaissance
     * @throws IOException toujours
     */
    private boolean mana_addiction() throws IOException {
        int jet = Input.D4() + rand.nextInt(3) - 1;
        if(this.niveau >= 8){
            jet -= 1;
        }
        if (jet < 4) {
            System.out.println("Vous perdez connaissance.");
            if(this.niveau >= 8){
                assomme(-1);
            }
            else{
                assomme();
            }
            return true;
        }
        else {
            jet = 0;
            if (this.niveau >= 8) {
                jet += 1;
            }
            if (this.niveau >= 4 && jet + Input.readInt() > 1) {
                System.out.println("Vous récuperez " + (jet - 1) + "PP.");
            }
        }
        return false;
    }

}

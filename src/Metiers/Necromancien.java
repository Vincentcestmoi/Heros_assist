package Metiers;

import Exterieur.Input;

import Enum.Metier;
import Enum.Position;
import Enum.Action;
import Monstre.Lieu;

import Monstre.Monstre;

import java.io.IOException;

public class Necromancien extends Joueur {
    Metier metier = Metier.NECROMANCIEN;
    private boolean a_maudit;

    public Necromancien(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
        vie = 4;
        attaque = 1;
        PP = "mana";
        PP_value = 5;
        PP_max = 8;
        caracteristique = "Taumaturge";
        competences = "Sacrifice, Ressurection, Zombification, Appel des morts";
    }

    public Metier getMetier() {
        return metier;
    }

    protected String nomMetier(){
        return "necromancien";
    }

    @Override
    public void init_affrontement(boolean force, Position pos) throws IOException {
        super.init_affrontement(force, pos);
        a_maudit = false;
    }

    @Override
    public String text_tour(){
        String text = "/(ap)pel des morts";
        if(a_familier()){
            text += "/(sa)crifier son familier";
        }
        return text;
    }

    @Override
    public boolean tour(String choix) throws IOException {
        switch(choix) {
            case "ap" -> {
                necromancie();
                return true;
            }
            case "sa" -> {
                System.out.println("Vous récuperez 4PP");
                perdre_familier();
                return false;
            }
        }
        return false;
    }

    @Override
    public String text_action() {
        String text = super.text_action();
        if (!est_berserk() && !a_maudit) {
            text += "/(ma)udir";
        }
        return text;
    }

    @Override
    public Action action(String choix, boolean est_familier) throws IOException {
        if(est_familier){
            return super.action(choix, true);
        }
        if(choix.equals("ma") && !est_berserk() && !a_maudit) {
            return Action.MAUDIR;
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi) throws IOException {
        if (action == Action.MAUDIR && !est_berserk()) {
            maudir(ennemi);
            return false;
        }
        return super.traite_action(action, ennemi);
    }

    @Override
    public void fin_tour_combat(){

    }

    @Override
    public boolean ajouter_familier(int obeissance) throws IOException {
        if (a_familier()){
            if(Input.yn(nom + " possède déjà un familier, voulez vous ...'remplacer' l'ancien ? ")) {
                System.out.println("Vous recupérez 4PP grâce au sacrifice de votre ancien compagnon.\n");
                setOb(obeissance);
                return true;
            }
            else{
                System.out.println("Le nouveau venu a été convertie en 4PP.");
                return false;
            }
        }
        else if(Input.yn("Voulez vous sacrifier votre nouveau familier ?")) {
            System.out.println("Le nouveau venu a été convertie en 4PP.");
            return false;
        }
        else{
            setOb(obeissance);
            return true;
        }
    }

    @Override
    public boolean peut_ressuciter() {
        return true;
    }

    @Override
    public boolean ressuciter(int malus) throws IOException {
        if (malus > 5) {
            malus = 5;
        }
        int jet = Input.D8() - malus + rand.nextInt(3) - 1;
        if (jet <= 3) {
            System.out.println("Echec de la résurection");
            return false;
        }
        if (jet <= 5) {
            System.out.println("Résurection avec 4 (max) points de vie");
        } else if (jet <= 7) {
            System.out.println("Résurection avec 8 (max) points de vie");
        } else {
            System.out.println("Résurection avec 12 (max) points de vie");
        }
        return true;
    }

    /**
     * Applique l'effet de la compétence "malédiction"
     * @param ennemi la cible de la malédiction
     * @throws IOException toujours
     */
    private void maudir(Monstre ennemi) throws IOException {
        a_maudit = true;
        int boost = rand.nextInt(3);
        switch (Input.D6()){
            case 2 -> {
                System.out.println("Vous maudissez faiblement " + ennemi.getNom() + ".");
                ennemi.bostVie(-(1 + boost), true);
            }
            case 3, 4 -> {
                System.out.println("Vous maudissez " + ennemi.getNom() + ".");
                ennemi.bostVie(-(2 + boost), true);
            }
            case 5 -> {
                System.out.println("Vous maudissez agressivement " + ennemi.getNom() + ".");
                ennemi.bostVie(-(3 + boost), true);
            }
            case 6, 7, 8 -> {
                System.out.println("Vous maudissez puissament " + ennemi.getNom() + ".");
                ennemi.bostVie(-(5 + boost), true);
            }
            default -> {
                System.out.println("vous n'arrivez pas à maudir " + ennemi.getNom() + ".");
                a_maudit = false;
            }
        }
    }

    /**
     * Indique le résultat de la compétence "appel des morts"
     * @throws IOException toujours
     */
    private void necromancie() throws IOException {
        Monstre m = Lieu.true_monstre(getPosition());
        System.out.println("Vous rappelez à la vie les cadavres de ces terres.");
        System.out.println("Combien de PP mettez vous dans le sort ? (min 4) : ");
        int mana = Input.readInt();
        int jet = Input.D8() + mana + rand.nextInt(2) - 1;
        String monstre_nom;
        int ob;
        if (jet <= 6) {
            monstre_nom = "carcasse putréfié";
            m.bostAtk(-7, true);
            m.bostVie(-9, true);
            m.bostArmure(-4, true);
            ob = 1 + rand.nextInt(2);
        }
        else if (jet <= 8) {
            monstre_nom = "zombie écorché";
            m.bostAtk(-5, true);
            m.bostVie(-7, true);
            m.bostArmure(-4, true);
            ob = 1 + rand.nextInt(2);
        }
        else if (jet <= 11) {
            monstre_nom = "esprit vengeur";
            m.bostAtk(3, true);
            m.bostVie(-7, true);
            m.bostArmure(-4, true);
            ob = 2 + rand.nextInt(3);
        }
        else if (jet <= 13) {
            monstre_nom = "squellette blindé";
            m.bostAtk(-3, true);
            m.bostVie(4, true);
            m.bostArmure(1, true);
            ob = 3 + rand.nextInt(4);
        }
        else if (jet <= 15) {
            monstre_nom = "abomination vengeresse";
            m.bostAtk(2, true);
            m.bostVie(4, true);
            m.bostArmure(0, true);
            ob = 3 + rand.nextInt(4);
        }
        else {
            monstre_nom = "Ancien gardien";
            m.bostAtk(3, true);
            m.bostVie(7, true);
            m.bostArmure(2, true);
            ob = 4 + rand.nextInt(3);
        }
        m.rename(monstre_nom);
        m.presente_familier();
        ajouter_familier(ob);
    }



    /**
     * Tente de ressuciter un ennemi par nécromancie, et l'ajoute en tant que familier le cas échéant
     * @param ennemi le monstre à ressuciter
     * @return la variation de l'état du familier
     * @throws IOException toujours
     */
    private int zombifier (Monstre ennemi) throws IOException {
        int jet = Input.D8() + (ennemi.getEtat() - 10) / 2;
        if(jet > 8){
            jet = 8;
        }
        int retour;
        switch (jet) {
            case 3 -> { //-8
                System.out.println(ennemi.getNom() + " a été... rapellé");
                ennemi.bostAtk(-6, true);
                ennemi.bostVie(-8, true);
                ennemi.bostArmure(-3, true);
                retour = -rand.nextInt(6) - 7;
            }
            case 4, 5 -> { //-5
                System.out.println(ennemi.getNom() + " a été partiellement ressucité");
                ennemi.bostAtk(-3, true);
                ennemi.bostVie(-5, true);
                ennemi.bostArmure(-2, true);
                retour = -rand.nextInt(6) - 4;
            }
            case 6, 7 -> { // -2
                System.out.println(ennemi.getNom() + " a été ressucité");
                ennemi.bostAtk(-1, true);
                ennemi.bostVie(-2, true);
                retour = -rand.nextInt(6) - 1;
            }
            case 8 -> { //+2
                System.out.println(ennemi.getNom() + " a été parfaitement ressucité");
                ennemi.bostAtk(1, true);
                ennemi.bostVie(2, true);
                retour = 1;
            }
            default -> {
                System.out.println("échec du sort.");
                return 0;
            }
        }
        ennemi.presente_familier();
        if(ajouter_familier(2)) {
            return -100;
        }
        return retour - rand.nextInt(4); //pénalité de sacrifice
    }

    @Override
    protected void monstre_mort_perso(Monstre ennemi) throws IOException {
        if (ennemi.corps_utilisable() && est_actif() && est_vivant()) {
            if (Exterieur.Input.yn("Voulez vous tenter de ressuciter " + ennemi.getNom() + " en tant que familier pour 2PP ?")) {
                ennemi.alterEtat(zombifier(ennemi));
            }
        }
    }

}

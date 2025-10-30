package Metiers;

import Enum.Action;
import Enum.Dieux;
import Enum.Metier;
import Enum.Position;
import Exterieur.Input;
import Monstre.Lieu;
import Monstre.Monstre;

import java.io.IOException;

public class Necromancien extends Joueur {
    Metier metier = Metier.NECROMANCIEN;
    private boolean a_maudit;
    private int nb_piece;
    private int nb_item;
    private int pp_sacrifice;
    
    public Necromancien(String nom, Position position, int ob_f, Dieux parent, int xp) {
        super(nom, position, ob_f, parent, xp);
        vie = 4;
        attaque = 1;
        PP = "mana";
        PP_value = 5;
        PP_max = 7;
        add_caracteristique("Taumaturge");
        add_competence("Sacrifice, Malédiction");
        nb_piece = 6;
        nb_item = 3;
        pp_sacrifice = 2;
    }
    
    @Override
    protected void actualiser_niveau() {
        if (this.niveau >= 1) {
            add_competence("Zombification");
        }
        if (this.niveau >= 2) {
            add_competence("Ressurection");
        }
        if (this.niveau >= 3) {
            pp_sacrifice += 1;
        }
        if (this.niveau >= 4) {
            nb_item += 2;
            nb_piece += 4;
        }
        if (this.niveau >= 5) {
            this.vie += 1;
            add_competence("Appel des morts");
        }
        if (this.niveau >= 6) {
            nb_piece += 2;
            nb_item += 1;
            pp_sacrifice += 1;
        }
        if (this.niveau >= 7) {
            this.PP_max += 1;
            this.PP_value += 1;
        }
        if (this.niveau >= 8) {
            nb_item += 1;
            nb_piece += 2;
        }
        if (this.niveau >= 10) {
            pp_sacrifice += 1;
            PP_max += 1;
        }
    }
    
    @Override
    protected void lvl_up() {
        int temp = this.niveau;
        if (temp < 0) {
            temp = 0;
        }
        if (temp > 11) {
            temp = 11;
        }
        String text = switch (temp) {
            case 0 -> "Error : this function is not suposed to be called at level 0.";
            case 1 -> {
                add_competence("Zombification");
                yield "Nouvelle compétence débloquée !";
            }
            case 2 -> {
                add_competence("Ressurection");
                yield "Nouvelle compétence débloquée !";
            }
            case 3 -> {
                pp_sacrifice += 1;
                yield "Vos sacrifice ont été légèrement renforcés.\nVos compétence en malédiction ont été légèrement "
                        + "augmentées.";
            }
            case 4 -> {
                nb_item += 2;
                nb_piece += 4;
                yield "Vos compétence de thaumaturge ont été renforcées.\nVos compétence de résurection ont été " +
                        "légèrement renforcées.";
            }
            case 5 -> {
                this.vie += 1;
                add_competence("Appel des morts");
                yield "Votre résistance a augmentée.\nNouvelle compétence débloquée !";
            }
            case 6 -> {
                nb_piece += 2;
                nb_item += 1;
                pp_sacrifice += 1;
                yield """
                        Vos compétence de thaumaturge ont été légèrement renforcées.
                        Vos sacrifices ont été légèrement renforcés.
                        Vos compétence de zombifications ont été légèrement renforcées.""";
            }
            case 7 -> {
                this.PP_max += 1;
                this.PP_value += 1;
                yield "Votre réserve de mana a augmentée.\nVos compétence en malédiction ont été augmentées.";
            }
            case 8 -> {
                nb_item += 1;
                nb_piece += 2;
                yield "Vos compétence de thaumaturge ont été légèrement renforcées.\nVos compétence de résurection " + "ont été renforcées.";
            }
            case 9 ->
                    "Vos compétence de zombification ont été légèrement augmentées.\nVos compétences en appels des " + "morts " + "ont été légèrement augmentées.";
            case 10 -> {
                pp_sacrifice += 1;
                PP_max += 1;
                yield """
                        Nouvelles recettes débloquées.
                        Vos compétence de zombification ont été légèrement renforcées.
                        Vos compétence en appels des morts ont été légèrement renforcées.
                        Vos compétence en malédiction ont été légèrements renforcées.
                        Vos sacrifices ont été légèrement renforcés.
                        Votre réserve de mana a augmentée.
                        """;
            }
            case 11 -> "Vous avez atteint le niveau max (frappe le dev c'est sa faute).";
            default -> throw new IllegalStateException("Unexpected value: " + temp);
        };
        System.out.println(text);
    }
    
    @Override
    protected void presente_caracteristique() {
        System.out.println("Thaumaturge : Quand il meurt, un thaumaturge peut emporter avec lui " + nb_item + " " +
                "pièces d'équipements de son choix et " + nb_piece + "PO dans l'au-delà.");
    }
    
    @Override
    protected void presente_pouvoir() {
        System.out.println("Sacrifice : Tuer un allié (familier comprit) régénère " + pp_sacrifice + " mana.");
        System.out.println("Malédiction : Diminue définitivement la résistance d'une cible.");
        if (this.niveau >= 1) {
            System.out.println("Zombification : Pour 2 mana, tente de ramener un monstre fraichement tué à la vie " + "sous la forme" + "d'un fidèle serviteur. Peut endommager le cadavre.");
        }
        if (this.niveau >= 2) {
            System.out.println("Ressurection : Pour 2 mana, ramène un joueur à la vie.");
        }
        if (this.niveau >= 5) {
            System.out.println("Appel des morts : Pour 4 mana ou plus, ramène à la vie un monstre mort depuis " +
                    "longtemps." + "La réussite du sort et la puissance de l'entité invoquée depand de la quantité " + "de" + " mana utilisée.");
        }
    }
    
    public Metier getMetier() {
        return metier;
    }
    
    protected String nomMetier() {
        return "mage noir";
    }
    
    @Override
    public void mort_def() {
        super.mort_def();
        System.out.println("Grace à vos talent de thaumaturge, vous conservez vos modifications physiques et " +
                "emportez" + "avec vous " + nb_piece + "PO et " + nb_item + " de vos items de votre choix (vous " +
                "devez" + " entrer à nouveau les effets cachées).");
    }
    
    @Override
    public void init_affrontement(boolean force, Position pos) throws IOException {
        super.init_affrontement(force, pos);
        a_maudit = false;
    }
    
    @Override
    public String text_tour() {
        String text = "";
        if (this.niveau >= 5) {
            text += "/(ap)pel des morts";
        }
        if (a_familier()) {
            text += "/(sa)crifier son familier";
        }
        return text;
    }
    
    @Override
    public boolean tour(String choix) throws IOException {
        switch (choix) {
            case "ap" -> {
                if (this.niveau >= 5) {
                    necromancie();
                    return true;
                }
            }
            case "sa" -> {
                System.out.println("Vous récuperez " + pp_sacrifice + "PP.");
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
        if (est_familier) {
            return super.action(choix, true);
        }
        if (choix.equals("ma") && !est_berserk() && !a_maudit) {
            return Action.MAUDIR;
        }
        return super.action(choix, false);
    }
    
    @Override
    public boolean traite_action(Action action, Monstre ennemi, int bonus_popo) throws IOException {
        if (action == Action.MAUDIR && !est_berserk()) {
            ennemi.dommage(bonus_popo);
            maudir(ennemi);
            return false;
        }
        return super.traite_action(action, ennemi, bonus_popo);
    }
    
    @Override
    public boolean action_consomme_popo(Action action) {
        if (action == Action.MAUDIR) {
            return true;
        }
        return super.action_consomme_popo(action);
    }
    
    @Override
    public boolean ajouter_familier(int obeissance) throws IOException {
        if (a_familier()) {
            if (Input.yn(nom + " possède déjà un familier, voulez vous ...'remplacer' l'ancien ? ")) {
                System.out.println("Vous recupérez " + pp_sacrifice + "PP grâce au sacrifice de votre ancien " +
                        "compagnon.\n");
                setOb(obeissance);
                return true;
            } else {
                System.out.println("Le nouveau venu a été convertie en " + pp_sacrifice + "PP.");
                return false;
            }
        } else if (Input.yn("Voulez vous sacrifier votre nouveau familier ?")) {
            System.out.println("Le nouveau venu a été convertie en " + pp_sacrifice + "PP.");
            return false;
        } else {
            setOb(obeissance);
            return true;
        }
    }
    
    @Override
    public boolean peut_ressuciter() {
        return this.niveau >= 2;
    }
    
    @Override
    public boolean ressuciter(int malus) throws IOException {
        if (this.niveau < 2) {
            return false;
        }
        if (malus > 5) {
            malus = 5;
        }
        if (this.niveau >= 4) {
            malus -= 1;
        }
        if (this.niveau >= 8) {
            malus -= 2;
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
        int boost_lvl = 0;
        int[] paliers = {3, 7, 7, 10};
        for (int palier : paliers) {
            if (this.niveau >= palier) {
                boost += 1;
                boost_lvl += 1;
            }
        }
        switch (Input.D6() + boost_lvl) {
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
            case 9, 10 -> {
                System.out.println("Vous arracher à " + ennemi.getNom() + " des fragments de son âme !");
                ennemi.bostVie(-(8 + boost), true);
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
        if (this.niveau >= 9) {
            jet += 1;
        }
        if (this.niveau >= 10) {
            jet += 1;
        }
        String monstre_nom;
        int ob;
        if (jet <= 6) {
            monstre_nom = "carcasse putréfié";
            m.bostAtk(-7, true);
            m.bostVie(-9, true);
            m.bostArmure(-4, true);
            ob = 1 + rand.nextInt(2);
        } else if (jet <= 8) {
            monstre_nom = "zombie écorché";
            m.bostAtk(-5, true);
            m.bostVie(-7, true);
            m.bostArmure(-4, true);
            ob = 1 + rand.nextInt(2);
        } else if (jet <= 11) {
            monstre_nom = "esprit vengeur";
            m.bostAtk(3, true);
            m.bostVie(-7, true);
            m.bostArmure(-4, true);
            ob = 2 + rand.nextInt(3);
        } else if (jet <= 13) {
            monstre_nom = "squellette blindé";
            m.bostAtk(-3, true);
            m.bostVie(4, true);
            m.bostArmure(1, true);
            ob = 3 + rand.nextInt(4);
        } else if (jet <= 15) {
            monstre_nom = "abomination vengeresse";
            m.bostAtk(2, true);
            m.bostVie(4, true);
            m.bostArmure(0, true);
            ob = 3 + rand.nextInt(4);
        } else {
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
    private int zombifier(Monstre ennemi) throws IOException {
        int jet = Input.D8() + (ennemi.getEtat() / 10) - 1;
        int[] paliers = {6, 9, 10};
        for (int palier : paliers) {
            if (this.niveau >= palier) {
                jet += 1;
            }
        }
        if (jet > 8 && niveau < 9) {
            jet = 8;
        }
        if (jet > 8 && jet < 11) {
            jet = 8;
        }
        if (jet >= 11) {
            jet = 9;
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
            case 9 -> { //+5
                System.out.println(ennemi.getNom() + " a été invoqué, plus fort que de son vivant.");
                ennemi.bostAtk(3, true);
                ennemi.bostVie(5, true);
                ennemi.bostArmure(1, true);
                retour = 3;
            }
            default -> {
                System.out.println("échec du sort.");
                return 0;
            }
        }
        ennemi.presente_familier();
        if (ajouter_familier(2)) {
            return -100;
        }
        return retour - rand.nextInt(4); //pénalité de sacrifice
    }
    
    @Override
    protected void monstre_mort_perso(Monstre ennemi) throws IOException {
        if (ennemi.corps_utilisable() && est_actif() && est_vivant() && this.niveau >= 1) {
            if (Exterieur.Input.yn("Voulez vous tenter de ressuciter " + ennemi.getNom() + " en tant que familier " + "pour 2PP ?")) {
                ennemi.alterEtat(zombifier(ennemi));
            }
        }
    }
}

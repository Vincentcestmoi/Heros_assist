package Metiers;

import Auxiliaire.Texte;
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
        add_caracteristique("Thaumaturge");
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
            add_competence("Resurrection");
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
            case 0 -> "Error : this function is not supposed to be called at level 0.";
            case 1 -> {
                add_competence("Zombification");
                yield "Nouvelle compétence débloquée !";
            }
            case 2 -> {
                add_competence("Resurrection");
                yield "Nouvelle compétence débloquée !";
            }
            case 3 -> {
                add_competence("Soumission");
                pp_sacrifice += 1;
                yield """
                        Vos sacrifices ont été légèrement renforcé.
                        Vos compétence en malédiction ont été légèrement augmentées.
                        Nouvelle compétence débloquée !
                        """;
            }
            case 4 -> {
                nb_item += 2;
                nb_piece += 4;
                yield "Vos compétence de thaumaturge ont été renforcées.\nVos compétence de resurrection ont été " +
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
                        Vos compétences de zombification ont été légèrement renforcées.""";
            }
            case 7 -> {
                this.PP_max += 1;
                this.PP_value += 1;
                yield """
                        Votre réserve de mana a augmentée.
                        Vos compétence en malédiction ont été augmentées.
                        Vos compétences de soumission ont été légèrement augmentées.
                        """;
            }
            case 8 -> {
                nb_item += 1;
                nb_piece += 2;
                yield "Vos compétence de thaumaturge ont été légèrement renforcées.\nVos compétence de resurrection " +
                        "ont été renforcées.";
            }
            case 9 ->
                    "Vos compétences de zombification ont été légèrement augmentées.\nVos compétences d'appel des " +
                            "morts ont été légèrement augmentées.";
            case 10 -> {
                pp_sacrifice += 1;
                PP_max += 1;
                yield """
                        Nouvelles recettes débloquées.
                        Vos compétence de zombification ont été légèrement renforcées.
                        Vos compétence en appels des morts ont été légèrement renforcées.
                        Vos compétence en malédiction ont été légèrement renforcées.
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
            System.out.printf("Zombification : Pour %d mana, tente de ramener un monstre fraichement tué à la vie " +
                    "sous la forme d'un fidèle serviteur. Peut endommager le cadavre.\n", rune_noire ? 3 : 2);
        }
        if (this.niveau >= 2) {
            System.out.println("Resurrection : Pour 2 mana, ramène un joueur à la vie.");
        }
        if (this.niveau >= 5) {
            System.out.printf("Appel des morts : Pour %d mana ou plus, ramène à la vie un monstre mort depuis " +
                    "longtemps. La réussite du sort et la puissance de l'entité invoquée dépend de la quantité de " +
                    "mana utilisée.\n", rune_noire ? 5 : 4);
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
        if(rune_mortifere){
            Texte.thaumaturge(2 * nb_item, 2 * nb_piece);
        } else {
            Texte.thaumaturge(nb_item, nb_piece);
        }
    }
    
    @Override
    public void init_affrontement(boolean force, Position pos) throws IOException {
        super.init_affrontement(force, pos);
        a_maudit = false;
    }
    
    @Override
    public String text_tour() {
        String text = super.text_tour();
        if (this.niveau >= 5) {
            text += "/(ap)pel des morts";
        }
        if (a_familier()) {
            text += "/(sa)crifier son familier";
        }
        if (this.niveau >= 3 && a_familier() && !familier_loyalmax()) {
            text += "/(so)umettre son familier";
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
                if (a_familier()) {
                    System.out.println("Vous récupérez " + pp_sacrifice + "PP.");
                    perdre_familier();
                    return false;
                }
            }
            case "so" -> {
                if (this.niveau >= 3 && a_familier() && !familier_loyalmax()) {
                    soumission();
                    return true;
                }
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
    public boolean peut_ressusciter() {
        return this.niveau >= 2;
    }
    
    @Override
    public boolean ressusciter(int malus) throws IOException {
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
                ennemi.boostVie(-(1 + boost), true);
            }
            case 3, 4 -> {
                System.out.println("Vous maudissez " + ennemi.getNom() + ".");
                ennemi.boostVie(-(2 + boost), true);
            }
            case 5 -> {
                System.out.println("Vous maudissez agressivement " + ennemi.getNom() + ".");
                ennemi.boostVie(-(3 + boost), true);
            }
            case 6, 7, 8 -> {
                System.out.println("Vous maudissez puissament " + ennemi.getNom() + ".");
                ennemi.boostVie(-(5 + boost), true);
            }
            case 9, 10 -> {
                System.out.println("Vous arracher à " + ennemi.getNom() + " des fragments de son âme !");
                ennemi.boostVie(-(8 + boost), true);
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
        System.out.printf("Combien de PP mettez vous dans le sort ? (min %d) : ", rune_noire ? 5 : 4);
        int mana = Input.readInt();
        int jet = Input.D8() + mana + rand.nextInt(2) - 1;
        if(rune_noire){
            jet += 2;
        }
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
            m.boostAtk(-7, true);
            m.boostVie(-9, true);
            m.boostArmure(-4, true);
            ob = 1 + rand.nextInt(2);
        } else if (jet <= 8) {
            monstre_nom = "zombie écorché";
            m.boostAtk(-5, true);
            m.boostVie(-7, true);
            m.boostArmure(-4, true);
            ob = 1 + rand.nextInt(2);
        } else if (jet <= 11) {
            monstre_nom = "esprit vengeur";
            m.boostAtk(3, true);
            m.boostVie(-7, true);
            m.boostArmure(-4, true);
            ob = 2 + rand.nextInt(3);
        } else if (jet <= 13) {
            monstre_nom = "squelette blindé";
            m.boostAtk(-3, true);
            m.boostVie(4, true);
            m.boostArmure(1, true);
            ob = 3 + rand.nextInt(4);
        } else if (jet <= 15) {
            monstre_nom = "abomination vengeresse";
            m.boostAtk(2, true);
            m.boostVie(4, true);
            m.boostArmure(0, true);
            ob = 3 + rand.nextInt(4);
        } else {
            monstre_nom = "Ancien gardien";
            m.boostAtk(3, true);
            m.boostVie(7, true);
            m.boostArmure(2, true);
            ob = 4 + rand.nextInt(3);
        }
        m.rename(monstre_nom);
        m.presente_familier();
        ajouter_familier(ob);
    }
    
    
    /**
     * Tente de ressusciter un ennemi par nécromancie, et l'ajoute en tant que familier le cas échéant
     * @param ennemi le monstre à ressusciter
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
        if(rune_noire){
            jet += 2;
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
                System.out.println(ennemi.getNom() + " a été... rappellé");
                ennemi.boostAtk(-6, true);
                ennemi.boostVie(-8, true);
                ennemi.boostArmure(-3, true);
                retour = -rand.nextInt(6) - 7;
            }
            case 4, 5 -> { //-5
                System.out.println(ennemi.getNom() + " a été partiellement ressuscité");
                ennemi.boostAtk(-3, true);
                ennemi.boostVie(-5, true);
                ennemi.boostArmure(-2, true);
                retour = -rand.nextInt(6) - 4;
            }
            case 6, 7 -> { // -2
                System.out.println(ennemi.getNom() + " a été ressuscité");
                ennemi.boostAtk(-1, true);
                ennemi.boostVie(-2, true);
                retour = -rand.nextInt(6) - 1;
            }
            case 8 -> { //+2
                System.out.println(ennemi.getNom() + " a été parfaitement ressuscité");
                ennemi.boostAtk(1, true);
                ennemi.boostVie(2, true);
                retour = 1;
            }
            case 9 -> { //+5
                System.out.println(ennemi.getNom() + " a été invoqué, plus fort que de son vivant.");
                ennemi.boostAtk(3, true);
                ennemi.boostVie(5, true);
                ennemi.boostArmure(1, true);
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
    
    private void soumission() throws IOException {
        int jet = Input.D6();
        if (this.niveau >= 7) {
            jet += 1;
        }
        if (jet > 6) {
            jet = 6;
        }
        System.out.println("Vous soumettez votre familier à votre volonté.\n");
        this.ob_f += switch (jet + bonus_dresser()) {
            case 1 -> {
                if (this.ob_f < 4) {
                    System.out.println("Votre familier résiste fortement à votre emprise.\n");
                    jet = Input.D4();
                    if (this.niveau >= 7) {
                        jet += 1;
                    }
                    if (jet <= 2) {
                        System.out.println("Votre familier perds définitivement 1 point de résistance.");
                    }
                }
                yield 1;
            }
            case 2, 3 -> 2;
            case 4, 5 -> 3;
            case 6 -> {
                jet = Input.D4();
                if (this.niveau >= 7) {
                    jet += 1;
                }
                if (jet >= 3) {
                    System.out.println("L'âme de votre familier devient votre.\n");
                    yield 5;
                }
                yield 3;
            }
            default -> {
                System.out.println("Résultat non reconnu, compétence ignorée.\n");
                yield 0;
            }
        };
        corrige_ob();
        if (familier_loyalmax()) {
            System.out.println("Vous avez atteint le niveau maximal de loyauté de la part de votre familier.");
        }
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

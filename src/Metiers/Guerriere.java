package Metiers;

import Enum.*;
import Exterieur.Input;
import Monstre.Monstre;
import main.Main;

import java.io.IOException;

public class Guerriere extends Joueur {
    Metier metier = Metier.GUERRIERE;
    private boolean lame_break;
    
    public Guerriere(String nom, Position position, int ob_f, Dieux parent, int xp) {
        super(nom, position, ob_f, parent, xp);
        vie = 6;
        attaque = 2;
        PP = "aura";
        PP_value = 1;
        PP_max = 3;
        add_caracteristique("Force naturelle, Violent");
        add_competence("Berserk");
    }
    
    @Override
    protected void actualiser_niveau() {
        if (this.niveau >= 1) {
            add_competence("Rage");
        }
        if (this.niveau >= 2) {
            this.attaque += 1;
        }
        if (this.niveau >= 3) {
            add_caracteristique("Invincibilité");
        }
        if (this.niveau >= 4) {
            this.attaque += 1;
        }
        if (this.niveau >= 5) {
            add_competence("Lame d'aura");
        }
        if (this.niveau >= 6) {
            this.vie += 2;
            this.attaque += 1;
            this.PP_max += 1;
        }
        if (this.niveau >= 8) {
            this.attaque += 1;
            this.vie += 1;
        }
        if (this.niveau >= 9) {
            this.PP_max += 1;
        }
        if (this.niveau >= 10) {
            this.vie += 1;
            this.attaque += 1;
        }
    }
    
    @Override
    protected void presente_caracteristique() {
        System.out.println("Force naturelle : Augmente l'attaque classique.");
        System.out.println("Violent : Augmente la puissance des coups critiques à l'attaque classique.\n" + "Diminue "
                + "les chances de fuir sous folie meurtrière");
        if (this.niveau >= 3) {
            System.out.println("Invincibilité : Permet parfois de tromper la mort.");
        }
    }
    
    @Override
    protected void presente_pouvoir() {
        System.out.println("Berserk : pour 1 mana/aura, imprègne de folie meurtrière l'esprit du lanceur avant qu'il "
                + "ne frappe, augmentant sa puissance au prix de sa santé mentale.");
        if (this.niveau >= 1) {
            System.out.println("Rage : Pour 1 point d'aura, augmente légèrement la folie meurtrière.");
        }
        if (this.niveau >= 5) {
            System.out.println("Lame d'aura : pour 3 points d'aura, lance une attaque classique surpuissante. " + "N" + "écessite une arme pour être utilisé. Détruit les armes à la fin du combat.");
        }
    }
    
    public Metier getMetier() {
        return metier;
    }
    
    protected String nomMetier() {
        return "guerrière";
    }
    
    @Override
    void lvl_up() {
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
                add_competence("Rage");
                yield "Nouvelle capacité débloquée !";
            }
            case 2 -> {
                this.attaque += 1;
                yield "Votre attaque a légèrement augmenté.";
            }
            case 3 -> {
                add_caracteristique("Invincibilité");
                yield """
                        Nouvelle capacité débloquée !
                        Votre force mentale s'est légèrement accrue.
                        """;
            }
            case 4 -> {
                this.attaque += 1;
                yield """
                        Votre attaque a légèrement augmenté.
                        Votre force d'attaque a légèrement augmentée.
                        """;
            }
            case 5 -> {
                add_competence("Lame d'aura");
                yield """
                        Nouvelle capacité débloquée !
                        Votre précision s'été légèrement améliorée.
                        """;
            }
            case 6 -> {
                this.vie += 2;
                this.attaque += 1;
                this.PP_max += 1;
                yield """
                        Votre réserve d'aura a légèrement augmenté.
                        Votre attaque a légèrement augmenté.
                        Votre résistance a augmenté.
                        """;
            }
            case 7 -> """
                    Votre force d'attaque a légèrement augmenté.
                    Votre rage s'est légèrement intensifiée.
                    Votre précision s'été améliorée.
                    """;
            case 8 -> {
                this.attaque += 1;
                this.vie += 1;
                yield """
                        Votre attaque a légèrement augmenté.
                        Votre résistance a légèrement augmenté.
                        Votre force mentale s'est légèrement accrue.
                        """;
            }
            case 9 -> {
                this.PP_max += 1;
                yield """
                        Votre résilience s'est légèrement accrue.
                        Votre rage s'est intensifiée.
                        Votre aura s'est renforcée.
                        """;
            }
            case 10 -> {
                this.vie += 1;
                this.attaque += 1;
                yield """
                        Votre attaque a légèrement augmenté.
                        Votre résistance a augmenté.
                        Votre force d'attaque a légèrement augmenté.
                        Votre rage s'est légèrement intensifiée.
                        Votre précision s'été légèrement améliorée.
                        """;
            }
            case 11 -> "Vous avez atteint le niveau max (frappe le dev c'est sa faute).";
            default -> throw new IllegalStateException("Unexpected value: " + temp);
        };
        System.out.println(text);
    }
    
    @Override
    public void init_affrontement(boolean force, Position pos) throws IOException {
        super.init_affrontement(force, pos);
        lame_break = false;
    }
    
    @Override
    public void fin_affrontement(boolean ennemi_nomme) throws IOException {
        super.fin_affrontement(ennemi_nomme);
        if (lame_break) {
            System.out.println("Les armes de " + nom + " se brisent");
        }
    }
    
    @Override
    protected boolean folie_berserk() throws IOException {
        System.out.println("Vous êtes pris(e) de folie mertrière et distinguez mal vos alliés de vos ennemis.");
        float palier_folie = 2f + berserk;
        if (this.niveau >= 3) {
            palier_folie -= 0.5f;
        }
        if (this.niveau >= 8) {
            palier_folie -= 0.5f;
        }
        return Input.D6() < palier_folie;
    }
    
    @Override
    protected void berserk_boost(boolean is_crazy) {
        if (is_crazy) {
            berserk += 0.2f + rand.nextInt(7) * 0.1f; //0.2 à 0.8 de boost
        } else {
            this.berserk += 0.1f + rand.nextInt(4) * 0.1f; //0.1 à 0.5 de boost
        }
        int[] paliers = {7, 9, 9, 10};
        for (int palier : paliers) {
            if (this.niveau >= palier) {
                this.berserk += 0.1f;
            }
        }
    }
    
    @Override
    public String text_action() {
        String text = super.text_action();
        if (!est_berserk()) {
            text += "/(be)rserker";
        }
        if (!lame_break && this.niveau >= 5) {
            text += "/(la)me d'aura";
        }
        return text;
    }
    
    @Override
    public Action action(String choix, boolean est_familier) throws IOException {
        if (est_familier) {
            return super.action(choix, true);
        }
        switch (choix) {
            case "be" -> {
                if (!est_berserk()) {
                    return Action.BERSERK;
                }
            }
            case "la" -> {
                if (!lame_break && this.niveau >= 5) {
                    return Action.LAME_DAURA;
                }
            }
        }
        return super.action(choix, false);
    }
    
    @Override
    public boolean traite_action(Action action, Monstre ennemi, int bonus_popo) throws IOException {
        switch (action) {
            case BERSERK -> {
                berserk();
                return true;
            }
            case LAME_DAURA -> {
                lame_aura(ennemi, bonus_popo);
                return false;
            }
        }
        return super.traite_action(action, ennemi, bonus_popo);
    }
    
    @Override
    public boolean action_consomme_popo(Action action) {
        if (action == Action.LAME_DAURA) {
            return true;
        }
        return super.action_consomme_popo(action);
    }
    
    @Override
    public String text_extra(Action action) {
        String text = super.text_extra(action);
        if (this.niveau >= 1) {
            text += "/(ra)ge";
        }
        return text;
    }
    
    @Override
    public Action_extra extra(String choix) {
        if (choix.equals("ra") && this.niveau >= 1) {
            return Action_extra.RAGE;
        }
        return super.extra(choix);
    }
    
    @Override
    protected int berserk_fuite() throws IOException {
        if (!est_berserk()) {
            return 0;
        }
        float folie = berserk - Input.D4() * 0.5f;
        //rage
        int[] paliers = {7, 9, 9, 10};
        for (int palier : paliers) {
            if (this.niveau >= palier) {
                folie += 0.1f;
            }
        }
        //force mentale
        paliers = new int[]{3, 8};
        for (int palier : paliers) {
            if (this.niveau >= palier) {
                folie -= 0.2f;
            }
        }
        return Math.min(0, -Math.round(folie));
    }
    
    @Override
    protected int bonus_atk() {
        int base = super.bonus_atk();
        int[] paliers = {4, 7, 10};
        for (int palier : paliers) {
            if (this.niveau >= palier) {
                base += 1;
            }
        }
        return base;
    }
    
    @Override
    protected float berserk_atk(int base) throws IOException {
        float tolerance = 0;
        if (this.niveau >= 3) {
            tolerance += 0.5f;
        }
        if (this.niveau >= 8) {
            tolerance += 0.5f;
        }
        if (berserk >= 5.5f + tolerance) {
            return burst(base);
        }
        return super.berserk_atk(base);
    }
    
    /**
     * Extension de la méthode berserk_atk, version amplifiée
     * @param base la puissance de frappe
     * @return le bonus de dommages, ou berserk_atk_alliee si le joueur attaque un allié
     */
    private float burst(int base) {
        System.out.println(getNom() + " éclate dans une rage prodigieuse !");
        int contrecoup = rand.nextInt(Main.corriger(berserk), 7) + 2; //2~8
        //force mentale
        if (this.niveau >= 3) {
            contrecoup -= 1;
        }
        if (this.niveau >= 8) {
            contrecoup -= 1;
        }
        int rage = 0;
        if (this.niveau >= 7) {
            rage += 1;
        }
        if (this.niveau >= 9) {
            rage += 2;
        }
        if (this.niveau >= 10) {
            rage += 1;
        }
        contrecoup += rage;
        assomme(2 - contrecoup);
        return base * (berserk + 0.1f * rage) * 1.5f;
    }
    
    @Override
    protected float critique_atk(int base) {
        int imprecision = 50;
        int[] paliers = {5, 7, 7, 10};
        for (int palier : paliers) {
            if (this.niveau >= palier) {
                imprecision -= 10;
            }
        }
        if (this.niveau >= 9) { // bonus classe mère
            imprecision -= 1;
        }
        if (rand.nextInt(imprecision) == 0) { //2% à 11.1%
            return base * 0.15f * (rand.nextInt(5) + 1); //15% à 75% de bonus
        }
        return 0;
    }
    
    @Override
    protected void berserk() { //valeur différentes de la capa héréditaire
        System.out.println(nom + " est prit d'une folie meurtrière !");
        berserk = 0.2f + 0.1f * rand.nextInt(6); //0.2 à 0.7
        int[] paliers = {7, 9, 10};
        for (int palier : paliers) {
            if (this.niveau >= palier) {
                berserk += 0.1f;
            }
        }
    }
    
    /**
     * Compétence "lame d'aura", une attaque classique avec d'énorme dommage bonus
     * @param ennemi le monstre ennemi
     */
    private void lame_aura(Monstre ennemi, int bonus_popo) throws IOException {
        //noinspection DuplicatedCode C'est globalement une attaque classique
        int base = Input.atk();
        float bonus = 0;
        if (est_berserk()) {
            bonus = berserk_atk(base);
            if (bonus == berserk_atk_alliee) {
                return;
            }
        }
        bonus += critique_atk(base);
        bonus += bonus_atk();
        bonus += attaque_bonus;
        
        //capacité d'aura
        float total = base + bonus;
        if (this.niveau >= 9) {
            total *= 3.2f;
        } else {
            total *= 2.7f;
        }
        lame_break = true;
        
        total += bonus_popo; // indépendant des dommages de cac donc pas améliorés
        
        ennemi.dommage(Main.corriger(total, 3));
    }
    
    @Override
    public boolean auto_ressusciter(int malus) throws IOException {
        if (this.niveau < 3) {
            return false;
        }
        int palier_mort = 7 + malus;
        if (this.niveau >= 9) {
            palier_mort -= 1;
        }
        return (Input.D10() >= palier_mort); //40~50% sans malus
    }
}

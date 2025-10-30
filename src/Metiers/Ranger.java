package Metiers;

import Enum.Metier;
import Enum.Position;
import Enum.Action;
import Enum.Dieux;

import Exterieur.Input;
import Monstre.Monstre;
import main.Main;

import java.io.IOException;

public class Ranger extends Joueur {
    Metier metier = Metier.RANGER;

    public Ranger(String nom, Position position, int ob_f, Dieux parent, int xp) {
        super(nom, position, ob_f, parent, xp);
        vie = 4;
        attaque = 1;
        PP = "mana";
        PP_value = 2;
        PP_max = 3;
        add_caracteristique("Eclaireur, Sniper");
    }

    @Override
    protected void actualiser_niveau() {
        if (this.niveau >= 1) {
            add_competence("Coup critique");
            this.attaque += 1;
        }
        if (this.niveau >= 2) {
            add_caracteristique("Oeil d'aigle");
        }
        if (this.niveau >= 4) {
            add_competence("Assassinat");
        }
        if (this.niveau >= 5) {
            this.PP_value += 1;
            this.PP_max += 1;
            add_caracteristique("Explorateur");
        }
        if (this.niveau >= 7) {
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
        System.out.println("Eclaireur : Augmente légèrement vos jets d'exploration et de fuite.");
        System.out.println("Sniper : Augmente la puissance de vos tirs.");
        if (this.niveau >= 2) {
            System.out.printf("Oeil d'aigle : augmente la probabilité et puissance des coups " +
                    "critiques. %s", this.niveau >= 6 ? "Améliore vos capacités d'analyse.\n" : "\n");
        }
        if (this.niveau >= 5) {
            System.out.println("Explorateur : Augmente vos jet d'exploration.");
        }
    }

    @Override
    protected void presente_pouvoir() {
        if (this.niveau >= 1) {
            System.out.println("Coup critique : Pour 1 mana, tir une flèche avec une haute probabilité de faire" +
                    " des dommages additionnel, et une faible probabilitée de faire moins de dégats.");
        }
        if (this.niveau >= 4) {
            System.out.println("Assassinat : Pour 1 mana, se glisse discrètement derrière une cible pour " +
                    "lui infliger de gros dommage. Difficile à réaliser");
        }
    }

    public Metier getMetier() {
        return metier;
    }

    protected String nomMetier() {
        return "ranger";
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
                add_competence("Coup critique");
                this.attaque += 1;
                yield """
                        Nouvelle capacité débloquée !
                        Votre attaque a légèrement augmentée.
                        """;
            }
            case 2 -> {
                add_caracteristique("Oeil d'aigle");
                yield "Nouvelle compétence débloquée !";
            }
            case 3 -> "Votre précision s'est légèrement améliorée.";
            case 4 -> {
                add_competence("Assassinat");
                yield """
                        Nouvelle capacité débloquée !
                        Vos compétences de tireur se sont légèrement améliorées.
                        """;
            }
            case 5 -> {
                this.PP_value += 1;
                this.PP_max += 1;
                add_caracteristique("Explorateur");
                yield """
                        Nouvelle compétence débloquée !";
                        Votre réserve de mana s'est légèrement accrue.
                        Votre précision s'est légèrement améliorée.
                        """;
            }
            case 6 -> """
                    Votre vision s'est grandement améliorée.
                    Vos compétences de tireur se sont légèrement améliorées.
                    """;
            case 7 -> {
                this.attaque += 1;
                this.vie += 1;
                yield """
                        Votre résistance a légèrement augmenté.
                        Votre attaque a légèrement augmenté.
                        Votre expertise de terrain a augmenté.
                        """;
            }
            case 8 -> """
                    Votre vision s'est légèrement améliorée.
                    Vos compétences de tireur se sont légèrement améliorées.
                    """;
            case 9 -> {
                this.PP_max += 1;
                yield """
                        Votre réserve de mana s'est légèrement accrue.
                        Votre discrétion a augmenté.
                        Votre précision a légèrement augmenté.
                        """;
            }
            case 10 -> {
                this.vie += 1;
                this.attaque += 1;
                yield """
                        Votre attaque a légèrement augmenté.
                        Votre résistance a augmenté.
                        Vos compétences de tireurs se sont améliorées.
                        Votre vision s'est légèrement améliorée.
                        Vos compétences d'éclaireur se sont amélioré
                        """;
            }
            case 11 -> "Vous avez atteint le niveau max (frappe le dev c'est sa faute).";
            default -> throw new IllegalStateException("Unexpected value: " + temp);
        };
        System.out.println(text);
    }

    @Override
    public String text_action() {
        String text = super.text_action();
        if (est_front() && est_berserk() && this.niveau >= 4) {
            return text + "/(as)saut"; //variation cachée de assassinat
        }
        if (!est_berserk()) {
            if (!est_front() && this.niveau >= 4) {
                text += "/(as)sassinat";
            }
            if (this.niveau >= 1) {
                text += "/(co)ut critique";
            }
        }
        return text;
    }

    @Override
    public Action action(String choix, boolean est_familier) throws IOException {
        if (est_familier) {
            return super.action(choix, true);
        }
        switch (choix) {
            case "co" -> {
                if (!est_berserk() && this.niveau >= 1) {
                    return Action.CRITIQUE;
                }
            }
            case "as" -> {
                if (est_berserk() && est_front() && this.niveau >= 4) {
                    return Action.ASSAUT;
                }
                if (!est_berserk() && !est_front() && this.niveau >= 4) {
                    return Action.ASSASSINAT;
                }
            }
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi, int bonus_popo) throws IOException {
        switch (action) {
            case CRITIQUE -> {
                coup_critique(ennemi, bonus_popo);
                return false;
            }
            case ASSAUT -> {
                assaut(ennemi, bonus_popo);
                return false;
            }
            case ASSASSINAT -> {
                assassinat(ennemi, bonus_popo);
                return false;
            }
        }
        return super.traite_action(action, ennemi, bonus_popo);
    }

    @Override
    public boolean action_consomme_popo(Action action) {
        if (action == Action.CRITIQUE || action == Action.ASSAUT || action == Action.ASSASSINAT) {
            return true;
        }
        return super.action_consomme_popo(action);
    }

    @Override
    public int bonus_exploration() {
        int bonus = super.bonus_exploration();
        //éclaireur
        if (this.niveau < 10) {
            bonus += rand.nextInt(2); //0~1
        } else {
            bonus += 1;
        }
        //explorateur
        if (this.niveau >= 5) {
            if (this.niveau >= 7) {
                bonus += rand.nextInt(2) + 1; // 1~2
            } else {
                bonus += rand.nextInt(3); //0~2
            }
        }
        return bonus;
    }

    @Override
    public float critique_tir(int base) {
        if (this.niveau < 2) {
            return super.critique_tir(base);
        }
        int imprecision = 50;
        int[] paliers = {2, 6, 6, 6, 8};
        for (int palier : paliers) {
            if (this.niveau >= palier) {
                imprecision -= 8;
            }
        }
        if (this.niveau >= 9) { //bonus classe mère
            imprecision -= 1;
        }
        if (this.niveau >= 10) {
            imprecision -= 2; //on pourrait enlever 7 à chaque fois plutôt, mais cela réduit l'efficacité des autres paliers
        }
        if (rand.nextInt(imprecision) == 0) { //2% à 14.25%
            return base * 0.15f * (rand.nextInt(6) + 1); //15% à 90% de bonus
        }
        return 0;
    }

    @Override
    protected float critique_atk(int base) {
        if (this.niveau < 2) {
            return super.critique_atk(base);
        }
        int imprecision = 50;
        int[] paliers = {2, 6, 6, 6, 8, 10};
        for (int palier : paliers) {
            if (this.niveau >= palier) {
                imprecision -= 7;
            }
        }
        if (this.niveau >= 9) {
            imprecision -= 1;
        }
        if (rand.nextInt(imprecision) == 0) { //2% à 14.25%
            return base * 0.1f * (rand.nextInt(5) + 2); //20% à 60% de bonus
        }
        return 0;
    }

    @Override
    protected int bonus_tir() {
        int bonus = super.bonus_tir();
        int[] paliers = {0, 4, 6, 8, 10, 10};
        for (int palier : paliers) {
            if (this.niveau >= palier) {
                bonus += 1;
            }
        }
        return bonus;
    }

    @Override
    protected int position_fuite() {
        int retour;
        if (est_front()) {
            if (est_front_f()) {
                retour = -2;
            } else {
                retour = -3;
            }
        } else {
            return 3;
        }
        //bonus éclaireur
        if (this.niveau >= 10) {
            return retour + 1;
        }
        return retour + rand.nextInt(2);
    }

    @Override
    protected int bonus_fuite() {
        int bonus = super.bonus_fuite();
        //bonus éclaireur (potentiellement cumulé avec le position_fuite)
        if (this.niveau >= 10) {
            return bonus + 1;
        }
        return bonus + rand.nextInt(2);
    }

    /**
     * Applique la compétence "coup critique" sur un tir classique
     *
     * @param ennemi le monstre ennemi
     * @throws IOException toujours
     */
    public void coup_critique(Monstre ennemi, int bonus_popo) throws IOException {
        int tir = Input.tir() + bonus_popo;
        int jet = this.niveau >= 5 ? Input.D6() : Input.D4();
        if (this.niveau >= 3) {
            jet += rand.nextInt(2);
        }
        if (this.niveau >= 9) {
            jet += rand.nextInt(2);
        }
        if (jet < 1) {
            jet = 1;
        }
        if (jet > 8) {
            jet = 8;
        }
        switch (jet) {
            case 1 -> {
                System.out.println("La pointe de votre flèche éclate en plein vol.");
                ennemi.tir(tir, 0.5F);
            }
            case 2, 3 -> ennemi.tir(tir, 1.1F);
            case 4, 5 -> {
                System.out.println("Votre flèche file droit sur " + ennemi.getNom() + " et lui porte un coup puissant.");
                ennemi.tir(tir, 2F);
            }
            case 6, 7 -> {
                System.out.println("Votre flèche atteint " + ennemi.getNom() + " en plein crâne.");
                ennemi.tir(tir, 2.4F);
            }
            case 8 -> {
                System.out.println("Votre flèche transperce " + ennemi.getNom() + ", lui perforant des organes vitaux.");
                ennemi.tir(tir, 2.8F);
            }
            default -> {
                System.out.println("Entré invalide, tir classique appliqué.");
                ennemi.tir(tir);
            }
        }
    }

    /**
     * Appliques les effets de la compétence "assassinat"
     *
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void assassinat(Monstre ennemi, int bonus_popo) throws IOException {
        int jet = Input.D6();
        jet += rand.nextInt(3) - 1;
        jet -= 4; //difficulté
        if (this.niveau >= 9) {
            jet += 2;
        }
        if (jet > 0) {
            System.out.println("Vous vous faufilez derrière " + ennemi.getNom() + " sans qu'il ne vous remarque.");
            ennemi.dommage(Main.corriger(Input.atk() * 1.3f + 6.5f + bonus_popo));
        } else {
            ennemi.dommage(bonus_popo);
            System.out.println("Vous jugez plus prudent de ne pas engagez pour l'instant...");
        }
    }

    /**
     * Applique la compétence "assaut"
     *
     * @param ennemi le monstre ennemi
     * @throws IOException toujours
     * @implNote attaque avec des valeurs quelque peu modifiées
     */
    private void assaut(Monstre ennemi, int bonus_popo) throws IOException {
        int base = Input.atk();
        float bonus = calcule_bonus_atk_assaut(base, bonus_popo);
        if (bonus != berserk_atk_alliee) {
            System.out.println(nom + " charge brutalement " + ennemi.getNom());
            ennemi.dommage(base + Main.corriger(bonus, 2));
            ennemi.attaque(this);
        }
    }

    private float calcule_bonus_atk_assaut(int base, int bonus_popo) throws IOException {
        float bonus = calcule_bonus_atk(base, bonus_popo);
        if (bonus == berserk_atk_alliee) {
            return bonus;
        }

        int jet = Input.D8() + rand.nextInt(3) - 1;
        bonus += 0.1f * jet * base;
        return bonus;
    }
}
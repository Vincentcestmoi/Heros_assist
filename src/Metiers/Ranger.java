package Metiers;

import Enum.Action;
import Enum.Dieux;
import Enum.Metier;
import Enum.Position;
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
        this.attaque += bonus_sup10(11, 10) + bonus_sup10(14, 10);
        this.vie += bonus_sup10(13, 10) + bonus_sup10(16, 10);
        this.PP_max += bonus_sup10(15, 10);
    }
    
    @Override
    protected void presente_caracteristique() {
        System.out.println("Eclaireur : Augmente légèrement vos jets d'exploration et de fuite.");
        System.out.println("Sniper : Augmente la puissance de vos tirs.");
        if (this.niveau >= 2) {
            System.out.printf("Oeil d'aigle : augmente la probabilité et puissance des coups " + "critiques. %s",
                    this.niveau >= 6 ? "Améliore vos capacités d'analyse.\n" : "\n");
        }
        if (this.niveau >= 5) {
            System.out.println("Explorateur : Augmente vos jet d'exploration.");
        }
    }
    
    @Override
    protected void presente_pouvoir() {
        if (this.niveau >= 1) {
            System.out.println("Coup critique : Pour 1 mana, tir une flèche avec une haute probabilité de faire" + " "
                    + "des dommages additionnel, et une faible probabilitée de faire moins de dégats.");
        }
        if (this.niveau >= 4) {
            System.out.println("Assassinat : Pour 1 mana, se glisse discrètement derrière une cible pour " + "lui " + "infliger de gros dommage. Difficile à réaliser");
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
            case 11 -> niveau_sup();
            default -> throw new IllegalStateException("Unexpected value: " + temp);
        };
        System.out.println(text);
    }
    
    /**
     * Calcule les bonus de niveau supérieurs au niveau 10 (cyclique)
     * @return L'affichage du texte
     */
    private String niveau_sup() {
        int unit = this.niveau % 10;
        String text = "";
        if (unit == 1) { // 11, 21, ...
            this.attaque += 1;
            text += "Votre attaque a légèrement augmenté.\n";
        }
        if (unit % 2 == 0) { //12, 14, 16, 18, 20, 22, ...
            text += "Vos compétences de tireur se sont légèrement améliorées.\n";
        }
        if (unit == 3) { // 13, 23, 33, ...
            this.vie += 1;
            text += "Votre résistance a légèrement augmenté.\n";
        }
        if (unit == 4) { //14, 24, 34, ...
            this.attaque += 1;
            text += "Votre attaque a légèrement augmenté.\n";
        }
        if (unit == 5) { // 15, 25, 35, ...
            PP_max += 1;
            text += "Votre réserve de mana s'est légèrement accrue.\n";
        }
        if (unit == 6) { //16, 26, 36, ...
            this.vie += 1;
            text += "Votre résistance a légèrement augmenté.\n";
        }
        if (unit == 7) { //17, 27, 37, ...
            text += "Votre précision s'est légèrement améliorée.\n";
        }
        if (unit == 9) { //19, 29, ...
            text += "Votre précision a légèrement augmenté.\n";
        }
        
        if (this.niveau % 7 == 0) { //14, 21, 28, ...
            text += "Vos compétence d'analyse se sont améliorées.\n";
        }
        if (this.niveau % 9 == 0) { //18, 27, 36, ...
            text += "Vos capacité d'exploration se sont légèrement améliorées.\n";
        }
        
        return text;
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
        bonus += rand.nextInt(bonus_sup10(18, 9) + 1);
        return bonus;
    }
    
    @Override
    public int bonus_analyse() {
        int bonus = super.bonus_analyse();
        if (this.niveau >= 6) {
            bonus += 1;
        }
        bonus += bonus_sup10(14, 7);
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
            imprecision -= 2; //on pourrait enlever 7 à chaque fois plutôt, mais cela réduit l'efficacité des autres
            // paliers
        }
        if (rand.nextInt(imprecision) == 0) { //2% à 14.25%
            float bonus = base * 0.15f * (rand.nextInt(6) + 1); //15% à 90% de bonus
            bonus += base * 0.1f * rand.nextInt(bonus_sup10(17, 10) + bonus_sup10(19, 10) + 1);
            return bonus;
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
            float bonus = base * 0.1f * (rand.nextInt(5) + 2); //20% à 60% de bonus
            bonus += base * 0.1f * rand.nextInt(bonus_sup10(17, 10) + bonus_sup10(19, 10) + 1);
            return bonus;
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
        bonus += bonus_sup10(12, 2);
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
     * @param ennemi le monstre ennemi
     * @throws IOException toujours
     */
    public void coup_critique(Monstre ennemi, int bonus_popo) throws IOException {
        int tir = Input.tir() + bonus_popo;
        int jet = 0;
        if (this.niveau >= 3) {
            jet += rand.nextInt(2);
        }
        if (this.niveau >= 9) {
            jet += rand.nextInt(2);
        }
        jet += bonus_sup10(17, 10);
        jet += bonus_sup10(19, 10);
        if (jet <= 8) {
            jet += this.niveau >= 5 ? Input.D6() : Input.D4();
        }
        if (jet > 9) {
            jet = 9;
        }
        switch (jet) {
            case 2, 3 -> ennemi.tir(tir, 1.1F);
            case 4, 5 -> {
                System.out.println("Votre flèche file droit sur " + ennemi.nomme(false) + " et lui porte un coup " +
                        "puissant" + ".");
                ennemi.tir(tir, 2F);
            }
            case 6, 7 -> {
                System.out.println("Votre flèche atteint " + ennemi.nomme(false) + " en plein crâne.");
                ennemi.tir(tir, 2.4F);
            }
            case 8 -> {
                System.out.println("Votre flèche transperce " + ennemi.nomme(false) + ", lui perforant des organes " +
                        "vitaux" + ".");
                ennemi.tir(tir, 2.8F);
            }
            case 9 -> {
                System.out.println("Votre flèche transperce " + ennemi.nomme(false) + ", lui perforant des organes " +
                        "vitaux" + ".");
                ennemi.tir(tir, 4.1F);
            }
            default -> {
                System.out.println("La pointe de votre flèche éclate en plein vol.");
                ennemi.tir(tir, 0.5F);
            }
        }
    }
    
    /**
     * Appliques les effets de la compétence "assassinat"
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
            System.out.println("Vous vous faufilez derrière " + ennemi.nomme(false) + " sans qu'il ne vous remarque.");
            ennemi.dommage(Main.corriger(Input.atk() * 1.3f + 6.5f + bonus_popo));
        } else {
            System.out.println("Vous jugez plus prudent de ne pas engagez pour l'instant...");
            ennemi.dommage(bonus_popo);
        }
    }
    
    /**
     * Applique la compétence "assaut"
     * @param ennemi le monstre ennemi
     * @throws IOException toujours
     * @implNote attaque avec des valeurs quelque peu modifiées
     */
    private void assaut(Monstre ennemi, int bonus_popo) throws IOException {
        int puissance = puissance_assaut(bonus_popo);
        if (puissance != 0) {
            System.out.println(getNom() + " charge brutalement " + ennemi.nomme(false));
            ennemi.dommage(puissance);
            ennemi.attaque(this);
        }
    }
    
    private int puissance_assaut(int bonus_popo) throws IOException {
        int base = Input.atk();
        base += bonus_atk();
        if(base == 0){
            return 0;
        }
        
        float bonus = berserk_atk(base);
        if (bonus == berserk_atk_alliee) {
            return 0;
        }
        
        bonus += critique_atk(base);
        bonus += attaque_bonus;
        bonus = Main.corriger(bonus, 0);
        
        int jet = Input.D8() + rand.nextInt(3) - 1;
        bonus += 0.1f * jet * base; //0 à 90%
        return base + Main.corriger(bonus, 2) + bonus_popo;
    }
}
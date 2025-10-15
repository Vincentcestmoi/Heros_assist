package Metiers;

import Enum.Metier;
import Enum.Position;
import Enum.Action;

import Exterieur.Input;
import Monstre.Monstre;
import main.Main;

import java.io.IOException;

public class Ranger extends Joueur {
    Metier metier = Metier.RANGER;

    public Ranger(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
        vie = 4;
        attaque = 2;
        PP = "mana";
        PP_value = 3;
        PP_max = 4;
        caracteristique = "Eclaireur, Explorateur, Oeil d'aigle";
        competences = "Assassinat, Coup critique";
    }

    public Metier getMetier() {
        return metier;
    }

    protected String nomMetier(){
        return "ranger";
    }

    @Override
    public String text_action(){
        String text = super.text_action();
            if(est_front() && est_berserk()) {
                return text + "/(as)saut";
            }
            if(!est_berserk()) {
                if (!est_front()) {
                    text += "/(as)sassinat";
                }
                text += "/(co)ut critique";
            }
        return text;
    }

    @Override
    public Action action(String choix, boolean est_familier) throws IOException {
        if(est_familier){
            return super.action(choix, true);
        }
        switch (choix) {
            case "co" -> {
                if (!est_berserk()) {
                    return Action.CRITIQUE;
                }
            }
            case "as" -> {
                if (est_berserk() && est_front()) {
                        return Action.ASSAUT;
                    }
                if(!est_berserk() && !est_front()) {
                    return Action.ASSASSINAT;
                }
            }
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi, int bonus_popo) throws IOException {
        switch(action) {
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
    public boolean action_consomme_popo(Action action){
        if(action == Action.CRITIQUE || action == Action.ASSAUT || action == Action.ASSASSINAT){
            return true;
        }
        return super.action_consomme_popo(action);
    }

    @Override
    public int bonus_exploration(){
        return rand.nextInt(2) /* eclaireur */ + rand.nextInt(3)/* explorateur */;
    }

    @Override
    public float critique_tir(int base){
        if(rand.nextInt(8) == 0) { //12.5%
            return base * 0.15f * (rand.nextInt(6) + 1); //15% à 90% de bonus
        }
        return 0;
    }

    @Override
    protected float critique_atk(int base) {
        if(rand.nextInt(10) == 0) { //10%
            return base * 0.1f * (rand.nextInt(8) + 1); //10% à 80% de bonus
        }
        return 0;
    }

    @Override
    protected int bonus_tir(){
        return 3;
    }

    @Override
    protected int position_fuite() {
        if(est_front()){
            if(est_front_f()){
                return -1;
            }
            return -2;
        }
        return 3;
    }

    @Override
    protected int bonus_fuite() {
        return 2;
    }

    /**
     * Applique la compétence "coup critique" sur un tir classique
     * @param ennemi le monstre ennemi
     * @throws IOException toujours
     */
    public static void coup_critique(Monstre ennemi, int bonus_popo) throws IOException {
        int tir = Input.tir() + bonus_popo;
        switch(Input.D4()){
            case 1 -> {
                System.out.println("La pointe de votre flèche éclate en plein vol.");
                ennemi.tir(tir, 0.5F);
            }
            case 2, 3 -> ennemi.tir(tir);
            case 4, 5 -> {
                System.out.println("Votre flèche file droit sur " + ennemi.getNom() + " et lui porte un coup puissant.");
                ennemi.tir(tir, 2F);
            }
            default -> {
                System.out.println("Entré invalide, tir classique appliqué.");
                ennemi.tir(tir);
            }
        }
    }

    /**
     * Appliques les effets de la compétence "assassinat"
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void assassinat(Monstre ennemi, int bonus_popo) throws IOException {
        if(Input.D6() + rand.nextInt(3) - 1 > 3){
            System.out.println("Vous vous faufilez derrière " + ennemi.getNom() + " sans qu'il ne vous remarque.");
            ennemi.dommage(Main.corriger(Input.atk() * 1.3f + 6.5f + bonus_popo));
        }
        else {
            ennemi.dommage(bonus_popo);
            System.out.println("Vous jugez plus prudent de ne pas engagez pour l'instant...");
        }
    }

    /**
     * Applique la compétence "assaut"
     * @param ennemi le monstre ennemi
     * @throws IOException toujours
     */
    private void assaut(Monstre ennemi, int bonus_popo) throws IOException {
        System.out.println("Vous chargez brutalement " + ennemi.getNom());
        int jet = Input.D8() + rand.nextInt(3) - 1;
        int base = Input.atk();
        float bonus = 0.1f * jet * base;
        if (est_berserk()) {
            bonus = berserk_atk(base);
            if (bonus == berserk_atk_alliee) {
                return;
            }
        }
        bonus += critique_atk(base);
        bonus += bonus_atk();
        bonus += attaque_bonus;
        bonus += bonus_popo;
        ennemi.dommage(base + Main.corriger(bonus, 2));
        ennemi.attaque(this);
    }

}

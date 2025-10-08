package Metiers;

import Enum.Metier;
import Enum.Position;
import Enum.Action;

import Monstre.Monstre;

import java.io.IOException;

public class Ranger extends Joueur {
    Metier metier = Metier.RANGER;

    public Ranger(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
    }

    public Metier getMetier() {
        return metier;
    }

    @Override
    public void presente_base() {
        System.out.println("Metiers.Ranger");
        System.out.println("Enum.Base : Résistance : 4 ; attaque : 2 ; PP: 4/4");
        System.out.println("Caractéristiques : Explorateur, Eclaireur, Tireur d'élite, Assassin");
        System.out.println("Pouvoir : Assassinat, Coup critique, Assaut");
    }

    @Override
    public String text_action(){
        String text = super.text_action();
            if(est_front() && est_berserk()) {
                return text + "/(as)saut";
            }
            if(!est_berserk()) {
                if (!est_front()) {
                    text += "(as)sassinat";
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
    public boolean traite_action(Action action, Monstre ennemi) throws IOException {
        switch(action) {
            case CRITIQUE -> {
                Sort.coup_critique(ennemi);
                return false;
            }
            case ASSAUT -> {
                //Metiers.Sort.assaut(ennemi); TODO
                return false;
            }
            case ASSASSINAT -> {
                Sort.assassinat(ennemi);
                return false;
            }
        }
        return super.traite_action(action, ennemi);
    }

    public int bonus_exploration(){
        return rand.nextInt(2) /* eclaireur */ + rand.nextInt(3)/* explorateur */;
    }

    @Override
    public void fin_tour_combat(){

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

}

package Metiers;

import Exterieur.Input;

import Enum.Metier;
import Enum.Position;
import Enum.Action;

import Monstre.Monstre;

import java.io.IOException;

public class Shaman extends Joueur {
    Metier metier = Metier.SHAMAN;

    public Shaman(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
        vie = 4;
        attaque = 1;
        PP = "mana";
        PP_value = 0;
        PP_max = 0;
        caracteristique = "Ame errante, Second souffle, Eclaireur";
        competences = "Incantation, Lien, Paix intérieure";
    }

    public Metier getMetier() {
        return metier;
    }

    protected String nomMetier(){
        return "shaman";
    }

    @Override
    public String text_action() {
        String text = super.text_action();
        if (!est_berserk()) {
            if (!a_familier()) {
                text += "/(li)en";
            }
            text += "/(in)cantation";
        }
        else {
            text += "/(pa)ix intérieure";
        }
        return text;
    }

    @Override
    public Action action(String choix, boolean est_familier) throws IOException {
        if(est_familier){
            return super.action(choix, true);
        }
        switch (choix) {
            case "in" -> {
                return Action.INCANTATION;
            }
            case "li" -> {
                if (!a_familier()) {
                    return Action.LIEN;
                }
            }
            case "pa" -> {
                if (est_berserk()) {
                    return Action.CALME;
                }
            }
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi) throws IOException {
        switch(action) {
            case INCANTATION -> {
                //Metiers.Sort.incantation(ennemi); TODO
                return false;
            }
            case LIEN -> {
                //Metiers.Sort.lien(ennemi); TODO
                return false;
            }
            case CALME -> {
                calme();
                return false;
            }
        }
        return super.traite_action(action, ennemi);
    }

    @Override
    public int bonus_exploration(){
        return rand.nextInt(2) /* eclaireur */;
    }

    @Override
    public void fin_tour_combat(){

    }

    @Override
    public boolean peut_jouer() {
        // peut jouer inconscient
        return est_actif() && !skip;
    }

    @Override
    public boolean peut_ressuciter() {
        return true;
    }

    public boolean ressuciter(int malus) throws IOException {
        if (malus > 2) {
            malus = 2;
        }
        int jet = Input.D6() - malus + rand.nextInt(3) - 1;
        if (jet <= 4) {
            System.out.println("Echec de la résurection");
            return false;
        }
        if (jet <= 6) {
            System.out.println("Résurection avec 1 (max) points de vie");
        }
        else {
            System.out.println("Résurection avec 2 (max) points de vie");
        }
        return true;
    }

    @Override
    public boolean peut_diriger_familier(){
        return est_actif() && a_familier_actif() && est_vivant();
    }

    @Override
    protected int berserk_fuite() throws IOException {
        if(!est_berserk()){
            return 0;
        }
        return Math.min(0, Math.round(Input.D6() * 0.1f - berserk));
    }

    //*********************************************METHODE PERSO******************************************************//

    private void calme() {
        System.out.println(nom + " s'harmonise avec l'univers et laisse retomber sa rage.");
        this.berserk = 0f;
    }

}
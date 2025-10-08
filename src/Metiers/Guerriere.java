package Metiers;

import Exterieur.Input;

import Enum.Metier;
import Enum.Position;
import Enum.Action;

import Monstre.Monstre;

import main.Main;

import java.io.IOException;

public class Guerriere extends Joueur {
    Metier metier = Metier.GUERRIERE;

    public Guerriere(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
    }

    public Metier getMetier() {
        return metier;
    }

    @Override
    public void presente_base() {
        System.out.println("Guerrier");
        System.out.println("Enum.Base : Résistance : 6 ; attaque : 2 ; PP: 1/5");
        System.out.println("Caractéristiques : Invincible");
        System.out.println("Pouvoir : Berserk, Lame d'aura");
    }

    @Override
    public void fin_tour_combat(){

    }

    @Override
    protected float berserk_tir(int base) throws IOException {
        if (Input.D6() < 1.5f + berserk) {
            int i;
            do {
                i = rand.nextInt(Main.nbj);
            } while (!Main.joueurs[i].est_actif());
            int temp = Input.tir();
            temp += Main.corriger(temp * (berserk * 0.4f));
            System.out.println("Pris(e) de folie, " + nom + " attaque " + Main.joueurs[i].getNom() + " et lui inflige " + temp + " dommages !");
            return berserk_tir_alliee;
        }
        return base * berserk - rand.nextInt(3);
    }

    @Override
    public String text_action() {
        String text = super.text_action();
        if (!est_berserk()) {
            text += "/(be)rserker";
        }
        text += "/(la)me d'aura";
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
                return Action.LAME_DAURA;
            }
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi) throws IOException {
        switch(action) {
            case BERSERK -> {
                //Metiers.Sort.berserk(); TODO
                return true;
            }
            case LAME_DAURA -> {
                //Metiers.Sort.sort(ennemi); TODO
                return false;
            }
        }
        return super.traite_action(action, ennemi);
    }

    @Override
    protected int berserk_fuite() throws IOException {
        if(!est_berserk()){
            return 0;
        }
        return Math.min(0, Math.round(Input.D4() * 0.3f - berserk));
    }

    @Override
    protected int bonus_atk(){
        return 1;
    }

    @Override
    protected float berserk_atk(int base) throws IOException {
        if(berserk + 1.5f >= 5.5f){
            return burst(base);
        }
        if (Input.D6() < 1.5f + berserk) {
            int i;
            do {
                i = rand.nextInt(Main.nbj + 1);
            } while (i != Main.nbj || !Main.joueurs[i].est_actif());
            int bonus = Main.corriger(base * (berserk * 0.7f));
            berserk += 0.2f + rand.nextInt(7) * 0.1f; //0.2 à 0.8 de boost
            if(i == Main.nbj){
                return bonus;
            }
            System.out.println("Pris(e) de folie, " + nom + " attaque " + Main.joueurs[i].getNom() + " et lui inflige " + (base + bonus) + " dommages !");
            return berserk_atk_alliee;
        }
        berserk += 0.1f + rand.nextInt(5) * 0.1f; //0.1 à 0.5 de boost
        return base * berserk;
    }

    /**
     * Extension de la méthode berserk_atk, version amplifiée
     * @param base la puissance de frappe
     * @return le bonus de dommages, ou berserk_atk_alliee si le joueur attaque un allié
     */
    private float burst(int base) {
        System.out.println(getNom() + " éclate dans une rage prodigieuse !");
        int contrecoup = rand.nextInt(Main.corriger(berserk), 6) + 2; //2~8 normalement
        assomme(2 - contrecoup);
        return base * berserk * 1.5f;
    }

    @Override
    protected float critique_atk(int base) {
        if(rand.nextInt(8) == 0) { //12.5%
            return base * 0.25f * (rand.nextInt(4) + 1); //25% à 100% de bonus
        }
        return 0;
    }

}

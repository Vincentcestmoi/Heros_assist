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
    private boolean lame_break;

    public Guerriere(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
        vie = 6;
        attaque = 2;
        PP = "aura";
        PP_value = 1;
        PP_max = 5;
        caracteristique = "Force naturelle, Invincibilité";
        competences = "Berserk, Lame d'aura";
    }

    public Metier getMetier() {
        return metier;
    }

    protected String nomMetier(){
        return "guerrière";
    }

    @Override
    public void init_affrontement(boolean force, Position pos) throws IOException {
        super.init_affrontement(force, pos);
        lame_break = false;
    }

    @Override
    public void fin_affrontement(){
        super.fin_affrontement();
        if(lame_break){
            System.out.println("Vos armes se brisent");
        }
    }

    @Override
    protected float berserk_tir(int base) throws IOException {
        System.out.println("Vous êtes pris(e) de folie mertrière et distinguez mal vos alliés de vos ennemis.");
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
        if(!lame_break) {
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
                if(!lame_break) {
                    return Action.LAME_DAURA;
                }
            }
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi) throws IOException {
        switch(action) {
            case BERSERK -> {
                berserk();
                return true;
            }
            case LAME_DAURA -> {
                lame_aura(ennemi);
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
        System.out.println("Vous êtes pris(e) de folie mertrière et distinguez mal vos alliés de vos ennemis.");
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

    /**
     * Lance le sort berserk : rand le joueur berserk
     */
    private void berserk(){
        System.out.println(nom + " est prit d'une folie meurtrière !");
        berserk = 0.2f + 0.1f * rand.nextInt(9); //0.2 à 1
    }


    /**
     * Compétence "lame d'aura", une attaque classique avec d'énorme dommage bonus
     * @param ennemi le monstre ennemi
     */
    private void lame_aura(Monstre ennemi) throws IOException {
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
        //bonus += modificateur; TODO

        //capacité d'aura
        float total = base + bonus;
        total *= 2.7f;
        lame_break = true;

        ennemi.dommage(Main.corriger(total, 3));
    }

    @Override
    public boolean auto_ressuciter(int malus) throws IOException{
        return (Input.D10() > 7); //30%
    }

}

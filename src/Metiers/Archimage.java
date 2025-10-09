package Metiers;

import Exterieur.Input;

import Enum.Metier;
import Enum.Position;
import Enum.Action;

import Monstre.Monstre;
import main.Main;

import java.io.IOException;

public class Archimage extends Joueur {
    Metier metier = Metier.ARCHIMAGE;

    public Archimage(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
        vie = 4;
        attaque = 0;
        PP = "mana";
        PP_value = 8;
        PP_max = 10;
        caracteristique = "Double sort, Manchot, Bruyant";
        competences = "Sort, Méditation, Purge";
    }

    public Metier getMetier() {
        return metier;
    }

    protected String nomMetier(){
        return "archimage";
    }

    @Override
    public String text_tour(){
        return  "/(me)ditation";
    }

    @Override
    public boolean tour(String choix) throws IOException {
        if(choix.equalsIgnoreCase("me")){
            Sort.meditation();
            return true;
        }
        return false;
    }

    @Override
    public String text_action() {
        String text = super.text_action();
        if (!est_berserk()) {
            text += "/(me)ditation/(so)rt";
        }
        return text;
    }

    @Override
    public Action action(String choix, boolean est_familier) throws IOException {
        if(est_familier){
            return super.action(choix, true);
        }
        switch(choix) {
            case "me" -> {
                if (!est_berserk()) {
                    return Action.MEDITATION;
                }
            }
            case "so" -> {
                if (!est_berserk()) {
                    return Action.SORT;
                }
            }
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi) throws IOException {
        switch(action) {
            case MEDITATION -> {
                Sort.meditation();
                return false;
            }
            case SORT -> {
                //Metiers.Sort.sort(ennemi); TODO
                return false;
            }
        }
        return super.traite_action(action, ennemi);
    }

    @Override
    public int bonus_exploration(){
        return rand.nextInt(2) - 1 /* bruyant */;
    }

    @Override
    public void fin_tour_combat(){

    }

    @Override
    public void essaie_reveil() throws IOException {
        // l'archimage peut se réveiller via un sort
        if (Input.yn("Utiliser purge (3PP) pour reprendre conscience ?")) {
            System.out.println(nom + " se réveille.\n");
            conscient = true;
            reveil = 0;
        }
        else{
            super.essaie_reveil();
        }
        if(est_assomme()){
            System.out.println(nom + " récupère 1 point de mana.");
        }
    }

    private void onde_choc(Monstre ennemi) throws IOException {

        // sur les participants sauf le lanceur
        for(int i = 0; i < Main.nbj; i++) {
            Joueur joueur = Main.joueurs[i];
            if (joueur != this && joueur.est_actif()) {
                joueur.choc();
            }
        }

        // sur l'ennemi
        System.out.println(ennemi.getNom() + " est frappé par l'onde de choc.");
        System.out.print(this.getNom() + " : ");
        switch (Input.D6()){
            case 2 -> ennemi.do_etourdi();
            case 3, 4 -> ennemi.affecte();
            case 5, 6 -> ennemi.do_assomme();
            default -> System.out.println(ennemi.getNom() + " n'a pas l'air très affecté...");
        }
    }

}

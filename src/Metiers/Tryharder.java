package Metiers;

import Enum.Metier;
import Enum.Position;

public class Tryharder extends Joueur {
    Metier metier = Metier.TRYHARDER;

    public Tryharder(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
        vie = 5;
        attaque = 1;
        PP = "mana";
        PP_value = 6;
        PP_max = 6;
        caracteristique = "Déterminé";
        competences = "Aucune";
    }

    public Metier getMetier() {
        return metier;
    }

    protected String nomMetier(){
        return "tryharder";
    }

}
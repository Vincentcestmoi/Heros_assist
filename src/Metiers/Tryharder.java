package Metiers;

import Enum.Metier;
import Enum.Position;
import Enum.Dieux;

public class Tryharder extends Joueur {
    Metier metier = Metier.TRYHARDER;

    public Tryharder(String nom, Position position, int ob_f, Dieux parent, int xp) {
        super(nom, position, ob_f, parent, xp);
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
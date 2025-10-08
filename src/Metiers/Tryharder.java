package Metiers;

import Enum.Metier;
import Enum.Position;

public class Tryharder extends Joueur {
    Metier metier = Metier.TRYHARDER;

    public Tryharder(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
    }

    public Metier getMetier() {
        return metier;
    }

    @Override
    public void presente_base() {
        System.out.println("tryharder");
        System.out.println("Enum.Base : Résistance : 5 ; attaque : 1 ; PP: 5/5");
        System.out.println("Caractéristiques : Determiné");
        System.out.println("Pouvoir : Aucun");
    }

}
package Metiers;

import Enum.Dieux;
import Enum.Grade;
import Enum.Metier;
import Enum.Position;

public class Artisan extends Joueur {
    final Metier metier = Metier.ARTISAN;
    
    public Artisan(String nom, Position position, int ob_f, Dieux parent, int xp, Grade grade) {
        super(nom, position, ob_f, parent, xp, grade);
        vie = 3;
        attaque = 1;
        PP = "mana";
        PP_value = 1;
        PP_max = 3;
        add_caracteristique("blouse de travail");
        add_competence("Artisan");
    }
    
    @Override
    protected void actualiser_niveau() {
    
    }
    
    @Override
    void presente_caracteristique() {
        System.out.println("blouse de travail : stock jusqu'à 3 matériaux.");
    }
    
    @Override
    void presente_pouvoir() {
        System.out.println("Artisan : peut améliorer des objets en utilisant les matériau nécessaires.");
    }
    
    @Override
    public Metier getMetier() {
        return metier;
    }
    
    @Override
    protected String nomMetier() {
        return "artisan";
    }
    
    @Override
    void lvl_up() {
    
    }
}

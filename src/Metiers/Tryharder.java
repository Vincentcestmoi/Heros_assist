package Metiers;

import Enum.Metier;
import Enum.Position;
import Enum.Dieux;

public class Tryharder extends Joueur {
    Metier metier = Metier.TRYHARDER;

    public Tryharder(String nom, Position position, int ob_f, Dieux parent, int xp) {
        super(nom, position, ob_f, parent, xp);
        vie = 3;
        attaque = 1;
        PP = "mana";
        PP_value = 2;
        PP_max = 2;
        add_caracteristique("Déterminé");
        SetEffetParent();
    }

    @Override
    protected void actualiser_niveau() {
        this.vie += this.niveau;
        this.attaque += (this.niveau + 1) / 3;
        this.PP_max += this.niveau / 3;
        if(this.niveau >= 5){
            add_caracteristique("Doué");
        }
        if(this.niveau >= 8){
            add_caracteristique("Talentieux");
        }
    }

    @Override
    protected void presente_caracteristique(){
        System.out.println("Déterminé : Gagne des statistiques avec la pratique.");
        if(this.niveau >= 5){
            System.out.println("Doué : Gagne plus rapidement de l'expérience.");
        }
        if(this.niveau >= 8){
            System.out.println("Talentueux : Gagne encore plus rapidement de l'expérience.");
        }
    }

    @Override
    protected void presente_pouvoir(){
    }

    @Override
    //le tryhardeur progresse 20% plus vite (palier par 4 au lieu de 5).
    public void gagneXp(){
        gagneXpLocal();
        if(this.niveau >= 5 && rand.nextInt(10) == 0){ // +10%
            gagneXpLocal();
        }
        if(this.niveau >= 8 && rand.nextInt(15) <= 1){ // +13%
            gagneXpLocal();
        }
        if(getXplocal() >= (this.niveau + 1) * 4){
            int dif = getXplocal() - this.niveau * 4;
            this.niveau += 1;
            resetXpLocal();
            for(int i = 0; i < dif; i++){
                gagneXpLocal();
            }
            System.out.println(nom + " a gagné un niveau !");
            super_lvl_up();
        }
    }

    @Override
    void lvl_up() {
        this.vie += 1;
        System.out.println("Votre résistance a légèrement augmenté.");
        if(this.niveau % 3 == 2){
            this.attaque += 1;
            System.out.println("Votre attaque a légèrement augmenté.");
        }
        if(this.niveau % 3 == 0){
            this.PP_max += 1;
            System.out.println("Votre réserve de mana a légèrement augmentée.");
        }
        if(this.niveau == 5){
            add_caracteristique("Doué");
            System.out.println("Nouvelle capacité débloquée !");
        }
        if(this.niveau == 8){
            add_caracteristique("Talentieux");
            System.out.println("Nouvelle capacité débloquée !");
        }
    }

    public Metier getMetier() {
        return metier;
    }

    protected String nomMetier(){
        return "tryharder";
    }

}
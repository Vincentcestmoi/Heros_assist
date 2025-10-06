import java.io.IOException;

public class Archimage extends Joueur {
    Metier metier = Metier.ARCHIMAGE;

    public Archimage(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
    }

    protected Metier getMetier() {
        return metier;
    }

    @Override
    public void presente_base() {
        System.out.println("Archimage");
        System.out.println("Base : Résistance : 4 ; attaque : 0 ; PP: 10/10");
        System.out.println("Caractéristiques : Addiction au mana, Maitre de l'énergie, Double incantateur, Manchot, Bruyant");
        System.out.println("Pouvoir : Méditation, Boule de feu, Armure de glace, Foudre, Onde de choc, Purge");
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

}

import java.io.IOException;

public class Guerriere extends Joueur {
    Metier metier = Metier.GUERRIERE;

    public Guerriere(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
    }

    protected Metier getMetier() {
        return metier;
    }

    @Override
    public void presente_base() {
        System.out.println("Guerrier");
        System.out.println("Base : Résistance : 6 ; attaque : 2 ; PP: 1/5");
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
        if (Input.D6() < 1.5f + berserk) {
            int i;
            do {
                i = rand.nextInt(Main.nbj);
            } while (!Main.joueurs[i].est_actif());
            int temp = Input.atk();
            temp += Main.corriger(temp * (berserk * 0.7f));
            System.out.println("Pris(e) de folie, " + nom + " attaque " + Main.joueurs[i].getNom() + " et lui inflige " + temp + " dommages !");
            berserk += 0.2f + rand.nextInt(7) * 0.1f; //0.2 à 0.8 de boost
            return berserk_atk_alliee;
        }
        berserk += 0.1f + rand.nextInt(5) * 0.1f; //0.1 à 0.5 de boost
        return base * berserk;
    }

    @Override
    protected float critique_atk(int base) {
        if(rand.nextInt(8) == 0) { //12.5%
            return base * 0.25f * (rand.nextInt(4) + 1); //25% à 100% de bonus
        }
        return 0;
    }

}

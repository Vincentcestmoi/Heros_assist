import java.io.IOException;

public class Alchimiste extends Joueur {
    Metier metier = Metier.ALCHIMISTE;

    public Alchimiste(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
    }

    protected Metier getMetier() {
        return metier;
    }

    @Override
    public void presente_base() {
        System.out.println("Alchimiste");
        System.out.println("Base : Résistance : 5 ; attaque : 1 ; PP: 0/0 ; ingrédient : 3/11");
        System.out.println("Caractéristiques : Dextérité");
        System.out.println("Pouvoir : Fouille, Dissection, Concoction");
    }


    @Override
    public String text_tour(){
        return "/(fo)uiller/(co)ncocter des potions";
    }

    @Override
    public boolean tour(String choix) throws IOException {
        if(choix.equalsIgnoreCase("fo")){
            Sort.fouille();
            return true;
        }
        if(choix.equalsIgnoreCase("co")){
            Sort.concocter();
            return true;
        }
        return false;
    }

    @Override
    public String text_action() {
        String text = super.text_action();
        if (!est_berserk()) {
            text += "/(co)ncocter des potions/(fo)uiller";
        }
        return text;
    }

    @Override
    public Action action(String choix, boolean est_familier) throws IOException {
        if(est_familier){
            return super.action(choix, true);
        }
        switch (choix) {
            case "fo" -> {
                if (!est_berserk()) {
                    return Action.FOUILLE;
                }
            }
            case "co" -> {
                if (!est_berserk()) {
                    return Action.CONCOCTION;
                }
            }
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi) throws IOException {
        switch (action) {
            case FOUILLE -> {
                if (!est_berserk()) {
                    Sort.fouille();
                    return false;
                }
            }
            case CONCOCTION -> {
                if (!est_berserk()) {
                    Sort.concocter();
                    return false;
                }
            }
        }
        return super.traite_action(action, ennemi);
    }

    public void fin_tour_combat(){

    }

    @Override
    public boolean peut_ressuciter() {
        return true;
    }

    @Override
    public boolean ressuciter(int malus) throws IOException {
        if(malus > 3){
            malus = 3;
        }
        if (Input.yn("Utilisez vous une potion divine ?")) {
            System.out.println("Résurection avec " + switch (Input.D6() - malus) {
                case 2 -> "2";
                case 3, 4 -> "4";
                case 5, 6 -> "6";
                case 7, 8 -> "8";
                default -> "1";
            } + " points de vie.");
            return true;
        }
        if (Input.yn("Utilisez vous un élixir ?")) {
            System.out.println("Résurection avec " + switch (Input.D20()) {
                case 4, 5, 6 -> "3 points de vie et 4";
                case 7, 8 -> "5 points de vie et 7";
                case 9, 10 -> "6 points de vie et 9";
                case 11, 12 -> "6 points de vie et 12";
                case 13, 14, 15 -> "7 points de vie et 13";
                case 16, 17 -> "7 points de vie et 14";
                case 18, 19 -> "9 points de vie et 14";
                case 20, 21, 22 -> "9 points de vie et 16";
                default -> "2 points de vie et 3";
            } + " points de résistance additionels.");
            return true;
        }
        System.out.println("Vous n'avez aucun moyen de ressuciter la cible.");
        return false;
    }

}
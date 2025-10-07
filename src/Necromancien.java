import java.io.IOException;
import java.util.Objects;

public class Necromancien extends Joueur {
    Metier metier = Metier.NECROMANCIEN;

    public Necromancien(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
    }

    protected Metier getMetier() {
        return metier;
    }

    @Override
    public void presente_base() {
        System.out.println("Necromancien");
        System.out.println("Base : Résistance : 4 ; attaque : 1 ; PP: 6/8");
        System.out.println("Caractéristiques : Thaumaturge, Rite sacrificiel");
        System.out.println("Pouvoir : Appel des morts, Résurrection, Zombification, Malédiction");
    }

    @Override
    public String text_tour(){
        return  "/(ap)pel des morts";
    }

    @Override
    public boolean tour(String choix) throws IOException {
        if(choix.equalsIgnoreCase("ap")){
            Sort.necromancie(getPosition());
            return true;
        }
        return false;
    }

    @Override
    public String text_action() {
        String text = super.text_action();
        if (!est_berserk()) {
            text += "/(ma)udir";
        }
        return text;
    }

    @Override
    public Action action(String choix, boolean est_familier) throws IOException {
        if(est_familier){
            return super.action(choix, true);
        }
        if(choix.equals("ma") && !est_berserk()) {
            return Action.MAUDIR;
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi) throws IOException {
        if (Objects.requireNonNull(action) == Action.MAUDIR && !est_berserk()) {
            Sort.fouille();
            return false;
        }
        return super.traite_action(action, ennemi);
    }

    @Override
    public void fin_tour_combat(){

    }

    @Override
    public boolean peut_ressuciter() {
        return true;
    }

    @Override
    public boolean ressuciter(int malus) throws IOException {
        if (malus > 5) {
            malus = 5;
        }
        int jet = Input.D8() - malus + rand.nextInt(3) - 1;
        if (jet <= 3) {
            System.out.println("Echec de la résurection");
            return false;
        }
        if (jet <= 5) {
            System.out.println("Résurection avec 4 (max) points de vie");
        } else if (jet <= 7) {
            System.out.println("Résurection avec 8 (max) points de vie");
        } else {
            System.out.println("Résurection avec 12 (max) points de vie");
        }
        return true;
    }

}

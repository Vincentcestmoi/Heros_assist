public class Tryharder extends Joueur {
    Metier metier = Metier.RANGER;

    public Tryharder(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
    }

    protected Metier getMetier() {
        return metier;
    }

    @Override
    public void presente_base() {
        System.out.println(Output.barrer("chomeur") + "tryharder");
        System.out.println("Base : Résistance : 5 ; attaque : 1 ; PP: 5/5");
        System.out.println("Caractéristiques : Determiné");
        System.out.println("Pouvoir : Aucun");
    }

}
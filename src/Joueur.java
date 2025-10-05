import java.io.IOException;
import java.util.Random;

public class Joueur {
    private final String nom;
    private Position position;
    private final Metier metier;
    private int ob_f;

    Joueur(String nom, Position position, Metier metier, int ob_f) {
        this.nom = nom;
        this.position = position;
        this.metier = metier;
        this.ob_f = ob_f;
    }

    static Random rand = new Random();

    //************************************************GETTER**********************************************************//

    public boolean est_alchimiste(){
        return metier.equals(Metier.ALCHIMISTE);
    }

    public boolean est_archimage(){
        return metier.equals(Metier.ARCHIMAGE);
    }

    public boolean est_ranger(){
        return metier.equals(Metier.RANGER);
    }

    public boolean est_necromancien(){
        return metier.equals(Metier.NECROMANCIEN);
    }

    public boolean est_shaman(){
        return metier.equals(Metier.SHAMAN);
    }

    public boolean est_guerriere(){
        return metier.equals(Metier.GUERRIERE);
    }

    public boolean a_familier(){
        return ob_f > 0;
    }

    public boolean familier_loyalmax(){
        return ob_f >= Main.f_max;
    }

    public Position getPosition() {
        return position;
    }

    public String getNom() {
        return nom;
    }

    public int get_ob_f() {
        return ob_f;
    }

    //************************************************PRESENTATION****************************************************//

    public void presente_base(){
        switch(metier){
            case ALCHIMISTE -> {
                System.out.println("Alchimiste");
                System.out.println("Base : Résistance : 5 ; attaque : 1 ; PP: 0/0 ; ingrédient : 3/11");
                System.out.println("Caractéristiques : Dextérité");
                System.out.println("Pouvoir : Fouille, Dissection, Concoction");
            }
            case ARCHIMAGE -> {
                System.out.println("Archimage");
                System.out.println("Base : Résistance : 4 ; attaque : 0 ; PP: 10/10");
                System.out.println("Caractéristiques : Addiction au mana, Maitre de l'énergie, Double incantateur, Manchot, Bruyant");
                System.out.println("Pouvoir : Méditation, Boule de feu, Armure de glace, Foudre, Onde de choc, Purge");
            }
            case RANGER -> {
                System.out.println("Ranger");
                System.out.println("Base : Résistance : 4 ; attaque : 2 ; PP: 4/4 ; tir : 3");
                System.out.println("Caractéristiques : Explorateur, Eclaireur");
                System.out.println("Pouvoir : Assassinat, Coup critique, Assaut");
            }
            case NECROMANCIEN -> {
                System.out.println("Necromancien");
                System.out.println("Base : Résistance : 4 ; attaque : 1 ; PP: 6/8");
                System.out.println("Caractéristiques : Thaumaturge, Rite sacrificiel");
                System.out.println("Pouvoir : Appel des morts, Résurrection, Zombification, Malédiction");
            }
            case SHAMAN -> {
                System.out.println("Shaman");
                System.out.println("Base : Résistance : 4 ; attaque : 1 ; PP: 0/0");
                System.out.println("Caractéristiques : Ancien esprit, Eclaireur");
                System.out.println("Pouvoir : Lien, Incantation, Paix intérieure");
            }
            case GUERRIERE -> {
                System.out.println("Guerrier");
                System.out.println("Base : Résistance : 6 ; attaque : 3 ; PP: 1/5");
                System.out.println("Caractéristiques : Invincible");
                System.out.println("Pouvoir : Berserk, Lame d'aura");
            }
            case AUCUN -> {
                System.out.println(Output.barrer("chomeur") + "tryharder");
                System.out.println("Base : Résistance : 5 ; attaque : 1 ; PP: 6/6");
                System.out.println("Caractéristiques : Aucune");
                System.out.println("Pouvoir : Aucun");
            }
        }
    }

    public void presente(){
        System.out.print(this.nom + " est " + Output.texte_metier(this.metier) + " et se trouve " + Main.texte_pos(this.position));
        if(a_familier()){
            System.out.print(" avec son familier");
        }
        System.out.println(".");
    }

    //************************************************METHODE INDEPENDANTE********************************************//

    public void descendre(){
        this.position = switch (position) {
            case VIGNES -> Position.PRAIRIE;
            case TEMPLE -> Position.VIGNES;
            case MER -> Position.TEMPLE;
            case MONTS -> Position.MER;
            case ASCENDANT -> {
                System.out.println("ERROR : DONOT");
                yield Position.PRAIRIE;
            }
            default -> { //ENFERS, PRAIRIES, OLYMPE
                System.out.println("Erreur : position " + position + " ne peut être descendue !");
                yield position;
            }
        };
    }
    
    public void ascendre(int index) throws IOException {
        Position pos = position;
        position = Position.ASCENDANT;  //on isole le joueur
        String text = nom;
        int lead = -1;
        Monstre m;
        int bonus = bonus_exploration();
        int jet = Input.D4() + bonus;
        if (jet > 4){
            jet = 4;
        }
        else if (jet < 1){
            jet = 1;
        }
        switch (jet) {
            case 1 -> {
                text += " rencontre un monstre vers la fin de son voyage.";
                m = Lieu.true_monstre(pos, true);
            }
            case 2 -> {
                text += " est attaqué par un monstre à peine parti(e).";
                lead = index;
                m = Lieu.true_monstre(pos);
            }
            case 3 -> {
                text += " rencontre un monstre à peine après le début de son voyage.";
                m = Lieu.true_monstre(pos);
            }
            case 4 -> {
                position = pos;
                monter();
                System.out.println(text + " parvient sans encombre " + Main.texte_pos(position) + ".");
                return;
            }
            default -> {
                System.out.println("Erreur : résultat inatendu. Action annulée.");
                position = pos;
                return;
            }
        }
        System.out.println(text);
        Combat.affrontement(Position.ASCENDANT, lead, m);
        if(position == Position.ENFERS){ //le joueur est mort.
            return;
        }
        position = pos;
        if (Input.yn(nom + " a-t-il vaincu le monstre ?")) {
            monter();
            System.out.println(nom + " arrive " + Main.texte_pos(position) + ".");
        } else {
            System.out.println(nom + " est resté " + Main.texte_pos(position));
        }
    }
    
    private void monter(){
        position = switch (position) {
            case ENFERS -> Position.PRAIRIE;
            case PRAIRIE -> Position.VIGNES;
            case VIGNES -> Position.TEMPLE;
            case TEMPLE -> Position.MER;
            case MER -> Position.MONTS;
            case MONTS -> Position.OLYMPE;
            case OLYMPE -> {
                System.out.println("Erreur : position " + position + " ne peut être augmentée !");
                yield position;
            }
            case ASCENDANT -> {
                System.out.println("ERROR : DONOT");
                yield Position.ENFERS;
            }
        };
    }

    public void dresser() throws IOException {
        if (!a_familier()) {
            System.out.println("Erreur : aucun familier détecté.");
            return;
        }
        ob_f += entrainer();
        if (!a_familier()) {
            System.out.println("Votre familier vous a fuit de manière définitive.");
        } else if (familier_loyalmax()) {
            System.out.println("Vous avez atteint le niveau maximal de loyauté de la part de votre familier.");
        }
        corrige_ob();
    }

    private int entrainer() throws IOException {
        return switch (Input.D6()) {
            case 1 -> {
                if (Input.D4() <= 2) {
                    System.out.println("Votre familier désapprouve fortement vos méthodes d'entrainement.\n");
                    yield -1;
                }
                System.out.println();
                yield 0;
            }
            case 2, 3 -> {
                System.out.println("Vous familier n'a pas l'air très attentif...\n");
                yield 0;
            }
            case 4, 5 -> {
                System.out.println("Votre familier vous respecte un peu plus.\n");
                yield 1;
            }
            case 6 -> {
                if (Input.D4() >= 3) {
                    System.out.println("Votre familier semble particulièrement apprécier votre entrainement !\n");
                    yield 2;
                }
                System.out.println();
                yield 1;
            }
            default -> {
                System.out.println("Résultat non reconnu, compétence ignorée.\n");
                yield 0;
            }
        };
    }

    private void corrige_ob(){
        if (ob_f < 0){
           ob_f = 0;
        }
        else if(ob_f > Main.f_max){
            ob_f = Main.f_max;
        }
    }

    public void suicider(){
        System.out.println(nom + " est mort.");
        ob_f = 0;
        position = Position.ENFERS;
    }

    public void ajouter_familier() throws IOException {
        if (a_familier() && !Input.yn(nom + " possède déjà un familier, le remplacer ? ")) {
            System.out.println("Ancien familier conservé.\n");
        }
        else {
            System.out.println(nom + " a un nouveau familier.\n");
            ob_f = 1;
        }
    }

    public void perdre_familier() {
        System.out.println("Le familier de " + nom + " est mort.");
        ob_f = 0;
    }

    public void aller_au_marche() {
        switch (position) {
            case PRAIRIE -> Equipement.marche_prairie();
            case VIGNES -> Equipement.marche_vigne();
            case TEMPLE -> Equipement.marche_temple();
            case MER -> Equipement.marche_mer();
            case MONTS -> Equipement.marche_monts();
            case ENFERS, OLYMPE -> System.out.println("Erreur : Il n'y a pas de marché ici.");
            case ASCENDANT -> System.out.println("ERROR : DONOT");
        }
    }

    //************************************************METHODE METIER**************************************************//

    public String text_tour() {
        return switch (metier) {
            case ALCHIMISTE -> text_tour_alchimiste();
            case ARCHIMAGE -> text_tour_archimage();
            case RANGER -> text_tour_ranger();
            case GUERRIERE -> text_tour_guerriere();
            case NECROMANCIEN -> text_tour_necromencien();
            case SHAMAN -> text_tour_shaman();
            case AUCUN -> text_tour_aucun();
        };
    }

    public boolean tour(String choix) throws IOException {
        return switch (metier){
            case ALCHIMISTE -> tour_alchimiste(choix);
            case ARCHIMAGE -> tour_archimage(choix);
            case RANGER -> tour_ranger(choix);
            case GUERRIERE -> tour_guerriere(choix);
            case NECROMANCIEN -> tour_necromancien(choix);
            case SHAMAN -> tour_shaman(choix);
            case AUCUN -> tour_aucun(choix);
        };
    }

    public int bonus_exploration(){
        return switch (metier){
            case ALCHIMISTE -> bonus_exploration_alchimiste();
            case ARCHIMAGE -> bonus_exploration_archimage();
            case RANGER -> bonus_exploration_ranger();
            case GUERRIERE -> bonus_exploration_guerriere();
            case NECROMANCIEN -> bonus_exploration_necromancien();
            case SHAMAN -> bonus_exploration_shaman();
            case AUCUN -> bonus_exploration_aucun();
        };
    }

    //************************************************ALCHIMISTE******************************************************//

    private String text_tour_alchimiste(){
        return "/(fo)uiller/(co)ncocter des potions";
    }

    private boolean tour_alchimiste(String choix) throws IOException {
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

    private int bonus_exploration_alchimiste(){
        return 0;
    }

    //************************************************ARCHIMAGE*******************************************************//

    private static String text_tour_archimage(){
        return  "/(me)ditation";
    }

    private boolean tour_archimage(String choix) throws IOException {
        if(choix.equalsIgnoreCase("me")){
            Sort.meditation();
            return true;
        }
        return false;
    }

    private int bonus_exploration_archimage(){
        return rand.nextInt(2) - 1 /* bruyant */;
    }

    //************************************************GUERRIER********************************************************//

    private static String text_tour_guerriere(){
        return "";
    }

    private boolean tour_guerriere(String choix){
        return false;
    }

    private int bonus_exploration_guerriere(){
        return 0;
    }

    //************************************************NECROMANCIEN****************************************************//

    private static String text_tour_necromencien(){
        return  "/(ap)pel des morts";
    }

    private boolean tour_necromancien(String choix) throws IOException {
        if(choix.equalsIgnoreCase("ap")){
            Sort.necromancie(position);
            return true;
        }
        return false;
    }

    private int bonus_exploration_necromancien(){
        return 0;
    }

    //************************************************RANGER**********************************************************//

    private static String text_tour_ranger(){
        return "";
    }

    private boolean tour_ranger(String choix){
        return false;
    }

    private int bonus_exploration_ranger(){
        return rand.nextInt(2) /* eclaireur */ + rand.nextInt(3)/* explorateur */;
    }

    //************************************************SHAMAN**********************************************************//

    private static String text_tour_shaman(){
        return "";
    }

    private boolean tour_shaman(String choix){
        return false;
    }

    private int bonus_exploration_shaman(){
        return rand.nextInt(2) /* eclaireur */;
    }

    //************************************************AUCUN***********************************************************//

    private static String text_tour_aucun(){
        return "";
    }

    private boolean tour_aucun(String choix){
        return false;
    }

    private int bonus_exploration_aucun(){
        return 0;
    }

}

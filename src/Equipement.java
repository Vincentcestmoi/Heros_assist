import java.util.Random;

public class Equipement {
    private final String nom;
    private int resistance;
    private int armure;
    private int attaque;
    private String effet;
    private final Rang rang;
    private final Base base;

    static Random rand = new Random();

    Equipement(String nom, Base base, Rang rang, Effet_equip effet) {
        this.nom = nom;
        this.rang = rang;
        this.base = base;
        switch(base){
            case CASQUE -> this.make_casque();
            case ARMURE -> this.make_armure();
            case MAIN_1 -> this.make_main1();
            case MAIN_2 -> this.make_main2();
            case BOUCLIER -> this.make_bouclier();
            case ARC -> this.make_arc();
            case CEINTURE -> this.make_ceinture();
            default -> {
                this.attaque = 0;
                this.resistance = 0;
                this.armure = 0;
            }
        }
        applique_effet(effet);
    }

    /**
     * Crée un casque standard en fonction de son rang
     */
    private void make_casque(){
        this.attaque = 0;
        this.armure = 0;
        switch(rang){
            case O -> this.resistance = 1;
            case I -> this.resistance = 2;
            case II -> this.resistance = 3;
            case III -> this.resistance = 5;
            case IV -> this.resistance = 15;
        }
    }

    /**
     * Crée une armure standard en fonction de son rang
     */
    private void make_armure(){
        this.attaque = 0;
        switch(rang){
            case O -> {
                this.armure = 0;
                this.resistance = 1;
            }
            case I -> {
                this.armure = 0;
                this.resistance = 2;
            }
            case II -> {
                this.armure = 1;
                this.resistance = 6;
            }
            case III -> {
                this.armure = 2;
                this.resistance = 9;
            }
            case IV -> {
                this.armure = 3;
                this.resistance = 25;
            }
        }
    }

    /**
     * Crée une arme à une main standard en fonction de son rang
     */
    private void make_main1(){
        this.armure = 0;
        this.resistance = 0;
        switch(rang){
            case O -> this.attaque = 1;
            case I -> this.attaque = 2;
            case II -> this.attaque = rand.nextInt(2) + 3;
            case III -> this.attaque = rand.nextInt(2) + 5;
            case IV -> this.attaque = rand.nextInt(6) + 11;
        }
    }

    /**
     * Crée une arme à deux mains standard en fonction de son rang
     */
    private void make_main2(){
        this.armure = 0;
        this.resistance = 0;
        switch(rang){
            case O -> this.attaque = 3;
            case I -> this.attaque = 5;
            case II -> this.attaque = rand.nextInt(3) + 7;
            case III -> this.attaque = rand.nextInt(4) + 10;
            case IV -> this.attaque = rand.nextInt(10) + 25;
        }
    }

    /**
     * Crée un bouclier standard en fonction de son rang
     */
    private void make_bouclier(){
        this.armure = 0;
        switch(rang){
            case O -> {
                this.attaque = 0;
                this.resistance = 1;
            }
            case I -> {
                this.attaque = 1;
                this.resistance = 2;
            }
            case II -> {
                this.attaque = 2;
                this.resistance = 3;
            }
            case III -> {
                this.attaque = 4;
                this.resistance = 5;
            }
            case IV -> {
                this.attaque = 10;
                this.resistance = 15;
            }
        }
    }

    /**
     * Crée un arc standard en fonction de son rang
     */
    private void make_arc(){
        this.armure = 0;
        this.resistance = 0;
        switch(rang){
            case O -> this.attaque = 0; // 1 * +1
            case I -> this.attaque = 1; // 3 * +1
            case II -> this.attaque = rand.nextInt(3) + 1; //2 * +3
            case III -> this.attaque = rand.nextInt(4) + 2; //2 * +4
            case IV -> this.attaque = rand.nextInt(10) + 15; //1 * +10
        }
    }

    /**
     * Crée une ceinture standard en fonction de son rang
     */
    private void make_ceinture() {
        switch (rang) {
            case O -> {
                this.armure = 0;
                if (rand.nextBoolean()) {
                    this.resistance = 2;
                    this.attaque = 0;
                } else {
                    this.resistance = 0;
                    this.attaque = 1;
                }
            }
            case I -> {
                this.armure = 0;
                switch (rand.nextInt(3)) {
                    case 0 -> {
                        this.resistance = 1;
                        this.attaque = 1;
                    }
                    case 1 -> {
                        this.resistance = 3;
                        this.attaque = 0;
                    }
                    case 2 -> {
                        this.resistance = 0;
                        this.attaque = 2;
                    }
                }
            }
            case II -> {
                switch (rand.nextInt(6)) {
                    case 0 -> {
                        this.resistance = 3;
                        this.attaque = 0;
                        this.armure = 1;
                    }
                    case 1 -> {
                        this.resistance = 3;
                        this.attaque = 2;
                        this.armure = 0;
                    }
                    case 2 -> {
                        this.resistance = 0;
                        this.attaque = 4;
                        this.armure = 0;
                    }
                    case 3 -> {
                        this.resistance = 0;
                        this.attaque = 2;
                        this.armure = 1;
                    }
                    case 4 -> {
                        this.resistance = 0;
                        this.attaque = 0;
                        this.armure = 2;
                    }
                    case 5 -> {
                        this.resistance = 6;
                        this.attaque = 0;
                        this.armure = 0;
                    }
                }
            }case III -> {
                switch (rand.nextInt(6)) {
                    case 0 -> {
                        this.resistance = 6;
                        this.attaque = 0;
                        this.armure = 1;
                    }
                    case 1 -> {
                        this.resistance = 6;
                        this.attaque = 3;
                        this.armure = 0;
                    }
                    case 2 -> {
                        this.resistance = 0;
                        this.attaque = 6;
                        this.armure = 0;
                    }
                    case 3 -> {
                        this.resistance = 0;
                        this.attaque = 4;
                        this.armure = 1;
                    }
                    case 4 -> {
                        this.resistance = 2;
                        this.attaque = 1;
                        this.armure = 2;
                    }
                    case 5 -> {
                        this.resistance = 9;
                        this.attaque = 0;
                        this.armure = 0;
                    }
                }
            }
            case IV -> {
                this.armure = 4;
                this.resistance = 10;
                this.attaque = 7;
            }
        }
    }

    /**
     * Applique les effets spéciaux à l'équipement
     */
    private void applique_effet(Effet_equip effet){
        this.effet = null;
        switch(effet){
            case ARCA -> {
                switch(rang){
                    case O, I -> this.effet = "Augmente de 1 l'attaque des arcs.";
                    case II -> this.effet = "Augmente de 3 l'attaque des arcs.";
                    case III -> this.effet = "Augmente de 4 l'attaque des arcs.";
                    case IV -> this.effet = "Augmente de 10 l'attaque des arcs.";
                }
            }
            case ARC -> this.effet = "Cet objet est un arc, il permet de tirer.";
            case PASD0 -> this.effet = "Pas encore dev, piochez un équipement O.";
            case PASD1 -> this.effet = "Pas encore dev, piochez un équipement I.";
            case PASD2 -> this.effet = "Pas encore dev, piochez un équipement II.";
            case PASD3 -> this.effet = "Pas encore dev, piochez un équipement III.";
            case PASD4 -> this.effet = "Pas encore dev, piochez un équipement IV.";
            case PASDP -> this.effet = "Pas encore dev, piochez une promotion.";
            case RESISTANCE1 -> this.resistance += 1;
            case ENFERS4 -> {
                this.attaque = 1;
                this.effet = "Augmente de 4 l'attaque aux enfers.";
            }
            case PRAIRIE3 -> {
                this.attaque = 2;
                this.effet = "Augmente de 3 l'attaque en prairie.";
            }
            case CONSO_EXT1 -> this.effet = "Soigne de 1 et régénère 1PP.";
            case CONSO_EXT2 -> this.effet = "Soigne de 2 et régénère 2PP.";
            case CONSO_RES2 -> this.effet = "Soigne de 2.";
            case CONSO_RES4 -> this.effet = "Soigne de 4.";
            default -> {} //inclus AUCUN
        }
    }

    /**
     *
     */
    public void presente(){
        String type = getString();
        System.out.println("\n" + this.nom + " (" + type + ")");
        if(this.attaque > 0){
            System.out.println("+" + this.attaque + " attaque(s)");
        }
        if(this.resistance > 0){
            System.out.println("+" + this.resistance + " resistance(s)");
        }
        if(this.armure > 0){
            System.out.println("+" + this.armure + " armure(s)");
        }
        if(this.effet != null){
            System.out.println(this.effet);
        }
        System.out.println();
    }

    private String getString() {
        String type;
        switch(base){
            case CEINTURE -> type = "ceinture";
            case MAIN_1 -> type = "une main";
            case MAIN_2 -> type = "deux main";
            case BOUCLIER -> type = "bouclier";
            case ARC -> type = "arc";
            case ARMURE -> type = "armure";
            case CASQUE -> type = "casque";
            case BRACELET -> type = "bracelet";
            case CONSO_EX -> type = "consommable bonus";
            case CONSO_MAIN -> type = "consommable";
            default -> type = "divers";
        }
        return type;
    }

    static public void drop_0() {
        Pre_Equipement drop = Pre_Equipement.drop_0();
        Equipement equipement = new Equipement(drop.nom, drop.base, drop.rang, drop.effet);
        equipement.presente();
    }

    static public void drop_1() {
        Pre_Equipement drop = Pre_Equipement.drop_1();
        Equipement equipement = new Equipement(drop.nom, drop.base, drop.rang, drop.effet);
        equipement.presente();
    }

    static public void drop_2() {
        Pre_Equipement drop = Pre_Equipement.drop_2();
        Equipement equipement = new Equipement(drop.nom, drop.base, drop.rang, drop.effet);
        equipement.presente();
    }

    static public void drop_3() {
        Pre_Equipement drop = Pre_Equipement.drop_3();
        Equipement equipement = new Equipement(drop.nom, drop.base, drop.rang, drop.effet);
        equipement.presente();
    }

    static public void drop_4() {
        Pre_Equipement drop = Pre_Equipement.drop_4();
        Equipement equipement = new Equipement(drop.nom, drop.base, drop.rang, drop.effet);
        equipement.presente();
    }

    static public void drop_promo() {
        Pre_Equipement drop = Pre_Equipement.drop_promo();
        Equipement equipement = new Equipement(drop.nom, drop.base, drop.rang, drop.effet);
        equipement.presente();
    }

}

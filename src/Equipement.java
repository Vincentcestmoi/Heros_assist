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

    Equipement(Pre_Equipement pre) {
        this.nom = pre.nom;
        this.rang = pre.rang;
        this.base = pre.base;
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
        applique_effet(pre.effet);
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
            case ARCEXP -> this.effet = "Augmente de 6 une attaque à l'arc, se consomme à l'usage.";
            case ARC -> this.effet = "Cet objet est un arc, il permet de tirer.";
            case PASD0 -> this.effet = "Pas encore dev, piochez un équipement O.";
            case PASD1 -> this.effet = "Pas encore dev, piochez un équipement I.";
            case PASD2 -> this.effet = "Pas encore dev, piochez un équipement II.";
            case PASD3 -> this.effet = "Pas encore dev, piochez un équipement III.";
            case PASD4 -> this.effet = "Pas encore dev, piochez un équipement IV.";
            case PASDP -> this.effet = "Pas encore dev, piochez une promotion.";
            case RESISTANCE1 -> this.resistance += 1;
            case RESISTANCE2 -> this.resistance += 2;
            case RESISTANCE4 -> this.resistance += 4;
            case RESISTANCE6 -> this.resistance += 6;
            case ATTAQUE1 -> this.attaque += 1;
            case ATTAQUE2 -> this.attaque += 2;
            case ARMURE1 -> this.armure += 1;
            case ARMURE2 -> this.armure += 2;
            case BAD_ALLONGE -> {
                this.attaque += 1;
                this.resistance -= 2;
            }
            case BONNE_ALLONGE -> {
                this.attaque -= 2;
                this.resistance += 3;
            }
            case RAGE1 -> {
                this.resistance -= 1;
                this.attaque += 2;
            }
            case RAGE2 -> {
                this.resistance -= 3;
                this.attaque += 4;
            }
            case RAGE3 -> {
                this.resistance -= 6;
                this.attaque += 6;
            }
            case ENFERS4 -> {
                this.attaque -= 2;
                this.effet = "Augmente de 4 l'attaque aux enfers.";
            }
            case PRAIRIE3 -> {
                this.attaque -= 1;
                this.effet = "Augmente de 3 l'attaque en prairie.";
            }
            case TRIDENT -> {
                this.attaque -= 2;
                this.effet = "Augmente de 4 l'attaque en mer.";
            }
            case CONSO_EXT1 -> this.effet = "Soigne de 1 et régénère 1PP.";
            case CONSO_EXT2 -> this.effet = "Soigne de 2 et régénère 2PP.";
            case CONSO_EXT3 -> this.effet = "Soigne de 3 et régénère 3PP.";
            case CONSO_RES2 -> this.effet = "Soigne de 2.";
            case CONSO_RES4 -> this.effet = "Soigne de 4.";
            case CONSO_RES6 -> this.effet = "Soigne de 6.";
            case CONSO_RES8 -> this.effet = "Soigne de 8.";
            case PP4 -> this.effet = "Régénère 4 PP.";
            case PP6 -> this.effet = "Régénère 6 PP.";
            case PPMAX -> this.effet = "Pour ce combat, vous avez un nombre infini de PP ; à la fin du combat, mettez vos PP à 0.";
            case SOUPE_MAGIQUE -> this.effet = "Soigne de 4, augmente temporairement la résistance de 3 et l'attaque de 2.";
            case NECTAR -> this.effet = "Combinez le à l'ambroisie pour gagner.";
            case AMBROISIE -> this.effet = "Combinez le au nectar pour gagner";
            case SAC0 -> this.effet = "Permet de stocker 1 objet (pas d'arme).";
            case SAC1 -> {
                switch(rand.nextInt(4)) {
                    case 0 -> this.effet = "Permet de stocker 1 objet (pas d'arme) et 1 arme à une main.";
                    case 1 -> this.effet = "Permet de stocker 1 objet ou arme à une main.";
                    case 2 -> this.effet = "Permet de stocker 2 objets (pas d'arme).";
                    case 3 -> this.effet = "Permet de stocker l'équivalent de 2 mains en arme.";
                }
            }
            case SAC2 -> {
                switch(rand.nextInt(4)) {
                    case 0 -> this.effet = "Permet de stocker 2 objets (pas d'armes) et 1 arme à une main.";
                    case 1 -> this.effet = "Permet de stocker (2 objets ou armes à une main) ou 1 arme à deux mains.";
                    case 2 -> this.effet = "Permet de stocker 2 armes sans contraintes.";
                    case 3 -> this.effet = "Permet de stocker l'équivalent de 3 mains en arme.";
                }
            }
            case GUERRE -> this.effet = "+1 attaque pour les descendant d'Ares.";
            case LAME_VENT -> {
                this.attaque += 1;
                this.effet = "Votre première attaque inflige 3 dommages supplémentaires.";
            }
            case ARMURE_GLACE -> {
                this.armure += 1;
                this.effet = "Vous devez payer 2PP pour enfiler cette armure";
            }
            case BOUCLIER_TERRE -> {
                this.resistance += 2;
                this.effet = "Vous devez payer 2PP pour équiper ce bouclier";
            }
            case COIFFE_FEU -> {
                this.attaque += 2;
                this.effet = "Vous devez payer 2PP pour porter cette coiffe";
            }
            case LAME_DODO -> {
                this.attaque -= 3;
                this.effet = "+4 attaques pour les descendant d'Hypnos";
            }
            case RUNE_RESIDU -> this.effet = "Peut être consummé à la place de 2 PP.";
            case PARCH_FEU -> this.effet = "Permet de lancer le sort Boule de feu : pour 2PP, inflige 5 dégats magiques.";
            case PARCH_DODO -> this.effet = "Permet de lancer le sort Sommeil : pour 2PP, arrête un combat.";
            case PARCH_FORCE -> this.effet = "Permet de lancer le sort Renforcement : pour 1PP, augmente définitivement l'attaque de 1, se détruit après usage.";
            case RUNE_CROISS -> this.effet = "Diminue de 1 le coût de la compétence Embranchement végétal.";
            case RUNE_PLUIE -> this.effet = "Diminue de 1 le coût de la compétence Déferlante.";
            case RUNE_HAINE -> this.effet = "Augmente de 3 points additionels l'attaque sous Berserk, mais diminue aussi la résistance de 2 points additionels.";
            case RUNE_VIRALE -> this.effet = "Double les dommages infligés par la compétence Maladie.";
            case RUNE_ARDENTE -> this.effet = "augmente de 3 les dommages du sort Boule de feu";
            case RUNE_ARDENTE2 -> this.effet = "augmente de 5 les dommages du sort Boule de feu, mais augmente de 1 son coût.";
            case RUNE_DODO -> this.effet = "Diminue de 1 le coût de la compétence Sommeil.";
            case RUNE_MORT -> this.effet = "Double l'efficacité de la compétence Thaumaturge";
            case RUNE_ORAGE -> this.effet = "Diminue de 1 le coût de la compétence Foudre.";
            case SOIN -> this.effet = "Vous pouvez, une fois par combat, utiliser votre action pour soigner une cible de 6.";
            case PROTECTION -> this.effet = "Peut être sacrifié pour ignorer une fois des dommages (avant de les calculer).";
            case RUNE_VENGEANCE -> this.effet = "Récuperez 2PP chaque fois qu'un familier ou joueur allié est tué par un monstre.";
            case RUNE_INTERDITE -> this.effet = "Augmente de 1 le coût de la compétence Nécromancie, mais augmente de 1 le résultat du jet de dé.";
            case BRACELET_ABSO -> this.effet = "Gagnez 1 PP chaque fois qe vous tuez un monstre (vous ou votre familier doit porter le dernier coup).";
            case LUNETTE -> this.effet = "Vous êtes immunisé à l'altération cécité";
            case BRACELET_MAUDIT -> {
                this.resistance -= 3;
                this.armure += 1;
            }
            case DISSEC -> this.effet = "Augmente de 1 le résultat de dé de la compétence Dissection.";
            case ALCHI -> this.effet = "Augmente de 1 le résultat de tout vos dé de concoction.";
            case PARCH_BERSERK -> this.effet = "Permet de lancer le sort Folie meurtrière : pour 2PP, augmente temporairement de 5 points l'attaque et diminue de 7 la résistances.";
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
        else if(this.attaque < 0){
            System.out.println("-" + -this.attaque + " attaque(s)");
        }
        if(this.resistance > 0){
            System.out.println("+" + this.resistance + " resistance(s)");
        }
        else if(this.resistance < 0){
            System.out.println("-" + -this.resistance + " resistance(s)");
        }
        if(this.armure > 0){
            System.out.println("+" + this.armure + " armure(s)");
        }
        else if(this.armure < 0){
            System.out.println("-" + -this.armure + " armure(s)");
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
            case SAC -> type = "sac";
            case RUNE -> type = "rune";
            default -> type = "divers";
        }
        return type;
    }

    static public void drop_0() {
        Equipement equipement = new Equipement(Pre_Equipement.drop_0());
        equipement.presente();
    }

    static public void drop_1() {
        Equipement equipement = new Equipement(Pre_Equipement.drop_1());
        equipement.presente();
    }

    static public void drop_2() {
        Equipement equipement = new Equipement(Pre_Equipement.drop_2());
        equipement.presente();
    }

    static public void drop_3() {
        Equipement equipement = new Equipement(Pre_Equipement.drop_3());
        equipement.presente();
    }

    static public void drop_4() {
        Equipement equipement = new Equipement(Pre_Equipement.drop_4());
        equipement.presente();
    }

    static public void drop_promo() {
        Equipement equipement = new Equipement(Pre_Equipement.drop_promo());
        equipement.presente();
    }

}

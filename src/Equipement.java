import java.io.IOException;
import java.util.Random;

public class Equipement {
    public final String nom;
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
            case SAC -> this.make_sac();
            default -> { //AUTRE, BRACELET, MONTURE, CONSO_EX, CONSO_MAIN, RUNE
                this.attaque = 0;
                this.resistance = 0;
                this.armure = 0;
            }
        }
        applique_effet(pre.effet);
    }

    private void make_sac() {
        this.attaque = 0;
        this.armure = 0;
        switch(rang){
            case O, I -> this.resistance = 0;
            case II -> this.resistance = 1;
            case III -> this.resistance = 2;
            case IV -> {
                this.resistance = 5;
                this.armure = 1;
            }
        }
    }

    Equipement(String nom, Rang rang, Base base, int attaque, int resistance, int armure, int prix) {
        this.nom = nom;
        this.attaque = attaque;
        this.resistance = resistance;
        this.armure = armure;
        this.rang = rang;
        this.base = base;
        this.effet = "prix : " + prix;
    }

    Equipement(String nom, Rang rang, Base base, int attaque, int resistance, int armure, int prix, String effet) {
        this.nom = nom;
        this.attaque = attaque;
        this.resistance = resistance;
        this.armure = armure;
        this.rang = rang;
        this.base = base;
        this.effet = effet + "\nprix : " + prix;
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
                this.armure = 2;
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
                    case PROMOTION -> this.effet = "Augmente de " + (rand.nextInt(4) + 1) + " l'attaque des arcs.";
                }
            }
            case ARCEXP -> this.effet = "Augmente de 6 une attaque à l'arc, se consomme à l'usage.";
            case PASDP -> this.effet = "Pas encore dev, piochez une promotion.";
            case RESISTANCE1 -> this.resistance += 1;
            case RESISTANCE2 -> this.resistance += 2;
            case RESISTANCE3 -> this.resistance += 3;
            case RESISTANCE4 -> this.resistance += 4;
            case RESISTANCE6 -> this.resistance += 6;
            case ATTAQUE1 -> this.attaque += 1;
            case ATTAQUE2 -> this.attaque += 2;
            case ATTAQUE7 -> this.attaque += 7;
            case ATTAQUE8 -> this.attaque += 8;
            case ARMURE1 -> this.armure += 1;
            case ARMURE2 -> this.armure += 2;
            case BRACELETMAX -> {
                this.armure += 1;
                this.resistance += 5;
                this.attaque += 3;
            }
            case BRACELET_DIVIN -> {
                this.armure += 3;
                this.resistance += 9;
                this.attaque += 6;
            }
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
            case MONT5 -> {
                this.attaque -= 2;
                this.effet = "Augmente de 5 l'attaque en montagne.";
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
            case PPL -> this.effet = "Pour ce combat, récupérez 1PP à chacun de vos débuts de tour.";
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
            case PARCH_LUMIERE -> this.effet = "Permet de lancer le sort Lumière : pour 2PP, diminue de 2 points l'attaque de l'ennemie (augmentez tous de 2 votre armure pour le code).";
            case RUNE_CROISS -> this.effet = "Diminue de 1 le coût de la compétence Embranchement végétal.";
            case RUNE_PLUIE -> this.effet = "Diminue de 1 le coût de la compétence Déferlante.";
            case RUNE_HAINE -> this.effet = "Augmente le bonus d'attaque sous Berserk, mais aussi le malus de résistance.";
            case RUNE_VIRALE -> this.effet = "Double les dommages infligés par la compétence Maladie.";
            case RUNE_ARDENTE -> this.effet = "augmente de 3 les dommages du sort Boule de feu";
            case RUNE_ARDENTE2 -> this.effet = "augmente de 5 les dommages du sort Boule de feu, mais augmente de 1 son coût.";
            case RUNE_DODO -> this.effet = "Diminue de 1 le coût de la compétence Sommeil.";
            case RUNE_MORT -> this.effet = "Double l'efficacité de la compétence Thaumaturge";
            case RUNE_ORAGE -> this.effet = "Diminue de 1 le coût de la compétence Foudre.";
            case SOIN -> this.effet = "Vous pouvez, une fois par combat, utiliser votre action pour soigner une cible de 6.";
            case PROTECTION -> this.effet = "Peut être utilisé pour ignorer des dommages (avant de les calculer). Une seule utilisation.";
            case RUNE_VENGEANCE -> this.effet = "Récuperez 2PP chaque fois qu'un familier ou joueur allié est tué par un monstre.";
            case RUNE_INTERDITE -> this.effet = "Augmente de 1 le coût de la compétence Nécromancie, mais augmente de 1 le résultat du jet de dé.";
            case BRACELET_ABSO -> this.effet = "Gagnez 1 PP chaque fois qe vous tuez un monstre (vous ou votre familier doit porter le dernier coup).";
            case LUNETTE -> this.effet = "Vous êtes immunisé à l'altération cécité";
            case BRACELET_MAUDIT -> {
                this.resistance -= 3;
                this.armure += 1;
            }
            case DISSEC -> this.effet = "Augmente de 1 le résultat de dé des compétence Dissection et Fouille.";
            case ALCHI -> this.effet = "Augmente de 2 le résultat de tout vos dé de concoction.";
            case PARCH_BERSERK -> this.effet = "Permet de lancer le sort Folie meurtrière : pour 2PP," +
                    "augmente temporairement de 5 points l'attaque et diminue de 7 la résistances.";
            case BOURDON -> {
                this.effet = "Quand vous lancez un sort, lancez un dé 4 : si vous faites 3 ou plus, votre sort coute 1 de moins.";
                attaque += 2;
                armure -= 2;
            }
            case PARCH_VOLCAN -> this.effet = "Permet de lancer le sort Folie meurtrière : pour 6PP, infliges 55 dégats magiques";
            case PARCH_ABSO -> this.effet = "Quand vous tuez un monstre, vous gagnez définitivement 7 points de résistances, 3 points" +
                    "d'attaques, 1 point d'armure et 4PP. (vous ou votre familier devez porter le dernier coup).";
            case PEGASE -> {
                this.resistance += 3;
                this.effet = "Ajoutez 1 à tout vos dés d'exploration et d'ascension.";
            }
            case CHEVAL -> {
                this.resistance += 5;
                this.effet = "Vos dés de fuites sont automatiquement maximaux.";
            }
            case MOLOSSE -> {
                this.resistance += 4;
                this.attaque += 3;
            }
            case PIE -> {
                this.attaque += 1;
                this.effet = "A la fin de chaque combat auquel vous participez, gagnez 1PO, ne s'active pas si vous avez fuit. Gagnez 1 sur vos dés de fuite.";
            }
            case SPHINX -> {
                this.attaque += 1;
                this.resistance += 2;
                this.effet = "Pour 1PP, augmente temporairement votre résistance de 3 points.";
            }
            case ALTRUISME -> this.effet = "Au début de chaque combat, répartissez 5 points temporaires de résistance entre vos alliés (familiers inclus)." +
                    "Vous ne pouvez les attribuer à vous même.";
            case RUNE_ARCA -> this.effet = "Diminue de 1 la coût de tout vos sort";
            case ANTIDODE -> this.effet = "Le consommer fournit une immunité définitive au poison et à la cécité.";
            case ANNIHILITON -> this.effet = "En sacrifiant une rune, vous pouvez lancer un attaque magique de puissance le triple de votre attaque classique.";
            case REZ -> this.effet = "Si vous êtes mort à la fin d'un combat, vous ramène à la vie. S'efface après utilisation. Ne peut être échangé.";
            case FUITE -> this.effet = "A tout moment, vous pouvez utiliser cet objet pour fuir un combat. Se détruit après usage. Ne peut être utilisé si vous êtes mort.";
            case GRENADE -> this.effet = "Inflige 4 dommages additionels. Utilisable 3 fois au total.";
            case MER_EXP -> this.effet = "Si vous vous trouvez en mer, vous pouvez choisir n'importe quel résultat entre 1 et 7 au lieu de lancer le dé." +
                    "Un résultat choisi ignore le bonus de dé.";
            case ITEM_IMMUN -> this.effet = "Qu'importe la cause et le contexte, chaque fois que vous perdez un équipement, récuperez le." +
                    "(ne fonctionne pas si vous mourrez, vendez, échangez ou dépensez un équipement)";
            case SAC_TEMP -> this.effet = "Lorsque vous utilisez un objet à usage limité pour la dernière fois," +
                    "vous pouvez choisir de détruire cette sacoche à la place et récuperer tous les utilisations de votre objet.";
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
            case MONTURE -> type = "monture";
            case AUTRE -> type = "divers";
            default -> type = "Erreur : type non reconnu.";
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

    static public void drop_promo() throws IOException {
        Equipement equipement = new Equipement(Pre_Equipement.drop_promo());
        equipement.presente();
    }
    //Equipement(String nom, Rang rang, Base base, int attaque, int resistance, int armure, int prix, String effet)

    static Equipement lotterie = new Equipement("ticket de lotterie", Rang.O, Base.AUTRE, 0, 0, 0, 3,
            "Lancez un dé à 6 face, si vous faite 6, gagnez 15PO. Jetez ce ticket.");

    static Equipement main1_0 = new Equipement("branche solide", Rang.O, Base.MAIN_1, 1, 0, 0, 5);
    static Equipement main2_0 = new Equipement("pavé", Rang.O, Base.MAIN_2, 3, 0, 0, 7);
    static Equipement arc_0 = new Equipement("vieil arc", Rang.O, Base.ARC, 0, 0, 0, 3);
    static Equipement bouclier_0 = new Equipement("planche en bois", Rang.O, Base.BOUCLIER, 0, 2, 0, 8);
    static Equipement armure_0 = new Equipement("vieille armure", Rang.O, Base.ARMURE, 0, 1, 0, 3);
    static Equipement casque_0 = new Equipement("seau", Rang.O, Base.CASQUE, 0, 1, 0, 3);
    static Equipement ingredients_0 = new Equipement("ingrédient", Rang.O, Base.AUTRE, 0, 0, 0, 3);
    static Equipement ingredients2_0 = new Equipement("2 ingrédients", Rang.O, Base.AUTRE, 0, 0, 0, 7);

    static Equipement[] marche0 = {arc_0, main1_0, main2_0, armure_0, bouclier_0, casque_0, ingredients_0, ingredients2_0};

    static Equipement arc_I = new Equipement("arc", Rang.I, Base.ARC, 1, 0, 0, 4);
    static Equipement bouclier_I = new Equipement("petit bouclier", Rang.I, Base.BOUCLIER, 1, 2, 0, 7);
    static Equipement main1_I = new Equipement("couteau", Rang.I, Base.MAIN_1, 2, 0, 0, 6);
    static Equipement main2_I = new Equipement("grande épée", Rang.I, Base.MAIN_2, 5, 0, 0, 9);
    static Equipement armure_I = new Equipement("armure", Rang.I, Base.ARMURE, 0, 2, 0, 5);
    static Equipement casque_I = new Equipement("mauvais casque", Rang.I, Base.CASQUE, 0, 2, 0, 5);
    static Equipement ingredients_I = new Equipement("ingrédient", Rang.O, Base.AUTRE, 0, 0, 0, 4);
    static Equipement ingredients2_I = new Equipement("2 ingrédients", Rang.O, Base.AUTRE, 0, 0, 0, 8);

    static Equipement[] marcheI = {arc_I, main1_I, main2_I, armure_I, bouclier_I, casque_I, ingredients_I, ingredients2_I, lotterie};

    static Equipement arc_II = new Equipement("grand arc", Rang.II, Base.ARC, 3, 0, 0, 5);
    static Equipement bouclier_II = new Equipement("bouclier", Rang.II, Base.BOUCLIER, 2, 3, 0, 9);
    static Equipement main1_II = new Equipement("hachette", Rang.II, Base.MAIN_1, 4, 0, 0, 8);
    static Equipement main2_II = new Equipement("grande hache", Rang.II, Base.MAIN_2, 9, 0, 0, 12);
    static Equipement armure_II = new Equipement("bonne armure", Rang.II, Base.ARMURE, 0, 6, 1, 11);
    static Equipement casque_II = new Equipement("casque", Rang.II, Base.CASQUE, 0, 3, 0, 6);

    static Equipement[] marcheII = {arc_II, main1_II, main2_II, armure_II, bouclier_II, casque_II, ingredients_0, ingredients2_0, lotterie};

    static Equipement arc_III = new Equipement("arc à poulie", Rang.III, Base.ARC, 5, 0, 0, 7);
    static Equipement bouclier_III = new Equipement("bon bouclier", Rang.III, Base.BOUCLIER, 4, 5, 0, 13);
    static Equipement main1_III = new Equipement("bon glaive", Rang.III, Base.MAIN_1, 6, 0, 0, 12);
    static Equipement main2_III = new Equipement("grosse épée", Rang.III, Base.MAIN_2, 13, 0, 0, 16);
    static Equipement armure_III = new Equipement("excellente armure", Rang.III, Base.ARMURE, 0, 11, 2, 21);
    static Equipement casque_III = new Equipement("bon casque", Rang.III, Base.CASQUE, 0, 5, 1, 18);
    static Equipement ingredients_III = new Equipement("ingrédient", Rang.O, Base.AUTRE, 0, 0, 0, 5);
    static Equipement ingredients2_III = new Equipement("2 ingrédients", Rang.O, Base.AUTRE, 0, 0, 0, 11);

    static Equipement[] marcheIII = {arc_III, main1_III, main2_III, armure_III, bouclier_III, casque_III, ingredients_III, ingredients2_III};

    @SuppressWarnings("DuplicatedCode")
    public static void marche_prairie() {
        System.out.println("Vous êtes dans le marché des prairies et 3 objets vous sont proposés :");
        int i;
        Equipement a, b;
        Equipement[] list;

        if(rand.nextBoolean()){
            list = marche0;
        }
        else{
            list = marcheI;
        }
        a = list[rand.nextInt(list.length)];

        if(rand.nextBoolean()){
            list = marche0;
        }
        else{
            list = marcheI;
        }
        do{
            i = rand.nextInt(list.length);
        }while(a == list[i]);
        b = list[i];

        if(rand.nextBoolean()){
            list = marche0;
        }
        else{
            list = marcheI;
        }
        do{
            i = rand.nextInt(list.length);
        }while(list[i] == b || list[i] == a);

        a.presente();
        b.presente();
        list[i].presente();
    }

    public static void marche_vigne() {
        System.out.println("Vous êtes dans le marché des vignes et 3 objets vous sont proposés :");
        int i;
        Equipement a, b;
        Equipement[] list = marcheI;

        a = list[rand.nextInt(list.length)];

        do {
            i = rand.nextInt(list.length);
        } while (a == list[i]);
        b = list[i];

        do {
            i = rand.nextInt(list.length);
        } while (list[i] == b || list[i] == a);

        a.presente();
        b.presente();
        list[i].presente();
    }

    @SuppressWarnings("DuplicatedCode")
    public static void marche_temple() {
        int nbo = rand.nextInt(3) + 2;
        System.out.println("Vous trouvez un marchant dans le temple, qui vous propose " + nbo + " objets :");
        int i;
        Equipement a, b, c = null;
        Equipement[] list;

        if (rand.nextBoolean()) {
            list = marcheI;
        } else {
            list = marcheII;
        }
        a = list[rand.nextInt(list.length)];

        if (rand.nextBoolean()) {
            list = marcheI;
        } else {
            list = marcheII;
        }
        do {
            i = rand.nextInt(list.length);
        } while (a == list[i]);
        b = list[i];

        if(nbo > 2) {
            if (rand.nextBoolean()) {
                list = marcheI;
            } else {
                list = marcheII;
            }
            do {
                i = rand.nextInt(list.length);
            } while (list[i] == b || list[i] == a);
            c = list[i];
        }

        if(nbo > 3){
            if (rand.nextBoolean()) {
                list = marcheI;
            } else {
                list = marcheII;
            }
            do {
                i = rand.nextInt(list.length);
            } while (list[i] == b || list[i] == a || list[i] == c);
        }

        a.presente();
        b.presente();
        if(nbo > 2){
            c.presente();
        }
        if(nbo > 3) {
            list[i].presente();
        }
    }

    public static void marche_mer() {
        System.out.println("Vous croisez un navire marchand qui vous propose 2 objets :");
        Equipement[] list;
        if(rand.nextBoolean()){
            list = marcheII;
        }
        else{
            list = marcheIII;
        }
        int i = rand.nextInt(list.length);
        Equipement a = list[i];
        if(rand.nextBoolean()){
            list = marcheII;
        }
        else{
            list = marcheIII;
        }
        do{
            i = rand.nextInt(list.length);
        }while(list[i] == a);
        a.presente();
        list[i].presente();
    }

    public static void marche_monts() {
        System.out.println("Vous croisez un pelerin qui vous propose de vous vendre un objet :");
        marcheIII[rand.nextInt(marcheIII.length)].presente();
    }
}

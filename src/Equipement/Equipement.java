package Equipement;

import Enum.Base;
import Enum.Effet_equip;
import Enum.Rang;
import Exterieur.Output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Equipement {
    public final String nom;
    private int resistance;
    private int armure;
    private int attaque;
    private String effet;
    private final Rang rang;
    private final Base base;
    private final int prix;
    
    static Random rand = new Random();
    
    Equipement(Pre_Equipement pre) {
        this.nom = pre.nom;
        this.rang = pre.rang;
        this.base = pre.base;
        switch (base) {
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
        this.prix = 0;
    }
    
    Equipement(String nom, Rang rang, Base base, int attaque, int resistance, int armure, int prix) {
        this.nom = nom;
        this.attaque = attaque;
        this.resistance = resistance;
        this.armure = armure;
        this.rang = rang;
        this.base = base;
        this.prix = prix;
        this.effet = null;
    }
    
    Equipement(String nom, Rang rang, Base base, int attaque, int resistance, int armure, int prix, String effet) {
        this.nom = nom;
        this.attaque = attaque;
        this.resistance = resistance;
        this.armure = armure;
        this.rang = rang;
        this.base = base;
        this.prix = prix;
        this.effet = effet;
    }
    
    /**
     * Crée un sac standard en fonction de son rang
     */
    private void make_sac() {
        this.attaque = 0;
        this.armure = 0;
        switch (rang) {
            case O, I -> this.resistance = 0;
            case II -> this.resistance = 1;
            case III -> this.resistance = 2;
            case IV -> {
                this.resistance = 5;
                this.armure = 1;
            }
        }
    }
    
    /**
     * Crée un casque standard en fonction de son rang
     */
    private void make_casque() {
        this.attaque = 0;
        this.armure = 0;
        switch (rang) {
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
    private void make_armure() {
        this.attaque = 0;
        switch (rang) {
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
    private void make_main1() {
        this.armure = 0;
        this.resistance = 0;
        switch (rang) {
            case O -> this.attaque = 2;
            case I -> this.attaque = 3;
            case II -> this.attaque = rand.nextInt(2) + 4;
            case III -> this.attaque = rand.nextInt(2) + 6;
            case IV -> this.attaque = rand.nextInt(6) + 13;
        }
    }
    
    /**
     * Crée une arme à deux mains standard en fonction de son rang
     */
    private void make_main2() {
        this.armure = 0;
        this.resistance = 0;
        switch (rang) {
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
    private void make_bouclier() {
        this.armure = 0;
        switch (rang) {
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
    private void make_arc() {
        this.armure = 0;
        this.resistance = 0;
        switch (rang) {
            case O -> this.attaque = rand.nextInt(2); // 2 * +1
            case I -> this.attaque = 1 + rand.nextInt(3); // 5 * +1
            case II -> this.attaque = rand.nextInt(3) + 2; // 4 * +3
            case III -> this.attaque = rand.nextInt(4) + 3; // 3 * +4
            case IV -> this.attaque = rand.nextInt(10) + 15; // 1 * +15
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
            }
            case III -> {
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
    private void applique_effet(Effet_equip effet) {
        this.effet = null;
        switch (effet) {
            case ARCA -> this.effet = switch (rang) {
                case O, I -> "Augmente de 1 l'attaque des arcs.";
                case II -> "Augmente de 3 l'attaque des arcs.";
                case III -> "Augmente de 4 l'attaque des arcs.";
                case IV -> "Augmente de 15 l'attaque des arcs.";
                case PROMOTION -> "Augmente de " + (rand.nextInt(4) + 1) + " l'attaque des arcs.";
            };
            case ARCEXP -> this.effet = "Augmente de 6 une attaque à l'arc, se consomme à l'usage.";
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
                this.effet = "Augmente l'attaque selon certaines conditions (#01).";
            }
            case PRAIRIE3 -> {
                this.attaque -= 1;
                this.effet = "Augmente l'attaque selon certaines conditions (#02).";
            }
            case TRIDENT -> {
                this.attaque -= 2;
                this.effet = "Augmente l'attaque selon certaines conditions (#03).";
            }
            case MONT5 -> {
                this.attaque -= 2;
                this.effet = "Augmente l'attaque selon certaines conditions (#04).";
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
            case PPMAX ->
                    this.effet =
                            "Pour un combat, vous avez un nombre infini de PP ; à la fin du combat, mettez vos " +
                                    "PP à 0.";
            case SOUPE_MAGIQUE ->
                    this.effet = "Soigne de 4, augmente temporairement la résistance de 3 et l'attaque de 2.";
            case NECTAR -> this.effet = "Un met délicat (#05).";
            case AMBROISIE -> this.effet = "Une boisson délicate (#06)";
            case SAC0 -> this.effet = "Permet de stocker 1 objet.";
            case SAC1 -> {
                switch (rand.nextInt(4)) {
                    case 0 -> this.effet = "Permet de stocker 1 objet et 1 arme à une main.";
                    case 1 -> this.effet = "Permet de stocker 1 objet ou 1 arme à une main.";
                    case 2 -> this.effet = "Permet de stocker 2 objets.";
                    case 3 -> this.effet = "Permet de stocker l'équivalent de 2 mains en arme.";
                }
            }
            case SAC2 -> {
                switch (rand.nextInt(4)) {
                    case 0 -> this.effet = "Permet de stocker 2 objets et 1 arme à une main.";
                    case 1 -> this.effet = "Permet de stocker l'équivalent de 2 objets ou mains.";
                    case 2 -> this.effet = "Permet de stocker 2 armes.";
                    case 3 -> this.effet = "Permet de stocker l'équivalent de 3 mains en arme.";
                }
            }
            case GUERRE -> this.effet = "Réagit à certain(s) dieu(x) (#07).";
            case LAME_VENT -> {
                this.attaque += 1;
                this.effet = "Votre première attaque inflige des dommages supplémentaires (#08).";
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
            case LAME_FERTILE -> {
                this.attaque -= 3;
                this.effet = "Réagit à certain(s) dieu(x) (#09).";
            }
            case RUNE_RESIDU -> this.effet = "Peut être consumé à la place de 2 PP.";
            case PARCH_FEU ->
                    this.effet = "Contient un sort de feu mineur pouvant être lancé pour 2 mana (#10)";
            case PARCH_DODO -> this.effet = "Contient un sort de sommeil pouvant être lancé pour 2 mana (#11).";
            case PARCH_FORCE ->
                    this.effet =
                            "Permet de lancer le sort Renforcement : pour 1PP, augmente définitivement l'attaque de 1, se détruit après usage.";
            case PARCH_LUMIERE ->
                    this.effet =
                            "Permet de lancer le sort Lumière pour 2 mana (#12).";
            case RUNE_CROISS -> this.effet = "Améliore certaines compétences... A condition de les posséder (#13).";
            case RUNE_PLUIE -> this.effet = "Améliore certaines compétences... A condition de les posséder (#14).";
            case RUNE_HAINE ->
                    this.effet = "Améliore certaines compétences... A condition de les posséder (#15).";
            case RUNE_VIRALE -> this.effet = "Améliore certaines compétences... A condition de les posséder (#16).";
            case RUNE_ARDENTE -> this.effet = "Améliore certaines compétences... A condition de les posséder (#17).";
            case RUNE_ARDENTE2 ->
                    this.effet = "Améliore certaines compétences... A condition de les posséder (#18).";
            case RUNE_DODO -> this.effet = "Améliore certaines compétences... A condition de les posséder (#19).";
            case RUNE_MORT -> this.effet = "Améliore certaines compétences... A condition de les posséder (#20).";
            case RUNE_ORAGE -> this.effet = "Améliore certaines compétences... A condition de les posséder (#21).";
            case RUNE_COMMERCE -> this.effet = "Améliore certaines compétences... A condition de les posséder (#22).";
            case SOIN -> this.effet = "Permet de soigner une cible (#23).";
            case PROTECTION -> this.effet = "??? (#24)";
            case RUNE_VENGEANCE ->
                    this.effet = "Récupérez de l'énergie chaque fois qu'un familier ou joueur allié est tué par un monstre.";
            case RUNE_INTERDITE ->
                    this.effet = "Améliore certaines compétences... A condition de les posséder (#25).";
            case BRACELET_ABSO ->
                    this.effet = "Vous pouvez absorber l'énergie de vos victimes (#26) (ne se cumule pas).";
            case LUNETTE -> this.effet = "Vous confère une vue spirituelle (#27).";
            case BRACELET_MAUDIT -> {
                this.resistance -= 3;
                this.armure += 1;
            }
            case DISSEC -> this.effet = "Augmente les compétences Dissection et Fouille (#28)";
            case ALCHI -> this.effet = "Augmente vos compétences de concoction (#29).";
            case BOURDON -> {
                this.effet = "Il est dit qu'il permet de mieux lancer des sorts... (#30)";
                attaque += 2;
                armure -= 2;
            }
            case PARCH_VOLCAN ->
                    this.effet = "Permet de lancer le sort Eruption volcanique pour 6 mana (#31).";
            case PARCH_ABSO ->
                    this.effet = "Vous pouvez absorber la force vitale de vos victimes (#32) (ne se cumule pas).";
            case PEGASE -> {
                this.resistance += 3;
                this.effet = "Vous permet de voyager plus efficacement (#33).";
            }
            case CHEVAL -> {
                this.resistance += 5;
                this.effet = "Vous aides à fuir plus efficacement (#34).";
            }
            case MOLOSSE -> {
                this.resistance += 4;
                this.attaque += 3;
            }
            case PIE -> {
                this.attaque += 1;
                this.effet = "Ramasse parfois des pièces et vous aide à fuir (#35).";
            }
            case SPHINX -> {
                this.attaque += 1;
                this.resistance += 2;
                this.effet = "Une fois par combat, augmente temporairement la résistance d'une cible (#36).";
            }
            case MULE -> {
                this.resistance += 1;
                this.effet = "Peut porter jusqu'à 3 sacs et leurs contenue. Les effets des sacs sont appliqués.";
            }
            case BATTERIE -> {
                this.attaque += 1;
                this.effet = "Contient 3/5 mana. Peut absorber ou décharger son mana sur une cible consentante.";
            }
            case ARAIGNE -> this.attaque += 5;
            case ELEPHANT -> {
                this.resistance += 4;
                this.armure += 1;
            }
            //TODO
            case FEE -> {
                this.resistance += 1;
                this.effet = "Une fois par combat, soigne une cible de 10.";
            }
            case ALTRUISME ->
                    this.effet =
                            "Au début de chaque combat, répartissez 5 points temporaires de résistance entre " + "vos"
                                    + " alliés (familiers inclus)." + "Vous ne pouvez les attribuer à vous même.";
            case RUNE_ARCA -> this.effet = "Diminue de 1 la coût de tout vos sort";
            case ANTIDOTE -> this.effet = "Le consommer fournit une immunité définitive au poison et à la cécité.";
            case ANNIHILATION ->
                    this.effet = "En sacrifiant une rune, vous pouvez lancer un attaque magique de puissance le " +
                            "triple de votre attaque classique.";
            case REZ ->
                    this.effet = "Si vous êtes mort à la fin d'un combat, vous ramène à la vie. S'efface après " +
                            "utilisation. Ne peut être échangé.";
            case FUITE ->
                    this.effet = "A tout moment, vous pouvez utiliser cet objet pour fuir un combat. Se détruit " +
                            "après" + " usage. Ne peut être utilisé si vous êtes mort.";
            case GRENADE -> this.effet = "Inflige 4 dommages additionnels. Utilisable 3 fois au total.";
            case MER_EXP ->
                    this.effet =
                            "Si vous vous trouvez en mer, vous pouvez choisir n'importe quel résultat entre 1 " + "et"
                                    + " 7 au lieu de lancer le dé." + "Un résultat choisi ignore le bonus de dé.";
            case ITEM_IMMUN ->
                    this.effet = "Chaque fois que vous perdez un équipement, récupérez le." + " (ne fonctionne pas si" +
                            " vous mourrez, vendez, échangez ou dépensez un équipement)";
            case SAC_TEMP ->
                    this.effet = "Lorsque vous utilisez un objet à usage limité pour la dernière fois," + "vous " +
                            "pouvez choisir de détruire cette sacoche à la place et récupérez tous les utilisations " + "de votre objet.";
            //fin TODO
            case AUCUN -> {
            }
        }
    }
    
    private void presente() {
        presente(0);
    }
    
    /**
     * Présente l'objet
     * @param reduction la réduction sur le coup de l'objet
     */
    private void presente(int reduction) {
        String type = getString();
        System.out.println("\n" + this.nom + " (" + type + ")");
        if (this.attaque > 0) {
            System.out.println("+" + this.attaque + " attaque(s)");
        } else if (this.attaque < 0) {
            System.out.println("-" + -this.attaque + " attaque(s)");
        }
        if (this.resistance > 0) {
            System.out.println("+" + this.resistance + " resistance(s)");
        } else if (this.resistance < 0) {
            System.out.println("-" + -this.resistance + " resistance(s)");
        }
        if (this.armure > 0) {
            System.out.println("+" + this.armure + " armure(s)");
        } else if (this.armure < 0) {
            System.out.println("-" + -this.armure + " armure(s)");
        }
        if (this.effet != null) {
            System.out.println(this.effet);
        }
        if (this.prix > 0) {
            int temp = this.prix - reduction;
            if (temp > 0) {
                System.out.println("Prix : " + temp);
            } else {
                System.out.println("Prix : 1");
            }
        }
        System.out.println();
    }
    
    private String getString() {
        String type;
        switch (base) {
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
            case CREATURE -> type = "monture";
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
    //Equipement.Equipement(String nom, Rang rang, Base base, int attaque, int resistance, int armure, int prix,
    // String effet)
    
    static Equipement loterie = new Equipement("ticket de loterie", Rang.O, Base.AUTRE, 0, 0, 0, 3,
            "Lancez un dé " + "à" + " 6 face, si vous faite 6, gagnez 15PO. Jetez ce ticket.");
    
    static Equipement main1_0 = new Equipement("branche solide", Rang.O, Base.MAIN_1, 1, 0, 0, 5);
    static Equipement main2_0 = new Equipement("pavé", Rang.O, Base.MAIN_2, 4, 0, 0, 7);
    static Equipement arc_0 = new Equipement("vieil arc", Rang.O, Base.ARC, 0, 0, 0, 3);
    static Equipement bouclier_0 = new Equipement("planche en bois", Rang.O, Base.BOUCLIER, 0, 2, 0, 8);
    static Equipement armure_0 = new Equipement("vieille armure", Rang.O, Base.ARMURE, 0, 1, 0, 3);
    static Equipement casque_0 = new Equipement("seau", Rang.O, Base.CASQUE, 0, 1, 0, 3);
    static Equipement ingredients_0 = new Equipement("ingrédient", Rang.O, Base.AUTRE, 0, 0, 0, 3);
    static Equipement ingredients2_0 = new Equipement("2 ingrédients", Rang.O, Base.AUTRE, 0, 0, 0, 7);
    
    static Equipement[] marche0 = {arc_0, main1_0, main2_0, armure_0, bouclier_0, casque_0, ingredients_0,
            ingredients2_0};
    
    static Equipement arc_I = new Equipement("arc", Rang.I, Base.ARC, 1, 0, 0, 4);
    static Equipement bouclier_I = new Equipement("petit bouclier", Rang.I, Base.BOUCLIER, 1, 2, 0, 7);
    static Equipement main1_I = new Equipement("couteau", Rang.I, Base.MAIN_1, 2, 0, 0, 6);
    static Equipement main2_I = new Equipement("grande épée", Rang.I, Base.MAIN_2, 5, 0, 0, 9);
    static Equipement armure_I = new Equipement("armure", Rang.I, Base.ARMURE, 0, 2, 0, 5);
    static Equipement casque_I = new Equipement("mauvais casque", Rang.I, Base.CASQUE, 0, 2, 0, 5);
    static Equipement ingredients_I = new Equipement("ingrédient", Rang.O, Base.AUTRE, 0, 0, 0, 4);
    static Equipement ingredients2_I = new Equipement("2 ingrédients", Rang.O, Base.AUTRE, 0, 0, 0, 8);
    
    static Equipement[] marcheI = {arc_I, main1_I, main2_I, armure_I, bouclier_I, casque_I, ingredients_I,
            ingredients2_I, loterie};
    
    static Equipement arc_II = new Equipement("grand arc", Rang.II, Base.ARC, 3, 0, 0, 5);
    static Equipement bouclier_II = new Equipement("bouclier", Rang.II, Base.BOUCLIER, 2, 3, 0, 9);
    static Equipement main1_II = new Equipement("hachette", Rang.II, Base.MAIN_1, 4, 0, 0, 8);
    static Equipement main2_II = new Equipement("grande hache", Rang.II, Base.MAIN_2, 9, 0, 0, 12);
    static Equipement armure_II = new Equipement("bonne armure", Rang.II, Base.ARMURE, 0, 6, 1, 11);
    static Equipement casque_II = new Equipement("casque", Rang.II, Base.CASQUE, 0, 3, 0, 6);
    
    static Equipement[] marcheII = {arc_II, main1_II, main2_II, armure_II, bouclier_II, casque_II, ingredients_0,
            ingredients2_0, loterie};
    
    static Equipement arc_III = new Equipement("arc à poulie", Rang.III, Base.ARC, 5, 0, 0, 7);
    static Equipement bouclier_III = new Equipement("bon bouclier", Rang.III, Base.BOUCLIER, 4, 5, 0, 13);
    static Equipement main1_III = new Equipement("bon glaive", Rang.III, Base.MAIN_1, 6, 0, 0, 12);
    static Equipement main2_III = new Equipement("grosse épée", Rang.III, Base.MAIN_2, 13, 0, 0, 16);
    static Equipement armure_III = new Equipement("excellente armure", Rang.III, Base.ARMURE, 0, 11, 2, 21);
    static Equipement casque_III = new Equipement("bon casque", Rang.III, Base.CASQUE, 0, 5, 1, 18);
    
    static Equipement[] marcheIII = {arc_III, main1_III, main2_III, armure_III, bouclier_III, casque_III};
    
    /**
     * Sélectionne aléatoirement des objets dans une liste sans doublons et les présente
     * @param reduc    réduction appliquée à l'affichage
     * @param list     liste d'équipements disponibles
     * @param nb_items nombre d'objets à proposer
     */
    private static void marche_global(int reduc, Equipement[] list, int nb_items) {
        List<Equipement> valides = new ArrayList<>();
        for (Equipement e : list) {
            if (e != null) {
                valides.add(e);
            }
        }
        
        if (nb_items > valides.size()) {
            throw new IllegalArgumentException("Impossible de sélectionner " + nb_items + " objets uniques parmi " + valides.size() + " valides.");
        }
        
        Collections.shuffle(valides, rand);
        List<Equipement> selection = valides.subList(0, nb_items);
        
        for (Equipement e : selection) {
            e.presente(reduc);
        }
        
        Output.jouerSonMarche();
    }
    
    /**
     * Affiche 3 objets aléatoires du marché des prairies.
     * @param reduc réduction appliquée à l'affichage
     */
    public static void marche_prairie(int reduc) {
        System.out.println("Vous êtes dans le marché des prairies et trois objets vous sont proposés :");
        Equipement[] list = new Equipement[marche0.length + marcheI.length];
        System.arraycopy(marche0, 0, list, 0, marche0.length);
        System.arraycopy(marcheI, 0, list, marche0.length, marcheI.length);
        marche_global(reduc, list, 3);
    }
    
    /**
     * Affiche 3 objets du marché des vignes.
     * @param reduc réduction appliquée à l'affichage
     */
    public static void marche_vigne(int reduc) {
        System.out.println("Vous êtes dans le marché des vignes et trois objets vous sont proposés :");
        marche_global(reduc, marcheI, 3);
    }
    
    /**
     * Affiche entre 2 et 4 objets proposés par un marchand dans un temple.
     * @param reduc réduction appliquée à l'affichage
     */
    public static void marche_temple(int reduc) {
        int nbo = rand.nextInt(3) + 2; //2~4
        System.out.println("Vous trouvez un marchant dans le temple, qui vous propose " + nbo + " objets :");
        Equipement[] list = new Equipement[marcheI.length + marcheII.length];
        System.arraycopy(marcheI, 0, list, 0, marcheI.length);
        System.arraycopy(marcheII, 0, list, marcheI.length, marcheII.length);
        marche_global(reduc, list, nbo);
    }
    
    /**
     * Affiche 2 objets proposés par un navire marchand.
     * @param reduc réduction appliquée à l'affichage
     */
    public static void marche_mer(int reduc) {
        System.out.println("Vous croisez un navire marchand qui vous propose deux objets :");
        Equipement[] list = new Equipement[marcheII.length + marcheIII.length];
        System.arraycopy(marcheII, 0, list, 0, marcheII.length);
        System.arraycopy(marcheIII, 0, list, marcheII.length, marcheIII.length);
        marche_global(reduc, list, 2);
    }
    
    /**
     * Affiche un seul objet proposé par un pèlerin dans les monts.
     * @param reduc réduction appliquée à l'affichage
     */
    public static void marche_monts(int reduc) {
        System.out.println("Vous croisez un pèlerin qui vous propose de vous vendre un objet :");
        marche_global(reduc, marcheIII, 1);
    }
}

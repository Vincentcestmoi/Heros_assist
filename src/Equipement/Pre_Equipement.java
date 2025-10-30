package Equipement;

import Enum.Base;
import Enum.Rang;
import Enum.Promo_Type;
import Enum.Effet_equip;
import Exterieur.Output;
import Exterieur.Input;

import java.io.IOException;
import java.util.Random;

public class Pre_Equipement {
    public String nom;
    public Base base;
    public Rang rang;
    public Effet_equip effet;
    public boolean is_unique;
    public Promo_Type promo_type;

    Pre_Equipement(String nom, Base base, Rang rang, Effet_equip effet, boolean is_unique) {
        this.nom = nom;
        this.base = base;
        this.rang = rang;
        this.effet = effet;
        this.is_unique = is_unique;
        this.promo_type = Promo_Type.QUIT;
    }

    Pre_Equipement(String nom, Base base, Rang rang, Effet_equip effet, Promo_Type promo_type) {
        this.nom = nom;
        this.base = base;
        this.rang = rang;
        this.effet = effet;
        this.is_unique = true;
        this.promo_type = promo_type;
    }

    static Random rand = new Random();

    /**
     * Suprimme les équipements donnés
     *
     * @param nom   le nom de l'équipement à supprimer
     * @param rang  le rang de l'équipement à supprimer
     * @param promo le type de promotion, significatif uniquement si le rang est PROMOTION
     */
    public static void safe_delete(String nom, Rang rang, Promo_Type promo) {
        safe_delete(nom, rang, promo, false);
    }

    /**
     * Suprimme les équipements donnés
     *
     * @param nom     le nom de l'équipement à supprimer
     * @param rang    le rang de l'équipement à supprimer
     * @param promo   le type de promotion, significatif uniquement si le rang est PROMOTION
     * @param silence si on affiche le message de suppression
     */
    public static void safe_delete(String nom, Rang rang, Promo_Type promo, boolean silence) {
        boolean corect = false;
        Pre_Equipement[] list = switch (rang) {
            case O -> rang0;
            case I -> rang1;
            case II -> rang2;
            case III -> rang3;
            case IV -> rang4;
            case PROMOTION -> {
                corect = true;
                yield getPromo(promo);
            }
        };
        if (list == null) {
            System.out.println(promo + " ou " + rang + " n'existe pas.");
            return;
        }
        for (int i = 0; i < list.length; i++) {
            if (list[i] != null && nom.equals(list[i].nom)) {
                if (!silence) {
                    System.out.println(nom + " supprimé(e) avec succès.");
                }
                list[i] = null;
                if (corect) {
                    switch (promo) {
                        case MONTURE -> nb_monture--;
                        case AMELIORATION -> nb_boost--;
                        case ARTEFACT -> nb_arte--;
                    }
                }
                return;
            }
        }
        System.out.println(nom + " n'existe pas.");
    }

    private static Pre_Equipement[] getPromo(Promo_Type promo) {
        return switch (promo) {
            case MONTURE -> prom_list_mont;
            case ARTEFACT -> prom_list_arte;
            case AMELIORATION -> prom_list_boost;
            case QUIT -> null;
        };
    }

    /**
     * Retire les objets uniques déjà droppés des listes de drops
     */
    private void safe_delete() {
        if (this.is_unique) {
            Output.dismiss_item(this);
            safe_delete(this.nom, this.rang, this.promo_type, true);
        }
    }

    /**
     * Extrait et renvoie un équipement rang 0
     */
    public static Pre_Equipement drop_0() {
        System.out.println("Vous récupérez un équipement rang 0 :");
        int t;
        Pre_Equipement equip;
        do {
            t = rand.nextInt(rang0.length);
            equip = rang0[t];
        } while (equip == null);
        equip.safe_delete();
        return equip;
    }

    /**
     * Extrait et renvoie un équipement rang I
     */
    public static Pre_Equipement drop_1() {
        System.out.println("Vous récupérez un équipement rang I :");
        int t;
        Pre_Equipement equip;
        do {
            t = rand.nextInt(rang1.length);
            equip = rang1[t];
        } while (equip == null);
        equip.safe_delete();
        return equip;
    }

    /**
     * Extrait et renvoie un équipement rang II
     */
    public static Pre_Equipement drop_2() {
        System.out.println("Vous récupérez un équipement rang II :");
        int t;
        Pre_Equipement equip;
        do {
            t = rand.nextInt(rang2.length);
            equip = rang2[t];
        } while (equip == null);
        equip.safe_delete();
        return equip;
    }

    /**
     * Extrait et renvoie un équipement rang III
     */
    public static Pre_Equipement drop_3() {
        System.out.println("Vous récupérez un équipement rang III :");
        int t;
        Pre_Equipement equip;
        do {
            t = rand.nextInt(rang3.length);
            equip = rang3[t];
        } while (equip == null);
        equip.safe_delete();
        return equip;
    }

    /**
     * Extrait et renvoie un équipement rang IV
     */
    public static Pre_Equipement drop_4() {
        System.out.println("Vous récupérez un équipement rang IV :");
        int t;
        Pre_Equipement equip;
        do {
            t = rand.nextInt(rang4.length);
            equip = rang4[t];
        } while (equip == null);
        equip.safe_delete();
        return equip;
    }

    /**
     * Extrait et renvoie une promotion
     *
     * @return un pre-équipement de rang promotion
     */
    public static Pre_Equipement drop_promo() throws IOException {
        System.out.println("Vous récupérez une promotion.");
        Pre_Equipement[] list;
        Promo_Type type = Input.promo();
        switch (type) {
            case MONTURE -> list = prom_list_mont;
            case AMELIORATION -> list = prom_list_boost;
            case ARTEFACT -> list = prom_list_arte;
            default -> {
                System.out.println("Erreur : Equipement.Equipement non reconnu.");
                return new Pre_Equipement("Erreur", AUTRE, PROMOTION, AUCUN, false);
            }
        }
        int t;
        Pre_Equipement equip;
        do {
            t = rand.nextInt(list.length);
            equip = list[t];
        } while (equip == null);
        equip.safe_delete();
        return equip;
    }

    static Base AUTRE = Base.AUTRE;
    static Base ARC = Base.ARC;
    static Base ARMURE = Base.ARMURE;
    static Base CASQUE = Base.CASQUE;
    static Base CEINTURE = Base.CEINTURE;
    static Base MAIN1 = Base.MAIN_1;
    static Base MAIN2 = Base.MAIN_2;
    static Base BRACELET = Base.BRACELET;
    static Base BOUCLIER = Base.BOUCLIER;
    static Base CONSO_EX = Base.CONSO_EX;
    static Base CONSO_MAIN = Base.CONSO_MAIN;
    static Base SAC = Base.SAC;
    static Base RUNE = Base.RUNE;
    static Base MONTURE = Base.MONTURE;

    static Rang O = Rang.O;
    static Rang I = Rang.I;
    static Rang II = Rang.II;
    static Rang III = Rang.III;
    static Rang IV = Rang.IV;
    static Rang PROMOTION = Rang.PROMOTION;

    static Effet_equip AUCUN = Effet_equip.AUCUN;

    static Pre_Equipement ceinture0 = new Pre_Equipement("vieille ceinture", CEINTURE, O, AUCUN, false);
    static Pre_Equipement arc0 = new Pre_Equipement("vieil arc", ARC, O, AUCUN, false);
    static Pre_Equipement dague0 = new Pre_Equipement("branche solide", MAIN1, O, AUCUN, false);
    static Pre_Equipement epee0 = new Pre_Equipement("gros caillou", MAIN2, O, AUCUN, false);
    static Pre_Equipement armure0 = new Pre_Equipement("vieille armure", ARMURE, O, AUCUN, false);
    static Pre_Equipement casque0 = new Pre_Equipement("vieux seau", CASQUE, O, AUCUN, false);
    static Pre_Equipement bouclier0 = new Pre_Equipement("planche en bois", BOUCLIER, O, AUCUN, false);
    static Pre_Equipement healPP0 = new Pre_Equipement("ration de survie", CONSO_EX, O, Effet_equip.CONSO_EXT1, false);
    static Pre_Equipement heal0 = new Pre_Equipement("fruit en conserve", CONSO_EX, O, Effet_equip.CONSO_RES2, false);
    static Pre_Equipement sac0 = new Pre_Equipement("vieux sac", SAC, O, Effet_equip.SAC0, false);

    static Pre_Equipement bracelet0 = new Pre_Equipement("bracelet de protection cabossé", BRACELET, O, Effet_equip.RESISTANCE1, true);
    static Pre_Equipement lam_enf = new Pre_Equipement("Lame infernale", MAIN1, O, Effet_equip.ENFERS4, true);
    static Pre_Equipement lam_herbe = new Pre_Equipement("Lame végétale", MAIN1, O, Effet_equip.PRAIRIE3, true);
    static Pre_Equipement fleche_plus0 = new Pre_Equipement("plumes d'oie", AUTRE, O, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plus02 = new Pre_Equipement("plumes d'aigle", AUTRE, O, Effet_equip.ARCA, true);
    static Pre_Equipement rune0 = new Pre_Equipement("rune résiduelle", RUNE, O, Effet_equip.RUNE_RESIDU, true);

    static Pre_Equipement[] rang0 = {ceinture0, arc0, dague0, epee0, armure0, casque0, heal0, healPP0, bouclier0,
            bracelet0, fleche_plus0, fleche_plus02, lam_enf, lam_herbe, sac0, rune0};

    static Pre_Equipement ceintureI = new Pre_Equipement("ceinture", CEINTURE, I, AUCUN, false);
    static Pre_Equipement arcI = new Pre_Equipement("arc simple", ARC, I, AUCUN, false);
    static Pre_Equipement dagueI = new Pre_Equipement("dague usagée", MAIN1, I, AUCUN, false);
    static Pre_Equipement epeeI = new Pre_Equipement("grande épée", MAIN2, I, AUCUN, false);
    static Pre_Equipement armureI = new Pre_Equipement("armure basique", ARMURE, I, AUCUN, false);
    static Pre_Equipement casqueI = new Pre_Equipement("casque endommagé", CASQUE, I, AUCUN, false);
    static Pre_Equipement bouclierI = new Pre_Equipement("petit bouclier", BOUCLIER, I, AUCUN, false);
    static Pre_Equipement healPPI = new Pre_Equipement("aliments sain", CONSO_EX, I, Effet_equip.CONSO_EXT2, false);
    static Pre_Equipement healI = new Pre_Equipement("potion de vie", CONSO_EX, I, Effet_equip.CONSO_RES4, false);
    static Pre_Equipement sacI = new Pre_Equipement("sac à dos", SAC, I, Effet_equip.SAC1, false);

    static Pre_Equipement fleche_plusI = new Pre_Equipement("pointe en fer", AUTRE, I, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusI2 = new Pre_Equipement("hance d'acier", AUTRE, I, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusI3 = new Pre_Equipement("viseur", AUTRE, I, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusI4 = new Pre_Equipement("pointe affutée", AUTRE, I, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusI5 = new Pre_Equipement("monocle de précision", AUTRE, I, Effet_equip.ARCA, true);
    static Pre_Equipement armure_w = new Pre_Equipement("armure de guerre", ARMURE, I, Effet_equip.GUERRE, true);
    static Pre_Equipement casque_w = new Pre_Equipement("casque de guerre", CASQUE, I, Effet_equip.GUERRE, true);
    static Pre_Equipement bouclier_w = new Pre_Equipement("bouclier de guerre", BOUCLIER, I, Effet_equip.GUERRE, true);
    static Pre_Equipement armure_a = new Pre_Equipement("armure enchanté", ARMURE, I, Effet_equip.ATTAQUE1, true);
    static Pre_Equipement casque_a = new Pre_Equipement("casque enchanté", CASQUE, I, Effet_equip.ATTAQUE1, true);
    static Pre_Equipement bouclier_a = new Pre_Equipement("bouclier enchanté", BOUCLIER, I, Effet_equip.ATTAQUE1, true);
    static Pre_Equipement main1_a = new Pre_Equipement("dague enchanté", MAIN1, I, Effet_equip.ATTAQUE1, true);
    static Pre_Equipement main2_a = new Pre_Equipement("épée tranchante", MAIN2, I, Effet_equip.ATTAQUE1, true);
    static Pre_Equipement armure_r = new Pre_Equipement("solide armure", ARMURE, I, Effet_equip.RESISTANCE1, true);
    static Pre_Equipement casque_r = new Pre_Equipement("casque de bonne facture", CASQUE, I, Effet_equip.RESISTANCE1, true);
    static Pre_Equipement bouclier_r = new Pre_Equipement("bon bouclier", BOUCLIER, I, Effet_equip.RESISTANCE1, true);
    static Pre_Equipement main2_r = new Pre_Equipement("longue lance", MAIN2, I, Effet_equip.RESISTANCE1, true);
    static Pre_Equipement main1_ba = new Pre_Equipement("coutelat", MAIN1, I, Effet_equip.BAD_ALLONGE, true);
    static Pre_Equipement main2_ba = new Pre_Equipement("épée courte", MAIN2, I, Effet_equip.BAD_ALLONGE, true);
    static Pre_Equipement armure_ar = new Pre_Equipement("armure renforcée", ARMURE, I, Effet_equip.ARMURE1, true);
    static Pre_Equipement elem_main = new Pre_Equipement("Lame des vents", MAIN2, I, Effet_equip.LAME_VENT, true);
    static Pre_Equipement elem_casque = new Pre_Equipement("Diadème de flamme", CASQUE, I, Effet_equip.COIFFE_FEU, true);
    static Pre_Equipement elem_armure = new Pre_Equipement("Armure de glace", ARMURE, I, Effet_equip.ARMURE_GLACE, true);
    static Pre_Equipement elem_bouclier = new Pre_Equipement("Bouclier des roches", BOUCLIER, I, Effet_equip.BOUCLIER_TERRE, true);
    static Pre_Equipement bracelet_r1 = new Pre_Equipement("Bracelet de protection", BRACELET, I, Effet_equip.RESISTANCE2, true);
    static Pre_Equipement bracelet_ar1 = new Pre_Equipement("Bracelet du défenseur", BRACELET, I, Effet_equip.ARMURE1, true);
    static Pre_Equipement bracelet_a1 = new Pre_Equipement("Bracelet de force", BRACELET, I, Effet_equip.ATTAQUE1, true);
    static Pre_Equipement bracelet_ra1 = new Pre_Equipement("Bracelet de colère", BRACELET, I, Effet_equip.RAGE1, true);
    static Pre_Equipement epee_dodo = new Pre_Equipement("Epee du sommeil", MAIN2, I, Effet_equip.LAME_DODO, true);
    static Pre_Equipement parchemin_feu = new Pre_Equipement("Parchemin de feu", AUTRE, I, Effet_equip.PARCH_FEU, true);
    static Pre_Equipement parch_force = new Pre_Equipement("Parchemin de puissance", AUTRE, I, Effet_equip.PARCH_FORCE, true);
    static Pre_Equipement rune_croiss = new Pre_Equipement("Rune végétale", AUTRE, I, Effet_equip.RUNE_CROISS, true);
    static Pre_Equipement rune_feu = new Pre_Equipement("Rune ardente", AUTRE, I, Effet_equip.RUNE_ARDENTE, true);
    static Pre_Equipement rune_pluie = new Pre_Equipement("Rune pluvieuse", AUTRE, I, Effet_equip.RUNE_PLUIE, true);
    static Pre_Equipement rune_haine = new Pre_Equipement("Rune violente", AUTRE, I, Effet_equip.RUNE_HAINE, true);
    static Pre_Equipement rune_virale = new Pre_Equipement("Rune virale", AUTRE, I, Effet_equip.RUNE_VIRALE, true);
    static Pre_Equipement rune_orage = new Pre_Equipement("Rune orageuse", AUTRE, I, Effet_equip.RUNE_ORAGE, true);
    static Pre_Equipement rune_mort = new Pre_Equipement("Rune mortifère", AUTRE, I, Effet_equip.RUNE_MORT, true);
    static Pre_Equipement rune_dodo = new Pre_Equipement("Rune endormie", AUTRE, I, Effet_equip.RUNE_DODO, true);
    static Pre_Equipement popoI = new Pre_Equipement("Soupe magique", CONSO_MAIN, I, Effet_equip.SOUPE_MAGIQUE, true);

    static Pre_Equipement[] rang1 = {fleche_plusI, fleche_plusI2, fleche_plusI3, fleche_plusI4, fleche_plusI5, ceintureI,
            arcI, dagueI, epeeI, armureI, casqueI, bouclierI, healPPI, healI, sacI, armure_w, casque_w,
            bouclier_w, armure_a, casque_a, bouclier_a, main1_a, main1_a, main2_a, armure_r,
            casque_r, bouclier_r, main2_r, main1_ba, main1_ba, main2_ba, armure_ar, elem_main,
            elem_casque, elem_armure, elem_bouclier, bracelet_r1, bracelet_r1, bracelet_ar1,
            bracelet_a1, bracelet_a1, bracelet_ra1, epee_dodo, parchemin_feu, parch_force, rune_croiss,
            rune_feu, rune_feu, rune_pluie, rune_haine, rune_virale, rune_orage, rune_mort, rune_dodo, popoI};

    static Pre_Equipement ceintureII = new Pre_Equipement("ceinture de fer", CEINTURE, II, AUCUN, false);
    static Pre_Equipement arcII = new Pre_Equipement("grand arc", ARC, II, AUCUN, false);
    static Pre_Equipement dagueII = new Pre_Equipement("épée courte", MAIN1, II, AUCUN, false);
    static Pre_Equipement epeeII = new Pre_Equipement("hache lourde", MAIN2, II, AUCUN, false);
    static Pre_Equipement armureII = new Pre_Equipement("armure", ARMURE, II, AUCUN, false);
    static Pre_Equipement casqueII = new Pre_Equipement("casque simple", CASQUE, II, AUCUN, false);
    static Pre_Equipement bouclierII = new Pre_Equipement("bouclier", BOUCLIER, II, AUCUN, false);
    static Pre_Equipement sacII = new Pre_Equipement("sac de voyage", SAC, II, Effet_equip.SAC2, false);
    static Pre_Equipement popoII = new Pre_Equipement("potion d'énergie", CONSO_MAIN, II, Effet_equip.PP4, false);
    static Pre_Equipement healII = new Pre_Equipement("bandages", CONSO_EX, II, Effet_equip.CONSO_RES6, false);

    static Pre_Equipement fleche_plusII = new Pre_Equipement("poison à flèche", AUTRE, II, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusII2 = new Pre_Equipement("encoche renforcée", AUTRE, II, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusII3 = new Pre_Equipement("projectile incendiare", AUTRE, II, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusII4 = new Pre_Equipement("acide à flèche", AUTRE, II, Effet_equip.ARCA, true);
    static Pre_Equipement armureII_ar = new Pre_Equipement("armure renforcée", ARMURE, II, Effet_equip.ARMURE1, true);
    static Pre_Equipement casque_II_ar = new Pre_Equipement("casque renforcé", CASQUE, II, Effet_equip.ARMURE1, true);
    static Pre_Equipement bouclierII_ar = new Pre_Equipement("bouclier renforcé", BOUCLIER, II, Effet_equip.ARMURE1, true);
    static Pre_Equipement armureII_r = new Pre_Equipement("armure de bonne facture", ARMURE, II, Effet_equip.RESISTANCE2, true);
    static Pre_Equipement casque_II_r = new Pre_Equipement("casque de qualité", CASQUE, II, Effet_equip.RESISTANCE2, true);
    static Pre_Equipement bouclierII_r = new Pre_Equipement("bon bouclier", BOUCLIER, II, Effet_equip.RESISTANCE2, true);
    static Pre_Equipement main1II_ba = new Pre_Equipement("dague", MAIN1, II, Effet_equip.BAD_ALLONGE, true);
    static Pre_Equipement main2II_ba = new Pre_Equipement("masse de guerre", MAIN2, II, Effet_equip.BAD_ALLONGE, true);
    static Pre_Equipement main2II_ga = new Pre_Equipement("bonne lance", MAIN2, II, Effet_equip.BONNE_ALLONGE, true);
    static Pre_Equipement lame_mer = new Pre_Equipement("Trident enchanté", MAIN1, II, Effet_equip.TRIDENT, true);
    static Pre_Equipement bracelet_energie = new Pre_Equipement("Bracelet d'absorption", BRACELET, II, Effet_equip.BRACELET_ABSO, true);
    static Pre_Equipement bracelet_soin = new Pre_Equipement("Bracelet de soin", BRACELET, II, Effet_equip.SOIN, true);
    static Pre_Equipement bracelet_ra2 = new Pre_Equipement("Bracelet de rage", BRACELET, II, Effet_equip.RAGE2, true);
    static Pre_Equipement bracelet_a2 = new Pre_Equipement("Bracelet de puissance", BRACELET, II, Effet_equip.ATTAQUE2, true);
    static Pre_Equipement bracelet_r2 = new Pre_Equipement("Bracelet défensif", BRACELET, II, Effet_equip.RESISTANCE4, true);
    static Pre_Equipement fleche_plusIIex = new Pre_Equipement("flèche explosives", AUTRE, II, Effet_equip.ARCEXP, true);
    static Pre_Equipement parchemin_dodo = new Pre_Equipement("Parchemin de sommeil", AUTRE, II, Effet_equip.PARCH_DODO, true);
    static Pre_Equipement rune_feu2 = new Pre_Equipement("Rune incinérante", AUTRE, II, Effet_equip.RUNE_ARDENTE2, true);
    static Pre_Equipement medail_protect = new Pre_Equipement("Médaillon de protection", AUTRE, II, Effet_equip.PROTECTION, true);
    static Pre_Equipement lunette = new Pre_Equipement("Lunette magique", AUTRE, II, Effet_equip.LUNETTE, true);
    static Pre_Equipement rune_veng = new Pre_Equipement("Rune vengeresse", RUNE, II, Effet_equip.RUNE_VENGEANCE, true);
    static Pre_Equipement rune_necro = new Pre_Equipement("Rune interdite", RUNE, II, Effet_equip.RUNE_INTERDITE, true);
    static Pre_Equipement bracelet_maudit = new Pre_Equipement("Bracelet maudit", BRACELET, II, Effet_equip.BRACELET_MAUDIT, true);
    static Pre_Equipement dissect = new Pre_Equipement("outils chirurgicaux", AUTRE, II, Effet_equip.DISSEC, true);
    static Pre_Equipement alchi = new Pre_Equipement("équipement d'alchimiste", AUTRE, II, Effet_equip.ALCHI, true);
    static Pre_Equipement bourdon = new Pre_Equipement("bâton magique", MAIN1, II, Effet_equip.BOURDON, true);

    static Pre_Equipement[] rang2 = {fleche_plusII, fleche_plusII2, fleche_plusII3, fleche_plusII4, ceintureII, arcII,
            dagueII, epeeII, armureII, casqueII, bouclierII, sacII, casque_II_ar, armureII_ar, bouclierII_ar,
            armureII_r, casque_II_r, bouclierII_r, main1II_ba, main1II_ba, main2II_ba, main2II_ga, lame_mer,
            bracelet_energie, bracelet_energie, bracelet_soin, bracelet_ra2, bracelet_a2, bracelet_r2, fleche_plusIIex,
            fleche_plusIIex, parchemin_dodo, rune_feu2, rune_feu2, popoII, healII, medail_protect, lunette, rune_veng,
            rune_necro, bracelet_maudit, dissect, alchi, bourdon};

    static Pre_Equipement ceintureIII = new Pre_Equipement("ceinture des champions", CEINTURE, III, AUCUN, false);
    static Pre_Equipement arcIII = new Pre_Equipement("arc à poulie", ARC, III, AUCUN, false);
    static Pre_Equipement dagueIII = new Pre_Equipement("dague affutée", MAIN1, III, AUCUN, false);
    static Pre_Equipement epeeIII = new Pre_Equipement("grosse épée", MAIN2, III, AUCUN, false);
    static Pre_Equipement armureIII = new Pre_Equipement("excellente armure", ARMURE, III, AUCUN, false);
    static Pre_Equipement casqueIII = new Pre_Equipement("casque de qualité", CASQUE, III, AUCUN, false);
    static Pre_Equipement bouclierIII = new Pre_Equipement("bon bouclier", BOUCLIER, III, AUCUN, false);
    static Pre_Equipement sacIII = new Pre_Equipement("sac de voyage renforcé", SAC, III, Effet_equip.SAC2, false);
    static Pre_Equipement popoIII = new Pre_Equipement("potion de mana", Base.CONSO_MAIN, III, Effet_equip.PP6, false);
    static Pre_Equipement healIII = new Pre_Equipement("fortifiant", CONSO_EX, III, Effet_equip.CONSO_RES8, false);
    static Pre_Equipement healPPIII = new Pre_Equipement("drogue de guerre", CONSO_EX, III, Effet_equip.CONSO_EXT3, false);

    static Pre_Equipement fleche_plusIII = new Pre_Equipement("pointe torsadée", AUTRE, III, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusIII2 = new Pre_Equipement("structure enchantée", AUTRE, III, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusIII3 = new Pre_Equipement("pointe électrique", AUTRE, III, Effet_equip.ARCA, true);
    static Pre_Equipement bracelet_ar3 = new Pre_Equipement("Bracelet d'invincibilité", BRACELET, III, Effet_equip.ARMURE2, true);
    static Pre_Equipement bracelet_r3 = new Pre_Equipement("Bracelet de l'immortel", BRACELET, III, Effet_equip.RESISTANCE6, true);
    static Pre_Equipement bracelet_ra3 = new Pre_Equipement("Bracelet du guerrier fou", BRACELET, III, Effet_equip.RAGE3, true);
    static Pre_Equipement parchemin_folie = new Pre_Equipement("parchemin de folie guerrière", AUTRE, III, Effet_equip.PARCH_BERSERK, true);
    static Pre_Equipement popo_max = new Pre_Equipement("potion ancestrale", Base.CONSO_MAIN, III, Effet_equip.PPMAX, true);
    static Pre_Equipement casque_III_ar = new Pre_Equipement("casque renforcé", CASQUE, III, Effet_equip.ARMURE1, true);
    static Pre_Equipement bouclierIII_ar = new Pre_Equipement("bouclier renforcé", BOUCLIER, III, Effet_equip.ARMURE1, true);
    static Pre_Equipement armureIII_ar = new Pre_Equipement("chef d'oeuvre", ARMURE, III, Effet_equip.ARMURE2, true);
    static Pre_Equipement armureIII_r = new Pre_Equipement("très bonne armure", ARMURE, III, Effet_equip.RESISTANCE2, true);
    static Pre_Equipement casque_III_r = new Pre_Equipement("casque supérieur", CASQUE, III, Effet_equip.RESISTANCE2, true);
    static Pre_Equipement bouclierIII_r = new Pre_Equipement("excellent bouclier", BOUCLIER, III, Effet_equip.RESISTANCE2, true);
    static Pre_Equipement main1III_ba = new Pre_Equipement("dague courte", MAIN1, III, Effet_equip.BAD_ALLONGE, true);
    static Pre_Equipement main2III_ba = new Pre_Equipement("arbuste", MAIN2, III, Effet_equip.BAD_ALLONGE, true);
    static Pre_Equipement main2III_ga = new Pre_Equipement("lance celeste", MAIN2, III, Effet_equip.BONNE_ALLONGE, true);
    static Pre_Equipement lame_mont = new Pre_Equipement("Epée des brumes", MAIN1, III, Effet_equip.MONT5, true);
    static Pre_Equipement main1III_a = new Pre_Equipement("poignard aiguisé", MAIN1, III, Effet_equip.ATTAQUE1, true);
    static Pre_Equipement bracelet_max = new Pre_Equipement("Bracelet suprême", BRACELET, III, Effet_equip.BRACELETMAX, true);


    static Pre_Equipement[] rang3 = {fleche_plusIII, fleche_plusIII2, fleche_plusIII3, ceintureIII, arcIII,
            dagueIII, epeeIII, armureIII, casqueIII, bouclierIII, sacIII, bracelet_ar3, bracelet_r3, bracelet_ra3,
            parchemin_folie, popo_max, popoIII, healIII, healPPIII, casque_III_ar, bouclierIII_ar, armureIII_r,
            casque_III_r, bouclierIII_r, main1III_ba, main1III_ba, main2III_ba, main2III_ga, lame_mont, armureIII_ar,
            main1III_a, bracelet_max};

    static Pre_Equipement nectar = new Pre_Equipement("nectar", AUTRE, IV, Effet_equip.NECTAR, false);
    static Pre_Equipement ambroisie = new Pre_Equipement("ambroisie", AUTRE, IV, Effet_equip.AMBROISIE, false);
    static Pre_Equipement arcIV = new Pre_Equipement("Arc d'Apollon", ARC, IV, AUCUN, true);
    static Pre_Equipement main1IV = new Pre_Equipement("Glaives divins", MAIN1, IV, AUCUN, true);
    static Pre_Equipement main2IV = new Pre_Equipement("Lame divine", MAIN2, IV, AUCUN, true);
    static Pre_Equipement fleche_plusIV = new Pre_Equipement("flèches divines", AUTRE, IV, Effet_equip.ARCA, true);
    static Pre_Equipement armureIV = new Pre_Equipement("Armure d'Achille", ARMURE, IV, Effet_equip.ATTAQUE7, true);
    static Pre_Equipement casqueIV = new Pre_Equipement("Casque divin", CASQUE, IV, Effet_equip.ATTAQUE8, true);
    static Pre_Equipement bouclierIV = new Pre_Equipement("Aegis", BOUCLIER, IV, AUCUN, true);
    static Pre_Equipement ceintureIV = new Pre_Equipement("Ceinture d'Heracles", CEINTURE, IV, AUCUN, true);
    static Pre_Equipement braceletIV = new Pre_Equipement("Bracelet divin", BRACELET, IV, Effet_equip.BRACELET_DIVIN, true);
    static Pre_Equipement parch_volcan = new Pre_Equipement("parchemin d'éruption volcanique", AUTRE, IV, Effet_equip.PARCH_VOLCAN, true);
    static Pre_Equipement parch_abso = new Pre_Equipement("Parchemin d'absorbtion", AUTRE, IV, Effet_equip.PARCH_ABSO, true);

    static Pre_Equipement[] rang4 = {nectar, ambroisie, fleche_plusIV, arcIV, main1IV, main2IV,
            armureIV, casqueIV, bouclierIV, ceintureIV, braceletIV, parch_volcan, parch_abso};

    static Pre_Equipement pegase = new Pre_Equipement("Pégase", MONTURE, PROMOTION, Effet_equip.PEGASE, Promo_Type.MONTURE);
    static Pre_Equipement cheval = new Pre_Equipement("Cheval", MONTURE, PROMOTION, Effet_equip.CHEVAL, Promo_Type.MONTURE);
    static Pre_Equipement molosse = new Pre_Equipement("Molosse infernal", MONTURE, PROMOTION, Effet_equip.MOLOSSE, Promo_Type.MONTURE);
    static Pre_Equipement pie = new Pre_Equipement("Pie voleuse", MONTURE, PROMOTION, Effet_equip.PIE, Promo_Type.MONTURE);
    static Pre_Equipement sphinx = new Pre_Equipement("Sphinx", MONTURE, PROMOTION, Effet_equip.SPHINX, Promo_Type.MONTURE);

    static Pre_Equipement[] prom_list_mont = {pegase, cheval, molosse, pie, sphinx};
    static public int nb_monture = prom_list_mont.length;

    static Pre_Equipement broches = new Pre_Equipement("Broche souverraine", AUTRE, PROMOTION, Effet_equip.ALTRUISME, Promo_Type.AMELIORATION);
    static Pre_Equipement tal_ar = new Pre_Equipement("Talisman d'acier", AUTRE, PROMOTION, Effet_equip.ARMURE1, Promo_Type.AMELIORATION);
    static Pre_Equipement tal_a = new Pre_Equipement("Talisman offensif", AUTRE, PROMOTION, Effet_equip.ATTAQUE2, Promo_Type.AMELIORATION);
    static Pre_Equipement tal_r = new Pre_Equipement("Talisman défensif", AUTRE, PROMOTION, Effet_equip.RESISTANCE3, Promo_Type.AMELIORATION);
    static Pre_Equipement fleche_plusP = new Pre_Equipement("Tir assisté", AUTRE, PROMOTION, Effet_equip.ARCA, Promo_Type.AMELIORATION);
    static Pre_Equipement fleche_plusP2 = new Pre_Equipement("Bénédiction magique", AUTRE, PROMOTION, Effet_equip.ARCA, Promo_Type.AMELIORATION);
    static Pre_Equipement fleche_plusP3 = new Pre_Equipement("Flèches chamaniques", AUTRE, PROMOTION, Effet_equip.ARCA, Promo_Type.AMELIORATION);
    static Pre_Equipement rune_arca = new Pre_Equipement("Rune arcanique", RUNE, PROMOTION, Effet_equip.RUNE_ARCA, Promo_Type.AMELIORATION);
    static Pre_Equipement antidote = new Pre_Equipement("Vin d'Asclépios", CONSO_MAIN, PROMOTION, Effet_equip.ANTIDODE, Promo_Type.AMELIORATION);

    static Pre_Equipement[] prom_list_boost = {broches, tal_ar, tal_a, tal_r, fleche_plusP, fleche_plusP2, fleche_plusP3,
            rune_arca, antidote};
    static public int nb_boost = prom_list_boost.length;


    static Pre_Equipement rune_anni = new Pre_Equipement("Inverteur de fision", AUTRE, PROMOTION, Effet_equip.ANNIHILITON, Promo_Type.ARTEFACT);
    static Pre_Equipement rez = new Pre_Equipement("Tatouage de Résurection", AUTRE, PROMOTION, Effet_equip.REZ, Promo_Type.ARTEFACT);
    static Pre_Equipement fuite = new Pre_Equipement("Téleporteur courte porté", AUTRE, PROMOTION, Effet_equip.FUITE, Promo_Type.ARTEFACT);
    static Pre_Equipement parch_lum = new Pre_Equipement("Parchemin de lumière", AUTRE, PROMOTION, Effet_equip.PARCH_LUMIERE, Promo_Type.ARTEFACT);
    static Pre_Equipement grenade = new Pre_Equipement("Grenades", CONSO_EX, PROMOTION, Effet_equip.GRENADE, Promo_Type.ARTEFACT);
    static Pre_Equipement popo_vitesse = new Pre_Equipement("Potion de vitesse", AUTRE, PROMOTION, Effet_equip.PROTECTION, Promo_Type.ARTEFACT);
    static Pre_Equipement popo_PP = new Pre_Equipement("Potion lente", CONSO_MAIN, PROMOTION, Effet_equip.PPL, Promo_Type.ARTEFACT);
    static Pre_Equipement barque = new Pre_Equipement("Navire magique", AUTRE, PROMOTION, Effet_equip.MER_EXP, Promo_Type.ARTEFACT);
    static Pre_Equipement barriere = new Pre_Equipement("Protection absolue", AUTRE, PROMOTION, Effet_equip.ITEM_IMMUN, Promo_Type.ARTEFACT);
    static Pre_Equipement seconde_chance = new Pre_Equipement("Sacoche temporelle", AUTRE, PROMOTION, Effet_equip.SAC_TEMP, Promo_Type.ARTEFACT);

    static Pre_Equipement[] prom_list_arte = {rune_anni, rez, fuite, parch_lum, grenade, fuite, popo_vitesse, popo_PP,
            barque, barriere, seconde_chance};
    static public int nb_arte = prom_list_arte.length;
}

import java.util.Random;

public class Pre_Equipement {
    public String nom;
    public Base base;
    public Rang rang;
    public Effet_equip effet;
    public boolean is_unique;

    Pre_Equipement(String nom, Base base, Rang rang, Effet_equip effet, boolean is_unique) {
        this.nom = nom;
        this.base = base;
        this.rang = rang;
        this.effet = effet;
        this.is_unique = is_unique;
    }

    /**
     * S'assure que les équipements uniques ne peuvent être tiré plusieurs fois
     */
    private void safe_delete(){
        if(!is_unique){
            return;
        }
        Pre_Equipement[] list;
        switch(rang){
            case O -> list = rang0;
            case I -> list = rang1;
            case II -> list = rang2;
            case III -> list = rang3;
            case IV -> list = rang4;
            case PROMOTION -> list = prom_list;
            default -> {
                System.out.println("Erreur : l'équipement " + nom + " n'a pas de liste correspondante.");
                return;
            }
        }
        for(int i = 0; i < list.length; i++){
            if(list[i].equals(this)){
                list[i] = null;
                return;
            }
        }
        System.out.println("Erreur : l'équipement " + nom + " n'existe pas dans la liste donnée.");
    }

    static Random rand = new Random();

    /**
     * Extrait et renvoie un équipement rang 0
     */
    public static Pre_Equipement drop_0(){
        System.out.println("Vous récupérez un équipement rang 0 :");
        int t;
        Pre_Equipement equip;
        do{
            t = rand.nextInt(rang0.length);
            equip = rang0[t];
        }while(equip == null);
        equip.safe_delete();
        return equip;
    }

    /**
     * Extrait et renvoie un équipement rang I
     */
    public static Pre_Equipement drop_1(){
        System.out.println("Vous récupérez un équipement rang I :");
        int t;
        Pre_Equipement equip;
        do{
            t = rand.nextInt(rang1.length);
            equip = rang1[t];
        }while(equip == null);
        equip.safe_delete();
        return equip;
    }

    /**
     * Extrait et renvoie un équipement rang II
     */
    public static Pre_Equipement drop_2(){
        System.out.println("Vous récupérez un équipement rang II :");
        int t;
        Pre_Equipement equip;
        do{
            t = rand.nextInt(rang2.length);
            equip = rang2[t];
        }while(equip == null);
        equip.safe_delete();
        return equip;
    }

    /**
     * Extrait et renvoie un équipement rang III
     */
    public static Pre_Equipement drop_3(){
        System.out.println("Vous récupérez un équipement rang III :");
        int t;
        Pre_Equipement equip;
        do{
            t = rand.nextInt(rang3.length);
            equip = rang3[t];
        }while(equip == null);
        equip.safe_delete();
        return equip;
    }

    /**
     * Extrait et renvoie un équipement rang IV
     */
    public static Pre_Equipement drop_4(){
        System.out.println("Vous récupérez un équipement rang IV :");
        int t;
        Pre_Equipement equip;
        do{
            t = rand.nextInt(rang4.length);
            equip = rang4[t];
        }while(equip == null);
        equip.safe_delete();
        return equip;
    }

    /**
     * Extrait et renvoie une promotion
     *
     * @return un pre-équipement de rang promotion
     */
    public static Pre_Equipement drop_promo(){
        System.out.println("Vous récupérez une promotion");
        int t;
        Pre_Equipement equip;
        do{
            t = rand.nextInt(prom_list.length);
            equip = prom_list[t];
        }while(equip == null);
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

    static Rang O = Rang.O;
    static Rang I = Rang.I;
    static Rang II = Rang.II;
    static Rang III = Rang.III;
    static Rang IV = Rang.IV;
    static Rang PROMOTION = Rang.PROMOTION;
    
    static Effet_equip AUCUN = Effet_equip.AUCUN;

    static Pre_Equipement defaultp = new Pre_Equipement("pas dev", AUTRE, PROMOTION, Effet_equip.PASDP, false);

    static Pre_Equipement ceinture0 = new Pre_Equipement("vieille ceinture", CEINTURE, O, AUCUN, false);
    static Pre_Equipement arc0 = new Pre_Equipement("vieil arc", ARC, O, AUCUN, false);
    static Pre_Equipement dague0 = new Pre_Equipement("vieille dague", MAIN1, O, AUCUN, false);
    static Pre_Equipement epee0 = new Pre_Equipement("vieille épée", MAIN2, O, AUCUN, false);
    static Pre_Equipement armure0 = new Pre_Equipement("vieille armure", ARMURE, O, AUCUN, false);
    static Pre_Equipement casque0 = new Pre_Equipement("vieux casque", CASQUE, O, AUCUN, false);
    static Pre_Equipement bouclier0 = new Pre_Equipement("vieux bouclier", BOUCLIER, O, AUCUN, false);
    static Pre_Equipement healPP0 = new Pre_Equipement("ration de survie", CONSO_EX, O, Effet_equip.CONSO_EXT1, false);
    static Pre_Equipement heal0 = new Pre_Equipement("fruit en conserve", CONSO_EX, O, Effet_equip.CONSO_RES2, false);
    static Pre_Equipement sac0 = new Pre_Equipement("vieux sac", SAC, O, Effet_equip.SAC0, false);

    static Pre_Equipement bracelet0 = new Pre_Equipement("bracelet de protection cabossé", BRACELET, O, Effet_equip.RESISTANCE1, true);
    static Pre_Equipement lam_enf = new Pre_Equipement("Lame infernale", MAIN1, O, Effet_equip.ENFERS4, true);
    static Pre_Equipement lam_herbe = new Pre_Equipement("Lame végétale", MAIN1, O, Effet_equip.PRAIRIE3, true);
    static Pre_Equipement fleche_plus0 = new Pre_Equipement("plumes d'oie", AUTRE, O, Effet_equip.ARCA, true);
    static Pre_Equipement rune0 = new Pre_Equipement("rune résiduelle", RUNE, O, Effet_equip.RUNE_RESIDU, true);

    static Pre_Equipement[] rang0 = {ceinture0, arc0, dague0, epee0, armure0, casque0, heal0, healPP0, bouclier0,
            bracelet0, fleche_plus0, lam_enf, lam_herbe, sac0, rune0};

    static Pre_Equipement ceintureI = new Pre_Equipement("ceinture", CEINTURE, I, AUCUN, false);
    static Pre_Equipement arcI = new Pre_Equipement("arc simple", ARC, I, AUCUN, false);
    static Pre_Equipement dagueI = new Pre_Equipement("dague classique", MAIN1, I, AUCUN, false);
    static Pre_Equipement epeeI = new Pre_Equipement("épée lourde", MAIN2, I, AUCUN, false);
    static Pre_Equipement armureI = new Pre_Equipement("armure basique", ARMURE, I, AUCUN, false);
    static Pre_Equipement casqueI = new Pre_Equipement("casque commun", CASQUE, I, AUCUN, false);
    static Pre_Equipement bouclierI = new Pre_Equipement("bouclier", BOUCLIER, I, AUCUN, false);
    static Pre_Equipement healPPI = new Pre_Equipement("aliments sain", CONSO_EX, I, Effet_equip.CONSO_EXT2, false);
    static Pre_Equipement healI = new Pre_Equipement("potion de vie", CONSO_EX, I, Effet_equip.CONSO_RES4, false);
    static Pre_Equipement sacI = new Pre_Equipement("sac à dos", SAC, I, Effet_equip.SAC1, false);

    static Pre_Equipement fleche_plusI = new Pre_Equipement("pointe en fer", AUTRE, I, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusI2 = new Pre_Equipement("hance d'acier", AUTRE, I, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusI3 = new Pre_Equipement("viseur", AUTRE, I, Effet_equip.ARCA, true);
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
    static Pre_Equipement main1_ba = new Pre_Equipement("coutelat", MAIN1, I, Effet_equip.BAD_ALLONGE1, true);
    static Pre_Equipement main2_ba = new Pre_Equipement("épée courte", MAIN2, I, Effet_equip.BAD_ALLONGE1, true);
    static Pre_Equipement armure_ar = new Pre_Equipement("armure renforcée", ARMURE, I, Effet_equip.ARMURE1, true);
    static Pre_Equipement elem_main = new Pre_Equipement("Lame des vents", MAIN2, I, Effet_equip.LAME_VENT, true);
    static Pre_Equipement elem_casque = new Pre_Equipement("Diadème de flamme", CASQUE, I, Effet_equip.COIFFE_FEU, true);
    static Pre_Equipement elem_armure = new Pre_Equipement("Armure de glace", ARMURE, I, Effet_equip.ARMURE_GLACE, true);
    static Pre_Equipement elem_bouclier = new Pre_Equipement("Bouclier des roches", BOUCLIER, I, Effet_equip.BOUCLIER_TERRE, true);
    static Pre_Equipement bracelet_r1 = new Pre_Equipement("Bracelet de protection", BRACELET, I, Effet_equip.RESISTANCE2, true);
    static Pre_Equipement bracelet_ar1 = new Pre_Equipement("Bracelet du défenseur", BRACELET, I, Effet_equip.ARMURE1, true);
    static Pre_Equipement bracelet_a1 = new Pre_Equipement("Bracelet de force", BRACELET, I, Effet_equip.ATTAQUE1, true);
    static Pre_Equipement bracelet_ra1 = new Pre_Equipement("Bracelet de rage", BRACELET, I, Effet_equip.RAGE1, true);
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

    static Pre_Equipement[] rang1 = {fleche_plusI, fleche_plusI2, fleche_plusI3, ceintureI, arcI,
            dagueI, epeeI, armureI, casqueI, bouclierI, healPPI, healI, sacI, armure_w, casque_w,
            bouclier_w, armure_a, casque_a, bouclier_a, main1_a, main1_a, main2_a, armure_r,
            casque_r, bouclier_r, main2_r, main1_ba, main1_ba, main2_ba, armure_ar, elem_main,
            elem_casque, elem_armure, elem_bouclier, bracelet_r1, bracelet_r1, bracelet_ar1,
            bracelet_a1, bracelet_a1, bracelet_ra1, epee_dodo, parchemin_feu, parch_force, rune_croiss,
            rune_feu, rune_feu, rune_pluie, rune_haine, rune_virale, rune_orage, rune_mort, rune_dodo, popoI};

    static Pre_Equipement ceintureII = new Pre_Equipement("ceinture", CEINTURE, II, AUCUN, false);
    static Pre_Equipement arcII = new Pre_Equipement("arc simple", ARC, II, AUCUN, false);
    static Pre_Equipement dagueII = new Pre_Equipement("dague classique", MAIN1, II, AUCUN, false);
    static Pre_Equipement epeeII = new Pre_Equipement("épée lourde", MAIN2, II, AUCUN, false);
    static Pre_Equipement armureII = new Pre_Equipement("armure basique", ARMURE, II, AUCUN, false);
    static Pre_Equipement casqueII = new Pre_Equipement("casque commun", CASQUE, II, AUCUN, false);
    static Pre_Equipement bouclierII = new Pre_Equipement("bouclier", BOUCLIER, II, AUCUN, false);
    static Pre_Equipement sacII = new Pre_Equipement("sac de voyage", SAC, II, Effet_equip.SAC2, false);

    static Pre_Equipement fleche_plusII = new Pre_Equipement("poison à flèche", AUTRE, II, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusII2 = new Pre_Equipement("encoche renforcée", AUTRE, II, Effet_equip.ARCA, true);


    static Pre_Equipement[] rang2 = {fleche_plusII, fleche_plusII2, ceintureII, arcII,
            dagueII, epeeII, armureII, casqueII, bouclierII, sacII};

    static Pre_Equipement ceintureIII = new Pre_Equipement("ceinture", CEINTURE, III, AUCUN, false);
    static Pre_Equipement arcIII = new Pre_Equipement("arc simple", ARC, III, AUCUN, false);
    static Pre_Equipement dagueIII = new Pre_Equipement("dague classique", MAIN1, III, AUCUN, false);
    static Pre_Equipement epeeIII = new Pre_Equipement("épée lourde", MAIN2, III, AUCUN, false);
    static Pre_Equipement armureIII = new Pre_Equipement("armure basique", ARMURE, III, AUCUN, false);
    static Pre_Equipement casqueIII = new Pre_Equipement("casque commun", CASQUE, III, AUCUN, false);
    static Pre_Equipement bouclierIII = new Pre_Equipement("bouclier", BOUCLIER, III, AUCUN, false);

    static Pre_Equipement fleche_plusIII = new Pre_Equipement("pointe torsadée", AUTRE, III, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusIII2 = new Pre_Equipement("structure enchantée", AUTRE, III, Effet_equip.ARCA, true);

    static Pre_Equipement[] rang3 = {fleche_plusIII, fleche_plusIII2, ceintureIII, arcIII,
            dagueIII, epeeIII, armureIII, casqueIII, bouclierIII};


    static Pre_Equipement nectar = new Pre_Equipement("nectar", AUTRE, IV, Effet_equip.NECTAR, true);
    static Pre_Equipement ambroisie = new Pre_Equipement("ambroisie", AUTRE, IV, Effet_equip.AMBROISIE, true);
    static Pre_Equipement fleche_plusIV = new Pre_Equipement("flèches divines", AUTRE, IV, Effet_equip.ARCA, true);

    static Pre_Equipement[] rang4 = {nectar, ambroisie, fleche_plusIV};

    static Pre_Equipement[] prom_list = {defaultp};
}

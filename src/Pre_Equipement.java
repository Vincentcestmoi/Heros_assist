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

    static Rang O = Rang.O;
    static Rang I = Rang.I;
    static Rang II = Rang.II;
    static Rang III = Rang.III;
    static Rang IV = Rang.IV;
    static Rang PROMOTION = Rang.PROMOTION;
    
    static Effet_equip AUCUN = Effet_equip.AUCUN;

    //static Pre_Equipement default0 = new Pre_Equipement("pas dev", AUTRE, O, Effet_equip.PASD0, false);
    static Pre_Equipement default1 = new Pre_Equipement("pas dev", AUTRE, I, Effet_equip.PASD1, false);
    static Pre_Equipement default2 = new Pre_Equipement("pas dev", AUTRE, II, Effet_equip.PASD2, false);
    static Pre_Equipement default3 = new Pre_Equipement("pas dev", AUTRE, III, Effet_equip.PASD3, false);
    static Pre_Equipement default4 = new Pre_Equipement("pas dev", AUTRE, IV, Effet_equip.PASD4, false);
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

    static Pre_Equipement bracelet0 = new Pre_Equipement("bracelet de protection cabossé", BRACELET, O, Effet_equip.RESISTANCE1, true);
    static Pre_Equipement lam_enf = new Pre_Equipement("Lame infernale", MAIN1, O, Effet_equip.ENFERS4, true);
    static Pre_Equipement lam_herbe = new Pre_Equipement("Lame végétale", MAIN1, O, Effet_equip.PRAIRIE3, true);
    static Pre_Equipement fleche_plus0 = new Pre_Equipement("plumes d'oie", AUTRE, O, Effet_equip.ARCA, true);

    static Pre_Equipement[] rang0 = {ceinture0, arc0, dague0, epee0, armure0, casque0, heal0, healPP0, bouclier0, bracelet0, fleche_plus0, lam_enf, lam_herbe};

    static Pre_Equipement ceintureI = new Pre_Equipement("ceinture", CEINTURE, I, AUCUN, false);
    static Pre_Equipement arcI = new Pre_Equipement("arc simple", ARC, I, AUCUN, false);
    static Pre_Equipement dagueI = new Pre_Equipement("dague classique", MAIN1, I, AUCUN, false);
    static Pre_Equipement epeeI = new Pre_Equipement("épée lourde", MAIN2, I, AUCUN, false);
    static Pre_Equipement armureI = new Pre_Equipement("armure basique", ARMURE, I, AUCUN, false);
    static Pre_Equipement casqueI = new Pre_Equipement("casque commun", CASQUE, I, AUCUN, false);
    static Pre_Equipement bouclierI = new Pre_Equipement("bouclier", BOUCLIER, I, AUCUN, false);
    static Pre_Equipement healPPI = new Pre_Equipement("aliments sain", CONSO_EX, I, Effet_equip.CONSO_EXT2, false);
    static Pre_Equipement healI = new Pre_Equipement("potion de vie", CONSO_EX, I, Effet_equip.CONSO_RES4, false);

    static Pre_Equipement fleche_plusI = new Pre_Equipement("pointe en fer", AUTRE, I, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusI2 = new Pre_Equipement("hance d'acier", AUTRE, I, Effet_equip.ARCA, true);
    static Pre_Equipement fleche_plusI3 = new Pre_Equipement("viseur", AUTRE, I, Effet_equip.ARCA, true);


    static Pre_Equipement[] rang1 = {fleche_plusI, fleche_plusI2, fleche_plusI3, ceintureI, arcI, dagueI, epeeI, armureI, casqueI, bouclierI, healPPI, healI};
    static Pre_Equipement[] rang2 = {default2};
    static Pre_Equipement[] rang3 = {default3};
    static Pre_Equipement[] rang4 = {default4};
    static Pre_Equipement[] prom_list = {defaultp};
}

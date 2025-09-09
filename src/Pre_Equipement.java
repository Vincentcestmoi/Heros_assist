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
            default -> list = null;
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
    static Pre_Equipement bracelet0 = new Pre_Equipement("bracelet de protection cabossé", BRACELET, O, Effet_equip.RESISTANCE1, true);
    static Pre_Equipement lam_enf = new Pre_Equipement("Lame infernale", MAIN1, O, Effet_equip.ENFERS4, true);
    static Pre_Equipement lam_herbe = new Pre_Equipement("Lame végétale", MAIN1, O, Effet_equip.PRAIRIE3, true);

    static Pre_Equipement[] rang0 = {ceinture0, arc0, dague0, epee0, armure0, casque0, bouclier0, bracelet0, lam_enf, lam_herbe};
    static Pre_Equipement[] rang1 = {default1};
    static Pre_Equipement[] rang2 = {default2};
    static Pre_Equipement[] rang3 = {default3};
    static Pre_Equipement[] rang4 = {default4};
    static Pre_Equipement[] prom_list = {defaultp};
}

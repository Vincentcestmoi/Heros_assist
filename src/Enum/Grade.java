package Enum;

public enum Grade { //permet de retourner à des positions des passées et d'avoir des réductions aux marchés
    AUCUN,
    FER,    //enfers
    ACIER,  //prairie
    ARGENT, //vigne
    OR,     //temple
    DIAMANT,//mer
    MITHRIL,//mont
    DRAGON; //olympe ?
    /**
     * Le grade de départ des joueurs
     */
    public static final Grade DEFAULT = AUCUN;
}

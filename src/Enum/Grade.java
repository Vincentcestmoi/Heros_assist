package Enum;

public enum Grade { //permet de retourner à des positions des passées, d'avoir des réductions aux marchés, des services améliorés, cetaines quêtes, etc.
    AUCUN,
    FER,
    ACIER,
    ARGENT,
    OR,
    DIAMANT,
    MITHRIL,
    ADAMANT,
    PHOENIX,
    DRAGON,
    DIVIN;
    /**
     * Le grade de départ des joueurs
     */
    public static final Grade DEFAULT = AUCUN;
    /**
     * Le meilleur grade accéssible
     */
    public static final Grade MAX = DIVIN;
}

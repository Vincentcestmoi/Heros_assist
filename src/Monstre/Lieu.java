package Monstre;

import Enum.Position;

import java.util.Random;

public class Lieu {
    
    static Random random = new Random();
    static int proba_inf = 5; // 5%
    static int proba_sup = 95; // 4%
    
    /**
     * Recherche un monstre des enfers
     * légère probabilité qu'il vienne de la pririe
     * @return un monstre
     */
    public static Monstre enfers() {
        if (random.nextInt(100) > proba_sup) {
            return true_prairie();
        } else {
            return true_enfers();
        }
    }
    
    /**
     * Recherche un monstre dans la prairie
     * légère probabilité qu'il vienne des enfers ou de vignes
     * @return un monstre
     */
    public static Monstre prairie() {
        int tirage = random.nextInt(100);
        if (tirage < proba_inf) {
            return true_enfers();
        } else if (tirage > proba_sup) {
            return true_vigne();
        } else {
            return true_prairie();
        }
    }
    
    /**
     * Recherche un monstre dans les vignes
     * légère probabilité qu'il vienne de la prairie ou du temple
     * @return un monstre
     */
    public static Monstre vigne() {
        int tirage = random.nextInt(100);
        if (tirage < proba_inf) {
            return true_prairie();
        } else if (tirage > proba_sup) {
            return true_temple();
        } else {
            return true_vigne();
        }
    }
    
    /**
     * Recherche un monstre dans le temple
     * légère probabilité qu'il vienne de la vigne ou de la mer
     * @return un monstre
     */
    public static Monstre temple() {
        int tirage = random.nextInt(100);
        if (tirage < proba_inf) {
            return true_vigne();
        } else if (tirage > proba_sup) {
            return true_mer();
        } else {
            return true_temple();
        }
    }
    
    /**
     * Recherche un monstre dans la mer
     * légère probabilité qu'il vienne des monts ou du temple
     * @return un monstre
     */
    public static Monstre mer() {
        int tirage = random.nextInt(100);
        if (tirage < proba_inf) {
            return true_temple();
        } else if (tirage > proba_sup) {
            return true_mont();
        } else {
            return true_mer();
        }
    }
    
    /**
     * Recherche un monstre dans les monts
     * légère probabilité qu'il vienne de la mer
     * @return un monstre
     */
    public static Monstre mont() {
        if (random.nextInt(100) < proba_inf) {
            return true_mer();
        } else {
            return true_mont();
        }
    }
    
    /**
     * Prélève un monstre dans les enfers
     * @return un monstre
     */
    public static Monstre true_enfers() {
        Race race;
        do {
            race = Race.enfers[random.nextInt(Race.enfers.length)];
        } while (race == null);
        return new Monstre(race);
    }
    
    /**
     * Prélève un monstre dans la prairie
     * @return un monstre
     */
    public static Monstre true_prairie() {
        Race race;
        do {
            race = Race.prairie[random.nextInt(Race.prairie.length)];
        } while (race == null);
        return new Monstre(race);
    }
    
    /**
     * Prélève un monstre dans la vigne
     * @return un monstre
     */
    public static Monstre true_vigne() {
        Race race;
        do {
            race = Race.vigne[random.nextInt(Race.vigne.length)];
        } while (race == null);
        return new Monstre(race);
    }
    
    /**
     * Prélève un monstre dans le temple
     * @return un monstre
     */
    public static Monstre true_temple() {
        Race race;
        do {
            race = Race.temple[random.nextInt(Race.temple.length)];
        } while (race == null);
        return new Monstre(race);
    }
    
    /**
     * Prélève un monstre dans la mer
     * @return un monstre
     */
    public static Monstre true_mer() {
        Race race;
        do {
            race = Race.mer[random.nextInt(Race.mer.length)];
        } while (race == null);
        return new Monstre(race);
    }
    
    /**
     * Prélève un monstre dans les monts
     * @return un monstre
     */
    public static Monstre true_mont() {
        Race race;
        do {
            race = Race.mont[random.nextInt(Race.mont.length)];
        } while (race == null);
        return new Monstre(race);
    }
    
    /**
     * Prélève un monstre dans l'Olympe
     * @return un monstre
     */
    public static Monstre olympe() {
        Race race;
        do {
            race = Race.olympe[random.nextInt(Race.olympe.length)];
        } while (race == null);
        return new Monstre(race);
    }
    
    public static Monstre get_dummy() {
        return new Monstre(Race.pantin);
    }
    
    /**
     * Renvoie un monstre de la position demandé en supprimant les chances qu'il vienne d'une position voisine
     * @param pos la position dont on veut le monstre
     * @return un Monstre
     */
    public static Monstre true_monstre(Position pos) {
        return switch (pos) {
            case ENFERS -> true_enfers();
            case PRAIRIE -> true_prairie();
            case VIGNES -> true_vigne();
            case TEMPLE -> true_temple();
            case MER -> true_mer();
            case MONTS -> true_mont();
            case OLYMPE -> olympe();
            case ASCENDANT -> {
                System.out.println("ERROR : DONOT");
                yield true_enfers();
            }
        };
    }
    
    /**
     * Renvoie un monstre de la position demandé en supprimant les chances qu'il vienne d'une position voisine
     * @param pos la position dont on veut le monstre
     * @return un Monstre
     */
    public static Monstre true_monstre(Position pos, boolean next_pos) {
        if (!next_pos) {
            return true_monstre(pos);
        }
        return switch (pos) {
            case ENFERS -> true_prairie();
            case PRAIRIE -> true_vigne();
            case VIGNES -> true_temple();
            case TEMPLE -> true_mer();
            case MER -> true_mont();
            case MONTS, OLYMPE -> olympe();
            case ASCENDANT -> {
                System.out.println("ERROR : DONOT");
                yield true_enfers();
            }
        };
    }
}
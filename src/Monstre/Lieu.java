package Monstre;

import Auxiliaire.Utilitaire;
import Enum.Position;

import java.util.Random;

/**
 * Une classe dédiée uniquement à tirer aléatoirement des monstres
 */
public class Lieu {
    
    static Random random = new Random();
    static int proba_inf = 5; // 5%
    static int proba_sup = 95; // 4%
    
    /**
     * Recherche un monstre des enfers
     * légère probabilité qu'il vienne de la prairie
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
        return get_monstre(Race.enfers);
    }
    
    /**
     * Prélève un monstre dans la prairie
     * @return un monstre
     */
    public static Monstre true_prairie() {
        return get_monstre(Race.prairie);
    }
    
    /**
     * Prélève un monstre dans la vigne
     * @return un monstre
     */
    public static Monstre true_vigne() {
        return get_monstre(Race.vigne);
    }
    
    /**
     * Prélève un monstre dans le temple
     * @return un monstre
     */
    public static Monstre true_temple() {
        return get_monstre(Race.temple);
    }
    
    /**
     * Prélève un monstre dans la mer
     * @return un monstre
     */
    public static Monstre true_mer() {
        return get_monstre(Race.mer);
    }
    
    /**
     * Prélève un monstre dans les monts
     * @return un monstre
     */
    public static Monstre true_mont() {
        return get_monstre(Race.mont);
    }
    
    /**
     * Prélève un monstre dans l'Olympe
     * @return un monstre
     */
    public static Monstre olympe() {
        return get_monstre(Race.olympe);
    }
    
    /**
     * Renvoie un monstre au hasard parmi ceux demandés
     * @param list la liste des monstres potentiels
     * @return un monstre parmi la liste
     */
    public static Monstre get_monstre(Race[] list){
        int total = 0;
        for(Race r : list){
            total += r.get_proba();
        }
        int t = random.nextInt(total);
        Utilitaire.LoopGuard garde = new Utilitaire.LoopGuard();
        for (Race race : list) {
            t -= race.get_proba();
            if (t <= 0) {
                return new Monstre(race);
            }
            garde.check();
        }
        throw new IllegalStateException("Dépassement de la donnée");
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
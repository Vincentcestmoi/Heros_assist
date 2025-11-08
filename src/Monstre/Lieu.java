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
    static int proba_sup = 96; // 4%
    
    /**
     * Recherche un monstre des enfers
     * légère probabilité qu'il vienne de la prairie
     * @return un monstre
     */
    public static Monstre enfers() {
        if (random.nextInt(100) >= proba_sup) {
            return true_prairie(false);
        } else {
            return true_enfers(true);
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
            return true_enfers(false);
        } else if (tirage >= proba_sup) {
            return true_vigne(false);
        } else {
            return true_prairie(false);
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
            return true_prairie(false);
        } else if (tirage >= proba_sup) {
            return true_temple(false);
        } else {
            return true_vigne(true);
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
            return true_vigne(false);
        } else if (tirage >= proba_sup) {
            return true_mer(false);
        } else {
            return true_temple(true);
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
            return true_temple(false);
        } else if (tirage >= proba_sup) {
            return true_mont(false);
        } else {
            return true_mer(true);
        }
    }
    
    /**
     * Recherche un monstre dans les monts
     * légère probabilité qu'il vienne de la mer
     * @return un monstre
     */
    public static Monstre mont() {
        if (random.nextInt(100) < proba_inf) {
            return true_mer(false);
        } else {
            return true_mont(true);
        }
    }
    
    /**
     * Prélève un monstre dans les enfers
     * @return un monstre
     */
    public static Monstre true_enfers(boolean nomme_acceptes) {
        return get_monstre(Race.enfers, nomme_acceptes);
    }
    
    /**
     * Prélève un monstre dans la prairie
     * @return un monstre
     */
    public static Monstre true_prairie(boolean nomme_acceptes) {
        return get_monstre(Race.prairie, nomme_acceptes);
    }
    
    /**
     * Prélève un monstre dans la vigne
     * @return un monstre
     */
    public static Monstre true_vigne(boolean nomme_acceptes) {
        return get_monstre(Race.vigne, nomme_acceptes);
    }
    
    /**
     * Prélève un monstre dans le temple
     * @return un monstre
     */
    public static Monstre true_temple(boolean nomme_acceptes) {
        return get_monstre(Race.temple, nomme_acceptes);
    }
    
    /**
     * Prélève un monstre dans la mer
     * @return un monstre
     */
    public static Monstre true_mer(boolean nomme_acceptes) {
        return get_monstre(Race.mer, nomme_acceptes);
    }
    
    /**
     * Prélève un monstre dans les monts
     * @return un monstre
     */
    public static Monstre true_mont(boolean nomme_acceptes) {
        return get_monstre(Race.mont, nomme_acceptes);
    }
    
    /**
     * Prélève un monstre dans l'Olympe
     * @return un monstre
     */
    public static Monstre olympe(boolean nomme_acceptes) {
        return get_monstre(Race.olympe, nomme_acceptes);
    }
    
    /**
     * Renvoie un monstre au hasard parmi ceux demandés
     * @param list la liste des monstres potentiels
     * @return un monstre parmi la liste
     */
    public static Monstre get_monstre(Race[] list, boolean nomme_acceptes) {
        int total = 0;
        for (Race r : list) {
            if(nomme_acceptes || !r.est_nomme())
            {
                total += r.get_proba();
            }
        }
        int t = random.nextInt(total);
        Utilitaire.LoopGuard garde = new Utilitaire.LoopGuard();
        for (Race race : list) {
            if(race.est_nomme() && !nomme_acceptes){
                continue;
            }
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
    public static Monstre true_monstre(Position pos, boolean nomme_acceptes) {
        return switch (pos) {
            case ENFERS -> true_enfers(nomme_acceptes);
            case PRAIRIE -> true_prairie(nomme_acceptes);
            case VIGNES -> true_vigne(nomme_acceptes);
            case TEMPLE -> true_temple(nomme_acceptes);
            case MER -> true_mer(nomme_acceptes);
            case MONTS -> true_mont(nomme_acceptes);
            case OLYMPE -> olympe(nomme_acceptes);
            case ASCENDANT -> {
                System.out.println("ERROR : DONOT");
                yield true_enfers(nomme_acceptes);
            }
        };
    }
    
    /**
     * Renvoie un monstre de la position demandé en supprimant les chances qu'il vienne d'une position voisine
     * @param pos la position dont on veut le monstre
     * @return un Monstre
     */
    public static Monstre true_monstre(Position pos, boolean next_pos, boolean nomme_acceptes) {
        if (!next_pos) {
            return true_monstre(pos, nomme_acceptes);
        }
        return switch (pos) {
            case ENFERS -> true_prairie(nomme_acceptes);
            case PRAIRIE -> true_vigne(nomme_acceptes);
            case VIGNES -> true_temple(nomme_acceptes);
            case TEMPLE -> true_mer(nomme_acceptes);
            case MER -> true_mont(nomme_acceptes);
            case MONTS, OLYMPE -> olympe(nomme_acceptes);
            case ASCENDANT -> {
                System.out.println("ERROR : DONOT");
                yield true_enfers(nomme_acceptes);
            }
        };
    }
}
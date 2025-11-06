package Auxiliaire;

import Exterieur.SaveManager;

import java.io.IOException;

public class Utilitaire {
    
    /**
     * Une classe destinée à servir de fusible dans une boucle, renvoyant une erreur si trop d'itération ont lieu
     * Set aussi à déclencher les autosave dans la boucle principale.
     */
    public static class LoopGuard {
        private final long maxIterations;
        private long counter = 0;
        
        // pour checkMain
        private long lastSaveTime = System.currentTimeMillis();
        private final long saveIntervalMillis;
        
        /**
         * Définie une LoopGuard avec un nombre maximum d'itérations de 10 000
         */
        public LoopGuard() {
            this.maxIterations = 10_000;
            this.saveIntervalMillis = 0L;
        }
        
        /**
         * Définie une LoopGuard pour la boucle principale
         * @param maxIterations la limite du nombre d'itérations avant la fermeture sécuritaire du programme
         * @param saveIntervalMillis le délai minimum entre deux autosave
         */
        public LoopGuard(long maxIterations, long saveIntervalMillis) {
            this.maxIterations = maxIterations;
            this.saveIntervalMillis = saveIntervalMillis;
        }
        
        /**
         * S'assure que le nombre d'itérations effectué ne dépasse pas un certain seuil
         */
        public void check() {
            counter++;
            if (counter > maxIterations) {
                throw new MaxLoopExceededException("Boucle infinie suspectée : plus de " + maxIterations + " " +
                        "itérations.");
            }
        }
        
        /**
         * Vérifie la boucle principale et déclenche une autosave si assez de temps s'est écoulé
         */
        public void checkMain() throws IOException {
            
            long now = System.currentTimeMillis();
            if (now - lastSaveTime >= saveIntervalMillis) {
                SaveManager.sauvegarder(true);
                lastSaveTime = now;
            }
            
            check();
        }
    }
    
    
    public static class MaxLoopExceededException extends RuntimeException {
        /**
         * Pour throw quand une boucle dépasse un certain nombre d'itérations
         */
        public MaxLoopExceededException(String message) {
            super(message);
        }
    }
    
}

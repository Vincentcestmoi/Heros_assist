package Auxiliaire;

public class Utilitaire {
    
    /**
     * Une classe destinée à servir de fusible dans une boucle, renvoyant une erreur si trop d'itération ont lieu
     */
    public static class LoopGuard {
        private final int maxIterations;
        private int counter = 0;
        
        /**
         * Définie une LoopGuard
         * @param maxIterations le nombre maximum d'itérations toléré
         */
        public LoopGuard(int maxIterations) {
            this.maxIterations = maxIterations;
        }
        
        /**
         * Définie une LoopGuard avec un nombre maximum d'itérations de 10 000
         */
        public LoopGuard() {
            this.maxIterations = 10_000;
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

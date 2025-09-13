import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import static java.lang.Math.max;

public class Combat {


    static Input input = new Input();
    static Random rand = new Random();

    /**
     * Lance un combat entre les joueur et un monstres
     *
     * @param nb_joueurs   le nombre de joueurs dans la partie
     * @param position le lieu où a lieu l'affrontement
     * @param joueur_force l'indice du joueur qui est attaqué en cas d'embuscade (de 1 à 4) ou -1 sinon
     * @param ob_a l'obéissance du familier de Joueur_A (de 1 à 3) ou 0 s'il n'existe pas
     * @param ob_b l'obéissance du familier de Joueur_B (de 1 à 3) ou 0 s'il n'existe pas
     * @param ob_c l'obéissance du familier de Joueur_C (de 1 à 3) ou 0 s'il n'existe pas
     * @param ob_d l'obéissance du familier de joueur_D (de 1 à 3) ou 0 s'il n'existe pas
     * @param ennemi le monstre que les joueurs affronte
     * @return un int lié au joueur qui obtient un familier
     * @throws IOException lecture de terminal
     */
    public static int affrontement(int nb_joueurs, Position position, int joueur_force, int ob_a, int ob_b, int ob_c, int ob_d, Monstre ennemi) throws IOException {

        // repérer les participants
        boolean j_a = joueur_force == 0 || (Main.positions[0] == position && input.yn("Est-ce que " + Main.Joueur_A + " participe au combat ?"));
        boolean j_b = joueur_force == 1 || (nb_joueurs > 1 && Main.positions[1] == position && input.yn("Est-ce que " + Main.Joueur_B + " participe au combat ?"));
        boolean j_c = joueur_force == 2 || (nb_joueurs > 2 && Main.positions[2] == position && input.yn("Est-ce que " + Main.Joueur_C + " participe au combat ?"));
        boolean j_d = joueur_force == 3 || (nb_joueurs > 3 && Main.positions[3] == position && input.yn("Est-ce que " + Main.Joueur_D + " participe au combat ?"));
        boolean f_a = ob_a > 0 && j_a && input.yn("Est-ce que le familier de " + Main.Joueur_A + " participe au combat ?");
        boolean f_b = ob_b > 0 && j_b && input.yn("Est-ce que le familier de " + Main.Joueur_B + " participe au combat ?");
        boolean f_c = ob_c > 0 && j_c && input.yn("Est-ce que le familier de " + Main.Joueur_C + " participe au combat ?");
        boolean f_d = ob_d > 0 && j_d && input.yn("Est-ce que le familier de " + Main.Joueur_D + " participe au combat ?");

        if (!(j_a || j_b || j_c || j_d)){
            System.out.println("Erreur : aucun joueur détecté, annulation du combat.");
            return 0;
        }

        // stockage des participants
        boolean[] actif = {j_a, j_b, j_c, j_d, f_a, f_b, f_c, f_d};
        boolean[] assomme = {false, false, false, false, false, false, false, false};
        boolean[] mort = {false, false, false, false, false, false, false, false};
        int[] reveil = {0, 0, 0, 0, 0, 0, 0, 0};
        String[] nom = {Main.Joueur_A, Main.Joueur_B, Main.Joueur_C, Main.Joueur_D,
                "le familier de " + Main.Joueur_A, "le familier de " + Main.Joueur_B, "le familier de " + Main.Joueur_C,
                "le familier de " + Main.Joueur_D};

        int nbp = 0;
        for (boolean b : actif) {
            if (b) {
                nbp += 1;
            }
        }


        // on prépare les tours en aléatoire
        int[] tirage = {-1, -1, -1, -1, -1, -1, -1, -1};
        for(int i = 0; i < 8;){
            int temp = rand.nextInt(8);
            if(tirage[temp] == -1){
                tirage[temp] = i;
                i++;
            }
        }

        // choisir le joueur en première ligne
        int pr_l = joueur_force;
        if(joueur_force != -1){
            System.out.println("Le joueur " + new char[]{'A', 'B', 'C', 'D'}[joueur_force] + " est en première ligne.\n");
        }
        else if (nbp > 1){ // demander gentimment
            for (int i = 0; i < 8; i++) {
                if (actif[i]) {
                    if (input.yn("Est-ce que " + nom[i] + " passe au front ?")){
                        pr_l = i;
                        break;
                    }
                }
            }
        }
        if(pr_l == -1){ // z'avez plus le choix
            for(int i = 0; i < 8; i++){
                if (actif[i]){
                    pr_l = i;
                    System.out.println(nom[i] + " se retrouve en première ligne.\n");
                    break;
                }
            }
        }

        if(competence(ennemi, nom, pr_l, actif)){
            gestion_mort_end(mort, nom);
            return 0;
        }

        // si l'ennemi à l'avantage de la surprise
        if (joueur_force != -1){
            ennemi.attaque(nom[pr_l]);
        }

        // combat
        int ob;
        String n;
        Action act;
        boolean run = !ennemi.est_mort();
        boolean a_pass = false;
        while(run){
            for(int j = 0; j < 8; j++){
                int i = tirage[j]; //on "corrige" la sélection avec notre ordre
                if(actif[i]) { // on ne joue que les participants actifs
                    n = nom[i]; // on stocke le nom pour plus tard
                    if(assomme[i]){
                        System.out.println(n + " est inconscient.");
                        if(Objects.equals(n, Main.Joueur_B)){
                            if(input.yn("Utiliser purge (3PP) ?")){
                                System.out.println(n + " se réveille.\n");
                                assomme[i] = false;
                                reveil[i] = 0;
                                continue;
                            }
                        }
                        if(input.D4() + reveil[i] >= 3){
                            System.out.println(n + " se réveille.\n");
                            assomme[i] = false;
                            reveil[i] = 0;
                        }
                        else{
                            System.out.println(n + " est toujours inconscient.\n");
                            reveil[i] += 1;
                            if(Objects.equals(n, Main.Joueur_B)){
                                System.out.println(n + " recupère 1PP.\n");
                                if(rand.nextBoolean()){
                                    reveil[i] -= 1;
                                }
                            }
                        }
                        if(Objects.equals(n, Main.Joueur_A) && a_pass){
                            a_pass = false;
                        }
                        continue;
                    }
                    if(Objects.equals(n, Main.Joueur_A) && a_pass){
                        a_pass = false;
                        continue;
                    }
                    if(n.contains("familier")){
                        if(n.contains(Main.Joueur_A)){
                            ob = ob_a;
                        }
                        else if(n.contains(Main.Joueur_B)){
                            ob = ob_b;
                        }
                        else if(n.contains(Main.Joueur_C)){
                            ob = ob_c;
                        }
                        else if(n.contains(Main.Joueur_D)){
                            ob = ob_d;
                        }
                        else{
                            System.out.println("Erreur : " + n + " détecté comme familier, mais aucun joueur rattaché" +
                                    "entité ignorée pour la suite du combat.");
                            ob = -1;
                            actif[i] = false;
                        }
                    }
                    else{
                        ob = 0; // joueur
                    }
                    if(ob != -1) {
                        // action
                        act = input.action(n, ob != 0, i == pr_l);
                        if (ob == 0) { //joueur
                            switch (act) {
                                case TIRER -> ennemi.tir(input.atk());
                                case MAGIE -> ennemi.dommage_magique(input.magie());
                                case FUIR -> {
                                    if (i != pr_l || input.D6() > 3) {
                                        actif[i] = false;
                                        System.out.println(n + " a fuit le combat.\n");
                                    } else {
                                        System.out.println(n + " n'est pas parvenu à distancer " + ennemi.nom + "\n");
                                    }
                                }
                                case ASSOMER -> ennemi.assommer();
                                case ENCAISSER -> ennemi.encaisser();
                                case SOIGNER -> {
                                    boolean temp = i == pr_l ||
                                            input.ask_heal(nom, actif, pr_l);
                                    ennemi.soigner(temp);
                                }
                                case DOMESTIQUER -> {
                                    if (ennemi.domestiquer()) {
                                        System.out.println("nouveau familier : " + ennemi.nom);
                                        System.out.println("attaque : " + ennemi.attaque);
                                        System.out.println("vie : " + ennemi.vie_max);
                                        System.out.println("armure : " + ennemi.armure + "\n");

                                        gestion_mort_end(mort, nom);
                                        return switch (n) {
                                            case Main.Joueur_A -> 1;
                                            case Main.Joueur_B -> 2;
                                            case Main.Joueur_C -> 3;
                                            case Main.Joueur_D -> 4;
                                            default -> 0;
                                        };
                                    }
                                }
                                case ANALYSER -> analyser(i == pr_l, ennemi);
                                case AUTRE -> System.out.println("Vous faites quelques chose.\n");
                                case ETRE_MORT -> a_pass = act_mort(actif, assomme, mort, nom, n, a_pass, i);
                                case AVANCER -> {
                                    System.out.println(n + " passe en première ligne.\n");
                                    pr_l = i;
                                    ennemi.reset_encaisser();
                                    competence_avance(ennemi, nom[pr_l]);
                                }
                                case MAUDIR -> maudir(ennemi);
                                case ONDE_CHOC -> onde_choc(actif, nom, assomme, ennemi);
                                case END -> {
                                    gestion_mort_end(mort, nom);
                                    return 0;
                                }
                                case POTION_REZ -> {
                                    int temp = input.ask_rez(nom, mort);
                                    if(temp != -1 && popo_rez(n, nom[temp])){
                                        mort[temp] = false;
                                        actif[temp] = true;
                                    }
                                    if(i == pr_l){ //premiere ligne
                                        System.out.println(n + "s'expose pour donner sa potion.");
                                        ennemi.part_soin += 0.4F;
                                    }
                                }
                                default -> ennemi.dommage(input.atk()); // ATTAQUER
                            }
                        } else { //familier
                            if (act == Action.ETRE_MORT) {
                                a_pass = act_mort(actif, assomme, mort, nom, n, a_pass, i);
                            }
                            else if(act == Action.END) {
                                gestion_mort_end(mort, nom);
                                return 0;
                            }
                            else {
                                switch (ob + input.D6() - 1) {
                                    case 1 -> {
                                        System.out.println(n + " a fuit le combat.\n");
                                        actif[i] = false;
                                    }
                                    case 2 -> System.out.println(n + " n'écoute pas ses ordres.\n");
                                    case 3, 4, 5 -> {
                                        System.out.println(n + " attaque l'ennemi.\n");
                                        ennemi.dommage(input.atk());
                                    }
                                    default -> { // >= 6
                                        switch (act) { //action choisie
                                            case ATTAQUER -> ennemi.dommage(input.atk());
                                            case FUIR -> {
                                                if (i != pr_l || input.D6() > 3) {
                                                    actif[i] = false;
                                                    System.out.println(n + " a fuit le combat.\n");
                                                } else {
                                                    System.out.println(n + "n'est pas parvenu à distancer " + ennemi.nom + "\n");
                                                }
                                            }
                                            case AUTRE -> System.out.println(n + " fait quelque chose.\n");
                                            case AVANCER -> {
                                                System.out.println("Le " + n + " passe en première ligne.\n");
                                                pr_l = i;
                                                ennemi.reset_encaisser();
                                                competence_avance(ennemi, nom[pr_l]);
                                            }
                                            default -> {
                                                System.out.println(n + " attaque l'ennemi.\n");
                                                ennemi.dommage(input.atk());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(!actif[pr_l]){ // il faut toujours une unité en première ligne
                        for(int k = 0; k < 8; k++){
                            if(actif[k]){
                                pr_l = k;
                                System.out.println(nom[k] + " se retrouve en première ligne.\n");
                                break;
                            }
                        }
                        if(!actif[pr_l]) { // la correction n'a pas eu lieu
                            run = false;
                            System.out.println("Aucun joueur ou familier détecté en combat.");
                            // break inutile car if(actif[i]) toujours à false
                        }
                    }
                    if(ennemi.est_mort()) {
                        // la mort est donné par les méthodes de dommage
                        run = false;
                        gestion_nomme(ennemi);
                        boolean actif_a = false;
                        for(int k = 0; k < 8; k++){
                            if (actif[k] && Objects.equals(nom[k], Main.Joueur_A)) {
                                actif_a = true;
                                break;
                            }
                        }
                        if(actif_a && input.yn("Voulez vous tenter de ressuciter " + ennemi.nom + " en tant que familier pour 2PP ?")) {
                            if (ressuciter(ennemi)) {
                                gestion_mort_end(mort, nom);
                                return 1;
                            }
                        }
                        break;
                    }
                }
            }
            if(run) {
                ennemi.attaque(nom[pr_l]);
                if(ennemi.est_mort()){
                    run = false;
                }
            }
        }
        System.out.println("Fin du combat\n");
        gestion_mort_end(mort, nom);
        return 0;
    }

    /**
     * Gère le retour OFF de l'action
     * @param actif booléens indiquants quels participants sont actifs
     * @param assomme booléens indiquants quels participants sont assommés
     * @param mort booléens indiquants quels participants sont morts
     * @param nom liste des noms des participants
     * @param n le nom du participant actuel (supposemment off)
     * @param a_pass si le joueur A (nécro) passera son prochain tour
     * @param i l'indice du joueur actuel
     * @return si le joueur A tente une résurection
     * @throws IOException mon poto
     */
    private static boolean act_mort(boolean[] actif, boolean[] assomme, boolean[] mort, String[] nom, String n, boolean a_pass, int i) throws IOException {
        if(a_pass){
            return true;
        }
        if(input.yn(n + " est-il mort ?")){
            boolean actif_a = false;
            for(int k = 0; k < 8; k++){
                if (actif[k] && Objects.equals(nom[k], Main.Joueur_A) && !assomme[k]) {
                    actif_a = true;
                    break;
                }
            }
            if(actif_a && !n.equals(Main.Joueur_A) && input.yn("Est-ce que " + Main.Joueur_A + " veux tenter de ressuciter " + n + " pour 2 PP ?")) {
                if (!ressuciter_allie()) {
                    System.out.println(n + " est mort.\n");
                    actif[i] = false;
                    mort[i] = true;
                }
                a_pass = true;
            }
            else{
                System.out.println(n + " est mort.");
                actif[i] = false;
                mort[i] = true;
            }
        }
        else {
            System.out.println(n + " est retiré du combat.\n");
            actif[i] = false;
        }
        return a_pass;
    }

    /**
     * Gère les compétences du monstre juste avant le combat
     * @param ennemi le monstre qu'affrontent les participants
     * @param nom les noms des participants
     * @param pr_l l'indice du participant en premières lignes
     * @param actif une liste indiquant si les participants sont actifs
     * @return si le combat s'arrête
     */
    static boolean competence(Monstre ennemi, String[] nom, int pr_l, boolean[] actif) throws IOException {
        switch (ennemi.competence){
            case DAMNATION_RES -> System.out.println(nom[pr_l] + " perd 2 points de vie pour la durée du combat.");
            case DAMNATION_ATK -> System.out.println(nom[pr_l] + " perd 1 point d'attaque pour la durée du combat.");
            case DAMN_ARES -> {
                if (input.yn("Un de vous est-il descendant d'Ares ?")){
                    System.out.println(ennemi.nom + " vous maudit.");
                    System.out.println("Tout descendant d'Arès perd 2 points d'attaque pour la durée du combat.");
                }
            }
            case HATE_DEMETER -> {
                if (input.yn("Un de vous est-il descendant de Demeter ?")){
                    System.out.println(ennemi.nom + " vous regarde avec haine.");
                    ennemi.attaque += 1;
                }
            }
            case HATE_DYONISOS -> {
                if (input.yn("Un de vous est-il descendant de Dyonis ?")){
                    System.out.println(ennemi.nom + " vous regarde avec haine.");
                    ennemi.attaque += 1;
                }
            }
            case HATE_POSEIDON -> {
                if (input.yn("Un de vous est-il descendant de Poseidon ?")){
                    System.out.println(ennemi.nom + " vous regarde avec haine.");
                    ennemi.attaque += 1;
                }
            }
            case HATE_ZEUS -> {
                if (input.yn("Un de vous est-il descendant de Zeus ?")){
                    System.out.println(ennemi.nom + " vous regarde avec haine.");
                    ennemi.attaque += 1;
                }
            }
            case FEAR_ZEUS -> {
                if (input.yn("Un de vous est-il descendant de Zeus ?")){
                    System.out.println(ennemi.nom + " vous crains.");
                    ennemi.attaque -= 1;
                }
            }
            case FEAR_DEMETER -> {
                if (input.yn("Un de vous est-il descendant de Demeter ?")){
                    System.out.println(ennemi.nom + " vous crains.");
                    ennemi.attaque -= 1;
                }
            }
            case FEAR_POSEIDON -> {
                if (input.yn("Un de vous est-il descendant de Poseidon ?")){
                    System.out.println(ennemi.nom + " vous crains.");
                    ennemi.attaque -= 1;
                }
            }
            case FEAR_HYPNOS -> {
                if (input.yn("Un de vous est-il descendant d'Hypnos ?")){
                    System.out.println(ennemi.nom + " vous crains.");
                    ennemi.attaque -= 1;
                }
            }
            case FEAR_DYONISOS -> {
                if (input.yn("Un de vous est-il descendant de Dyonisos ?")){
                    System.out.println(ennemi.nom + " vous crains.");
                    ennemi.attaque -= 1;
                }
            }
            case CIBLE_CASQUE -> {
                if (input.yn(nom[pr_l] + " porte-il un casque ?") && input.D6() <= 4){
                    System.out.println(ennemi.nom + " fait tomber votre casque pour le combat.");
                }
            }
            case ARMURE_NATURELLE, ARMURE_GLACE -> ennemi.armure += 1;
            case ARMURE_NATURELLE2, ARMURE_GLACE2 -> ennemi.armure += 2;
            case ARMURE_NATURELLE3 -> ennemi.armure += 3;
            case ARMURE_NATURELLE4 -> ennemi.armure += 4;
            case VITALITE_NATURELLE -> {
                ennemi.vie_max += 3;
                ennemi.vie += 3;
            }
            case VITALITE_NATURELLE2 -> {
                ennemi.vie_max += 6;
                ennemi.vie += 6;
            }
            case VITALITE_NATURELLE3 -> {
                ennemi.vie_max += 9;
                ennemi.vie += 9;
            }
            case FORCE_NATURELLE, FLAMME_ATTAQUE -> ennemi.attaque += 2;
            case FORCE_NATURELLE2 -> ennemi.attaque += 4;
            case FORCE_NATURELLE3 -> ennemi.attaque += 6;
            case PRUDENT -> {
                int tolerance = ennemi.attaque;
                for(int i = 0; i < 8; i++){
                    if(actif[i]){
                        System.out.print(nom[i] + " ");
                        tolerance -= input.atk();
                        if(tolerance < 0){
                            System.out.println(ennemi.nom + " fuit en vous voyant avancer.");
                            return true;
                        }
                    }
                }
            }
            case SUSPICIEUX -> {
                int tolerance = ennemi.vie + ennemi.armure * 3;
                for(int i = 0; i < 8; i++){
                    if(actif[i]){
                        System.out.print(nom[i] + " ");
                        tolerance -= 3 * input.def();
                        tolerance -= input.vie();
                        if(tolerance < 0){
                            System.out.println(ennemi.nom + " fuit en vous voyant avancer.");
                            return true;
                        }
                    }
                }
            }
            case MEFIANT -> {
                int tolerance = ennemi.vie + ennemi.armure * 3 + ennemi.attaque;
                for(int i = 0; i < 8; i++){
                    if(actif[i]){
                        System.out.print(nom[i] + " ");
                        tolerance -= 3 * input.def();
                        tolerance -= input.vie();
                        tolerance -= input.atk();
                        if(tolerance < 0){
                            System.out.println(ennemi.nom + " fuit en vous voyant avancer.");
                            return true;
                        }
                    }
                }
            }
            case REGARD_APPEURANT -> {
                System.out.println(nom[pr_l] + " croise le regard de " + ennemi.nom);
                if(input.D4() != 4){
                    System.out.println(nom[pr_l] + " est appeuré(e) et perd 1 points d'attaque pour la durée du combat");
                }
            }
            case REGARD_TERRIFIANT -> {
                System.out.println(nom[pr_l] + " croise le regard de " + ennemi.nom);
                if(input.D6() <= 5){
                    System.out.println(nom[pr_l] + " est terrifié(e) et perd 3 points d'attaque pour la durée du combat");
                }
            }
            case FAIBLE -> ennemi.attaque -= 3;
            case BENEDICTION -> {
                System.out.println(ennemi.nom + " béni " + nom[pr_l] + " qui gagne définitivement 1 point de résistance.");
                System.out.println(ennemi.nom + " a disparu...");
                return true;
            }
            case EQUIPE -> {
                System.out.println(ennemi.nom + " est lourdement équipé.");
                ennemi.attaque += rand.nextInt(3);
                int temp = rand.nextInt(5);
                ennemi.vie_max += temp;
                ennemi.vie += temp;
                ennemi.armure += rand.nextInt(2);
            }
            case DUO -> System.out.println("Il y a deux " + ennemi.nom + " !");
            case GEANT -> {
                ennemi.attaque = Monstre.corriger((float) (ennemi.attaque * 1.2));
                ennemi.vie_max = Monstre.corriger((float) (ennemi.vie_max * 1.2) + 2);
                ennemi.vie = ennemi.vie_max;
                ennemi.armure = ennemi.armure == 0 ? 0 : ennemi.armure - 1;
            }
            case BRUME -> System.out.println(ennemi.nom + "crée un rideau de brûme, tous les participants perdent 1 point d'attaque pour la durée du combat.");
            case GOLEM_PIERRE -> {
                ennemi.nom = "golem de pierre";
                ennemi.attaque += rand.nextInt(3) + 1;
                ennemi.vie_max += rand.nextInt(4) + 3;
                ennemi.vie = ennemi.vie_max;
                ennemi.armure += rand.nextInt(4) + 1;
            }
            case GOLEM_FER -> {
                ennemi.nom = "golem de fer";
                ennemi.attaque += rand.nextInt(4) + 2;
                ennemi.vie_max += rand.nextInt(8) + 5;
                ennemi.vie = ennemi.vie_max;
                ennemi.armure += rand.nextInt(5) + 2;
                ennemi.niveau_drop_max += 1;
            }
            case GOLEM_ACIER -> {
                ennemi.nom = "golem d'acier";
                ennemi.attaque += rand.nextInt(6) + 3;
                ennemi.vie_max += rand.nextInt(10) + 7;
                ennemi.vie = ennemi.vie_max;
                ennemi.armure += rand.nextInt(6) + 3;
                ennemi.niveau_drop_max += 1;
                ennemi.niveau_drop_min += 1;
                ennemi.drop_quantite_max += 1;
            }
            case GOLEM_MITHRIL -> {
                ennemi.nom = "golem de mithril";
                ennemi.attaque += rand.nextInt(8) + 4;
                ennemi.vie_max += rand.nextInt(12) + 9;
                ennemi.vie = ennemi.vie_max;
                ennemi.armure += rand.nextInt(6) + 4;
                ennemi.niveau_drop_max += 2;
                ennemi.niveau_drop_min += 1;
                ennemi.drop_quantite_max += 2;
            }
        }
        return false;
    }

    /**
     * Gère les compétences de l'ennemi lors d'un changement de position
     * @param ennemi le monstre ennemi
     * @param nom_pr_l le nom de l'unité en première ligne
     * @throws IOException ça va mon pote ?
     */
    static void competence_avance(Monstre ennemi, String nom_pr_l) throws IOException {
        switch (ennemi.competence){
            case ASSAUT -> {
                System.out.println(ennemi.nom + " se jete sur " + nom_pr_l + " avant que vous ne vous en rendiez compte");
                ennemi.attaque(nom_pr_l);
            }
            case CHANT_SIRENE -> System.out.println("Le chant de " + ennemi.nom + " perturbe " + nom_pr_l + " qui perd 1 point d'attaque pour la durée du combat.");
        }
    }

    /**
     * Suprimme le monstre de sa zone après sa mort s'il est nommé
     * @param ennemi le monstre ennemi
     * @implNote couvre les illusions
     */
    static void gestion_nomme(Monstre ennemi){
        if(ennemi.competence == Competence.PRUDENT || ennemi.competence == Competence.MEFIANT || ennemi.competence== Competence.SUSPICIEUX ||
                ennemi.competence == Competence.CHRONOS) {
            switch (ennemi.nom) {
                case "Cerbère" -> {
                    for (int i = 0; i < Race.enfers.length; i++) {
                        if (Objects.equals(Race.enfers[i].nom, "Cerbère")) {
                            Race.enfers[i] = null;
                            return;
                        }
                    }
                }
                case "Lycaon" -> {
                    for (int i = 0; i < Race.prairie.length; i++) {
                        if (Objects.equals(Race.prairie[i].nom, "Lycaon")) {
                            Race.prairie[i] = null;
                            return;
                        }
                    }
                }
                case "Mormo" -> {
                    for (int i = 0; i < Race.prairie.length; i++) {
                        if (Objects.equals(Race.prairie[i].nom, "Mormo")) {
                            Race.prairie[i] = null;
                            return;
                        }
                    }
                }
                case "Laton" -> {
                    for (int i = 0; i < Race.vigne.length; i++) {
                        if (Objects.equals(Race.vigne[i].nom, "Laton")) {
                            Race.vigne[i] = null;
                            return;
                        }
                    }
                }
                case "Empousa" -> {
                    for (int i = 0; i < Race.vigne.length; i++) {
                        if (Objects.equals(Race.vigne[i].nom, "Empousa")) {
                            Race.vigne[i] = null;
                            return;
                        }
                    }
                }
                case "Python" -> {
                    for (int i = 0; i < Race.temple.length; i++) {
                        if (Objects.equals(Race.temple[i].nom, "Python")) {
                            Race.temple[i] = null;
                            return;
                        }
                    }
                }
                case "Echidna" -> {
                    for (int i = 0; i < Race.temple.length; i++) {
                        if (Objects.equals(Race.temple[i].nom, "Echidna")) {
                            Race.temple[i] = null;
                            return;
                        }
                    }
                }
                case "Scylla" -> {
                    for (int i = 0; i < Race.mer.length; i++) {
                        if (Objects.equals(Race.mer[i].nom, "Scylla")) {
                            Race.mer[i] = null;
                            return;
                        }
                    }
                }
                case "Charibe" -> {
                    for (int i = 0; i < Race.mer.length; i++) {
                        if (Objects.equals(Race.mer[i].nom, "Charibe")) {
                            Race.mer[i] = null;
                            return;
                        }
                    }
                }
                case "Typhon" -> {
                    for (int i = 0; i < Race.mont.length; i++) {
                        if (Objects.equals(Race.mont[i].nom, "Typhon")) {
                            Race.mont[i] = null;
                            return;
                        }
                    }
                }
                case "l'Aigle du Caucase" -> {
                    for (int i = 0; i < Race.mont.length; i++) {
                        if (Objects.equals(Race.mont[i].nom, "l'Aigle du Caucase")) {
                            Race.mont[i] = null;
                            return;
                        }
                    }
                }
                case "Chronos" -> {
                    for (int i = 0; i < Race.olympe.length; i++) {
                        if (Objects.equals(Race.olympe[i].nom, "Chronos")) {
                            Race.olympe[i] = null;
                            return;
                        }
                    }
                }
                default -> { //par sécurité
                }
            }
            for (int i = 0; i < Race.enfers.length; i++) {
                if (Objects.equals(Race.enfers[i].nom, ennemi.nom)) {
                    Race.enfers[i] = null;
                    return;
                }
            }
            for (int i = 0; i < Race.prairie.length; i++) {
                if (Objects.equals(Race.prairie[i].nom, ennemi.nom)) {
                    Race.prairie[i] = null;
                    return;
                }
            }
            for (int i = 0; i < Race.vigne.length; i++) {
                if (Objects.equals(Race.vigne[i].nom, ennemi.nom)) {
                    Race.vigne[i] = null;
                    return;
                }
            }
            for (int i = 0; i < Race.temple.length; i++) {
                if (Objects.equals(Race.temple[i].nom, ennemi.nom)) {
                    Race.temple[i] = null;
                    return;
                }
            }
            for (int i = 0; i < Race.mer.length; i++) {
                if (Objects.equals(Race.mer[i].nom, ennemi.nom)) {
                    Race.mer[i] = null;
                    return;
                }
            }
            for (int i = 0; i < Race.mont.length; i++) {
                if (Objects.equals(Race.mont[i].nom, ennemi.nom)) {
                    Race.mont[i] = null;
                    return;
                }
            }
            for (int i = 0; i < Race.olympe.length; i++) {
                if (Objects.equals(Race.olympe[i].nom, ennemi.nom)) {
                    Race.olympe[i] = null;
                    return;
                }
            }

        }
    }

    /**
     * Applique la compétence de nécromantien "nécromancie"
     * @param ennemi le monstre à ressuciter
     * @return si le sort a fonctionné
     * @throws IOException pour l'input
     * @implNote codé en dur de manière très irresponsable
     */
    private static boolean ressuciter(Monstre ennemi) throws IOException {
        switch(input.D8()){
            case 4, 5 -> { //25%
                System.out.println(ennemi.nom + " a été partiellement ressucité.");

                System.out.println("nouveau familier : zombie " + ennemi.nom);
                System.out.println("attaque : " + max(ennemi.attaque / 4, 1));
                System.out.println("vie : " + max(ennemi.vie_max / 4, 1));
                System.out.println("armure : " + ennemi.armure / 4 + "\n");
                return true;
            }
            case 6 -> { //50%
                System.out.println(ennemi.nom + " a été suffisemment ressucité");

                System.out.println("nouveau familier : zombie " + ennemi.nom);
                System.out.println("attaque : " + max(ennemi.attaque / 2, 1));
                System.out.println("vie : " + max(ennemi.vie_max / 2, 1));
                System.out.println("armure : " + ennemi.armure / 2 + "\n");
                return true;
            }
            case 7 -> { // 75%
                System.out.println(ennemi.nom + " a été correctement ressucité" );

                System.out.println("nouveau familier : " + ennemi.nom + " le ressucité");
                System.out.println("attaque : " + max(ennemi.attaque * 3 / 4, 1));
                System.out.println("vie : " + max(ennemi.vie_max * 3 / 4, 1));
                System.out.println("armure : " + ennemi.armure * 3 / 4 + "\n");
                return true;
            }
            case 8, 9 -> {
                System.out.println(ennemi.nom + " a été parfaitement ressucité");

                System.out.println("nouveau familier : " + ennemi.nom);
                System.out.println("attaque : " + max(ennemi.attaque, 1));
                System.out.println("vie : " + max(ennemi.vie_max, 1));
                System.out.println("armure : " + ennemi.armure + "\n");
                return true;
            }
            default -> {
                System.out.println("échec du sort.");
                return false;
            }
        }
    }

    /**
     * tente de ressuciter un allie
     * @return si l'allié a été ressucité
     * @throws IOException notre poto anti bug
     */
    private static boolean ressuciter_allie() throws IOException {
        return switch (input.D8()) {
            case 6 -> {
                System.out.println("Résurection avec 4 points de vie");
                yield true;
            }
            case 7 -> {
                System.out.println("Résurection avec 8 (max) points de vie");
                yield true;
            }
            case 8, 9 -> {
                System.out.println("Résurection avec 12 (max) points de vie");
                yield true;
            }
            default -> {
                System.out.println("Echec de la résurection");
                yield false;
            }
        };
    }

    private static void maudir(Monstre ennemi) throws IOException {
        switch (input.D6()){
            case 2 -> {
                System.out.println("Vous maudissez faiblement " + ennemi.nom);
                ennemi.vie_max -= 1;
                ennemi.vie -= 1;
            }
            case 3, 4 -> {
                System.out.println("Vous maudissez " + ennemi.nom);
                ennemi.vie_max -= 2;
                ennemi.vie -= 2;
            }
            case 5 -> {
                System.out.println("Vous maudissez agressivement " + ennemi.nom);
                ennemi.vie_max -= 3;
                ennemi.vie -= 3;
            }
            case 6 -> {
                System.out.println("Vous maudissez puissament " + ennemi.nom);
                ennemi.vie_max -= 5;
                ennemi.vie -= 5;
            }
            default -> System.out.println("vous n'arrivez pas à maudir " + ennemi.nom);
        }
    }

    static private void onde_choc(boolean[] actif, String[] nom, boolean[] assomme, Monstre ennemi) throws IOException {
        for(int i = 0; i < nom.length; i++){
            if(!nom[i].equals(Main.Joueur_B) && actif[i]){
                System.out.println(nom[i] + " est frappé par l'onde de choc.");
                if(i <= 4){
                    if(input.D6() <= 3){
                        System.out.println(nom[i] + " perd connaissance.\n");
                        assomme[i] = true;
                    }
                    else{
                        System.out.println(nom[i] + " parvient à rester conscient.\n");
                    }
                }
                else{
                    if(input.D4() <= 3){
                        System.out.println(nom[i] + " perd connaissance.\n");
                        assomme[i] = true;
                    }
                    else{
                        System.out.println(nom[i] + " parvient à rester conscient.\n");
                    }
                }
            }
        }
        System.out.println(ennemi.nom + " est frappé par l'onde de choc.");
        System.out.print(Main.Joueur_B + " : ");
        switch (input.D6()){
            case 2 -> ennemi.do_etourdi();
            case 3, 4 -> ennemi.affecte();
            case 5, 6 -> ennemi.do_assomme();
            default -> System.out.println(ennemi.nom + " n'a pas l'air très affecté...\n");
        }
    }

    static private boolean popo_rez(String nom_healer, String nom_mort) throws IOException {
        if(input.yn("Utilisez vous une potion divine ?")){
            System.out.println(nom_healer + " fait boire à " + nom_mort + " une potion gorgé de l'énergie des dieux.");
            switch (input.D6()) {
                case 1 -> {
                    System.out.println(nom_mort + " se réveille avec 1 points de vie.\n");
                    return true;
                }
                case 2 -> {
                    System.out.println(nom_mort + " se réveille avec 2 points de vie.\n");
                    return true;
                }
                case 3, 4 -> {
                    System.out.println(nom_mort + " se réveille avec 4 points de vie.\n");
                    return true;
                }
                case 5, 6 -> {
                    System.out.println(nom_mort + " se réveille avec 6 points de vie.\n");
                    return true;
                }
                default -> {
                    System.out.println(nom_mort + "reste mort.\n");
                    return false;
                }
            }
        }
        if(input.yn("Utilisez vous un élixir ?")){
            switch (input.D20()) {
                case 1, 2, 3 -> {
                    System.out.println(nom_mort + " se réveille avec 2 points de vie et 3 points de résistance additionels.\n");
                    return true;
                }
                case 4, 5, 6 -> {
                    System.out.println(nom_mort + " se réveille avec 3 points de vie et 4 points de résistance additionels.\n");
                    return true;
                }
                case 7, 8 -> {
                    System.out.println(nom_mort + " se réveille avec 5 points de vie et 7 points de résistance additionels.\n");
                    return true;
                }
                case 9, 10 -> {
                    System.out.println(nom_mort + " se réveille avec 6 points de vie et 9 points de résistance additionels.\n");
                    return true;
                }
                case 11, 12 -> {
                    System.out.println(nom_mort + " se réveille avec 6 points de vie et 12 points de résistance additionels.\n");
                    return true;
                }
                case 13, 14, 15 -> {
                    System.out.println(nom_mort + " se réveille avec 7 points de vie et 13 points de résistance additionels.\n");
                    return true;
                }
                case 16, 17 -> {
                    System.out.println(nom_mort + " se réveille avec 7 points de vie et 14 points de résistance additionels.\n");
                    return true;
                }
                case 18, 19 -> {
                    System.out.println(nom_mort + " se réveille avec 8 points de vie et 14 points de résistance additionels.\n");
                    return true;
                }
                case 20 -> {
                    System.out.println(nom_mort + " se réveille avec 8 points de vie et 15 points de résistance additionels.\n");
                    return true;
                }
                default -> {
                    System.out.println(nom_mort + "reste mort.\n");
                    return false;
                }
            }
        }
        System.out.println("Vous n'avez aucun moyen de ressuciter " + nom_mort + ".");
        return false;
    }

    /**
     * Analyse le monstre ennemi et écrit ses stats aux joueurs
     * @param is_prl booléan indiquant si l'analyste est en première ligne
     * @param ennemi le monstre analysé
     * @throws IOException ce bon vieux throws
     */
    static private void analyser(boolean is_prl, Monstre ennemi) throws IOException {
        System.out.println("Vous analysez le monstre en face de vous.");
        int temp = input.D8();
        if(!is_prl){
            temp -= 2; //malus si en seconde ligne
        }
        int pv, pvm, arm, atk;
        switch(ennemi.competence) {
            case ILLU_AURAI -> {
                pvm = Race.aurai_malefique.get_vie();
                pv = pvm - (ennemi.vie_max - ennemi.vie);
                arm = Race.aurai_malefique.get_armure();
                atk = Race.aurai_malefique.get_attaque();
            }
            case ILLU_CYCLOPE -> {
                pvm = Race.cyclope.get_vie();
                pv = pvm - (ennemi.vie_max - ennemi.vie);
                arm = Race.cyclope.get_armure();
                atk = Race.cyclope.get_attaque();
            }
            case ILLU_DULLA -> {
                pvm = Race.dullahan.get_vie();
                pv = pvm - (ennemi.vie_max - ennemi.vie);
                arm = Race.dullahan.get_armure();
                atk = Race.dullahan.get_attaque();
            }
            case ILLU_GOLEM -> {
                pvm = Race.golem.get_vie();
                pv = pvm - (ennemi.vie_max - ennemi.vie);
                arm = Race.golem.get_armure();
                atk = Race.golem.get_attaque();
            }
            case ILLU_ROCHE -> {
                pvm = Race.roche_maudite.get_vie();
                pv = pvm - (ennemi.vie_max - ennemi.vie);
                arm = Race.roche_maudite.get_armure();
                atk = Race.roche_maudite.get_attaque();
            }
            case ILLU_SIRENE -> {
                pvm = Race.sirene.get_vie();
                pv = pvm - (ennemi.vie_max - ennemi.vie);
                arm = Race.sirene.get_armure();
                atk = Race.sirene.get_attaque();
            }
            case ILLU_TRITON -> {
                pvm = Race.triton.get_vie();
                pv = pvm - (ennemi.vie_max - ennemi.vie);
                arm = Race.triton.get_armure();
                atk = Race.triton.get_attaque();
            }
            case ILLU_VENTI -> {
                pvm = Race.venti.get_vie();
                pv = pvm - (ennemi.vie_max - ennemi.vie);
                arm = Race.venti.get_armure();
                atk = Race.venti.get_attaque();
            }
            default -> {
                pvm = ennemi.vie_max;
                pv = ennemi.vie;
                arm = ennemi.armure;
                atk = ennemi.attaque;
            }
        }
        System.out.println(ennemi.nom + " :");
        System.out.println("vie : " + (temp >= 5 ? pv : "???") + "/" + (temp >= 2 ? pvm : "???"));
        System.out.println("attaque : " + (temp >= 3 ? atk : "???"));
        System.out.println("armure : " + (temp >= 7 ? arm : "???\n"));
    }

    static private void gestion_mort_end(boolean[] morts, String[] nom) throws IOException {
        for(int i = 0; i < 4; i++){
            if(morts[i] && input.yn(nom[i] + " est mort durant le combat, le reste-t-il ?")){
                System.out.println(nom[i] + " se retrouve aux enfers.\n");
                Main.positions[i] = Position.ENFERS;
            }
        }
    }
}

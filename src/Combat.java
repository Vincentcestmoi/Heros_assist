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
     * @return un int lié au joueur qui obtient un familier (ou -1 si aucun n'en obtient).
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

        if (!(j_a || j_b || j_c || j_d)) {
            System.out.println("Erreur : aucun joueur détecté, annulation du combat.");
            return -1;
        }

        // stockage des participants
        boolean[] actif = {j_a, j_b, j_c, j_d, f_a, f_b, f_c, f_d};
        String[] nom = {Main.Joueur_A, Main.Joueur_B, Main.Joueur_C, Main.Joueur_D,
                "le familier de " + Main.Joueur_A, "le familier de " + Main.Joueur_B, "le familier de " + Main.Joueur_C,
                "le familier de " + Main.Joueur_D};
        boolean[] mort = {false, false, false, false, false, false, false, false};

        int nbp = 0;
        for (boolean b : actif) {
            if (b) {
                nbp += 1;
            }
        }

        int pr_l = joueur_force;
        if(pr_l == -1){
            pr_l = getPrL(nom, nbp, actif);
        }
        System.out.println(nom[pr_l] + " se retrouve en première ligne.\n");

        if (competence(ennemi, nom, pr_l, actif)) {
            return -1;
        }

        // si l'ennemi à l'avantage de la surprise
        if (joueur_force != -1) {
            ennemi.attaque(nom[joueur_force]);
        }

        int x = combat(ob_a, ob_b, ob_c, ob_d, ennemi, actif, nom, pr_l, mort);

        System.out.println("Fin du combat\n");
        gestion_mort_end(mort, nom);
        return x;
    }

    /**
     * Renvoie l'index du joueur qui serra en première ligne
     * @param nom le nom des participants
     * @param nbp le nombre de participants
     * @param actif les boolean indiquant si les participants sont actifs
     * @return l'index du joueur en première ligne
     * @throws IOException toujours
     */
    private static int getPrL(String[] nom, int nbp, boolean[] actif) throws IOException {

        // demander gentimment
        if (nbp > 1) {
            for (int i = 0; i < 8; i++) {
                if (actif[i]) {
                    if (input.yn("Est-ce que " + nom[i] + " passe au front ?")) {
                        return i;
                    }
                }
            }
        }

        // z'avez plus le choix
        int i;
        do {
            i = rand.nextInt(8);
        } while (!actif[i]);
        return i;
    }

    /**
     * Gère le combat en appliquant les actions
     * @param ob_a l'obéissance du familier du joueur A (0 s'il n'en a pas).
     * @param ob_b l'obéissance du familier du joueur B (0 s'il n'en a pas).
     * @param ob_c l'obéissance du familier du joueur C (0 s'il n'en a pas).
     * @param ob_d l'obéissance du familier du joueur D (0 s'il n'en a pas).
     * @param ennemi le monstre adverse
     * @param actif liste de boolean indiquant si un participant est actif
     * @param nom liste des noms des participants
     * @param pr_l index du participant de première ligne
     * @param mort liste de boolean indiquant si les participants sont morts
     * @return l'index du joueur qui domestique l'ennemi, ou 0 sinon
     * @throws IOException et oui
     */
    private static int combat(int ob_a, int ob_b, int ob_c, int ob_d, Monstre ennemi, boolean[] actif, String[] nom,
                                  int pr_l, boolean[] mort) throws IOException {


        boolean[] assomme = {false, false, false, false, false, false, false, false};
        int[] reveil = {0, 0, 0, 0, 0, 0, 0, 0};


        // on prépare une bijection aléatoire pour l'ordre de jeu
        int[] t = {-1, -1, -1, -1, -1, -1, -1, -1};
        for (int i = 0; i < 8; ) {
            int temp = rand.nextInt(8);
            if (t[temp] == -1) {
                t[temp] = i;
                i++;
            }
        }

        int ob, i;
        String n;
        Action act;
        boolean run = !ennemi.check_mort();
        boolean a_pass = false, berserk = false;
        while (run) {

            //chaque joueur
            for (int j = 0; j < 8; j++) {
                i = t[j];

                // on ne joue que les participants actifs
                if (!actif[i]) {
                    continue;
                }

                n = nom[i]; // on stocke le nom par commodité

                // si le nécromancien a ressucité, il a déjà utilisé son tour
                if (Objects.equals(n, Main.necromancien) && a_pass) {
                    System.out.println(n + " s'est concentré sur son sort de résurection.");
                    a_pass = false;
                    continue;
                }

                if (gere_assome(assomme, n, i, reveil)){
                    continue;
                }

                // on différencie les familiers et joueurs
                if (n.contains("familier")) {
                    if (n.contains(Main.Joueur_A)) {
                        ob = ob_a;
                    } else if (n.contains(Main.Joueur_B)) {
                        ob = ob_b;
                    } else if (n.contains(Main.Joueur_C)) {
                        ob = ob_c;
                    } else if (n.contains(Main.Joueur_D)) {
                        ob = ob_d;
                    } else {
                        System.out.println("Erreur : " + n + " détecté comme familier, mais aucun joueur rattaché" +
                                "entité ignorée pour la suite du combat.");
                        actif[i] = false;
                        continue;
                    }
                } else {
                    ob = 0; // joueur
                }

                // action
                act = input.action(n, ob != 0, i == pr_l, mort);
                //TODO : méditation
                switch (act) {
                    case OFF -> {
                        a_pass = alteration(actif, assomme, mort, nom, n, a_pass, i);
                        if(i != pr_l) {
                            a_pass = alteration(actif, assomme, mort, nom, nom[pr_l], a_pass, pr_l);
                            if (actif[i]) {
                                j--;
                            }
                        }
                    }
                    case END -> {
                        return -1;
                    }
                    case TIRER -> ennemi.tir(input.atk());
                    case MAGIE -> ennemi.dommage_magique(input.magie());
                    case FUIR -> {
                        if(familier_act(ob, actif, i, n, ennemi)) {
                            fuir(ennemi.nom, i, i == pr_l, actif, n);
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

                            return switch (n) {
                                case Main.Joueur_A -> 0;
                                case Main.Joueur_B -> 1;
                                case Main.Joueur_C -> 2;
                                case Main.Joueur_D -> 3;
                                default -> -1;
                            };
                        }
                    }
                    case ANALYSER -> analyser(i == pr_l, ennemi);
                    case AUTRE -> {
                        if(familier_act(ob, actif, i, n, ennemi)){
                            System.out.println(n + " fait quelque chose.\n");
                        }
                    }
                    case AVANCER -> {
                        if(familier_act(ob, actif, i, n, ennemi)){
                            System.out.println(n + " passe en première ligne.\n");
                            pr_l = i;
                            ennemi.reset_encaisser();
                            competence_avance(ennemi, nom[pr_l]);
                        }
                    }
                    case MAUDIR -> maudir(ennemi);
                    case ONDE_CHOC -> onde_choc(actif, nom, assomme, ennemi);
                    case POTION_REZ -> {
                        int temp = input.ask_rez(mort);
                        if (temp != -1 && popo_rez(nom[temp])) {
                            mort[temp] = false;
                            actif[temp] = true;
                            assomme[temp] = false;
                        }
                        if (i == pr_l) { //premiere ligne
                            System.out.println(n + "s'expose pour donner sa potion.");
                            ennemi.part_soin += 0.4F;
                        }
                    }
                    case BERSERK -> {
                        System.out.println(Main.guerriere + " est prit d'une folie meurtrière !");
                        berserk = true;
                        ennemi.dommage(input.atk(), 1.2F);
                    }
                    case LAME_DAURA -> {
                        if (berserk && input.D6() < 4) {
                            int l;
                            do {
                                l = rand.nextInt(8);
                            } while (!actif[l]);
                            System.out.println("Prise de folie, " + Main.guerriere + " attaque " + nom[i] + "  !");
                        } else {
                            ennemi.dommage(input.atk(), 2.4F);
                            System.out.println("L'arme principale de " + Main.guerriere + " se brise !");
                        }
                    }
                    case RETOUR -> {
                        int k = j;
                        do{
                            k = k == 0 ? 7 : k - 1;
                        }while(!actif[t[k]]);
                        j = t[k];
                    }
                    default -> { // ATTAQUER
                        if (berserk && n.equals(Main.guerriere)) { //berserker
                            if (input.D6() < 4) {
                                int l;
                                do {
                                    l = rand.nextInt(8);
                                } while (!actif[l]);
                                System.out.println("Prise de folie, " + Main.guerriere + " attaque " + nom[i] + "  !");
                            } else {
                                ennemi.dommage(input.atk(), 1.2F);
                            }
                        }
                        // attaque normale
                        else if(familier_act(ob, actif, i, n, ennemi)){
                            System.out.println(n + " attaque l'ennemi.\n");
                            ennemi.dommage(input.atk());
                        }
                    }
                }

                // s'assure qu'un participant est toujours en première ligne
                if (!actif[pr_l]) {
                    boolean is_active = false;
                    int k = 0;
                    for(;k < 8; k++){
                        if(actif[k]){
                            is_active = true;
                            break;
                        }
                    }
                    if (!is_active) { // plus de joueur participant
                        run = false;
                        System.out.println("Aucun joueur ou familier détecté en combat.");
                        // break inutile, car actif[i] toujours à false
                    }
                    do{
                        k = rand.nextInt(8);
                    }while(!actif[k]);
                    pr_l = k;
                    System.out.println(nom[k] + " se retrouve en première ligne.\n");
                }

                if (ennemi.check_mort()) {
                    // la mort est donné par les méthodes de dommage
                    gestion_nomme(ennemi);
                    run = false;

                    //le nécromancien peut tenter de ressuciter le monstre
                    boolean actif_a = false;
                    for (int k = 0; k < 8; k++) {
                        if (actif[k] && Objects.equals(nom[k], Main.necromancien)) {
                            actif_a = true;
                            break;
                        }
                    }
                    if (actif_a && input.yn("Voulez vous tenter de ressuciter " + ennemi.nom + " en tant que familier pour 2PP ?")) {
                        if (ressuciter(ennemi)) {
                            return switch(Main.necromancien){
                                //noinspection DataFlowIssue
                                case Main.Joueur_A -> 0;
                                case Main.Joueur_B -> 1;
                                case Main.Joueur_C -> 2;
                                case Main.Joueur_D -> 3;
                                default -> -1;
                            };
                        }
                    }
                    break;
                }
            }

            // tour de l'adversaire
            if (run) {
                ennemi.attaque(nom[pr_l]);
                if (ennemi.check_mort()) {
                    gestion_nomme(ennemi);
                    run = false;
                }
            }
        }
        return -1;
    }

    /**
     * Regarde si le participant est assommé et gère le cas échéant
     * @param assomme tableau de boolean indiquant les participants assommés
     * @param n nom du participant
     * @param index indix du participant
     * @param reveil tableau de valeur indiquant a quel point les participants sont proches de se reveiller
     * @return si la participant perd son tour
     * @throws IOException toujours
     */
    private static boolean gere_assome(boolean[] assomme, String n, int index, int[] reveil) throws IOException {
        if (!assomme[index]) {
            return false;
        }
        System.out.println(n + " est inconscient.");

        // l'archimage peut se réveiller et jouer quand même via un sort
        if (Objects.equals(n, Main.archimage) && input.yn("Utiliser purge (3PP) ?")) {
            System.out.println(n + " se réveille.\n");
            assomme[index] = false;
            reveil[index] = 0;
            return false;
        }

        // réveil standard
        else if (input.D6() + reveil[index] >= 5) {
            System.out.println(n + " se réveille.\n");
            assomme[index] = false;
            reveil[index] = 0;
        } else {
            System.out.println(n + " est toujours inconscient.");
            if (Objects.equals(n, Main.archimage)) {
                System.out.println(n + " recupère 1PP.\n");
            }
            else{
                System.out.println();
            }

            // réveil pas à pas
            reveil[index] += 1;
        }
        return true;
    }

    /**
     * Tente de fuir le combat
     * @param ne le nom du monstre ennemi
     * @param i l'index du participant
     * @param is_pr si le participant est en première ligne
     * @param actif la liste de boolean d'activité des participants
     * @param n le nom du participant
     * @throws IOException ça roule
     */
    private static void fuir(String ne, int i, boolean is_pr, boolean[] actif, String n) throws IOException {
        if (is_pr || input.D6() > 2 + rand.nextInt(2)) {
            actif[i] = false;
            System.out.println(n + " a fuit le combat.\n");
        } else {
            System.out.println(n + " n'est pas parvenu à distancer " + ne + "\n");
        }
    }

    /**
     * Simule le comportement d'un familier en fonction de son niveau d'obéissance
     * @param ob l'obéissance du familier, si la valeur est 0, le programme renverra true
     * @param actif la liste de boolean d'activité des participants
     * @param index l'index du familier
     * @param n le nom du familier
     * @param ennemi le monstre ennemi
     * @return si le familier joue l'action, un false remplace l'action
     * @implNote un joueur peut être entré avec une obéissance de 0.
     */
    private static boolean familier_act(int ob, boolean[] actif, int index, String n, Monstre ennemi) throws IOException {
        if(ob < 1 || ob == Main.f_max){
            return true;
        }
        int temp = ob + input.D6() - 3 + rand.nextInt(2); //valeur d'obeisance à l'action
        if (temp <= 1) {
            System.out.println(n + " fuit le combat.\n");
            actif[index] = false;
        }
        else if (temp == 2) {
            System.out.println(n + " n'écoute pas vos ordres.\n");
        }
        else if (temp <= 4){
            System.out.println(n + " ignore vos directives et attaque l'ennemi.\n");
            ennemi.dommage(input.atk());
        }
        else{
            return true;
        }
        return false;
    }

    /**
     * Gère le retour OFF de l'action, c.-à-d. la mort, l'inconscience ou le retrait du joueur actif ou de celui
     * de première ligne
     * @param actif booléens indiquants quels participants sont actifs
     * @param assomme booléens indiquants quels participants sont assommés
     * @param mort booléens indiquants quels participants sont morts
     * @param nom liste des noms des participants
     * @param n le nom du participant actuel (supposemment off)
     * @param a_pass si le joueur A (nécro) passera son prochain tour
     * @param i l'indice du joueur actuel
     * @return si le nécromancien a utilisé son tour
     * @throws IOException mon poto
     */
    private static boolean alteration(boolean[] actif, boolean[] assomme, boolean[] mort, String[] nom, String n, boolean a_pass, int i) throws IOException {
        if(input.yn(n + " est-il/elle mort(e) ?")){

            //on regarde si on peut le ressuciter immédiatement
            boolean actif_a = false;
            for(int k = 0; k < 8; k++){
                if (actif[k] && Objects.equals(nom[k], Main.necromancien) && !n.equals(nom[k]) && !assomme[k] && !a_pass) { //le joueur est necromancien et disponible
                    actif_a = true;
                    break;
                }
            }
            if(actif_a && input.yn("Est-ce que " + Main.necromancien + " veux tenter de ressuciter " + n + " pour 2 PP ?")) {
                if (!ressuciter()) {
                    System.out.println(n + " est mort(e).");
                    actif[i] = false;
                    mort[i] = true;
                }
                a_pass = true;
            }
            else{
                System.out.println(n + " est mort(e).");
                actif[i] = false;
                mort[i] = true;
            }
        }
        else if (input.yn(n + " est-il/elle inconscient(e) ?")) {
            assomme[i] = true;
        }
        else if (!input.yn(n + " est-il/elle toujours en combat ?")) {
            System.out.println(n + " est retiré(e) du combat.");
            actif[i] = false;
        }
        System.out.println();
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
                if (input.yn(nom[pr_l] + " porte-il/elle un casque ?") && input.D6() <= 4){
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
     * @implNote ne couvre que les monstres nommés
     */
    static void gestion_nomme(Monstre ennemi) {
        if (ennemi.competence == Competence.PRUDENT || ennemi.competence == Competence.MEFIANT || ennemi.competence == Competence.SUSPICIEUX ||
                ennemi.competence == Competence.CHRONOS) {
            delete_monstre_nomme(ennemi.nom);
        }
    }

    /**
     *
     * Supprime le monstre nommé donné de sa liste
     * @param monstre le nom du monstre à supprimer
     * @implNote ne couvre que les monstres nommés, utiliser delete_montre sinon
     */
    static void delete_monstre_nomme(String monstre) {
        Race[] list = getList(monstre);
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i] != null && Objects.equals(list[i].nom, monstre)) {
                    list[i] = null;
                    Output.dismiss_race(getListnom(monstre), monstre);
                    return;
                }
            }
        }
        System.out.println(monstre + " non reconnu comme monstre nommé, utilisez delete_montre pour supprimer.");
    }

    /**
     * Supprime le monstre donné de sa liste
     * @param monstre le nom du monstre à supprimer
     * @implNote n'enregistre pas la suppression dans les fichiers de sauvegarde
     */
    static void delete_monstre(String monstre) {
        Race[][] lists = {Race.enfers, Race.prairie, Race.vigne, Race.temple, Race.mer, Race.mont, Race.olympe};
        for (Race[] l : lists) {
            for (int i = 0; i < l.length; i++) {
                if (l[i] != null && Objects.equals(l[i].nom, monstre)) {
                    System.out.println(monstre + " supprimé(e).");
                    l[i] = null;
                    return;
                }
            }
        }
        System.out.println(monstre + " abscent(e) des bases de données, suppression impossible.");
    }

    /**
     * Fournie la liste à laquelle appartient le monstre
     * @param ennemi le nom du monstre à identifier
     * @return sa liste de référence
     * @implNote ne regarde que les monstres nommés
     */
    private static Race[] getList(String ennemi) {
        Race[] list = null;
        switch (ennemi) {
            case "Cerbère" -> list = Race.enfers;
            case "Lycaon", "Mormo" -> list = Race.prairie;
            case "Laton", "Empousa" -> list = Race.vigne;
            case "Python", "Echidna" -> list =  Race.temple;
            case "Scylla", "Charibe" -> list =  Race.mer;
            case "Typhon", "l'Aigle du Caucase" -> list =  Race.mont;
            case "Chronos" -> list =  Race.olympe;
        }
        return list;
    }

    /**
     * Fournie le nom de la liste à laquelle appartient le monstre
     * @param ennemi le nom du monstre à identifier
     * @return le nom de sa liste de référence
     * @implNote ne regarde que les monstres nommés
     */
    private static String getListnom(String ennemi) {
        return switch (ennemi) {
            case "Cerbère" -> "enfers";
            case "Lycaon", "Mormo" -> "prairie";
            case "Laton", "Empousa" -> "vigne";
            case "Python", "Echidna" -> "temple";
            case "Scylla", "Charibe" -> "mer";
            case "Typhon", "l'Aigle du Caucase" -> "mont";
            case "Chronos" -> "olympe";
            default -> {
                System.out.println(ennemi + " non reconnu.");
                yield "erreur";
            }
        };
    }

    /**
     * Tente de ressuciter un ennemi par nécromancie
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
     * Tente de ressuciter un allié par nécromancie
     * @return si l'allié a été ressucité
     * @throws IOException notre poto anti bug
     */
    private static boolean ressuciter() throws IOException {
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
        int boost = rand.nextInt(3);
        switch (input.D6()){
            case 2 -> {
                System.out.println("Vous maudissez faiblement " + ennemi.nom);
                ennemi.vie_max -= 1 + boost;
                ennemi.vie -= 1 + boost;
            }
            case 3, 4 -> {
                System.out.println("Vous maudissez " + ennemi.nom);
                ennemi.vie_max -= 2 + boost;
                ennemi.vie -= 2 + boost;
            }
            case 5 -> {
                System.out.println("Vous maudissez agressivement " + ennemi.nom);
                ennemi.vie_max -= 3 + boost;
                ennemi.vie -= 3 + boost;
            }
            case 6 -> {
                System.out.println("Vous maudissez puissament " + ennemi.nom);
                ennemi.vie_max -= 5 + boost;
                ennemi.vie -= 5 + boost;
            }
            default -> System.out.println("vous n'arrivez pas à maudir " + ennemi.nom);
        }
    }

    static private void onde_choc(boolean[] actif, String[] nom, boolean[] assomme, Monstre ennemi) throws IOException {
        for(int i = 0; i < nom.length; i++){
            if(!nom[i].equals(Main.archimage) && actif[i]){
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
        System.out.print(Main.archimage + " : ");
        switch (input.D6()){
            case 2 -> ennemi.do_etourdi();
            case 3, 4 -> ennemi.affecte();
            case 5, 6 -> ennemi.do_assomme();
            default -> System.out.println(ennemi.nom + " n'a pas l'air très affecté...\n");
        }
    }

    /**
     *
     * @param nom_mort le nom du joueur qui se fait ressuciter
     * @return si le mort revient à la vie
     * @throws IOException toujours
     */
    static private boolean popo_rez(String nom_mort) throws IOException {
        if(input.yn("Utilisez vous une potion divine ?")){
            System.out.println(Main.alchimiste + " fait boire à " + nom_mort + " une potion gorgé de l'énergie des dieux.");
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
        int temp = input.D8() + rand.nextInt(2);
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
        for(int i = 0; i < 8; i++){
            if(morts[i] && input.yn(nom[i] + " est mort durant le combat, le reste-t-il/elle ?")) {
                if (nom[i].equals(Main.guerriere) && input.D10() > 6) {
                    System.out.println(nom[i] + " résiste à la mort.\n");
                    return;
                }
                int t;
                if (i < 4) {
                    System.out.println(nom[i] + " se retrouve aux enfers.\n");
                    Main.positions[i] = Position.ENFERS;
                    t = i;
                }
                else {
                    System.out.println(nom[i] + " a rendu l'âme.\n");
                    t = i - 4;
                }
                switch (t) {
                    case 0 -> Main.f_a = 0;
                    case 1 -> Main.f_b = 0;
                    case 2 -> Main.f_c = 0;
                    case 3 -> Main.f_d = 0;
                }
            }
        }
    }
}

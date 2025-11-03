package main;

import Enum.Action;
import Enum.Action_extra;
import Enum.Dieux;
import Enum.Position;
import Exterieur.Input;
import Exterieur.Output;
import Metiers.Joueur;
import Monstre.Monstre;
import Monstre.Race;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Combat {
    
    static Random rand = new Random();
    private static boolean run;
    
    /**
     * Lance un combat entre les joueur et un monstres
     * @param position     le lieu où a lieu l'affrontement
     * @param joueur_force l'indice du joueur qui est attaqué en cas d'embuscade (de 1 à 4) ou -1 sinon
     * @param ennemi       le monstre que les joueurs affronte
     * @throws IOException lecture de terminal
     */
    public static void affrontement(Position position, int joueur_force, Monstre ennemi) throws IOException {
        
        int nb_part = 0;
        
        if (joueur_force != -1) {
            Output.jouerSonAttaque();
        }
        
        // préparer les participants
        for (int i = 0; i < Main.nbj; i++) {
            Main.joueurs[i].init_affrontement(i == joueur_force, position);
            if (Main.joueurs[i].est_actif()) {
                nb_part++;
                if (Main.joueurs[i].a_familier_actif()) {
                    nb_part++;
                }
            }
        }
        
        if (nb_part == 0) {
            System.out.println("Aucun joueur détecté, annulation du combat.");
            return;
        }
        
        // choix du joueur au front
        int pr_l;
        if (joueur_force != -1) {
            pr_l = joueur_force;
            Main.joueurs[pr_l].faire_front(true);
        } else {
            pr_l = getPrL(nb_part);
        }
        
        if (Main.joueurs[pr_l].a_familier_front()) {
            System.out.println("Le familier de " + Main.joueurs[pr_l].getNom() + " se retrouve en première ligne.");
        } else {
            System.out.println(Main.joueurs[pr_l].getNom() + " se retrouve en première ligne.");
        }
        
        if (competence(ennemi, pr_l)) {
            return;
        }
        
        // si l'ennemi à l'avantage de la surprise
        if (joueur_force != -1) {
            ennemi.attaque(Main.joueurs[pr_l]);
        }
        
        combat(ennemi, pr_l, position);
        
        System.out.println("Fin du combat\n");
        gestion_fin_combat(ennemi.est_nomme());
    }
    
    /**
     * Renvoie l'index du joueur qui sera en première ligne
     * @param nbp le nombre de participants
     * @return l'index du joueur en première ligne
     * @throws IOException toujours
     */
    private static int getPrL(int nbp) throws IOException {
        
        // demander gentimment
        if (nbp > 1) {
            for (int i = 0; i < Main.nbj; i++) {
                if (Main.joueurs[i].faire_front(false)) {
                    return i;
                }
            }
        }
        
        // z'avez plus le choix
        int i;
        do {
            i = rand.nextInt(Main.nbj);
        } while (!Main.joueurs[i].faire_front(true));
        return i;
    }
    
    /**
     * Gère le combat d'ascension d'un joueur
     * @param ennemi le monstre a affronter
     * @param grimpeur le joueur qui tente de monter
     * @param est_attaquant si le joueur a l'initiative
     * @return si le joueur grimpe
     * @throws IOException toujours
     */
    public static boolean ascension(Monstre ennemi, Joueur grimpeur, boolean est_attaquant) throws IOException {
        if (!est_attaquant) {
            Output.jouerSonAttaque();
            grimpeur.init_affrontement(true, grimpeur.getPosition());
        } else{
            grimpeur.init_affrontement(false, grimpeur.getPosition());
        }
        
        if (!grimpeur.est_actif()) {
            System.out.println("Aucun joueur détecté, annulation de l'ascension.");
            return false;
        }
        
        grimpeur.faire_front(true);
        
        if (grimpeur.a_familier_front()) {
            System.out.printf("Le familier de %s se retrouve en première ligne.\n", grimpeur.getNom());
        } else {
            System.out.printf("%s se retrouve en première ligne.\n", grimpeur.getNom());
        }
        
        int index = 0;
        for(int i = 0; i < Main.nbj; i++) {
            if(Main.joueurs[i].est_actif()) {
                index = i;
                break;
            }
        }
        
        if (competence(ennemi, index)) { //le monstre fuit
            return true;
        }
        
        if (!est_attaquant) {
            ennemi.attaque(grimpeur);
        }
        
        combat(ennemi, index, grimpeur.getPosition());
        
        System.out.println("Fin du combat\n");
        gestion_fin_combat(ennemi.est_nomme());
        return ennemi.est_vaincu();
    }
    
    /**
     * Gère le combat en appliquant les actions
     * @param ennemi le monstre adverse
     * @param pr_l   index du participant de première ligne
     * @throws IOException et oui
     */
    private static void combat(Monstre ennemi, int pr_l, Position pos) throws IOException {
        
        // on prépare une bijection aléatoire pour l'ordre de jeu
        int[] t = new int[Main.nbj];
        Arrays.fill(t, -1);
        for (int i = 0; i < Main.nbj; ) {
            int temp = rand.nextInt(Main.nbj);
            if (t[temp] == -1) {
                t[temp] = i;
                i++;
            }
        }
        
        combat_start(ennemi, pos);
        
        int i;
        Action act, act_f;
        Action_extra act_ex;
        Joueur joueur;
        boolean skip;
        while (run) {
            
            //chaque joueur
            for (int j = 0; j < Main.nbj && run; j++) {
                skip = false;
                i = t[j];
                joueur = Main.joueurs[i];
                Joueur.debut_tour();
                
                // on ne joue que les participants actifs
                if (!run || joueur.est_pas_activable()) {
                    continue;
                }
                
                joueur.essaie_reveil();
                if (joueur.a_familier_actif()) {
                    joueur.f_essaie_reveil();
                }
                
                // resurection, être assommé, etc.
                if (!joueur.peut_jouer()) {
                    System.out.println(joueur.getNom() + " ne peut pas réaliser d'action dans l'immédiat.");
                    if (joueur.a_familier_actif()) {
                        joueur.familier_seul(ennemi);
                    }
                    joueur.fin_tour_combat();
                    continue;
                }
                if (joueur.a_familier_actif() && joueur.familier_peut_pas_jouer()) {
                    System.out.println("Le familier de " + joueur.getNom() + " ne peut pas réaliser d'action dans " + "l'immédiat.");
                }
                
                // action
                do {
                    act = Input.action(joueur, false);
                    if (act == Action.JOINDRE) {
                        Joueur.joindre(pos);
                    }
                } while (act == Action.JOINDRE && joueur.peut_jouer());
                if (!joueur.peut_jouer()) {
                    act = Action.AUCUNE;
                }
                act_ex = Input.extra(joueur, act);
                int dps_popo = 0;
                switch (act_ex) {
                    case AUCUNE, AUTRE -> {
                    }
                    case ANALYSER -> analyser(joueur, ennemi);
                    case POTION -> dps_popo = joueur.popo();
                    default -> dps_popo = joueur.jouer_extra(act_ex);
                }
                
                //effet de bonus des potions
                if (dps_popo < 0) { //poison sur lame
                    if (act == Action.ATTAQUER) {
                        dps_popo = -dps_popo;
                    } else {
                        dps_popo = 0;
                    }
                }
                if (dps_popo >= 10) { //seule la bombe ou la popo explosive au max peuvent atteindre
                    ennemi.affecte();
                }
                
                switch (act) {
                    case END -> stop_run();
                    case OFF -> {
                        alteration(joueur, pr_l);
                        j -= 1;
                        skip = true;
                    }
                    case TIRER -> {
                        joueur.tirer(ennemi, dps_popo);
                        dps_popo = 0;
                    }
                    case MAGIE -> {
                        System.out.println("Vous utilisez votre magie sur " + ennemi.getNom());
                        ennemi.dommage_magique(Input.magie() + dps_popo);
                        dps_popo = 0;
                    }
                    case FUIR -> joueur.fuir(ennemi);
                    case ASSOMER -> ennemi.assommer(joueur.getBerserk());
                    case ENCAISSER -> ennemi.encaisser();
                    case SOIGNER -> {
                        boolean temp = i == pr_l || Input.ask_heal(pr_l);
                        ennemi.soigner(temp);
                    }
                    case DOMESTIQUER -> {
                        if (ennemi.domestiquer(joueur.bonus_dresser())) {
                            if (ennemi.est_pantin()) {
                                System.out.println("Félicitation, vous avez réussit à domestiquer la cible.");
                                System.out.println("Fin de la simulation, le monstre aurait un niveau d'affection de "
                                        + "1/7.");
                            } else {
                                ennemi.presente_familier();
                                joueur.ajouter_familier();
                                joueur.gagneXp();
                                stop_run();
                            }
                        } else if (ennemi.est_pantin()) {
                            System.out.println("Vous n'avez pas réussit à domestiquer la cible.");
                            System.out.println("Fin de la simulation, le monstre aurait un niveau d'affection de 0/7.");
                        }
                    }
                    case AUTRE -> System.out.println(joueur.getNom() + " fait quelque chose.");
                    case AVANCER -> {
                        pr_l = i;
                        joueur.faire_front(true);
                        ennemi.reset_encaisser();
                        competence_avance(ennemi, Main.joueurs[pr_l]);
                    }
                    case AUCUNE -> {
                    }
                    default -> {
                        if (joueur.traite_action(act, ennemi, dps_popo)) {
                            joueur.attaquer(ennemi, dps_popo);
                            dps_popo = 0;
                        } else if (joueur.action_consomme_popo(act)) {
                            dps_popo = 0;
                        }
                    }
                }
                if (dps_popo > 0) {
                    ennemi.dommage(dps_popo);
                }
                System.out.println();
                if (joueur.a_familier_actif() && run && !skip) {
                    act_f = familier_act(joueur, Input.action(joueur, true));
                    switch (act_f) {
                        case FUIR -> joueur.f_fuir();
                        case AUTRE -> System.out.println("Le famillier de " + joueur.getNom() + " fait quelque chose.");
                        case ENCAISSER -> ennemi.f_encaisser();
                        case AVANCER -> joueur.f_faire_front();
                        case PROTEGER -> joueur.f_proteger(ennemi);
                        case AUCUNE -> {
                        }
                        default -> joueur.f_attaque(ennemi);
                    }
                    joueur.fin_tour_combat();
                    System.out.println();
                }
                
                // s'assure qu'un participant est toujours en première ligne
                if (run && (!Main.joueurs[pr_l].est_actif() || !Main.joueurs[pr_l].est_vivant())) {
                    boolean is_active = false;
                    int k = 0;
                    for (; k < Main.nbj; k++) {
                        Joueur joueur_temp = Main.joueurs[k];
                        if (joueur_temp.est_actif() && joueur_temp.est_vivant()) {
                            is_active = true;
                            break;
                        }
                    }
                    if (!is_active) { // plus de joueur participant
                        stop_run();
                        System.out.println("Aucun joueur détecté en combat.");
                    } else {
                        do {
                            k = rand.nextInt(Main.nbj);
                        } while (!Main.joueurs[k].est_actif() || !Main.joueurs[k].est_vivant());
                        pr_l = k;
                        Main.joueurs[k].faire_front(true);
                    }
                }
                
                if (run) {
                    if (verifie_mort(ennemi, pos)) {
                        stop_run();
                        joueur.dernier_coup();
                    }
                }
            }
            
            // tour de l'adversaire
            if (run) {
                ennemi.attaque(Main.joueurs[pr_l]);
                if (verifie_mort(ennemi, pos)) {
                    stop_run();
                }
                System.out.println();
            }
        }
    }
    
    private static void combat_start(Monstre ennemi, Position pos) throws IOException {
        run = ennemi.check_mort(pos);
    }
    
    /**
     * Stop le combat
     */
    public static void stop_run() {
        run = false;
    }
    
    /**
     * Vérifie si le monstre est mort et en gère les aprés coup
     * @param ennemi le monstre adverse
     * @return true si le monstre est mort, false s'il est en vie
     * @throws IOException toujours
     */
    private static boolean verifie_mort(Monstre ennemi, Position pos) throws IOException {
        if (ennemi.check_mort(pos)) {
            return false;
        }
        // la mort est donné par les méthodes de dommage
        gestion_nomme(ennemi);
        return true;
    }
    
    /**
     * Simule le comportement d'un familier en fonction de son niveau d'obéissance
     * @param joueur le propriétaire du familier
     * @return si le familier joue l'action, un false remplace l'action
     */
    private static Action familier_act(Joueur joueur, Action action) throws IOException {
        if (!joueur.a_familier_actif() || joueur.familier_loyalmax()) {
            return action;
        }
        System.out.println("Vous donnez un ordre à votre familier.");
        int temp = joueur.get_ob_f() + Input.D6() - 3 + rand.nextInt(2); //valeur d'obéissance à l'action
        if (temp <= 1) {
            System.out.println("Le familier de " + joueur.getNom() + " fuit le combat.");
            joueur.f_inactiver();
            return Action.AUCUNE;
        } else if (temp == 2) {
            System.out.println("Le familier de " + joueur.getNom() + " n'écoute pas vos ordres.");
            return Action.AUCUNE;
        } else if (temp <= 4 && action != Action.ATTAQUER) {
            System.out.println("Le familier de " + joueur.getNom() + " ignore vos directives et attaque l'ennemi.");
            return Action.ATTAQUER;
        }
        return action;
    }
    
    /**
     * Gère le retour OFF de l'action, c.-à-d. la mort, l'inconscience ou le retrait du joueur actif ou de celui
     * de première ligne
     * @param joueur le joueur actif
     * @param prl    l'index du joueur de première ligne
     * @throws IOException mon poto
     */
    private static void alteration(Joueur joueur, int prl) throws IOException {
        
        String text = "L'alteration concerne-t-elle :\n\t1: " + joueur.getNom();
        if (joueur.a_familier_actif()) {
            text += "\n\t2: Le familier de " + joueur.getNom();
        }
        Joueur front = Main.joueurs[prl];
        if (front != joueur) {
            text += "\n\t3: " + front.getNom();
            if (front.a_familier_actif()) {
                text += "\n\t4: Le familier de " + front.getNom();
            }
        }
        
        int reponse;
        do {
            System.out.println(text);
            reponse = Input.readInt();
        } while (reponse < 1 || reponse > 4);
        String nom = "";
        switch (reponse) {
            case 1 -> nom = joueur.getNom();
            case 2 -> nom = "le familier de " + joueur.getNom();
            case 3 -> {
                nom = front.getNom();
                joueur = front;
            }
            case 4 -> {
                nom = "le familier de " + front.getNom();
                joueur = front;
            }
        }
        if (reponse == 1 || reponse == 3) {
            joueur.addiction();
        }
        
        //mort
        if (Input.yn(nom + " est-il/elle mort(e) ?")) {
            //on regarde si on peut le ressuciter immédiatement
            if (reponse == 2 || reponse == 4) {
                joueur.f_rendre_mort();
                return;
            }
            int malus = 0;
            for (int k = 0; k < Main.nbj; k++) {
                Joueur j_temp = Main.joueurs[k];
                if (j_temp.peut_ressusciter() && j_temp.peut_jouer()) {
                    if (Input.yn("Est-ce que " + j_temp.getNom() + " veux tenter de ressuciter " + joueur.getNom() +
                            " ?")) {
                        if (j_temp.ressusciter(malus)) {
                            System.out.println(joueur.getNom() + " a été arraché(e) à l'emprise de la mort.");
                            joueur.do_ressucite(malus);
                            j_temp.gagneXp();
                            return;
                        }
                        malus += 1;
                    }
                }
            }
            joueur.rendre_mort();
        }
        
        // assommé
        else if (Input.yn(nom + " est-il/elle inconscient(e) ?")) {
            if (reponse == 2 || reponse == 4) {
                joueur.f_assomme();
            } else {
                joueur.assomme();
            }
        }
        
        // berserk
        else if ((reponse == 1 || reponse == 3) && !joueur.est_berserk() && Input.yn(nom + " devient-il/elle berserk "
                + "?")) {
            joueur.berserk(0.1f + 0.1f * rand.nextInt(3));
        } else if ((reponse == 2 || reponse == 4) && !joueur.f_est_berserk() && Input.yn(nom + " devient-il berserk " + "?")) {
            joueur.f_berserk(0.1f + 0.1f * rand.nextInt(3));
        }
        
        // off
        else if (Input.yn(nom + " est-il/elle hors du combat ?")) {
            if (reponse == 2 || reponse == 4) {
                joueur.f_inactiver();
            } else {
                joueur.inactiver();
            }
        }
    }
    
    /**
     * Gère les compétences du monstre juste avant le combat
     * @param ennemi le monstre qu'affrontent les participants
     * @param pr_l   l'indice du participant en premières lignes
     * @return si le combat s'arrête
     */
    private static boolean competence(Monstre ennemi, int pr_l) throws IOException {
        String nom = Main.joueurs[pr_l].getNom();
        if (Main.joueurs[pr_l].a_familier_front()) {
            nom = "familier de " + nom;
        }
        switch (ennemi.getCompetence()) {
            case DAMNATION_RES -> System.out.println(nom + " perd 2 points de vie pour la durée du combat.");
            case DAMNATION_ATK -> System.out.println(nom + " perd 1 point d'attaque pour la durée du combat.");
            case DAMN_ARES -> {
                for (int i = 0; i < Main.nbj; i++) {
                    if (Main.joueurs[i].getParent() == Dieux.ARES) {
                        System.out.println(ennemi.getNom() + " vous maudit.");
                        System.out.println("Tout descendant d'Arès perd 2 points d'attaque pour la durée du combat.");
                        break;
                    }
                }
            }
            case HATE_DEMETER -> hate(ennemi, Dieux.DEMETER);
            case HATE_DYONISOS -> hate(ennemi, Dieux.DIONYSOS);
            case HATE_POSEIDON -> hate(ennemi, Dieux.POSEIDON);
            case HATE_ZEUS -> hate(ennemi, Dieux.ZEUS);
            case FEAR_ZEUS -> fear(ennemi, Dieux.ZEUS);
            case FEAR_DEMETER -> fear(ennemi, Dieux.DEMETER);
            case FEAR_POSEIDON -> fear(ennemi, Dieux.POSEIDON);
            case FEAR_DYONISOS -> fear(ennemi, Dieux.DIONYSOS);
            case CIBLE_CASQUE -> {
                if (Input.yn(nom + " porte-il/elle un casque ?") && Input.D6() <= 4) {
                    System.out.println(ennemi.getNom() + " fait tomber votre casque pour le combat.");
                }
            }
            case ARMURE_GLACE -> ennemi.boostArmure(1, false);
            case ARMURE_NATURELLE -> ennemi.boostArmure(1, true);
            case ARMURE_GLACE2 -> ennemi.boostArmure(2, false);
            case ARMURE_NATURELLE2 -> ennemi.boostArmure(2, true);
            case ARMURE_NATURELLE3 -> ennemi.boostArmure(3, true);
            case ARMURE_NATURELLE4 -> ennemi.boostArmure(4, true);
            case VITALITE_NATURELLE -> ennemi.boostVie(3, true);
            case VITALITE_NATURELLE2 -> ennemi.boostVie(6, true);
            case VITALITE_NATURELLE3 -> ennemi.boostVie(9, true);
            case FLAMME_ATTAQUE -> ennemi.boostAtk(2, false);
            case FORCE_NATURELLE -> ennemi.boostAtk(2, true);
            case FORCE_NATURELLE2 -> ennemi.boostAtk(4, true);
            case FORCE_NATURELLE3 -> ennemi.boostAtk(6, true);
            case PRUDENT -> { //n'attaque pas s'il se fait OS
                int tolerance = ennemi.getVieMax() + ennemi.getArmure();
                for (int i = 0; i < Main.nbj; i++) {
                    if (Main.joueurs[i].est_actif()) {
                        System.out.print(Main.joueurs[i].getNom() + " ");
                        tolerance -= Input.atk();
                        if (Main.joueurs[i].a_familier_actif()) {
                            System.out.print("Familier de " + Main.joueurs[i].getNom() + " ");
                            tolerance -= Input.atk();
                        }
                        if (tolerance <= 0) {
                            System.out.println(ennemi.getNom() + " fuit en vous voyant avancer.");
                            return true;
                        }
                    }
                }
            }
            case SUSPICIEUX -> { //n'attaque que s'il peut tuer tout le monde en 2 coups
                int tolerance = ennemi.getAtk() * 2;
                for (int i = 0; i < Main.nbj; i++) {
                    if (Main.joueurs[i].est_actif()) {
                        System.out.print(Main.joueurs[i].getNom() + " ");
                        tolerance -= 2 * Input.def();
                        tolerance -= Input.vie();
                        if (Main.joueurs[i].a_familier_actif()) {
                            System.out.print("Familier de " + Main.joueurs[i].getNom() + " ");
                            tolerance -= 2 * Input.def();
                            tolerance -= Input.vie();
                        }
                        if (tolerance < 0) {
                            System.out.println(ennemi.getNom() + " fuit en vous voyant avancer.");
                            return true;
                        }
                    }
                }
            }
            case MEFIANT -> { // fuit s'il n'a pas de meilleures stats
                int tolerance = ennemi.getVieMax() + ennemi.getArmure() * 3 + ennemi.getAtk();
                for (int i = 0; i < Main.nbj; i++) {
                    if (Main.joueurs[i].est_actif()) {
                        System.out.print(Main.joueurs[i].getNom() + " ");
                        tolerance -= 3 * Input.def();
                        tolerance -= Input.vie();
                        tolerance -= Input.atk();
                        if (Main.joueurs[i].a_familier_actif()) {
                            System.out.print("Familier de " + Main.joueurs[i].getNom() + " ");
                            tolerance -= 3 * Input.def();
                            tolerance -= Input.vie();
                            tolerance -= Input.atk();
                        }
                        if (tolerance < 0) {
                            System.out.println(ennemi.getNom() + " fuit en vous voyant avancer.");
                            return true;
                        }
                    }
                }
            }
            case REGARD_APPEURANT -> {
                System.out.println(nom + " croise le regard de " + ennemi.getNom());
                if (Input.D4() < 4) {
                    System.out.println(nom + " est appeuré(e) et perd 1 points d'attaque pour la durée du combat");
                }
            }
            case REGARD_TERRIFIANT -> {
                System.out.println(nom + " croise le regard de " + ennemi.getNom());
                if (Input.D6() <= 5) {
                    System.out.println(nom + " est terrifié(e) et perd 3 points d'attaque pour la durée du combat");
                }
            }
            case FAIBLE -> ennemi.boostAtk(-3, true);
            case BENEDICTION -> {
                System.out.println(ennemi.getNom() + " béni " + nom + " qui gagne définitivement 1 point de " + "r" + "ésistance.");
                System.out.println(ennemi.getNom() + " a disparu...");
                return true;
            }
            case EQUIPE -> {
                System.out.println(ennemi.getNom() + " est lourdement équipé.");
                ennemi.boostAtk(rand.nextInt(3), false);
                ennemi.boostVie(rand.nextInt(5), false);
                ennemi.boostArmure(rand.nextInt(2), false);
            }
            case DUO -> System.out.println("Il y a deux " + ennemi.getNom() + "s !");
            case GEANT -> {
                ennemi.boostAtk(Main.corriger(ennemi.getAtk() * 0.2f), true);
                ennemi.boostVie(Main.corriger((ennemi.getVieMax() * 0.2f) + 2), true);
                ennemi.boostArmure(-1, true);
            }
            case BRUME -> System.out.println(ennemi.getNom() + "crée un rideau de brûme.");
            case GOLEM_PIERRE -> {
                ennemi.golemNom(" de pierre");
                ennemi.boostAtk(rand.nextInt(3) + 1, true);
                ennemi.boostVie(rand.nextInt(4) + 3, true);
                ennemi.boostArmure(rand.nextInt(4) + 1, true);
            }
            case GOLEM_FER -> {
                ennemi.golemNom(" de fer");
                ennemi.boostAtk(rand.nextInt(4) + 2, true);
                ennemi.boostVie(rand.nextInt(8) + 5, true);
                ennemi.boostArmure(rand.nextInt(5) + 2, true);
                ennemi.boostDropMax(1);
            }
            case GOLEM_ACIER -> {
                ennemi.golemNom(" d'acier'");
                ennemi.boostAtk(rand.nextInt(6) + 3, true);
                ennemi.boostVie(rand.nextInt(10) + 7, true);
                ennemi.boostArmure(rand.nextInt(6) + 3, true);
                ennemi.boostDropMax(1);
                ennemi.boostDropMin(1);
                ennemi.boostDrop(1);
            }
            case GOLEM_MITHRIL -> {
                ennemi.golemNom(" de mithril");
                ennemi.boostAtk(rand.nextInt(8) + 4, true);
                ennemi.boostVie(rand.nextInt(12) + 9, true);
                ennemi.boostArmure(rand.nextInt(6) + 4, true);
                ennemi.boostDropMax(2);
                ennemi.boostDropMin(1);
                ennemi.boostDrop(2);
            }
        }
        return false;
    }
    
    /**
     * Applique les compétence de type HATE des monstres
     * @param ennemi le monstre en question
     * @param dieu   le dieu qu'il haït
     */
    private static void hate(Monstre ennemi, Dieux dieu) {
        for (int i = 0; i < Main.nbj; i++) {
            if (Main.joueurs[i].getParent() == dieu) {
                System.out.println(ennemi.getNom() + " vous regarde avec haine.");
                ennemi.boostAtk(1 + rand.nextInt(2), false);
                return;
            }
        }
    }
    
    /**
     * Applique les compétence de type FEAR des monstres
     * @param ennemi le monstre en question
     * @param dieu   le dieu qu'il craint
     */
    private static void fear(Monstre ennemi, Dieux dieu) {
        for (int i = 0; i < Main.nbj; i++) {
            if (Main.joueurs[i].getParent() == dieu) {
                System.out.println(ennemi.getNom() + " vous crains.");
                ennemi.boostAtk(-1 - rand.nextInt(2), false);
            }
        }
    }
    
    /**
     * Gère les compétences de l'ennemi lors d'un changement de position
     * @param ennemi le monstre ennemi
     * @param joueur l'unité en première ligne
     * @throws IOException ça va mon pote ?
     */
    static void competence_avance(Monstre ennemi, Joueur joueur) throws IOException {
        switch (ennemi.getCompetence()) {
            case ASSAUT -> {
                System.out.println(ennemi.getNom() + " se jete sur " + joueur.getNom() + " avant que vous ne vous en "
                        + "rendiez compte");
                ennemi.attaque(joueur);
            }
            case CHANT_SIRENE ->
                    System.out.println("Le chant de " + ennemi.getNom() + " perturbe " + joueur.getNom() + " qui " +
                            "perd" + " 1 point d'attaque pour la durée du combat.");
        }
    }
    
    /**
     * Suprimme le monstre de sa zone après sa mort s'il est nommé
     * @param ennemi le monstre ennemi
     * @implNote ne couvre que les monstres nommés
     */
    static void gestion_nomme(Monstre ennemi) {
        if (ennemi.est_nomme()) {
            Output.dismiss_race(ennemi.getNom());
            Race.delete_monstre(ennemi.getNom());
        }
    }
    
    /**
     * Analyse le monstre ennemi et écrit ses stats aux joueurs
     * @param joueur le joueur réalisant l'analyste
     * @param ennemi le monstre analysé
     * @throws IOException ce bon vieux throws
     */
    static private void analyser(Joueur joueur, Monstre ennemi) throws IOException {
        System.out.println("Vous analysez le monstre en face de vous.");
        int jet = Input.D8() + rand.nextInt(2);
        jet += joueur.bonus_analyse();
        int pv, pvm, arm, atk;
        switch (ennemi.getCompetence()) {
            case ILLU_AURAI -> {
                pvm = Race.aurai_malefique.get_vie();
                pv = pvm - (ennemi.getVieMax() - ennemi.getVie());
                arm = Race.aurai_malefique.get_armure();
                atk = Race.aurai_malefique.get_attaque();
            }
            case ILLU_CYCLOPE -> {
                pvm = Race.cyclope.get_vie();
                pv = pvm - (ennemi.getVieMax() - ennemi.getVie());
                arm = Race.cyclope.get_armure();
                atk = Race.cyclope.get_attaque();
            }
            case ILLU_DULLA -> {
                pvm = Race.dullahan.get_vie();
                pv = pvm - (ennemi.getVieMax() - ennemi.getVie());
                arm = Race.dullahan.get_armure();
                atk = Race.dullahan.get_attaque();
            }
            case ILLU_GOLEM -> {
                pvm = Race.golem.get_vie();
                pv = pvm - (ennemi.getVieMax() - ennemi.getVie());
                arm = Race.golem.get_armure();
                atk = Race.golem.get_attaque();
            }
            case ILLU_ROCHE -> {
                pvm = Race.roche_maudite.get_vie();
                pv = pvm - (ennemi.getVieMax() - ennemi.getVie());
                arm = Race.roche_maudite.get_armure();
                atk = Race.roche_maudite.get_attaque();
            }
            case ILLU_SIRENE -> {
                pvm = Race.sirene.get_vie();
                pv = pvm - (ennemi.getVieMax() - ennemi.getVie());
                arm = Race.sirene.get_armure();
                atk = Race.sirene.get_attaque();
            }
            case ILLU_TRITON -> {
                pvm = Race.triton.get_vie();
                pv = pvm - (ennemi.getVieMax() - ennemi.getVie());
                arm = Race.triton.get_armure();
                atk = Race.triton.get_attaque();
            }
            case ILLU_VENTI -> {
                pvm = Race.venti.get_vie();
                pv = pvm - (ennemi.getVieMax() - ennemi.getVie());
                arm = Race.venti.get_armure();
                atk = Race.venti.get_attaque();
            }
            default -> {
                pvm = ennemi.getVieMax();
                pv = ennemi.getVie();
                arm = ennemi.getArmure();
                atk = ennemi.getAtk();
            }
        }
        System.out.println(ennemi.getNom() + " :");
        if (ennemi.est_pantin()) {
            System.out.println("vie : " + (jet >= 5 ? "∞" : "???") + "/" + (jet >= 2 ? "∞" : "???"));
        } else {
            System.out.println("vie : " + (jet >= 5 ? pv : "???") + "/" + (jet >= 2 ? pvm : "???"));
        }
        System.out.println("attaque : " + (jet >= 3 ? atk : "???"));
        System.out.println("armure : " + (jet >= 7 ? arm : "???"));
        System.out.println();
    }
    
    /**
     * Traite les joueurs après la fin du combat
     * @param ennemi_nomme si l'ennemi etait un monstre nommé (bonus d'xp)
     * @throws IOException toujours
     */
    static private void gestion_fin_combat(boolean ennemi_nomme) throws IOException {
        for (int i = 0; i < Main.nbj; i++) {
            Main.joueurs[i].fin_affrontement(ennemi_nomme);
        }
    }
}

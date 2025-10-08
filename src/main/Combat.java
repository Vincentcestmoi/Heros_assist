package main;

import Exterieur.Input;
import Exterieur.Output;
import Metiers.Joueur;
import Enum.Metier;
import Metiers.Sort;
import Monstre.Monstre;
import Monstre.Race;
import Enum.Position;
import Enum.Action;
import Enum.Competence;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Random;

public class Combat {
    
    static Random rand = new Random();

    /**
     * Lance un combat entre les joueur et un monstres
     * @param position     le lieu où a lieu l'affrontement
     * @param joueur_force l'indice du joueur qui est attaqué en cas d'embuscade (de 1 à 4) ou -1 sinon
     * @param ennemi       le monstre que les joueurs affronte
     * @throws IOException lecture de terminal
     */
    public static void affrontement(Position position, int joueur_force, Monstre ennemi) throws IOException {

        int nb_part = 0;

        // préparer les participants
        for (int i = 0; i < Main.nbj; i++) {
            Main.joueurs[i].init_affrontement(i == joueur_force, position);
            if(Main.joueurs[i].est_actif()){
                nb_part++;
                if(Main.joueurs[i].a_familier_actif()){
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
        if(joueur_force != -1){
            pr_l = joueur_force;
            Main.joueurs[pr_l].faire_front(true);
        }
        else {
            pr_l = getPrL(nb_part);
        }

        if(Main.joueurs[pr_l].a_familier_front()){
            System.out.println("Le familier de " + Main.joueurs[pr_l].getNom() + " se retrouve en première ligne.\n");
        }
        else{
            System.out.println(Main.joueurs[pr_l].getNom() + " se retrouve en première ligne.\n");
        }

        if (competence(ennemi, pr_l)) {
            return;
        }

        // si l'ennemi à l'avantage de la surprise
        if (joueur_force != -1) {
            ennemi.attaque(Main.joueurs[pr_l].getNom());
        }

        System.out.println();
        combat(ennemi, pr_l);

        System.out.println("Fin du combat\n");
        gestion_mort_end();
    }

    /**
     * Renvoie l'index du joueur qui sera en première ligne
     * @param nbp   le nombre de participants
     * @return l'index du joueur en première ligne
     * @throws IOException toujours
     */
    private static int getPrL(int nbp) throws IOException {

        // demander gentimment
        if (nbp > 1) {
            for (int i = 0; i <= Main.nbj ; i++) {
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
     * Gère le combat en appliquant les actions
     *
     * @param ennemi le monstre adverse
     * @param pr_l   index du participant de première ligne
     * @throws IOException et oui
     */
    private static void combat(Monstre ennemi, int pr_l) throws IOException {

        // on prépare une bijection aléatoire pour l'ordre de jeu
        int[] t = new int[Main.nbj];
        Arrays.fill(t, -1);
        for (int i = 0; i < Main.nbj;) {
            int temp = rand.nextInt(Main.nbj);
            if (t[temp] == -1) {
                t[temp] = i;
                i++;
            }
        }

        int i;
        Action act, act_f = Action.AUCUNE;
        Joueur joueur;
        boolean run = ennemi.check_mort();
        while (run) {

            //chaque joueur
            for (int j = 0; j < Main.nbj; j++) {
                i = t[j];
                joueur = Main.joueurs[j];

                // on ne joue que les participants actifs
                if (joueur.est_pas_activable()) {
                    continue;
                }

                joueur.essaie_reveil();
                if(joueur.a_familier()) {
                    joueur.f_essaie_reveil();
                }

                // resurection, être assommé, etc.
                if (!joueur.peut_jouer()) {
                    System.out.println(joueur.getNom() + " ne peut pas réaliser d'action dans l'immédiat.");
                    if(joueur.a_familier()) {
                        joueur.familier_seul(ennemi);
                    }
                    joueur.fin_tour_combat();
                    continue;
                }
                if(joueur.a_familier() && joueur.familier_peut_pas_jouer()) {
                    System.out.println("Le familier de " + joueur.getNom() + " ne peut pas réaliser d'action dans l'immédiat.");
                }


                // action
                act = Input.action(joueur, false);
                if(joueur.a_familier()) {
                    act_f = familier_act(joueur, Input.action(joueur, true));
                }
                switch (act) {
                    case OFF -> {
                        alteration(joueur);
                        System.out.println();
                        if (i != pr_l) {
                            alteration(Main.joueurs[pr_l]);
                            if (joueur.est_actif()) {
                                j--;
                            }
                        }
                    }
                    case END -> {
                        return;
                    }
                    case RETOUR -> {
                        int k = j;
                        do {
                            k = k == 0 ? 7 : k - 1;
                        } while (Main.joueurs[t[k]].est_pas_activable());
                        j = t[k];
                    }

                    case TIRER -> joueur.tirer(ennemi);
                    case MAGIE -> {
                        System.out.println("Vous utilisez votre magie sur " + ennemi.getNom());
                        ennemi.dommage_magique(Input.magie());
                    }
                    case FUIR -> joueur.fuir();
                    case ASSOMER -> ennemi.assommer(joueur.getBerserk());
                    case ENCAISSER -> ennemi.encaisser();
                    case SOIGNER -> {
                        boolean temp = i == pr_l || Input.ask_heal(pr_l);
                        ennemi.soigner(temp);
                    }
                    case DOMESTIQUER -> {
                        if (ennemi.domestiquer()) {
                            ennemi.presente_familier();
                            return;
                        }
                    }
                    case ANALYSER -> analyser(i == pr_l, ennemi);
                    case AUTRE -> System.out.println(joueur.getNom() + " fait quelque chose.");
                    case AVANCER -> {
                        pr_l = i;
                        joueur.faire_front(true);
                        ennemi.reset_encaisser();
                        competence_avance(ennemi, Main.joueurs[pr_l].getNom());
                    }
                    default -> {
                        if (joueur.traite_action(act, ennemi)) {
                            joueur.attaquer(ennemi);
                        }
                    }
                }
                if(joueur.a_familier()) {
                    System.out.println("Le familier de " + joueur.getNom() + " agis.");
                    switch (act_f) {
                        case FUIR -> joueur.f_fuir();
                        case AUTRE -> System.out.println("Le famillier de " + joueur.getNom() + " fait quelque chose.");
                        case ENCAISSER -> ennemi.f_encaisser();
                        case AVANCER -> joueur.f_faire_front();
                        case PROTEGER -> joueur.f_proteger(ennemi);
                        default -> joueur.f_attaque(ennemi);
                    }
                }

                // s'assure qu'un participant est toujours en première ligne
                if (!Main.joueurs[pr_l].est_actif() || !Main.joueurs[pr_l].est_vivant()) {
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
                        run = false;
                        System.out.println("Aucun joueur détecté en combat.");
                        // break inutile, car actif[i] toujours à false
                    } else {
                        do {
                            k = rand.nextInt(Main.nbj);
                        } while (!Main.joueurs[k].est_actif() || !Main.joueurs[k].est_vivant());
                        pr_l = k;
                        Main.joueurs[k].faire_front(true);
                    }
                }

                int temp = verifie_mort(ennemi);
                if(temp != -2){
                    return;
                }
            }

            // tour de l'adversaire
            if (run) {
                ennemi.attaque(Main.joueurs[pr_l].getNom());
                int temp = verifie_mort(ennemi);
                if(temp != -2){
                    return;
                }
            }
        }
    }

    /**
     * Vérifie si le monstre est mort et en gère les aprés coup
     * @param ennemi le monstre adverse
     * @return -2 si le monstre est en vie, -1 s'il est mort, l'index du joueur qui l'a domestiqué sinon
     * @throws IOException toujours
     */
    private static int verifie_mort(Monstre ennemi) throws IOException {
        if (ennemi.check_mort()) {
            return -2;
        }
        // la mort est donné par les méthodes de dommage
        gestion_nomme(ennemi);
        return -1;
    }/*

        //le nécromancien peut tenter de ressuciter le monstre
        int etat = 15 + rand.nextInt(12);
        for (int k = 0; k < Main.nbj; k++) {
            if (etat > 0 && actif[k] && Main.metier[k] == Enum.Metier.NECROMANCIEN) {
                if (Exterieur.Input.yn("Voulez vous tenter de ressuciter " + ennemi.nom + " en tant que familier pour 2PP ?")) {
                    int temp = ressuciter(ennemi, etat);
                    etat = etat + temp;
                    if (temp != 0) {
                        if (!Exterieur.Input.yn("Tuez vous le familier que vous venez de ramener à la vie pour gagner 4PP ?")) {
                            return k;
                        }
                        etat -= rand.nextInt(4) + 3;
                        if (etat < 0) {
                            System.out.println("Le cadavre du monstre n'est plus qu'une masse informe et inutilisable.");
                            return -1;
                        }
                    }
                }
            }
        }
        for (int k = 0; k < Main.nbj; k++) {
            if (etat > 0 && actif[k] && Main.metier[k] == Enum.Metier.ALCHIMISTE) {
                if (Exterieur.Input.yn("Voulez vous dissequer le cadavre ?")) {
                    etat += Metiers.Sort.dissection(etat);
                    if (etat < 0) {
                        System.out.println("Le cadavre du monstre n'est plus qu'une masse informe et inutilisable.");
                        return -1;
                    }
                }
            }
        }
        if (etat == 0 || pos == Enum.Position.ENFERS || pos == Enum.Position.OLYMPE || pos == Enum.Position.ASCENDANT) {
            return -1;
        }
        System.out.println("Vous pouvez vendre le cadavre de " + ennemi.nom + " pour " + (1 + (etat - 1) / 10) + " PO.");
        return -1;
    }*/

    /**
     * Simule le comportement d'un familier en fonction de son niveau d'obéissance
     *
     * @param joueur le propriétaire du familier
     * @return si le familier joue l'action, un false remplace l'action
     * @implNote un joueur peut être entré avec une obéissance de 0.
     */
    private static Action familier_act(Joueur joueur, Action action) throws IOException {
        if(!joueur.a_familier()){
            return action;
        }
        int temp = joueur.get_ob_f() + Input.D6() - 3 + rand.nextInt(2); //valeur d'obéissance à l'action
        if (temp <= 1) {
            System.out.println("Le familier de " + joueur.getNom() + " fuit le combat.");
            joueur.f_inactiver();
            return Action.AUTRE;
        }
        else if (temp == 2) {
            System.out.println("Le familier de " + joueur.getNom() + " n'écoute pas vos ordres.");
            return Action.AUTRE;
        }
        else if (temp <= 4 && action != Action.ATTAQUER) {
            System.out.println("Le familier de " + joueur.getNom() + " ignore vos directives et attaque l'ennemi.");
            return Action.ATTAQUER;
        }
        return action;
    }

    /**
     * Gère le retour OFF de l'action, c.-à-d. la mort, l'inconscience ou le retrait du joueur actif ou de celui
     * de première ligne
     *
     * @param joueur le joueur auquel on s'intéresse
     * @throws IOException mon poto
     */
    private static void alteration(Joueur joueur) throws IOException {

        //mort
        if (Input.yn(joueur.getNom() + " est-il/elle mort(e) ?")) {
            //on regarde si on peut le ressuciter immédiatement
            int malus = 0;
            for (int k = 0; k < Main.nbj; k++) {
                Joueur j_temp = Main.joueurs[k];
                if (j_temp.peut_ressuciter() && j_temp.peut_jouer()) {
                    if (Input.yn("Est-ce que " + j_temp.getNom() + " veux tenter de ressuciter " + joueur.getNom() + " ?")) {
                        if (j_temp.ressuciter(malus)) {
                            System.out.println(joueur.getNom() + " a été arraché(e) à l'emprise de la mort.");
                            joueur.do_ressucite(malus);
                            return;
                        }
                        malus += 1;
                    }
                }
            }
            joueur.rendre_mort();
        }
        if(Input.yn("Le familier de " + joueur.getNom() + " est-il mort ?")){
            joueur.f_rendre_mort();
            return;
        }

        // TODO : déplacer
        if (joueur.getMetier() == Metier.ARCHIMAGE && Input.yn("Le mana de " + joueur.getNom() + " est-il tombé à 0 ?")) {
            if(Sort.addiction()){
                joueur.assomme();
            }
        }

        // assommé
        else if (Input.yn(joueur.getNom() + " est-il/elle inconscient(e) ?")) {
            joueur.assomme();
        }
        if (Input.yn("Le familier de " + joueur.getNom() + " est-il inconscient ?")) {
            joueur.f_assomme();
        }

        // berserk
        else if (!joueur.est_berserk() && Input.yn(joueur.getNom() + " devient-il/elle berserk ?")) {
            joueur.berserk(0.1f + 0.1f * rand.nextInt(3));
        }
        else if (!joueur.f_est_berserk() && Input.yn("Le familier de " + joueur.getNom() + " devient-il berserk ?")) {
            joueur.f_berserk(0.1f + 0.1f * rand.nextInt(3));
        }

        // off
        else if (Input.yn(joueur.getNom() + " est-il/elle hors du combat ?")) {
            joueur.inactiver();
        }
        else if (Input.yn("Le familier de " + joueur.getNom() + " est-il/elle hors du combat ?")) {
            joueur.f_inactiver();
        }
    }

    /**
     * Gère les compétences du monstre juste avant le combat
     *
     * @param ennemi le monstre qu'affrontent les participants
     * @param pr_l   l'indice du participant en premières lignes
     * @return si le combat s'arrête
     */
    private static boolean competence(Monstre ennemi, int pr_l) throws IOException {
        String nom = Main.joueurs[pr_l].getNom();
        if(Main.joueurs[pr_l].a_familier_front()){
            nom = "familier de " + nom;
        }
        switch (ennemi.getCompetence()) {
            case DAMNATION_RES -> System.out.println(nom + " perd 2 points de vie pour la durée du combat.");
            case DAMNATION_ATK -> System.out.println(nom + " perd 1 point d'attaque pour la durée du combat.");
            case DAMN_ARES -> {
                if (Input.yn("Un de vous est-il descendant d'Ares ?")) {
                    System.out.println(ennemi.getNom() + " vous maudit.");
                    System.out.println("Tout descendant d'Arès perd 2 points d'attaque pour la durée du combat.");
                }
            }
            case HATE_DEMETER -> hate(ennemi, "Demeter");
            case HATE_DYONISOS -> hate(ennemi, "Dyonisos");
            case HATE_POSEIDON -> hate(ennemi, "Poseidon");
            case HATE_ZEUS -> hate(ennemi, "Zeus");
            case FEAR_ZEUS -> fear(ennemi, "Zeus");
            case FEAR_DEMETER -> fear(ennemi, "Demeter");
            case FEAR_POSEIDON -> fear(ennemi, "Poseidon");
            case FEAR_HYPNOS -> fear(ennemi, "hypnos");
            case FEAR_DYONISOS -> fear(ennemi, "Dyonisos");
            case CIBLE_CASQUE -> {
                if (Input.yn(nom + " porte-il/elle un casque ?") && Input.D6() <= 4) {
                    System.out.println(ennemi.getNom() + " fait tomber votre casque pour le combat.");
                }
            }
            case ARMURE_GLACE -> ennemi.bostArmure(1, false);
            case ARMURE_NATURELLE -> ennemi.bostArmure(1, true);
            case ARMURE_GLACE2 -> ennemi.bostArmure(2, false);
            case ARMURE_NATURELLE2 -> ennemi.bostArmure(2, true);
            case ARMURE_NATURELLE3 -> ennemi.bostArmure(3, true);
            case ARMURE_NATURELLE4 -> ennemi.bostArmure(4, true);
            case VITALITE_NATURELLE -> ennemi.bostVie(3, true);
            case VITALITE_NATURELLE2 -> ennemi.bostVie(6, true);
            case VITALITE_NATURELLE3 -> ennemi.bostVie(9, true);
            case FLAMME_ATTAQUE -> ennemi.bostAtk(2, false);
            case FORCE_NATURELLE -> ennemi.bostAtk(2, true);
            case FORCE_NATURELLE2 -> ennemi.bostAtk(4, true);
            case FORCE_NATURELLE3 -> ennemi.bostAtk(6, true);
            case PRUDENT -> {
                int tolerance = ennemi.getVieMax();
                for (int i = 0; i < Main.nbj; i++) {
                    if (Main.joueurs[i].est_actif()) {
                        System.out.print(Main.joueurs[i].getNom() + " ");
                        tolerance -= Input.atk();
                        if(Main.joueurs[i].a_familier_actif()){
                            System.out.print("Familier de " + Main.joueurs[i].getNom() + " ");
                            tolerance -= Input.atk();
                        }
                        if (tolerance < 0) {
                            System.out.println(ennemi.getNom() + " fuit en vous voyant avancer.");
                            return true;
                        }
                    }
                }
            }
            case SUSPICIEUX -> {
                int tolerance = ennemi.getAtk() * 2;
                for (int i = 0; i < Main.nbj; i++) {
                    if (Main.joueurs[i].est_actif()) {
                        System.out.print(Main.joueurs[i].getNom() + " ");
                        tolerance -= 2 * Input.def();
                        tolerance -= Input.vie();
                        if(Main.joueurs[i].a_familier_actif()){
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
            case MEFIANT -> {
                int tolerance = ennemi.getVieMax() + ennemi.getArmure() * 3 + ennemi.getAtk();
                for (int i = 0; i < Main.nbj; i++) {
                    if (Main.joueurs[i].est_actif()) {
                        System.out.print(Main.joueurs[i].getNom() + " ");
                        tolerance -= 3 * Input.def();
                        tolerance -= Input.vie();
                        tolerance -= Input.atk();
                        if(Main.joueurs[i].a_familier_actif()){
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
                if (Input.D4() != 4) {
                    System.out.println(nom + " est appeuré(e) et perd 1 points d'attaque pour la durée du combat");
                }
            }
            case REGARD_TERRIFIANT -> {
                System.out.println(nom + " croise le regard de " + ennemi.getNom());
                if (Input.D6() <= 5) {
                    System.out.println(nom + " est terrifié(e) et perd 3 points d'attaque pour la durée du combat");
                }
            }
            case FAIBLE -> ennemi.bostAtk(-3, true);
            case BENEDICTION -> {
                System.out.println(ennemi.getNom() + " béni " + nom + " qui gagne définitivement 1 point de résistance.");
                System.out.println(ennemi.getNom() + " a disparu...");
                return true;
            }
            case EQUIPE -> {
                System.out.println(ennemi.getNom() + " est lourdement équipé.");
                ennemi.bostAtk(rand.nextInt(3), false);
                ennemi.bostVie(rand.nextInt(5), false);
                ennemi.bostArmure(rand.nextInt(2), false);
            }
            case DUO -> System.out.println("Il y a deux " + ennemi.getNom() + "s !");
            case GEANT -> {
                ennemi.bostAtk(Main.corriger(ennemi.getAtk() * 0.2f), true);
                ennemi.bostVie(Main.corriger((ennemi.getVieMax() * 0.2f) + 2), true);
                ennemi.bostArmure(-1, true);
            }
            case BRUME ->
                    System.out.println(ennemi.getNom() + "crée un rideau de brûme, tous les participants perdent 1 point d'attaque pour la durée du combat.");
            case GOLEM_PIERRE -> {
                ennemi.golemNom(" de pierre");
                ennemi.bostAtk(rand.nextInt(3) + 1, true);
                ennemi.bostVie(rand.nextInt(4) + 3, true);
                ennemi.bostArmure(rand.nextInt(4) + 1, true);
            }
            case GOLEM_FER -> {
                ennemi.golemNom(" de fer");
                ennemi.bostAtk(rand.nextInt(4) + 2, true);
                ennemi.bostVie(rand.nextInt(8) + 5, true);
                ennemi.bostArmure(rand.nextInt(5) + 2, true);
                ennemi.bostDropMax(1);
            }
            case GOLEM_ACIER -> {
                ennemi.golemNom(" d'acier'");
                ennemi.bostAtk(rand.nextInt(6) + 3, true);
                ennemi.bostVie(rand.nextInt(10) + 7, true);
                ennemi.bostArmure(rand.nextInt(6) + 3, true);
                ennemi.bostDropMax(1);
                ennemi.bostDropMin(1);
                ennemi.bostDrop(1);
            }
            case GOLEM_MITHRIL -> {
                ennemi.golemNom(" de mithril");
                ennemi.bostAtk(rand.nextInt(8) + 4, true);
                ennemi.bostVie(rand.nextInt(12) + 9, true);
                ennemi.bostArmure(rand.nextInt(6) + 4, true);
                ennemi.bostDropMax(2);
                ennemi.bostDropMax(1);
                ennemi.bostDrop(2);
            }
        }
        return false;
    }

    /**
     * Applique les compétence de type HATE des monstres
     * @param ennemi le monstre en question
     * @param dieu le nom du dieu qu'il haït
     * @throws IOException toujours
     */
    private static void hate(Monstre ennemi, String dieu) throws IOException {
        if (Input.yn("Un de vous est-il descendant de " + dieu + " ?")) {
            System.out.println(ennemi.getNom() + " vous regarde avec haine.");
            ennemi.bostAtk(1, false);
        }
    }

    /**
     * Applique les compétence de type FEAR des monstres
     * @param ennemi le monstre en question
     * @param dieu le nom du dieu qu'il craint
     * @throws IOException toujours
     */
    private static void fear(Monstre ennemi, String dieu) throws IOException {
        if (Input.yn("Un de vous est-il descendant de " + dieu + " ?")) {
            System.out.println(ennemi.getNom() + " vous crains.");
            ennemi.bostAtk(-1, false);
        }
    }

    /**
     * Gère les compétences de l'ennemi lors d'un changement de position
     *
     * @param ennemi   le monstre ennemi
     * @param nom_pr_l le nom de l'unité en première ligne
     * @throws IOException ça va mon pote ?
     */
    static void competence_avance(Monstre ennemi, String nom_pr_l) throws IOException {
        switch (ennemi.getCompetence()) {
            case ASSAUT -> {
                System.out.println(ennemi.getNom() + " se jete sur " + nom_pr_l + " avant que vous ne vous en rendiez compte");
                ennemi.attaque(nom_pr_l);
            }
            case CHANT_SIRENE ->
                    System.out.println("Le chant de " + ennemi.getNom() + " perturbe " + nom_pr_l + " qui perd 1 point d'attaque pour la durée du combat.");
        }
    }

    /**
     * Suprimme le monstre de sa zone après sa mort s'il est nommé
     *
     * @param ennemi le monstre ennemi
     * @implNote ne couvre que les monstres nommés
     */
    static void gestion_nomme(Monstre ennemi) {
        if(EnumSet.of(Competence.PRUDENT, Competence.MEFIANT, Competence.SUSPICIEUX, Competence.CHRONOS).contains(ennemi.getCompetence()))  {
            Output.dismiss_race(ennemi.getNom());
            Race.delete_monstre(ennemi.getNom());
        }
    }

    /**
     * Fournie la liste à laquelle appartient le monstre
     *
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
            case "Python", "Echidna" -> list = Race.temple;
            case "Scylla", "Charibe" -> list = Race.mer;
            case "Typhon", "l'Aigle du Caucase" -> list = Race.mont;
            case "Chronos" -> list = Race.olympe;
        }
        return list;
    }

    /**
     * Fournie le nom de la liste à laquelle appartient le monstre
     *
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
     *
     * @param ennemi le monstre à ressuciter
     * @param etat la qualité du cadavre
     * @return la variation de l'état du familier, ou 0 si le sort a échoué
     * @throws IOException pour l'Exterieur.Input
     */
    private static int ressuciter(Monstre ennemi, int etat) throws IOException {
        int jet = Input.D8() + (etat - 10) / 2;
        if(jet > 8){
            jet = 8;
        }
        int retour;
        switch (jet) {
            case 3 -> { //-8
                System.out.println(ennemi.getNom() + " a été... rapellé");
                ennemi.bostAtk(-6, true);
                ennemi.bostVie(-8, true);
                ennemi.bostArmure(-3, true);
                retour = -rand.nextInt(6) - 7;
            }
            case 4, 5 -> { //-5
                System.out.println(ennemi.getNom() + " a été partiellement ressucité");
                ennemi.bostAtk(-3, true);
                ennemi.bostVie(-5, true);
                ennemi.bostArmure(-2, true);
                retour = -rand.nextInt(6) - 4;
            }
            case 6, 7 -> { // -2
                System.out.println(ennemi.getNom() + " a été ressucité");
                ennemi.bostAtk(-1, true);
                ennemi.bostVie(-2, true);
                retour = -rand.nextInt(6) - 1;
            }
            case 8 -> { //+2
                System.out.println(ennemi.getNom() + " a été parfaitement ressucité");
                ennemi.bostAtk(1, true);
                ennemi.bostVie(2, true);
                retour = 1;
            }
            default -> {
                System.out.println("échec du sort.");
                retour = 0;
            }
        }
        return retour;
    }

    /**
     * Analyse le monstre ennemi et écrit ses stats aux joueurs
     *
     * @param is_prl booléan indiquant si l'analyste est en première ligne
     * @param ennemi le monstre analysé
     * @throws IOException ce bon vieux throws
     */
    static private void analyser(boolean is_prl, Monstre ennemi) throws IOException {
        System.out.println("Vous analysez le monstre en face de vous.");
        int temp = Input.D8() + rand.nextInt(2);
        if (!is_prl) {
            temp -= 2; //malus si en seconde ligne
        }
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
        System.out.println("vie : " + (temp >= 5 ? pv : "???") + "/" + (temp >= 2 ? pvm : "???"));
        System.out.println("attaque : " + (temp >= 3 ? atk : "???"));
        System.out.println("armure : " + (temp >= 7 ? arm : "???"));
    }

    static private void gestion_mort_end() throws IOException {/* TODO
        for (int i = 0; i < Main.nbj; i++) {
            if (morts[i] && Exterieur.Input.yn(nom[i] + " est mort durant le combat, le reste-t-il/elle ?")) {
                if ((Main.metier[i] == Enum.Metier.GUERRIERE && Exterieur.Input.D10() > 6)
                        || (Main.metier[i] == Enum.Metier.SHAMAN && Exterieur.Input.D10() > 8)) {
                    System.out.println(nom[i] + " résiste à la mort.\n");
                    return;
                }
                int t;
                if (i < Main.nbj) {
                    System.out.println(nom[i] + " se retrouve aux enfers.\n");
                    Main.positions[i] = Enum.Position.ENFERS;
                    t = i;
                }
                else {
                    System.out.println(nom[i] + " a rendu l'âme.\n");
                    t = i - Main.nbj;
                }
                Main.f[t] = 0;
            }
        }*/
    }
}

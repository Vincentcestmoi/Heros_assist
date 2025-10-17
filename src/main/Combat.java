package main;

import Exterieur.Input;
import Exterieur.Output;
import Metiers.Joueur;
import Monstre.Monstre;
import Monstre.Race;
import Enum.Position;
import Enum.Action;
import Enum.Action_extra;
import Enum.Competence;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
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
            System.out.println("Le familier de " + Main.joueurs[pr_l].getNom() + " se retrouve en première ligne.");
        }
        else{
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

        System.out.println("Fin du combat");
        gestion_fin_combat();
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
            for (int i = 0; i < Main.nbj ; i++) {
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
    private static void combat(Monstre ennemi, int pr_l, Position pos) throws IOException {

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

        combat_start(ennemi, pos);

        int i;
        Action act, act_f;
        Action_extra act_ex;
        Joueur joueur;
        while (run) {

            //chaque joueur
            for (int j = 0; j < Main.nbj && run; j++) {
                i = t[j];
                joueur = Main.joueurs[j];
                Joueur.debut_tour();

                // on ne joue que les participants actifs
                if (!run || joueur.est_pas_activable()) {
                    continue;
                }

                joueur.essaie_reveil();
                if(joueur.a_familier_actif()) {
                    joueur.f_essaie_reveil();
                }

                // resurection, être assommé, etc.
                if (!joueur.peut_jouer()) {
                    System.out.println(joueur.getNom() + " ne peut pas réaliser d'action dans l'immédiat.");
                    if(joueur.a_familier_actif()) {
                        joueur.familier_seul(ennemi);
                    }
                    joueur.fin_tour_combat();
                    continue;
                }
                if(joueur.a_familier_actif() && joueur.familier_peut_pas_jouer()) {
                    System.out.println("Le familier de " + joueur.getNom() + " ne peut pas réaliser d'action dans l'immédiat.");
                }


                // action
                do {
                    act = Input.action(joueur, false);
                    if(act == Action.OFF){
                        alteration(joueur, pr_l);
                    }
                }while(act == Action.OFF && joueur.peut_jouer());
                if(!joueur.peut_jouer()){
                    act = Action.AUCUNE;
                }
                act_ex = Input.extra(joueur, act);
                int dps_popo = 0;
                switch(act_ex){
                    case AUCUNE, AUTRE -> {}
                    case RAGE -> joueur.rage();
                    case ANALYSER -> analyser(joueur.est_front(), ennemi);
                    case POTION -> {
                        dps_popo = joueur.popo();
                        if(dps_popo < 0){ //poison sur lame
                            if(act == Action.ATTAQUER){
                                dps_popo = -dps_popo;
                            }
                            else{
                                dps_popo = 0;
                            }
                        }
                        if(dps_popo >= 10){ //seule la bombe ou la popo explosive au max peuvent atteindre
                            ennemi.affecte();
                        }
                    }
                }
                switch (act) {
                    case END -> stop_run();
                    case TIRER -> {
                        joueur.tirer(ennemi, dps_popo);
                        dps_popo = 0;
                    }
                    case MAGIE -> {
                        System.out.println("Vous utilisez votre magie sur " + ennemi.getNom());
                        ennemi.dommage_magique(Input.magie() + dps_popo);
                        dps_popo = 0;
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
                            joueur.ajouter_familier();
                            stop_run();
                        }
                    }
                    case AUTRE -> System.out.println(joueur.getNom() + " fait quelque chose.");
                    case AVANCER -> {
                        pr_l = i;
                        joueur.faire_front(true);
                        ennemi.reset_encaisser();
                        competence_avance(ennemi, Main.joueurs[pr_l]);
                    }
                    case AUCUNE -> {}
                    default -> {
                        if (joueur.traite_action(act, ennemi, dps_popo)) {
                            joueur.attaquer(ennemi, dps_popo);
                            dps_popo = 0;
                        }
                        else if(joueur.action_consomme_popo(act)){
                            dps_popo = 0;
                        }
                    }
                }
                if(dps_popo > 0){
                    ennemi.dommage(dps_popo);
                }
                System.out.println();
                if(joueur.a_familier_actif() && run) {
                    act_f = familier_act(joueur, Input.action(joueur, true));
                    switch (act_f) {
                        case FUIR -> joueur.f_fuir();
                        case AUTRE -> System.out.println("Le famillier de " + joueur.getNom() + " fait quelque chose.");
                        case ENCAISSER -> ennemi.f_encaisser();
                        case AVANCER -> joueur.f_faire_front();
                        case PROTEGER -> joueur.f_proteger(ennemi);
                        case AUCUNE -> {}
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

                if(run) {
                    int temp = verifie_mort(ennemi, pos);
                    if (temp != -2) {
                        stop_run();
                    }
                }
            }

            // tour de l'adversaire
            if (run) {
                ennemi.attaque(Main.joueurs[pr_l]);
                int temp = verifie_mort(ennemi, pos);
                if(temp != -2){
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
     * @return -2 si le monstre est en vie, -1 s'il est mort
     * @throws IOException toujours
     */
    private static int verifie_mort(Monstre ennemi, Position pos) throws IOException {
        if (ennemi.check_mort(pos)) {
            return -2;
        }
        // la mort est donné par les méthodes de dommage
        gestion_nomme(ennemi);
        return -1;
    }

    /**
     * Simule le comportement d'un familier en fonction de son niveau d'obéissance
     *
     * @param joueur le propriétaire du familier
     * @return si le familier joue l'action, un false remplace l'action
     */
    private static Action familier_act(Joueur joueur, Action action) throws IOException {
        if(!joueur.a_familier_actif() || joueur.familier_loyalmax()){
            return action;
        }
        System.out.println("Vous donnez un ordre à votre familier.");
        int temp = joueur.get_ob_f() + Input.D6() - 3 + rand.nextInt(2); //valeur d'obéissance à l'action
        if (temp <= 1) {
            System.out.println("Le familier de " + joueur.getNom() + " fuit le combat.");
            joueur.f_inactiver();
            return Action.AUCUNE;
        }
        else if (temp == 2) {
            System.out.println("Le familier de " + joueur.getNom() + " n'écoute pas vos ordres.");
            return Action.AUCUNE;
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
     * @param joueur le joueur actif
     * @param prl l'index du joueur de première ligne
     * @throws IOException mon poto
     */
    private static void alteration(Joueur joueur, int prl) throws IOException {

        String text = "L'alteration concerne-t-elle :\n\t1: " + joueur.getNom();
        if(joueur.a_familier_actif()){
            text += "\n\t2: Le familier de " + joueur.getNom();
        }
        Joueur front = Main.joueurs[prl];
        if(front != joueur){
            text += "\n\t3: " + front.getNom();
            if(front.a_familier_actif()){
                text += "\n\t4: Le familier de " + front.getNom();
            }
        }

        int reponse;
        do{
            System.out.println(text);
            reponse = Input.readInt();
        }while(reponse < 1 || reponse > 4);
        String nom = "";
        switch (reponse){
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
        if(reponse == 1 || reponse == 3){
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

        // assommé
        else if (Input.yn(nom + " est-il/elle inconscient(e) ?")) {
            if(reponse ==2 || reponse == 4){
                joueur.f_assomme();
            }
            else {
                joueur.assomme();
            }
        }

        // berserk
        else if ((reponse == 1 || reponse == 3) && !joueur.est_berserk() && Input.yn(nom + " devient-il/elle berserk ?")) {
            joueur.berserk(0.1f + 0.1f * rand.nextInt(3));
        }
        else if ((reponse == 2 || reponse == 4) && !joueur.f_est_berserk() && Input.yn(nom + " devient-il berserk ?")) {
            joueur.f_berserk(0.1f + 0.1f * rand.nextInt(3));
        }

        // off
        else if (Input.yn(nom + " est-il/elle hors du combat ?")) {
            if(reponse == 2 || reponse == 4){
                joueur.f_inactiver();
            }
            else {
                joueur.inactiver();
            }
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
                ennemi.bostDropMin(1);
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
     * @param joueur l'unité en première ligne
     * @throws IOException ça va mon pote ?
     */
    static void competence_avance(Monstre ennemi, Joueur joueur) throws IOException {
        switch (ennemi.getCompetence()) {
            case ASSAUT -> {
                System.out.println(ennemi.getNom() + " se jete sur " + joueur.getNom() + " avant que vous ne vous en rendiez compte");
                ennemi.attaque(joueur);
            }
            case CHANT_SIRENE ->
                    System.out.println("Le chant de " + ennemi.getNom() + " perturbe " + joueur.getNom() + " qui perd 1 point d'attaque pour la durée du combat.");
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

    /**
     * Traite les joueurs après la fin du combat
     * @throws IOException toujours
     */
    static private void gestion_fin_combat() throws IOException {
        for (int i = 0; i < Main.nbj; i++) {
            Main.joueurs[i].fin_affrontement();
        }
    }
}

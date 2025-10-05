import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import static java.lang.Math.max;

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
        combat(ennemi, pr_l, position);

        System.out.println("Fin du combat\n");
        gestion_mort_end(mort, nom);
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
            i = rand.nextInt(Main.nbj * 2);
        } while (Main.joueurs[i].faire_front(true));
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

        int[] alter_tir = {0, 0}; //{altération, tour_restant}
        int[] alter_attaque = {0, 0};


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
        Action act, act_f;
        Joueur joueur;
        boolean run = ennemi.check_mort();
        while (run) {

            //chaque joueur
            for (int j = 0; j < Main.nbj; j++) {
                i = t[j];
                joueur = Main.joueurs[j];

                // on ne joue que les participants actifs
                if (!joueur.est_actif()) {
                    continue;
                }

                gere_assomme(joueur);

                // resurection, être assommé, etc.
                if (!joueur.peut_jouer()) {
                    System.out.println(joueur.getNom() + " ne peut pas réaliser d'action dans l'immédiat.");
                    joueur.familier_seul(ennemi);
                    joueur.fin_tour_combat();
                    continue;
                }
                if(!joueur.familier_peut_jouer()) {
                    System.out.println("Le familier de " + joueur.getNom() + " ne peut pas réaliser d'action dans l'immédiat.");
                }


                // action
                act = Input.action(joueur, false);
                act_f = Input.action(joueur, true);
                //TODO à partir d'ici
                switch (act) {

                    case OFF -> {
                        alteration(actif, assomme, mort, reveil, berserk, n, skip, i);
                        System.out.println();
                        if (i != pr_l) {
                            alteration(actif, assomme, mort, reveil, berserk, nom[pr_l], skip, pr_l);
                            if (actif[i]) {
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
                        } while (!actif[t[k]]);
                        j = t[k];
                    }

                    case TIRER -> {
                        if (berserk[i] > 0) { //berserker
                            if (Input.D6() < 2 + berserk[i]) {
                                int l;
                                do {
                                    l = rand.nextInt(8);
                                } while (!actif[l]);
                                int temp = Input.tir() + alter_tir[0];
                                temp += Monstre.corriger(temp * (berserk[i] / 2));
                                System.out.println("Pris(e) de folie, " + Main.nom[i] + " attaque " + nom[i] + " et lui inflige " + temp + " dommages !");
                            } else {
                                ennemi.tir(Input.atk() + alter_tir[0], berserk[i] + 1);
                            }
                        }
                        // coup critique
                        if((Main.metier[i] == Metier.RANGER && rand.nextInt(10) == 0) || rand.nextInt(100) == 0) {
                            ennemi.tir(Input.tir() + alter_tir[0], 1.0f + 0.1f * rand.nextInt(11));
                        }
                        else{
                            ennemi.tir(Input.tir() + alter_tir[0]);
                        }
                    }
                    case MAGIE -> {
                        System.out.println("Vous utilisez votre magie sur " + ennemi.nom);
                        ennemi.dommage_magique(Input.magie());
                    }
                    case FUIR -> {
                        if (familier_act(ob, actif, i, n, ennemi)) {
                            fuir(ennemi.nom, i, i == pr_l, actif, n, berserk);
                        }
                    }
                    case ASSOMER -> assommer(ennemi, berserk[i]);
                    case ENCAISSER -> ennemi.encaisser();
                    case SOIGNER -> {
                        boolean temp = i == pr_l ||
                                Input.ask_heal(nom, actif, pr_l);
                        ennemi.soigner(temp);
                    }
                    case DOMESTIQUER -> {
                        if (ennemi.domestiquer()) {
                            ennemi.presente_familier();
                            return;
                        }
                    }
                    case ANALYSER -> analyser(i == pr_l, ennemi);
                    case AUTRE -> {
                        if (familier_act(ob, actif, i, n, ennemi)) {
                            System.out.println(n + " fait quelque chose.");
                        }
                    }
                    case AVANCER -> {
                        if (familier_act(ob, actif, i, n, ennemi)) {
                            System.out.println(n + " passe en première ligne.");
                            pr_l = i;
                            ennemi.reset_encaisser();
                            competence_avance(ennemi, nom[pr_l]);
                        }
                    }

                    //compétence de classe
                    case MAUDIR -> {
                        if (Input.yn("Ciblez vous le monstre annemi ?")) {
                            Sort.maudir(ennemi);
                        } else {
                            Sort.maudir();
                        }
                    }
                    case MEDITATION -> Sort.meditation();
                    case SORT -> Sort.sort(actif, nom, assomme, reveil, ennemi, i);
                    case POTION_REZ -> {
                        int temp = Input.ask_rez(mort);
                        if (temp != -1 && popo_rez(nom[temp], nom[i])) {
                            mort[temp] = false;
                            actif[temp] = true;
                            assomme[temp] = false;
                            berserk[temp] = 0F;
                        }
                        if (i == pr_l) { //premiere ligne
                            System.out.println(n + "s'expose pour donner sa potion.");
                            ennemi.part_soin += 0.4F;
                        }
                    }
                    case BERSERK -> {
                        System.out.println(n + " est prit d'une folie meurtrière !");
                        berserk[i] = 0.2f + 0.1f * rand.nextInt(9);
                        ennemi.dommage(Input.atk() + alter_attaque[0], berserk[i]);
                    }
                    case LAME_DAURA -> {
                        if (berserk[i] > 0 && Input.D6() < 4) {
                            int l;
                            do {
                                l = rand.nextInt(8);
                            } while (!actif[l]);
                            int temp = Input.atk() + alter_attaque[0];
                            temp += Monstre.corriger(temp * (berserk[i] / 2));
                            System.out.println("Prise de folie, " + n + " attaque " + nom[i] + " et lui infliges " + temp + " dommages !");
                            berserk[i] += rand.nextInt(3) * 0.1f + 0.1f;
                        } else {
                            int temp = Input.atk() + alter_attaque[0];
                            temp += Monstre.corriger(temp * berserk[i]);
                            ennemi.dommage(temp, 2.7F);
                            System.out.println("L'arme principale de " + n + " se brise !");
                        }
                    }
                    case FOUILLE -> Sort.fouille();
                    case CONCOCTION -> Sort.concocter();
                    case ASSASSINAT -> {
                        if(Sort.assassinat(ennemi)){
                            pr_l = i;
                        }
                    }
                    case ASSAUT -> Sort.assaut(ennemi, berserk[i]);
                    case CRITIQUE -> Sort.coup_critique(ennemi);
                    case LIEN -> {
                        if(Sort.lien(i, ennemi, mort, actif)){
                            return;
                        }
                    }
                    case INCANTATION -> Sort.incantation(i, ennemi, berserk, assomme, reveil, alter_tir, alter_attaque);
                    case CALME -> {
                        System.out.println(n + " s'harmonise avec l'univers et laisse retomber sa rage.");
                        berserk[i] = 0f;
                    }

                    default -> { // ATTAQUER
                        if (berserk[i] > 0) { //berserker
                            if (Input.D6() < 2 + berserk[i]) {
                                int l;
                                do {
                                    l = rand.nextInt(8);
                                } while (!actif[l]);
                                int temp = Input.atk() + alter_attaque[0];
                                temp += Monstre.corriger(temp * (berserk[i] / 2));
                                System.out.println("Prise de folie, " + Main.nom[i] + " attaque " + nom[i] + " et lui inflige " + temp + " dommages !");
                            } else {
                                ennemi.dommage(Input.atk() + alter_attaque[0], berserk[i] + 1);
                            }
                        }
                        // attaque normale
                        else if (familier_act(ob, actif, i, n, ennemi)) {
                            System.out.println(n + " attaque l'ennemi.");
                            //coup critique
                            if((i < Main.nbj && //c'est un joueur
                                    (rand.nextInt(100) == 0 ||
                                            (Main.metier[i] == Metier.GUERRIERE && rand.nextInt(10) == 0)))
                                    || rand.nextInt(255) == 0) { // on donne une chance au familier aussi
                                ennemi.dommage(Input.atk() + alter_attaque[0], 1.1f + 0.1f * rand.nextInt(10));
                            }
                            ennemi.dommage(Input.atk() + alter_attaque[0]);
                        }
                    }
                }
                System.out.println();

                // s'assure qu'un participant est toujours en première ligne
                if (!actif[pr_l]) {
                    boolean is_active = false;
                    int k = 0;
                    for (; k < Main.nbj * 2; k++) {
                        if (actif[k]) {
                            is_active = true;
                            break;
                        }
                    }
                    if (!is_active) { // plus de joueur participant
                        run = false;
                        System.out.println("Aucun joueur ou familier détecté en combat.");
                        // break inutile, car actif[i] toujours à false
                    } else {
                        do {
                            k = rand.nextInt(8);
                        } while (!actif[k]);
                        pr_l = k;
                        System.out.println(nom[k] + " se retrouve en première ligne.\n");
                    }
                }
                int temp = verifie_mort(ennemi, actif, pos);
                if(temp != -2){
                    return;
                }
            }

            // tour de l'adversaire
            if (run) {
                ennemi.attaque(nom[pr_l]);
                int temp = verifie_mort(ennemi, actif, pos);
                if(temp != -2){
                    return;
                }
            }
        }
    }

    /**
     * Vérifie si le monstre est mort et en gère les aprés coup
     * @param ennemi le monstre adverse
     * @param actif une liste indiquant s'il faut considerer les participants ou non
     * @return -2 si le monstre est en vie, -1 s'il est mort, l'index du joueur qui l'a domestiqué sinon
     * @throws IOException toujours
     */
    private static int verifie_mort(Monstre ennemi, boolean[] actif, Position pos) throws IOException {
        if (ennemi.check_mort()) {
            return -2;
        }
        // la mort est donné par les méthodes de dommage
        gestion_nomme(ennemi);

        //le nécromancien peut tenter de ressuciter le monstre
        int etat = 15 + rand.nextInt(12);
        for (int k = 0; k < Main.nbj; k++) {
            if (etat > 0 && actif[k] && Main.metier[k] == Metier.NECROMANCIEN) {
                if (Input.yn("Voulez vous tenter de ressuciter " + ennemi.nom + " en tant que familier pour 2PP ?")) {
                    int temp = ressuciter(ennemi, etat);
                    etat = etat + temp;
                    if (temp != 0) {
                        if (!Input.yn("Tuez vous le familier que vous venez de ramener à la vie pour gagner 4PP ?")) {
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
            if (etat > 0 && actif[k] && Main.metier[k] == Metier.ALCHIMISTE) {
                if (Input.yn("Voulez vous dissequer le cadavre ?")) {
                    etat += Sort.dissection(etat);
                    if (etat < 0) {
                        System.out.println("Le cadavre du monstre n'est plus qu'une masse informe et inutilisable.");
                        return -1;
                    }
                }
            }
        }
        if (etat == 0 || pos == Position.ENFERS || pos == Position.OLYMPE || pos == Position.ASCENDANT) {
            return -1;
        }
        System.out.println("Vous pouvez vendre le cadavre de " + ennemi.nom + " pour " + (1 + (etat - 1) / 10) + " PO.");
        return -1;
    }

    /**
     * Regarde si le participant est assommé et gère le cas échéant
     * @param joueur le joueur dont on doit vérifier s'il est assommé
     * @throws IOException toujours
     */
    private static void gere_assomme(Joueur joueur) throws IOException {

        joueur.essaie_reveil();
        joueur.f_essaie_reveil();

        if (joueur.est_assomme()) {
            joueur.passe();
        }
        if(joueur.f_est_assomme()){
            joueur.f_passe();
        }
    }

    /**
     * Tente de fuir le combat
     *
     * @param ne    le nom du monstre ennemi
     * @param i     l'index du participant
     * @param is_pr si le participant est en première ligne
     * @param actif la liste de boolean d'activité des participants
     * @param n     le nom du participant
     * @throws IOException ça roule
     */
    private static void fuir(String ne, int i, boolean is_pr, boolean[] actif, String n, float[] berserk) throws IOException {
        int bonus = 0;
        if(i < Main.nbj && Main.metier[i] == Metier.RANGER) {
            bonus += rand.nextInt(4);
        }
        if(berserk[i] > 0 && Input.D4() + bonus < berserk[i]) {
            System.out.println(n + " est trop enragé(e) pour fuir.");
        }
        else if (!is_pr || Input.D6() + bonus > 2 + rand.nextInt(2)) {
            actif[i] = false;
            System.out.println(n + " a fuit le combat.");
        } else {
            System.out.println(n + " n'est pas parvenu à distancer " + ne + ".");
        }
    }

    /**
     * Simule le comportement d'un familier en fonction de son niveau d'obéissance
     *
     * @param ob     l'obéissance du familier, si la valeur est 0, le programme renverra true
     * @param actif  la liste de boolean d'activité des participants
     * @param index  l'index du familier
     * @param n      le nom du familier
     * @param ennemi le monstre ennemi
     * @return si le familier joue l'action, un false remplace l'action
     * @implNote un joueur peut être entré avec une obéissance de 0.
     */
    private static boolean familier_act(int ob, boolean[] actif, int index, String n, Monstre ennemi) throws IOException {
        if (ob < 1 || ob == Main.f_max) {
            return true;
        }
        int temp = ob + Input.D6() - 3 + rand.nextInt(2); //valeur d'obeisance à l'action
        if (temp <= 1) {
            System.out.println(n + " fuit le combat.");
            actif[index] = false;
        } else if (temp == 2) {
            System.out.println(n + " n'écoute pas vos ordres.");
        } else if (temp <= 4) {
            System.out.println(n + " ignore vos directives et attaque l'ennemi.");
            ennemi.dommage(Input.atk());
        } else {
            return true;
        }
        return false;
    }

    /**
     * Gère le retour OFF de l'action, c.-à-d. la mort, l'inconscience ou le retrait du joueur actif ou de celui
     * de première ligne
     *
     * @param actif   booléens indiquants quels participants sont actifs
     * @param assomme booléens indiquants quels participants sont assommés
     * @param mort    booléens indiquants quels participants sont morts
     * @param reveil  int permettant au joueur de reprendre connaissance plus vite
     * @param n       le nom du participant actuel (supposemment off)
     * @param skip    liste des joueurs ne pouvant pas jouer leur prochain tour (indépendemment de mort et assomme)
     * @param i       l'indice du joueur actuel
     * @throws IOException mon poto
     */
    private static void alteration(boolean[] actif, boolean[] assomme, boolean[] mort, int[] reveil, float[] berserk, String n, boolean[] skip, int i) throws IOException {
        if (Input.yn(n + " est-il/elle mort(e) ?")) {

            //on regarde si on peut le ressuciter immédiatement
            int malus = 0;
            for (int k = 0; k < Main.nbj; k++) {
                if (actif[k] && Main.metier[k] == Metier.NECROMANCIEN && !n.equals(Main.nom[k]) && !assomme[k] && !skip[k]) { //le joueur est necromancien et disponible
                    if (Input.yn("Est-ce que " + Main.nom[k] + " veux tenter de ressuciter " + n + " pour 2 PP ?")) {
                        if (ressuciter(malus)) {
                            actif[i] = true;
                            mort[i] = false;
                            System.out.println(n + " a été arraché(e) à l'emprise de la mort.");
                            if(rand.nextInt(4) <= malus){
                                assomme[i] = true;
                                System.out.println(n + " n'a pas repris conscience.");
                                reveil[i] = rand.nextInt(2) + (3 - malus);
                            }
                            return;
                        } else {
                            malus += 1;
                        }
                    }
                }
            }
            actif[i] = false;
            mort[i] = true;
        } else if (Input.yn(n + " est-il/elle inconscient(e) ?")) {
            assomme[i] = true;
            reveil[i] = 0;
        } else if (i < Main.nbj && Main.metier[i] == Metier.ARCHIMAGE && Input.yn("Le mana de " + n + " est-il tombé à 0 ?")) {
            if(Sort.addiction()){
                assomme[i] = true;
                reveil[i] = 0;
            }
        } else if (Input.yn(n + " est-il/elle berserk ?")) {
            berserk[i] = 0.1f + 0.1f * rand.nextInt(6);
        }
        else if (!Input.yn(n + " est-il/elle toujours en combat ?")) {
            System.out.println(n + " est retiré(e) du combat.");
            actif[i] = false;
        }
    }

    /**
     * Gère les compétences du monstre juste avant le combat
     *
     * @param ennemi le monstre qu'affrontent les participants
     * @param pr_l   l'indice du participant en premières lignes
     * @return si le combat s'arrête
     */
    static boolean competence(Monstre ennemi, int pr_l) throws IOException {
        String nom = Main.joueurs[pr_l].getNom();
        if(Main.joueurs[pr_l].a_familier_front()){
            nom = "familier de " + nom;
        }
        switch (ennemi.competence) {
            case DAMNATION_RES -> System.out.println(nom + " perd 2 points de vie pour la durée du combat.");
            case DAMNATION_ATK -> System.out.println(nom + " perd 1 point d'attaque pour la durée du combat.");
            case DAMN_ARES -> {
                if (Input.yn("Un de vous est-il descendant d'Ares ?")) {
                    System.out.println(ennemi.nom + " vous maudit.");
                    System.out.println("Tout descendant d'Arès perd 2 points d'attaque pour la durée du combat.");
                }
            }
            case HATE_DEMETER -> {
                if (Input.yn("Un de vous est-il descendant de Demeter ?")) {
                    System.out.println(ennemi.nom + " vous regarde avec haine.");
                    ennemi.attaque += 1;
                }
            }
            case HATE_DYONISOS -> {
                if (Input.yn("Un de vous est-il descendant de Dyonis ?")) {
                    System.out.println(ennemi.nom + " vous regarde avec haine.");
                    ennemi.attaque += 1;
                }
            }
            case HATE_POSEIDON -> {
                if (Input.yn("Un de vous est-il descendant de Poseidon ?")) {
                    System.out.println(ennemi.nom + " vous regarde avec haine.");
                    ennemi.attaque += 1;
                }
            }
            case HATE_ZEUS -> {
                if (Input.yn("Un de vous est-il descendant de Zeus ?")) {
                    System.out.println(ennemi.nom + " vous regarde avec haine.");
                    ennemi.attaque += 1;
                }
            }
            case FEAR_ZEUS -> {
                if (Input.yn("Un de vous est-il descendant de Zeus ?")) {
                    System.out.println(ennemi.nom + " vous crains.");
                    ennemi.attaque -= 1;
                }
            }
            case FEAR_DEMETER -> {
                if (Input.yn("Un de vous est-il descendant de Demeter ?")) {
                    System.out.println(ennemi.nom + " vous crains.");
                    ennemi.attaque -= 1;
                }
            }
            case FEAR_POSEIDON -> {
                if (Input.yn("Un de vous est-il descendant de Poseidon ?")) {
                    System.out.println(ennemi.nom + " vous crains.");
                    ennemi.attaque -= 1;
                }
            }
            case FEAR_HYPNOS -> {
                if (Input.yn("Un de vous est-il descendant d'Hypnos ?")) {
                    System.out.println(ennemi.nom + " vous crains.");
                    ennemi.attaque -= 1;
                }
            }
            case FEAR_DYONISOS -> {
                if (Input.yn("Un de vous est-il descendant de Dyonisos ?")) {
                    System.out.println(ennemi.nom + " vous crains.");
                    ennemi.attaque -= 1;
                }
            }
            case CIBLE_CASQUE -> {
                if (Input.yn(nom + " porte-il/elle un casque ?") && Input.D6() <= 4) {
                    System.out.println(ennemi.nom + " fait tomber votre casque pour le combat.");
                }
            }
            case ARMURE_GLACE -> ennemi.armure += 1;
            case ARMURE_NATURELLE -> {
                ennemi.armure += 1;
                ennemi.armure_base += 1;
            }
            case ARMURE_GLACE2 -> ennemi.armure += 2;
            case ARMURE_NATURELLE2 -> {
                ennemi.armure += 2;
                ennemi.armure_base += 2;
            }
            case ARMURE_NATURELLE3 -> {
                ennemi.armure += 3;
                ennemi.armure_base += 3;
            }
            case ARMURE_NATURELLE4 -> {
                ennemi.armure += 4;
                ennemi.armure_base += 4;
            }
            case VITALITE_NATURELLE -> {
                ennemi.vie_max += 3;
                ennemi.vie += 3;
                ennemi.vie_base += 3;
            }
            case VITALITE_NATURELLE2 -> {
                ennemi.vie_max += 6;
                ennemi.vie += 6;
                ennemi.vie_base += 6;
            }
            case VITALITE_NATURELLE3 -> {
                ennemi.vie_max += 9;
                ennemi.vie += 9;
                ennemi.vie_base += 9;
            }
            case FLAMME_ATTAQUE -> ennemi.attaque += 2;
            case FORCE_NATURELLE -> {
                ennemi.attaque += 2;
                ennemi.attaque_base += 2;
            }
            case FORCE_NATURELLE2 -> {
                ennemi.attaque += 4;
                ennemi.attaque_base += 4;
            }
            case FORCE_NATURELLE3 -> {
                ennemi.attaque += 6;
                ennemi.attaque_base += 6;
            }
            case PRUDENT -> {
                int tolerance = ennemi.vie_max;
                for (int i = 0; i < Main.nbj; i++) {
                    if (Main.joueurs[i].est_actif()) {
                        System.out.print(Main.joueurs[i].getNom() + " ");
                        tolerance -= Input.atk();
                        if(Main.joueurs[i].a_familier_actif()){
                            System.out.print("Familier de " + Main.joueurs[i].getNom() + " ");
                            tolerance -= Input.atk();
                        }
                        if (tolerance < 0) {
                            System.out.println(ennemi.nom + " fuit en vous voyant avancer.");
                            return true;
                        }
                    }
                }
            }
            case SUSPICIEUX -> {
                int tolerance = ennemi.attaque * 2;
                for (int i = 0; i < Main.nbj * 2; i++) {
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
                            System.out.println(ennemi.nom + " fuit en vous voyant avancer.");
                            return true;
                        }
                    }
                }
            }
            case MEFIANT -> {
                int tolerance = ennemi.vie + ennemi.armure * 3 + ennemi.attaque;
                for (int i = 0; i < Main.nbj * 2; i++) {
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
                            System.out.println(ennemi.nom + " fuit en vous voyant avancer.");
                            return true;
                        }
                    }
                }
            }
            case REGARD_APPEURANT -> {
                System.out.println(nom + " croise le regard de " + ennemi.nom);
                if (Input.D4() != 4) {
                    System.out.println(nom + " est appeuré(e) et perd 1 points d'attaque pour la durée du combat");
                }
            }
            case REGARD_TERRIFIANT -> {
                System.out.println(nom + " croise le regard de " + ennemi.nom);
                if (Input.D6() <= 5) {
                    System.out.println(nom + " est terrifié(e) et perd 3 points d'attaque pour la durée du combat");
                }
            }
            case FAIBLE -> {
                ennemi.attaque -= 3;
                ennemi.attaque_base -= 3;
            }
            case BENEDICTION -> {
                System.out.println(ennemi.nom + " béni " + nom + " qui gagne définitivement 1 point de résistance.");
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
            case BRUME ->
                    System.out.println(ennemi.nom + "crée un rideau de brûme, tous les participants perdent 1 point d'attaque pour la durée du combat.");
            case GOLEM_PIERRE -> {
                ennemi.nom = "golem de pierre";
                ennemi.attaque += rand.nextInt(3) + 1;
                ennemi.attaque_base = ennemi.attaque;
                ennemi.vie_max += rand.nextInt(4) + 3;
                ennemi.vie = ennemi.vie_max;
                ennemi.vie_base = ennemi.vie;
                ennemi.armure += rand.nextInt(4) + 1;
                ennemi.armure_base = ennemi.armure;
            }
            case GOLEM_FER -> {
                ennemi.nom = "golem de fer";
                ennemi.attaque += rand.nextInt(4) + 2;
                ennemi.attaque_base = ennemi.attaque;
                ennemi.vie_max += rand.nextInt(8) + 5;
                ennemi.vie = ennemi.vie_max;
                ennemi.vie_base = ennemi.vie;
                ennemi.armure += rand.nextInt(5) + 2;
                ennemi.armure_base = ennemi.armure;
                ennemi.niveau_drop_max += 1;
            }
            case GOLEM_ACIER -> {
                ennemi.nom = "golem d'acier";
                ennemi.attaque += rand.nextInt(6) + 3;
                ennemi.attaque_base = ennemi.attaque;
                ennemi.vie_max += rand.nextInt(10) + 7;
                ennemi.vie = ennemi.vie_max;
                ennemi.vie_base = ennemi.vie;
                ennemi.armure += rand.nextInt(6) + 3;
                ennemi.armure_base = ennemi.armure;
                ennemi.niveau_drop_max += 1;
                ennemi.niveau_drop_min += 1;
                ennemi.drop_quantite_max += 1;
            }
            case GOLEM_MITHRIL -> {
                ennemi.nom = "golem de mithril";
                ennemi.attaque += rand.nextInt(8) + 4;
                ennemi.attaque_base = ennemi.attaque;
                ennemi.vie_max += rand.nextInt(12) + 9;
                ennemi.vie = ennemi.vie_max;
                ennemi.vie_base = ennemi.vie;
                ennemi.armure += rand.nextInt(6) + 4;
                ennemi.armure_base = ennemi.armure;
                ennemi.niveau_drop_max += 2;
                ennemi.niveau_drop_min += 1;
                ennemi.drop_quantite_max += 2;
            }
        }
        return false;
    }

    /**
     * Gère les compétences de l'ennemi lors d'un changement de position
     *
     * @param ennemi   le monstre ennemi
     * @param nom_pr_l le nom de l'unité en première ligne
     * @throws IOException ça va mon pote ?
     */
    static void competence_avance(Monstre ennemi, String nom_pr_l) throws IOException {
        switch (ennemi.competence) {
            case ASSAUT -> {
                System.out.println(ennemi.nom + " se jete sur " + nom_pr_l + " avant que vous ne vous en rendiez compte");
                ennemi.attaque(nom_pr_l);
            }
            case CHANT_SIRENE ->
                    System.out.println("Le chant de " + ennemi.nom + " perturbe " + nom_pr_l + " qui perd 1 point d'attaque pour la durée du combat.");
        }
    }

    /**
     * Suprimme le monstre de sa zone après sa mort s'il est nommé
     *
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
     * Supprime le monstre nommé donné de sa liste
     *
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
     *
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
     * @throws IOException pour l'Input
     */
    private static int ressuciter(Monstre ennemi, int etat) throws IOException {
        switch (Input.D8() + (etat - 10) / 2) {
            case 4, 5 -> { //25%
                System.out.println(ennemi.nom + " a été partiellement ressucité.");

                System.out.println("nouveau familier : zombie " + ennemi.nom);
                System.out.println("attaque : " + max(ennemi.attaque / 4, 1));
                System.out.println("vie : " + max(ennemi.vie_max / 4, 1));
                System.out.println("armure : " + ennemi.armure / 4 + "\n");
                return -rand.nextInt(6) - 7;
            }
            case 6 -> { //50%
                System.out.println(ennemi.nom + " a été suffisemment ressucité");

                System.out.println("nouveau familier : zombie " + ennemi.nom);
                System.out.println("attaque : " + max(ennemi.attaque / 2, 1));
                System.out.println("vie : " + max(ennemi.vie_max / 2, 1));
                System.out.println("armure : " + ennemi.armure / 2 + "\n");
                return -rand.nextInt(6) - 4;
            }
            case 7 -> { // 75%
                System.out.println(ennemi.nom + " a été correctement ressucité");

                System.out.println("nouveau familier : " + ennemi.nom + " le ressucité");
                System.out.println("attaque : " + max(ennemi.attaque * 3 / 4, 1));
                System.out.println("vie : " + max(ennemi.vie_max * 3 / 4, 1));
                System.out.println("armure : " + ennemi.armure * 3 / 4 + "\n");
                return -rand.nextInt(6) - 1;
            }
            case 8, 9 -> {
                System.out.println(ennemi.nom + " a été parfaitement ressucité");

                System.out.println("nouveau familier : " + ennemi.nom);
                System.out.println("attaque : " + (ennemi.attaque + 1));
                System.out.println("vie : " + (ennemi.vie_max + 2));
                System.out.println("armure : " + ennemi.armure + "\n");
                return 1;
            }
            default -> {
                System.out.println("échec du sort.");
                return 0;
            }
        }
    }

    /**
     * Tente de ressuciter un allié par nécromancie
     * @param malus un malus sur la tentative
     * @return si l'allié a été ressucité
     * @throws IOException notre poto anti bug
     */
    private static boolean ressuciter(int malus) throws IOException {
        if(malus > 3){
            malus = 3;
        }
        return switch (Input.D8() - malus) {
            case 4, 5, 6 -> {
                System.out.println("Résurection avec 4 points de vie");
                yield true;
            }
            case 7 -> {
                System.out.println("Résurection avec 8 (max) points de vie");
                yield true;
            }
            case 8, 9, 10 -> {
                System.out.println("Résurection avec 12 (max) points de vie");
                yield true;
            }
            default -> {
                System.out.println("Echec de la résurection");
                yield false;
            }
        };
    }

    /**
     * @param nom_mort le nom du joueur qui se fait ressuciter
     * @param nom_alchi le nom du joueur qui ressucite
     * @return si le mort revient à la vie
     * @throws IOException toujours
     */
    static private boolean popo_rez(String nom_mort, String nom_alchi) throws IOException {
        if (Input.yn("Utilisez vous une potion divine ?")) {
            System.out.println(nom_alchi + " fait boire à " + nom_mort + " une potion gorgé de l'énergie des dieux.");
            switch (Input.D6()) {
                case 1 -> {
                    System.out.println(nom_mort + " se réveille avec 1 points de vie.");
                    return true;
                }
                case 2 -> {
                    System.out.println(nom_mort + " se réveille avec 2 points de vie.");
                    return true;
                }
                case 3, 4 -> {
                    System.out.println(nom_mort + " se réveille avec 4 points de vie.");
                    return true;
                }
                case 5, 6 -> {
                    System.out.println(nom_mort + " se réveille avec 6 points de vie.");
                    return true;
                }
                default -> {
                    System.out.println(nom_mort + "reste mort.");
                    return false;
                }
            }
        }
        if (Input.yn("Utilisez vous un élixir ?")) {
            System.out.println(nom_alchi + " fait boire à " + nom_mort + " une potion miraculeuse.");
            switch (Input.D20()) {
                case 1, 2, 3 -> {
                    System.out.println(nom_mort + " se réveille avec 2 points de vie et 3 points de résistance additionels.");
                    return true;
                }
                case 4, 5, 6 -> {
                    System.out.println(nom_mort + " se réveille avec 3 points de vie et 4 points de résistance additionels.");
                    return true;
                }
                case 7, 8 -> {
                    System.out.println(nom_mort + " se réveille avec 5 points de vie et 7 points de résistance additionels.");
                    return true;
                }
                case 9, 10 -> {
                    System.out.println(nom_mort + " se réveille avec 6 points de vie et 9 points de résistance additionels.");
                    return true;
                }
                case 11, 12 -> {
                    System.out.println(nom_mort + " se réveille avec 6 points de vie et 12 points de résistance additionels.");
                    return true;
                }
                case 13, 14, 15 -> {
                    System.out.println(nom_mort + " se réveille avec 7 points de vie et 13 points de résistance additionels.");
                    return true;
                }
                case 16, 17 -> {
                    System.out.println(nom_mort + " se réveille avec 7 points de vie et 14 points de résistance additionels.");
                    return true;
                }
                case 18, 19 -> {
                    System.out.println(nom_mort + " se réveille avec 8 points de vie et 14 points de résistance additionels.");
                    return true;
                }
                case 20 -> {
                    System.out.println(nom_mort + " se réveille avec 8 points de vie et 15 points de résistance additionels.");
                    return true;
                }
                default -> {
                    System.out.println(nom_mort + "reste mort.");
                    return false;
                }
            }
        }
        System.out.println("Vous n'avez aucun moyen de ressuciter " + nom_mort + ".");
        return false;
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
        switch (ennemi.competence) {
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
        System.out.println("armure : " + (temp >= 7 ? arm : "???"));
    }

    static private void gestion_mort_end(boolean[] morts, String[] nom) throws IOException {
        for (int i = 0; i < Main.nbj * 2; i++) {
            if (morts[i] && Input.yn(nom[i] + " est mort durant le combat, le reste-t-il/elle ?")) {
                if ((Main.metier[i] == Metier.GUERRIERE && Input.D10() > 6)
                        || (Main.metier[i] == Metier.SHAMAN && Input.D10() > 8)) {
                    System.out.println(nom[i] + " résiste à la mort.\n");
                    return;
                }
                int t;
                if (i < Main.nbj) {
                    System.out.println(nom[i] + " se retrouve aux enfers.\n");
                    Main.positions[i] = Position.ENFERS;
                    t = i;
                }
                else {
                    System.out.println(nom[i] + " a rendu l'âme.\n");
                    t = i - Main.nbj;
                }
                Main.f[t] = 0;
            }
        }
    }

    /**
     * Applique la compétence "assommer" sur le monstre
     * Demande aux joueurs les informations nécessaires
     * @throws IOException jsp mais sans ça, ça ne marche pas
     */
    static void assommer(Monstre monstre, float bonus) throws IOException {
        // compétence ennemie
        switch (monstre.competence) {
            case VOL -> {
                System.out.println("L'attaque n'atteint pas " + monstre.nom + ".");
                monstre.competence = Competence.VOL_OFF;
                System.out.println(monstre.nom + " se pose à terre.\n");
                return;
            }
            case VOLAGE -> {
                System.out.println("L'attaque n'atteint pas " + monstre.nom + ".");
                monstre.competence = Competence.AUCUNE;
                System.out.println(monstre.nom + " se pose à terre.\n");
                return;
            }
            case FURTIF -> {
                System.out.println("Vous ne parvenez plus à identifier où se trouve " + monstre.nom + " et renoncez à attaquer.\n");
                monstre.competence = Competence.AUCUNE;
                return;
            }
            default -> {
            }

        }
        //action
        int attaque = Input.atk();
        int jet = Input.D6() + (int)bonus;
        if(jet > 7){
            jet = 7;
        }
        switch (jet) {
            case 1:
                System.out.print("Vous manquez votre cible.");
                attaque = 0;
                break;
            case 2:
                System.out.print("Vous frappez de justesse votre cible, au moins, vous l'avez touchée.");
                attaque = 0;
                monstre.affecte();
                break;
            case 3, 4 :
                attaque = Monstre.corriger((float) attaque / 2);
                monstre.affecte();
                break;
            case 5:
                attaque = Monstre.corriger((float) attaque / 2);
                monstre.do_assomme();
                break;
            case 6:
                System.out.println("Vous frappez avec force !");
                monstre.do_assomme();
                break;
            case 7:
                System.out.println("Vous frappez à vous en blesser les bras !");
                if(rand.nextBoolean()){
                    System.out.println("Vous subissez 1 point de dommage");
                }
                monstre.do_assomme();
                attaque = Monstre.corriger(attaque * bonus);

            default:
                System.out.println("Le résultat n'a pas été comprit, attaque classique appliquée.");
        }
        monstre.dommage(attaque);
    }
}

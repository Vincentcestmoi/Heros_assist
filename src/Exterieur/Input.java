package Exterieur;

import Metiers.Joueur;
import Equipement.Pre_Equipement;
import Monstre.Race;
import main.Main;

import Enum.Position;
import Enum.Rang;
import Enum.Promo_Type;
import Enum.Action;
import Enum.Action_extra;
import Enum.Choix;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import javax.json.*;

public class Input {

    // sauvegarde

    /**
     * Sauvegarde les données des précedéntes partie et propose aux joueurs de charger une sauvegarde
     * @return true si la sauvegarde est complété, et false si elle doit être faite à la main
     */
    public static boolean load() throws IOException {

        SaveManager.afficherSauvegardes();
        int reponse;
        do {
            System.out.print("Votre choix : ");
            reponse = readInt();
        } while (reponse < 0 || reponse >= SaveManager.SAVE_DIRS.length);

        File infoFile = new File(SaveManager.SAVE_DIRS[reponse] + "/info.json");
        boolean sauvegardeExiste = infoFile.exists();

        Main.Path = reponse;

        // charger ou nouvelle partie
        if (!sauvegardeExiste || !yn("Sauvegarde détectée, charger cette sauvegarde ?")) {
            if (yn("Confirmez la suppression")) {
                Output.viderSauvegarde(reponse);
                System.out.println("lancement du jeu.\n\n");
                return false;
            }
        }

        // Chargement des joueurs
        Main.Path = reponse;
        Main.joueurs = SaveManager.chargerSauvegarde();
        Main.nbj = Main.joueurs.length;

        for (Joueur joueur : Main.joueurs) {
            System.out.print("Joueur chargé avec succès : ");
            joueur.presente();
            System.out.println();
        }

        //monstre nommé
        corrige_nomme();
        //item unique
        corrige_item();

        System.out.println("lancement du jeu.\n\n");
        return true;
    }

    /**
     * Supprime les objets uniques déjà tirés
     */
    private static void corrige_item() {
        try (JsonReader reader = Json.createReader(new FileReader("Save" + Main.Path + "/info.json"))) {
            JsonObject info = reader.readObject();
            JsonObject items = info.getJsonObject("items_uniques");
            if (items == null) return;

            for (String rangKey : items.keySet()) {
                if (!rangKey.equals("PROMOTION")) {
                    Rang rang = Rang.valueOf(rangKey);
                    JsonArray noms = items.getJsonArray(rangKey);
                    for (JsonValue val : noms) {
                        Pre_Equipement.safe_delete(((JsonString) val).getString(), rang, Promo_Type.QUIT);
                    }
                } else {
                    JsonObject promoObj = items.getJsonObject("PROMOTION");
                    for (String promoKey : promoObj.keySet()) {
                        Promo_Type promo = Promo_Type.valueOf(promoKey);
                        JsonArray noms = promoObj.getJsonArray(promoKey);
                        for (JsonValue val : noms) {
                            Pre_Equipement.safe_delete(((JsonString) val).getString(), Rang.PROMOTION, promo);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture des items uniques : " + e.getMessage());
        }
    }


    /**
     * Supprime les monstres nommés enregistrés comme abscent
     */
    private static void corrige_nomme() {
        try (JsonReader reader = Json.createReader(new FileReader("Save" + Main.Path + "/info.json"))) {
            JsonObject info = reader.readObject();
            JsonArray monstres = info.getJsonArray("monstres_nommes");

            if (monstres == null) return;

            for (JsonValue val : monstres) {
                String nom = ((JsonString) val).getString();
                Race.delete_monstre(nom);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture des monstres nommés : " + e.getMessage());
        }
    }


    // ********************************************************************************************************** //

    //visuel (terminal)

    /**
     * Lit le texte en terminal
     *
     * @return le texte lu
     * @throws IOException en cas de problème ?
     */
    public static String read() throws IOException {
        StringBuilder readed = new StringBuilder();
        int temp = System.in.read();
        while (temp != 10) {
            readed.append((char) temp);
            temp = System.in.read();
        }
        System.out.println();
        return readed.toString();
    }

    /**
     * Lit une valeur en terminal et la renvoie (doit être en chiffre uniquement)
     *
     * @return la valeur
     * @throws IOException en cas de problème ?
     */
    public static int readInt() throws IOException {
        int number;
        while (true) {
            number = 0;
            int temp = System.in.read();

            if (temp == '\n') {
                continue;
            }

            boolean valid = true;
            while (temp != '\n' && temp != -1) {
                int digit = temp - '0';
                if (digit < 0 || digit > 9) {
                    valid = false;
                } else {
                    number = number * 10 + digit;
                }
                temp = System.in.read();
            }

            if (valid) {
                System.out.println();
                return number;
            }

            System.out.println("\nErreur détectée : saisie non numérique. Veuillez réécrire votre nombre.");
        }
    }


    /**
     * Demande au joueur le résultat d'un jet 4 et le renvoie
     * majoration par 6.
     * @return le chiffre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public static int D4() throws IOException {
        System.out.print("D4 : ");
        Output.jouerSonDe();
        int temp = readInt();
        if(temp > 6){
            return 6;
        }
        return Math.max(temp, 1);
    }

    /**
     * Demande au joueur le résultat d'un jet 6 et le renvoie
     * majoration par 8.
     * @return le chiffre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public static int D6() throws IOException {
        System.out.print("D6 : ");
        Output.jouerSonDe();
        int temp = readInt();
        if(temp > 8){
            return 8;
        }
        return Math.max(temp, 1);
    }

    /**
     * Demande au joueur le résultat d'un jet 8 et le renvoie
     * majoration par 10
     * @return le chiffre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public static int D8() throws IOException {
        System.out.print("D8 : ");
        Output.jouerSonDe();
        int temp = readInt();
        if(temp > 10){
            return 10;
        }
        return Math.max(temp, 1);
    }

    /**
     * Demande au joueur le résultat d'un jet 10 et le renvoie
     * majoration par 12
     * @return le chiffre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public static int D10() throws IOException {
        System.out.print("D10 : ");
        Output.jouerSonDe();
        int temp = readInt();
        if(temp > 12){
            return 12;
        }
        return Math.max(temp, 1);
    }

    /**
     * Demande au joueur le résultat d'un jet 12 et le renvoie
     * majoration par 14
     * @return le chiffre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public static int D12() throws IOException {
        System.out.print("D12 : ");
        Output.jouerSonDe();
        int temp = readInt();
        if(temp > 14){
            return 14;
        }
        return Math.max(temp, 1);
    }

    /**
     * Demande au joueur le résultat d'un jet 20 et le renvoie
     * majoration par 22
     * @return le chiffre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public static int D20() throws IOException {
        System.out.print("D20 : ");
        Output.jouerSonDe();
        int temp = readInt();
        if(temp > 22){
            return 22;
        }
        return Math.max(temp, 1);
    }

    /**
     * Pose une question au joueur
     * @return s'il a répondu oui
     */
    public static boolean yn(String question) throws IOException {
        while(true) {
            System.out.print(question + " O/n ");
            String reponse = read();
            if (reponse.equals("O") || reponse.equals("o") || reponse.isEmpty()) {
                return true;
            }
            if (reponse.equals("n") || reponse.equals("N")) {
                return false;
            }
            System.out.println("Réponse non comprise.");
        }
    }

    /**
     * Demande au joueur son attaque
     *
     * @return le nombre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public static int atk() throws IOException {
        System.out.print("entrez votre attaque actuelle : ");
        return readInt();
    }

    /**
     * Demande au joueur son attaque
     *
     * @return le nombre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public static int tir() throws IOException {
        System.out.print("entrez votre puissance de tir actuelle : ");
        return readInt();
    }

    /**
     * Demande au joueur sa vie
     *
     * @return le nombre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public static int vie() throws IOException {
        System.out.print("entrez votre résistance actuelle : ");
        return readInt();
    }

    /**
     * Demande au joueur son armure
     *
     * @return le nombre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public static int def() throws IOException {
        System.out.print("entrez votre armure actuelle : ");
        return readInt();
    }

    /**
     * Demande au joueur la force de son attaque magique
     *
     * @return le nombre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public static int magie() throws IOException {
        System.out.print("entrez votre puissance d'attaque magique : ");
        return readInt();
    }

    /**
     * Laisse le joueur choisir sa promotion
     * @return un indice sur le type de promotion que le joueur veut
     */
    public static Promo_Type promo() throws IOException {
        String texte = "Choississez votre type de récompense : ";
        if(Pre_Equipement.nb_monture > 0){
            texte += "(m)onture ";
        }
        if(Pre_Equipement.nb_boost > 0){
            texte += "(r)enforcement ";
        }
        if(Pre_Equipement.nb_arte > 0){
            texte += "(a)rtéfact ";
        }
        System.out.println(texte);
        switch(read()){
            case "m", "M", "monture", "Monture" -> {
                if(Pre_Equipement.nb_monture > 0) {
                    return Promo_Type.MONTURE;
                }
            }
            case "r", "R", "renforcement", "Renforcement" -> {
                if(Pre_Equipement.nb_boost > 0) {
                    return Promo_Type.AMELIORATION;
                }
            }
            case "a", "A", "Artefact", "artefact", "Artéfact", "artéfact" -> {
                if(Pre_Equipement.nb_arte > 0) {
                    return Promo_Type.ARTEFACT;
                }
            }
            case "q", "Q" -> {
                if(yn("Confirmez : ")){
                    return Promo_Type.QUIT;
                }
            }
        }
        return promo();
    }

    /**
     * Demande au joueur l'action qu'il veut mener et la transmet
     * @param joueur le joueur qui réalise l'action
     * @param est_familier s'il s'agit d'un familier
     * @return l'action choisit
     * @throws IOException toujours
     */
    public static Action action(Joueur joueur, boolean est_familier) throws IOException {
        if(est_familier && joueur.familier_peut_pas_jouer()){
            return Action.AUTRE;
        }
        if(est_familier && !joueur.peut_diriger_familier()){
            return Action.F_SEUL;
        }
        String text;
        if (!est_familier) { // joueur
            text = joueur.text_action();
        } else { //familier
            text = joueur.f_text_action();
        }
        text += " : ";
        System.out.println(text);
        String input = read().toLowerCase();
        if (input.isEmpty()) {
            return Action.ATTAQUER;
        }
        return switch (input) {
            case "a" -> Action.ATTAQUER;
            case "f" -> Action.FUIR;
            case "c" -> Action.AUTRE;
            case "o" -> Action.OFF;
            case "j" -> Action.JOINDRE;

            // actions joueur
            case "t" -> {
                if (!est_familier && ! joueur.a_cecite()) {
                    yield Action.TIRER;
                }
                System.out.println("Action non reconnue.");
                yield action(joueur, true);
            }
            case "m" -> {
                if (!est_familier && !joueur.est_berserk()) {
                    yield Action.MAGIE;
                }
                System.out.println("Action non reconnue.");
                yield action(joueur, est_familier);
            }
            case "p" -> {
                if (!est_familier && !joueur.est_berserk()) {
                    yield Action.SOIGNER;
                }
                System.out.println("Action non reconnue.");
                yield action(joueur, est_familier);
            }

            // première ligne
            case "s" -> {
                if (!est_familier) {
                    if (joueur.est_front()) {
                        yield Action.ASSOMER;
                    } else {
                        yield Action.AVANCER;
                    }
                }
                if (joueur.est_front() && !joueur.a_familier_front()){
                    yield Action.AVANCER;
                }
                System.out.println("Action non reconnue.");
                yield action(joueur, true);
            }
            case "e" -> {
                if (joueur.est_front() && (!est_familier && !joueur.est_berserk()) ||
                        (joueur.est_front_f() && est_familier && !joueur.f_est_berserk())) {
                    yield Action.ENCAISSER;
                }
                System.out.println("Action non reconnue.");
                yield action(joueur, est_familier);
            }
            case "d" -> {
                if (joueur.est_front() && !est_familier &&!joueur.est_berserk()) {
                    yield Action.DOMESTIQUER;
                }
                System.out.println("Action non reconnue.");
                yield action(joueur, est_familier);
            }
            case "v" -> {
                if(est_familier && joueur.est_front() && !joueur.a_familier_front()){
                    yield Action.PROTEGER;
                }
                yield action(joueur, est_familier);
            }

            // actions particulières
            case "q" -> {
                if (yn("Confirmez ")) {
                    yield Action.END;
                }
                yield action(joueur, est_familier);
            }

            default -> {
                Action act = joueur.action(input, est_familier);
                if(act != Action.AUCUNE){
                    yield act;
                }
                System.out.println("Action non reconnue.");
                yield action(joueur, est_familier);
            }
        };
    }

    public static Action_extra extra(Joueur joueur, Action action) throws IOException {
        if(action == Action.AUCUNE || action == Action.END || action == Action.CONCOCTION || action == Action.DOMESTIQUER
        || action == Action.DEXTERITE || action == Action.LIEN || action == Action.MEDITATION){
            return Action_extra.AUCUNE;
        }
        String text = joueur.text_extra(action);
        System.out.println(text);
        String reponse = read().toLowerCase();
        return switch (reponse) {
            case "c" -> Action_extra.AUTRE;
            case "a", "", "\n" -> Action_extra.AUCUNE;
            case "n" -> Action_extra.ANALYSER;
            case "p" -> Action_extra.POTION;
            default -> {
                Action_extra temp = joueur.extra(reponse);
                if(temp != Action_extra.AUCUNE){
                    yield temp;
                }
                System.out.println("Unkown input.");
                yield extra(joueur, action);
            }
        };
    }

    /**
     * Demande au joueur qui il veut soigner
     * @param premier_ligne l'indice du participant de première ligne
     * @return si la cible est en première ligne
     */
    public static boolean ask_heal(int premier_ligne) throws IOException {
        int i = 0;
        Joueur joueur;
        while (true) {
            joueur = Main.joueurs[i];
            if (joueur.est_actif() && joueur.est_vivant()) {
                if (yn("Voulez vous soigner " + joueur.getNom() + " ?")) {
                    return i == premier_ligne;
                }
            }
            i = i == Main.nbj - 1 ? 0 : i + 1;
        }
    }

    /**
     * Demande au joueur ce qu'il fait de son tour
     * @param index l'index du joueur dont c'est le tour
     * @return : un choix correspondant
     */
    public static Choix tour(int index) throws IOException {
        Joueur joueur = Main.joueurs[index];
        Position position = joueur.getPosition();
        while (true) {
            String text = "Que voulez-vous faire : (E)xplorer";
            boolean peut_descendre =  position != Position.PRAIRIE && position != Position.ENFERS && position != Position.OLYMPE;
            boolean peut_monter = position != Position.OLYMPE;
            boolean market = position != Position.OLYMPE && position != Position.ENFERS;
            boolean peut_entrainer = joueur.a_familier() && !joueur.familier_loyalmax();
            text += joueur.text_tour();
            if(peut_descendre){
                text += "/(d)escendre";
            }
            if(peut_monter){
                text += "/(m)onter";
            }
            if(market){
                text += "/(a)ller au marché";
            }
            if(peut_entrainer){
                text += "/(en)trainer son familier";
            }
            text += "/(c)ustom/(s)tatistique ?";
            System.out.println(text);
            String readed = read().toLowerCase();
            switch (readed) {

                // normaux
                case "e", "explorer", "" -> {
                    return Choix.EXPLORER;
                }
                case "d", "descendre" -> {
                    if(peut_descendre){
                        return Choix.DESCENDRE;
                    }
                    System.out.println("Exterieur.Input unknow");
                }
                case "m", "monter" ->{
                    if(peut_monter){
                        return Choix.MONTER;
                    }
                    System.out.println("Exterieur.Input unknow");
                }
                case "a", "aller au marche", "aller au marché", "aller", "marche", "marché" -> {
                    if(market){
                        return Choix.MARCHE;
                    }
                    System.out.println("Exterieur.Input unknow");
                }
                case "entrainer son familier", "en" -> {
                    if(peut_entrainer) {
                        return Choix.DRESSER;
                    }
                    System.out.println("Exterieur.Input unknow");
                }
                case "c", "custom" -> {
                    return Choix.ATTENDRE;
                }
                case "s", "stat", "stats", "statistique", "statistiques" -> {
                    return Choix.STAT;
                }

                // caché
                case "q" -> {
                    System.out.println("Confirmez l'arret");
                    if(read().equals("q")) {
                        return Choix.QUITTER;
                    }
                }
                case "add" -> {
                    System.out.println("Confirmez l'addition");
                    if(read().equals("add")) {
                        return Choix.FAMILIER_PLUS;
                    }
                }
                case "del" -> {
                    System.out.println("Confirmez le descès");
                    if (read().equals("del")) {
                        return Choix.FAMILIER_MOINS;
                    }
                }
                case "re" -> {
                    System.out.println("Confirmez");
                    if(read().equals("re")) {
                        return Choix.RETOUR;
                    }
                }
                case "sui" -> {
                    System.out.println("Confirmez");
                    if(read().equals("sui")) {
                        return Choix.SUICIDE;
                    }
                }

                default -> {
                    if (joueur.tour(readed)) {
                        return Choix.AUCUN;
                    }
                    System.out.println("Exterieur.Input unknow");
                }
            }
        }
    }
}

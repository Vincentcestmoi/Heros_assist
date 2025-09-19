import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;

public class Input {


    // sauvegarde

    /**
     * Sauvegarde les données enregistrées, ne touche rien si les données sont innaccessibles
     */
    public int load() throws IOException {
        File fichier = new File(Main.Path + "Joueur A" + Main.Ext);
        int nbj = 0;
        if (!fichier.exists() || read_log("Joueur A").equals(";")) {
            return -1;
        }
        if (!yn("Sauvegarde détectée, charger cette sauvegarde ?") && yn("Confirmez la suppression")) {
            String[] nomFichier = {"Joueur A", "Joueur B", "Joueur C", "Joueur D", "enfers", "prairie", "vigne",
                    "temple", "mer", "mont", "olympe", "rangO", "rangI", "rangII", "rangIII", "rangIV",
                    "promo_monture", "promo_artefact", "promo_renforcement"};
            for(String s : nomFichier) {
                Output.delete_fichier(s);
            }
            System.out.println("lancement du jeu.\n\n");
            return -1;
        } else {
            //joueur
            if (load_ja()) {
                nbj++;
            }
            if(load_jb()){
                nbj++;
            }
            if(load_jc()){
                nbj++;
            }
            if(load_jd()){
                nbj++;
            }
            //monstre nommé
            corrige_nomme();
            //item unique
            corrige_item();
        }
        System.out.println("lancement du jeu.\n\n");
        return nbj;
    }

    /**
     * Supprime les objets uniques déjà tirés
     */
    private void corrige_item() {
        String[] nomFichier = {"rangO", "rangI", "rangII", "rangIII", "rangIV", "promo_monture", "promo_artefact", "promo_renforcement"};
        Rang[] rang = {Rang.O, Rang.I, Rang.II, Rang.III, Rang.IV, Rang.PROMOTION, Rang.PROMOTION, Rang.PROMOTION};
        Promo_Type[] promo = {Promo_Type.QUIT, Promo_Type.QUIT, Promo_Type.QUIT, Promo_Type.QUIT, Promo_Type.QUIT,
                Promo_Type.MONTURE, Promo_Type.ARTEFACT, Promo_Type.AMELIORATION};
        String log;
        for (int i = 0; i < promo.length; i++) {
            log = read_log(nomFichier[i]);
            if(log.equals(";") || log.isEmpty()) {
                continue;
            }
            StringBuilder temp = new StringBuilder();
            for (int j = 0; j < log.length(); j++) {
                if (log.charAt(j) == ',') {
                    Pre_Equipement.safe_delete(temp.toString(), rang[i], promo[i]);
                    temp = new StringBuilder();
                } else if (log.charAt(j) == ';' || log.charAt(j) == '\n') {
                    Pre_Equipement.safe_delete(temp.toString(), rang[i], promo[i]);
                    j = log.length() + 1;
                } else {
                    temp.append(log.charAt(j));
                }
            }
        }
    }

    /**
     * Supprime les monstres nommés enregistrés comme abscent
     */
    private void corrige_nomme() {
        String[] nomFichier = {"enfers", "prairie", "vigne", "temple", "mer", "mont", "olympe"};
        String log;
        for (String s : nomFichier) {
            log = read_log(s);
            if(log.equals(";") || log.isEmpty()) {
                continue;
            }
            StringBuilder temp = new StringBuilder();
            for (int j = 0; j < log.length(); j++) {
                if (log.charAt(j) == ',') {
                    Combat.delete_monstre(temp.toString());
                    temp = new StringBuilder();
                } else if (log.charAt(j) == ';' || log.charAt(j) == '\n') {
                    Combat.delete_monstre(temp.toString());
                    j = log.length() + 1;
                } else {
                    temp.append(log.charAt(j));
                }
            }
        }
    }

    private boolean load_ja() {
        if(load_j("Joueur A", Main.Joueur_A, 0)){
            System.out.println(Main.Joueur_A + " chargé(e).");
            return true;
        }
        return false;
    }

    private boolean load_jb() {
        if(load_j("Joueur B", Main.Joueur_B, 1)){
            System.out.println(Main.Joueur_B + " chargé(e).");
            return true;
        }
        return false;
    }

    private boolean load_jc() {
        if(load_j("Joueur C", Main.Joueur_C, 2)){
            System.out.println(Main.Joueur_C + " chargé(e).");
            return true;
        }
        return false;
    }

    private boolean load_jd() {
        if(load_j("Joueur D", Main.Joueur_D, 3)){
            System.out.println(Main.Joueur_D + " chargé(e).");
            return true;
        }
        return false;
    }

    /**
     * Charge les données d'un joueur
     * @param nomFichier le chemin de la sauvegarde
     * @param joueur le joueur à charger
     * @param index l'index correspondant au joueur
     * @return si le chargement a eu lieu
     * @implNote suppose que le fichier est correctement formaté, le cas contraire cause des crashs
     */
    private boolean load_j(String nomFichier, String joueur, int index) {
        String log = read_log(nomFichier);
        if(log.isEmpty() || log.equals(";")) {
            return false;
        }
        StringBuilder temp = new StringBuilder();
        int i = 0;
        while (log.charAt(i) != ',') {
            temp.append(log.charAt(i));
            i++;
        }
        if(!joueur.contentEquals(temp)){
            System.out.println("Mauvais joueur : attendu : " + joueur + ", obtenue : " + temp + ", lien impossible");
            System.exit(0);
        }
        temp = new StringBuilder();
        i++;
        while (log.charAt(i) != ',') {
            temp.append(log.charAt(i));
            i++;
        }
        switch (temp.toString()) {
            case "ENFERS" -> Main.positions[index] = Position.ENFERS;
            case "PRAIRIE" -> Main.positions[index] = Position.PRAIRIE;
            case "VIGNES" -> Main.positions[index] = Position.VIGNES;
            case "TEMPLE" -> Main.positions[index] = Position.TEMPLE;
            case "MER" -> Main.positions[index] = Position.MER;
            case "MONTS" -> Main.positions[index] = Position.MONTS;
            case "OLYMPE" -> Main.positions[index] = Position.OLYMPE;
            case "ASCENDANT" -> {
                System.out.println("ERROR : DONOT");
                Main.positions[index] = Position.PRAIRIE;
            }
        }
        switch (index){
            case 0 -> Main.f_a = (int)log.charAt(i + 1) - 48;
            case 1 -> Main.f_b = (int)log.charAt(i + 1) - 48;
            case 2 -> Main.f_c = (int)log.charAt(i + 1) - 48;
            case 3 -> Main.f_d = (int)log.charAt(i + 1) - 48;
        }
        return true;
    }

    /**
     * Lis un fichoer
     * @param cheminFichier le chemin du fichier
     * @return le contenu du fichier
     */
    public static String read_log(String cheminFichier) {
        File fichier = new File(Main.Path + cheminFichier + Main.Ext);
        StringBuilder text = new StringBuilder();

        if (!fichier.exists()) {
            System.out.println("Le fichier '" + cheminFichier + "' n'existe pas.");
            try {
                if (fichier.createNewFile()) {
                    System.out.println("✅ Fichier '" + cheminFichier + "' créé avec succès !");
                    return text.toString();
                }
            }
            catch (IOException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                text.append(ligne);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
        return text.toString();
    }

    //visuel (terminal)

    /**
     * Lit le texte en terminal
     *
     * @return le texte lu
     * @throws IOException en cas de problème ?
     */
    public String read() throws IOException {
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
    public int readInt() throws IOException {
        int number = 0;
        int temp = System.in.read();
        while (temp != 10) {
            number = number * 10 + (temp - 48);
            temp = System.in.read();
        }
        System.out.println();
        return number;
    }

    /**
     * Demande au joueur le résultat d'un jet 4 et le renvoie
     *
     * @return le chiffre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public int D4() throws IOException {
        System.out.print("D4 : ");
        Output.jouerSonDe();
        return readInt();
    }

    /**
     * Demande au joueur le résultat d'un jet 6 et le renvoie
     *
     * @return le chiffre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public int D6() throws IOException {
        System.out.print("D6 : ");
        Output.jouerSonDe();
        return readInt();
    }

    /**
     * Demande au joueur le résultat d'un jet 8 et le renvoie
     *
     * @return le chiffre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public int D8() throws IOException {
        System.out.print("D8 : ");
        Output.jouerSonDe();
        return readInt();
    }

    /**
     * Demande au joueur le résultat d'un jet 10 et le renvoie
     *
     * @return le chiffre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public int D10() throws IOException {
        System.out.print("D10 : ");
        Output.jouerSonDe();
        return readInt();
    }

    /**
     * Demande au joueur le résultat d'un jet 12 et le renvoie
     *
     * @return le chiffre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public int D12() throws IOException {
        System.out.print("D12 : ");
        Output.jouerSonDe();
        return readInt();
    }

    /**
     * Demande au joueur le résultat d'un jet 20 et le renvoie
     *
     * @return le chiffre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public int D20() throws IOException {
        System.out.print("D20 : ");
        Output.jouerSonDe();
        return readInt();
    }

    /**
     * Pose une question au joueur
     * @return s'il a répondu oui
     */
    public boolean yn(String question) throws IOException {
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
    public int atk() throws IOException {
        System.out.print("entrez votre attaque actuelle : ");
        return readInt();
    }

    /**
     * Demande au joueur sa vie
     *
     * @return le nombre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public int vie() throws IOException {
        System.out.print("entrez votre résistance actuelle : ");
        return readInt();
    }

    /**
     * Demande au joueur son armure
     *
     * @return le nombre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public int def() throws IOException {
        System.out.print("entrez votre armure actuelle : ");
        return readInt();
    }

    /**
     * Demande au joueur la force de son attaque magique
     *
     * @return le nombre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public int magie() throws IOException {
        System.out.print("entrez votre puissance d'attaque magique : ");
        return readInt();
    }

    /**
     * Laisse le joueur choisir sa promotion
     * @return un indice sur le type de promotion que le joueur veut
     */
    public Promo_Type promo() throws IOException {
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
     * Demande au joueur l'action qu'il veut mener et renvoie un int correspondant
     *
     * @param nom                le nom du joueur ou de son familier
     * @param est_familier       s'il s'agit d'un familier
     * @param est_premiere_ligne si la cible est en première ligne
     * @param mort la liste des morts
     * @return un int correspondant à l'action
     * @throws IOException en cas de problème ?
     */
    public Action action(String nom, Boolean est_familier, Boolean est_premiere_ligne, boolean[] mort) throws IOException {
        String text;
        boolean ya_mort = false;
        for (int i = 0; i < 4; i++) {
            if (mort[i]) {
                ya_mort = true;
                break;
            }
        }
        if (!est_familier) { // joueur
            text = nom + " entrez votre action : (A)ttaquer/(t)irer/(m)agie/(f)uir";
            if (est_premiere_ligne) {
                text += "/a(s)somer/(e)ncaisser/(d)omestiquer";
            } else {
                text += "/(s)'avancer";
            }
            text += "/(p)remier soin/a(n)alyser/(c)ustom/(o)ff";
            switch (nom) {
                case Main.necromancien -> text += "/(ma)udir";
                case Main.archimage -> text += "/(on)de de choc";
                case Main.alchimiste -> {
                    if (ya_mort) {
                        text += "/(re)ssuciter par potion";
                    }
                }
                case Main.guerriere -> text += "/(be)rserker/(la)me d'aura";
            }
        } else { //familier
            text = "Donnez un ordre au " + nom + " (A)ttaquer/(f)uir/(c)ustom/(o)ff";
            if (!est_premiere_ligne) {
                text += "/(s)'avancer";
            }
        }
        text += " : ";
        System.out.println(text);
        String input = read();
        if (input.equals("A") || input.equals("a") || input.isEmpty()) {
            return Action.ATTAQUER;
        }
        return switch (input) {
            case "F", "f" -> Action.FUIR;
            case "C", "c" -> Action.AUTRE;
            case "O", "o" -> Action.OFF;

            // actions joueur
            case "T", "t" -> {
                if (!est_familier) {
                    yield Action.TIRER;
                }
                System.out.println("Action non reconnue.");
                yield action(nom, true, est_premiere_ligne, mort);
            }
            case "M", "m" -> {
                if (!est_familier) {
                    yield Action.MAGIE;
                }
                System.out.println("Action non reconnue.");
                yield action(nom, true, est_premiere_ligne, mort);
            }
            case "P", "p" -> {
                if (!est_familier) {
                    yield Action.SOIGNER;
                }
                System.out.println("Action non reconnue.");
                yield action(nom, true, est_premiere_ligne, mort);
            }
            case "n", "N" -> {
                if (!est_familier) {
                    yield Action.ANALYSER;
                }
                System.out.println("Action non reconnue.");
                yield action(nom, true, est_premiere_ligne, mort);
            }

            // joueur en première ligne
            case "S", "s" -> {
                if (est_premiere_ligne && !est_familier) {
                    yield Action.ASSOMER;
                } else if (!est_premiere_ligne) {
                    yield Action.AVANCER;
                }
                System.out.println("Action non reconnue.");
                yield action(nom, true, true, mort);
            }
            case "E", "e" -> {
                if (est_premiere_ligne && !est_familier) {
                    yield Action.ENCAISSER;
                }
                System.out.println("Action non reconnue.");
                yield action(nom, est_familier, est_premiere_ligne, mort);
            }
            case "D", "d" -> {
                if (est_premiere_ligne && !est_familier) {
                    yield Action.DOMESTIQUER;
                }
                System.out.println("Action non reconnue.");
                yield action(nom, est_familier, est_premiere_ligne, mort);
            }

            // action métier
            case "ma", "MA", "Ma", "mA" -> {
                if (nom.equals(Main.necromancien)) {
                    yield Action.MAUDIR;
                }
                System.out.println("Action non reconnue.");
                yield action(nom, est_familier, est_premiere_ligne, mort);
            }
            case "on", "ON", "On", "oN" -> {
                if (nom.equals(Main.archimage)) {
                    yield Action.ONDE_CHOC;
                }
                System.out.println("Action non reconnue.");
                yield action(nom, est_familier, est_premiere_ligne, mort);
            }
            case "re", "RE", "Re", "rE" -> {
                if (ya_mort && nom.equals(Main.alchimiste)) {
                    yield Action.POTION_REZ;
                }
                System.out.println("Action non reconnue.");
                yield action(nom, est_familier, est_premiere_ligne, mort);
            }
            case "be", "BE", "Be", "bE" -> {
                if (nom.equals(Main.guerriere)) {
                    yield Action.BERSERK;
                }
                System.out.println("Action non reconnue.");
                yield action(nom, est_familier, est_premiere_ligne, mort);
            }
            case "la", "LA", "La", "lA" -> {
                if (nom.equals(Main.guerriere)) {
                    yield Action.LAME_DAURA;
                }
                System.out.println("Action non reconnue.");
                yield action(nom, est_familier, est_premiere_ligne, mort);
            }

            // actions particulières
            case "q", "Q" -> {
                if (yn("Confirmez ")) {
                    yield Action.END;
                }
                yield action(nom, est_familier, est_premiere_ligne, mort);
            }
            case "r", "R" -> {
                if (yn("Confirmez ")) {
                    yield Action.RETOUR;
                }
                yield action(nom, est_familier, est_premiere_ligne, mort);
            }

            default -> {
                System.out.println("Action non reconnue.");
                yield action(nom, est_familier, est_premiere_ligne, mort);
            }
        };
    }

    /**
     * Demande au joueur qui il veut soigner
     * @param nom la liste des noms des participants
     * @param actif le booléen d'activiter des participants
     * @param premier_ligne l'indice du participant de première ligne
     * @return si la cible est en première ligne
     */
    public boolean ask_heal(String[] nom, boolean[] actif, int premier_ligne) throws IOException {
        int i = 0;
        while (true) {
            if (actif[i]) {
                if (yn("Voulez vous soigner " + nom[i] + " ?")) {
                    return i == premier_ligne;
                }
            }
            i = i == 8 ? 0 : i + 1;
        }
    }

    /**
     * Demande au joueur qui il veut ressuciter
     * @param mort le booléen de descès des participants
     * @return l'indice du ressucité
     */
    public int ask_rez(boolean[] mort) throws IOException {
        boolean ok = false;
        for(int i = 0; i < 4; i++){
            if (mort[i]) {
                ok = true;
                break;
            }
        }
        if(!ok){
            System.out.println(Arrays.toString(mort));
            return -1;
        }
        int i = 0;
        while (true) {
            if (mort[i]) {
                if (yn("Voulez vous rescussiter " + Main.nom[i] + " ?")) {
                    return i;
                }
            }
            i = i == 4 ? 0 : i + 1;
        }
    }

    /**
     * Demande au joueur ce qu'il fait de son tour
     * @param position la position du joueur
     * @param obe l'obéissance du familier du joueur
     * @return : un choix correspondant
     */
    Choix tour(Position position, int obe) throws IOException {
        while (true) {
            String text = "Que voulez-vous faire : (E)xplorer";
            boolean peut_descendre =  position != Position.PRAIRIE && position != Position.ENFERS && position != Position.OLYMPE;
            boolean peut_monter = position != Position.OLYMPE;
            boolean market = position != Position.OLYMPE && position != Position.ENFERS;
            boolean peut_entrainer = obe > 0 && obe < 3;
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
            text += "/(c)ustom ?";
            System.out.println(text);
            switch (read()) {
                case "E", "e", "explorer", "Explorer", "" -> {
                    return Choix.EXPLORER;
                }
                case "d", "D", "Descendre", "descendre" -> {
                    if(peut_descendre){
                        return Choix.DESCENDRE;
                    }
                    System.out.println("Input unknow");
                }
                case "m", "M", "monter", "Monter" ->{
                    if(peut_monter){
                        return Choix.MONTER;
                    }
                    System.out.println("Input unknow");
                }
                case "a", "A", "Aller au marche", "aller au marche", "Aller au marché", "aller au marché", "Aller",
                     "aller", "marche", "Marche", "Marché", "marché" -> {
                    if(market){
                        return Choix.MARCHE;
                    }
                    System.out.println("Input unknow");
                }
                case "entrainer son familier", "Entrainer son familier", "en", "En", "EN", "eN" -> {
                    if(peut_entrainer) {
                        return Choix.DRESSER;
                    }
                    System.out.println("Input unknow");
                }
                case "c", "C", "Custom", "custom" -> {
                    return Choix.ATTENDRE;
                }
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
                default -> System.out.println("Input unknow");
            }
        }
    }

}

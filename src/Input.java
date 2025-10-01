import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Objects;

public class Input {

    private final String[] fichLoad = {"Joueur A", "Joueur B", "Joueur C", "Joueur D", "Joueur E", "Joueur F", "Joueur G",
            "Joueur H", "enfers", "prairie", "vigne", "temple", "mer", "mont", "olympe", "rangO", "rangI", "rangII", "rangIII", "rangIV",
            "promo_monture", "promo_artefact", "promo_renforcement"};


    // sauvegarde

    /**
     * Sauvegarde les données enregistrées, ne touche rien si les données sont innaccessibles
     */
    public void load() throws IOException {
        File fichier = new File(Main.Path + fichLoad[0] + Main.Ext);
        if (!fichier.exists() || read_log(fichLoad[0]).equals(";")) {
            return;
        }
        if (!yn("Sauvegarde détectée, charger cette sauvegarde ?") && yn("Confirmez la suppression")) {
            for(String s : fichLoad) {
                Output.delete_fichier(s);
            }
            System.out.println("lancement du jeu.\n\n");
            return;
        }
        else {
            // taille temporaire pour les data
            Main.nom = new String[Main.nbj_max];
            Main.f = new int[Main.nbj_max];
            Main.positions = new Position[Main.nbj_max];
            Main.metier = new Metier[Main.nbj_max];

            //joueurs
            for(int i = 0; i < Main.nbj_max; i++) {
                if (load_j(fichLoad[i], i)) {
                    System.out.println(Main.nom[i] + ", " + Output.texte_metier(Main.metier[i]) + " chargé(e) avec succès " + Main.texte_pos(Main.positions[i]) + ".");
                    Main.nbj++;
                }
                else{
                    System.out.println(Main.nbj + " joueurs chargés avec succés.");
                    break;
                }
            }

            // on réduit à la bonne taille les listes
            String[] t_nom = new String[Main.nbj];
            System.arraycopy(Main.nom, 0, t_nom, 0, Main.nbj);
            Main.nom = t_nom;
            int[] t_f = new int[Main.nbj];
            System.arraycopy(Main.f, 0, t_f, 0, Main.nbj);
            Main.f = t_f;
            Position[] t_pos = new Position[Main.nbj];
            System.arraycopy(Main.positions, 0, t_pos, 0, Main.nbj);
            Main.positions = t_pos;
            Metier[] metier = new Metier[Main.nbj];
            System.arraycopy(Main.metier, 0, metier, 0, Main.nbj);
            Main.metier = metier;

            //monstre nommé
            corrige_nomme();
            //item unique
            corrige_item();
        }
        System.out.println("lancement du jeu.\n\n");
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

    /**
     * Charge les données d'un joueur
     * @param nomFichier le chemin de la sauvegarde
     * @param index l'index correspondant au joueur
     * @return si le chargement a eu lieu
     * @implNote suppose que le fichier est correctement formaté, le cas contraire cause des crashs
     */
    private boolean load_j(String nomFichier, int index) {
        String log = read_log(nomFichier);
        if(log.isEmpty() || log.charAt(0) == ';') {
            return false;
        }

        //nom
        StringBuilder temp = new StringBuilder();
        int i = 0;
        while (log.charAt(i) != ',') {
            temp.append(log.charAt(i));
            i++;
        }
        Main.nom[index] = temp.toString();
        temp = new StringBuilder();
        i++;

        //metier
        while (log.charAt(i) != ',') {
            temp.append(log.charAt(i));
            i++;
        }
        setJob(String.valueOf(temp), index);
        temp = new StringBuilder();
        i++;

        // position
        while (log.charAt(i) != ',') {
            temp.append(log.charAt(i));
            i++;
        }
        Main.positions[index] = switch (temp.toString()) {
            case "ENFERS" ->  Position.ENFERS;
            case "PRAIRIE" ->  Position.PRAIRIE;
            case "VIGNES" ->  Position.VIGNES;
            case "TEMPLE" ->  Position.TEMPLE;
            case "MER" ->  Position.MER;
            case "MONTS" ->  Position.MONTS;
            case "OLYMPE" ->  Position.OLYMPE;
            case "ASCENDANT" -> {
                System.out.println("ERROR : DONOT");
                yield Position.PRAIRIE;
            }
            default -> {
                System.out.println("ERROR : UNKNOW POSITION : " + temp);
                yield Position.PRAIRIE;
            }
        };

        //familier
        Main.f[index] = (int)log.charAt(i + 1) - 48;
        return true;
    }

    private void setJob(String temp, int index) {
        Metier job;
        if(Objects.equals(temp, Output.texte_metier(Metier.NECROMANCIEN))) {
            job = Metier.NECROMANCIEN;
        }
        else if(Objects.equals(temp, Output.texte_metier(Metier.ARCHIMAGE))) {
            job = Metier.ARCHIMAGE;
        }
        else if(Objects.equals(temp, Output.texte_metier(Metier.ALCHIMISTE))) {
            job = Metier.ALCHIMISTE;
        }
        else if(Objects.equals(temp, Output.texte_metier(Metier.GUERRIERE))) {
            job = Metier.GUERRIERE;
        }
        else if(Objects.equals(temp, Output.texte_metier(Metier.RANGER))) {
            job = Metier.RANGER;
        }
        else {
            job = Metier.AUCUN;
        }
        Main.metier[index] = job;
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

    // ********************************************************************************************************** //

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
     * Demande au joueur son attaque
     *
     * @return le nombre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public int tir() throws IOException {
        System.out.print("entrez votre puissance de tir actuelle : ");
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
     * @param index              l'index du joueur (ou du familié associé)
     * @param est_familier       s'il s'agit d'un familier
     * @param est_premiere_ligne si la cible est en première ligne
     * @param mort la liste des morts
     * @return un int correspondant à l'action
     * @throws IOException en cas de problème ?
     */
    public Action action(int index, Boolean est_familier, Boolean est_premiere_ligne, boolean[] mort, boolean est_berserk) throws IOException {
        String text;
        boolean ya_mort = false;
        for (int i = 0; i < 4; i++) {
            if (mort[i]) {
                ya_mort = true;
                break;
            }
        }
        if (!est_familier) { // joueur
            text = Main.nom[index] + " entrez votre action : (A)ttaquer/(t)irer";
            if(!est_berserk && Main.metier[index] != Metier.ARCHIMAGE){
                text += "/(m)agie";
            }
            if (est_premiere_ligne) {
                text += "/a(s)sommer";
                if(!est_berserk){
                    text += "/(e)ncaisser/(d)omestiquer";
                }
            } else {
                text += "/(s)'avancer";
            }
            if(!est_berserk){
                text += "/(p)remier soin/a(n)alyser";
            }
            text += "/(f)uir/(c)ustom/(o)ff";
            text += switch(Main.metier[index]) {
                case NECROMANCIEN -> {
                    if (!est_berserk) {
                        yield "/(ma)udir";
                    }
                    yield "";
                }
                case ARCHIMAGE -> {
                        if(!est_berserk) {
                            yield "/(me)ditation/(so)rt";
                        }
                        yield "";
                }
                case ALCHIMISTE -> {
                    if(est_berserk) {
                        yield "";
                    }
                    if (ya_mort) {
                        yield "/(re)ssuciter par potion/(co)ncocter des potions/(fo)uiller";
                    }
                    yield "/(co)ncocter des potions/(fo)uiller";
                }
                case GUERRIERE -> {
                    if(!est_berserk) {
                        yield "/(be)rserker/(la)me d'aura";
                    }
                    yield "/(la)me d'aura";
                }
                case RANGER -> {
                    if(est_premiere_ligne && est_berserk) {
                        yield "/(as)saut";
                    }
                    if(!est_berserk){
                        if(est_premiere_ligne){
                            yield "/(co)ut critique";
                        }
                        yield "/(co)up critique/(as)sassinat";
                    }
                    yield "";
                }
                case AUCUN -> "";
            };
        } else { //familier
            text = "Donnez un ordre au familier de " + Main.nom[index - 4] + " (A)ttaquer/(f)uir/(c)ustom/(o)ff";
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
                yield action(index, true, est_premiere_ligne, mort, est_berserk);
            }
            case "M", "m" -> {
                if (!est_familier && !est_berserk) {
                    yield Action.MAGIE;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }
            case "P", "p" -> {
                if (!est_familier && !est_berserk) {
                    yield Action.SOIGNER;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }
            case "n", "N" -> {
                if (!est_familier && !est_berserk) {
                    yield Action.ANALYSER;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }

            // joueur en première ligne
            case "S", "s" -> {
                if (est_premiere_ligne && !est_familier) {
                    yield Action.ASSOMER;
                } else if (!est_premiere_ligne) {
                    yield Action.AVANCER;
                }
                System.out.println("Action non reconnue.");
                yield action(index, true, true, mort, est_berserk);
            }
            case "E", "e" -> {
                if (est_premiere_ligne && !est_familier && !est_berserk) {
                    yield Action.ENCAISSER;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }
            case "D", "d" -> {
                if (est_premiere_ligne && !est_familier &&!est_berserk) {
                    yield Action.DOMESTIQUER;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }

            // action métier
            case "ma", "MA", "Ma", "mA" -> {
                if (index < Main.nbj && Main.metier[index] == Metier.NECROMANCIEN && !est_berserk) {
                    yield Action.MAUDIR;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }

            case "me", "ME", "Me", "eM" -> {
                if (index < Main.nbj && Main.metier[index] == Metier.ARCHIMAGE && !est_berserk) {
                    yield Action.MEDITATION;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }
            case "so", "SO", "So", "sO" -> {
                if (index < Main.nbj && Main.metier[index] == Metier.ARCHIMAGE && !est_berserk) {
                    yield Action.SORT;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }
            case "fo", "FO", "Fo", "fO" -> {
                if (index < Main.nbj && Main.metier[index] == Metier.ALCHIMISTE && !est_berserk){
                    yield Action.FOUILLE;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }
            case "co", "CO", "Co", "cO" -> {
                if (index < Main.nbj && Main.metier[index] == Metier.ALCHIMISTE && !est_berserk) {
                    yield Action.CONCOCTION;
                }
                if (index < Main.nbj && Main.metier[index] == Metier.RANGER && !est_berserk) {
                    yield Action.CRITIQUE;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }
            case "re", "RE", "Re", "rE" -> {
                if (ya_mort && index < Main.nbj && Main.metier[index] == Metier.ALCHIMISTE && !est_berserk) {
                    yield Action.POTION_REZ;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }

            case "be", "BE", "Be", "bE" -> {
                if (index < Main.nbj && Main.metier[index] == Metier.GUERRIERE && !est_berserk) {
                    yield Action.BERSERK;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }
            case "la", "LA", "La", "lA" -> {
                if (index < Main.nbj && Main.metier[index] == Metier.GUERRIERE) {
                    yield Action.LAME_DAURA;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }
            case "as", "AS", "As", "aS" -> {
                if (index < Main.nbj && Main.metier[index] == Metier.RANGER) {
                    if(est_berserk && est_premiere_ligne){
                        yield Action.ASSAUT;
                    }
                    yield Action.ASSASSINAT;
                }
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }

            // actions particulières
            case "q", "Q" -> {
                if (yn("Confirmez ")) {
                    yield Action.END;
                }
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }
            case "r", "R" -> {
                if (yn("Confirmez ")) {
                    yield Action.RETOUR;
                }
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
            }

            default -> {
                System.out.println("Action non reconnue.");
                yield action(index, est_familier, est_premiere_ligne, mort, est_berserk);
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
    Choix tour(Position position, int obe, int index) throws IOException {
        while (true) {
            String text = "Que voulez-vous faire : (E)xplorer";
            boolean peut_descendre =  position != Position.PRAIRIE && position != Position.ENFERS && position != Position.OLYMPE;
            boolean peut_monter = position != Position.OLYMPE;
            boolean market = position != Position.OLYMPE && position != Position.ENFERS;
            boolean peut_entrainer = obe > 0 && obe < 3;
            if(Main.metier[index] == Metier.ALCHIMISTE){
                text += "/(fo)uiller/(co)ncocter des potions";
            }
            if(Main.metier[index] == Metier.ARCHIMAGE){
                text += "/(me)ditation";
            }
            if(Main.metier[index] == Metier.NECROMANCIEN){
                text += "/(ap)pel des morts";
            }
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

                // normaux
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

                // par métier
                case "ap", "AP", "Ap", "aP" -> {
                    if(Main.metier[index] == Metier.NECROMANCIEN){
                        return Choix.NECROMANCIE;
                    }
                    System.out.println("Input unknow");
                }
                case "fo", "FO", "Fo", "fO" -> {
                    if(Main.metier[index] == Metier.ALCHIMISTE){
                        return Choix.FOUILLE;
                    }
                    System.out.println("Input unknow");
                }
                case "co", "CO", "Co", "cO" -> {
                    if(Main.metier[index] == Metier.ALCHIMISTE){
                        return Choix.CONCOCTION;
                    }
                    System.out.println("Input unknow");
                }
                case "me", "ME", "Me", "mE" -> {
                    if(Main.metier[index] == Metier.ARCHIMAGE){
                        return Choix.MEDITATION;
                    }
                    System.out.println("Input unknow");
                }
                case "adgr" -> {
                    if(Main.metier[index] == Metier.ARCHIMAGE){
                        System.out.println("Confirmez");
                        if(read().equals("adgr")) {
                            return Choix.ADD_GREAT_RUNE;
                        }
                    }
                    else {
                        System.out.println("Input unknow");
                    }
                }
                case "admr" -> {
                    if(Main.metier[index] == Metier.ARCHIMAGE){
                        System.out.println("Confirmez");
                        if(read().equals("admr")) {
                            return Choix.ADD_MINOR_RUNE;
                        }
                    }
                    else {
                        System.out.println("Input unknow");
                    }
                }
                case "dgr" -> {
                    if(Main.metier[index] == Metier.ARCHIMAGE){
                        System.out.println("Confirmez");
                        if(read().equals("dgr")) {
                            return Choix.DEL_GREAT_RUNE;
                        }
                    }
                    else {
                        System.out.println("Input unknow");
                    }
                }
                case "dmr" -> {
                    if(Main.metier[index] == Metier.ARCHIMAGE){
                        System.out.println("Confirmez");
                        if(read().equals("dmr")) {
                            return Choix.DEL_MINOR_RUNE;
                        }
                    }
                    else {
                        System.out.println("Input unknow");
                    }
                }
                default -> System.out.println("Input unknow");
            }
        }
    }

    /**
     * Laisse l'alchimiste choisir la potion qu'il veut créer
     * @return le type de potion
     * @throws IOException toujours
     */
    public Action concoction() throws IOException {
        System.out.println("Quel type de potion voulez vous concocter : (re)sistance/(al)éatoire/(di)vine/en (se)rie/" +
                "(en)ergie/(fo)rce/(in)stable/(mi)racle/(so)in/(to)xique/(c)ustom ?");
        return switch(read()){
            case "re", "RE", "Re", "rE" -> Action.RESISTANCE;
            case "al", "AL", "Al", "aL" -> Action.ALEATOIRE;
            case "di", "DI", "Di", "dI" -> Action.DIVINE;
            case "se", "SE", "Se", "sE" -> Action.SERIE;
            case "en", "EN", "En", "enE" -> Action.ENERGIE;
            case "fo", "FO", "Fo", "fO" -> Action.FORCE;
            case "in", "IN", "In", "iN" -> Action.INSTABLE;
            case "mi", "MI", "Mi", "mI" -> Action.MIRACLE;
            case "so", "SO", "So", "sO" -> Action.SOIN;
            case "to", "TO", "To", "tO" -> Action.TOXIQUE;
            case "c", "C" -> Action.AUTRE;
            default -> {
                System.out.println("Input unknow");
                yield concoction();
            }
        };
    }

    /**
     * Demande à l'archimage quel sort il veut lancer
     * @return le sort à lancer
     * @throws IOException toujours
     */
    public Action sort() throws IOException {
        System.out.println("Quel sort voulez vous lancer : (bo)ule de feu/(ar)mure de glace/(on)de de choc/(fo)udre/(c)ustom ?");
        return switch(read()){
            case "bo", "BO", "Bo", "bO" -> Action.BDF;
            case "ar", "AR", "Ar", "aR" -> Action.ADG;
            case "on", "ON", "On", "oN" -> Action.ONDE_CHOC;
            case "fo", "FO", "Fo", "fO" -> Action.FOUDRE;
            case "c", "C" -> Action.AUTRE;
            default -> {
                System.out.println("Input unknow");
                yield sort();
            }
        };
    }
}

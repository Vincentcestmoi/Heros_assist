import java.io.IOException;

public class Input {

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
            System.out.print("Réponse non comprise");
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
        System.out.print("entrez votre vie actuelle : ");
        return readInt();
    }

    /**
     * Demande au joueur son armure
     *
     * @return le nombre donné par le joueur
     * @throws IOException en cas de problème ?
     */
    public int def() throws IOException {
        System.out.print("entrez votre défense actuelle : ");
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
     * Demande au joueur l'action qu'il veut mener et renvoie un int correspondant
     *
     * @param nom                le nom du joueur ou de son familier
     * @param est_familier       s'il s'agit d'un familier
     * @param est_premiere_ligne si la cible est en première ligne
     * @return un int correspondant à l'action
     * @throws IOException en cas de problème ?
     */
    public Action action(String nom, Boolean est_familier, Boolean est_premiere_ligne) throws IOException {
        if (!est_familier) { // joueur
            if (est_premiere_ligne) {
                System.out.println(nom + " entrez votre action : Attaquer(A)/Tirer(t)/Magie(m)/Fuir(f)/aSsomer(s)/Encaisser(e)/Premier soin(p)/Domestiquer(d)/aNalyser(n)/Custom(C)/Off(O) : ");
                String input = read();
                if (input.equals("A") || input.equals("a") || input.isEmpty()) {
                    return Action.ATTAQUER;
                }
                switch (input) {
                    case "T", "t" -> {
                        return Action.TIRER;
                    }
                    case "M", "m" -> {
                        return Action.MAGIE;
                    }
                    case "F", "f" -> {
                        return Action.FUIR;
                    }
                    case "S", "s" -> {
                        return Action.ASSOMER;
                    }
                    case "E", "e" -> {
                        return Action.ENCAISSER;
                    }
                    case "P", "p" -> {
                        return Action.SOIGNER;
                    }
                    case "D", "d" -> {
                        return Action.DOMESTIQUER;
                    }
                    case "n", "N" -> {
                        return Action.ANALYSER;
                    }
                    case "C", "c" -> {
                        return Action.AUTRE;
                    }
                    case "O", "o" -> {
                        return Action.ETRE_MORT;
                    }
                    case "q", "Q" -> {
                        if(yn("Confirmez ")){
                            return Action.END;
                        }
                        return action(nom, false, true);
                    }
                    default -> {
                        System.out.println("Entrée non reconnue, attaque classique appliquée");
                        return Action.ATTAQUER;
                    }
                }
            } else {
                System.out.println(nom + " entrez votre action : Attaquer(A)/Tirer(t)/Magie(m)/Fuir(f)/Premier soin(p)/aNalyser(n)/Custom(C)/Off(O)/S'avancer(s) : ");
                String input = read();
                if (input.equals("A") || input.equals("a") || input.isEmpty()) {
                    return Action.ATTAQUER;
                }
                switch (input) {
                    case "T", "t" -> {
                        return Action.TIRER;
                    }
                    case "M", "m" -> {
                        return Action.MAGIE;
                    }
                    case "F", "f" -> {
                        return Action.FUIR;
                    }
                    case "P", "p" -> {
                        return Action.SOIGNER;
                    }
                    case "n", "N" -> {
                        return Action.ANALYSER;
                    }
                    case "C", "c" -> {
                        return Action.AUTRE;
                    }
                    case "O", "o" -> {
                        return Action.ETRE_MORT;
                    }
                    case "S", "s" -> {
                        return Action.AVANCER;
                    }
                    case "q", "Q" -> {
                        if(yn("Confirmez ")){
                            return Action.END;
                        }
                        return action(nom, false, true);
                    }
                    default -> {
                        System.out.println("Entrée non reconnue, attaque classique appliquée");
                        return Action.ATTAQUER;
                    }
                }
            }
        } else { // familier
            if (est_premiere_ligne) {
                System.out.println("Donnez un ordre au " + nom + " Attaquer(A)/Fuir(f)/Custom(C)/Off(O) : ");
                String input = read();
                if (input.equals("A") || input.equals("a") || input.isEmpty()) {
                    return Action.ATTAQUER;
                }
                switch (input) {
                    case "F", "f" -> {
                        return Action.FUIR;
                    }
                    case "C", "c" -> {
                        return Action.AUTRE;
                    }
                    case "O", "o" -> {
                        return Action.ETRE_MORT;
                    }
                    case "q", "Q" -> {
                        if(yn("Confirmez ")){
                            return Action.END;
                        }
                        return action(nom, false, true);
                    }
                    default -> {
                        System.out.println("Entrée non reconnue, attaque classique appliquée");
                        return Action.ATTAQUER;
                    }
                }
            } else {
                System.out.println("Donnez un ordre au " + nom + " Attaquer(A)/Fuir(f)/Custom(C)/Off(O)/S'avancer(s) : ");
                String input = read();
                if (input.equals("A") || input.equals("a") || input.isEmpty()) {
                    return Action.ATTAQUER;
                }
                switch (input) {
                    case "F", "f" -> {
                        return Action.FUIR;
                    }
                    case "C", "c" -> {
                        return Action.AUTRE;
                    }
                    case "O", "o" -> {
                        return Action.ETRE_MORT;
                    }
                    case "S", "s" -> {
                        return Action.AVANCER;
                    }
                    case "q", "Q" -> {
                        if(yn("Confirmez ")){
                            return Action.END;
                        }
                        return action(nom, false, true);
                    }
                    default -> {
                        System.out.println("Entrée non reconnue, attaque classique appliquée");
                        return Action.ATTAQUER;
                    }
                }
            }
        }
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
                i = i == 8 ? 0 : i + 1;
            }
        }
    }

    /**
     * Demande au joueur ce qu'il fait de son tour
     * @return : un choix correspondant
     */
    Choix tour() throws IOException {
        while (true) {
            System.out.println("Que voulez-vous faire ?");
            switch (read()) {
                case "prairie", "prairi", "Prairi", "Prairie" -> {
                    return Choix.EXP_PRAIRIE;
                }
                case "enfer", "Enfer", "Enfers", "enfers" -> {
                    return Choix.EXP_ENFERS;
                }
                case "vigne", "Vigne", "Vignes", "vignes" -> {
                    return Choix.EXP_VIGNE;
                }
                case "Temple", "temple" -> {
                    return Choix.EXP_TEMPLE;
                }
                case "mer", "Mer" -> {
                    return Choix.EXP_MER;
                }
                case "Mont", "mont" -> {
                    return Choix.EXP_MONT;
                }
                case "Olympe", "olympe" -> {
                    return Choix.EXP_OLYMPE;
                }
                case "domestiquer", "Domestiquer", "Dompter", "dompter", "Dresser", "dresser" -> {
                    return Choix.DRESSER;
                }
                case "Passer", "passer" , "pass", "Pass", "skip", "Skip" -> {
                    return Choix.ATTENDRE;
                }
                case "q" -> {
                    System.out.println("Confirmez l'arret");
                    if(read().equals("q")) {
                        return Choix.QUITTER;
                    }
                }
                case "10" -> {
                    System.out.println("Confirmez l'addition");
                    if(read().equals("10")) {
                        return Choix.FAMILIER_PLUS;
                    }
                }
                case "11" -> {
                    System.out.println("Confirmez le descès");
                    if (read().equals("11")) {
                        return Choix.FAMILIER_MOINS;
                    }
                }
                case "12" -> {
                    System.out.println("Confirmez");
                    if(read().equals("12")) {
                        return Choix.RETOUR;
                    }
                }
                default -> System.out.println("Input unknow");
            }
        }
    }

}

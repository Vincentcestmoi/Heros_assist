import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.sound.sampled.*;

public class Input {


    // sons

    /**
     * Joue un son de dés pendant 2,3 secondes (bloque le terminal durant ce temp)
     */
    private void jouerSonDe() {
        try {
            File fichierAudio = new File("son_des.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(fichierAudio);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

            // Attendre la durée souhaitée, puis arrêter le son
            Thread.sleep(2300);
            clip.stop();
            clip.close();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException | InterruptedException e) {
            System.err.println("Erreur lors de la lecture du son : " + e.getMessage());
        }
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
        jouerSonDe();
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
        jouerSonDe();
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
        jouerSonDe();
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
        jouerSonDe();
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
        jouerSonDe();
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
        for(int i = 0; i < 4; i++){
            if(mort[i]){
                ya_mort = true;
                break;
            }
        }
        if (!est_familier) { // joueur
            text = nom + " entrez votre action : (A)ttaquer/(t)irer/(m)agie/(f)uir";
            if (est_premiere_ligne) {
                text += "/a(s)somer/(e)ncaisser/(d)omestiquer";
            }
            else{
                text += "/(s)'avancer";
            }
            text += "/(p)remier soin/a(n)alyser/(c)ustom/(o)ff";
            switch (nom) {
                case Main.Joueur_A -> text += "/(ma)udir";
                case Main.Joueur_B -> text += "/(on)de de choc";
                case Main.Joueur_C -> {
                    if (ya_mort) {
                        text += "/(re)ssuciter par potion";
                    }
                }
            }
            text += " : ";
            System.out.println(text);
            String input = read();
            if (input.equals("A") || input.equals("a") || input.isEmpty()) {
                return Action.ATTAQUER;
            }
            return switch (input) {
                case "T", "t" -> Action.TIRER;
                case "M", "m" -> Action.MAGIE;
                case "F", "f" -> Action.FUIR;
                case "P", "p" -> Action.SOIGNER;
                case "n", "N" -> Action.ANALYSER;
                case "C", "c" -> Action.AUTRE;
                case "O", "o" -> Action.ETRE_MORT;
                case "S", "s" -> {
                    if (est_premiere_ligne) {
                        yield Action.ASSOMER;
                    }
                    else{
                        yield Action.AVANCER;
                    }
                }
                case "E", "e" -> {
                    if (est_premiere_ligne) {
                        yield Action.ENCAISSER;
                    }
                    System.out.println("Action non reconnue.");
                    yield action(nom, false, false, mort);
                }
                case "D", "d" -> {
                    if (est_premiere_ligne) {
                        yield Action.DOMESTIQUER;
                    }
                    System.out.println("Action non reconnue.");
                    yield action(nom, false, false, mort);
                }
                case "q", "Q" -> {
                    if (yn("Confirmez ")) {
                        yield Action.END;
                    }
                    yield action(nom, false, est_premiere_ligne, mort);
                }
                case "ma", "MA", "Ma", "mA" -> {
                    if (nom.equals(Main.Joueur_A)) {
                        yield Action.MAUDIR;
                    }
                    System.out.println("Action non reconnue.");
                    yield action(nom, false, est_premiere_ligne, mort);
                }
                case "on", "ON", "On", "oN" -> {
                    if (nom.equals(Main.Joueur_B)) {
                        yield Action.ONDE_CHOC;
                    }
                    System.out.println("Action non reconnue.");
                    yield action(nom, false, est_premiere_ligne, mort);
                }
                case "re", "RE", "Re", "rE" -> {
                    if (ya_mort && nom.equals(Main.Joueur_C)) {
                        yield Action.POTION_REZ;
                    }
                    System.out.println("Action non reconnue.");
                    yield action(nom, false, est_premiere_ligne, mort);
                }
                default -> {
                    System.out.println("Action non reconnue.");
                    yield action(nom, false, est_premiere_ligne, mort);
                }
            };
        }
        else { // familier
            text = "Donnez un ordre au " + nom + " (A)ttaquer/(f)uir/(c)ustom/(o)ff";
            if(!est_premiere_ligne){
                text += "/(s)'avancer";
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
                case "O", "o" -> Action.ETRE_MORT;
                case "s", "S" -> {
                    if(!est_premiere_ligne) {
                        yield Action.AVANCER;
                    }
                    System.out.println("Action non reconnue.");
                    yield action(nom, true, true, mort);
                }
                case "q", "Q" -> {
                    if(yn("Confirmez ")){
                        yield Action.END;
                    }
                    yield action(nom, true, est_premiere_ligne, mort);
                }
                default -> {
                    System.out.println("Action non reconnue.");
                    yield action(nom, true, est_premiere_ligne, mort);
                }
            };
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
     * @return : un choix correspondant
     */
    Choix tour(Position position) throws IOException {
        while (true) {
            String text = "Que voulez-vous faire : (E)xplorer";
            boolean peut_descendre =  position != Position.PRAIRIE && position != Position.ENFERS && position != Position.OLYMPE;
            boolean peut_monter = position != Position.OLYMPE;
            boolean market = position != Position.OLYMPE && position != Position.ENFERS;
            if(peut_descendre){
                text += "/(d)escendre";
            }
            if(peut_monter){
                text += "/(m)onter";
            }
            if(market){
                text += "/(a)ller au marché";
            }
            text += "/(en)trainer son familier/(c)ustom ?";
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
                    return Choix.DRESSER;
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

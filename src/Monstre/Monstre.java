package Monstre;

import Auxiliaire.Texte;
import Auxiliaire.Utilitaire;
import Enum.Competence;
import Enum.Genre;
import Enum.Position;
import Equipement.Equipement;
import Exterieur.Input;
import Exterieur.Output;
import Exterieur.SaveManager;
import Metiers.Joueur;
import main.Combat;
import main.Main;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Monstre {
    protected String nom;
    protected int attaque;
    protected int vie;
    protected int vie_max;
    protected int armure;
    protected Competence competence;
    protected Boolean etourdi;      // quand l'adversaire utilise "assommer"
    protected Boolean assomme;       // quand l'adversaire utilise "assommer"
    protected int niveau_drop_min;
    protected int niveau_drop_max;
    protected int drop_quantite;
    protected float encaissement;   // quand l'adversaire utilise "encaisser"
    protected float part_soin;      // quand l'adversaire utilise "premier soin" en première ligne.
    protected int etat;         //l'état du corps du monstre
    protected Genre genre;
    protected final boolean est_nomme;
    
    // stats de base à conserver
    protected int vie_base;
    protected int attaque_base;
    protected int armure_base;
    
    static Random rand = new Random();
    
    Monstre(Race race) {
        this.nom = race.get_nom();
        this.est_nomme = race.est_nomme();
        this.genre = race.get_genre();
        this.attaque = race.get_attaque();
        this.vie = race.get_vie();
        this.vie_max = this.vie;
        this.armure = race.get_armure();
        this.niveau_drop_min = race.get_niveau_drop_min();
        this.niveau_drop_max = race.get_niveau_drop_max();
        this.drop_quantite = race.get_drop_quantite();
        this.competence = race.competence();
        
        this.etourdi = false;
        this.assomme = false;
        this.encaissement = 0f;
        this.part_soin = 0f;
        
        this.vie_base = this.vie_max;
        this.attaque_base = this.attaque;
        this.armure_base = this.armure;
        
        this.etat = 18 + rand.nextInt(11);
        if (est_nomme) {
            this.etat += 7;
        }
        if (Objects.equals(this.nom, "illusioniste")) {
            illu_check();
        }
    }
    
    public int getEtat() {
        return this.etat;
    }
    
    public boolean corps_utilisable() {
        return getEtat() > 0;
    }
    
    public void alterEtat(int valeur) {
        this.etat += valeur;
    }
    
    public String getNom() {
        return this.nom;
    }
    
    /**
     * Indique si le nom du monstre cause une élision
     * @return si le nom commence par a, e, i, o, u, y ou h.
     */
    boolean elision(){
        return getNom().matches("^[aeiouyhAEIOUYH].*");
    }
    
    /**
     * Présente le monstre avec la liaison "de" correctement mis en forme
     * @return la forme correcte de "de le monstre"
     */
    public String text_de(){
        if(est_male() && !elision()){
            return "du " + getNom();
        }
        return "de " + nomme(false);
    }
    
    /**
     * Présente le monstre avec la liaison "à" correctement mis en forme
     * @return la forme correcte de "à le monstre"
     */
    public String text_a(){
        if(est_male() && !elision()){
            return "au " + getNom();
        }
        return "à " + nomme(false);
    }
    
    /**
     * Donne le nom du monstre avec un suffixe
     * @param indefinie s'il faut utiliser un suffixe défini ou indéfinie
     * @return le groupe nominal du monstre
     */
    public String nomme(boolean indefinie){
        String presentation = "";
        if(genre == Genre.FEMININ){
            if(indefinie){
                presentation += "une ";
            } else if(elision()) {
                presentation += "l'";
            } else {
                presentation += "la ";
            }
        } else if(genre == Genre.MASCULIN){
            if(indefinie){
                presentation += "un ";
            } else if(elision()) {
                presentation += "l'";
            } else{
                presentation += "le ";
            }
        } //les nommés n'ont pas de déterminant, car c'est un nom propre
        presentation += getNom();
        return presentation;
    }
    
    public void rename(String nom) {
        this.nom = nom;
    }
    
    public Competence getCompetence() {
        return this.competence;
    }
    
    public int getVieMax() {
        return this.vie_max;
    }
    
    public int getVie() {
        return this.vie;
    }
    
    public int getAtk() {
        return this.attaque;
    }
    
    public int getArmure() {
        return this.armure;
    }
    
    public boolean est_male(){
        return this.genre == Genre.MASCULIN;
    }
    
    /**
     * Retire au monstre sa compétence
     */
    private void supprime_competence(){
        this.competence = Competence.AUCUNE;
    }
    
    /**
     * Gère la rotation des compétences aquajet
     */
    private void cycle_aquajet(){
        switch(getCompetence()){
            case AQUAJET3 -> this.competence = Competence.AQUAJET2;
            case AQUAJET2 -> this.competence = Competence.AQUAJET1;
            case AQUAJET1 -> this.competence = Competence.AQUAJET;
            case AQUAJET -> this.competence = Competence.AQUAJET3;
        }
    }
    
    /**
     * Gère la rotation de la compétence de Typhon
     */
    private void cycle_typhon(){
        switch(getCompetence()){
            case TYPHON1 -> this.competence = Competence.TYPHON2;
            case TYPHON2 -> this.competence = Competence.TYPHON3;
            case TYPHON3 -> this.competence = Competence.TYPHON1;
        }
    }
    
    /**
     * Gère les changements des compétences de type vol, volage et caucase
     */
    private void cycle_vol(){
        switch(getCompetence()){
            case VOL -> this.competence = Competence.VOL_OFF;
            case VOLAGE -> supprime_competence();
            case VOL_OFF -> this.competence = Competence.VOL_OFF2;
            case VOL_OFF2 -> {
                this.competence = Competence.VOL;
                System.out.println(nomme(false) + " s'envole !");
            }
            case CAUCASE -> this.competence = Competence.CAUCASE_OFF;
            case CAUCASE_OFF -> {
                this.competence = Competence.CAUCASE;
                System.out.println(nomme(false) + " s'envole !");
            }
        }
    }
    
    /**
     * Traite la perte d'une des têtes de Scylla
     */
    private void scylla_decapitation(){
        String text = "Vous avez coupé une des têtes " + text_de();
        soigne(1000);
        boostAtk(2, true);
        boostVie(1, true);
        switch (getCompetence()){
            case SCYLLA6 -> this.competence = Competence.SCYLLA5;
            case SCYLLA5 -> this.competence = Competence.SCYLLA4;
            case SCYLLA4 -> this.competence = Competence.SCYLLA3;
            case SCYLLA3 -> this.competence = Competence.SCYLLA2;
            case SCYLLA2 -> {
                this.competence = Competence.SCYLLA;
                text = "Il ne reste plus qu'une tête " + text_a() + " !";
                boostVie(12, true);
            }
        }
        System.out.println(text);
    }
    
    
    /**
     * Regarde si le monstre est nommé
     * @return true s'il est nommé, false sinon
     */
    public boolean est_nomme() {
        return this.est_nomme;
    }
    
    /**
     * Regarde si le monstre est un pantin d'entrainement
     * @return True s'il s'agit d'un pantin d'entrainement, false sinon
     */
    public boolean est_pantin() {
        return this.competence == Competence.DUMMY;
    }
    
    /**
     * Augmente/Modifie l'attaque
     * @param value       la modification à appliquer
     * @param fondamental si la modification est intrinsèque à l'unité ou juste temporaire/de surface
     */
    public void boostAtk(int value, boolean fondamental) {
        if (est_pantin()) {
            System.out.printf("Attaque diminuée de %d", value);
            if (fondamental) {
                System.out.print(" de manière définitive");
            }
            System.out.println(".");
            return;
        }
        
        this.attaque += value;
        if (fondamental) {
            this.attaque_base += value;
            if (this.attaque_base < 0) {
                this.attaque_base = 0;
            }
        }
        
        if (getAtk() < 1 && this.attaque_base > 0) {
            this.attaque = 1;
        } else if (getAtk() < 0) {
            this.attaque = 0;
        }
    }
    
    /**
     * Augmente/Modifie l'armure
     * @param value       la modification à appliquer
     * @param fondamental si la modification est intrinsèque à l'unité ou juste temporaire/de surface
     */
    public void boostArmure(int value, boolean fondamental) {
        if (est_pantin()) {
            System.out.printf("Armure diminuée de %d", value);
            if (fondamental) {
                System.out.print(" de manière définitive");
            }
            System.out.println(".");
            return;
        }
        
        this.armure += value;
        if (getArmure() < 0) {
            this.armure = 0;
        }
        if (fondamental) {
            this.armure_base += value;
            if (this.armure_base < 0) {
                this.armure_base = 0;
            }
        }
    }
    
    /**
     * Augmente/Modifie la résistance
     * @param value       la modification à appliquer
     * @param fondamental si la modification est intrinsèque à l'unité ou juste temporaire/de surface
     */
    public void boostVie(int value, boolean fondamental) {
        if (est_pantin()) {
            System.out.printf("Résistance diminuée de %d", value);
            if (fondamental) {
                System.out.print(" de manière définitive");
            }
            System.out.println(".");
            return;
        }
        
        this.vie_max += value;
        if (getVieMax() <= 0) {
            this.vie_max = 1;
        }
        this.vie += value;
        if (fondamental) {
            this.vie_base += value;
            if (this.vie_base <= 0) {
                this.vie_base = 1;
            }
        }
    }
    
    /**
     * Augmente/Modifie la valeur d'encaissement
     * @param value la modification à appliquer
     */
    public void boostEncaissement(float value) {
        if (this.encaissement == 0) {
            this.encaissement = value;
        } else {
            this.encaissement += Math.min(0.35f * value, 1f);
        }
    }
    
    /**
     * Nomme le golem
     * @param materiaux le type de matériaux du golem
     */
    public void golemNom(String materiaux) {
        this.nom = "golem" + materiaux;
    }
    
    public void boostDropMax(int value) {
        this.niveau_drop_max += value;
    }
    
    public void boostDropMin(int value) {
        this.niveau_drop_min += value;
    }
    
    public void boostDrop(int value) {
        this.drop_quantite += value;
    }
    
    /**
     * Soigne le monstre. Ne peut dépasser sa vie max ou lui retirer des pv.
     * @param value la quantité a soigné, ignorée si négative
     */
    public void soigne(int value){
        if(value <= 0){
            return;
        }
        this.vie += value;
        if(getVie() > getVieMax()){
            this.vie = getVieMax();
        }
    }
    
    /**
     * Maquille l'illusionniste en lui donnant le nom de l'illusion
     */
    private void illu_check() {
        this.nom = switch (getCompetence()) {
            case ILLU_AURAI -> {
                this.genre = Genre.FEMININ;
                yield "aurai maléfique";
            }
            case ILLU_CYCLOPE -> "cyclope";
            case ILLU_DULLA -> "dullahan";
            case ILLU_GOLEM -> "golem";
            case ILLU_ROCHE -> {
                this.genre = Genre.FEMININ;
                yield "roche maudite";
            }
            case ILLU_SIRENE -> {
                this.genre = Genre.FEMININ;
                yield "sirène";
            }
            case ILLU_TRITON -> "triton";
            case ILLU_VENTI -> "venti";
            default -> getNom();
        };
    }
    
    /**
     * Remet à zéro le multiplicateur de dégas dû au soin de l'ennemi
     */
    private void reset_part_soin(){
        this.part_soin = 0f;
    }
    
    /**
     * Ajoute de la valeur au multiplicateur de dommage d^au soin de l'ennemi
     */
    private void add_part_soin(float value){
        this.part_soin += value;
    }
    
    /**
     * Remet à zéro l'encaissement de l'ennemi
     */
    public void reset_encaisser() {
        this.encaissement = 0f;
    }
    
    /**
     * Inflige des dommages au monstre et renvoie un message informatif
     * @param quantite la force des dommages
     * @param silence  si l'on peut ou non commenter le résultat
     */
    private void subit_dommage(int quantite, boolean silence) {
        if (est_pantin()) {
            System.out.print("Vous avez infligé " + quantite + " dommages");
            Utilitaire.LoopGuard garde = new Utilitaire.LoopGuard();
            while (quantite > 25) {
                garde.check();
                System.out.print("!");
                quantite -= 10;
            }
            System.out.println();
            return;
        }
        if(quantite <= 0){
            return;
        }
        
        this.vie -= quantite;
        if (getVie() <= 0) {
            return;
        }
        
        int alteration;
        float ratio = (float) quantite / (float) getVieMax();
        if(ratio > 0.9f){
            alteration = 2 + rand.nextInt(3);
        } else if (ratio > 0.8f) {
            alteration = 2 + rand.nextInt(2);
        } else if (ratio > 0.6f) {
            alteration = 2;
        } else if (ratio > 0.2f) {
            alteration = 1;
        } else if (ratio > 0.1f) {
            alteration = rand.nextInt(2);
        } else {
            alteration = rand.nextInt(2) * rand.nextInt(2);
        }
        alterEtat(-alteration);
        
        String text;
        if (getVie() <= getVieMax() * 0.15) {
            text = nomme(false) + " a l'air sur le point de s'effondrer.";
        } else if (getVie() <= getVieMax() * 0.25) {
            text = nomme(false) + " souffre sévèrement sous l'impact.";
        } else if (getVie() <= getVieMax() * 0.35) {
            text = nomme(false) + " a prit un sacré coup .";
        } else if (getVie() <= getVieMax() * 0.45) {
            text = nomme(false) + " semble en mauvaise posture.";
        } else if (getVie() <= getVieMax() * 0.8){
            text = nomme(false) + " est toujours d'aplomb.";
        } else if (getVie() <= getVieMax() * 0.9) {
            text = nomme(false) + " ne semble pas souffrir de l'impact.";
        } else {
            text = nomme(false) + " ne réagit même pas à l'assaut.";
        }
        if (!silence) {
            System.out.println(text);
        }
        applique_competence_post_dommage();
    }
    
    /**
     * Applique les compétence qui s'active dès que le monstre subit des dommages
     */
    private void applique_competence_post_dommage(){
        switch (getCompetence()) {
            case CERBERE -> boostAtk(1, false);
            case LYCAON -> {
                if (getVie() < 10) {
                    System.out.println(nomme(false) + " se transforme en loup !");
                    this.competence = Competence.LYCAON2;
                    boostAtk(2, true);
                    boostVie(2, true);
                }
            }
            case LYCAON2 -> {
                if(getVie() < 7){
                    System.out.println(nomme(false) + " se transforme en un hybride mi-homme mi-loup !");
                    this.competence = Competence.LYCAON3;
                    boostAtk(2, true);
                    boostVie(2, true);
                    boostArmure(1, true);
                }
            }
            case EMPOUSA -> {
                Utilitaire.LoopGuard garde = new Utilitaire.LoopGuard();
                String text = "";
                if(getVie() <= 0){
                    text = nomme(false) + " se transforme !\n";
                }
                while(est_mort() && getVieMax() > 0){
                    garde.check();
                    if(getArmure() * 6 > getVieMax() && getArmure() * 6 > (getAtk() - 1) * 2){
                        empousa_def();
                    } else if ((getAtk() - 1) * 2 > getVieMax()){
                        empousa_atk();
                    } else {
                        empousa_vie();
                    }
                }
                if(!est_mort()) {
                    System.out.print(text);
                }
            }
        }
    }
    
    private void empousa_def(){
        boostArmure(-1, true);
        soigne(6);
    }
    
    private void empousa_atk(){
        boostAtk(-1, true);
        soigne(2);
    }
    
    private void empousa_vie(){
        boostVie(-1, true);
        soigne(1);
    }
    
    /**
     * Écris la quantité de dommage infligé par le monstre à l'adversaire
     * @param joueur l'entité attaquée
     * @implNote Calcul les effet de "encaisser", "étourdit" et "assommé"
     */
    public void attaque(Joueur joueur) throws IOException {
        if (this.encaissement > 0.95f) {
            this.encaissement = 0.95f;
        }
        float modificateur = 1 - this.encaissement + this.part_soin;
        if (this.assomme) {
            undo_assomme();
        } else if (this.etourdi) {
            if (applique_competence_pre(joueur, modificateur) && getAtk() > 0) {
                System.out.println(nomme(false) + " est étourdit et inflige " + Main.corriger(getAtk() * 0.5F * modificateur) + " dommages à " + joueur.getFrontNom() + ".");
                applique_competence_post(joueur);
            }
            undo_etourdi();
        } else {
            if (applique_competence_pre(joueur, modificateur) && getAtk() > 0) {
                System.out.println(nomme(false) + " inflige " + Main.corriger(getAtk() * modificateur) + " dommages " + "à" + " " + joueur.getFrontNom() + ".");
                applique_competence_post(joueur);
            }
        }
        fin_combat(joueur);
        System.out.println();
    }
    
    /**
     * Applique la compétence du monstre avant son attaque
     * @return si l'attaque a bien lieu après application de la compétence
     * @throws IOException comme ça, ça marche
     */
    private boolean applique_competence_pre(Joueur joueur, float modificateur) throws IOException {
        switch (getCompetence()) {
            case EXPLOSION -> {
                boostAtk(6, false);
                System.out.println(nomme(false) + " s'apprête à causer une explosion !");
            }
            case GEL -> {
                if (Input.yn("Portez vous (pl) une armure ?")) {
                    System.out.println(nomme(false) + " détruit votre armure");
                    supprime_competence();
                } else {
                    boostAtk(3, false);
                }
            }
            case REGARD_MORTEL -> {
                System.out.println(nomme(false) + " regarde " + joueur.getFrontNom() + " droit dans les yeux.");
                if (Input.D6() + joueur.bonus_analyse() <= 4) {
                    System.out.printf("%s sent son âme se faire assaillir et perd définitivement %d points de " + "r" +
                            "ésistance.\n", joueur.getFrontNom(), getAtk());
                    supprime_competence();
                    return false;
                }
                supprime_competence();
            }
            case REGARD_PETRIFIANT -> {
                System.out.printf("%s regarde %s droit dans les yeux.\n",nomme(false) , joueur.getFrontNom());
                if (Input.D6() + joueur.bonus_analyse() <= 4) {
                    System.out.printf("%s se change partiellement en pierre !\n", joueur.getFrontNom());
                    System.out.printf("%s perd définitivement %d points de résistance et gagne définitivement %d " +
                            "points de défense.\n", joueur.getFrontNom(), getAtk() + 2, 1 + rand.nextInt(2));
                    supprime_competence();
                    return false;
                }
                supprime_competence();
            }
            case CHARGE -> {
                System.out.println(nomme(false) + " charge !");
                boostAtk(3, false);
            }
            case VIOLENT -> {
                if (rand.nextBoolean()) {
                    System.out.println(nomme(false) + " attaque violemment " + joueur.getFrontNom() + " et lui inflige " + Main.corriger(getAtk() * 1.5F * modificateur) + " dommages.");
                    return false;
                }
            }
            case ASSASSINAT -> {
                System.out.println(nomme(false) + " se glisse discrètement derrière " + joueur.getFrontNom() + " sans " + "que" + " personne ne l'aperçoive.");
                boostAtk(getAtk(), false);
                reset_encaisser();
            }
            case KAMICASE -> {
                System.out.println(nomme(false) + " explose !!!");
                boostAtk(getAtk() * 2, false);
            }
            case AQUAJET -> boostAtk(18, false);
            case VOLEUR_CASQUE -> {
                if (Input.yn(joueur.getFrontNom() + " porte-iel un casque ?") && Input.D6() <= 4) {
                    System.out.println(nomme(false) + " vole votre casque et part avec.");
                    Combat.stop_run();
                    return false;
                }
            }
            case SCYLLA6 -> {
                scyllattaque(6, modificateur, joueur.getFrontNom());
                return false;
            }
            case SCYLLA5 -> {
                scyllattaque(5, modificateur, joueur.getFrontNom());
                return false;
            }
            case SCYLLA4 -> {
                scyllattaque(4, modificateur, joueur.getFrontNom());
                return false;
            }
            case SCYLLA3 -> {
                scyllattaque(3, modificateur, joueur.getFrontNom());
                return false;
            }
            case SCYLLA2 -> {
                scyllattaque(2, modificateur, joueur.getFrontNom());
                return false;
            }
            case TYPHON1, TYPHON2 -> System.out.println(nomme(false) + " crée des vents violent qui infligent " + Main.corriger(getAtk() * 0.4f) + " dommages à tous les participants.");
            case TYPHON3 -> System.out.println(nomme(false) + " provoque une éruption volcanique qui inflige " + Main.corriger(getAtk() * 1.2f) + " dommages à tous les participants.");
        }
        return true;
    }
    
    /**
     * Fait attaquer chacune des têtes de scylla
     * @param nb_tete le nombre de têtes de Scylla qui attaque
     * @param modificateur le modificateur de dégas
     */
    private void scyllattaque(int nb_tete, float modificateur, String cible) {
        System.out.printf("Les %d têtes de Scylla vous attaquent !\n", nb_tete);
        for (int i = 0; i < nb_tete; i++) {
            System.out.println(nomme(false) + " inflige " + Main.corriger(getAtk() * modificateur) + " dommages à " + cible + ".");
        }
    }
    
    /**
     * Applique la compétence du monstre après son attaque
     */
    private void applique_competence_post(Joueur joueur) {
        switch (getCompetence()) {
            case EXPLOSION -> {
                boostAtk(-6, false);
                supprime_competence();
            }
            case VAMPIRISME -> {
                if (Input.yn("L'attaque a-t-elle touchée ?")) {
                    soigne(1);
                }
            }
            case VAMPIRISME4 -> {
                if (Input.yn("L'attaque a-t-elle touchée ?")) {
                    soigne(4);
                }
            }
            case POISON_CECITE -> joueur.prend_cecite();
            case GEL -> supprime_competence();
            case MORSURE_MALADIVE -> {
                System.out.println("La morsure provoque une grave infection qui fait définitivement perdre 1 point " + "de" + " résistance à " + joueur.getFrontNom() + ".");
                supprime_competence();
            }
            case MORSURE_SAUVAGE -> {
                System.out.println("La morsure infecte " + joueur.getFrontNom() + " avec un parasite qui lui draine " + "1" + " PP.");
                supprime_competence();
            }
            case MORSURE_EREINTANTE -> {
                System.out.println("La morsure provoque chez " + joueur.getFrontNom() + " une grave réaction et lui " + "fait perdre 1 point d'attaque définitivement.");
                supprime_competence();
            }
            case POISON -> joueur.prend_poison1();
            case POISON2 -> joueur.prend_poison2();
            case CHARGE -> {
                boostAtk(-3, false);
                supprime_competence();
            }
            case ASSASSINAT -> {
                boostAtk(getAtk() / 2, false);
                supprime_competence();
            }
            case AQUAJET3, AQUAJET2 -> cycle_aquajet();
            case AQUAJET1 -> {
                cycle_aquajet();
                System.out.println(nomme(false) + " prépare quelque chose...");
            }
            case AQUAJET -> {
                cycle_aquajet();
                boostAtk(-18, false);
            }
            case FRAPPE_SPECTRALE ->
                    System.out.println("L'attaque traverse partiellement l'armure de " + joueur.getFrontNom() + " et "
                            + "ignore " + rand.nextInt(4) + " point(s) de défense.");
            case KAMICASE -> this.vie = 0;
            
            //nommé
            case CERBERE -> {
                boostAtk(1, false);
                int assomme = rand.nextInt(5) - 2;
                if(assomme > 0){
                    System.out.println(nomme(false) + " vous frappe si fort que vous perdez connaissance.");
                    if(joueur.a_familier_front()){
                        joueur.f_assomme(4 - assomme);
                    } else {
                        joueur.assomme(4 - assomme);
                    }
                }
            }
            case MORMO -> {
                System.out.println(nomme(false) + " vous terrifie !");
                System.out.println(joueur.getFrontNom() + " perd temporairement 1 point d'attaque.");
            }
            case EMPOUSA -> {
                if (Input.yn("L'attaque a-t-elle touchée ?")) {
                    soigne(3);
                }
            }
            case LADON -> {
                joueur.prend_poison1();
                joueur.prend_poison2();
                joueur.prend_cecite();
            }
            case ECHIDNA -> {
                joueur.prend_poison2();
                int assomme = rand.nextInt(4) - 2;
                if(assomme > 0){
                    System.out.println(nomme(false) + " vous frappe si fort que vous perdez connaissance.");
                    if(joueur.a_familier_front()){
                        joueur.f_assomme(3 - assomme);
                    } else {
                        joueur.assomme(3 - assomme);
                    }
                }
            }
            case CHARYBDE, SCYLLA -> System.out.println(nomme(false) + " provoque une vague infligeant " + Main.corriger(getAtk() * 0.5f) + " dommages supplémentaire à tous les participants.");
            case TYPHON1, TYPHON2, TYPHON3 -> cycle_typhon();
            case CHRONOS -> {
                if (Input.yn("L'attaque a-t-elle touchée ?")) {
                    System.out.println(joueur.getFrontNom() + " perd définitivement 1 équipement de son choix.");
                }
            }
        }
    }
    
    /**
     * Gère les compétences intervenant à la fin du tour (indépendant du fait que l'attaque ait eu lieu)
     */
    private void fin_combat(Joueur joueur) throws IOException {
        reset_encaisser();
        reset_part_soin();
        reset_encaisser();
        switch (getCompetence()) {
            case POURRI -> {
                System.out.println(nomme(false) + " tombe en morceau.");
                subit_dommage(1, true);
            }
            case PHOTOSYNTHESE -> soigne(1);
            case BLESSE -> {
                System.out.println(nomme(false) + " saigne abondamment.");
                subit_dommage(3, true);
            }
            case DUO -> {
                supprime_competence(); // pour éviter une boucle
                attaque(joueur);
                this.competence = Competence.DUO;
            }
            case VOL_OFF, VOL_OFF2 -> cycle_vol();
            case LYCAON -> soigne(2);
            case LYCAON2 -> soigne(3);
            case LYCAON3 -> soigne(4);
            case CAUCASE, CAUCASE_OFF -> soigne(5);
        }
    }
    
    /**
     * Renvoie la quantité et qualité des équipements obtenus à la mort du monstre
     */
    private void drop() {
        System.out.println("Vous fouillez le corp " + text_de());
        if (this.drop_quantite <= 0 || getCompetence() == Competence.ARNAQUE) {
            System.out.println("Vous ne trouvez aucun équipement sur son cadavre");
            return;
        }
        switch (getCompetence()) {
            case SACRE -> {
                System.out.println("Vous avez tué " + nomme(true) + ", aimé(e) des dieux, honte sur vous !");
                System.out.println("Vous (tous ceux présent) perdez définitivement 1 point de résistance.");
                return;
            }
            case PERLE -> System.out.println("Vous avez trouvé une perle (5PO) dans le corps " + text_de() + " !");
            case DETESTE -> {
                System.out.println("Vous avez tué " + nomme(true) + ", haïe des dieux, gloire à vous !");
                System.out.println("Vous (tous ceux présent) gagnez définitivement 1 point de résistance.");
            }
            case FOLIE_MEURTRIERE -> {
                System.out.println("\nLe corp " + text_de() + " s'agite !");
                System.out.println(nomme(false) + " vous (pl) inflige" + getAtk() + " dommages.\n");
            }
            case DUO_PASSED -> {
                supprime_competence();
                System.out.println("Vous vous dirigez vers le deuxième " + getNom());
                drop();
            }
        }
        for (int i = 0; i < this.drop_quantite; i++) {
            int temp = this.niveau_drop_min + rand.nextInt(this.niveau_drop_max - this.niveau_drop_min + 1);
            switch (temp) {
                case 0 -> Equipement.drop_0();
                case 1 -> Equipement.drop_1();
                case 2 -> Equipement.drop_2();
                case 3 -> Equipement.drop_3();
                case 4 -> Equipement.drop_4();
                case 5 -> Equipement.drop_promo();
                default -> throw new InvalidParameterException("niveau de promotion invalide : %d.".formatted(temp));
            }
        }
        System.out.println();
    }
    
    /**
     * Inflige des dommages à distance au monstre
     * @param quantite la puissance d'attaque
     * @implNote Considère l'armure et la compétence du monstre
     * gère le cas de mort du monstre
     */
    public void tir(int quantite) {
        if (quantite <= 0) {
            return;
        }
        int degas = applique_competence_tir(max(quantite - getArmure(), 1));
        if (degas > 0) {
            Output.JouerSonTir();
        }
        subit_dommage(degas, false);
    }
    
    /**
     * Inflige des dommages à distance au monstre
     * @param quantite la puissance d'attaque
     * @param mult     un multiplicateur à appliquer à la quantité
     * @implNote Considère l'armure et la compétence du monstre
     * gère le cas de mort du monstre
     */
    public void tir(int quantite, float mult) {
        if (quantite <= 0) {
            return;
        }
        tir(Main.corriger(quantite * mult));
    }
    
    /**
     * Applique la compétence avant de subir des dommages à distance
     * @param degas les dommages infligés par l'attaque
     * @return les dégas subits par le monstre
     */
    private int applique_competence_tir(int degas) {
        switch (getCompetence()) {
            case FRAGILE -> degas += 1;
            case ESPRIT -> {
                System.out.println("Votre projectile passe au travers " + text_de() + " sans l'affecter.");
                supprime_competence();
                degas = 0;
            }
            case RAPIDE, CAUCASE, CAUCASE_OFF -> {
                System.out.println(nomme(false) + " esquive partiellement votre projectile.");
                degas = Main.corriger(degas * 0.5f);
            }
            case FLAMME_DEFENSE -> {
                System.out.println("Votre projectile brûle en s'approchant " + text_de() + ".");
                supprime_competence();
                degas = 0;
            }
            case ESQUIVE -> {
                if (rand.nextInt(10) == 0) {
                    System.out.println(nomme(false) + " esquive votre projectile.");
                    degas = 0;
                }
            }
            case FURTIF -> {
                System.out.println("Vous ne parvenez pas à repérer où se trouve " + nomme(false) + ".");
                supprime_competence();
                degas = 0;
            }
            case PEAU_DURE, GOLEM_PIERRE, CHRONOS -> {
                System.out.println("La peau dure " + text_de() + " amortie une partie de l'impact");
                degas = Main.corriger(degas * 0.85f);
            }
            case CHANT_SIRENE -> {
                System.out.println("Le chant " + text_de() + " perturbe le tir.");
                if (Input.D8() <= 3) {
                    degas = degas - 4;
                }
            }
            case PEAU_DACIER, GOLEM_ACIER, GOLEM_FER -> {
                System.out.println("La peau extrêmement dure " + text_de() + " absorbe l'essentiel de l'impact.");
                degas = Main.corriger(degas * 0.05f);
            }
            case INTANGIBLE -> {
                System.out.println("Votre projectile passe au travers " + text_de() + " sans l'affecter.");
                degas = 0;
            }
            case GOLEM_MITHRIL -> {
                System.out.println("La peau particulièrement solide " + text_de() + " réduit l'impact.");
                degas = Main.corriger(degas * 0.75F);
            }
            case BRUME, EMPOUSA -> degas = max(degas - 2, 0);
            case PYTHON -> {
                System.out.println(nomme(false) + " avait anticipé votre projectile et l'esquive partiellement.");
                if(rand.nextBoolean()){
                    degas = Main.corriger(degas * 0.33F);
                } else {
                    degas = Main.corriger(degas * 0.66F);
                }
            }
        }
        return degas;
    }
    
    /**
     * Inflige des dommages au monstre sans aucun calcul
     * @param quantite la quantité de dommage à infliger
     */
    public void dommage_direct(int quantite) {
        dommage_direct(quantite, true);
    }
    
    /**
     * Inflige des dommages au monstre sans aucun calcul
     * @param quantite  la quantité de dommage à infliger
     * @param is_silent si on renvoie un message suite aux dommages subits
     */
    public void dommage_direct(int quantite, boolean is_silent) {
        if (quantite <= 0) {
            return;
        }
        subit_dommage(quantite, is_silent);
    }
    
    /**
     * Inflige des dommages magiques au monstre
     * @param quantite la puissance d'attaque
     * @implNote Considère l'armure et la compétence du monstre
     * gère le cas de mort du monstre
     */
    public void dommage_magique(int quantite) {
        if (quantite <= 0) {
            return;
        }
        int degas = applique_competence_magie(quantite);
        subit_dommage(degas, false);
    }
    
    /**
     * Applique la compétence avant de subir des dommages magiques
     * @param degas les dommages infligés par l'attaque
     * @return les dégas subits par le monstre
     */
    private int applique_competence_magie(int degas) {
        switch (getCompetence()) {
            case FRAGILE -> degas += 1;
            case ESPRIT -> {
                System.out.println("Votre magie passe au travers " + text_de() + " sans l'affecter.");
                supprime_competence();
                degas = 0;
            }
            case SPELL_IMMUNE, CHRONOS -> {
                System.out.println("Votre magie n'a aucun effet sur " + nomme(false) + ".");
                degas = 0;
            }
            case PARTIELLE_SPELL_IMMUNIE -> {
                System.out.println("Votre magie semble sans effet sur " + nomme(false) + ".");
                supprime_competence();
                degas = 0;
            }
            case FURTIF -> {
                System.out.println("Vous ne parvenez plus à identifier où se trouve " + nomme(false) + " et renoncez à " +
                        "utiliser votre magie.");
                supprime_competence();
                degas = 0;
            }
            case PEAU_MAGIQUE -> {
                System.out.println("La peau " + text_de() + " diminue l'impact du sort.");
                degas = Main.corriger(degas * 0.5f);
            }
            case CUIR_MAGIQUE -> {
                System.out.println("Le cuir " + text_de() + " absorbe l'essentiel de l'impact du sort.");
                degas = Main.corriger(degas * 0.1f);
            }
            case CHANT_SIRENE -> {
                System.out.println("Le chant " + text_de() + " perturbe le lancement du sort.");
                if (Input.D8() <= 3) {
                    degas = degas > 4 ? degas - 4 : 0;
                }
            }
            case PEAU_DACIER -> {
                System.out.println("La peau extrêmement dure " + text_de() + " absorbe une partie de l'impact du sort.");
                degas = Main.corriger(degas * 0.5f);
            }
            case GOLEM_PIERRE, GOLEM_FER, GOLEM_ACIER -> {
                System.out.println("La constitution particulière " + text_de() + " diminue légèrement l'impact du sort.");
                degas = Main.corriger(degas * 0.9F);
            }
            case GOLEM_MITHRIL -> {
                System.out.println("La matériaux particuliers composant " + nomme(false) + " réduisent immensément l'impact "
                        + "du sort.");
                degas = Main.corriger(degas * 0.05f);
            }
            case CAUCASE, CAUCASE_OFF -> {
                System.out.println(nomme(false) + " esquive très partiellement votre sort.");
                degas = Main.corriger(degas * 0.8f);
                
            }
            case PYTHON -> {
                System.out.println(nomme(false) + " avait anticipé votre sort et l'esquive partiellement.");
                if(rand.nextBoolean()){
                    degas = Main.corriger(degas * 0.33F);
                } else {
                    degas = Main.corriger(degas * 0.66F);
                }
            }
        }
        return degas;
    }
    
    /**
     * Regarde si le monstre est mort et agit en conséquence
     * @return si le monstre est vivant
     */
    public boolean check_mort(Position pos) throws IOException {
        if (est_mort()) {
            Output.jouerSonMonstreMort();
            switch (getCompetence()) {
                case ILLU_AURAI, ILLU_CYCLOPE, ILLU_DULLA, ILLU_GOLEM, ILLU_ROCHE, ILLU_SIRENE, ILLU_TRITON,
                     ILLU_VENTI -> {
                    System.out.println(nomme(false) + " se dissipe ! Tout celà n'était qu'une illusion !");
                    this.nom = "illusioniste";
                    supprime_competence();
                    this.genre = Genre.MASCULIN;
                }
                case DUO -> {
                    System.out.println("Un des " + getNom() + " est mort(e).");
                    this.competence = Competence.DUO_PASSED;
                    soigne(1000);
                    return true;
                }
                default -> System.out.println(nomme(false) + " est mort(e).");
            }
            drop();
            alterEtat(getVie()); //on retire les dégas en trop
            Joueur.monstre_mort(this);
            
            if (getEtat() <= 0 || pos == Position.ENFERS || pos == Position.OLYMPE || pos == Position.ASCENDANT) {
                return false;
            }
            int value = (1 + (getEtat() - 1) / 10);
            System.out.println("Vous pouvez vendre le cadavre " + text_de() + " pour " + value + " PO.");
            Output.jouerSonOr(value);
            return false;
        }
        return true;
    }
    
    
    /**
     * Inflige des dommages via attaque classique au monstre
     * @param quantite la puissance d'attaque
     */
    public void dommage(int quantite) {
        if (quantite <= 0) {
            return;
        }
        int degas = applique_competence_dommage(max(quantite - getArmure(), 1));
        if (degas <= 0) {
            return;
        }
        Output.JouerSonDommage();
        subit_dommage(degas, false);
        if (!est_mort()) {
            applique_competence_post_dommage_classique();
        }
    }
    
    /**
     * Inflige des dommages au monstre
     * @param quantite la puissance d'attaque
     * @param mult     par combien on multiplie les dommages d'entrée
     *                 gère le cas de mort du monstre
     */
    public void dommage(int quantite, float mult) {
        if(quantite <= 0 || mult <= 0) {
            return;
        }
        dommage(Main.corriger(quantite * mult));
    }
    
    /**
     * Applique la compétence avant de subir des dommages classique
     * @param degas les dommages infligés par l'attaque
     * @return les degas subits par le monstre
     */
    private int applique_competence_dommage(int degas) {
        switch (getCompetence()) {
            case FRAGILE -> degas += 1;
            case VOL, VOLAGE, CAUCASE -> {
                Texte.esquive_vol(nomme(false));
                cycle_vol();
                degas = 0;
            }
            case ESPRIT -> {
                System.out.println("Votre attaque traverse " + nomme(false) + " sans l'affecter.");
                supprime_competence();
                degas = 0;
            }
            case ESQUIVE -> {
                if (rand.nextInt(15) == 0) {
                    System.out.println(nomme(false) + " esquive votre attaque.");
                    degas = 0;
                }
            }
            case FURTIF -> {
                System.out.println("Vous ne parvenez plus à identifier où se trouve " + nomme(false) + ".");
                supprime_competence();
                degas = 0;
            }
            case PEAU_DURE, GOLEM_PIERRE, GOLEM_FER, CHRONOS -> {
                System.out.println("La peau dure " + text_de() + " amortie une partie de l'assaut");
                degas = Main.corriger((float) degas * 0.9F);
            }
            case CHANT_SIRENE -> {
                System.out.println("Le chant " + text_de() + " perturbe l'attaque'.");
                if (Input.D8() <= 1) {
                    degas = degas > 4 ? degas - 4 : 0;
                }
            }
            case PEAU_DACIER, GOLEM_ACIER -> {
                System.out.println("La peau extrêmement dure " + text_de() + " absorbe l'essentiel de l'assaut.");
                degas = Main.corriger(degas * 0.1f);
            }
            case GOLEM_MITHRIL -> {
                System.out.println("La peau particulièrement solide " + text_de() + " absorbe une grande partie de "
                        + "l'assaut.");
                degas = Main.corriger(degas * 0.5f);
            }
            case BRUME, EMPOUSA -> degas -= 1;
            case PYTHON -> {
                System.out.println(nomme(false) + " avait prophétisé votre assaut et l'esquive partiellement.");
                if(rand.nextBoolean()){
                    degas = Main.corriger(degas * 0.40F);
                } else {
                    degas = Main.corriger(degas * 0.60F);
                }
            }
        }
        return degas;
    }
    
    /**
     * Applique la compétence après avoir subi des dommages classiques
     */
    private void applique_competence_post_dommage_classique() {
        switch (getCompetence()) {
            case ARMURE_GLACE -> System.out.println("L'armure de glace " + text_de() + " vous inflige 1 dommage.");
            case ARMURE_GLACE2 -> System.out.println("L'armure de glace " + text_de() + " vous inflige 3 dommages.");
            case ARMURE_FEU -> System.out.println("Les flammes " + text_de() + " vous inflige 1 dommage.");
            case ARMURE_FOUDRE ->
                    System.out.println("La foudre entourant " + nomme(false) + " vous inflige 3 dommages.");
            case ILLU_AURAI, ILLU_CYCLOPE, ILLU_DULLA, ILLU_GOLEM, ILLU_ROCHE, ILLU_SIRENE, ILLU_TRITON, ILLU_VENTI -> {
                if (getVie() <= 4) {
                    this.nom = "illusioniste";
                    this.genre = Genre.MASCULIN;
                    System.out.println(nomme(false) + " se révèle ! Tout celà n'était qu'une illusion !");
                    supprime_competence();
                }
            }
            case MORMO -> {
                System.out.println("Frapper " + nomme(false) + " provoque en vous une grande terreur !");
                System.out.println("Vous perdez temporairement 1 point d'attaque.");
            }
            case CAUCASE_OFF -> cycle_vol();
        }
    }
    
    /**
     * Règle l'état du monstre sur "assommé" ou "étourdit"
     */
    public void affecte() {
        Random rand = new Random();
        if (rand.nextBoolean()) {
            do_assomme();
        } else {
            do_etourdi();
        }
    }
    
    /**
     * Règle l'état du monstre à "assommé"
     */
    public void do_assomme() {
        if (est_mort()) {
            return;
        }
        if(est_nomme() && !this.etourdi){
            System.out.println(nomme(false) + " lutte pour ne pas perdre connaissance.");
            do_etourdi();
            return;
        }
        switch (getCompetence()) {
            case GOLEM_ACIER, GOLEM_MITHRIL -> {
                System.out.println(nomme(false) + " n'a pas de conscience, et ne peut pas être assommé.");
                return;
            }
            case GOLEM_PIERRE, GOLEM_FER -> {
                System.out.println(nomme(false) + " n'a pas de conscience, et ne peut pas être assommé.");
                System.out.println(nomme(false) + " cependant, est déséquilibré.");
                do_etourdi();
                return;
            }
        }
        this.assomme = true;
        System.out.println(nomme(false) + " est assommé(e).");
    }
    
    /**
     * Règle l'état du monstre à "étourdi"
     */
    public void do_etourdi() {
        if (est_mort()) {
            return;
        }
        switch (getCompetence()) {
            case GOLEM_ACIER, GOLEM_MITHRIL -> {
                System.out.println(nomme(false) + " est trop solide pour être étourdi.");
                return;
            }
            case GOLEM_PIERRE, GOLEM_FER -> {
                if (rand.nextBoolean()) {
                    System.out.println(nomme(false) + " laisse tomber des fragments de son corps pour ne pas être désavantagé.");
                    subit_dommage(rand.nextInt(5) + 1, true);
                    if (est_mort()) {
                        return;
                    }
                }
            }
            case CHRONOS, CHARYBDE -> {
                if(rand.nextBoolean()) {
                    System.out.println(nomme(false) + " n'a pas l'air prêt de perdre connaissance.");
                    return;
                }
            }
        }
        if (!assomme) {
            this.etourdi = true;
            System.out.println(nomme(false) + " est étourdi(e).");
        }
    }
    
    /**
     * Retire l'état "assommé" du monstre
     * Peut infliger l'état "étourdi"
     */
    void undo_assomme() {
        if (est_mort()) {
            return;
        }
        this.assomme = false;
        System.out.print(nomme(false) + " se réveille ");
        Random rand = new Random();
        if (rand.nextBoolean()) {
            this.etourdi = true;
            System.out.println("encore étourdi(e).");
        } else {
            this.etourdi = false;
            System.out.println("en pleine possession de ses moyens.");
        }
    }
    
    /**
     * Retire l'état "étourdi" au monstre
     */
    void undo_etourdi() {
        if (est_mort()) {
            return;
        }
        this.etourdi = false;
        System.out.println(nomme(false) + " n'est plus étourdi(e).");
    }
    
    /**
     * Vérifie si le monstre est mort et gère les potentiels effets
     * @return si le monstre est mort
     */
    public boolean est_mort() {
        if(getVie() > 0){
            return false;
        }
        return switch (getCompetence()) {
            case DUMMY -> false;
            case REVENANT -> {
                System.out.println("Une sombre brûme s'abat sur vous, vous perdez (tous) 1 point d'attaque pour la durée " + "du combat.");
                System.out.println(nomme(false) + " se relève !");
                this.vie = rand.nextInt(getVieMax() - 5) + 5;
                supprime_competence();
                yield false;
            }
            case SCYLLA2, SCYLLA3, SCYLLA4, SCYLLA5, SCYLLA6 -> {
                scylla_decapitation();
                yield false;
            }
            default -> true;
        };
    }
    
    /**
     * Renvoie si le monstre est vaincu, i.e. mort ou domestiqué
     * @return true s'il est vaincu, false sinon
     */
    public boolean est_vaincu() {
        return est_mort() || getVieMax() < 0;
    }
    
    /**
     * Applique la compétence "encaisser" et ses résultats
     * Demande aux joueurs les informations nécessaires
     * @throws IOException jsp mais sans ça, ça ne marche pas
     * @implNote On considère que le joueur qui utilise encaisser est en première ligne
     */
    public void encaisser(Joueur j) throws IOException {
        int attaque = j.puissance_attaque();
        if (attaque <= 0) {
            return;
        }
        float modif;
        switch (Input.D6()) {
            case 1:
                boostEncaissement(0.4F);
                System.out.println("Vous vous préparer à encaisser en oubliant d'attaquer !");
                modif = 0;
                break;
            case 2, 3, 4:
                modif = 0.1f;
                boostEncaissement(0.4F);
                System.out.println("Vous vous préparez à encaisser.");
                break;
            case 5:
                modif = 0.5f;
                boostEncaissement(0.55F);
                System.out.println("Vous vous préparez à encaisser.");
                break;
            case 6, 7:
                modif = 0.5f;
                boostEncaissement(0.7F);
                System.out.println("Vous vous préparez fermement à encaisser, solide comme un roc.");
                break;
            default:
                System.out.println("Le résultat n'a pas été comprit, attaque classique appliquée.");
                modif = 1;
        }
        if (modif > 0) {
            attaque = Main.corriger(attaque * modif);
            dommage(attaque);
        }
    }
    
    public void f_encaisser() throws IOException {
        float modif;
        switch (Input.D4()) {
            case 1:
                modif = 0f;
                boostEncaissement(0.3F);
                System.out.println("Votre familier se prépare à encaisser en oubliant d'attaquer !");
                break;
            case 2:
                modif = 0.1f;
                boostEncaissement(0.3F);
                System.out.println("Votre familier se prépare à encaisser.");
                break;
            case 3:
                modif = 0.2f;
                boostEncaissement(0.5F);
                System.out.println("Votre familier se prépare à encaisser.");
                break;
            case 4, 5:
                modif = 0.4f;
                boostEncaissement(0.7F);
                System.out.println("Votre familier se prépare solidement à encaisser.");
                break;
            default:
                System.out.println("Le résultat n'a pas été comprit, attaque classique appliquée.");
                modif = 1f;
        }
        if (modif > 0) {
            int attaque = Input.atk();
            if (attaque <= 0) {
                return;
            }
            attaque = Main.corriger(attaque * modif);
            dommage(attaque);
        }
    }
    
    /**
     * Renvoie la quantité de soin appliqué par la compétence "guérison"
     * @param premiere_ligne si le lanceur ou la cible est en première ligne
     * @throws IOException jsp mais sans ça, ça ne marche pas
     */
    public void soigner(Boolean premiere_ligne) throws IOException {
        int soin = Input.D6();
        switch (soin) {
            case 6 -> {
                System.out.println("Vous soignez la cible de 9.");
                if (premiere_ligne) {
                    System.out.println("Vous vous exposez légèrement.");
                    add_part_soin(0.1f);
                }
            }
            case 5 -> {
                System.out.println("Vous soignez la cible de 7.");
                if (premiere_ligne) {
                    System.out.println("Vous vous exposez légèrement.");
                    add_part_soin(0.1f);
                }
            }
            case 4, 3, 2 -> {
                System.out.println("Vous soignez la cible de " + (2 + soin) + ".");
                if (premiere_ligne) {
                    System.out.println("Vous vous exposez.");
                    add_part_soin(0.5f);
                }
            }
            case 1 -> {
                System.out.println("Vous soignez la cible de 2.");
                if (premiere_ligne) {
                    System.out.println("Vous vous exposez lourdement.");
                    add_part_soin(1f);
                }
            }
            default -> {
                System.out.println("Le résultat n'a pas été comprit, attaque classique appliquée.");
                dommage(Input.atk());
            }
        }
    }
    
    /**
     * Renvoie le résultat de la compétence "domestiquer"
     * @param bonus un bonus du joueur
     * @return si le monstre est domestiqué
     * @throws IOException jsp mais sans ça, ça ne marche pas
     */
    public Boolean domestiquer(int bonus) throws IOException {
        if(est_nomme()) {
            System.out.println(nomme(false) + " est une force de la nature, une puissance indomptable.");
            return false;
        }
        int ratio = (getVie() * 100 / getVieMax());
        switch (getCompetence()) {
            case COLERE, VIOLENT -> {
                if (Input.D8() + bonus <= getVie()) {
                    System.out.println(nomme(false) + " réagit très agressivement.");
                    return false;
                }
            }
            case SAUVAGE -> {
                System.out.println(nomme(false) + " est trop sauvage pour être domestiqué.");
                return false;
            }
            case GOLEM_PIERRE, GOLEM_FER -> {
                if (Input.D8() <= getVie() * 2) {
                    System.out.println(nomme(false) + " n'est pas très réceptif à votre tentative.");
                    return false;
                }
            }
            case GOLEM_ACIER -> {
                if (Input.D6() * 2 <= getVie()) {
                    System.out.println(nomme(false) + " n'est pas très réceptif à votre tentative.");
                    return false;
                }
            }
            case GOLEM_MITHRIL -> {
                if (Input.D8() * 2 <= getVie()) {
                    System.out.println(nomme(false) + " ne remarque même pas votre présence.");
                    return false;
                } else {
                    System.out.println(nomme(false) + " vous remarque.");
                    if (Input.D20() <= getVie()) {
                        System.out.println(nomme(false) + " ne vous accorde aucune importance.");
                        return false;
                    } else {
                        System.out.println(nomme(false) + " vous accorde son attention.");
                    }
                }
            }
            case ILLU_AURAI, ILLU_CYCLOPE, ILLU_DULLA, ILLU_GOLEM, ILLU_ROCHE, ILLU_SIRENE, ILLU_TRITON, ILLU_VENTI -> {
                System.out.println(nomme(false) + " réagit très étrangement à votre tentative.");
                return false;
            }
            case DUMMY -> {
                System.out.println("Simulation d'une intéraction avec un monstre aléatoire.");
                int pvm, pv;
                pvm = rand.nextInt(41) + 10; //10~50
                pv = rand.nextInt(pvm - 9) + 5; //5~pvm-5
                System.out.printf("Simulation : Monstre commun ayant %d/%d points de vie\n", pv, pvm);
                ratio = (pv * 100 / pvm);
            }
        }
        if (ratio >= 85) {
            System.out.println(nomme(false) + " réagit agressivement.");
            return false;
        }
        Random rand = new Random();
        if (ratio >= 75 && rand.nextInt(100) > ratio) {
            System.out.println(nomme(false) + " semble intrigué(e) par votre comportement.");
            ratio -= 100 - ratio;
        }
        if (ratio >= 50 && rand.nextInt(100) > ratio) {
            int temp = Input.D4();
            if (temp > 2) {
                System.out.println(nomme(false) + " semble réagir positivement à votre approche.");
                ratio = min(50, ratio - 10 * temp);
            } else {
                
                System.out.println(nomme(false) + " réagit agressivement.");
                return false;
            }
        }
        if (ratio - Input.D6() * 10 < 0) {
            System.out.println(nomme(false) + " vous accorde sa confiance.");
            if (!est_pantin()) {
                this.vie_max = -1; //valeur spéciale pour indiquer la défaite du monstre
            }
            return true;
        }
        System.out.println(nomme(false) + " réagit agressivement.");
        return false;
    }
    
    /**
     * Applique la compétence "assommer" sur le monstre
     * Demande aux joueurs les informations nécessaires
     * @throws IOException jsp mais sans ça, ça ne marche pas
     */
    public void assommer(Joueur j) throws IOException {
        // compétence ennemie
        switch (getCompetence()) {
            case VOL, VOLAGE, CAUCASE -> {
                Texte.esquive_vol(nomme(false));
                cycle_vol();
                return;
            }
            case FURTIF -> {
                System.out.println("Vous ne parvenez plus à identifier où se trouve " + nomme(false) + " et renoncez à " +
                        "attaquer.");
                supprime_competence();
                return;
            }
            
        }
        
        int attaque = j.puissance_attaque();
        if(attaque <= 0){
            return;
        }
        
        //action
        float multi;
        int bonus = Main.corriger(j.getBerserk(), 0);
        int jet;
        if(bonus < 6){
            jet = Input.D6() + bonus;
            if (jet > 7) {
                jet = 7;
            }
        } else {
            jet = 7;
        }
        switch (jet) {
            case 1:
                System.out.println("Vous manquez votre cible.");
                multi = 0f;
                break;
            case 2:
                System.out.println("Vous frappez de justesse votre cible, au moins, vous l'avez touchée.");
                multi = 0.1f;
                affecte();
                break;
            case 3, 4:
                multi = 0.5f;
                affecte();
                break;
            case 5:
                multi = 0.5f;
                do_assomme();
                break;
            case 6:
                multi = 1f;
                System.out.println("Vous frappez avec force !");
                do_assomme();
                break;
            case 7:
                System.out.println("Vous frappez à vous en blesser les bras !");
                if (rand.nextBoolean()) {
                    System.out.println("Vous subissez 1 point de dommage.");
                }
                do_assomme();
                multi = 1f + bonus;
                break;
            default:
                System.out.println("Le résultat n'a pas été comprit, attaque classique appliquée.");
                multi = 1f;
        }
        if(multi <= 0) {
            return;
        }
        attaque = Main.corriger(attaque * multi);
        dommage(attaque);
    }
    
    /**
     * Affiche les statistiques de base du monstre
     */
    public void presente_familier() {
        System.out.println("nouveau familier : " + getNom());
        System.out.println("attaque : " + this.attaque_base);
        System.out.println("vie : " + this.vie_base);
        System.out.println("armure : " + this.armure_base + "\n");
    }
    
    public void gere_nomme() throws IOException {
        if (est_nomme()) {
            Output.dismiss_race(getNom());
            Race.delete_monstre(getNom());
            SaveManager.sauvegarder(true);
        }
    }
    
    public void presente_analyse(int jet) {
        int pv, pvm, arm, atk;
        Monstre modele = switch (getCompetence()) {
            case ILLU_AURAI -> new Monstre(Race.aurai_malefique);
            case ILLU_CYCLOPE -> new Monstre(Race.cyclope);
            case ILLU_DULLA -> new Monstre(Race.dullahan);
            case ILLU_GOLEM -> new Monstre(Race.golem);
            case ILLU_ROCHE -> new Monstre(Race.roche_maudite);
            case ILLU_SIRENE -> new Monstre(Race.sirene);
            case ILLU_TRITON -> new Monstre(Race.triton);
            case ILLU_VENTI -> new Monstre(Race.venti);
            default -> this;
        };
        
        pvm = modele.getVieMax();
        pv = pvm - (getVieMax() - getVie());
        arm = modele.getArmure();
        atk = modele.getAtk();
        
        System.out.println(getNom() + " :");
        if (est_pantin()) {
            System.out.println("vie : " + (jet >= 5 ? "∞" : "???") + "/" + (jet >= 2 ? "∞" : "???"));
        } else {
            System.out.println("vie : " + (jet >= 5 ? pv : "???") + "/" + (jet >= 2 ? pvm : "???"));
        }
        System.out.println("attaque : " + (jet >= 3 ? atk : "???"));
        System.out.println("armure : " + (jet >= 7 ? arm : "???"));
        System.out.println();
    }
}
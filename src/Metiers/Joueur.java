package Metiers;

import Exterieur.Input;

import Enum.Metier;
import Enum.Position;
import Enum.Action;
import Enum.Action_extra;
import Enum.Dieux;

import Equipement.Equipement;

import Monstre.Lieu;
import Monstre.Monstre;

import main.Main;
import main.Combat;

import java.io.IOException;
import java.util.Random;
import javax.json.*;
import java.io.*;

public abstract class Joueur {
    static final int f_max = 7;
    protected final String nom;
    private Position position;
    private int ob_f;
    private final Dieux parent;
    private int xp;
    protected int niveau;

    // stat
    protected int vie;
    protected int attaque;
    protected int armure;
    protected String PP;
    protected int PP_value;
    protected int PP_max;
    protected String caracteristique;
    protected String competences;

    // en combat
    protected boolean front;
    protected boolean actif;
    protected boolean vivant;
    protected boolean conscient;
    protected boolean skip;
    protected int reveil;
    protected float berserk;
    protected boolean cecite;
    protected boolean poison1;
    protected boolean poison2;

    //familier en combat
    protected boolean f_front;
    protected boolean f_actif;
    protected boolean f_conscient;
    protected boolean f_skip;
    protected int f_reveil;
    protected float f_berserk;
    protected boolean f_cecite;
    protected boolean f_poison1;
    protected boolean f_poison2;

    //modificateur
    protected static int attaque_bonus = 0;
    protected static int tir_bonus = 0;
    protected static int tour_modif = 0;

    protected int bonus_infection;

    Joueur(String nom, Position position, int ob_f, Dieux parent, int xp) {
        this.nom = nom;
        this.position = position;
        this.ob_f = ob_f;
        this.parent = parent;
        setNiveau(xp);
        this.armure = 0;
        actualiser_niveau();
        SetEffetParent();
    }

    //************************************************CHARGEMENT******************************************************//

    /**
     * Crée un joueur à partir de sa sauvegarde
     * @param chemin le nom du fichier où se trouve la sauvegarde
     * @return le joueur
     * @throws FileNotFoundException evidemment on cherche à charger un fichier
     */
    public static Joueur chargerJoueur(String chemin) throws FileNotFoundException {
        try (JsonReader reader = Json.createReader(new FileReader(chemin))) {
            JsonObject json = reader.readObject();

            // Les valeurs qui peuvent ne pas être écrite
            int ob_f = 0, xp = 0;
            Position position = Position.DEFAULT;
            Dieux parent = Main.get_parent();

            String nom = json.getString("nom");
            Metier metier = Metier.valueOf(json.getString("metier"));
            try {
                ob_f = json.getInt("ob_f");
            } catch (Exception e) {
                System.out.println("Erreur : impossible de récuperer l'obéissance du familier. " + e.getMessage());
                System.out.println("Valeur par défaut appliquée : aucun familier");
            }
            try {
                position = Position.valueOf(json.getString("position"));
            } catch (Exception e) {
                System.out.println("Erreur : impossible de récuperer la zone. " + e.getMessage());
                System.out.println("Valeur par défaut appliquée : Prairie");
            }
            try {
                parent = Dieux.valueOf(json.getString("parent"));
            } catch (Exception e) {
                System.out.println("Erreur : impossible de récuperer le parent divin. " + e.getMessage());
                System.out.println("Valeur par défaut appliquée : aléatoire : " + parent.toString());
            }
            try {
                xp = json.getInt("xp");
            } catch (Exception e) {
                System.out.println("Erreur : impossible de récuperer l'xp du joueur. " + e.getMessage());
                System.out.println("Valeur par défaut appliquée : 0");
            }

            return CreerJoueur(nom, position, metier, ob_f, parent, xp);
        }
    }

    /**
     * Crée un joueur en fonction de ses données
     * @param nom le nom du joueur à créer
     * @param position la position du joueur à créer
     * @param metier la classe du joueur à créer
     * @param ob_f l'obéissance du joueur à créer (0 s'il n'en a pas).
     * @param parent l'ancêtre divin du joueur à créer
     * @param xp le total d'éxperience accumulée par lejoueur à créer
     * @return le joueur
     */
    public static Joueur CreerJoueur(String nom, Position position, Metier metier, int ob_f, Dieux parent, int xp) {
        return switch (metier) {
            case NECROMANCIEN -> new Necromancien(nom, position, ob_f, parent, xp);
            case ARCHIMAGE -> new Archimage(nom, position, ob_f, parent, xp);
            case ALCHIMISTE -> new Alchimiste(nom, position, ob_f, parent, xp);
            case GUERRIERE -> new Guerriere(nom, position, ob_f, parent, xp);
            case RANGER -> new Ranger(nom, position, ob_f, parent, xp);
            case SHAMAN -> new Shaman(nom, position, ob_f, parent, xp);
            case TRYHARDER -> new Tryharder(nom, position, ob_f, parent, xp);
        };
    }

    /**
     * Met à jour les informations du personnage à partir de son niveau
     */
    protected abstract void actualiser_niveau();

    //************************************************SAUVEGARDE******************************************************//

    public void sauvegarder(String chemin) throws IOException {
        JsonObject joueurJson = Json.createObjectBuilder()
                .add("nom", this.nom)
                .add("metier", this.getMetier().name())
                .add("ob_f", this.ob_f)
                .add("position", this.position.name())
                .add("xp", this.GetXpTotal())
                .add("parent", this.parent.name())
                .add("effets", "") // TODO
                .build();

        try (JsonWriter writer = Json.createWriter(new FileWriter(chemin))) {
            writer.writeObject(joueurJson);
        }
    }

    static Random rand = new Random();

    //************************************************PRESENTATION****************************************************//

    /**
     * Présente les caractéristiques et statistiques du joueur
     */
    public void presente_detail() {
        System.out.println(nom + " descendant de " + getParent() + ", " + nomMetier());
        System.out.print("Base : Résistance : " + vie);
        int temp = bonusResLieux();
        if(temp > 0){
            System.out.print("(+" + temp + ")");
        }
        System.out.print(" ; attaque : " + attaque);
        temp = bonusAtkLieux();
        if(temp > 0){
            System.out.print("(+" + temp + ")");
        }
        System.out.print(" ; armure : " + armure);
        temp = bonusArmLieux();
        if(temp > 0){
            System.out.print("(+" + temp + ")");
        }
        System.out.println( " ; " + PP + " : " + PP_value + "/" + PP_max);
        System.out.println("Caractéristiques : " + caracteristique);
        System.out.println("Pouvoir : " + competences);
        System.out.println();
        presente_caracteristique();
        System.out.println();
        presente_pouvoir();
        System.out.println();
        System.out.println(DescribeEffetParent());
    }

    /**
     * Présente la condition et position du joueur
     */
    public void presente() {
        System.out.print(this.nom + ", descendant de " + nomDieux() + ", est " + nomMetier() + " (niveau " + this.niveau
                + ")" + " et se trouve " + Main.texte_pos(getPosition()));
        if (a_familier()) {
            System.out.print(" avec son familier");
        }
        System.out.println(".");
    }

    abstract void presente_caracteristique();

    abstract void presente_pouvoir();

    //************************************************GETTER**********************************************************//

    public int getXp(){
        return xp;
    }

    public Dieux getParent(){
        return parent;
    }

    abstract public Metier getMetier();

    abstract protected String nomMetier();

    protected String nomDieux(){
        return switch(parent){
            case ZEUX -> "Zeus";
            case ARES -> "Ares";
            case HADES -> "Hades";
            case APOLLON -> "Apollon";
            case DEMETER -> "Demeter";
            case DYONISOS -> "Dyonisos";
            case POSEIDON -> "Poseidon";
        };
    }

    /**
     * Applique les caractéristiques et capacités héréditaires
     */
    protected void SetEffetParent() {
        switch(parent) {
            case ARES -> {
                if (getMetier() != Metier.GUERRIERE) {
                    competences += ", Berserk";
                }
            }
            case HADES -> {
                if(getMetier() != Metier.NECROMANCIEN){
                    caracteristique += ", Thaumaturge";
                }
            }
            case APOLLON -> competences += ", Infection";
            case DEMETER -> competences += ", Sérénité";
            case DYONISOS -> caracteristique += ", Sens des affaires";
            case POSEIDON -> competences += ", Inondation";
            case ZEUX -> competences += ", Foudre";
        }
    }

    /**
     * Présente les caractéristiques et capacités héréditaires
     */
    private String DescribeEffetParent() {
        return switch(parent) {
            case ARES -> {
                if (getMetier() != Metier.GUERRIERE) {
                    yield "Berserk : pour 1 mana/aura, imprègne de folie meutrière l'esprit du lanceur avant qu'il " +
                            "ne frappe, augmentant sa puissance au prix de sa santé mentale.";
                }
                yield "";
            }
            case HADES -> {
                if(getMetier() != Metier.NECROMANCIEN){
                    yield "Thaumaturge : Quand il meurt, un thaumaturge peut emporter avec lui 3 pièces d'équipements " +
                            "de son choix et 6PO dans l'au-delà.";
                }
                yield "";
            }
            case APOLLON -> "Infection : pour 1 mana, augmente les dommages de vos tirs pour un combat.";
            case DEMETER -> "Sérénité : pour 2 mana/aura, soigne une cible.";
            case DYONISOS -> "Sens des affaires : diminue les prix des objets en vente aux marchés.";
            case POSEIDON -> "Inondation : Un sort qui consomme 4 mana et inflige de gros dommages magiques.";
            case ZEUX -> "Foudre : Un sort qui consomme 5 mana et inflige de puissants dommages magiques.";
        };
    }

    /**
     * Convertie l'expérience en niveau et expérience
     * Remplit les champs nécessaires
     * @param experience l'expérience totale du joueur
     */
    private void setNiveau(int experience){
        int niveau = 0;
        while(experience > 5 * (niveau + 1)){
            niveau++;
            experience -= niveau * 5;
        }
        this.niveau = niveau;
        this.xp = experience;
    }

    /**
     * Convertie les niveaux et l'expérience du joueur en valeur d'xp uniquement
     * @return l'ensemble de l'xp que le joueur possède
     */
    private int GetXpTotal(){
        int xp_totale = this.xp;
        if(xp_totale < 0){
            xp_totale = 0;
        }
        for(int nv = this.niveau; nv > 0; nv--){
            xp_totale += 5 * nv;
        }
        return xp_totale;
    }

    protected void setOb(int value) {
        this.ob_f = value;
    }

    public boolean a_familier() {
        return ob_f > 0;
    }

    public boolean familier_loyalmax() {
        return ob_f >= f_max;
    }

    public Position getPosition() {
        return position;
    }

    public String getNom() {
        return nom;
    }

    public int get_ob_f() {
        return ob_f;
    }

    public boolean est_actif() {
        return actif;
    }

    public boolean a_familier_actif() {
        return a_familier() && f_actif;
    }

    public boolean a_familier_front() {
        return a_familier_actif() && front && f_front;
    }

    public boolean est_berserk() {
        return berserk > 0;
    }

    public boolean f_est_berserk() {
        return f_berserk > 0;
    }

    public boolean familier_peut_pas_jouer() {
        return !a_familier_actif() || f_skip || f_est_assomme();
    }

    public boolean est_assomme() {
        return !conscient;
    }

    public boolean f_est_assomme() {
        return !f_conscient;
    }

    public boolean est_front() {
        return front;
    }

    public boolean est_front_f() {
        return f_front;
    }

    public boolean est_vivant() {
        return vivant;
    }

    public boolean est_pas_activable() {
        return !est_actif() || !est_vivant();
    }

    public float getBerserk() {
        return berserk;
    }

    public boolean front_a_cecite() {
        if (a_familier_front()) {
            return f_a_cecite();
        }
        return a_cecite();
    }

    public boolean a_cecite() {
        return cecite;
    }

    private boolean f_a_cecite() {
        return f_cecite;
    }

    public void prend_cecite() {
        if (a_familier_front()) {
            f_prend_cecite();
        } else {
            p_prend_cecite();
        }
    }

    private void p_prend_cecite() {
        cecite = true;
        System.out.println(nom + " est empoisonné(e) et atteint(e) de cécité.");
    }

    private void f_prend_cecite() {
        f_cecite = true;
        System.out.println("Le familier de " + nom + " est empoisonné et atteint de cécité.");
    }

    public boolean front_a_poison1() {
        if (a_familier_front()) {
            return f_a_poison1();
        }
        return a_poison1();
    }

    private boolean a_poison1() {
        return poison1;
    }

    private boolean f_a_poison1() {
        return f_poison1;
    }

    public void prend_poison1() {
        if (a_familier_front()) {
            f_prend_poison1();
        } else {
            p_prend_poison1();
        }
    }

    private void p_prend_poison1() {
        poison1 = true;
        System.out.println(nom + " est empoisonné(e).");
    }

    private void f_prend_poison1() {
        f_poison1 = true;
        System.out.println("Le familier de " + nom + " est empoisonné.");
    }

    public void prend_poison2() {
        if (a_familier_front()) {
            f_prend_poison2();
        } else {
            p_prend_poison2();
        }
    }

    private void p_prend_poison2() {
        poison2 = true;
        System.out.println(nom + " est empoisonné(e).");
    }

    private void f_prend_poison2() {
        System.out.println("Le familier de " + nom + " est empoisonné.");
        f_poison2 = true;
    }

    public boolean front_a_poison2() {
        if (a_familier_front()) {
            return f_a_poison2();
        }
        return a_poison2();
    }

    private boolean a_poison2() {
        return poison2;
    }

    private boolean f_a_poison2() {
        return f_poison2;
    }

    protected int bonusResLieux(){
        return switch(position){
            case ASCENDANT, OLYMPE -> 0;
            case ENFERS -> parent == Dieux.HADES ? 2 : 0;
            case PRAIRIE -> parent == Dieux.DEMETER ? 2 : 0;
            case VIGNES -> parent == Dieux.DYONISOS ? 4 : 0;
            case TEMPLE -> parent == Dieux.APOLLON ? 4 : 0;
            case MER -> parent == Dieux.POSEIDON ? 5 : 0;
            case MONTS -> parent == Dieux.ZEUX ? 5 : 0;
        };
    }

    protected int bonusAtkLieux(){
        return switch(position){
            case ASCENDANT -> 0;
            case ENFERS -> {
                if(parent == Dieux.HADES){
                    yield 1;
                }
                yield 0;
            }
            case PRAIRIE -> {
                if(parent == Dieux.DEMETER){
                    yield 2;
                }
                else if(parent == Dieux.ARES){
                    yield 1;
                }
                yield 0;
            }
            case VIGNES -> {
                if(parent == Dieux.DYONISOS){
                    yield 1;
                }
                else if(parent == Dieux.ARES){
                    yield 1;
                }
                yield 0;
            }
            case TEMPLE -> {
                if(parent == Dieux.APOLLON || parent == Dieux.ARES){
                    yield 2;
                }
                yield 0;
            }
            case MER -> {
                if(parent == Dieux.POSEIDON){
                    yield 2;
                }
                else if(parent == Dieux.ARES){
                    yield 3;
                }
                yield 0;
            }
            case MONTS -> {
                if(parent == Dieux.ZEUX){
                    yield 5;
                }
                else if(parent == Dieux.ARES){
                    yield 3;
                }
                yield 0;
            }
            case OLYMPE -> {
                if(parent == Dieux.ARES){
                    yield 4;
                }
                yield 0;
            }
        };
    }

    protected int bonusArmLieux(){
        if((position == Position.MONTS && parent == Dieux.ZEUX) || (position == Position.MER && parent == Dieux.POSEIDON)){
            return 1;
        }
        return 0;
    }

    //************************************************METHODE INDEPENDANTE********************************************//

    /**
     * Donne de l'expérience au joueur de 1.
     * @implNote Ce qui agmente l'xp : participer à un combat, domestiquer un monstre, porter le dernier coup,
     * ressuciter un allié, avoir affronté un monstre nommé (victoire ou fuite)
     */
    public void gagneXp(){
        this.xp += 1;
        if(this.xp >= (this.niveau + 1) * 5){
            this.niveau += 1;
            this.xp -= this.niveau * 5;
            System.out.println(nom + " a gagné un niveau !");
            lvl_up();
        }
    }

    /**
     * Méthode permettant aux classes filles d'ajouter de l'expérience
     */
    protected void gagneXpLocal(){
        this.xp += 1;
    }

    /**
     * Méthode permettant aux classes filles d'analyser la quantité d'expérience
     */
    protected int getXplocal(){
        return this.xp;
    }

    /**
     * Méthode permettant de remettre à 0 l'expérience d'un joueur
     */
    protected void resetXpLocal(){
        this.xp = 0;
    }

    /**
     * Ajoute les données dû à la montée de niveau
     */
    abstract void lvl_up();

    /**
     * Compte les tours pour arrêter les bonus de vent du shaman
     */
    public static void debut_tour() {
        if (tour_modif > 0) {
            tour_modif--;
            if (tour_modif == 0) {
                tir_bonus = 0;
                attaque_bonus = 0;
                System.out.println("Le vent se couche.");
            }
        }
    }

    public static void monstre_mort(Monstre ennemi) throws IOException {
        for (int i = 0; i < Main.nbj; i++) {
            Main.joueurs[i].monstre_mort_perso(ennemi);
            if (!ennemi.corps_utilisable()) {
                System.out.println("Le cadavre du monstre n'est plus qu'une masse informe et inutilisable.");
                return;
            }
        }
    }

    /**
     * Met le joueur en condition de début de combat (avec le choix de participer)
     *
     * @param force si le joueur est forcé de se battre
     * @param pos   la position du combat
     * @throws IOException toujours
     */
    public void init_affrontement(boolean force, Position pos) throws IOException {
        if (!force && (pos != position || !Input.yn("Est-ce que " + nom + " participe au combat ?"))) {
            actif = false;
            f_actif = false;
            return;
        }
        actif = true;
        vivant = true;
        conscient = true;
        skip = false;
        reveil = 0;
        berserk = 0;
        cecite = false;
        poison1 = false;
        poison2 = false;
        front = false;
        f_front = false;
        attaque_bonus = 0;
        tir_bonus = 0;
        bonus_infection = 0;
        tour_modif = 0;
        if (a_familier() && Input.yn("Est-ce que votre familier participe au combat ?")) {
            f_actif = true;
            f_conscient = true;
            f_skip = false;
            f_reveil = 0;
            f_cecite = false;
            f_poison1 = false;
            f_poison2 = false;
        } else {
            f_actif = false;
        }
    }

    /**
     * Demande au joueur d'aller en première ligne et gère les résultats
     *
     * @param force si le joueur DOIT passer en première ligne
     * @return si le joueur passe en première ligne
     * @throws IOException toujours
     */
    public boolean faire_front(boolean force) throws IOException {
        if (!est_actif()) {
            return false;
        }
        if (force || Input.yn(nom + " veut-il passer en première ligne ?")) {
            front = true;
            if (a_familier_actif() && Input.yn(nom + " envoit-il/elle son familier devant lui ?")) {
                f_front = true;
            }
            return true;
        }
        return false;
    }

    public void f_faire_front() {
        f_front = true;
    }

    /**
     * Renvoie le nom de l'entité de front (le joueur ou le familier)
     */
    public String getFrontNom() {
        if (a_familier_front()) {
            return "Le familier de " + nom;
        }
        return nom;
    }

    /**
     * Simule l'action d'un familer auquel on ne donne pas d'ordre
     *
     * @param ennemi le monstre ennemi
     * @throws IOException toujours
     */
    public void familier_seul(Monstre ennemi) throws IOException {
        if (!a_familier_actif() || ob_f <= 3 || f_est_assomme()) {
            return;
        }
        System.out.println("Votre familier attaque l'ennemi pour proteger " + nom + ".");
        f_attaque(ennemi);
    }

    public void inactiver() {
        actif = false;
        f_actif = false;
    }

    public void f_inactiver() {
        f_actif = false;
    }

    /**
     * Traite la fin de combat des joueurs
     * @param ennemi_nomme si l'ennemi était un monstre nommé (bonus d'xp).
     * @throws IOException toujours
     */
    public void fin_affrontement(boolean ennemi_nomme) throws IOException {
        if (est_actif() && !est_vivant() && Input.yn(getNom() + " est mort durant le combat, le reste-t-il/elle ?")) {
            if (auto_ressuciter(0)) {
                System.out.println(getNom() + " résiste à la mort.");
            } else {
                mort_def();
            }
        }
        if(est_actif()){
            gagneXp();
            if(ennemi_nomme){
                gagneXp();
            }
        }
        actif = false;
        f_actif = false;
    }

    /**
     * Essaie de se réveiller (assommé)
     *
     * @throws IOException toujours
     */
    public void essaie_reveil() throws IOException {
        if (!est_assomme()) {
            return;
        }
        System.out.println(getNom() + " est inconscient(e).");
        if (Input.D6() + reveil >= 6) {
            System.out.println(nom + " se réveille.\n");
            conscient = true;
            reveil = 0;
            return;
        }
        System.out.println(nom + " est toujours inconscient.");
        reveil += 1;
    }

    /**
     * Le familier du joueur essaie de se réveiller, sans effet s'il est conscient
     *
     * @throws IOException toujours
     */
    public void f_essaie_reveil() throws IOException {
        if (!f_est_assomme()) {
            return;
        }
        System.out.println("Le familier de " + getNom() + " est inconscient(e).");
        if (Input.D6() + f_reveil >= 5) {
            System.out.println("Le familier de " + nom + " se réveille.\n");
            f_conscient = true;
            f_reveil = 0;
            return;
        }
        System.out.println("Le familier de " + nom + " est toujours inconscient.");
        f_reveil += 1;
    }

    /**
     * Calcule et applique les effets d'une attaque à distance
     *
     * @param ennemi le monstre ennemi
     * @param bonus_popo les dommages additionel des popo (ici uniquement les instables)
     * @throws IOException toujours
     */
    public void tirer(Monstre ennemi, int bonus_popo) throws IOException {
        int base = Input.tir();
        float bonus = 0;
        if (est_berserk()) {
            bonus = berserk_atk(base);
            if (bonus == berserk_atk_alliee) {
                return;
            }
        }
        bonus += critique_tir(base);
        bonus += bonus_tir();
        bonus += tir_bonus;
        bonus += bonus_infection;
        bonus += bonus_popo;
        ennemi.tir(base + Main.corriger(bonus, 0));
    }

    /**
     * Calcule et applique les effets d'une attaque classique sur un monstre
     * @param ennemi le monstre ennemi
     * @throws IOException toujours
     */
    public void attaquer(Monstre ennemi, int bonus_popo) throws IOException {
        int base = Input.atk();
        float bonus = calcule_bonus_atk(base, bonus_popo);
        if(bonus != berserk_atk_alliee){
            ennemi.dommage(base + Main.corriger(bonus, 0));
        }
    }

    /**
     * Calcule les dommages bonus d'une attaque classique
     * @param base les dommages de base de l'attaque
     * @param bonus_popo les dommages des consommables
     * @return la valeur des dommages bonus (arrondits)
     * @throws IOException toujours
     */
    protected float calcule_bonus_atk(int base, int bonus_popo) throws IOException {
        float bonus = 0;
        if (est_berserk()) {
            bonus = berserk_atk(base);
            if (bonus == berserk_atk_alliee) {
                return berserk_atk_alliee;
            }
        }
        bonus += critique_atk(base);
        bonus += bonus_atk();
        bonus += attaque_bonus;
        bonus += bonus_popo;
        return bonus;
    }

    /**
     * Gère le cas où le joueur à un problème de dépendance quelconque
     */
    public void addiction() throws IOException {

    }

    /**
     * Rends la vie à un mort
     * peut l'assommer
     *
     * @param malus une augmentation de la probabilité d'être assommé longtemps
     */
    public void do_ressucite(int malus) {
        vivant = true;
        int luck = rand.nextInt(4) - malus;
        if (luck < 0) {
            assomme(4 - malus);
        }
    }

    /**
     * Met à jour les données d'un joueur qui vient de mourir
     * réinitialise ses états à l'exception de la mort
     */
    public void rendre_mort() {
        vivant = false;
        reveil = 0;
        conscient = true;
        berserk = 0;
    }

    /**
     * Met à jour les données d'un joueur mort hors combat
     */
    public void mort_def() {
        System.out.println(nom + " est mort.");
        ob_f = 0;
        position = Position.ENFERS;
        if(this.parent == Dieux.HADES || getMetier() != Metier.NECROMANCIEN){
            System.out.println("Grâce à vos dons héréditaires, vous pouvez choisir 3 pièces d'équipements " +
                    "que vous conservez (vous devez entrer à nouveau les effets cachées). De plus, vous pouvez conserver" +
                    " jusqu'à 6 PO.");
            for(int i = this.niveau; i > 0; i--){
                if(rand.nextInt(3) == 0){
                    this.xp -= 1;
                }
            }
        }
        else{
            for(int i = this.niveau; i > 0; i--){
                if(rand.nextBoolean()){
                    this.xp -= 1;
                }
            }
        }
        System.out.println(nom + " perd de l'expérience.");
    }

    /**
     * Met à jour les données d'un joueur qui vient de perdre un familier
     */
    public void f_rendre_mort() {
        ob_f = 0;
        f_actif = false;
        f_front = false;
    }

    /**
     * Assomme le joueur
     * Annule son état de berserk
     */
    public void assomme() {
        assomme(0);
    }

    /**
     * Assomme le joueur
     * Annule son état de berserk
     *
     * @param reveil le bonus (ou malus) de réveil
     */
    public void assomme(int reveil) {
        conscient = false;
        this.reveil = reveil;
        berserk = 0;
    }

    public void f_assomme() {
        f_assomme(0);
    }

    public void f_assomme(int reveil) {
        f_conscient = false;
        f_reveil = reveil;
    }

    public void berserk(float rage) {
        this.berserk = rage;
    }

    public void f_berserk(float rage) {
        this.f_berserk = rage;
    }

    public void f_attaque(Monstre ennemi) throws IOException {
        // berserk
        if (f_est_berserk()) {

            //folie
            if (Input.D6() + ob_f * 0.5f < 2 + f_berserk) {
                int l;
                do {
                    l = rand.nextInt(8);
                } while (!Main.joueurs[l].est_actif());
                int temp = Input.atk();
                temp += Main.corriger(temp * (f_berserk / 2));
                System.out.println("Pris(e) de folie, le familier de " + nom + " attaque " + Main.joueurs[l].getNom()
                        + " et lui inflige " + temp + " dommages !");
            } else {
                ennemi.dommage(Input.atk(), f_berserk + 1);
            }
            f_berserk += rand.nextInt(3) * 0.1f;
            return;
        }
        //attaque classique
        if (rand.nextInt(255) == 0) {
            ennemi.dommage(Input.atk(), 1.1f + 0.1f * rand.nextInt(5));
            return;
        }
        ennemi.dommage(Input.atk());
    }

    //************************************************MAIN************************************************************//

    public void descendre() {
        this.position = switch (position) {
            case VIGNES -> Position.PRAIRIE;
            case TEMPLE -> Position.VIGNES;
            case MER -> Position.TEMPLE;
            case MONTS -> Position.MER;
            case ASCENDANT -> {
                System.out.println("ERROR : DONOT");
                yield Position.PRAIRIE;
            }
            default -> { //ENFERS, PRAIRIES, OLYMPE
                System.out.println("Erreur : position " + position + " ne peut être descendue !");
                yield position;
            }
        };
    }

    public void ascendre(int index) throws IOException {
        Position pos = position;
        position = Position.ASCENDANT;  //on isole le joueur
        String text = nom;
        int lead = -1;
        Monstre m;
        int bonus = bonus_exploration();
        int jet = Input.D4() + bonus;
        if (jet > 4) {
            jet = 4;
        } else if (jet < 1) {
            jet = 1;
        }
        switch (jet) {
            case 1 -> {
                text += " rencontre un monstre vers la fin de son voyage.";
                m = Lieu.true_monstre(pos, true);
            }
            case 2 -> {
                text += " est attaqué par un monstre à peine parti(e).";
                lead = index;
                m = Lieu.true_monstre(pos);
            }
            case 3 -> {
                text += " rencontre un monstre à peine après le début de son voyage.";
                m = Lieu.true_monstre(pos);
            }
            case 4 -> {
                position = pos;
                monter();
                System.out.println(text + " parvient sans encombre " + Main.texte_pos(position) + ".");
                return;
            }
            default -> {
                System.out.println("Erreur : résultat inatendu. Action annulée.");
                position = pos;
                return;
            }
        }
        System.out.println(text);
        Combat.affrontement(Position.ASCENDANT, lead, m);
        if (position == Position.ENFERS) { //le joueur est mort.
            return;
        }
        position = pos;
        if (Input.yn(nom + " a-t-il vaincu le monstre ?")) {
            monter();
            System.out.println(nom + " arrive " + Main.texte_pos(position) + ".");
        } else {
            System.out.println(nom + " est resté " + Main.texte_pos(position));
        }
    }

    protected void monter() {
        position = switch (position) {
            case ENFERS -> Position.PRAIRIE;
            case PRAIRIE -> Position.VIGNES;
            case VIGNES -> Position.TEMPLE;
            case TEMPLE -> Position.MER;
            case MER -> Position.MONTS;
            case MONTS -> Position.OLYMPE;
            case OLYMPE -> {
                System.out.println("Erreur : position " + position + " ne peut être augmentée !");
                yield position;
            }
            case ASCENDANT -> {
                System.out.println("ERROR : DONOT");
                yield Position.ENFERS;
            }
        };
    }

    public void dresser() throws IOException {
        if (!a_familier()) {
            System.out.println("Erreur : aucun familier détecté.");
            return;
        }
        ob_f += entrainer();
        if (!a_familier()) {
            System.out.println("Votre familier vous a fuit de manière définitive.");
        } else if (familier_loyalmax()) {
            System.out.println("Vous avez atteint le niveau maximal de loyauté de la part de votre familier.");
        }
        corrige_ob();
    }

    protected int entrainer() throws IOException {
        return switch (Input.D6()) {
            case 1 -> {
                if (Input.D4() <= 2) {
                    System.out.println("Votre familier désapprouve fortement vos méthodes d'entrainement.\n");
                    yield -1;
                }
                System.out.println();
                yield 0;
            }
            case 2, 3 -> {
                System.out.println("Vous familier n'a pas l'air très attentif...\n");
                yield 0;
            }
            case 4, 5 -> {
                System.out.println("Votre familier vous respecte un peu plus.\n");
                yield 1;
            }
            case 6 -> {
                if (Input.D4() >= 3) {
                    System.out.println("Votre familier semble particulièrement apprécier votre entrainement !\n");
                    yield 2;
                }
                System.out.println();
                yield 1;
            }
            default -> {
                System.out.println("Résultat non reconnu, compétence ignorée.\n");
                yield 0;
            }
        };
    }

    protected void corrige_ob() {
        if (ob_f < 0) {
            ob_f = 0;
        } else if (ob_f > f_max) {
            ob_f = f_max;
        }
    }

    /**
     * Traite l'ajout d'un nouveau familier
     *
     * @throws IOException toujours
     */
    public void ajouter_familier() throws IOException {
        ajouter_familier(1);
    }

    /**
     * Traite l'ajout d'un nouveau familier
     *
     * @param obeissance l'obéissance du nouveau familier
     * @return si le familier a été réellement ajouté
     * @throws IOException toujours
     */
    public boolean ajouter_familier(int obeissance) throws IOException {
        if (a_familier() && !Input.yn(nom + " possède déjà un familier, le remplacer ? ")) {
            System.out.println("Ancien familier conservé.\n");
            return false;
        } else {
            System.out.println(nom + " a un nouveau familier.\n");
            ob_f = obeissance;
        }
        return true;
    }

    /**
     * Tue le familier du joueur
     */
    public void perdre_familier() {
        System.out.println("Le familier de " + nom + " est mort.");
        ob_f = 0;
    }

    public void aller_au_marche() {
        int reduc = 0;
        if(getParent() == Dieux.DYONISOS){
            reduc = 2;
        }
        switch (position) {
            case PRAIRIE -> Equipement.marche_prairie(reduc);
            case VIGNES -> Equipement.marche_vigne(reduc);
            case TEMPLE -> Equipement.marche_temple(reduc);
            case MER -> Equipement.marche_mer(reduc);
            case MONTS -> Equipement.marche_monts(reduc);
            case ENFERS, OLYMPE -> System.out.println("Erreur : Il n'y a pas de marché ici.");
            case ASCENDANT -> System.out.println("ERROR : DONOT");
        }
    }

    //************************************************METHODE METIER**************************************************//

    /**
     * Extension de la fonction tour, annonce les choix de métier possibles
     *
     * @return le texte à ajouter
     */
    public String text_tour() {
        return "";
    }

    /**
     * Extension de la fonction Input.tour, permet de jouer les choix de métier
     *
     * @param choix le choix fait par le joueur
     * @return si le tour a été joué
     */
    public boolean tour(String choix) throws IOException {
        return false;
    }

    /**
     * Extension de la fonction Input.action, annonce les actions possibles du joueur
     *
     * @return le texte à ajouter
     */
    public String text_action() {
        if (est_assomme()) {
            return nom + " entrez votre action : ";
        }
        String text = nom + " entrez votre action : (A)ttaquer";
        if (!a_cecite()) {
            text += "/(t)irer";
        }
        if (!est_berserk() && getMetier() != Metier.ARCHIMAGE) {
            text += "/(m)agie";
        }
        if (est_front()) {
            text += "/a(s)sommer";
            if (!est_berserk()) {
                text += "/(e)ncaisser/(d)omestiquer";
            }
        } else {
            text += "/(s)'avancer";
        }
        if (!est_berserk()) {
            text += "/(p)remier soin";
            if (getParent() == Dieux.ARES && getMetier() != Metier.GUERRIERE) {
                text += "/(b)erserker";
            }
            if(getParent() == Dieux.APOLLON){
                text += "/(i)nfection";
            }
            if(getParent() == Dieux.DEMETER){
                text += "/sé(r)enité";
            }
            if(getParent() == Dieux.POSEIDON){
                text += "/(i)nondation";
            }
            if(getParent() == Dieux.ZEUX){
                text += "/(f)oudre";
            }
        }
        text += "/(f)uir/(c)ustom/(o)ff";
        return text;
    }

    /**
     * Extension de la fonction Input.action, annonce les actions possibles du familier
     *
     * @return le texte à ajouter
     */
    public String f_text_action() {
        String text = "Donnez un ordre au familier de " + nom + " : (A)ttaquer/(f)uir/(c)ustom";
        if (est_front_f()) {
            text += "/(e)ncaisser";
        } else if (est_front()) {
            text += "/pa(s)ser devant/(v)eiller";
        }
        return text;
    }

    /**
     * Extension de la fonction Input.action, permet de jouer les action de métier
     *
     * @param choix le choix fait par le joueur (en lowercase)
     * @param est_familier s'il s'agit d'un familier ou d'un joueur
     * @return l'action à jouer, ou AUCUNE si le choix n'est pas reconnu
     */
    public Action action(String choix, boolean est_familier) throws IOException {
        if(est_familier){
            return Action.AUCUNE;
        }
        return switch (choix) {
            case "b" -> {
                if(!est_berserk() && getParent() == Dieux.ARES && getMetier() != Metier.GUERRIERE){
                    yield Action.BERSERK; //version légèrement différente de la guerrière
                }
                yield Action.AUCUNE;
            }
            case "i" -> {
                if(getParent() == Dieux.APOLLON){
                    yield Action.INFECTION;
                }
                if(getParent() == Dieux.POSEIDON){
                    yield Action.INONDATION;
                }
                yield Action.AUCUNE;
            }
            case "r" -> {
                if(getParent() == Dieux.DEMETER){
                    yield Action.SERENITE;
                }
                yield Action.AUCUNE;
            }
            case "f" -> {
                if(getParent() == Dieux.ZEUX){
                    yield Action.FOUDRE;
                }
                yield Action.AUCUNE;
            }
            default -> Action.AUCUNE;
        };
    }

    /**
     * Extension du switch principal de main.combat, permet de réaliser des actions exclusives aux métiers
     * @param action l'action à réaliser
     * @return s'il faut encore réaliser l'action
     */
    public boolean traite_action(Action action, Monstre ennemi, int bonus_popo) throws IOException {
        return switch (action) {
            case BERSERK -> {
                berserk();
                yield true;
            }
            case INFECTION -> {
                infection();
                yield false;
            }
            case SERENITE -> {
                serenite();
                yield false;
            }
            case INONDATION -> {
                inondation(ennemi, bonus_popo);
                yield false;
            }
            case FOUDRE -> {
                foudre_zeus(ennemi, bonus_popo);
                yield false;
            }
            default -> true;
        };
    }

    /**
     * Extension du switch principal de main.combat, indique si les dommages de potion ont été utilisés
     * @param action l'action réalisée
     * @return s'il faut annuler les dégats des potions (s'ils ont déjà été appliqués).
     */
    public boolean action_consomme_popo(Action action){
        return false;
    }

    /**
     * Extension de la fonction Input.extra, annonce les actions bonus possibles du joueur
     *
     * @return le texte à ajouter
     */
    public String text_extra(Action action) {
        String text = "choississez une action bonus : ";
        if (!est_berserk()) {
            text += "a(n)alyser/";
            if (getMetier() != Metier.ARCHIMAGE || action != Action.SORT) {
                text += "(p)otion/";
            }
        }
        text += "(c)ustom/(a)ucune";
        return text;
    }

    /**
     * Extension de la fonction Input.extra, permet de jouer les actions bonus de métier
     *
     * @param choix le choix fait par le joueur (en lowercase)
     * @return si le tour a été joué
     */
    public Action_extra extra(String choix) {
        return Action_extra.AUCUNE;
    }

    public void jouer_extra(Action_extra extra) {
        if (extra == Action_extra.RAGE) {
            rage();
        }
    }

    /**
     * Traite l'action bonus rage (initialement exclusive à la guerrière)
     */
    public void rage() {
        System.out.println(nom + " s'enrage !");
        berserk += 0.1f + 0.1f * rand.nextInt(5); //0.1 à 0.5
    }

    /**
     * Traite l'action bonus potion
     *
     * @return les dégats additionnel des potions (en négatif si les dommages ne s'appliquent qu'à l'attaque au corps à corps).
     */
    public int popo() throws IOException {
        System.out.println(nom + """
                ,quelle type de potion utilisez vous :
                1 : Soin (PV)
                2 : Résistance (RES)
                3 : Force (ATK)
                4 : Poison (P)
                5 : Explosive (E)
                6 : Aucune/Custom""");
        int temp = Input.readInt();
        if(temp <= 0 || temp > 6){
            System.out.println("Unknow input.");
            return popo();
        }
        return switch (temp) {
            case 1 -> popo_soin();
            case 2 -> popo_res();
            case 3 -> popo_force();
            case 4 -> popo_cd();
            case 5 -> popo_instable();
            case 6 -> 0;
            default -> {
                System.out.println("Unknow input");
                yield popo();
            }
        };
    }

    /**
     * Calcule et traite les soin
     * @return 0
     * @throws IOException toujours
     */
    protected int popo_soin() throws IOException {
        System.out.println("""
                Entrez la potion que vous utilisez :
                1 : potion insipide (PV#1)
                2 : potion de vie   (PV#2)
                3 : potion de santé (PV#3)
                4 : fortifiant      (PV#4)
                5 : aucune (reviens au choix des potions))""");
        int temp = Input.readInt();
        if (temp <= 0 || temp > 6) {
            System.out.println("Unknow input");
            return popo_soin();
        }
        int soin = 0;
        switch (temp) {
            case 1 -> soin = 1;
            case 2 -> soin = 3 + rand.nextInt(2);
            case 3 -> soin = 5 + rand.nextInt(3);
            case 4 -> soin = 7 + rand.nextInt(4);
            case 5 -> {
                return popo();
            }
        }
        System.out.println(nom + " se soigne de " + soin + " grâce à une potion.");
        return 0;
    }

    /**
     * Calcule et traite les bonus de vie
     * @return 0
     * @throws IOException toujours
     */
    protected int popo_res() throws IOException {
        System.out.println("""
                Entrez la potion que vous utilisez :
                1 : potion de vigueur       (RES#1)
                2 : potion de résistance    (RES#2)
                3 : aucune (reviens au choix des potions))""");
        int temp = Input.readInt();
        if (temp <= 0 || temp > 3) {
            System.out.println("Unknow input");
            return popo_res();
        }
        int res = 0;
        switch (temp) {
            case 1 -> res = 3 + rand.nextInt(2);
            case 2 -> res = 4 + rand.nextInt(3);
            case 3 -> {
                return popo();
            }
        }
        System.out.println(nom + " gagne temporairement " + res + " points de résistance grâce à une potion.");
        return 0;
    }

    /**
     * Calcule et traite les bonus d'attaque
     * @return 0
     * @throws IOException toujours
     */
    @SuppressWarnings("DuplicatedCode") //la fonction fille dans Alchimiste.java
    protected int popo_force() throws IOException {
        System.out.println("""
                Entrez la potion que vous utilisez :
                1 : potion de force     (ATK#1)
                2 : potion de puissance (ATK#2)
                3 : potion du colosse   (ATK#3)
                4 : aucune (reviens au choix des potions))""");
        int temp = Input.readInt();
        if (temp <= 0 || temp > 4) {
            System.out.println("Unknow input");
            return popo_force();
        }
        int force = 0;
        switch (temp) {
            case 1 -> force = 2 + rand.nextInt(2);
            case 2 -> force = 3 + rand.nextInt(3);
            case 3 -> force = 4 + rand.nextInt(4);
            case 4 -> {
                return popo();
            }
        }
        System.out.println(nom + " gagne temporairement " + force + " points d'attaque grâce à une potion.");
        return 0;
    }

    /**
     * Calcule et traite les dommage au corps à corps
     * @return le négatif du bonus de dommage
     * @throws IOException toujours
     */
    protected int popo_cd() throws IOException {
        System.out.println("""
                Entrez la potion que vous utilisez :
                1 : potion douteuse     (P#1)
                2 : potion toxique      (P#2)
                3 : potion de poison    (P#3)
                4 : flasque nécrosé     (P#4)
                5 : potion nécrotyque   (P#5)
                6 : aucune (reviens au choix des potions))""");
        int temp = Input.readInt();
        if (temp <= 0 || temp > 6) {
            System.out.println("Unknow input");
            return popo_cd();
        }
        int poison = 0;
        switch (temp){
            case 1 -> poison = rand.nextInt(2) + rand.nextInt(2);
            case 2 -> poison = 1 + rand.nextInt(3);
            case 3 -> poison = 2 + rand.nextInt(4);
            case 4 -> poison = 3 + rand.nextInt(4);
            case 5 -> poison = 4 + rand.nextInt(5);
        }
        System.out.println("Vous enduisez votre lame d'une substance étrange.");
        return -poison;
    }

    /**
     * Calcule et traite les dommages des potions instables
     *
     * @return les dommages infligés
     * @throws IOException toujours
     */
    protected int popo_instable() throws IOException {
        System.out.println("""
                Entrez la potion que vous utilisez :
                1 : potion instable     (E#1)
                2 : potion de feu       (E#2)
                3 : potion explosive    (E#3)
                4 : bombe               (E#4)
                5 : aucune (reviens au choix des potions))""");
        return switch (Input.readInt()) {
            case 1 -> explo_instable();
            case 2 -> explo_feu();
            case 3 -> explo_explo();
            case 4 -> explo_bombe();
            case 5 -> popo();
            default -> {
                System.out.println("Unknow input");
                yield popo_instable();
            }
        };
    }

    private int explo_instable() throws IOException {
        int temp = Input.D4();
        if (temp > 2) {
            System.out.println("Le potion explose en vol et frappe légèrement l'ennemi.");
            return temp - 2;
        }
        System.out.println("La potion se brise par terre sans rien déclencher.");
        return 0;
    }

    private int explo_feu() throws IOException {
        int temp = Input.D4();
        if (temp <= 1) {
            System.out.println("La potion prends feu en touchant l'ennemi.");
            return 2;
        } else if (temp != 4) {
            System.out.println("La potion éclate au contacte de l'ennemi et le brûle.");
            return temp + 2;
        }
        System.out.println("La potion explose en une gerbe de flamme qui frappe violemment l'ennemi.");
        return 7;
    }

    private int explo_explo() throws IOException {
        int temp = Input.D6();
        if (temp <= 1) {
            System.out.println("La potion explose en l'air et et frappe légèrement l'ennemi.");
            return 4;
        } else if (temp != 6) {
            System.out.println("La potion détone au contacte de l'ennemi.");
            return temp + 4;
        }
        System.out.println("La potion herte violemment l'ennemi avant de lui exploser à la face.");
        return 12;
    }

    private int explo_bombe() throws IOException {
        int temp = Input.D8();
        if (temp <= 1) {
            System.out.println("La bombe détonne violemment en plein vol.");
            return 6;
        } else if (temp < 4) {
            System.out.println("La bombe explose devant l'ennemi.");
            return temp + 6;
        } else if (temp < 8) {
            System.out.println("La bombe percute l'ennemi et lui explose dessus.");
            return temp + 8;
        }
        System.out.println("La bombe herte violement l'ennemi avant de lui exploser violement au village.");
        return 18;
    }

    /**
     * Contablise les bonus d'exploration des métiers
     * @return le bonus
     */
    public int bonus_exploration(){
        return 0;
    }

    /**
     * Réalise les actions de fin de tour (en combat)
     */
    public void fin_tour_combat(){
        skip = false;
        f_skip = false;
        if(a_poison1()){
            System.out.println(nom + " souffre d'empoisonnement et subit " + (rand.nextInt(3) + 1) + " dommage(s).");
        }
        if(a_poison2()){
            System.out.println(nom + " souffre d'empoisonnement et subit " + (rand.nextInt(4) + 2) + " dommages.");
        }
        if(f_a_poison1()){
            System.out.println("Le familier de " + nom + " souffre d'empoisonnement et subit " + (rand.nextInt(3) + 1) + " dommage(s).");
        }
        if(f_a_poison2()){
            System.out.println("Le familier de " + nom + " souffre d'empoisonnement et subit " + (rand.nextInt(4) + 2) + " dommages.");
        }
    }

    /**
     * Subit la compétence "Onde ce choc" de l'archimage
     */
    protected void choc(int bonus) throws IOException {
        System.out.println(nom + " est frappé par l'onde de choc.");
        int jet = Input.D6() - rand.nextInt(4) - 2 + bonus;
        if(est_assomme()){
            reveil -= Math.max(1, jet);
        }
        else if (jet < 0) {
            System.out.println(nom + " perd connaissance.");
            assomme();
        } 
        else {
            System.out.println(nom + " parvient à rester conscient.");
        }
        //familier
        if(!a_familier_actif()){
            return;
        }
        jet = Input.D4() - 2 - rand.nextInt(2) + bonus;
        if(f_est_assomme()){
            f_reveil -= Math.max(1, jet);
        }
        else if (jet < 0) {
            System.out.println("Le familier de " + nom + " perd connaissance.");
            f_assomme();
        } else {
            System.out.println("Le familier de " + nom + " parvient à rester conscient.");
        }
    }

    /**
     * Indique si le joueur est capable de jouer, c'est-à-dire de choisir une action et de la réaliser
     * @return true si le joueur peut jouer
     */
    public boolean peut_jouer() {
        return est_actif() && conscient && !skip && vivant;
    }

    /**
     * Indique si le joueur est capable de ressuciter un autre joueur
     * @return un booléan correspondant
     */
    public boolean peut_ressuciter(){
        return false;
    }

    /**
     * Tente de ressuciter un joueur
     * @param malus un malus à appliquer à la tentative
     * @return true si la résurection est un succès, false sinon
     * @throws IOException toujours
     */
    public boolean ressuciter(int malus) throws IOException{
        return false;
    }

    /**
     * Tente de ressuciter tout seul
     * @param malus un malus à appliquer à la tentative
     * @return true si la résurection est un succès, false sinon
     * @throws IOException toujours
     */
    public boolean auto_ressuciter(int malus) throws IOException{
        return false;
    }

    /**
     * Indique si le joueur est capable de diriger son familier, c'est-à-dire de lui donner un ordre
     * @return true si le joueur a un familier et peut lui donner un ordre
     */
    public boolean peut_diriger_familier(){
        return est_actif() && est_vivant() && !est_assomme() && a_familier_actif();
    }

    /**
     * Gère les effets de coup critique à l'arc
     * @param base la puissance de tir originale
     * @return le bonus de dommages
     */
    protected float critique_tir(int base) {
        if(rand.nextInt(50) == 0) { //2%
            return base * 0.1f * (rand.nextInt(5) + 1); //10% à 50% de bonus
        }
        return 0;
    }

    /**
     * Calcule si un joueur succombe à sa folie de berserker ou non
     * @return si le joueur frappe sans savoir qui
     */
    protected boolean folie_berserk() throws IOException {
        return Input.D6() < 2 + berserk;
    }

    /**
     * Augmente l'état de rage meutrière du joueur
     * @param is_crazy si le joueur est déjà dans un état de folie (augmente la déterioration).
     */
    protected void berserk_boost(boolean is_crazy) {
        if(is_crazy){
            berserk += 0.1f + rand.nextInt(5) * 0.1f; //0.1 à 0.5 de boost
        }
        else{
        this.berserk += 0.1f + rand.nextInt(3) * 0.1f; //0.1 à 0.3 de boost
            }
    }

    /**
     * Indique les bonus d'attaque à l'arc
     * @return la quantité de dommages aditionnel
     */
    protected int bonus_tir(){
        return 0;
    }

    /**
     * Gère les effets de coup critique
     * @param base la puissance de frappe originale
     * @return le bonus de dommages
     */
    protected float critique_atk(int base) {
        if(rand.nextInt(50) == 0) { //2%
            return base * 0.1f * (rand.nextInt(5) + 1); //10% à 50% de bonus
        }
        return 0;
    }

    /**
     * Lance le sort berserk : rend le joueur berserk
     */
    protected void berserk(){
        System.out.println(nom + " est prit d'une folie meurtrière !");
        berserk = 0.1f + 0.1f * rand.nextInt(7); //0.1 à 0.7
    }

    /**
     * Lance le sort infection : améliore les attaques de l'arc
     */
    protected void infection(){
        System.out.println("Les flèches de " + nom + " s'emplissent de maladies.");
        bonus_infection += 2;
    }

    /**
     * Lance le sort sérénité : guérie un joueur
     */
    protected void serenite(){
        System.out.println("Ciblez un joueur ou familier.");
        System.out.println("La cible guérie de " + (5 + rand.nextInt(3) + "."));
    }

    /**
     * Lance le sort Inondation : inflige des dommages magiques et affecte
     */
    protected void inondation(Monstre ennemi, int dps_bonus) throws IOException {
        System.out.println("Une vague d'eau percute " + ennemi.getNom() + " de plein fouet.");
        ennemi.dommage_magique(10 + dps_bonus);
        ennemi.affecte();
    }

    /**
     * Lance le sort Foudre : inflige des dommages magiques et affecte
     */
    protected void foudre_zeus(Monstre ennemi, int dps_bonus) throws IOException {
        System.out.println("Un éclair s'abat sur " + ennemi.getNom() + ".");
        ennemi.dommage_magique(15 + dps_bonus);
        ennemi.affecte();
    }

    /**Nombre spécifique indiquant qu'un joueur berserk a tiré/attaqué un allié**/
    static float berserk_atk_alliee = -256;
    /**
     * Gère les effets de la folie du berserk lors d'attaque
     * @param base la puissance de frappe
     * @return le bonus de dommages, ou berserk_atk_alliee si le joueur attaque un allié
     * @throws IOException toujours
     */
    protected float berserk_atk(int base) throws IOException {
        System.out.println("Vous êtes pris(e) de folie mertrière et distinguez mal vos alliés de vos ennemis.");
        if (folie_berserk()) {
            int i;
            do {
                i = rand.nextInt(Main.nbj + 1);
            } while (i < Main.nbj || !Main.joueurs[i].est_actif());
            if (i == Main.nbj) {
                return base * berserk * 0.5f; //50% bonus en moins dû à la folie
            }
            int temp = base;
            temp += Main.corriger(temp * (berserk * 0.5f));
            String cible = Main.joueurs[i].getNom();
            if (a_familier_actif() && rand.nextBoolean()) {
                cible = "le familier de " + cible;
            }
            System.out.println("Pris(e) de folie, " + nom + " attaque " + cible + " et lui inflige " + temp + " dommages !");
            berserk_boost(true);
            return berserk_atk_alliee;
        }
        berserk_boost(false);
        return base * berserk;
    }

    /**
     * Indique les bonus d'attaque à l'arc
     * @return la quantité de dommages aditionnel
     */
    protected int bonus_atk(){
        if(a_cecite()){
            return -1;
        }
        return 0;
    }

    /**
     * Tente de fuir le combat
     * @param ennemi_nomme si l'ennemi est un monstre nommé (complexifie la tache et fournit de l'expérience en cas de
     *                     réussite).
     * @throws IOException ça roule
     */
    public void fuir(boolean ennemi_nomme) throws IOException {
        int bonus = -1 + rand.nextInt(3);
        if(ennemi_nomme){
            bonus -= 1;
        }
        bonus += bonus_fuite();
        bonus += berserk_fuite();
        bonus += position_fuite();
        if (Input.D6() + bonus >= 4) {
            System.out.println(nom + " a fuit le combat.");
            inactiver();
            if(ennemi_nomme){
                gagneXp();
            }
        } else {
            System.out.println(nom + " n'est pas parvenu à fuir le combat.");
        }
    }

    /**
     * Tente de fuir le combat (votre familier)
     * @throws IOException comme d'hab'
     */
    public void f_fuir() throws IOException {
        int bonus = -1 + rand.nextInt(3) + get_ob_f() / 2;
        bonus += berserk_fuite();
        bonus += f_position_fuite();
        if (Input.D6() + bonus >= 4) {
            System.out.println("Le familier de " + nom + " a fuit le combat.");
            f_inactiver();
        } else {
            System.out.println("Le familier de " + nom + " n'est pas parvenu à fuir le combat.");
        }
    }

    /**
     * Indique les bonus de fuite
     * @return le bonus
     */
    protected int bonus_fuite() {
        return 0;
    }

    /**
     * Indique le malus de fuite dû à l'état de berserk
     * @return le malus (négatif)
     * @throws IOException toujours
     */
    protected int berserk_fuite() throws IOException {
        if(!est_berserk()){
            return 0;
        }
        return Math.min(0, Math.round(Input.D4() * 0.5f - berserk));
    }

    /**
     * Indique les bonus de fuite dû à la position (première ligne)
     * @return le malus
     */
    protected int position_fuite() {
        if(est_front()){
            if(est_front_f()){
                return -2;
            }
            return -3;
        }
        return 3;
    }

    /**
     * Indique les bonus de fuite dû à la position (première ligne) du familier
     * @return le malus
     */
    protected int f_position_fuite(){
        if(est_front()){
            if(est_front_f()){
                return -3;
            }
            return -2;
        }
        return 2;
    }

    /**
     * Le familier protège son maître
     */
    public void f_proteger(Monstre ennemi) throws IOException {
        switch (Input.D6() + get_ob_f() / 3) {
            case 1, 2:
                ennemi.bostEncaissement(0.1F);
                System.out.println("Votre familier vous protège maladroitement.");
                break;
            case 3, 4:
                ennemi.bostEncaissement(0.3F);
                System.out.println("Votre familier vous protège.");
                break;
            case 5, 6:
                ennemi.bostEncaissement(0.5F);
                System.out.println("Votre familier vous protège.");
                break;
            case 7, 8, 9, 10:
                ennemi.bostEncaissement(0.7F);
                System.out.println("Votre familier concentre chaque fibre de son être à se préparer à vous proteger.");
                break;
            default:
                System.out.println("Le résultat n'a pas été comprit, compétence ignorée.");
        }
    }

    /**
     * Propose les actions métiers sur un cadavre
     */
    protected void monstre_mort_perso(Monstre ennemi) throws IOException{
    }
}

package Metiers;

import Auxiliaire.Texte;
import Enum.*;
import Equipement.Equipement;
import Exterieur.Input;
import Exterieur.Output;
import Monstre.Lieu;
import Monstre.Monstre;
import main.Combat;
import main.Main;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.util.*;

public abstract class Joueur {
    static final int f_max = 7;
    protected final String nom;
    private Position position;
    protected int ob_f;
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
    private String caracteristique;
    private String competences;
    
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
    protected boolean peut_joindre;
    protected boolean skip_joindre;
    
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
    protected boolean f_skip_joindre;
    
    //modificateur
    protected static int attaque_bonus = 0;
    protected static int tir_bonus = 0;
    protected static int tour_modif = 0;
    protected int bonus_infection;
    
    //item
    private boolean lame_infernale;
    private boolean lame_vegetale;
    private boolean trident;
    private boolean lame_mont;
    private boolean nectar;
    private boolean ambroisie;
    private int guerre;
    private boolean lame_vent;
    private boolean first_attaque;
    private boolean lame_fertile;
    private boolean parch_feu;
    private boolean parch_dodo;
    private boolean parch_lumiere;
    private boolean a_aveugle;
    private boolean rune_croissance;
    private boolean rune_pluie;
    protected boolean rune_haine;
    private boolean rune_virale;
    private boolean rune_dodo;
    protected boolean rune_mortifere;
    private boolean rune_orage;
    private boolean rune_commerce;
    private int rune_ardente;
    private int rune_ardente2;
    private boolean soin;
    private boolean a_soigne;
    private boolean bracelet_protect;
    protected boolean rune_noire;
    private boolean absorption;
    private boolean lunette;
    protected boolean dissec;
    protected boolean concoct;
    protected boolean bourdon;
    private boolean parch_volcan;
    private boolean absorption2;
    private boolean pegase;
    private boolean cheval;
    private boolean pie;
    private boolean sphinx;
    private boolean a_renforce;
    private boolean fee;
    private boolean a_soigne_fee;
    protected boolean rune_arca;
    protected boolean antidote;
    protected boolean rune_annihilation;
    private boolean tatouage_resurection;
    private boolean fuite;
    private int grenade;
    private boolean bateau;
    
    Joueur(String nom, Position position, int ob_f, Dieux parent, int xp) {
        this.nom = nom;
        this.position = position;
        this.ob_f = ob_f;
        this.parent = parent;
        this.caracteristique = "";
        this.competences = "";
        this.armure = 0;
        setNiveau(xp);
        super_actualiser_niveau();
        SetEffetParent();
        retirer_tout(true);
    }
    
    //************************************************CHARGEMENT******************************************************//
    
    private static int safeGetInt(JsonObject json, String key) {
        try {
            return json.getInt(key, 0);
        } catch (Exception e) {
            System.err.println("⚠️ Champ " + key + " invalide, valeur par défaut appliquée : " + 0);
            return 0;
        }
    }
    
    private static <E extends Enum<E>> E safeGetEnum(JsonObject json, String key, Class<E> enumClass, E def) {
        try {
            String value = json.getString(key, def.name());
            return Enum.valueOf(enumClass, value);
        } catch (Exception e) {
            System.err.println("⚠️ Champ " + key + " invalide, valeur par défaut appliquée : " + def);
            return def;
        }
    }
    
    /**
     * Crée un joueur à partir de sa sauvegarde JSON.
     * @param chemin le chemin du fichier de sauvegarde
     * @return le joueur reconstruit
     * @throws FileNotFoundException si le fichier n'existe pas
     */
    public static Joueur chargerJoueur(String chemin) throws FileNotFoundException {
        File file = new File(chemin);
        if (!file.exists()) {
            throw new FileNotFoundException("Fichier de sauvegarde introuvable : " + chemin);
        }
        
        try (JsonReader reader = Json.createReader(new FileReader(file))) {
            JsonObject json = reader.readObject();
            
            String nom = json.getString("nom", "SansNom");
            Metier metier = safeGetEnum(json, "metier", Metier.class, Metier.TRYHARDER);
            int ob_f = safeGetInt(json, "ob_f");
            int xp = safeGetInt(json, "xp");
            Position position = safeGetEnum(json, "position", Position.class, Position.DEFAULT);
            Dieux parent = safeGetEnum(json, "parent", Dieux.class, Main.get_parent());
            
            Joueur joueur = CreerJoueur(nom, position, metier, ob_f, parent, xp);
            
            // Effets
            if (json.containsKey("effets")) {
                try {
                    joueur.load_effet_structure(json.getJsonArray("effets"));
                } catch (Exception e) {
                    System.err.println("⚠️ Impossible de charger les effets : " + e.getMessage());
                }
            }
            
            return joueur;
        } catch (IOException e) {
            throw new FileNotFoundException("Erreur de lecture du fichier : " + chemin + " (" + e.getMessage() + ")");
        }
    }
    
    
    /**
     * Crée un joueur en fonction de ses données
     * @param nom      le nom du joueur à créer
     * @param position la position du joueur à créer
     * @param metier   la classe du joueur à créer
     * @param ob_f     l'obéissance du joueur à créer (0 s'il n'en a pas).
     * @param parent   l'ancêtre divin du joueur à créer
     * @param xp       le total d'experience accumulée par le joueur à créer
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
     * @implNote : Ne dois pas être appellé, sauf par le super
     */
    protected abstract void actualiser_niveau();
    
    private void super_actualiser_niveau() {
        if (this.niveau >= 6) {
            this.attaque += 1;
        }
        actualiser_niveau();
    }
    
    //************************************************SAUVEGARDE******************************************************//
    
    public void sauvegarder(String chemin, boolean discret) throws IOException {
        JsonObject joueurJson = Json.createObjectBuilder().add("nom", this.nom).add("metier",
                this.getMetier().name()).add("ob_f", this.ob_f).add("position", this.position.name()).add("xp",
                this.GetXpTotal()).add("parent", this.parent.name()).add("effets", save_effet_structure()).build();
        
        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory writerFactory = Json.createWriterFactory(config);
        
        try (Writer fileWriter = new FileWriter(chemin); JsonWriter writer = writerFactory.createWriter(fileWriter)) {
            writer.writeObject(joueurJson);
        }
        
        if(!discret){
            System.out.printf("Joueur mis à jour : %s\n", this.nom);
        }
    }
    
    
    static Random rand = new Random();
    
    //************************************************PRESENTATION****************************************************//
    
    /**
     * Présente les caractéristiques et statistiques du joueur
     */
    public void presente_detail() {
        System.out.println(nom + " descendant de " + getParent() + ", " + nomMetier() + " niveau " + this.niveau);
        System.out.print("Base : Résistance : " + this.vie);
        int temp = bonusResLieux();
        if (temp > 0) {
            System.out.print("(+" + temp + ")");
        }
        System.out.print(" ; attaque : " + this.attaque);
        temp = bonusAtkLieux();
        if (temp > 0) {
            System.out.print("(+" + temp + ")");
        }
        System.out.print(" ; armure : " + this.armure);
        temp = bonusArmLieux();
        if (temp > 0) {
            System.out.print("(+" + temp + ")");
        }
        System.out.println(" ; " + this.PP + " : " + this.PP_value + "/" + this.PP_max);
        System.out.println("Caractéristiques : " + this.caracteristique);
        System.out.println("Pouvoir : " + this.competences);
        System.out.println();
        presente_caracteristique();
        System.out.println();
        presente_pouvoir();
        System.out.println();
        System.out.println(DescribeEffetParent());
        System.out.println(Describe_effet_item());
    }
    
    /**
     * Présente la condition et position du joueur
     */
    public void presente() {
        System.out.print(this.nom + ", descendant de " + nomDieux() + ", est " + nomMetier() + " (niveau " + this.niveau + ")" + " et se trouve " + Main.texte_pos(getPosition()));
        if (a_familier()) {
            System.out.print(" avec son familier");
        }
        System.out.println(".");
    }
    
    abstract void presente_caracteristique();
    
    abstract void presente_pouvoir();
    
    //************************************************GETTER**********************************************************//
    
    public int getXp() {
        return xp;
    }
    
    public Dieux getParent() {
        return parent;
    }
    
    abstract public Metier getMetier();
    
    abstract protected String nomMetier();
    
    protected String nomDieux() {
        return switch (parent) {
            case ZEUS -> "Zeus";
            case ARES -> "Ares";
            case HADES -> "Hades";
            case APOLLON -> "Apollon";
            case DEMETER -> "Demeter";
            case DIONYSOS -> "Dionysos";
            case POSEIDON -> "Poseidon";
        };
    }
    
    /**
     * Applique les caractéristiques et capacités héréditaires
     */
    protected void SetEffetParent() {
        switch (parent) {
            case ARES -> {
                if (getMetier() != Metier.GUERRIERE) {
                    add_competence("Berserk");
                }
            }
            case HADES -> {
                if (getMetier() != Metier.NECROMANCIEN) {
                    add_caracteristique("Thaumaturge");
                }
            }
            case APOLLON -> add_competence("Infection");
            case DEMETER -> add_competence("Sérénité");
            case DIONYSOS -> add_caracteristique("Sens des affaires");
            case POSEIDON -> add_competence("Inondation");
            case ZEUS -> add_competence("Foudre");
        }
    }
    
    /**
     * Ajoute proprement la caractéristique à celles existantes
     * @param new_caracteristique la caractéristique à ajouter
     */
    protected void add_caracteristique(String new_caracteristique) {
        if (this.caracteristique.isEmpty()) {
            this.caracteristique = new_caracteristique;
        } else {
            this.caracteristique += ", " + new_caracteristique;
        }
    }
    
    /**
     * Ajoute proprement la competence à celles existantes
     * @param new_competence la competence à ajouter
     */
    protected void add_competence(String new_competence) {
        if (this.competences.isEmpty()) {
            this.competences = new_competence;
        } else {
            this.competences += ", " + new_competence;
        }
    }
    
    /**
     * Présente les caractéristiques et capacités héréditaires
     */
    private String DescribeEffetParent() {
        String temp = switch (parent) {
            case ARES -> {
                if (getMetier() != Metier.GUERRIERE) {
                    yield "Berserk : pour 1PP, imprègne de folie meurtrière l'esprit du lanceur avant qu'il " + "ne " +
                     "frappe, augmentant sa puissance au prix de sa santé mentale.";
                }
                yield "";
            }
            case HADES -> {
                if (getMetier() != Metier.NECROMANCIEN) {
                    yield "Thaumaturge : Quand il meurt, un thaumaturge peut emporter avec lui une partie de ses " +
                            "possession dans l'au-delà.";
                }
                yield "";
            }
            case APOLLON -> "Infection : pour 1PP, augmente les dommages de vos tirs pour un combat.";
            case DEMETER -> "Sérénité : pour %dPP, soigne une cible.".formatted(rune_croissance ? 1 : 2);
            case DIONYSOS -> "Sens des affaires : diminue les prix des objets en vente aux marchés.";
            case POSEIDON ->
                    "Inondation : Un sort qui consomme %dPP et inflige de gros dommages magiques.".formatted(rune_pluie ? 3 : 4);
            case ZEUS ->
                    "Foudre : Un sort qui consomme %dPP et inflige de puissants dommages magiques.".formatted(rune_orage ? 4 : 5);
        };
        if (!temp.isEmpty()) {
            temp += "\n";
        }
        return temp;
    }
    
    /**
     * Présente les caractéristiques et capacités obtenues via item
     */
    private String Describe_effet_item() {
        String text = "";
        if (lame_vent) {
            text += "Lame des vents : Déchaine des violentes lame de vent lors de certains assaut.\n";
        }
        if (parch_feu && getMetier() != Metier.ARCHIMAGE) {
            text += "Parchemin de feu : Permet de lancer un sort de feu mineur pour %d mana.\n".formatted(2 + rune_ardente2);
        }
        if (parch_dodo && getMetier() != Metier.ARCHIMAGE) {
            text += "Parchemin de sommeil : Permet de lancer un sort de sommeil pour %d mana.\n".formatted(rune_dodo
            ? 1 : 2);
        }
        if (parch_lumiere && getMetier() != Metier.ARCHIMAGE) {
            text += "Parchemin de lumière : permet de lancer un sort aveuglant pour 2 mana.\n";
        }
        if (parch_volcan && getMetier() != Metier.ARCHIMAGE) {
            text += "Parchemin volcanique : permet de lancer le sort  éruption volcanique pour 6 mana.\n";
        }
        if (soin) {
            text += "Bracelet de soin : permet, une fois par bataille, de soigner une cible.\n";
        }
        if (sphinx) {
            text += "Sphinx : permet, une fois par combat, de renforcer une cible.\n";
        }
        if (fee) {
            text += "Fee : permet, une fois par bataille, de soigner une cible.\n";
        }
        if (rune_annihilation) {
            text += "Rune d'annihilation : consume une rune pour lancer un sort surpuissant. (Peut se consommer " +
             "lui-même).\n";
        }
        if (fuite) {
            text += "Téléporteur courte porté : Permet de fuir d'un combat, peut se détruire à l'usage.\n";
        }
        if (grenade > 0) {
            text += "Grenades : inflige des dommages à l'adversaire. Vous possedez actuellement %d grenades.\n".formatted(grenade);
        }
        if (bateau) {
            text += "Navire magique : Vous ne pouvez pas être attaqué par surprise en mer.\n";
        }
        return text;
    }
    
    /**
     * Convertie l'expérience en niveau et expérience
     * Remplit les champs nécessaires
     * @param experience l'expérience totale du joueur
     */
    private void setNiveau(int experience) {
        int niveau = 0;
        while (experience >= 5 * (niveau + 1)) {
            niveau++;
            experience -= niveau * 5;
        }
        this.niveau = niveau;
        this.xp = experience;
    }
    
    /**
     * Renvoie un bonus x via la formule y = ax + b (y étant le niveau).
     * @param min    le niveau minimum pour obtenir le bonus (b).
     * @param palier le nombre de niveaux séparant chaque obtention de bonus (a).
     * @return le bonus (x) (positif, arrondit à l'inférieur)
     */
    protected int bonus_sup10(int min, int palier) {
        int bonus = 0;
        int temp = this.niveau;
        while (temp >= min) {
            bonus += 1;
            temp -= palier;
        }
        return bonus;
    }
    
    /**
     * Convertie les niveaux et l'expérience du joueur en valeur d'xp uniquement
     * @return l'ensemble de l'xp que le joueur possède
     */
    private int GetXpTotal() {
        int xp_totale = this.xp;
        if (xp_totale < 0) {
            xp_totale = 0;
        }
        for (int nv = this.niveau; nv > 0; nv--) {
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
        if (f_skip_joindre) {
            f_skip_joindre = false;
            return true;
        }
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
        if (skip_joindre) {
            System.out.println(nom + " débarque en plein combat !");
            skip_joindre = false;
            f_skip_joindre = false;
            return true;
        }
        return !est_actif() || !est_vivant();
    }
    
    public float getBerserk() {
        return berserk;
    }
    
    public boolean a_cecite() {
        return !lunette && !antidote && cecite;
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
    
    protected void p_prend_cecite() {
        if (cecite) {
            return;
        }
        cecite = true;
        System.out.println(nom + " est empoisonné(e) et atteint(e) de cécité.");
        if (lunette || antidote) {
            System.out.println("Celui ne l'affecte en rien.");
        }
    }
    
    protected void f_prend_cecite() {
        if (f_a_cecite()) {
            return;
        }
        f_cecite = true;
        System.out.println("Le familier de " + nom + " est empoisonné et atteint de cécité.");
    }
    
    private boolean a_poison1() {
        return poison1 && !antidote;
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
        if (poison1) {
            return;
        }
        poison1 = true;
        System.out.println(nom + " est empoisonné(e).");
        if (antidote) {
            System.out.println("Celui ne l'affecte en rien.");
        }
    }
    
    private void f_prend_poison1() {
        if (f_poison1) {
            return;
        }
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
        if (poison2) {
            return;
        }
        poison2 = true;
        System.out.println(nom + " est empoisonné(e).");
        if (antidote) {
            System.out.println("Celui ne l'affecte en rien.");
        }
    }
    
    private void f_prend_poison2() {
        if (f_poison2) {
            return;
        }
        f_poison2 = true;
        System.out.println("Le familier de " + nom + " est empoisonné.");
    }
    
    private boolean a_poison2() {
        return !antidote && poison2;
    }
    
    private boolean f_a_poison2() {
        return f_poison2;
    }
    
    protected int bonusResLieux() {
        return switch (position) {
            case ASCENDANT, OLYMPE -> 0;
            case ENFERS -> parent == Dieux.HADES ? 2 : 0;
            case PRAIRIE -> parent == Dieux.DEMETER ? 2 : 0;
            case VIGNES -> parent == Dieux.DIONYSOS ? 4 : 0;
            case TEMPLE -> parent == Dieux.APOLLON ? 4 : 0;
            case MER -> parent == Dieux.POSEIDON ? 5 : 0;
            case MONTS -> parent == Dieux.ZEUS ? 5 : 0;
        };
    }
    
    protected int bonusAtkLieux() {
        int bonus = 0;
        // bonus de sang
        if (this.parent == Dieux.ARES) {
            bonus += guerre;
        }
        if (this.parent == Dieux.DIONYSOS || this.parent == Dieux.DEMETER) {
            if (this.lame_fertile) {
                bonus += 5;
            }
        }
        //véritable bonus de lieu
        switch (position) {
            case ASCENDANT -> {
            }
            case ENFERS -> {
                if (lame_infernale) {
                    bonus += 4;
                }
                if (parent == Dieux.HADES) {
                    bonus += 1;
                }
            }
            case PRAIRIE -> {
                if (lame_vegetale) {
                    bonus += 3;
                }
                if (parent == Dieux.DEMETER) {
                    bonus += 2;
                } else if (parent == Dieux.ARES) {
                    bonus += 1;
                }
            }
            case VIGNES -> {
                if (parent == Dieux.DIONYSOS) {
                    bonus += 1;
                } else if (parent == Dieux.ARES) {
                    bonus += 1;
                }
            }
            case TEMPLE -> {
                if (parent == Dieux.APOLLON || parent == Dieux.ARES) {
                    bonus += 2;
                }
            }
            case MER -> {
                if (trident) {
                    bonus += 3;
                }
                if (parent == Dieux.POSEIDON) {
                    bonus += 2;
                } else if (parent == Dieux.ARES) {
                    bonus += 3;
                }
            }
            case MONTS -> {
                if (lame_mont) {
                    bonus += 5;
                }
                if (parent == Dieux.ZEUS) {
                    bonus += 5;
                } else if (parent == Dieux.ARES) {
                    bonus += 3;
                }
            }
            case OLYMPE -> {
                if (parent == Dieux.ARES) {
                    bonus += 4;
                }
            }
        }
        return bonus;
    }
    
    protected int bonusArmLieux() {
        if ((position == Position.MONTS && parent == Dieux.ZEUS) || (position == Position.MER && parent == Dieux.POSEIDON)) {
            return 1;
        }
        return 0;
    }
    
    //************************************************METHODE INDEPENDENTE********************************************//
    
    /**
     * Donne de l'expérience au joueur de 1.
     * @implNote Ce qui augmente l'xp : avoir affronté un monstre (victoire ou fuite) (*2 si nommé), porter le
     * dernier coup,
     * ressusciter un allié,
     */
    public void gagneXp() {
        this.xp += 1;
        if (this.xp >= (this.niveau + 1) * 5) {
            this.niveau += 1;
            this.xp -= this.niveau * 5;
            System.out.println(nom + " a gagné un niveau !");
            Output.JouerSonLvlUp();
            super_lvl_up();
        }
    }
    
    /**
     * Traite le fait qu'un joueur a porté le dernier coup sur un monstre
     */
    public void dernier_coup() {
        gagneXp();
        if (absorption) {
            Texte.absorber(this.nom, 1 + rand.nextInt(2));
        }
        if (absorption2) {
            Texte.absorber2(this.nom, 5 + rand.nextInt(3), 2 + rand.nextInt(3), rand.nextInt(3), 3 + rand.nextInt(3));
        }
    }
    
    /**
     * Méthode permettant aux classes filles d'ajouter de l'expérience
     */
    protected void gagneXpLocal() {
        this.xp += 1;
    }
    
    /**
     * Méthode permettant aux classes filles d'analyser la quantité d'expérience
     */
    protected int getXplocal() {
        return this.xp;
    }
    
    /**
     * Méthode permettant de remettre à 0 l'expérience d'un joueur
     */
    protected void resetXpLocal() {
        this.xp = 0;
    }
    
    /**
     * Ajoute les données dû à la montée de niveau
     * @implNote ne dois pas être appellé, sauf par le super
     */
    abstract void lvl_up();
    
    /**
     * Ajoute les données dû à la montée de niveau
     */
    protected void super_lvl_up() {
        System.out.print(switch (this.niveau) {
            case 4 -> "Votre vitesse de fuite a augmenté.\n";
            case 6 -> {
                this.vie += 1;
                yield "Votre résistance a augmenté.\n";
            }
            case 7 -> "Vos compétence de domptage ont augmenté.\n";
            case 9 -> "Votre précision a augmenté.\n";
            default -> "";
        });
        lvl_up();
    }
    
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
        // bijection aléatoire
        int[] t = new int[Main.nbj];
        int fusible = 0;
        Arrays.fill(t, -1);
        for (int i = 0; i < Main.nbj; ) {
            int temp = rand.nextInt(Main.nbj);
            if (t[temp] == -1) {
                t[temp] = i;
                i++;
            }
            fusible++;
            if(fusible > 10_000){
                throw new RuntimeException("Erreur , boucle infinie suspectée.");
            }
        }
        for (int i = 0; i < Main.nbj; i++) {
            Main.joueurs[t[i]].monstre_mort_perso(ennemi);
            if (!ennemi.corps_utilisable()) {
                System.out.println("Le cadavre du monstre n'est plus utilisable.");
                return;
            }
        }
    }
    
    /**
     * Met le joueur en condition de début de combat (avec le choix de participer)
     * @param force si le joueur est forcé de se battre
     * @param pos   la position du combat
     * @throws IOException toujours
     */
    public void init_affrontement(boolean force, Position pos) throws IOException {
        if (!force && (pos != this.position || !Input.yn("Est-ce que " + nom + " part au combat ?"))) {
            actif = false;
            f_actif = false;
            peut_joindre = pos == this.position;
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
        peut_joindre = false;
        first_attaque = true;
        a_aveugle = false;
        a_soigne = false;
        a_renforce = false;
        a_soigne_fee = false;
        if (a_familier() && Input.yn("Est-ce que votre familier vous rejoint au combat ?")) {
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
     * Permet aux joueurs qui en sont absents de rejoindre un combat
     * @param pos le lieu du combat
     * @throws IOException toujours
     */
    static public void joindre(Position pos) throws IOException {
        for (Joueur j : Main.joueurs) {
            if (j.peut_joindre) {
                j.init_affrontement(false, pos);
                if (j.est_actif()) {
                    j.skip_joindre = true;
                    j.f_skip_joindre = true;
                }
            }
        }
    }
    
    /**
     * Demande au joueur d'aller en première ligne et gère les résultats
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
     * Simule l'action d'un familier auquel on ne donne pas d'ordre
     * @param ennemi le monstre ennemi
     * @throws IOException toujours
     */
    public void familier_seul(Monstre ennemi) throws IOException {
        if (!a_familier_actif() || ob_f <= 3 || f_est_assomme()) {
            return;
        }
        System.out.println("Le familier attaque l'ennemi pour protéger " + nom + ".");
        f_attaque(ennemi);
    }
    
    public void inactiver() {
        actif = false;
        f_actif = false;
        peut_joindre = false;
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
            if (auto_ressusciter(0)) {
                System.out.println(getNom() + " résiste à la mort.");
            } else {
                mort_def();
                actif = false; //pour pas trigger la prochaine condition
            }
        }
        if (est_actif()) {
            gagneXp();
            if (ennemi_nomme) {
                gagneXp();
            }
            if (pie && rand.nextInt(3) == 0) { //33%
                Texte.pie(1 + rand.nextInt(3), getNom()); //1~3
            }
        }
        actif = false;
        f_actif = false;
    }
    
    /**
     * Essaie de se réveiller (assommé)
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
     * @param ennemi     le monstre ennemi
     * @param bonus_popo les dommages additionnels des popo (ici uniquement les instables)
     * @throws IOException toujours
     */
    public void tirer(Monstre ennemi, int bonus_popo) throws IOException {
        int base = Input.tir();
        base += bonus_tir();
        float bonus = 0;
        if (est_berserk()) {
            bonus = berserk_atk(base);
            if (bonus == berserk_atk_alliee) {
                return;
            }
        }
        bonus += critique_tir(base);
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
        base += bonus_atk();
        float bonus = calcule_bonus_atk(base, bonus_popo);
        if (bonus != berserk_atk_alliee) {
            ennemi.dommage(base + Main.corriger(bonus, 0));
        }
    }
    
    /**
     * Calcule les dommages bonus d'une attaque classique
     * @param base       les dommages de base de l'attaque
     * @param bonus_popo les dommages des consommables
     * @return la valeur des dommages bonus (arrondit).
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
        if (bracelet_protect) {
            bracelet_protect = false;
            Texte.bracelet_protect(this);
            return;
        }
        Output.JouerSonMort();
        vivant = false;
        reveil = 0;
        conscient = true;
        berserk = 0;
    }
    
    /**
     * Met à jour les données d'un joueur mort hors combat
     */
    public void mort_def() {
        if (tatouage_resurection) {
            tatouage_resurection = false;
            Texte.resurection_tatouage();
            return;
        }
        System.out.println(nom + " est mort.");
        Output.JouerSonMortDef();
        ob_f = 0;
        position = Position.ENFERS;
        retirer_tout(false);
        if (this.parent == Dieux.HADES && getMetier() != Metier.NECROMANCIEN) {
            int PO = rune_mortifere ? 13 : 6;
            int PIT = rune_mortifere ? 6 : 3;
            Texte.thaumaturge(PIT, PO);
            if (rune_mortifere || this.niveau <= 0) {
                return;
            }
            for (int i = this.niveau; i > 0; i--) {
                if (rand.nextInt(3) == 0) {
                    this.xp -= 1;
                }
            }
        } else {
            if (rune_mortifere || this.niveau <= 0) {
                return;
            }
            for (int i = this.niveau; i > 0; i--) {
                if (rand.nextBoolean()) {
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
                if (f_a_cecite()) {
                    temp -= 1;
                }
                System.out.println("Pris(e) de folie, le familier de " + nom + " attaque " + Main.joueurs[l].getNom() + " et lui inflige " + temp + " dommages !");
            } else {
                ennemi.dommage(Input.atk(), f_berserk + 1);
            }
            f_berserk += rand.nextInt(3) * 0.1f;
            return;
        }
        //attaque classique
        int temp = Input.atk();
        if (f_a_cecite()) {
            temp -= 1;
        }
        if (rand.nextInt(255) == 0) {
            ennemi.dommage(temp, 1.1f + 0.1f * rand.nextInt(5));
            return;
        }
        ennemi.dommage(temp);
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
                yield Position.DEFAULT;
            }
            default -> { //ENFERS, PRAIRIES, OLYMPE
                System.out.println("Erreur : position " + position + " ne peut être descendue !");
                yield position;
            }
        };
    }
    
    public void ascend() throws IOException {
        Position pos = getPosition();
        String text;
        Monstre m;
        boolean attaquant;
        int jet = Input.D6() + bonus_exploration();
        if (jet >= 6) {
            if (rand.nextBoolean()) {
                jet = 6;
            } else {
                jet = 3;
            }
        } else if (jet < 1) {
            jet = 1;
        }
        switch (jet) {
            case 1, 3, 5 -> {
                text =
                "Un monstre se trouve sur le chemin de %s, il ne semble pas encore l'avoir aperçu...".formatted(this.nom);
                m = Lieu.true_monstre(pos, rand.nextBoolean());
                attaquant = true;
            }
            case 2, 4 -> {
                m = Lieu.true_monstre(pos, rand.nextBoolean());
                text = "%s est attaqué par un %s !".formatted(this.nom, m.getNom());
                attaquant = false;
            }
            case 6 -> {
                monter();
                System.out.println(nom + " parvient sans encombre " + Main.texte_pos(position) + ".");
                check_bonus_lieux();
                return;
            }
            default -> throw new IllegalArgumentException("Argument inconnu.");
        }
        System.out.println(text);
        if (Combat.ascension(m, this, attaquant)) {
            monter();
            System.out.printf("%s arrive %s.\n", this.nom, Main.texte_pos(position));
            check_bonus_lieux();
        }
    }
    
    public void set_grimpeur(){
        this.position = Position.ASCENDANT;
    }
    
    public void setPosition(Position pos) {
        this.position = pos;
    }
    
    /**
     * Avertie si le joueur à des bonus de lieu
     */
    public void check_bonus_lieux() {
        if (bonusAtkLieux() > 0 || bonusResLieux() > 0 || bonusArmLieux() > 0) {
            Texte.bonus_lieu();
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
                yield Position.DEFAULT;
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
        return switch (Input.D6() + bonus_dresser()) {
            case 1 -> {
                if (Input.D4() <= 2) {
                    System.out.println("Votre familier désapprouve fortement vos méthodes d'entrainement.\n");
                    yield -1;
                }
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
     * @throws IOException toujours
     */
    public void ajouter_familier() throws IOException {
        ajouter_familier(1);
    }
    
    /**
     * Traite l'ajout d'un nouveau familier
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
        if (getParent() == Dieux.DIONYSOS) {
            reduc = 2;
            if (rune_commerce) {
                reduc += 3;
            }
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
     * @return le texte à ajouter
     */
    public String text_tour() {
        return "";
    }
    
    /**
     * Extension de la fonction Input.tour, permet de jouer les choix de métier
     * @param choix le choix fait par le joueur
     * @return si le tour a été joué
     */
    public boolean tour(String choix) throws IOException {
        return false;
    }
    
    /**
     * Extension de la fonction Input.action, annonce les actions possibles du joueur
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
            // sort héréditaire
            if (getParent() == Dieux.ARES && getMetier() != Metier.GUERRIERE) {
                text += "/(b)erserker";
            }
            if (getParent() == Dieux.APOLLON) {
                text += "/(i)nfection";
            }
            if (getParent() == Dieux.DEMETER) {
                text += "/sé(r)enité";
            }
            if (getParent() == Dieux.POSEIDON) {
                text += "/(i)nondation";
            }
            if (getParent() == Dieux.ZEUS) {
                text += "/(f)oudre";
            }
        }
        //item
        if (!est_berserk()) {
            //parchemin
            if (getMetier() != Metier.ARCHIMAGE) {
                if (parch_feu) {
                    text += "/sort de (feu) mineur";
                }
                if (parch_dodo) {
                    text += "/sort de (som)meil";
                }
                if (parch_lumiere && !a_aveugle) {
                    text += "/sort de (lum)iere";
                }
                if (parch_volcan) {
                    text += "/sort (vol)canique";
                }
            }
            if (rune_annihilation && !est_berserk()) {
                text += "/(ann)ihiler";
            }
        }
        if (fuite) {
            text += "/se (tél)éporter";
        }
        text += "/(f)uir/(c)ustom/(o)ff";
        return text;
    }
    
    /**
     * Extension de la fonction Input.action, annonce les actions possibles du familier
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
     * @param choix        le choix fait par le joueur (en lowercase)
     * @param est_familier s'il s'agit d'un familier ou d'un joueur
     * @return l'action à jouer, ou AUCUNE si le choix n'est pas reconnu
     */
    public Action action(String choix, boolean est_familier) throws IOException {
        if (est_familier) {
            return Action.AUCUNE;
        }
        return switch (choix) {
            // action héréditaire
            case "b" -> {
                if (!est_berserk() && getParent() == Dieux.ARES && getMetier() != Metier.GUERRIERE) {
                    yield Action.BERSERK; //version légèrement différente de la guerrière
                }
                yield Action.AUCUNE;
            }
            case "i" -> {
                if (getParent() == Dieux.APOLLON) {
                    yield Action.INFECTION;
                }
                if (getParent() == Dieux.POSEIDON) {
                    yield Action.INONDATION;
                }
                yield Action.AUCUNE;
            }
            case "r" -> {
                if (getParent() == Dieux.DEMETER) {
                    yield Action.SERENITE;
                }
                yield Action.AUCUNE;
            }
            case "f" -> {
                if (getParent() == Dieux.ZEUS) {
                    yield Action.FOUDRE;
                }
                yield Action.AUCUNE;
            }
            //item
            case "feu" -> {
                if (parch_feu && !est_berserk() && getMetier() != Metier.ARCHIMAGE) {
                    yield Action.SORT_FEU;
                }
                yield Action.AUCUNE;
            }
            case "som" -> {
                if (parch_dodo && !est_berserk() && getMetier() != Metier.ARCHIMAGE) {
                    yield Action.SORT_DODO;
                }
                yield Action.AUCUNE;
            }
            case "lum" -> {
                if (parch_lumiere && !est_berserk() && getMetier() != Metier.ARCHIMAGE && !a_aveugle) {
                    yield Action.SORT_LUMIERE;
                }
                yield Action.AUCUNE;
            }
            case "vol" -> {
                if (parch_volcan && !est_berserk() && getMetier() != Metier.ARCHIMAGE) {
                    yield Action.SORT_VOLCAN;
                }
                yield Action.AUCUNE;
            }
            case "ann" -> {
                if (rune_annihilation && !est_berserk()) {
                    yield Action.ANNIHILATION;
                }
                yield Action.AUCUNE;
            }
            case "tél", "tel" -> {
                if (fuite) {
                    yield Action.TP;
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
            //action héréditaire
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
            //item
            case SORT_FEU -> {
                boule_feu_mineure(ennemi, bonus_popo);
                yield false;
            }
            case SORT_DODO -> {
                Combat.stop_run();
                Texte.sort_dodo();
                yield false;
            }
            case SORT_LUMIERE -> {
                sort_lumiere(ennemi);
                yield false;
            }
            case SORT_VOLCAN -> {
                sort_volcan(ennemi, bonus_popo);
                yield false;
            }
            case ANNIHILATION -> {
                annihilation(ennemi, bonus_popo);
                yield false;
            }
            case TP -> {
                fuite_tp(ennemi);
                yield false;
            }
            default -> true;
        };
    }
    
    /**
     * Extension du switch principal de main.combat, indique si les dommages de potion ont été utilisés
     * @param action l'action réalisée
     * @return s'il faut annuler les dégas des potions (s'ils ont déjà été appliqués).
     */
    public boolean action_consomme_popo(Action action) {
        Set<Action> actionsConsommant = EnumSet.of(Action.SORT_FEU, Action.SORT_VOLCAN, Action.SORT_DODO,
                Action.FOUDRE, Action.INONDATION, Action.ANNIHILATION);
        return actionsConsommant.contains(action);
    }
    
    /**
     * Extension de la fonction Input.extra, annonce les actions bonus possibles du joueur
     * @return le texte à ajouter
     */
    public String text_extra(Action action) {
        String text = "choisissez une action bonus : ";
        if (!est_berserk()) {
            text += "a(n)alyser/";
            if (getMetier() != Metier.ARCHIMAGE || action != Action.SORT) {
                text += "(p)otion/";
            }
        }
        //item
        if (soin && !a_soigne) {
            text += "(soi)gner/";
        }
        if (sphinx && !a_renforce) {
            text += "(ren)forcer/";
        }
        if (fee && !a_soigne_fee) {
            text += "soin (fée)rique/";
        }
        if (grenade > 0) {
            text += "lancer une (gre)nade/";
        }
        text += "(c)ustom/(A)ucune";
        return text;
    }
    
    /**
     * Extension de la fonction Input.extra, permet de jouer les actions bonus de métier
     * @param choix le choix fait par le joueur (en lowercase)
     * @return si le tour a été joué
     */
    public Action_extra extra(String choix) {
        switch (choix) {
            case "soi" -> {
                if (soin && !a_soigne) {
                    return Action_extra.SOIGNER;
                }
            }
            case "ren" -> {
                if (sphinx && !a_renforce) {
                    return Action_extra.RENFORCER;
                }
            }
            case "fée", "fee" -> {
                if (fee && !a_soigne_fee) {
                    return Action_extra.SOIGNER_FEE;
                }
            }
            case "gre" -> {
                if (grenade > 0) {
                    return Action_extra.GRENADE;
                }
            }
        }
        return Action_extra.AUCUNE;
    }
    
    public int jouer_extra(Action_extra extra) {
        switch (extra) {
            case RAGE -> rage();
            case SOIGNER -> soigner();
            case RENFORCER -> renforcer();
            case SOIGNER_FEE -> soigner_fee();
            case GRENADE -> {
                return lancer_grenade();
            }
        }
        return 0;
    }
    
    /**
     * Traite l'action bonus rage (initialement exclusive à la guerrière)
     */
    protected void rage() {
        System.out.println(nom + " s'enrage !");
        berserk += 0.1f + 0.1f * rand.nextInt(5); //0.1 à 0.5
    }
    
    protected void soigner() {
        int soin = 6 + rand.nextInt(3); //6~8
        System.out.printf("Vous soignez votre cible de %d.\n", soin);
        a_soigne = true;
    }
    
    protected void renforcer() {
        int renforcement = 6 + rand.nextInt(3); //6~8
        System.out.printf("Votre cible gagne temporairement %d points de résistance.\n", renforcement);
        a_renforce = true;
    }
    
    protected void soigner_fee() {
        int soin = 8 + rand.nextInt(5); //8~12
        System.out.printf("Vous soignez votre cible de %d.\n", soin);
        a_soigne_fee = true;
    }
    
    protected int lancer_grenade() {
        grenade -= 1;
        return 6 + rand.nextInt(11);
    }
    
    /**
     * Traite l'action boule de feus mineurs
     * @param ennemi     la cible du sort
     * @param bonus_popo dommage additionnel
     * @throws IOException toujours
     */
    private void boule_feu_mineure(Monstre ennemi, int bonus_popo) throws IOException {
        int dmg = bonus_popo + switch (Input.D6()) {
            case 1 -> 3;
            case 2, 3 -> 6;
            case 4, 5 -> 9;
            case 6 -> 12;
            default -> {
                System.out.println("Argument inconnu, sort ignoré");
                yield 0;
            }
        };
        dmg += rune_ardente * 3;
        dmg += rune_ardente2 * 5;
        ennemi.dommage_magique(dmg);
    }
    
    /**
     * Traite le sort lumière
     * @param ennemi le monstre aveuglé
     */
    private void sort_lumiere(Monstre ennemi) {
        a_aveugle = true;
        Texte.aveugler(ennemi);
        ennemi.boostAtk(2, false);
    }
    
    /**
     * Traite le sort Eruption volcanique
     * @param ennemi     la cible du sort
     * @param bonus_popo des dommages additionel à appliquer
     * @throws IOException toujours
     */
    private void sort_volcan(Monstre ennemi, int bonus_popo) throws IOException {
        ennemi.dommage_magique(48 + rand.nextInt(17) + bonus_popo);
    }
    
    private void annihilation(Monstre ennemi, int bonus_popo) throws IOException {
        Texte.annihilation();
        int atk = Input.atk() + bonus_atk();
        int tir = Input.tir() + bonus_tir();
        if (tir > atk) {
            atk = tir;
        }
        ennemi.dommage_magique(atk);
        ennemi.dommage(atk + bonus_popo);
        ennemi.tir(atk);
    }
    
    /**
     * Traite l'action bonus potion
     * @return les dégas additionnel des potions (en négatif si les dommages ne s'appliquent qu'à l'attaque au corps
     * à corps).
     */
    public int popo() throws IOException {
        System.out.println(nom + """
                ,quelle type de potion utilisez vous :
                1 : Soin (PV)
                2 : Résistance (RES)
                3 : Force (ATK)
                4 : Poison (P)
                5 : Explosive (E)
                6 : drogue de guerre (BSRK)
                7 : Aucune/Custom""");
        int temp = Input.readInt();
        if (temp <= 0 || temp > 7) {
            System.out.println("Unknown input.");
            return popo();
        }
        return switch (temp) {
            case 1 -> popo_soin();
            case 2 -> popo_res();
            case 3 -> popo_force();
            case 4 -> popo_cd();
            case 5 -> popo_instable();
            case 6 -> popo_berserk();
            case 7 -> 0;
            default -> {
                System.out.println("Unknown input");
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
            System.out.println("Unknown input");
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
            System.out.println("Unknown input");
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
            System.out.println("Unknown input");
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
                5 : potion nécrotique   (P#5)
                6 : aucune (reviens au choix des potions))""");
        int temp = Input.readInt();
        if (temp <= 0 || temp > 6) {
            System.out.println("Unknown input");
            return popo_cd();
        }
        int poison = 0;
        switch (temp) {
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
                System.out.println("Unknown input");
                yield popo_instable();
            }
        };
    }
    
    /**
     * Calcule et traite la Folie meurtrière (Berserk)
     * @return 0
     * @throws IOException toujours
     */
    @SuppressWarnings("DuplicatedCode") //la fonction fille dans Alchimiste.java
    private int popo_berserk() throws IOException {
        System.out.println("""
                Entrez la potion que vous utilisez :
                1 : boulette irritante          (BSRK#1)
                2 : capsule de colère           (BSRK#2)
                3 : potion de violence          (BSRK#3)
                4 : pilule de folie meurtrière  (BSRK#4)
                5 : aucune (reviens au choix des potions))""");
        return switch (Input.readInt()) {
            case 1 -> {
                this.berserk += 0.1f + rand.nextInt(2) * 0.1f; //0.1~0.2
                yield 0;
            }
            case 2 -> {
                this.berserk += 0.1f + rand.nextInt(5) * 0.1f; //0.1~0.5
                yield 0;
            }
            case 3 -> {
                this.berserk += 0.25f + rand.nextInt(6) * 0.15f; //0.25~1
                yield 0;
            }
            case 4 -> {
                this.berserk += 0.5f + rand.nextInt(11) * 0.1f; //0.5~1.5
                yield 0;
            }
            case 5 -> popo();
            default -> {
                System.out.println("Unknown input");
                yield popo_berserk();
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
        System.out.println("La potion heurte violemment l'ennemi avant de lui exploser à la face.");
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
        System.out.println("La bombe heurte violemment l'ennemi avant de lui exploser au visage.");
        return 18;
    }
    
    /**
     * Comptabilise les bonus d'exploration des métiers
     * @return le bonus
     */
    public int bonus_exploration() {
        if (pegase) {
            return 1;
        }
        return 0;
    }
    
    /**
     * Comptabilise les bonus d'analyse du joueur
     * @return le bonus
     */
    public int bonus_analyse() {
        int bonus = 0;
        if (!est_front()) {
            bonus -= 2;
        }
        if (a_cecite()) {
            bonus -= 1;
        }
        return bonus;
    }
    
    public int trajet_mer(int jet) {
        if (bateau && jet < 5) {
            jet = 5;
        }
        return jet;
    }
    
    /**
     * Comptabilise les bonus de dressage
     * @return le bonus
     */
    public int bonus_dresser() {
        if (this.niveau >= 7) {
            return 1;
        }
        return 0;
    }
    
    /**
     * Réalise les actions de fin de tour (en combat)
     */
    public void fin_tour_combat() {
        skip = false;
        f_skip = false;
        if (a_poison1()) {
            System.out.println(nom + " souffre d'empoisonnement et subit " + (rand.nextInt(3) + 1) + " dommage(s) " +
 "directe(s).");
        }
        if (a_poison2()) {
            System.out.println(nom + " souffre d'empoisonnement et subit " + (rand.nextInt(4) + 3) + " dommages " +
"directes.");
        }
        if (f_a_poison1()) {
            System.out.println("Le familier de " + nom + " souffre d'empoisonnement et subit " + (rand.nextInt(3) + 1) + " dommage(s) directe(s).");
        }
        if (f_a_poison2()) {
            System.out.println("Le familier de " + nom + " souffre d'empoisonnement et subit " + (rand.nextInt(4) + 3) + " dommages directes.");
        }
    }
    
    /**
     * Subit la compétence "Onde ce choc" de l'archimage
     */
    protected void choc(int bonus) throws IOException {
        System.out.println(nom + " est frappé par l'onde de choc.");
        int jet = bonus;
        jet -= 2 + rand.nextInt(4); //2~5
        if (est_assomme()) {
            jet += Input.D6();
            reveil -= Math.max(1, jet);
            return;
        }
        if (jet < -1) {
            jet += Input.D6();
        }
        if (jet < 0) {
            System.out.println(nom + " perd connaissance.");
            assomme();
        } else {
            System.out.println(nom + " parvient à rester conscient.");
        }
        //familier
        if (!a_familier_actif()) {
            return;
        }
        jet = bonus;
        jet -= 2 - rand.nextInt(2); //2~3
        if (f_est_assomme()) {
            jet += Input.D4();
            f_reveil -= Math.max(1, jet);
            return;
        }
        if (jet < -1) {
            jet += Input.D4();
        }
        if (jet < 0) {
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
     * Indique si le joueur est capable de ressusciter un autre joueur
     * @return un booléan correspondant
     */
    public boolean peut_ressusciter() {
        return false;
    }
    
    /**
     * Tente de ressusciter un joueur
     * @param malus un malus à appliquer à la tentative
     * @return true si la resurrection est un succès, false sinon
     * @throws IOException toujours
     */
    public boolean ressusciter(int malus) throws IOException {
        return false;
    }
    
    /**
     * Tente de ressusciter tout seul
     * @param malus un malus à appliquer à la tentative
     * @return true si la resurrection est un succès, false sinon
     * @throws IOException toujours
     */
    public boolean auto_ressusciter(int malus) throws IOException {
        return false;
    }
    
    /**
     * Indique si le joueur est capable de diriger son familier, c'est-à-dire de lui donner un ordre
     * @return true si le joueur a un familier et peut lui donner un ordre
     */
    public boolean peut_diriger_familier() {
        return est_actif() && est_vivant() && !est_assomme() && a_familier_actif();
    }
    
    /**
     * Gère les effets de coup critique à l'arc
     * @param base la puissance de tir originale
     * @return le bonus de dommages
     */
    protected float critique_tir(int base) {
        int imprecision = 50;
        if (this.niveau >= 9) {
            imprecision = 40;
        }
        if (rand.nextInt(imprecision) == 0) { //2%~2.5%
            return base * 0.1f * (rand.nextInt(5) + 1); //10% à 50% de bonus
        }
        return 0;
    }
    
    /**
     * Calcule si un joueur succombe à sa folie de berserk ou non
     * @return si le joueur frappe sans savoir qui
     */
    protected boolean folie_berserk() throws IOException {
        return Input.D6() < 2 + berserk;
    }
    
    /**
     * Augmente l'état de rage meurtrière du joueur
     * @param is_crazy si le joueur est déjà dans un état de folie (augmente la déterioration).
     */
    protected void berserk_boost(boolean is_crazy) {
        if (is_crazy) {
            berserk += 0.1f + rand.nextInt(5) * 0.1f; //0.1 à 0.5 de boost
        } else {
            this.berserk += 0.1f + rand.nextInt(3) * 0.1f; //0.1 à 0.3 de boost
        }
    }
    
    /**
     * Indique les bonus d'attaque à l'arc
     * @return la quantité de dommages additionnelle
     */
    protected int bonus_tir() {
        int bonus = 0;
        if (a_cecite()) {
            bonus -= 10;
        }
        return bonus;
    }
    
    /**
     * Gère les effets de coup critique
     * @param base la puissance de frappe originale
     * @return le bonus de dommages
     */
    protected float critique_atk(int base) {
        int imprecision = 50;
        if (this.niveau >= 9) {
            imprecision -= 10;
        }
        if (rand.nextInt(imprecision) == 0) { //2% à 2.5%
            return base * 0.1f * (rand.nextInt(5) + 1); //10% à 50% de bonus
        }
        return 0;
    }
    
    /**
     * Lance le sort berserk : rend le joueur berserk
     */
    protected void berserk() {
        System.out.println(nom + " est prit d'une folie meurtrière !");
        berserk = 0.1f + 0.1f * rand.nextInt(7); //0.1 à 0.7
        if (rune_haine && getParent() == Dieux.ARES) {
            berserk += 0.1f + rand.nextInt(5) * 0.1f; //0.1~0.5
        }
    }
    
    /**
     * Lance le sort infection : améliore les attaques de l'arc
     */
    protected void infection() {
        System.out.println("Les flèches de " + nom + " s'emplissent de maladies.");
        bonus_infection += rune_virale ? 4 : 2;
    }
    
    /**
     * Lance le sort sérénité : guérie un joueur
     */
    protected void serenite() {
        System.out.println("Ciblez un joueur ou familier.");
        int soin = 5;
        soin += rand.nextInt(3);
        if (rune_croissance) {
            soin += 5;
            soin += rand.nextInt(5);
        }
        System.out.printf("La cible guérie de %d.\n", soin);
    }
    
    /**
     * Lance le sort Inondation : inflige des dommages magiques et affecte
     */
    protected void inondation(Monstre ennemi, int dps_bonus) throws IOException {
        System.out.println("Une vague d'eau percute " + ennemi.getNom() + " de plein fouet.");
        ennemi.dommage_magique((rune_pluie ? 10 : 15) + dps_bonus);
        ennemi.affecte();
    }
    
    /**
     * Lance le sort Foudre : inflige des dommages magiques et affecte
     */
    protected void foudre_zeus(Monstre ennemi, int dps_bonus) throws IOException {
        System.out.println("Un éclair s'abat sur " + ennemi.getNom() + ".");
        ennemi.dommage_magique(dps_bonus + (rune_orage ? 15 : 20));
        ennemi.affecte();
    }
    
    /**
     * Nombre spécifique indiquant qu'un joueur berserk a tiré/attaqué un allié
     **/
    static float berserk_atk_alliee = -256;
    
    /**
     * Gère les effets de la folie du berserk lors d'attaque
     * @param base la puissance de frappe
     * @return le bonus de dommages, ou berserk_atk_alliee si le joueur attaque un allié
     * @throws IOException toujours
     */
    protected float berserk_atk(int base) throws IOException {
        System.out.println("Vous êtes pris(e) de folie meurtrière et distinguez mal vos alliés de vos ennemis.");
        if (folie_berserk()) {
            int i;
            do {
                i = rand.nextInt(Main.nbj + 1);
            } while (i < Main.nbj && !Main.joueurs[i].est_actif());
            if (i == Main.nbj) {
                return base * berserk * 0.5f; //50% bonus en moins dû à la folie
            }
            int temp = base;
            temp += Main.corriger(temp * (berserk * 0.5f));
            String cible = Main.joueurs[i].getNom();
            if (a_familier_actif() && rand.nextBoolean()) {
                cible = "le familier de " + cible;
            }
            System.out.println("Pris(e) de folie, " + nom + " attaque " + cible + " et lui inflige " + temp + " " +
            "dommages !");
            berserk_boost(true);
            return berserk_atk_alliee;
        }
        berserk_boost(false);
        return base * berserk;
    }
    
    /**
     * Indique les bonus d'attaque classique
     * @return la quantité de dommages additionnels
     */
    protected int bonus_atk() {
        int bonus = 0;
        if (a_cecite()) {
            bonus -= 1;
        }
        if (lame_vent && first_attaque) {
            bonus += 3 + rand.nextInt(3);
            Texte.lame_vent();
            first_attaque = false;
        }
        return bonus;
    }
    
    /**
     * Tente de fuir le combat
     * @param ennemi Le monstre adverse (peut complexifier la tâche et changer le gain d'xp).
     * @throws IOException ça roule
     */
    public void fuir(Monstre ennemi) throws IOException {
        int bonus = -1 + rand.nextInt(3);
        if (ennemi.est_nomme()) {
            bonus -= 1;
        }
        if (cheval) {
            bonus += 3;
        }
        if (pie) {
            bonus += 1;
        }
        bonus += bonus_fuite();
        bonus += berserk_fuite();
        bonus += position_fuite();
        if (Input.D6() + bonus >= 3) {
            if (ennemi.est_pantin()) {
                System.out.println(nom + " aurait réussit à fuir le combat.");
                return;
            }
            System.out.println(nom + " a fuit le combat.");
            inactiver();
            if (ennemi.est_nomme()) {
                gagneXp();
            }
        } else {
            System.out.println(nom + " n'est pas parvenu à fuir le combat.");
        }
    }
    
    /**
     * Fuit le combat grâce à un téléporteur court porté
     */
    private void fuite_tp(Monstre ennemi) {
        if (ennemi.est_pantin()) {
            System.out.println(nom + " aurait réussit à fuir le combat.");
            return;
        }
        System.out.println(nom + " a fuit le combat.");
        inactiver();
        if (rand.nextInt(5) == 0) {
            System.out.println("Le téléporteur s'est détruit.");
            fuite = false;
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
        if (this.niveau >= 4) {
            return 1;
        }
        return 0;
    }
    
    /**
     * Indique le malus de fuite dû à l'état de berserk
     * @return le malus (négatif)
     * @throws IOException toujours
     */
    protected int berserk_fuite() throws IOException {
        if (!est_berserk()) {
            return 0;
        }
        return Math.min(0, Math.round(Input.D4() * 0.5f - berserk));
    }
    
    /**
     * Indique les bonus de fuite dû à la position (première ligne)
     * @return le malus
     */
    protected int position_fuite() {
        if (est_front()) {
            if (est_front_f()) {
                return -2;
            }
            return -3;
        }
        return 0;
    }
    
    /**
     * Indique les bonus de fuite dû à la position (première ligne) du familier
     * @return le malus
     */
    protected int f_position_fuite() {
        if (est_front()) {
            if (est_front_f()) {
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
                ennemi.boostEncaissement(0.1F);
                System.out.println("Votre familier vous protège maladroitement.");
                break;
            case 3, 4:
                ennemi.boostEncaissement(0.3F);
                System.out.println("Votre familier vous protège.");
                break;
            case 5, 6:
                ennemi.boostEncaissement(0.5F);
                System.out.println("Votre familier vous protège.");
                break;
            case 7, 8, 9, 10:
                ennemi.boostEncaissement(0.7F);
                System.out.println("Votre familier concentre chaque fibre de son être à se préparer à vous protéger.");
                break;
            default:
                System.out.println("Le résultat n'a pas été comprit, compétence ignorée.");
        }
    }
    
    /**
     * Propose les actions métiers sur un cadavre
     */
    protected void monstre_mort_perso(Monstre ennemi) throws IOException {
    }
    
    /**
     * Renvoie le résultat d'un jet de dé
     * @param paliers_de   les différents niveaux demandant différents dés, dans l'ordre décroissant
     * @param de           le dé à lancer selon le niveau, doit contenir un élément de plus que paliers_de
     * @param palier_bonus les niveaux ajoutant un bonus de 1 au dé
     * @return la valeur du jet avec les modificateurs appliqué
     * @throws IOException toujours
     */
    protected int jet(int[] paliers_de, int[] de, int[] palier_bonus) throws IOException {
        int jet = 0;
        for (int i = 0; i < paliers_de.length; i++) {
            if (this.niveau >= paliers_de[i]) {
                jet = de_par_palier(de[i]);
                break;
            }
        }
        if (jet == 0) {
            jet = de_par_palier(de[de.length - 1]);
        }
        for (int palier : palier_bonus) {
            if (this.niveau >= palier) {
                jet += 1;
            }
        }
        jet += rand.nextInt(3) - 1;
        return jet;
    }
    
    /**
     * Renvoie le résultat d'un jet du type spécifié
     * @param de_type le nombre de face du dé demandé (4, 6, 8, 10, 12 ou 20)
     * @return le résultat du jet de dé
     * @throws IOException           toujours
     * @throws IllegalStateException si le dé demandé n'est pas reconnu
     */
    protected int de_par_palier(int de_type) throws IOException {
        return switch (de_type) {
            case 4 -> Input.D4();
            case 6 -> Input.D6();
            case 8 -> Input.D8();
            case 10 -> Input.D10();
            case 12 -> Input.D12();
            case 20 -> Input.D20();
            default -> throw new IllegalStateException("Unexpected value: " + de_type);
        };
    }
    
    //************************************************ITEM************************************************************//
    
    public void add_lame_infernale() {
        this.lame_infernale = true;
        if (this.position == Position.ENFERS) {
            Texte.reaction_equipement();
        }
    }
    
    public void retire_lame_infernale() {
        this.lame_infernale = false;
    }
    
    public void add_lame_vegetale() {
        this.lame_vegetale = true;
        if (this.position == Position.PRAIRIE) {
            Texte.reaction_equipement();
        }
    }
    
    public void retire_lame_vegetale() {
        this.lame_vegetale = false;
    }
    
    public void add_trident() {
        this.trident = true;
        if (this.position == Position.MER) {
            Texte.reaction_equipement();
        }
    }
    
    public void retire_trident() {
        this.trident = false;
    }
    
    public void add_lame_mont() {
        this.lame_mont = true;
        if (this.position == Position.MONTS) {
            Texte.reaction_equipement();
        }
    }
    
    public void retire_lame_mont() {
        this.lame_mont = false;
    }
    
    public void add_nectar() {
        if (ambroisie) {
            Texte.victoire(this.nom);
        }
        this.nectar = true;
    }
    
    public void retire_nectar() {
        this.nectar = false;
    }
    
    public void add_ambroisie() {
        if (this.nectar) {
            Texte.victoire(this.nom);
        }
        this.ambroisie = true;
    }
    
    public void retire_ambroisie() {
        this.ambroisie = false;
    }
    
    public void add_guerre() {
        if (this.parent == Dieux.ARES) {
            Texte.reaction_equipement();
        }
        this.guerre += 1;
    }
    
    public void retire_guerre() {
        this.guerre -= 1;
    }
    
    public void add_lame_vent() {
        this.lame_vent = true;
    }
    
    public void retire_lame_vent() {
        this.lame_vent = false;
    }
    
    public void add_lame_fertile() {
        if (this.parent == Dieux.DEMETER || this.parent == Dieux.DIONYSOS) {
            Texte.reaction_equipement();
        }
        lame_fertile = true;
    }
    
    public void retire_lame_fertile() {
        lame_fertile = false;
    }
    
    public void add_parch_feu() {
        if (getMetier() == Metier.ARCHIMAGE) {
            Texte.parchemin_archimage();
        }
        if (rune_ardente2 > 0 || rune_ardente > 0) {
            Texte.reaction_equipement();
        }
        this.parch_feu = true;
    }
    
    public void retire_parch_feu() {
        this.parch_feu = false;
    }
    
    public void add_parch_dodo() {
        if (getMetier() == Metier.ARCHIMAGE) {
            Texte.parchemin_archimage();
        }
        if (rune_dodo) {
            Texte.reaction_equipement();
        }
        this.parch_dodo = true;
    }
    
    public void retire_parch_dodo() {
        this.parch_dodo = false;
    }
    
    public void add_parch_lumiere() {
        if (getMetier() == Metier.ARCHIMAGE) {
            Texte.parchemin_archimage();
        }
        this.parch_lumiere = true;
    }
    
    public void retire_parch_lumiere() {
        this.parch_lumiere = false;
    }
    
    public void add_rune_croissance() {
        if (this.parent == Dieux.DEMETER) {
            Texte.reaction_equipement();
        }
        this.rune_croissance = true;
    }
    
    public void retire_rune_croissance() {
        this.rune_croissance = false;
    }
    
    public void add_rune_pluie() {
        if (this.parent == Dieux.POSEIDON) {
            Texte.reaction_equipement();
        }
        this.rune_pluie = true;
    }
    
    public void retire_rune_pluie() {
        this.rune_pluie = false;
    }
    
    public void add_rune_haine() {
        if (this.parent == Dieux.ARES || getMetier() == Metier.GUERRIERE) {
            Texte.reaction_equipement();
        }
        this.rune_haine = true;
    }
    
    public void retire_rune_haine() {
        this.rune_haine = false;
    }
    
    public void add_rune_virale() {
        if (this.parent == Dieux.APOLLON) {
            Texte.reaction_equipement();
        }
        this.rune_virale = true;
    }
    
    public void retire_rune_virale() {
        this.rune_virale = false;
    }
    
    public void add_rune_dodo() {
        if (parch_dodo) {
            Texte.reaction_equipement();
        }
        this.rune_dodo = true;
    }
    
    public void retire_rune_dodo() {
        this.rune_dodo = false;
    }
    
    public void add_rune_mortifere() {
        if (this.parent == Dieux.HADES || getMetier() == Metier.NECROMANCIEN) {
            Texte.reaction_equipement();
        }
        this.rune_mortifere = true;
    }
    
    public void retire_rune_mortifere() {
        this.rune_mortifere = false;
    }
    
    public void add_rune_orage() {
        if (this.parent == Dieux.ZEUS) {
            Texte.reaction_equipement();
        }
        this.rune_orage = true;
    }
    
    public void retire_rune_orage() {
        this.rune_orage = false;
    }
    
    public void add_rune_ardente() {
        if (parch_feu || getMetier() == Metier.ARCHIMAGE) {
            Texte.reaction_equipement();
        }
        this.rune_ardente += 1;
    }
    
    public void retire_rune_ardente() {
        this.rune_ardente -= 1;
    }
    
    public void add_rune_ardente2() {
        if (parch_feu || getMetier() == Metier.ARCHIMAGE) {
            Texte.reaction_equipement();
        }
        this.rune_ardente2 += 1;
    }
    
    public void retire_rune_ardente2() {
        this.rune_ardente2 -= 1;
    }
    
    public void add_rune_commerce() {
        if (this.parent == Dieux.DIONYSOS) {
            Texte.reaction_equipement();
        }
        this.rune_commerce = true;
    }
    
    public void retire_rune_commerce() {
        this.rune_commerce = false;
    }
    
    public void add_soin() {
        this.soin = true;
    }
    
    public void retire_soin() {
        this.soin = false;
    }
    
    public void add_bracelet_protec() {
        this.bracelet_protect = true;
    }
    
    public void retire_bracelet_protec() {
        this.bracelet_protect = false;
    }
    
    public void add_rune_noire() {
        if (getMetier() == Metier.NECROMANCIEN) {
            Texte.reaction_equipement();
        }
        this.rune_noire = true;
    }
    
    public void retire_rune_noire() {
        this.rune_noire = false;
    }
    
    public void add_absorption() {
        if (this.absorption) {
            Texte.duplicata_impossible();
        }
        this.absorption = true;
    }
    
    public void retire_absorption() {
        this.absorption = false;
    }
    
    public void add_lunette() {
        this.lunette = true;
    }
    
    public void retire_lunette() {
        this.lunette = false;
    }
    
    public void add_dissec() {
        this.dissec = true;
    }
    
    public void retire_dissec() {
        this.dissec = false;
    }
    
    public void add_concoc() {
        this.concoct = true;
    }
    
    public void retire_concoc() {
        this.concoct = false;
    }
    
    public void add_bourdon() {
        if (getMetier() == Metier.ARCHIMAGE) {
            Texte.reaction_equipement();
        }
        this.bourdon = true;
    }
    
    public void retire_bourdon() {
        this.bourdon = false;
    }
    
    public void add_parch_volcan() {
        if (getMetier() == Metier.ARCHIMAGE) {
            Texte.parchemin_archimage();
        }
        this.parch_volcan = true;
    }
    
    public void retire_parch_volcan() {
        this.parch_volcan = false;
    }
    
    public void add_absorption2() {
        this.absorption2 = true;
    }
    
    public void retire_absorption2() {
        this.absorption2 = false;
    }
    
    public void add_cheval() {
        this.cheval = true;
    }
    
    public void retire_cheval() {
        this.cheval = false;
    }
    
    public void add_pegase() {
        this.pegase = true;
    }
    
    public void retire_pegase() {
        this.pegase = false;
    }
    
    public void add_pie() {
        this.pie = true;
    }
    
    public void retire_pie() {
        this.pie = false;
    }
    
    public void add_sphinx() {
        this.sphinx = true;
    }
    
    public void retire_sphinx() {
        this.sphinx = false;
    }
    
    public void add_fee() {
        this.fee = true;
    }
    
    public void retire_fee() {
        this.fee = false;
    }
    
    public void add_rune_arca() {
        if (getMetier() == Metier.ARCHIMAGE) {
            Texte.reaction_equipement();
        }
        this.rune_arca = true;
    }
    
    public void retire_rune_arca() {
        this.rune_arca = false;
    }
    
    public void add_antidote() {
        this.antidote = true;
    }
    
    public void retire_antidote() {
        this.antidote = false;
    }
    
    public void add_rune_annihilation() {
        this.rune_annihilation = true;
    }
    
    public void retire_rune_annihilation() {
        this.rune_annihilation = false;
    }
    
    public void add_tatouage_resurection() {
        this.tatouage_resurection = true;
    }
    
    public void retire_tatouage_resurection() {
        Texte.warning();
        this.tatouage_resurection = false;
    }
    
    public void add_fuite() {
        this.fuite = true;
    }
    
    public void retire_fuite() {
        this.fuite = false;
    }
    
    public void add_grenade() {
        this.grenade += 8;
    }
    
    public void retire_grenade() {
        Texte.jete_grenade(grenade);
        this.grenade = 0;
    }
    
    public void add_bateau() {
        this.bateau = true;
    }
    
    public void retire_bateau() {
        this.bateau = false;
    }
    
    public void retirer_tout(boolean silence) {
        if (!silence) {
            Texte.retirer_tout();
        }
        lame_infernale = false;
        lame_vegetale = false;
        trident = false;
        lame_mont = false;
        nectar = false;
        ambroisie = false;
        guerre = 0;
        lame_vent = false;
        lame_fertile = false;
        parch_feu = false;
        parch_dodo = false;
        parch_lumiere = false;
        rune_croissance = false;
        rune_pluie = false;
        rune_haine = false;
        rune_virale = false;
        rune_dodo = false;
        rune_mortifere = false;
        rune_orage = false;
        rune_ardente = 0;
        rune_ardente2 = 0;
        rune_commerce = false;
        soin = false;
        bracelet_protect = false;
        rune_noire = false;
        absorption = false;
        lunette = false;
        dissec = false;
        concoct = false;
        bourdon = false;
        parch_volcan = false;
        absorption2 = false;
        cheval = false;
        pegase = false;
        pie = false;
        sphinx = false;
        fee = false;
        rune_arca = false;
        antidote = false;
        rune_annihilation = false;
        tatouage_resurection = false;
        fuite = false;
        grenade = 0;
        bateau = false;
    }
    
    private JsonArray save_effet_structure() {
        JsonArrayBuilder array = Json.createArrayBuilder();
        
        if (lame_infernale) array.add(Json.createObjectBuilder().add("id", "01"));
        if (lame_vegetale) array.add(Json.createObjectBuilder().add("id", "02"));
        if (trident) array.add(Json.createObjectBuilder().add("id", "03"));
        if (lame_mont) array.add(Json.createObjectBuilder().add("id", "04"));
        if (nectar) array.add(Json.createObjectBuilder().add("id", "05"));
        if (ambroisie) array.add(Json.createObjectBuilder().add("id", "06"));
        if (guerre > 0) array.add(Json.createObjectBuilder().add("id", "07").add("quantite", guerre));
        if (lame_vent) array.add(Json.createObjectBuilder().add("id", "08"));
        if (lame_fertile) array.add(Json.createObjectBuilder().add("id", "09"));
        if (parch_feu) array.add(Json.createObjectBuilder().add("id", "10"));
        if (parch_dodo) array.add(Json.createObjectBuilder().add("id", "11"));
        if (parch_lumiere) array.add(Json.createObjectBuilder().add("id", "12"));
        if (rune_croissance) array.add(Json.createObjectBuilder().add("id", "13"));
        if (rune_pluie) array.add(Json.createObjectBuilder().add("id", "14"));
        if (rune_haine) array.add(Json.createObjectBuilder().add("id", "15"));
        if (rune_virale) array.add(Json.createObjectBuilder().add("id", "16"));
        if (rune_ardente > 0) array.add(Json.createObjectBuilder().add("id", "17").add("quantite", rune_ardente));
        if (rune_ardente2 > 0) array.add(Json.createObjectBuilder().add("id", "18").add("quantite", rune_ardente2));
        if (rune_dodo) array.add(Json.createObjectBuilder().add("id", "19"));
        if (rune_mortifere) array.add(Json.createObjectBuilder().add("id", "20"));
        if (rune_orage) array.add(Json.createObjectBuilder().add("id", "21"));
        if (rune_commerce) array.add(Json.createObjectBuilder().add("id", "22"));
        if (soin) array.add(Json.createObjectBuilder().add("id", "23"));
        if (bracelet_protect) array.add(Json.createObjectBuilder().add("id", "24"));
        if (rune_noire) array.add(Json.createObjectBuilder().add("id", "25"));
        if (absorption) array.add(Json.createObjectBuilder().add("id", "26"));
        if (lunette) array.add(Json.createObjectBuilder().add("id", "27"));
        if (dissec) array.add(Json.createObjectBuilder().add("id", "28"));
        if (concoct) array.add(Json.createObjectBuilder().add("id", "29"));
        if (bourdon) array.add(Json.createObjectBuilder().add("id", "30"));
        if (parch_volcan) array.add(Json.createObjectBuilder().add("id", "31"));
        if (absorption2) array.add(Json.createObjectBuilder().add("id", "32"));
        if (cheval) array.add(Json.createObjectBuilder().add("id", "33"));
        if (pegase) array.add(Json.createObjectBuilder().add("id", "34"));
        if (pie) array.add(Json.createObjectBuilder().add("id", "35"));
        if (sphinx) array.add(Json.createObjectBuilder().add("id", "36"));
        if (fee) array.add(Json.createObjectBuilder().add("id", "37"));
        if (rune_arca) array.add(Json.createObjectBuilder().add("id", "38"));
        if (antidote) array.add(Json.createObjectBuilder().add("id", "39"));
        if (rune_annihilation) array.add(Json.createObjectBuilder().add("id", "40"));
        if (tatouage_resurection) array.add(Json.createObjectBuilder().add("id", "41"));
        if (fuite) array.add(Json.createObjectBuilder().add("id", "42"));
        if (grenade > 0) array.add(Json.createObjectBuilder().add("id", "43").add("quantite", grenade));
        if (bateau) array.add(Json.createObjectBuilder().add("id", "44"));
        
        return array.build();
    }
    
    public void load_effet_structure(JsonArray effets) {
        for (JsonValue val : effets) {
            if (!(val instanceof JsonObject obj)) continue;
            String id = obj.getString("id", "");
            int quantite = obj.containsKey("quantite") ? obj.getInt("quantite") : 0;
            
            switch (id) {
                case "01":
                    lame_infernale = true;
                    break;
                case "02":
                    lame_vegetale = true;
                    break;
                case "03":
                    trident = true;
                    break;
                case "04":
                    lame_mont = true;
                    break;
                case "05":
                    nectar = true;
                    break;
                case "06":
                    ambroisie = true;
                    break;
                case "07":
                    guerre = quantite;
                    break;
                case "08":
                    lame_vent = true;
                    break;
                case "09":
                    lame_fertile = true;
                    break;
                case "10":
                    parch_feu = true;
                    break;
                case "11":
                    parch_dodo = true;
                    break;
                case "12":
                    parch_lumiere = true;
                    break;
                case "13":
                    rune_croissance = true;
                    break;
                case "14":
                    rune_pluie = true;
                    break;
                case "15":
                    rune_haine = true;
                    break;
                case "16":
                    rune_virale = true;
                    break;
                case "17":
                    rune_ardente = quantite;
                    break;
                case "18":
                    rune_ardente2 = quantite;
                    break;
                case "19":
                    rune_dodo = true;
                    break;
                case "20":
                    rune_mortifere = true;
                    break;
                case "21":
                    rune_orage = true;
                    break;
                case "22":
                    rune_commerce = true;
                    break;
                case "23":
                    soin = true;
                    break;
                case "24":
                    bracelet_protect = true;
                    break;
                case "25":
                    rune_noire = true;
                    break;
                case "26":
                    absorption = true;
                    break;
                case "27":
                    lunette = true;
                    break;
                case "28":
                    dissec = true;
                    break;
                case "29":
                    concoct = true;
                    break;
                case "30":
                    bourdon = true;
                    break;
                case "31":
                    parch_volcan = true;
                    break;
                case "32":
                    absorption2 = true;
                    break;
                case "33":
                    cheval = true;
                    break;
                case "34":
                    pegase = true;
                    break;
                case "35":
                    pie = true;
                    break;
                case "36":
                    sphinx = true;
                    break;
                case "37":
                    fee = true;
                    break;
                case "38":
                    rune_arca = true;
                    break;
                case "39":
                    antidote = true;
                    break;
                case "40":
                    rune_annihilation = true;
                    break;
                case "41":
                    tatouage_resurection = true;
                    break;
                case "42":
                    fuite = true;
                    break;
                case "43":
                    grenade = quantite;
                    break;
                case "44":
                    bateau = true;
                    break;
            }
        }
    }
    
    
}
package Monstre;

import Equipement.Equipement;
import Exterieur.Input;
import Enum.Competence;
import Enum.Position;
import main.Combat;
import main.Main;
import Metiers.Joueur;

import java.io.IOException;
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
    protected int drop_quantite_max;
    protected float encaissement;   // quand l'adversaire utilise "encaisser"
    protected float part_soin;      // quand l'adversaire utilise "premier soin" en première ligne.
    protected int etat;         //l'état du corps du monstre

    // stats de base à concerver
    protected int vie_base;
    protected int attaque_base;
    protected int armure_base;

    Monstre(Race race) {
        this.nom = race.get_nom();
        this.attaque = race.get_attaque();
        this.vie = race.get_vie();
        this.vie_max = this.vie;
        this.armure = race.get_armure();
        this.niveau_drop_min = race.get_niveau_drop_min();
        this.niveau_drop_max = race.get_niveau_drop_max();
        this.drop_quantite_max = race.get_drop_quantite();
        this.competence = race.competence();

        this.etourdi = false;
        this.assomme = false;
        this.encaissement = 0F;
        this.part_soin = 0F;

        this.vie_base = this.vie_max;
        this.attaque_base = this.attaque;
        this.armure_base = this.armure;

        this.etat = 20 + rand.nextInt(11);
        if(Objects.equals(this.nom, "illusioniste")){
            illu_check();
        }
    }

    static Random rand = new Random();

    public boolean corps_utilisable(){
        return etat > 0;
    }

    public int getEtat(){
        return etat;
    }

    public void alterEtat(int valeur){
        etat += valeur;
    }

    public String getNom() {
        return nom;
    }

    public void rename(String nom) {
        this.nom = nom;
    }

    public Competence getCompetence() {
        return competence;
    }

    public int getVieMax(){
        return vie_max;
    }

    public int getVie(){
        return vie;
    }

    public int getAtk(){
        return attaque;
    }

    public int getArmure(){
        return armure;
    }

    /**
     * Augmente/Modifie l'attaque
     * @param value la modification à appliquer
     * @param fondamental si la modification est intrasèque à l'unité ou juste temporaire/de surface
     */
    public void bostAtk(int value, boolean fondamental) {
        this.attaque += value;
        if(this.attaque < 0){
            this.attaque = 0;
        }
        if(fondamental){
            this.attaque_base += value;
            if(this.attaque_base < 0){
                this.attaque_base = 0;
            }
        }
    }

    /**
     * Augmente/Modifie l'armure
     * @param value la modification à appliquer
     * @param fondamental si la modification est intrasèque à l'unité ou juste temporaire/de surface
     */
    public void bostArmure(int value, boolean fondamental) {
        this.armure += value;
        if(this.armure < 0){
            this.armure = 0;
        }
        if(fondamental){
            this.armure_base += value;
            if(this.armure_base < 0){
                this.armure_base = 0;
            }
        }
    }

    /**
     * Augmente/Modifie la résistance
     * @param value la modification à appliquer
     * @param fondamental si la modification est intrasèque à l'unité ou juste temporaire/de surface
     */
    public void bostVie(int value, boolean fondamental) {
        this.vie_max += value;
        if(this.vie_max <= 0){
            this.vie_max = 1;
        }
        this.vie += value;
        if(fondamental){
            this.vie_base += value;
            if(this.vie_base <= 0){
                this.vie_base = 1;
            }
        }
    }

    /**
     * Augmente/Modifie la valeur d'encaissement
     * @param value la modification à appliquer
     */
    public void bostEncaissement(float value){
        this.encaissement += value;
    }

    /**
     * Nomme le golem
     * @param materieux le type de matériaux du golem
     */
    public void golemNom(String materieux){
        this.nom = "golem" + materieux;
    }

    public void bostDropMax(int value){
        this.niveau_drop_max += value;
    }

    public void bostDropMin(int value){
        this.niveau_drop_min += value;
    }

    public void bostDrop(int value){
        this.drop_quantite_max += value;
    }

    /**
     * Maquille l'illusioniste en lui donnant le nom de l'illusion
     */
    private void illu_check(){
        this.nom = switch (this.competence){
            case ILLU_AURAI -> "aurai maléfique";
            case ILLU_CYCLOPE -> "cyclope";
            case ILLU_DULLA -> "dullahan";
            case ILLU_GOLEM -> "golem";
            case ILLU_ROCHE -> "roche maudite";
            case ILLU_SIRENE -> "sirène";
            case ILLU_TRITON -> "triton";
            case ILLU_VENTI -> "venti";
            default -> this.nom;
        };
    }

    /**
     * Écris la quantité de dommage infligé par le monstre à l'adversaire
     * @param nom le nom de l'entité attaqué
     * @implNote Calcul les effet de "encaisser", "étourdit" et "assommé"
     */
    public void attaque(String nom) throws IOException {
        float modificateur = 1 - encaissement + part_soin;
        if (assomme) {
            undo_assomme();
        }
        else if (etourdi){
            if(applique_competence_pre(nom) && this.attaque > 0){
                System.out.println(this.nom + " est étourdit et inflige " + Main.corriger(this.attaque * 0.5F * modificateur) + " dommages à " + nom + ".");
                applique_competence_post(nom);
            }
            undo_etourdi();
        }
        else{
            if(applique_competence_pre(nom) && this.attaque > 0){
                System.out.println(this.nom + " inflige " + Main.corriger(this.attaque * modificateur) + " dommages à " + nom + ".");
                applique_competence_post(nom);
            }
        }
        fin_combat(nom);
        System.out.println();
    }

    /**
     * Applique la compétence du monstre avant son attaque
     * @throws IOException comme ça, ça marche
     * @return si l'attaque a bien lieu après application de la compétence
     */
    private boolean applique_competence_pre(String nom) throws IOException {
        switch (competence) {
            case EXPLOSION -> {
                this.attaque += 6;
                System.out.println(nom + " s'apprête à causer une explosion !");
            }
            case GEL -> {
                if(Input.yn("Portez vous (pl) une armure ?")){
                    System.out.println(this.nom + " détruit votre armure");
                    competence = Competence.AUCUNE;
                }
                else{
                    attaque += 3;
                }
            }
            case REGARD_MORTEL -> {
                System.out.println(this.nom + " regarde " + nom + " droit dans les yeux.");
                if(Input.D6() <= 4){
                    System.out.println(nom + " sent son âme se faire assaillir et perd " + Main.corriger(this.attaque + 2) + " points de vie.");
                    competence = Competence.AUCUNE;
                    encaissement = 0F;
                    part_soin = 0F;
                    return false;
                }
                competence = Competence.AUCUNE;
            }
            case REGARD_PETRIFIANT -> {
                System.out.println(this.nom + " regarde " + nom + " droit dans les yeux.");
                if(Input.D6() <= 4){
                    System.out.println(nom + "se change partiellement en pierre.");
                    System.out.println(nom + " perd définitivement 4 points de résistance et gagne définitivement 1 point de défense.");
                    competence = Competence.AUCUNE;
                    encaissement = 0F;
                    part_soin = 0F;
                    return false;
                }
                competence = Competence.AUCUNE;
            }
            case CHARGE -> {
                System.out.println(this.nom + " charge !");
                this.attaque += 3;
            }
            case VIOLENT -> {
                if(rand.nextBoolean()){
                    System.out.println(this.nom + " attaque violemment " + nom + " et lui inflige " + Main.corriger(attaque * 1.5F) + " dommages.");
                    return false;
                }
            }
            case ASSASSINAT -> {
                System.out.println(this.nom + " se glisse discrètement derrière " + nom + " sans que personne ne l'aperçoive.");
                this.attaque *= 2;
                this.encaissement = 0F;
            }
            case KAMICASE -> {
                System.out.println(this.nom + " explose !!!");
                this.attaque *= 3;
            }
            case AQUAJET -> {
                System.out.println(this.nom + " prépare quelque chose...");
                this.attaque += 18;
            }

            case VOLEUR_CASQUE -> {
                if (Input.yn(nom + " porte-iel un casque ?") && Input.D6() <= 4){
                    System.out.println(this.nom + " vole votre casque et part avec.");
                    Combat.stop_run();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Applique la compétence du monstre après son attaque
     * @throws IOException comme ça, ça marche
     */
    private void applique_competence_post(String nom) throws IOException {
        switch (competence) {
            case EXPLOSION -> {
                this.attaque -= 6;
                competence = Competence.AUCUNE;
            }
            case VAMPIRISME -> {
                if(Input.yn("L'attaque a-t-elle touchée ?") && this.vie < this.vie_max){
                    this.vie += 1;
                }
            }
            case VAMPIRISME4 -> {
                if(Input.yn("L'attaque a-t-elle touchée ?")){
                    this.vie += 4;
                    if(this.vie > this.vie_max){
                        this.vie = this.vie_max;
                    }
                }
            }
            case POISON_CECITE -> {
                if(Input.yn("L'attaque a-t-elle touchée ?")){
                    System.out.println(nom + " est empoisonné(e) et subit cécité pour le combat.");
                    competence = Competence.AUCUNE;
                }
            }
            case GEL -> competence = Competence.AUCUNE;
            case MORSURE_MALADIVE -> {
                System.out.println("La morsure provoque une grave infection qui fait définitivement perdre 1 point de résistance à " + nom + ".");
                competence = Competence.AUCUNE;
            }
            case MORSURE_SAUVAGE -> {
                System.out.println("La morsure infecte " + nom + " avec un parasite qui lui draine 1 PP.");
                competence = Competence.AUCUNE;
            }
            case MORSURE_EREINTANTE -> {
                System.out.println("La morsure provoque chez " + nom + " une grave réaction et lui fait perdre 1 point d'attaque définitivement.");
                competence = Competence.AUCUNE;
            }
            case POISON -> {
                if(Input.yn("L'attaque a-t-elle touchée ?")) {
                    System.out.println(nom + " est légèrement empoisonné(e).");
                    competence = Competence.A_POISON;
                }
            }
            case POISON2 -> {
                if(Input.yn("L'attaque a-t-elle touchée ?")) {
                    System.out.println(nom + " est empoisonné");
                    competence = Competence.A_POISON2;
                }
            }
            case CHARGE -> {
                this.attaque -= 3;
                competence = Competence.AUCUNE;
            }
            case ASSASSINAT -> {
                this.attaque /= 2;
                this.competence = Competence.AUCUNE;
            }
            case AQUAJET3 -> this.competence = Competence.AQUAJET2;
            case AQUAJET2 -> this.competence = Competence.AQUAJET1;
            case AQUAJET1 -> this.competence = Competence.AQUAJET;
            case AQUAJET -> {
                this.competence = Competence.AQUAJET3;
                this.attaque -= 18;
            }
            case FRAPPE_SPECTRALE -> System.out.println("L'attaque traverse partiellement l'armure de " + nom + " et ignore " + rand.nextInt(4) + " point(s) de défense.");
            case KAMICASE -> this.vie = 0;
            case CHRONOS -> {
                if(Input.yn("L'attaque a-t-elle touchée ?")){
                    System.out.println(nom + " perd définitivement 1 équipement de son choix.");
                }
            }
        }
    }

    /**
     * Gère les compétences intervenant après l'attaque
     */
    private void fin_combat(String nom) throws IOException {
        reset_encaisser();
        this.part_soin = 0F;
        switch (competence){
            case POURRI -> {
                System.out.println(this.nom + " tombe en morceau.");
                vie -= 1;
            }
            case PHOTOSYNTHESE -> vie = vie == vie_max ? vie + 1 : vie;
            case A_POISON -> System.out.println("La victime du poison subit " + rand.nextInt(3) + " dommage(s).");
            case A_POISON2 -> System.out.println("La victime du poison subit " + rand.nextInt(4) + 1 + " dommage(s).");
            case BLESSE -> {
                System.out.println(this.nom + " saigne abondamment.");
                vie -= 3;
            }
            case DUO -> {
                competence = Competence.AUCUNE; // pour éviter une boucle
                attaque(nom);
                competence = Competence.DUO;
            }
            case VOL_OFF -> competence = Competence.VOL_OFF2;
            case VOL_OFF2 -> {
                competence = Competence.VOL;
                System.out.println(this.nom + " s'envole !");
            }
        }
    }

    /**
     * Renvoie la quantité et qualité des équipements obtenus à la mort du monstre
     */
    private void drop() throws IOException {
        System.out.println("Vous fouillez le corp de " + this.nom);
        if(this.drop_quantite_max <= 0 || competence == Competence.ARNAQUE) {
            System.out.println("Vous ne trouvez aucun équipement sur son cadavre");
            return;
        }
        switch(this.competence) {
            case SACRE -> {
                System.out.println("Vous avez tué un(e) " + this.nom + ", aimé(e) des dieux, honte sur vous !");
                System.out.println("Vous (tous ceux présent) perdez définitivement 1 point de résistance.");
                return;
            }
            case PERLE -> System.out.println("Vous avez trouvé une perle (5PO) dans le corps de " + this.nom + " !");
            case DETESTE -> {
                System.out.println("Vous avez tué un(e) " + this.nom + ", haïe des dieux, gloire à vous !");
                System.out.println("Vous (tous ceux présent) gagnez définitivement 1 point de résistance.");
            }
            case FOLIE_MEURTRIERE -> {
                System.out.println("Le corp de " + this.nom + " s'agite !");
                System.out.println(this.nom + " vous (pl) inflige" + this.attaque + " dommages.");
            }
            case DUO_PASSED -> {
                competence = Competence.AUCUNE;
                System.out.println("Vous vous dirigez vers le deuxième " + nom);
                drop();
            }
        }
        Random rand = new Random();
        int q_drop = this.drop_quantite_max;
        for(int i = 0; i < q_drop; i++) {
            int temp = this.niveau_drop_min + rand.nextInt(this.niveau_drop_max - this.niveau_drop_min + 1);
            switch(temp){
                case 0 -> Equipement.drop_0();
                case 1 -> Equipement.drop_1();
                case 2 -> Equipement.drop_2();
                case 3 -> Equipement.drop_3();
                case 4 -> Equipement.drop_4();
                case 5 -> Equipement.drop_promo();
                default -> System.out.println("Vous n'avez pas la moindre idée de ce que vous venez de trouver.");
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
    public void tir(int quantite) throws IOException {
        if(quantite <= 0){
            return;
        }
        int degat = applique_competence_tir(max(quantite - this.armure, 1));
        this.vie -= degat;
        this.etat -= 1;
    }

    /**
     * Inflige des dommages à distance au monstre
     * @param quantite la puissance d'attaque
     * @param mult un multiplicateur à appliquer à la quantité
     * @implNote Considère l'armure et la compétence du monstre
     * gère le cas de mort du monstre
     */
    public void tir(int quantite, float mult) throws IOException {
        if(quantite <= 0){
            return;
        }
        tir(Main.corriger(quantite * mult));
    }

    /**
     * Applique la compétence avant de subir des dommages à distance
     * @param degas les dommages infligés par l'attaque
     * @return les dégas subits par le monstre
     */
    private int applique_competence_tir(int degas) throws IOException {

        switch (competence){
            case FRAGILE -> degas += 1;
            case ESPRIT -> {
                System.out.println("Votre projectile passe au travers de " + nom + " sans l'affecter.");
                competence = Competence.AUCUNE;
                degas = 0;
            }
            case RAPIDE -> {
                System.out.println(nom + " esquive partiellement votre projectile.");
                degas = Main.corriger((float) degas / 2);
            }
            case FLAMME_DEFENSE -> {
                System.out.println("Votre projectile brûle en s'approchant de " + nom + ".");
                competence = Competence.AUCUNE;
                degas = 0;
            }
            case ESQUIVE -> {
                if(rand.nextInt(10) == 0){
                    System.out.println(nom + " esquive votre projectile.");
                    degas = 0;
                }
            }
            case FURTIF -> {
                System.out.println("Vous ne parvenez pas à repérer où se trouve " + nom + ".");
                competence = Competence.AUCUNE;
                degas = 0;
            }
            case PEAU_DURE, GOLEM_PIERRE, CHRONOS -> {
                System.out.println("La peau dure de " + nom + " amortie une partie de l'impact");
                degas = Main.corriger((float) (degas * 0.85));
            }
            case CHANT_SIRENE -> {
                System.out.println("Le chant de " + nom + " perturbe le tir.");
                if(Input.D8() <= 2){
                    degas = degas > 4 ? degas - 4 : 0;
                }
            }
            case PEAU_DACIER, GOLEM_ACIER, GOLEM_FER -> {
                System.out.println("La peau extrêmement dure de " + this.nom + " absorbe l'essentiel de l'impact.");
                degas = Main.corriger((float) degas / 20);
            }
            case INTANGIBLE -> {
                System.out.println("Votre projectile passe au travers de " + nom + " sans l'affecter.");
                degas = 0;
            }
            case GOLEM_MITHRIL -> {
                System.out.println("La peau particulièrement solide de " + this.nom + " réduit l'impact.");
                degas = Main.corriger((float) degas * 0.75F);
            }
        }
        return degas;
    }

    /**
     * Inflige des dommages magiques au monstre
     * @param quantite la puissance d'attaque
     * @implNote Considère l'armure et la compétence du monstre
     * gère le cas de mort du monstre
     */
    public void dommage_magique(int quantite) throws IOException {
        if(quantite <= 0){
            return;
        }
        int degas = applique_competence_magie(quantite);
        this.vie -= degas;
        this.etat -= 1;
    }

    /**
     * Applique la compétence avant de subir des dommages magiques
     * @param degas les dommages infligés par l'attaque
     * @return les dégas subits par le monstre
     */
    private int applique_competence_magie(int degas) throws IOException {
        switch (competence){
            case FRAGILE -> degas += 1;
            case ESPRIT -> {
                System.out.println("Votre magie passe au travers de " + nom + " sans l'affecter.");
                competence = Competence.AUCUNE;
                degas = 0;
            }
            case SPELL_IMMUNE, CHRONOS -> {
                System.out.println("Votre magie n'a aucun effet sur " + nom + ".");
                degas = 0;
            }
            case PARTIELLE_SPELL_IMMUNIE -> {
                System.out.println("Votre magie semble sans effet sur " + nom + ".");
                competence = Competence.AUCUNE;
                degas = 0;
            }
            case FURTIF -> {
                System.out.println("Vous ne parvenez plus à identifier où se trouve " + nom + " et renoncez à utiliser votre magie.");
                competence = Competence.AUCUNE;
                degas = 0;
            }
            case PEAU_MAGIQUE -> {
                System.out.println("La peau de " + nom + " diminue l'impact du sort.");
                degas = Main.corriger((float) degas / 2);
            }
            case CUIR_MAGIQUE -> {
                System.out.println("Le cuir de " + nom + " absorbe l'essentiel de l'impact du sort.");
                degas = Main.corriger((float) degas / 10);
            }
            case CHANT_SIRENE -> {
                System.out.println("Le chant de " + nom + " perturbe le lancement du sort.");
                if(Input.D8() <= 3){
                    degas = degas > 4 ? degas - 4 : 0;
                }
            }
            case PEAU_DACIER -> {
                System.out.println("La peau extrêmement dure de " + this.nom + " absorbe une partie de l'impact du sort.");
                degas = Main.corriger((float) degas / 2);
            }
            case GOLEM_PIERRE, GOLEM_FER, GOLEM_ACIER -> {
                System.out.println("La constitution particulière de " + this.nom + " diminue légèrement l'impact du sort.");
                degas = Main.corriger((float)degas * 0.9F);
            }
            case GOLEM_MITHRIL -> {
                System.out.println("La matériaux particulier composant " + this.nom + " réduise immensément l'impact du sort.");
                degas = Main.corriger((float)degas / 20);
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
            switch(competence) {
                case ILLU_AURAI, ILLU_CYCLOPE, ILLU_DULLA, ILLU_GOLEM, ILLU_ROCHE, ILLU_SIRENE, ILLU_TRITON,
                     ILLU_VENTI -> {
                    System.out.println(this.nom + " se dissipe ! Tout celà n'était qu'une illusion !");
                    this.nom = "illusioniste";
                    this.competence = Competence.AUCUNE;
                }
                case DUO -> {
                    System.out.println("Un des " + this.nom + " est mort(e).");
                    this.competence = Competence.DUO_PASSED;
                }
                default -> System.out.println(this.nom + " est mort(e).");
            }
            drop();
            etat += vie; //on retire les dégats en trop
            Joueur.monstre_mort(this);

            if (etat <= 0 || pos == Position.ENFERS || pos == Position.OLYMPE || pos == Position.ASCENDANT) {
                return true;
            }
            System.out.println("Vous pouvez vendre le cadavre de " + nom + " pour " + (1 + (etat - 1) / 10) + " PO.");
            return false;
        }
        return true;
    }


    /**
     * Inflige des dommages au monstre
     * @param quantite la puissance d'attaque
     * @implNote Considère l'armure et la compétence du monstre
     * gère le cas de mort du monstre
     */
    public void dommage(int quantite) throws IOException {
        if(quantite <= 0){
            return;
        }
        int degas = applique_competence_dommage(max(quantite - this.armure, 1));
        this.vie -= degas;
        this.etat -= 1;
        if(!est_mort()) {
            applique_competence_post_dommage();
        }
    }

    /**
     * Inflige des dommages au monstre
     * @param quantite la puissance d'attaque
     * @param mult par combien on multiplie les dommages d'entrée
     * gère le cas de mort du monstre
     * @throws IOException toujours
     */
    public void dommage(int quantite, float mult) throws IOException {
        if(quantite <= 0){
            return;
        }
        int degas = applique_competence_dommage((Main.corriger(quantite * mult) - this.armure));
        this.vie -= degas;
        this.etat -= 1;
        if(!est_mort()) {
            applique_competence_post_dommage();
        }
        System.out.println();
    }

    /**
     * Applique la compétence avant de subir des dommages classique
     * @param degas les dommages infligés par l'attaque
     * @return les degas subits par le monstre
     */
    private int applique_competence_dommage(int degas) throws IOException {
        switch (competence) {
            case FRAGILE -> degas += 1;
            case VOL -> {
                System.out.println("L'attaque n'atteint pas " + this.nom + ".");
                competence = Competence.VOL_OFF;
                System.out.println(this.nom + " se pose à terre.");
                degas = 0;
            }
            case VOLAGE -> {
                System.out.println("L'attaque n'atteint pas " + this.nom + ".");
                competence = Competence.AUCUNE;
                System.out.println(this.nom + " se pose à terre.");
                degas = 0;
            }
            case ESPRIT -> {
                System.out.println("Votre attaque traverse " + nom + " sans l'affecter.");
                competence = Competence.AUCUNE;
                degas = 0;
            }
            case ESQUIVE -> {
                if(rand.nextInt(15) == 0){
                    System.out.println(nom + " esquive votre attaque.");
                    degas = 0;
                }
            }
            case FURTIF -> {
                System.out.println("Vous ne parvenez plus à identifier où se trouve " + nom + ".");
                competence = Competence.AUCUNE;
                degas = 0;
            }
            case PEAU_DURE, GOLEM_PIERRE, GOLEM_FER, CHRONOS -> {
                System.out.println("La peau dure de " + this.nom + " amortie une partie de l'assaut");
                degas = Main.corriger((float) degas * 0.9F);
            }
            case CHANT_SIRENE -> {
                System.out.println("Le chant de " + nom + " perturbe le lancement du sort.");
                if(Input.D8() <= 1){
                    degas = degas > 4 ? degas - 4 : 0;
                }
            }
            case PEAU_DACIER, GOLEM_ACIER -> {
                System.out.println("La peau extrêmement dure de " + this.nom + " absorbe l'essentiel de l'assaut.");
                degas = Main.corriger((float) degas / 10);
            }
            case GOLEM_MITHRIL -> {
                System.out.println("La peau particulièrement solide de " + this.nom + " absorbe une grande partie de l'assaut.");
                degas = Main.corriger((float) degas / 2);
            }
        }
        return degas;
    }

    /**
     * Applique la compétence après avoir subi des dommages classiques
     */
    private void applique_competence_post_dommage() {
        switch (competence) {
            case ARMURE_GLACE -> System.out.println("L'armure de glace de " + this.nom + " vous inflige 1 dommage.");
            case ARMURE_GLACE2 -> System.out.println("L'armure de glace de " + this.nom + " vous inflige 3 dommages.");
            case ARMURE_FEU -> System.out.println("Les flammes de " + nom + " vous inflige 1 dommage.");
            case ARMURE_FOUDRE -> System.out.println("La foudre entourant " + nom + " vous inflige 3 dommages.");
            case ILLU_AURAI, ILLU_CYCLOPE, ILLU_DULLA, ILLU_GOLEM, ILLU_ROCHE, ILLU_SIRENE, ILLU_TRITON,
                 ILLU_VENTI -> {
                if (this.vie <= 4){
                    this.nom = "illusioniste";
                    System.out.println(this.nom + " se révèle ! Tout celà n'était qu'une illusion !");
                    this.competence = Competence.AUCUNE;
                }
            }
        }
    }

    /**
     * Règle l'état du monstre sur "assommé" ou "étourdit"
     */
    public void affecte() {
        Random rand = new Random();
        if (rand.nextBoolean()){
            do_assomme();
        }
        else{
            do_etourdi();
        }
    }

    /**
     * Règle l'état du monstre à "assommé"
     */
    public void do_assomme() {
        switch (competence){
            case GOLEM_ACIER, GOLEM_MITHRIL -> {
                System.out.println(nom + " n'a pas de conscience, et ne peut pas être assommé(e).");
                return;
            }
            case GOLEM_PIERRE, GOLEM_FER -> {
                System.out.println(nom + " n'a pas de conscience, et ne peut pas être assommé(e).");
                System.out.println(nom + " cependant, est déséquilibré(e).");
                do_etourdi();
                return;
            }
            case CHRONOS -> {
                System.out.println(nom + " n'a pas l'air prêt de perdre connaissance.");
                return;
            }
        }
        this.assomme = true;
        System.out.println(this.nom + " est assommé(e).");
    }

    /**
     * Règle l'état du monstre à "étourdi"
     */
    public void do_etourdi() {

        switch (competence) {
            case GOLEM_ACIER, GOLEM_MITHRIL -> {
                System.out.println(nom + " est trop solide pour être étourdi(e).");
                return;
            }
            case GOLEM_PIERRE, GOLEM_FER -> {
                if(rand.nextBoolean()){
                    System.out.println(nom + " laisse tomber des fragments de son corps pour ne pas être désavantagé(e).");
                    this.vie -= rand.nextInt(5) + 1;
                    if (est_mort()){
                        return;
                    }
                }
            }
            case CHRONOS -> {
                System.out.println(nom + " n'a pas l'air prêt de perdre connaissance.");
                return;
            }
        }
        if(!assomme){
            this.etourdi = true;
            System.out.println(this.nom + " est étourdi(e).");
        }
    }

    /**
     * Retire l'état "assommé" du monstre
     * Peut infliger l'état "étourdi"
     */
    void undo_assomme(){
        if (est_mort()){
            return;
        }
        this.assomme = false;
        System.out.print(this.nom + " se réveille ");
        Random rand = new Random();
        if (rand.nextBoolean()){
            this.etourdi = true;
            System.out.println("encore étourdi(e).");
        }
        else{
            this.etourdi = false;
            System.out.println("en pleine possession de ses moyens.");
        }
    }

    /**
     * Retire l'état "étourdi" au monstre
     */
    void undo_etourdi(){
        if (est_mort()){
            return;
        }
        this.etourdi = false;
        System.out.println(this.nom + " n'est plus étourdi(e).");
    }

    /**
     * Renvoie si le monstre est mort
     * @return si le monstre est mort
     */
    public boolean est_mort(){
        if(competence == Competence.REVENANT){
            System.out.println("Une sombre brûme s'abat sur vous, vous perdez (tous) 1 point d'attaque pour la durée du combat.");
            System.out.println(this.nom + " se relève !");
            this.vie = rand.nextInt(this.vie_max - 5) + 5;
            this.competence = Competence.AUCUNE;
            return false;
        }
        return vie <= 0;
    }

    /**
     * Applique la compétence "encaisser" et ses résultats
     * Demande aux joueurs les informations nécessaires
     * @throws IOException jsp mais sans ça, ça ne marche pas
     * @implNote On considère que le joueur qui utilise encaisser est en première ligne
     */
    public void encaisser() throws IOException {
        int attaque = Input.atk();
        switch (Input.D6()) {
            case 1:
                encaissement = 0.5F;
                System.out.println("Vous vous préparer à encaisser en oubliant d'attaquer !");
                break;
            case 2, 3, 4:
                attaque = Main.corriger(attaque * 0.1f);
                encaissement = 0.5F;
                System.out.println("Vous vous préparez à encaisser.");
                dommage(attaque);
                break;
            case 5:
                attaque = Main.corriger(attaque * 0.5f);
                dommage(attaque);
                encaissement = 0.65F;
                System.out.println("Vous vous préparez à encaisser.");
                break;
            case 6, 7:
                attaque = Main.corriger(attaque * 0.5f);
                dommage(attaque);
                encaissement = 0.9F;
                System.out.println("Vous vous préparez fermement à encaisser, solide comme un roc.");
                break;
            default:
                System.out.println("Le résultat n'a pas été comprit, attaque classique appliquée.");
                dommage(attaque);
        }
    }

    public void f_encaisser() throws IOException {
        int attaque = Input.atk();
        switch (Input.D4()) {
            case 1:
                encaissement = 0.3F;
                System.out.println("Votre familier se prépare à encaisser en oubliant d'attaquer !");
                break;
            case 2:
                attaque = Main.corriger(attaque * 0.1f);
                encaissement = 0.3F;
                System.out.println("Votre familier se prépare à encaisser.");
                dommage(attaque);
                break;
            case 3:
                attaque = Main.corriger(attaque * 0.2f);
                encaissement = 0.5F;
                System.out.println("Votre familier se prépare à encaisser.");
                dommage(attaque);
                break;
            case 4, 5:
                attaque = Main.corriger(attaque * 0.4f);
                dommage(attaque);
                encaissement = 0.7F;
                System.out.println("Votre familier se prépare solidement à encaisser.");
                break;
            default:
                System.out.println("Le résultat n'a pas été comprit, attaque classique appliquée.");
                dommage(attaque);
        }
    }


    /**
     * Remet à zéro l'encaissement de l'ennemi
     */
    public void reset_encaisser(){
        encaissement = 0;
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
                    part_soin += 0.1F;
                }
            }
            case 5 -> {
                System.out.println("Vous soignez la cible de 7.");
                if (premiere_ligne){
                    System.out.println("Vous vous exposez légèrement.");
                    part_soin += 0.1F;
                }
            }
            case 4, 3, 2 -> {
                System.out.println("Vous soignez la cible de " + (2 + soin) + ".");
                if (premiere_ligne) {
                    System.out.println("Vous vous exposez.");
                    part_soin += 0.5F;
                }
            }
            case 1 -> {
                System.out.println("Vous soignez la cible de 2.");
                if (premiere_ligne) {
                    System.out.println("Vous vous exposez lourdement.");
                    part_soin += 1F;
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
     * @throws IOException jsp mais sans ça, ça ne marche pas
     * @return si le monstre est domestiqué
     */
    public Boolean domestiquer() throws IOException {
        switch (competence){
            case COLERE, VIOLENT -> {
                if(Input.D8() <= this.vie){
                    System.out.println(this.nom + " réagit très agressivement.");
                    return false;
                }
            }
            case SAUVAGE -> {
                System.out.println(this.nom + " est trop sauvage pour être domestiqué.");
                return false;
            }
            case PRUDENT, SUSPICIEUX, MEFIANT, CHRONOS -> {  //monstre nommé
                System.out.println(this.nom + " est une force de la nature, une puissance indomptable.");
                return false;
            }
            case GOLEM_PIERRE, GOLEM_FER -> {
                if(Input.D8() <= this.vie * 2) {
                    System.out.println(this.nom + " n'est pas très réceptif à votre tentative.");
                    return false;
                }
            }
            case GOLEM_ACIER -> {
                if(Input.D6() <= this.vie * 2) {
                    System.out.println(this.nom + " n'est pas très réceptif à votre tentative.");
                    return false;
                }
            }
            case GOLEM_MITHRIL -> {
                if(Input.D8() <= this.vie * 2) {
                    System.out.println(this.nom + " ne remarque même pas votre présence.");
                    return false;
                }
                else {
                    System.out.println(this.nom + " vous remarque.");
                    if(Input.D20() <= this.vie){
                        System.out.println(this.nom + " ne vous accorde aucune importance.");
                        return false;
                    }
                    else{
                        System.out.println(this.nom + " vous accorde son attention.");
                    }
                }
            }
            case ILLU_AURAI, ILLU_CYCLOPE, ILLU_DULLA, ILLU_GOLEM, ILLU_ROCHE, ILLU_SIRENE, ILLU_TRITON,
                 ILLU_VENTI -> {
                System.out.println(this.nom + " réagit très étrangement à votre tentative.");
                return false;
            }
        }
        int ratio = (this.vie * 100 / this.vie_max);
        if (ratio >= 85) {
            System.out.println(this.nom + " réagit agressivement.");
            return false;
        }
        Random rand = new Random();
        if (ratio >= 75 && rand.nextInt(100) > ratio) {
            System.out.println(this.nom + " semble intrigué(e) par votre comportement.");
            ratio -= 100 - ratio;
        }
        if (ratio >= 50 && rand.nextInt(100) > ratio) {
            int temp = Input.D4();
            if (temp > 2){
                System.out.println(this.nom + " semble réagir positivement à votre approche.");
                ratio = min(50, ratio - 10 * temp);
            }
            else{

                System.out.println(this.nom + " réagit agressivement.");
                return false;
            }
        }
        if (ratio - Input.D6() * 10 < 0){
            System.out.println(this.nom + " vous accorde sa confiance.");
            return true;
        }
        System.out.println(this.nom + " réagit agressivement.");
        return false;
    }

    /**
     * Applique la compétence "assommer" sur le monstre
     * Demande aux joueurs les informations nécessaires
     * @throws IOException jsp mais sans ça, ça ne marche pas
     */
    public void assommer(float bonus) throws IOException {
        // compétence ennemie
        switch (getCompetence()) {
            case VOL -> {
                System.out.println("L'attaque n'atteint pas " + nom + ".");
                competence = Competence.VOL_OFF;
                System.out.println(nom + " se pose à terre.");
                return;
            }
            case VOLAGE -> {
                System.out.println("L'attaque n'atteint pas " + nom + ".");
                competence = Competence.AUCUNE;
                System.out.println(nom + " se pose à terre.");
                return;
            }
            case FURTIF -> {
                System.out.println("Vous ne parvenez plus à identifier où se trouve " + nom + " et renoncez à attaquer.");
                competence = Competence.AUCUNE;
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
                affecte();
                break;
            case 3, 4 :
                attaque = Main.corriger((float) attaque / 2);
                affecte();
                break;
            case 5:
                attaque = Main.corriger((float) attaque / 2);
                do_assomme();
                break;
            case 6:
                System.out.println("Vous frappez avec force !");
                do_assomme();
                break;
            case 7:
                System.out.println("Vous frappez à vous en blesser les bras !");
                if(rand.nextBoolean()){
                    System.out.println("Vous subissez 1 point de dommage");
                }
                do_assomme();
                attaque = Main.corriger(attaque * bonus);

            default:
                System.out.println("Le résultat n'a pas été comprit, attaque classique appliquée.");
        }
        dommage(attaque);
    }

    /**
     * Affiche les statistiques de base du monstre
     */
    public void presente_familier() {
        System.out.println("nouveau familier : " + this.nom);
        System.out.println("attaque : " + this.attaque_base);
        System.out.println("vie : " + this.vie_base);
        System.out.println("armure : " + this.armure_base + "\n");
    }
}
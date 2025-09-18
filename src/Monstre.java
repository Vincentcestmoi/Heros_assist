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

        if(Objects.equals(this.nom, "illusioniste")){
            illu_check();
        }
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

    static Input input = new Input();
    static Random rand = new Random();

    /**
     * Renvoie l'arrondit minoré par 1 de la valeur donnée
     * @param valeur le float à corriger
     */
    static int corriger(float valeur) {
        if (valeur % 1 == 0){
            return max((int)(valeur), 1);
        }
        return max((int)(valeur) + 1, 1);
    }


    /**
     * Écris la quantité de dommage infligé par le monstre à l'adversaire
     * @param nom le nom de l'entité attaqué
     * @implNote Calcul les effet de "encaisser", "étourdit" et "assommé"
     */
    void attaque(String nom) throws IOException {
        float modificateur = 1 - encaissement + part_soin;
        if (assomme) {
            undo_assomme();
        }
        else if (etourdi){
            if(applique_competence_pre(nom) && this.attaque > 0){
                System.out.println(this.nom + " est étourdit et inflige " + corriger(((float) this.attaque / 2) * modificateur) + " dommages à " + nom + ".");
                applique_competence_post(nom);
            }
            undo_etourdi();
        }
        else{
            if(applique_competence_pre(nom) && this.attaque > 0){
                System.out.println(this.nom + " inflige " + corriger(this.attaque * modificateur) + " dommages à " + nom + ".");
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
                if(input.yn("Portez vous (pl) une armure ?")){
                    System.out.println(this.nom + " détruit votre armure");
                    competence = Competence.AUCUNE;
                }
                else{
                    attaque += 3;
                }
            }
            case REGARD_MORTEL -> {
                System.out.println(this.nom + " regarde " + nom + " droit dans les yeux.");
                if(input.D6() <= 4){
                    System.out.println(nom + " sent son âme se faire assaillir et perd " + corriger(this.attaque + 2) + " points de vie.");
                    competence = Competence.AUCUNE;
                    encaissement = 0F;
                    part_soin = 0F;
                    return false;
                }
                competence = Competence.AUCUNE;
            }
            case REGARD_PETRIFIANT -> {
                System.out.println(this.nom + " regarde " + nom + " droit dans les yeux.");
                if(input.D6() <= 4){
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
                    System.out.println(this.nom + " attaque violemment " + nom + " et lui inflige " + corriger((float) (attaque * 1.5)) + " dommages.");
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
                if (input.yn(nom + " porte-iel un casque ?") && input.D6() <= 4){
                    System.out.println(this.nom + " vole votre casque et part avec.");
                    this.competence = Competence.CASQUE_VOLE;
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
                if(input.yn("L'attaque a-t-elle touchée ?") && this.vie < this.vie_max){
                    this.vie += 1;
                }
            }
            case VAMPIRISME4 -> {
                if(input.yn("L'attaque a-t-elle touchée ?") && this.vie < this.vie_max){
                    this.vie += 4;
                }
            }
            case POISON_CECITE -> {
                if(input.yn("L'attaque a-t-elle touchée ?") && this.vie < this.vie_max){
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
                System.out.println(nom + " est légèrement empoisonné(e).");
                competence = Competence.A_POISON;
            }
            case POISON2 -> {
                System.out.println(nom + " est empoisonné");
                competence = Competence.A_POISON2;
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
            case KAMICASE, CASQUE_VOLE -> this.vie = 0;
            case CHRONOS -> {
                if(input.yn("L'attaque a-t-elle touchée ?")){
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
                check_mort();
            }
            case PHOTOSYNTHESE -> vie = vie == vie_max ? vie + 1 : vie;
            case A_POISON -> System.out.println("La victime du poison subit " + rand.nextInt(3) + " dommage(s).");
            case A_POISON2 -> System.out.println("La victime du poison subit " + rand.nextInt(4) + 1 + " dommage(s).");
            case BLESSE -> {
                System.out.println(this.nom + " saigne abondamment.");
                vie -= 3;
                check_mort();
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
    void drop() throws IOException {
        System.out.println("Vous fouillez le corp de " + this.nom);
        if(this.drop_quantite_max == 0 || competence == Competence.ARNAQUE) {
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
            case DUO -> {
                competence = Competence.AUCUNE;
                System.out.println("Vous vous dirigez vers le deuxième " + nom);
                drop();
            }
        }
        Random rand = new Random();
        int q_drop = rand.nextInt(this.drop_quantite_max) + 1;
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
    void tir(int quantite) throws IOException {
        if(quantite == 0){
            return;
        }
        System.out.println("Vous tirez sur " + this.nom);
        int degat = applique_competence_tir(max(quantite - this.armure, 1));
        this.vie -= degat;
        check_mort();
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
                degas = corriger((float) degas / 2);
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
                degas = corriger((float) (degas * 0.85));
            }
            case CHANT_SIRENE -> {
                System.out.println("Le chant de " + nom + " perturbe le tir.");
                if(input.D8() <= 2){
                    degas = degas > 4 ? degas - 4 : 0;
                }
            }
            case PEAU_DACIER, GOLEM_ACIER, GOLEM_FER -> {
                System.out.println("La peau extrêmement dure de " + this.nom + " absorbe l'essentiel de l'impact.");
                degas = corriger((float) degas / 20);
            }
            case INTANGIBLE -> {
                System.out.println("Votre projectile passe au travers de " + nom + " sans l'affecter.");
                degas = 0;
            }
            case GOLEM_MITHRIL -> {
                System.out.println("La peau particulièrement solide de " + this.nom + " réduit l'impact.");
                degas = corriger((float) degas * 0.75F);
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
    void dommage_magique(int quantite) throws IOException {
        if(quantite == 0){
            return;
        }
        System.out.println("Vous utilisez votre magie sur " + this.nom);
        int degas = applique_competence_magie(max(quantite, 1));
        this.vie -= degas;
        check_mort();
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
                degas = corriger((float) degas / 2);
            }
            case CUIR_MAGIQUE -> {
                System.out.println("Le cuir de " + nom + " absorbe l'essentiel de l'impact du sort.");
                degas = corriger((float) degas / 10);
            }
            case CHANT_SIRENE -> {
                System.out.println("Le chant de " + nom + " perturbe le lancement du sort.");
                if(input.D8() <= 3){
                    degas = degas > 4 ? degas - 4 : 0;
                }
            }
            case PEAU_DACIER -> {
                System.out.println("La peau extrêmement dure de " + this.nom + " absorbe une partie de l'impact du sort.");
                degas = corriger((float) degas / 2);
            }
            case GOLEM_PIERRE, GOLEM_FER, GOLEM_ACIER -> {
                System.out.println("La constitution particulière de " + this.nom + " diminue légèrement l'impact du sort.");
                degas = corriger((float)degas * 0.9F);
            }
            case GOLEM_MITHRIL -> {
                System.out.println("La matériaux particulier composant " + this.nom + " réduise immensément l'impact du sort.");
                degas = corriger((float)degas / 20);
            }
        }
        return degas;
    }

    /**
     * Regarde si le monstre est mort et agit en conséquence
     * @return si le monstre est mort
     */
    private boolean check_mort() throws IOException {
        if (est_mort()) {
            switch(competence) {
                case ILLU_AURAI, ILLU_CYCLOPE, ILLU_DULLA, ILLU_GOLEM, ILLU_ROCHE, ILLU_SIRENE, ILLU_TRITON,
                     ILLU_VENTI -> {
                    System.out.println(this.nom + " se dissipe ! Tout celà n'était qu'une illusion !");
                    this.nom = "illusioniste";
                    this.competence = Competence.AUCUNE;
                }
                default -> System.out.println(this.nom + " est mort(e).");
            }
            drop();
            return true;
        }
        return false;
    }


    /**
     * Inflige des dommages au monstre
     * @param quantite la puissance d'attaque
     * @implNote Considère l'armure et la compétence du monstre
     * gère le cas de mort du monstre
     */
    void dommage(int quantite) throws IOException {
        if(quantite == 0){
            return;
        }
        System.out.println("Vous attaquez " + this.nom);
        int degas = applique_competence_dommage(max(quantite - this.armure, 1));
        this.vie -= degas;
        if(!check_mort()) {
            applique_competence_post_dommage();
        }
        System.out.println();
    }

    /**
     * Inflige des dommages au monstre
     * @param quantite la puissance d'attaque
     * @implNote Considère l'armure et la compétence du monstre
     * gère le cas de mort du monstre
     */
    void dommage(int quantite, float mult) throws IOException {
        if(quantite == 0){
            return;
        }
        System.out.println("Vous attaquez " + this.nom);
        int degas = applique_competence_dommage((corriger(quantite * mult) - this.armure));
        this.vie -= degas;
        if(!check_mort()) {
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
                degas = corriger((float) degas * 0.9F);
            }
            case CHANT_SIRENE -> {
                System.out.println("Le chant de " + nom + " perturbe le lancement du sort.");
                if(input.D8() <= 1){
                    degas = degas > 4 ? degas - 4 : 0;
                }
            }
            case PEAU_DACIER, GOLEM_ACIER -> {
                System.out.println("La peau extrêmement dure de " + this.nom + " absorbe l'essentiel de l'assaut.");
                degas = corriger((float) degas / 10);
            }
            case GOLEM_MITHRIL -> {
                System.out.println("La peau particulièrement solide de " + this.nom + " absorbe une grande partie de l'assaut.");
                degas = corriger((float) degas / 2);
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
    void affecte() throws IOException {
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
    void do_assomme() throws IOException {
        switch (competence){
            case GOLEM_ACIER, GOLEM_MITHRIL -> {
                System.out.println(nom + " n'a pas de conscience, et ne peut pas être assommé(e).\n");
                return;
            }
            case GOLEM_PIERRE, GOLEM_FER -> {
                System.out.println(nom + " n'a pas de conscience, et ne peut pas être assommé(e).");
                System.out.println(nom + " cependant, est déséquilibré(e).");
                do_etourdi();
                return;
            }
            case CHRONOS -> {
                System.out.println(nom + " n'a pas l'air prêt de perdre connaissance.\n");
                return;
            }
        }
        this.assomme = true;
        System.out.println(this.nom + " est assommé(e).\n");
    }

    /**
     * Règle l'état du monstre à "étourdi"
     */
    void do_etourdi() throws IOException {

        switch (competence) {
            case GOLEM_ACIER, GOLEM_MITHRIL -> {
                System.out.println(nom + " est trop solide pour être étourdi(e).\n");
                return;
            }
            case GOLEM_PIERRE, GOLEM_FER -> {
                if(rand.nextBoolean()){
                    System.out.println(nom + " laisse tomber des fragments de son corps pour ne pas être désavantagé(e).\n");
                    this.vie -= rand.nextInt(5) + 1;
                    if (check_mort()){
                        return;
                    }
                }
            }
            case CHRONOS -> {
                System.out.println(nom + " n'a pas l'air prêt de perdre connaissance.\n");
                return;
            }
        }
        if(!assomme){
            this.etourdi = true;
            System.out.println(this.nom + " est étourdi(e).\n");
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
        System.out.println(this.nom + " se réveille.\n");
        Random rand = new Random();
        if (rand.nextBoolean()){
            this.etourdi = true;
            System.out.println("encore étourdi(e).\n");
        }
        else{
            this.etourdi = false;
            System.out.println("en pleine possession de ses moyens.\n");
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
        System.out.println(this.nom + " n'est plus étourdi(e).\n");
    }

    /**
     * Renvoie si le monstre est mort
     * @return si le monstre est mort
     */
    boolean est_mort(){
        if(competence == Competence.REVENANT){
            System.out.println("Une sombre brûme s'abat sur vous, vous perdez (tous) 1 point d'attaque pour la durée du combat.");
            System.out.println(this.nom + " se relève !\n");
            this.vie = rand.nextInt(this.vie_max - 5) + 5;
            this.competence = Competence.AUCUNE;
            return false;
        }
        return vie <= 0;
    }

    /**
     * Applique la compétence "assommer" sur le monstre
     * Demande aux joueurs les informations nécessaires
     * @throws IOException jsp mais sans ça, ça ne marche pas
     */
    void assommer() throws IOException {
        switch (competence) {
            case VOL -> {
                System.out.println("L'attaque n'atteint pas " + this.nom + ".");
                competence = Competence.VOL_OFF;
                System.out.println(this.nom + " se pose à terre.\n");
                return;
            }
            case VOLAGE -> {
                System.out.println("L'attaque n'atteint pas " + this.nom + ".");
                competence = Competence.AUCUNE;
                System.out.println(this.nom + " se pose à terre.\n");
                return;
            }
            case FURTIF -> {
                System.out.println("Vous ne parvenez plus à identifier où se trouve " + nom + " et renoncez à attaquer.\n");
                competence = Competence.AUCUNE;
                return;
            }
            default -> {
            }

        }
        int attaque = input.atk();
        switch (input.D6()) {
            case 1:
                System.out.print("Vous manquez votre cible.\n");
                return;
            case 2:
                System.out.print("Vous frappez de justesse votre cible, au moins, vous l'avez touchée.");
                affecte();
                break;
            case 3, 4 :
                attaque = corriger((float) attaque / 2);
                dommage(attaque);
                affecte();
                break;
            case 5:
                attaque = corriger((float) attaque / 2);
                dommage(attaque);
                do_assomme();
                break;
            case 6:
                System.out.println("Vous frappez avec force !");
                dommage(attaque);
                do_assomme();
                break;
            default:
                System.out.println("Le résultat n'a pas été comprit, attaque classique appliquée.");
                dommage(attaque);
        }
    }

    /**
     * Applique la compétence "encaisser" et ses résultats
     * Demande aux joueurs les informations nécessaires
     * @throws IOException jsp mais sans ça, ça ne marche pas
     * @implNote On considère que le joueur qui utilise encaisser est en première ligne
     */
    void encaisser() throws IOException {
        int attaque = input.atk();
        switch (input.D6()) {
            case 1:
                encaissement = 0.5F;
                System.out.println("Vous vous préparer à encaisser en oubliant d'attaquer !\n");
                break;
            case 2, 3, 4:
                attaque = corriger((float) attaque / 10);
                encaissement = 0.5F;
                System.out.println("Vous vous préparez à encaisser.");
                dommage(attaque);
                break;
            case 5:
                attaque = corriger((float) attaque / 2);
                dommage(attaque);
                encaissement = 0.5F;
                System.out.println("Vous vous préparez à encaisser.");
                dommage(attaque);
                break;
            case 6:
                attaque = corriger((float) attaque / 2);
                dommage(attaque);
                encaissement = 0.9F;
                System.out.println("Vous vous préparez fermement à encaisser, solide comme un roc.");
                break;
            default:
                System.out.println("Le résultat n'a pas été comprit, attaque classique appliquée.");
                dommage(attaque);
        }
    }

    /**
     * Remet à zéro l'encaissement de l'ennemi
     */
    void reset_encaisser(){
        encaissement = 0;
    }

    /**
     * Renvoie la quantité de soin appliqué par la compétence "guérison"
     * @param premiere_ligne si le lanceur ou la cible est en première ligne
     * @throws IOException jsp mais sans ça, ça ne marche pas
     */
    void soigner(Boolean premiere_ligne) throws IOException {
        int soin = input.D6();
        switch (soin) {
            case 6 -> {
                System.out.println("Vous soignez la cible de 9.");
                if (premiere_ligne) {
                    System.out.println("Vous vous exposez légèrement.");
                    part_soin += 0.1F;
                }
                System.out.println();
            }
            case 5 -> {
                System.out.println("Vous soignez la cible de 7.");
                if (premiere_ligne){
                    System.out.println("Vous vous exposez légèrement.");
                    part_soin += 0.1F;
                }
                System.out.println();
            }
            case 4, 3, 2 -> {
                System.out.println("Vous soignez la cible de " + (2 + soin) + ".");
                if (premiere_ligne) {
                    System.out.println("Vous vous exposez.");
                    part_soin += 0.5F;
                }
                System.out.println();
            }
            case 1 -> {
                System.out.println("Vous soignez la cible de 2.");
                if (premiere_ligne) {
                    System.out.println("Vous vous exposez lourdement.");
                    part_soin += 1F;
                }
                System.out.println();
            }
            default -> {
                System.out.println("Le résultat n'a pas été comprit, attaque classique appliquée.\n");
                dommage(input.atk());
            }
        }
    }

    /**
     * Renvoie le résultat de la compétence "domestiquer"
     * @throws IOException jsp mais sans ça, ça ne marche pas
     * @return si le monstre est domestiqué
     */
    Boolean domestiquer() throws IOException {
        switch (competence){
            case COLERE, VIOLENT -> {
                if(input.D8() <= this.vie){
                    System.out.println(this.nom + " réagit très agressivement.\n");
                    return false;
                }
            }
            case SAUVAGE -> {
                System.out.println(this.nom + " est trop sauvage pour être domestiqué.\n");
                return false;
            }
            case PRUDENT, SUSPICIEUX, MEFIANT, CHRONOS -> {  //monstre nommé
                System.out.println(this.nom + " est une force de la nature, une puissance indomptable.\n");
                return false;
            }
            case GOLEM_PIERRE, GOLEM_FER -> {
                if(input.D8() <= this.vie * 2) {
                    System.out.println(this.nom + " n'est pas très réceptif à votre tentative.\n");
                    return false;
                }
            }
            case GOLEM_ACIER -> {
                if(input.D6() <= this.vie * 2) {
                    System.out.println(this.nom + " n'est pas très réceptif à votre tentative.\n");
                    return false;
                }
            }
            case GOLEM_MITHRIL -> {
                if(input.D8() <= this.vie * 2) {
                    System.out.println(this.nom + " ne remarque même pas votre présence.\n");
                    return false;
                }
                else {
                    System.out.println(this.nom + " vous remarque.");
                    if(input.D20() <= this.vie){
                        System.out.println(this.nom + " ne vous accorde aucune importance.\n");
                        return false;
                    }
                    else{
                        System.out.println(this.nom + " vous accorde son attention.");
                    }
                }
            }
            case ILLU_AURAI, ILLU_CYCLOPE, ILLU_DULLA, ILLU_GOLEM, ILLU_ROCHE, ILLU_SIRENE, ILLU_TRITON,
                 ILLU_VENTI -> {
                System.out.println(this.nom + " réagit très étrangement à votre tentative.\n");
                return false;
            }
        }
        int ratio = (this.vie * 100 / this.vie_max);
        if (ratio >= 85) {
            System.out.println(this.nom + " réagit agressivement.\n");
            return false;
        }
        Random rand = new Random();
        if (ratio >= 75 && rand.nextInt(100) > ratio) {
            System.out.println(this.nom + " semble intrigué(e) par votre comportement.");
            ratio -= 100 - ratio;
        }
        if (ratio >= 50 && rand.nextInt(100) > ratio) {
            int temp = input.D4();
            if (temp > 2){
                System.out.println(this.nom + " semble réagir positivement à votre approche.");
                ratio = min(50, ratio - 10 * temp);
            }
            else{

                System.out.println(this.nom + " réagit agressivement.\n");
                return false;
            }
        }
        if (ratio - input.D6() * 10 < 0){
            System.out.println(this.nom + " vous accorde sa confiance.\n");
            return true;
        }
        System.out.println(this.nom + " réagit agressivement.\n");
        return false;
    }

    /**
     * Définie si la compétence "entrainement" fonctionne ou non
     * @return si le niveau d'obéissance du familier a augmenté :
     * 2 oui ;
     * 1 oui ;
     * 0 non ;
     * -1 rebellion potentielle ;
     * @throws IOException jsp mais sans ça, ça ne marche pas
     */
    static int entrainement() throws IOException {
        return switch (input.D6()) {
            case 1 -> {
                if (input.D4() <= 2) {
                    System.out.println("Votre familier désapprouve fortement vos méthodes d'entrainements.\n");
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
                System.out.println("Votre familier vous respecter un peu plus.\n");
                yield 1;
            }
            case 6 -> {
                if (input.D4() >= 3) {
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
}
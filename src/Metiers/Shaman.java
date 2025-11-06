package Metiers;

import Enum.*;
import Exterieur.Input;
import Monstre.Monstre;
import main.Combat;
import main.Main;

import java.io.IOException;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Shaman extends Joueur {
    Metier metier = Metier.SHAMAN;
    private int possession_atk;
    
    public Shaman(String nom, Position position, int ob_f, Dieux parent, int xp) {
        super(nom, position, ob_f, parent, xp);
        vie = 3;
        attaque = 1;
        PP = "mana";
        PP_value = 0;
        PP_max = 0;
        add_caracteristique("Ame errante");
        add_competence("Incantation");
        possession_atk = 0;
    }
    
    @Override
    protected void actualiser_niveau() {
        if (this.niveau >= 2) {
            add_competence("Lien");
            this.vie += 1;
        }
        if (this.niveau >= 3) {
            add_competence("Paix intérieure");
        }
        if (this.niveau >= 4) {
            this.attaque += 1;
        }
        if (this.niveau >= 5) {
            add_caracteristique("Second souffle");
        }
        if (this.niveau >= 7) {
            add_caracteristique("Eclaireur");
        }
        if (this.niveau >= 8) {
            this.armure += 1;
        }
        if (this.niveau >= 10) {
            this.vie += 1;
            this.attaque += 1;
        }
        this.attaque += bonus_sup10(11, 10) + bonus_sup10(18, 10);
        this.vie += bonus_sup10(13, 10) + bonus_sup10(16, 10) + bonus_sup10(19, 10) + bonus_sup10(20, 10);
        this.armure += bonus_sup10(12, 10);
    }
    
    @Override
    protected void presente_caracteristique() {
        System.out.println("Ame errante : peut incanter même inconscient.");
        if (this.niveau >= 5) {
            System.out.println("Second souffle : Permet rarement de tromper la mort.");
        }
        if (this.niveau >= 7) {
            System.out.println("Eclaireur : Augmente légèrement les dé d'exploration.");
        }
    }
    
    @Override
    protected void presente_pouvoir() {
        System.out.println("Incantation : lance de mystérieuses incantations invoquant les forces de la nature et " + "les" + " esprits de ses ancêtres.");
        if (this.niveau >= 2) {
            System.out.println("Lien : Projette son âme dans celle d'un monstre pour tenter de les lier de force. Un "
                    + "monstre" + " en bonne santé aura une âme puissante, alors que l'âme d'un monstre blessé est " + "plus" + " faible.");
        }
        if (this.niveau >= 3) {
            System.out.println("Paix intérieure : Regagne instantanément sa santé mentale et son calme.");
        }
    }
    
    public Metier getMetier() {
        return metier;
    }
    
    protected String nomMetier() {
        return "shaman";
    }
    
    @Override
    void lvl_up() {
        int temp = this.niveau;
        if (temp < 0) {
            temp = 0;
        }
        if (temp > 11) {
            temp = 11;
        }
        String text = switch (temp) {
            case 0 -> "Error : this function is not supposed to be called at level 0.";
            case 1 -> "Nouvelles incantations apprises !"; // nuage de base (pas foudre)
            case 2 -> {
                this.vie += 1;
                add_competence("Lien");
                yield """
                        Nouvelle compétence débloquée !
                        Votre résistance a légèrement augmenté.
                        """;
            }
            case 3 -> {
                add_competence("Paix intérieure");
                yield """
                        Nouvelle compétence débloquée !
                        Nouvelle incantation apprise !
                        """; //chant qui rend berserk
            }
            case 4 -> {
                this.attaque += 1;
                yield """
                        Des âmes anciennes, fortes et belliqueuses ont rejoint vos rangs.
                        Votre attaque s'en trouve légèrement renforcée.
                        """; //bonus chant colère
            }
            case 5 -> {
                this.vie += 1;
                add_caracteristique("Second souffle");
                yield """
                        Des esprits bienfaisants se mettent à vous suivre.
                        Votre résistance s'en trouve légèrement renforcée.
                        Nouvelle caractéristique débloquée !;
                        Nouvelles incantations apprises !
                        """; // bénédiction de base (pas armure)
            }
            case 6 -> """
                    Nouvelle incantation apprise !
                    Votre compréhension des nuages s'est améliorée.
                    Votre esprit est plus calme même sous la fureur.
                    """; //nuage foudre, bonus nuages, réduction malus berserk
            case 7 -> {
                add_caracteristique("Eclaireur");
                yield """
                        Nouvelle caractéristique débloquée !";
                        Votre âme s'est légèrement renforcée.
                        """; //bonus lien
            }
            case 8 -> {
                this.armure += 1;
                yield """
                        Vous vous liez aux esprit élémentaires.
                        Nouvelles incantations apprises !
                        Votre compréhension des nuages s'est améliorée.
                        La protection des esprit est sur vous !
                        """; //invocation des éléments, bonus nuages
            }
            case 9 -> //bénédiction armure, boost bénédiction, boost chant colère, boost second souffle
                    """
                            Les esprits de puissants anciens guerriers rejoignent vos rangs.
                            Les esprits d'anciens guérisseurs rejoignent vos rangs.
                            Nouvelle incantation apprise !
                            Votre âme développe son indépendance.
                            """;
            case 10 -> {
                this.vie += 1;
                this.attaque += 1;
                yield """
                        De nombreux esprits se lient à vous.
                        Votre attaque s'en trouve légèrement renforcée.
                        Votre résistance s'en trouve légèrement renforcée.
                        Votre âme s'en trouve grandement renforcée.
                        Votre compréhension du monde s'accroit grandement
                        """; //bonus lien, bonus type dé tout incantation
            }
            case 11 -> niveau_sup();
            default -> throw new IllegalStateException("Unexpected value: " + temp);
        };
        System.out.println(text);
    }
    
    /**
     * Calcule les bonus de niveau supérieurs au niveau 10 (cyclique)
     * @return L'affichage du texte
     */
    private String niveau_sup() {
        int unit = this.niveau % 10;
        String text = "";
        if (unit == 1) { // 11, 21, ...
            this.attaque += 1;
            text += "Votre attaque a légèrement augmentée.\n";
        }
        if (unit == 2) { //12, 22, 32, ...
            this.armure += 1;
            text += "Votre armure a légèrement augmentée.\n";
        }
        if (unit % 3 == 0) { // 13, 16, 19, 20, 23, 26, ...
            this.vie += 1;
            text += "Votre résistance a légèrement augmentée.\n";
        }
        if (unit == 4) { //14, 24, 34, ...
            text += "Votre compréhension des éléments s'accroit légèrement.\n";
        }
        if (unit == 5) { // 15, 25, 35, ...
            text += "Votre compréhension des nuages s'est améliorée.\n";
        }
        if (unit == 6) { // 16, 26, 36, ...
            text += "Les esprits d'anciens guérisseurs rejoignent vos rangs.\n";
        }
        if (unit == 7) { //17, 27, 37, ...
            text += "Les esprits de puissants anciens guerriers rejoignent vos rangs.\n";
        }
        if (unit == 8) { //18, 28, 38, ...
            this.attaque += 1;
            text += "Votre attaque a légèrement augmentée.\n";
        }
        if (unit == 9) { //19, 29, ...
            text += "Votre âme développe son indépendance.\n";
        }
        if (unit == 0) { //20, 30, ...
            text += "De nouveaux esprits se joignent à vous !\n";
        }
        
        if (this.niveau % 7 == 2) { //16, 23, 30, 37, ...
            text += "Votre âme se renforce.\n";
        }
        
        return text;
    }
    
    @Override
    public void init_affrontement(boolean force, Position pos) throws IOException {
        possession_atk = 0;
        super.init_affrontement(force, pos);
    }
    
    @Override
    public String text_action() {
        if (est_assomme()) {
            return "(in)cantation/(c)ustom/(o)ff";
        }
        String text = super.text_action();
        if (!est_berserk()) {
            if (!a_familier() && this.niveau >= 2) {
                text += "/(li)en";
            }
            text += "/(in)cantation";
        }
        return text;
    }
    
    @Override
    public Action action(String choix, boolean est_familier) throws IOException {
        if (est_familier) {
            return super.action(choix, true);
        }
        if (est_assomme()) {
            if (choix.equals("in")) {
                return Action.INCANTATION;
            }
            return super.action(choix, false);
        }
        switch (choix) {
            case "in" -> {
                return Action.INCANTATION;
            }
            case "li" -> {
                if (!a_familier() && this.niveau >= 2) {
                    return Action.LIEN;
                }
            }
        }
        return super.action(choix, false);
    }
    
    @Override
    public boolean traite_action(Action action, Monstre ennemi, int bonus_popo) throws IOException {
        switch (action) {
            case INCANTATION -> {
                ennemi.dommage(bonus_popo);
                incantation(ennemi);
                return false;
            }
            case LIEN -> {
                lien(ennemi);
                return false;
            }
        }
        return super.traite_action(action, ennemi, bonus_popo);
    }
    
    @Override
    public String text_extra(Action action) {
        String text = super.text_extra(action);
        if (est_berserk() && this.niveau >= 3) {
            text += "/(pa)ix intérieure";
        }
        return text;
    }
    
    @Override
    public Action_extra extra(String choix) {
        if (choix.equals("pa")) {
            return Action_extra.CALME;
        }
        return super.extra(choix);
    }
    
    public int jouer_extra(Action_extra extra) {
        if (extra == Action_extra.CALME) {
            calme();
        }
        return super.jouer_extra(extra);
    }
    
    @Override
    public boolean action_consomme_popo(Action action) {
        if (action == Action.INCANTATION) {
            return true;
        }
        return super.action_consomme_popo(action);
    }
    
    @Override
    protected int bonus_atk() {
        int bonus = super.bonus_atk();
        return bonus + possession_atk;
    }
    
    @Override
    public int bonus_exploration() {
        int bonus = super.bonus_exploration();
        //éclaireur
        if (this.niveau >= 7) {
            bonus += rand.nextInt(2);
        }
        return bonus;
    }
    
    @Override
    public boolean peut_jouer() {
        // peut jouer inconscient
        return est_actif() && !skip;
    }
    
    @Override
    public boolean peut_diriger_familier() {
        return est_actif() && a_familier_actif() && est_vivant();
    }
    
    @Override
    protected int berserk_fuite() throws IOException {
        if (this.niveau < 6) {
            return super.berserk_fuite();
        }
        if (!est_berserk()) {
            return 0;
        }
        float folie = berserk - Input.D6() * 0.5f;
        if (this.niveau >= 6) { //calme spirituel
            folie -= 1;
        }
        return -Main.corriger(folie, 0);
    }
    
    private void calme() {
        System.out.println(nom + " s'harmonise avec l'univers et laisse retomber sa rage.");
        this.berserk = 0f;
    }
    
    /**
     * Applique la compétence "lien" du shaman
     * @param ennemi le Monstre à lier
     */
    private void lien(Monstre ennemi) {
        if (ennemi.getCompetence() == Competence.CHRONOS) {
            System.out.println("Les esprits de vos ancêtres vous arrêtes avant que vous ne fassiez quelques choses " + "de" + " stupides.");
            return;
        }
        int ratio;
        if (ennemi.est_pantin()) {
            System.out.println("Simulation d'une intéraction avec un monstre aléatoire.");
            int pvm, pv;
            pvm = rand.nextInt(41) + 10; //10~50
            pv = rand.nextInt(pvm - 9) + 5; //5~pvm-5
            System.out.printf("Simulation : Monstre commun ayant %d/%d points de vie\n", pv, pvm);
            ratio = (int) ((float) pv / pvm * 10);
            System.out.println("Puissance d'âme simulée : " + ratio);
        } else {
            ratio = (int) ((float) ennemi.getVie() / ennemi.getVieMax() * 10); //1~10
        }
        if (ennemi.est_nomme()) {
            ratio += 4 + rand.nextInt(3); //4~6
        }
        System.out.println(getNom() + " tente de lier son âme à " + ennemi.getNom());
        
        int jet = -ratio;
        jet += rand.nextInt(3) - 1;
        if (this.niveau >= 7) {
            jet += 1;
        }
        if (this.niveau >= 10) {
            jet += 3;
        }
        jet += bonus_sup10(16, 7);
        if (jet <= 3) {
            jet += Input.D8();
        }
        
        if (jet <= -5) {
            System.out.println("L'âme de " + getNom() + " est violemment rejetée par celle de " + ennemi.getNom() +
                    " !");
            if (ennemi.est_pantin()) {
                System.out.println("Protocole de sécurité engagée, tentative de préservation de l'âme en cours.");
                System.out.println(getNom() + " subit " + (-jet) / 2 + " dommages directes.");
            } else {
                rendre_mort();
            }
        } else if (jet <= -1) {
            System.out.println("l'âme de " + getNom() + " est blessé par celle de " + ennemi.getNom());
            System.out.println(getNom() + " subit " + (-jet) + " dommages directes.");
        } else if (jet == 0) {
            System.out.println(getNom() + " n'est pas parvenu à se lier à " + ennemi.getNom());
        } else if (jet <= 3) {
            System.out.println(getNom() + " n'est pas parvenu à se lier à " + ennemi.getNom() + " et à blessé son âme");
            ennemi.dommage_direct(jet);
        } else {
            System.out.println("Les âmes de " + ennemi.getNom() + " et de " + getNom() + " entre en communion !");
            if (ennemi.est_pantin()) {
                System.out.printf("Fin de la simulation, le monstre aurait un niveau d'affection de %d/7\n", min(7,
                        rand.nextInt(jet) + 3));
                return;
            }
            setOb(min(7, rand.nextInt(jet) + 3 + bonus_sup10(16, 7)));
            Combat.stop_run();
            ennemi.presente_familier();
        }
    }
    
    /**
     * Demande au shaman quelle incantation il veut reciter
     * @return le text demandant l'incantation à lancer
     * @implNote appellé uniquement avec un niveau d'au moins 1
     */
    private String text_incantation() {
        String text = """
                Quel type d'incantation voulez-vous réciter ?
                \t1: ≠‼
                \t2: ↥☁
                """;
        if (this.niveau >= 5) {
            text += "\t3: ¤✧\n";
        }
        if (this.niveau >= 8) {
            text += "\t4: ∆Ψ\n";
        }
        return text;
    }
    
    /**
     * Applique la compétence "incantation" du shaman
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void incantation(Monstre ennemi) throws IOException {
        if (this.niveau < 1) {
            colere(ennemi);
            return;
        }
        System.out.println(text_incantation());
        switch (Input.readInt()) {
            case 1 -> colere(ennemi);
            case 2 -> nuage(ennemi);
            case 3 -> {
                if (this.niveau >= 5) {
                    benir();
                } else {
                    System.out.println("Unknown input");
                    incantation(ennemi);
                }
            }
            case 4 -> {
                if (this.niveau >= 8) {
                    element(ennemi);
                } else {
                    System.out.println("Unknown input");
                    incantation(ennemi);
                }
            }
            default -> {
                System.out.println("Unknown input");
                incantation(ennemi);
            }
        }
    }
    
    /**
     * La compétence "chant de colère" du shaman
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void colere(Monstre ennemi) throws IOException {
        System.out.println("""
                De nombreux esprits en colère répondent à vos chants, donnez leur des ordres :\
                
                \t1: ≠‼∴α
                \t2: ≠‼ψχ""");
        if (this.niveau >= 3) {
            System.out.println("\t3: ≠‼∅δ");
        }
        switch (Input.readInt()) {
            case 1 -> colere_boost();
            case 2 -> colere_attaque(ennemi);
            case 3 -> {
                if (this.niveau >= 3) {
                    colere_berserk();
                } else {
                    System.out.println("Input unknown");
                    colere(ennemi);
                }
            }
            default -> {
                System.out.println("Input unknown");
                colere(ennemi);
            }
        }
    }
    
    /**
     * Chant de colère version attaque bonus
     * @throws IOException toujours
     */
    private void colere_boost() throws IOException {
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(17, 10);
        if (rune_haine) {
            jet += 1;
        }
        if (jet <= 11) {
            jet += jet(new int[]{9}, new int[]{8, 6}, new int[]{4, 10, 10, 10});
        }
        if (jet <= 2) {
            System.out.println("Les esprits des ancients guerriers vous encouragent.");
            possession_atk += 1;
        } else if (jet <= 4) {
            System.out.println("L'âmes d'un anciens guerriers supporte vos actes.");
            possession_atk += 2;
        } else if (jet <= 6) {
            System.out.println("Des âmes d'anciens guerriers guident votre main.");
            possession_atk += 4;
        } else if (jet <= 8) {
            System.out.println("L'esprit d'un grand guerrier vous prête sa force.");
            possession_atk += 8;
        } else if (jet <= 10) {
            System.out.println("L'âme d'un grand guerrier vous prête son savoir");
            possession_atk += 11;
        } else if (jet == 11) {
            System.out.println("L'âme d'un grand guerrier raisonne avec la votre, vous offrant sa force et son " +
                    "savoir" + ".");
            possession_atk += 14;
        } else {
            System.out.println("Votre âme entre en symbiose avec celle de vos ancêtres belliqueux.");
            possession_atk += 18;
        }
        possession_atk += bonus_sup10(20, 10);
        possession_atk += bonus_sup10(17, 10);
    }
    
    /**
     * Chant de colère version dps
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void colere_attaque(Monstre ennemi) throws IOException {
        System.out.println("Les esprits de vos ancêtres déchainent leur colère sur " + ennemi.getNom());
        int attaque;
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(17, 10);
        if (rune_haine) {
            jet += 1;
        }
        if (jet <= 12) {
            jet += jet(new int[]{9, 4}, new int[]{10, 8, 6}, new int[]{10, 10, 10});
        }
        //D6
        if (jet <= 2) {
            attaque = 2;
        } else if (jet <= 4) {
            attaque = 3 + rand.nextInt(3); //3~5
        } else if (jet == 5) {
            attaque = 4 + rand.nextInt(3); //4~6
        } else if (jet == 6) {
            attaque = 6 + rand.nextInt(2); //6~7
        } //D8
        else if (jet == 7) {
            attaque = 8 + rand.nextInt(6); //8~13
        } else if (jet == 8) {
            attaque = 9 + rand.nextInt(7); //9~15
        } //D10
        else if (jet == 9) {
            attaque = 12 + rand.nextInt(7); //12~18
        } else if (jet == 10) {
            attaque = 13 + rand.nextInt(7); //15~22
        } //D10 + 3
        else if (jet <= 12) {
            attaque = 18 + rand.nextInt(13); //18~30
        } else {  //13
            attaque = 25 + rand.nextInt(9);  //25~33
        }
        
        attaque += bonus_sup10(17, 10);
        attaque += bonus_sup10(20, 10);
        
        ennemi.dommage_magique(attaque);
        if (attaque >= 28) {
            ennemi.do_assomme();
        } else if (attaque >= 18) {
            ennemi.affecte();
        } else if (attaque >= 14 || (attaque >= 10 && rand.nextBoolean())) {
            ennemi.do_etourdi();
        }
    }
    
    /**
     * Chant de colère version berserk
     * @throws IOException toujours
     */
    private void colere_berserk() throws IOException {
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(17, 10);
        if (rune_haine) {
            jet += 1;
        }
        if (jet <= 9) {
            jet += jet(new int[]{9, 4}, new int[]{8, 6, 4}, new int[]{10, 10});
        }
        // D4
        if (jet <= 2) {
            System.out.println("Un esprit provoque votre colère.");
            berserk += max(0.1f, jet * 0.1f);
        } else if (jet == 3) {
            System.out.println("Un esprit vengeur vous offre sa haine.");
            berserk += 0.3f;
        } else if (jet == 4) {
            System.out.println("Une âme pleine de colère emplie votre coeur de rage !");
            berserk += 0.4f + rand.nextInt(3) * 0.1f; //0.4~0.6
        } //D6
        else if (jet == 5) {
            System.out.println("Les esprit de victimes innocentes font s'emplir de colère votre coeur.");
            berserk += 0.6f + rand.nextInt(7) * 0.1f; //0.6~1.2
        } else if (jet == 6) {
            System.out.println("L'âme d'un guerrier vénérable vous transmet sa rage vengeresse.");
            berserk += 0.7f + rand.nextInt(9) * 0.1f; //0.7~1.5
        } // D8
        else if (jet == 7) {
            System.out.println("Les esprits de guerriers morts au combat insuffle une rage profonde dans votre âme !");
            berserk += 1f + rand.nextInt(5) * 0.2f; //1~1.8
        } else if (jet == 8) {
            System.out.println("Vous êtes possédé par l'esprit d'un puissant combattant, emplie d'une haine profonde "
                    + "!");
            berserk += 1.5f + rand.nextInt(8) * 0.15f; //1.5~2.55
        } //D8 + 2
        else if (jet == 9) {
            System.out.println("Un esprit ampli d'une haine sans fin vous possède !");
            berserk += 1.9f + rand.nextInt(6) * 0.25f; //1.9~3.15
        } else {
            System.out.println("RAAAAAAAAAAAAAAAAAAAH !!!! Vous êtes consumé par la haine, la rage et la folie !");
            berserk += 2.8f + rand.nextInt(7) * 0.2f; //2.8~4.2
        }
    }
    
    /**
     * Applique la compétence "appel des nuages" du shaman
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void nuage(Monstre ennemi) throws IOException {
        System.out.println("""
                Plusieurs nuages répondent à votre appel, choisissez celui que vous voulez faire venir :\
                
                \t1: ↥☁∿λ
                \t2: ↥☁∘χ
                \t3: ↥☁≋θ""");
        if (this.niveau >= 6) {
            System.out.println("\t4: ↥☁∇Ω");
        }
        switch (Input.read()) {
            case "1" -> nuage_pluie();
            case "2" -> nuage_grele(ennemi);
            case "3" -> nuage_brume(ennemi);
            case "4" -> {
                if (this.niveau >= 6) {
                    nuage_foudre(ennemi);
                } else {
                    System.out.println("Input unknown");
                    nuage(ennemi);
                }
            }
            default -> {
                System.out.println("Input unknown");
                nuage(ennemi);
            }
        }
    }
    
    /**
     * Appel des nuages version soin
     * @throws IOException toujours
     */
    private void nuage_pluie() throws IOException {
        System.out.println("Des nuages apparaissent dans le ciel et une pluie légère commence à tomber.");
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(15, 10);
        if (rune_pluie) {
            jet += 1;
        }
        if (jet <= 10) {
            jet += jet(new int[]{8, 6}, new int[]{8, 6, 4}, new int[]{10, 10});
        }
        //D4
        if (jet <= 1) {
            System.out.println("La pluie tombante recouvre vos blessures.");
            System.out.println("Chaque joueur et familier soigne de 1 points");
        } else if (jet <= 3) {
            System.out.println("La pluie tombante recouvre vos blessures.");
            System.out.println("Chaque joueur et familier soigne de 3 points");
        } else if (jet == 4) {
            
            System.out.println("Les gouttes d'eau s'abattent sur vos blessures, qui commencent à se refermer.");
            System.out.println("Chaque joueur et familier soigne de 5 points");
            
        } //D6
        else if (jet == 5) {
            System.out.println("Les gouttes d'eau recouvrant vos blessures vous aide à cicatriser.");
            System.out.printf("Chaque joueur et familier soigne de %d points\n", 8 + rand.nextInt(3)); //8~10
        } else if (jet == 6) {
            System.out.println("Une pluie douce et appaissante vous recouvre.");
            System.out.printf("Chaque joueur et familier soigne de %d points\n", 10 + rand.nextInt(4)); //10~13
        } //D8
        else if (jet == 7) {
            System.out.println("Une énergie régénérantes s'insinnue dans vos veines alors que l'eau coule sur votre " + "corps.");
            System.out.printf("Chaque joueur et familier soigne de %d points\n", 15 + rand.nextInt(9)); //15~23
        } else if (jet == 8) {
            System.out.println("Des gouttes dorées tombent des nuages et se collent à vos blessures, les guérissant " + "comme par magie.");
            System.out.printf("Chaque joueur et familier soigne de %d points\n", 17 + rand.nextInt(10)); //17~26
        } //D8+
        else if (jet == 9) {
            System.out.println("Les gouttes tombant du ciel s'insinuent dans vos corps, renforcent vos os et " +
                    "referment vos plaie.");
            System.out.printf("Chaque joueur et familier soigne de %d points, et gagne temporairement %d points de " + "résistance" + "\n", 21 + rand.nextInt(12), 3 + rand.nextInt(3)); //21~32 | 3~5
        } else {
            System.out.println("Une force ancienne s'infiltre dans vos corps au travers les gouttes d'eau.");
            System.out.printf("Chaque joueur et familier soigne de %d points, et gagne temporairement %d points de " + "résistance" + "\n", 25 + rand.nextInt(14), 8 + rand.nextInt(6)); //25~38 | 8~13
        }
    }
    
    /**
     * Appel des nuages version dps AOE
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void nuage_grele(Monstre ennemi) throws IOException {
        System.out.println("De sombres nuages s'amoncèlent au dessus de vous");
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(15, 10);
        if (rune_pluie) {
            jet += 1;
        }
        if (jet <= 9) {
            jet += jet(new int[]{8, 6}, new int[]{8, 6, 4}, new int[]{10, 10});
        }
        // D4
        if (jet <= 1) {
            System.out.println("Une fine grêle vous frappe.");
            System.out.println("Chaque joueur et familier subit 1 point de dommage.");
            ennemi.dommage(1);
        } else if (jet <= 3) {
            System.out.println("Une légère grêle vous frappe.");
            System.out.println("Chaque joueur et familier subit 2 points de dommage.");
            ennemi.dommage(2);
        } else if (jet == 4) {
            System.out.println("La grêle vous frappe.");
            System.out.println("Chaque joueur et familier subit 3 points de dommage.");
            ennemi.dommage(4);
        } //D6
        else if (jet == 5) {
            System.out.println("La grêle s'abat sur vous.");
            System.out.println("Chaque joueur et familier subit 4 points de dommage.");
            ennemi.dommage(7 + rand.nextInt(3)); //7~9
        } else if (jet == 6) {
            System.out.println("La grêle s'abat sur vous.");
            System.out.printf("Chaque joueur et familier subit %d points de dommage.\n", 4 + rand.nextInt(2)); //4~5
            ennemi.dommage(9 + rand.nextInt(3)); //9~11
        } //D8
        else if (jet == 7) {
            System.out.println("Une violente tempête se lève et la grêle vous frappe.");
            System.out.printf("Chaque joueur et familier subit %d points de dommage.\n", 6 + rand.nextInt(3)); //6~8
            ennemi.dommage(13 + rand.nextInt(4)); //13~16
            if (rand.nextBoolean()) {
                ennemi.do_etourdi();
            }
        } else if (jet == 8) {
            System.out.println("Une violente tempête se lève et la grèle vous frappe.");
            System.out.printf("Chaque joueur et familier subit %d points de dommage.\n", 7 + rand.nextInt(4)); //7~10
            ennemi.dommage(15 + rand.nextInt(4)); //15~18
            if (rand.nextBoolean()) {
                ennemi.affecte();
            }
        } //D8+
        else if (jet == 9) {
            System.out.println("Une violente tempête de grêle vous frappe.");
            System.out.printf("Chaque joueur et familier subit %d points de dommage.\n", 9 + rand.nextInt(5)); //9~13
            ennemi.dommage(18 + rand.nextInt(8)); //18~25
            ennemi.affecte();
        } else {
            System.out.println("Une immense tempête de neige et de grêle vous frappe de plein fouet.");
            System.out.printf("Chaque joueur et familier subit %d points de dommage.\n", 10 + rand.nextInt(4)); //10~13
            ennemi.dommage(20 + rand.nextInt(7)); //20~26
            if (rand.nextBoolean()) {
                ennemi.affecte();
            } else {
                ennemi.do_etourdi();
            }
        }
    }
    
    /**
     * Appel des nuages version debuff
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void nuage_brume(Monstre ennemi) throws IOException {
        System.out.println("Un nuage apparait au dessus de vous et commence à se rapprocher du sol");
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(15, 10);
        if (rune_pluie) {
            jet += 1;
        }
        if (jet <= 9) {
            jet += jet(new int[]{8, 6}, new int[]{8, 6, 4}, new int[]{10, 10, 10});
        }
        //D4
        if (jet <= 1) {
            System.out.println("Une légère brûme vous entoure.");
            System.out.println("Chaque joueur et familier perds temporairement 1 point d'attaque.");
            ennemi.boostAtk(-1, false);
        } else if (jet <= 3) {
            System.out.println("C'est... joli ?");
            ennemi.boostAtk(-1, false);
        } else if (jet == 4) {
            System.out.println("La brûme se lève.");
            System.out.println("Chaque joueur et familier perds temporairement 2 points d'attaque.");
            ennemi.boostAtk(-3, false);
        } //D6
        else if (jet == 5) {
            System.out.println("La brûme commence à vous encercler.");
            System.out.println("Chaque joueur et familier perds temporairement 2 point d'attaque.");
            ennemi.boostAtk(-5, false);
        } else if (jet == 6) {
            System.out.println("Une brûme épaisse commence à vous encercler.");
            System.out.println("Chaque joueur et familier perds temporairement 3 point d'attaque.");
            ennemi.boostAtk(-6, false);
        } //D8
        else if (jet == 7) {
            System.out.println("Un épais brouillard vous recouvre.");
            System.out.println("Chaque joueur et familier perds temporairement 3 points d'attaque.");
            ennemi.boostAtk(-8, false);
        } else if (jet == 8) {
            System.out.println("Un épais brouillard vous recouvre.");
            System.out.println("Chaque joueur et familier perds temporairement 4 points d'attaque.");
            ennemi.boostAtk(-9, false);
        } //D8+
        else if (jet == 9) {
            System.out.println("Un brouillard dense vous entoure.");
            System.out.println("Il est désormais impossible de tirer.");
            System.out.println("Il est désormais impossible de lancer un sort ciblé sur une autre cible que " + "soit"
                    + "-même" + ".");
            System.out.println("Chaque joueur et familier perds temporairement 6 points d'attaque.");
            for (Joueur j : Main.joueurs) {
                j.p_prend_cecite();
                j.f_prend_cecite();
            }
            ennemi.boostAtk(-11, false);
        } else {
            System.out.println("Une brûme vous entoure, si dense que vous ne vous voyez presque plus.");
            System.out.println("Il est désormais impossible de tirer.");
            System.out.println("Il est désormais impossible de lancer un sort ciblé sur une autre cible que " + "soit"
                    + "-même" + ".");
            System.out.println("Chaque joueur et familier perds temporairement 8 points d'attaque.");
            ennemi.boostAtk(-15, false);
        }
    }
    
    /**
     * Appel des nuages version dps debuff
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void nuage_foudre(Monstre ennemi) throws IOException {
        System.out.println("Un nuage menaçant apparait au dessus de vous et commence à se rapprocher du sol");
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(15, 10);
        if (rune_orage) {
            jet += 1;
        }
        if (rune_pluie) {
            jet += 1;
        }
        if (jet <= 10) {
            jet += jet(new int[]{8}, new int[]{8, 6}, new int[]{8, 10, 10});
        }
        // D6
        if (jet <= 1) {
            System.out.println("C'est... joli ?");
        } else if (jet <= 3) {
            System.out.println("Vous percevez un fugace arc électrique.");
            if (rand.nextBoolean()) {
                ennemi.do_etourdi();
            } else {
                ennemi.dommage(1);
            }
        } else if (jet <= 5) {
            System.out.println("Vous percevez un fugace arc électrique.");
            ennemi.dommage(rand.nextInt(5) + 1); //1~5
            ennemi.affecte();
        } else if (jet == 6) {
            System.out.println("La foudre frappe l'ennemi.");
            ennemi.dommage(3 + rand.nextInt(6)); //3~8
            ennemi.affecte();
        } //D8
        else if (jet == 7) {
            System.out.println("Le nuage s'abat sur le monstre ennemi, suivi d'un éclair.");
            ennemi.dommage(5 + rand.nextInt(6)); //5~10
            ennemi.boostAtk(-2, false);
            ennemi.affecte();
        } else if (jet == 8) {
            System.out.println("Le nuage s'abat sur le monstre ennemi, suivi d'un éclair.");
            ennemi.dommage(6 + rand.nextInt(7)); //6~12
            ennemi.boostAtk(-2, false);
            ennemi.affecte();
        } //D8+
        else if (jet <= 10) {
            System.out.println("Le nuage descent sur l'ennemi, s'illuminant chaque fois que la foudre le frappe.");
            for (int i = 0; i < rand.nextInt(3) + 2; i++) { //2 à 5 fois
                ennemi.dommage(6 + rand.nextInt(7)); //6~12
            }
            ennemi.boostAtk(-4, false);
            if (rand.nextBoolean()) {
                ennemi.affecte();
            }
            ennemi.do_assomme();
        } else {
            System.out.println("Le nuage noir s'abt sur l'ennemi, illuminant la scène chaque fois qu'un éclair le " + "frappe.");
            for (int i = 0; i < rand.nextInt(3) + 3; i++) { //3 à 6 fois
                ennemi.dommage(7 + rand.nextInt(9)); //7~15
            }
            ennemi.boostAtk(-5, false);
            ennemi.do_assomme();
        }
    }
    
    /**
     * Applique la compétence "invocation des éléments" du shaman
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void element(Monstre ennemi) throws IOException {
        System.out.println("""
                Choisissez un élément à invoquer :\
                \t1: ∆Ψ≋ξ
                \t2: ∆Ψ∴φ
                \t3: ∆Ψ⊡τ
                \t4: ∆Ψ∿μ""");
        switch (Input.read()) {
            case "1" -> vent(ennemi);
            case "2" -> feu(ennemi);
            case "3" -> terre(ennemi);
            case "4" -> eau(ennemi);
            default -> {
                System.out.println("Input unknown");
                element(ennemi);
            }
        }
    }
    
    /**
     * Invocation des éléments version buff tir
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void vent(Monstre ennemi) throws IOException {
        System.out.println("Le vent se lève...");
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(14, 10);
        if (jet <= 7) {
            jet += jet(new int[]{}, new int[]{6}, new int[]{10, 10});
        }
        // D6
        if (jet <= 1) {
            System.out.println("Une légère brise se fait sentir.");
            if (tir_vent(3, rand.nextInt(Main.nbj) + 1)) {
                return;
            }
        } else if (jet <= 3) {
            System.out.println("Une puissant vent souffle.");
            if (tir_vent(8, Main.nbj)) {
                return;
            }
        } else if (jet <= 5) {
            System.out.println("De violentes rafales se prononce.");
            ennemi.dommage_direct(2);
            if (tir_vent(12, Main.nbj + rand.nextInt(Main.nbj + 1))) {
                return;
            }
        } else if (jet == 6) {
            ennemi.dommage_direct(3);
            if (tir_vent(17, Main.nbj + 2 * rand.nextInt(Main.nbj + 1))) {
                return;
            }
        } // D6 + 2
        else if (jet == 7) {
            System.out.println("Le vent est si puissant que vous avez du mal à ne pas être emporté.");
            ennemi.dommage_direct(4);
            if (tir_vent(26, -5, Main.nbj * 2 + rand.nextInt((Main.nbj + 1) * 3))) {
                return;
            }
            System.out.println("Toutes les attaques sont temporairement baissées.");
        } else {
            System.out.println("De violentes bourrasques vous empêchent de tenir debout.");
            ennemi.dommage_direct(5);
            if (tir_vent(42, -23, Main.nbj * 3 + rand.nextInt((Main.nbj + 1) * 5))) {
                return;
            }
            System.out.println("Toutes les attaques sont temporairement baissées.");
        }
        System.out.println("Tous les tirs sont temporairement boostés.");
    }
    
    /**
     * Méthode auxiliaire de vent, met à jour les modificateur de vent
     * @param value_tir la nouvelle valeur de modificateur tir à mettre à jour
     * @param duree     la durée du bonus
     * @return si la modification a été ignorée
     */
    private boolean tir_vent(int value_tir, int duree) {
        return tir_vent(value_tir, 0, duree);
    }
    
    /**
     * Méthode auxiliaire de vent, met à jour les modificateur de vent
     * @param value_tir la nouvelle valeur de modificateur tir à mettre à jour
     * @param value_atk la nouvelle valeur de modificateur d'attaque
     * @param duree     la durée du bonus
     * @return si la modification a été ignorée
     */
    private boolean tir_vent(int value_tir, int value_atk, int duree) {
        if (tir_bonus <= value_tir) {
            tir_bonus = value_tir;
            attaque_bonus = value_atk;
            tour_modif = duree + rand.nextInt(3) - 1;
            return false;
        }
        System.out.println("Le vent souffle déjà fort.");
        return true;
    }
    
    /**
     * Invocation des éléments version debuff et dps
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void terre(Monstre ennemi) throws IOException {
        System.out.println("La terre commence à trembler...");
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(14, 10);
        if (jet <= 7) {
            jet += jet(new int[]{}, new int[]{6}, new int[]{10, 10});
        }
        // D6
        if (jet <= 1) {
            System.out.println("Vous entendez un léger grondement.");
            ennemi.dommage(2);
        } else if (jet <= 3) {
            System.out.println("Des fragments de roches s'arrachent du sol et hertent l'ennemi.");
            ennemi.dommage(3 + rand.nextInt(5)); //3~5
            ennemi.do_etourdi();
        } else if (jet <= 5) {
            System.out.println("Un rocher se soulève et frappe l'adversaire.");
            ennemi.dommage(5 + rand.nextInt(3)); //5~7
            if (rand.nextBoolean()) { //50% => 25% assommer
                ennemi.do_etourdi();
            } else {
                ennemi.affecte();
            }
        } else if (jet == 6) {
            System.out.println("Un rocher se soulève et frappe violemment l'adversaire.");
            ennemi.dommage(9 + rand.nextInt(7)); //9~15
            ennemi.affecte(); // 50% assommer
        } // D6 + 2
        else if (jet == 7) {
            System.out.println("Le sol se soulève et une avalanche rocailleuse frappe le monstre ennemi.");
            ennemi.dommage(20 + rand.nextInt(5)); //20~24
            if (rand.nextInt(3) != 0) { //66% => 83.1% assommer
                ennemi.do_assomme();
            } else {
                ennemi.affecte();
            }
        } else {
            System.out.println("Le sol se soulève et se fissure en deux. Les deux parties se fracasse l'une contre " + "l'autre sur le monstre ennemi.");
            ennemi.dommage(29 + rand.nextInt(45)); //29~73
            if (rand.nextInt(5) != 0) { //80% => 90% assommer
                ennemi.do_assomme();
            } else {
                ennemi.affecte();
            }
        }
    }
    
    /**
     * Invocation des éléments version dps
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void feu(Monstre ennemi) throws IOException {
        System.out.println("Vous entendez de légers crépitements...");
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(14, 10);
        if (jet <= 7) {
            jet += jet(new int[]{}, new int[]{6}, new int[]{10, 10});
        }
        int dommage;
        // D6
        if (jet <= 1) {
            dommage = 4;
        } else if (jet <= 3) {
            System.out.println("L'ennemi semble indisposé par quelque chose.");
            dommage = jet + 5 + rand.nextInt(2); //7~9
        } else if (jet == 4) {
            System.out.println("De la fumée s'échappe du monstre ennemi.");
            dommage = 7 + rand.nextInt(7); //7~13
        } else if (jet == 5) {
            System.out.println("Des flammes s'élève de l'adversaire !");
            dommage = 10 + rand.nextInt(6); // 10~15
        } else if (jet == 6) {
            System.out.println("L'ennemi brûle de l'intérieur !");
            dommage = 13 + rand.nextInt(9); // 13~21
        } // D6 + 2
        else if (jet == 7) {
            System.out.println("Le monstre ennemi se transforme en un véritable brasier !");
            dommage = 17 + rand.nextInt(6); // 17~22
        } else {
            System.out.println("Une véritable tornade de flamme rugit dans le corps de l'adversaire.");
            dommage = 23 + rand.nextInt(3); // 23~25
        }
        ennemi.dommage_direct(dommage, false);
    }
    
    /**
     * Invocation des éléments version buff/random
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void eau(Monstre ennemi) throws IOException {
        System.out.println("Vous entendez un léger gargouillement...");
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(14, 10);
        if (jet <= 7) {
            jet += jet(new int[]{}, new int[]{6}, new int[]{10, 10});
        }
        // D6
        if (jet <= 1) {
            System.out.println("Vous sentez quelques gouttes de pluie.");
            System.out.println("Tous les joueurs récupèrent 1PP.");
        } else if (jet <= 3) {
            System.out.println("Une pluie purificatrice s'abat.");
            System.out.println("Tous les joueurs récupèrent 2PP.");
        } else if (jet == 4) {
            System.out.println("De l'eau jaillit de sous l'adversaire, le faisant glisser.");
            System.out.println("Cette eau magique purifie votre corps");
            System.out.println("Tous les joueurs récupèrent 2PP.");
            ennemi.dommage(1 + rand.nextInt(2));
            ennemi.do_etourdi();
        } else if (jet <= 6) {
            System.out.println("Une vague magique frappe l'ennemi et vous renforce.");
            System.out.println("Tous les joueurs récupèrent 3PP.");
            ennemi.dommage_magique(2 + rand.nextInt(2));
            ennemi.affecte();
        } // D6 + 2
        else if (jet == 7) {
            System.out.println("Un torrent mystique s'abat sur le terrain, emportant l'ennemi et réveillant les " +
                    "joueurs.");
            System.out.println("Tous les joueurs récupèrent 3PP.");
            ennemi.dommage(3 + rand.nextInt(4));
            ennemi.affecte();
            for (int i = 0; i < Main.nbj; i++) {
                Main.joueurs[i].conscient = true;
                Main.joueurs[i].reveil = 0;
            }
        } else {
            System.out.println("Un torrent mystique s'abat sur le terrain, emportant l'ennemi et réveillant les " +
                    "joueurs.");
            System.out.println("Tous les joueurs récupèrent 4PP.");
            ennemi.dommage(4 + rand.nextInt(4));
            ennemi.do_assomme();
            for (int i = 0; i < Main.nbj; i++) {
                Main.joueurs[i].conscient = true;
                Main.joueurs[i].reveil = 0;
            }
        }
    }
    
    /**
     * Applique la compétence "bénédiction" du shaman
     * @throws IOException toujours
     */
    private void benir() throws IOException {
        System.out.println("""
                Cibler un allié (ou vous même) et choississez une bénédiction :\
                
                \t1: ¤✧∿η
                \t2: ¤✧↑κ
                \t3: ¤✧⊕ρ""");
        if (this.niveau >= 9) {
            System.out.println("\t4: ¤✧▣ζ");
        }
        switch (Input.read()) {
            case "1" -> benir_soin();
            case "2" -> benir_force();
            case "3" -> benir_vie();
            case "4" -> {
                if (this.niveau >= 9) {
                    benir_def();
                } else {
                    System.out.println("Input unknow");
                    benir();
                }
            }
            default -> {
                System.out.println("Input unknow");
                benir();
            }
        }
    }
    
    /**
     * Bénédiction version soin
     * @throws IOException toujours
     */
    private void benir_soin() throws IOException {
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(16, 10);
        if (jet <= 9) {
            jet += jet(new int[]{9}, new int[]{8, 6}, new int[]{10, 10, 10});
        }
        int soin = 0;
        //D6
        if (jet <= 1) {
            soin += 2;
        } else if (jet == 2) {
            soin += 3;
        } else if (jet <= 4) {
            soin += jet + 2;
        } else if (jet <= 6) {
            soin += 10 + rand.nextInt(6); //10~15
        } //D8
        else if (jet == 7) {
            soin += 17 + rand.nextInt(7); //17~23
        } else if (jet == 8) {
            soin += 19 + rand.nextInt(8); //19~26
        } //D8+
        else if (jet == 9) {
            soin += 25 + rand.nextInt(10); //25~34
        } else {
            soin += 50;
        }
        soin += bonus_sup10(20, 10);
        soin += bonus_sup10(16, 10);
        System.out.printf("La cible est soignée de %d.\n", soin);
    }
    
    /**
     * Bénédiction version buff résistance
     * @throws IOException toujours
     */
    private void benir_vie() throws IOException {
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(16, 10);
        if (jet <= 10) {
            jet += jet(new int[]{9}, new int[]{8, 6}, new int[]{10, 10, 10});
        }
        int boost = 0;
        // D6
        if (jet <= 1) {
            boost += 1;
        } else if (jet <= 4) {
            boost += jet;
        } else if (jet <= 6) {
            boost += 7;
        } // D8
        else if (jet == 7) {
            boost += 10 + rand.nextInt(4); //10~13
        } else if (jet == 8) {
            boost += 12 + rand.nextInt(3); //12~14
        } // D8+
        else if (jet == 9) {
            boost += 15 + rand.nextInt(2); //15~16
        } else if (jet == 10) {
            boost += 17 + rand.nextInt(2); //17~18
        } else {
            boost += 20;
        }
        boost += bonus_sup10(20, 10);
        boost += bonus_sup10(16, 10);
        System.out.printf("La cible gagne temporairement %d points de résistance.\n", boost);
    }
    
    /**
     * Bénédiction version buff attaque
     * @throws IOException toujours
     */
    private void benir_force() throws IOException {
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(16, 10);
        if (jet <= 9) {
            jet += jet(new int[]{9}, new int[]{8, 6}, new int[]{10, 10});
        }
        int boost;
        // D6
        if (jet <= 1) {
            boost = 1;
        } else if (jet == 2) {
            boost = 2;
        } else if (jet <= 6) {
            boost = jet - 1;
        } // D8
        else if (jet <= 8) {
            boost = jet + 1;
        } // D8+
        else {
            boost = jet + 3;
        }
        boost += bonus_sup10(20, 10);
        boost += bonus_sup10(16, 10);
        System.out.printf("La cible gagne temporairement %d point d'attaque.\n", boost);
    }
    
    /**
     * Bénédiction version buff résistance/armure
     * @throws IOException toujours
     */
    private void benir_def() throws IOException {
        int jet = bonus_sup10(20, 10);
        jet += bonus_sup10(16, 10);
        if (jet <= 9) {
            jet += jet(new int[]{}, new int[]{8}, new int[]{10, 10});
        }
        int def;
        if (jet <= 3) {
            def = 1;
        } else if (jet <= 5) {
            def = 2;
        } else if (jet <= 7) {
            def = 3;
        } else if (jet <= 9) {
            def = 4;
        } else {
            def = 6;
        }
        def += (bonus_sup10(20, 10) + bonus_sup10(16, 10)) / 3;
        System.out.printf("La cible gagne temporairement %d points d'armure.\n", def);
    }
    
    @Override
    public boolean auto_ressusciter(int malus) {
        if (this.niveau < 5) {
            return false;
        }
        int palier_mort = 9 + malus;
        if (this.niveau >= 9) {
            palier_mort -= 1;
        }
        palier_mort -= bonus_sup10(19, 10);
        if (palier_mort < 0) {
            return true;
        }
        return Input.D10() >= palier_mort; //20%~30% sans malus
    }
}
package Metiers;

import Exterieur.Input;

import Enum.Metier;
import Enum.Position;
import Enum.Action;
import Enum.Competence;
import Enum.Dieux;
import Enum.Action_extra;

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
        caracteristique = "Ame errante";
        competences = "Incantation";
        possession_atk = 0;
        SetEffetParent();
    }

    @Override
    protected void actualiser_niveau() {
        if(this.niveau >= 2){
            this.competences += ", Lien";
            this.vie += 1;
        }
        if(this.niveau >= 3){
            this.competences += ", Paix intérieure";
        }
        if(this.niveau >= 4){
            this.attaque += 1;
        }
        if(this.niveau >= 5){
            this.caracteristique += ", Second souffle";
        }
        if(this.niveau >= 7){
            this.caracteristique += ", Eclaireur";
        }
        if(this.niveau >= 8){
            this.armure += 1;
        }
        if(this.niveau >= 10){
            this.vie += 1;
            this.attaque += 1;
        }
    }

    @Override
    protected void presente_caracteristique(){
        System.out.println("Ame errante : peut incanter même inconscient.");
        if(this.niveau >= 5) {
            System.out.println("Second souffle : Permet rarement de tromper la mort.");
        }
        if(this.niveau >= 7) {
            System.out.println("Eclaireur : Augmente légèrement les dé d'exploration.");
        }
    }

    @Override
    protected void presente_pouvoir(){
        System.out.println("Incantation : lance de mystérieuses incantations invoquant les forces de la nature et les esprits de ses ancêtres.");
        if(this.niveau >= 2) {
            System.out.println("Lien : Projete son âme dans celle d'un monstre pour tenter de les lier de force. Un monstre" +
                    " en bonne santé aura une âme puissante, alors que l'âme d'un monstre blessé est plus faible.");
        }
        if(this.niveau >= 3) {
            System.out.println("Paix intérieure : Regagne instantannement sa santé mentale et son calme.");
        }
    }

    public Metier getMetier() {
        return metier;
    }

    protected String nomMetier(){
        return "shaman";
    }

    @Override
    void lvl_up() {
        int temp = this.niveau;
        if(temp < 0){
            temp = 0;
        }
        if(temp > 11){
            temp = 11;
        }
        String text = switch(temp){
            case 0 -> "Error : this function is not suposed to be called at level 0.";
            case 1 -> "Nouvelles incantations apprises !"; // nuage de base (pas foudre)
            case 2 -> {
                this.vie += 1;
                this.competences += ", Lien";
                yield """
            Nouvelle compétence débloquée !
            Votre résistance a légèrement augmenté.
            """;
            }
            case 3 -> {
                this.competences += ", Paix intérieure";
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
                this.caracteristique += ", Second souffle";
                yield """
                       Des esprits bienfaisants se mettent à vous suivre.
                       Votre résistance s'en trouve légèrement renforcée.
                       Nouvelle caractèristique débloquée !";
                       Nouvelles incantations apprises !
                       """; // bénédiction de base (pas armure)
            }
            case 6 -> """
                    Nouvelle incantation apprise !
                    Votre compréhension des nuages s'est améliorée.
                    Votre esprit est plus calme même sous la fureur.
                    """; //nuage foudre, bonus nuages, réduction malus berserk
            case 7 -> {
                this.caracteristique += ", Eclaireur";
                yield """
                       Nouvelle caractèristique débloquée !";
                       Votre âme s'est légèrement renforcée.
                       """; //bonus lien
            }
            case 8 -> {
                this.armure += 1;
                yield """
                Voux vous liez aux esprit élémentaires.
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
                    Votre âme dévellope son indépendance.
                    """;
            case 10 -> {
                this.vie += 1;
                this.attaque += 1;
                yield """
                    De nombreux esprits se lient à vous.
                    Votre attaque s'en trouve légèrement renforcée.
                    Votre résistance s'en trouve légèrement renforcée.
                    Votre âme s'en trouve grandement renforcée.
                    Votre compréhension du monde s'accroie grandement
                    """; //bonus lien, bonus type dé tout incantation
            }
            case 11 -> "Vous avez atteint le niveau max (frappe le dev c'est sa faute).";
            default -> throw new IllegalStateException("Unexpected value: " + temp);
        };
        System.out.println(text);
    }

    @Override
    public void init_affrontement(boolean force, Position pos) throws IOException {
        possession_atk = 0;
        super.init_affrontement(force, pos);
    }

    @Override
    public String text_action() {
        if(est_assomme()){
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
        if(est_familier){
            return super.action(choix, true);
        }
        if(est_assomme()){
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
        switch(action) {
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
        if(est_berserk() && this.niveau >= 3){
            text += "(pa)ix intérieure";
        }
        return text;
    }

    @Override
    public Action_extra extra(String choix) {
        if(choix.equals("pa")){
            return Action_extra.CALME;
        }
        return super.extra(choix);
    }

    public void jouer_extra(Action_extra extra) {
        if(extra == Action_extra.CALME){
            calme();
        }
        super.jouer_extra(extra);
    }

    @Override
    public boolean action_consomme_popo(Action action){
        if(action == Action.INCANTATION){
            return true;
        }
        return super.action_consomme_popo(action);
    }

    @Override
    protected int bonus_atk(){
        int bonus = super.bonus_atk();
        return bonus + possession_atk;
    }

    @Override
    public int bonus_exploration(){
        int bonus = super.bonus_exploration();
        //éclaireur
        if(this.niveau >= 7){
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
    public boolean peut_diriger_familier(){
        return est_actif() && a_familier_actif() && est_vivant();
    }

    @Override
    protected int berserk_fuite() throws IOException {
        if(this.niveau < 6){
            return super.berserk_fuite();
        }
        if(!est_berserk()){
            return 0;
        }
        float folie = berserk - Input.D6() * 0.5f;
        if(this.niveau >= 6){ //calme spirituel
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
     * @throws IOException toujours
     */
    private void lien(Monstre ennemi) throws IOException {
        if (ennemi.getCompetence() == Competence.CHRONOS) {
            System.out.println("Les esprits de vos ancêtres vous arretes avant que vous ne fassiez quelques choses de stupides.");
            return;
        }
        int ratio = (int) ((float) ennemi.getVie() / ennemi.getVieMax() * 12); //1~12
        if (ennemi.est_nomme()) {
            ratio += 4 + rand.nextInt(3); //4~6
        }
        System.out.println(getNom() + " tente de lier son âme à " + ennemi.getNom());
        int result = Input.D8();
        result -= ratio; //force d'âme du monstre
        result += rand.nextInt(3) - 1;
        if(this.niveau >= 7){
            result += 1;
        }
        if(this.niveau >= 10){
            result += 3;
        }
        if (result <= -5) {
            System.out.println("L'âme de " + getNom() + " est violemment rejetée par celle de " + ennemi.getNom() + " !");
            rendre_mort();
        }
        else if (result <= -1) {
            System.out.println("l'âme de " + getNom() + " est blessé par celle de " + ennemi.getNom());
            System.out.println(getNom() + " subit " + (-result) + " dommages.");
        }
        else if (result <= 3){
            System.out.println(getNom() + " n'est pas parvenu à se lier à " + ennemi.getNom() + " et à bléssé son âme");
            ennemi.dommage_directe(result);
        }
        else {
            System.out.println("Les âmes de " + ennemi.getNom() + " et de " + getNom() + " entre en communion !");
            setOb(min(7, rand.nextInt(result) + 3));
            Combat.stop_run();
            ennemi.presente_familier();
        }
    }

    /**
     * Demande au shaman quelle incantation il veut reciter
     * @return le text demandant l'incatation à lancer
     * @implNote appellé uniquement avec un niveau d'au moins 1
     */
    private String text_incantation() {
        String text = """
                Quel type d'incantation voulez-vous réciter ?
                \t1: ≠‼
                \t2: ↥☁
                """;
        if(this.niveau >= 5){
            text += "\n\t3: ¤✧";
        }
        if(this.niveau >= 8){
            text += "\n\t4: ∆Ψ";
        }
        return text;
    }

    /**
     * Applique la compétence "incantation" du shaman
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void incantation(Monstre ennemi) throws IOException {
        if(this.niveau < 1){
            colere(ennemi);
            return;
        }
        System.out.println(text_incantation());
        switch(Input.readInt()){
            case 1 -> colere(ennemi);
            case 2 -> nuage(ennemi);
            case 3 -> {
                if(this.niveau >= 5){
                    benir();
                }
                else{
                    System.out.println("Unknow input");
                    incantation(ennemi);
                }
            }
            case 4 -> {
                if(this.niveau >= 8) {
                    element(ennemi);
                }
                else{
                    System.out.println("Unknow input");
                    incantation(ennemi);
                }
            }
            default -> {
                System.out.println("Unknow input");
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
        if(this.niveau >= 3){
            System.out.println("\\t3: ≠‼∅δ");
        }
        switch(Input.readInt()){
            case 1 -> colere_boost();
            case 2 -> colere_attaque(ennemi);
            case 3 -> {
                if(this.niveau >= 3){
                    colere_berserk();
                }
                else {
                    System.out.println("Input unknow");
                    colere(ennemi);
                }
            }
            default -> {
                System.out.println("Input unknow");
                colere(ennemi);
            }
        }
    }

    /**
     * Chant de colère version attaque bonus
     * @throws IOException toujours
     */
    private void colere_boost() throws IOException {
        int jet = this.niveau >= 9 ? Input.D8() : Input.D6();
        jet += rand.nextInt(3) - 1;
        int[] paliers = {4, 10, 10, 10};
        for(int palier : paliers){
            if(this.niveau >= palier){
                jet += 1;
            }
        }
        if(jet <= 2){
            System.out.println("Les esprits des ancients guerriers vous encouragent.");
            possession_atk += 1;
        }
        else if (jet <= 4){
            System.out.println("L'âmes d'un anciens guerriers supporte vos actes.");
            possession_atk += 2;
        }
        else if (jet <= 6){
            System.out.println("Des âmes d'anciens guerriers guident votre main.");
            possession_atk += 4;
        }
        else if(jet <= 8){
            System.out.println("L'esprit d'un grand guerrier vous prête sa force.");
            possession_atk += 8;
        }
        else if(jet <= 10){
            System.out.println("L'âme d'un grand guerrier vous prête son savoir");
            possession_atk += 11;
        }
        else if (jet == 11){
            System.out.println("L'âme d'un grand guerrier raisonne avec la votre, vous offrant sa force et son savoir.");
            possession_atk += 14;
        }
        else{
            System.out.println("Votre âme entre en symbiose avec celle de vos ancestres belliqueux.");
            possession_atk += 18;
        }
    }

    /**
     * Chant de colère version dps
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void colere_attaque(Monstre ennemi) throws IOException {
        System.out.println("Les esprits de vos ancêtres déchainent leur colère sur " + ennemi.getNom());
        int attaque;
        int jet;
        if (this.niveau >= 9) {
            jet = Input.D10();
        } else if (this.niveau >= 4) {
            jet = Input.D8();
        } else {
            jet = Input.D6();
        }
        jet += rand.nextInt(3) - 1;
        if (this.niveau >= 10) {
            jet += 3;
        }
        //D6
        if (jet <= 2) {
            attaque = 2;
        } else if (jet <= 4) {
            attaque = 3 + rand.nextInt(3); //3~5
        } else if(jet == 5){
            attaque = 4 + rand.nextInt(3); //4~6
        } else if(jet == 6){
            attaque = 6 + rand.nextInt(2); //6~7
        } //D8
        else if (jet == 7) {
            attaque = 8 + rand.nextInt(6); //8~13
        } else if(jet == 8){
            attaque = 9 + rand.nextInt(7); //9~15
        } //D10
        else if(jet == 9){
            attaque = 12 + rand.nextInt(7); //12~18
        } else if (jet == 10){
            attaque = 13 + rand.nextInt(7); //15~22
        } //D10 + 3
        else if (jet <= 12){
            attaque = 18 + rand.nextInt(13); //18~30
        }
        else {  //13
            attaque = 25 + rand.nextInt(9);  //25~33
        }
        ennemi.dommage_magique(attaque);
        if(attaque >= 28){
            ennemi.do_assomme();
        } else if (attaque >= 18){
            ennemi.affecte();
        } else if (attaque >= 14 || (attaque >= 10 && rand.nextBoolean())){
            ennemi.do_etourdi();
        }
    }

    /**
     * Chant de colère version berserk
     * @throws IOException toujours
     */
    private void colere_berserk() throws IOException {
        int jet;
        if (this.niveau >= 9) {
            jet = Input.D8();
        } else if (this.niveau >= 4) {
            jet = Input.D6();
        } else {
            jet = Input.D4();
        }
        jet += rand.nextInt(3) - 1;
        if (this.niveau >= 10) {
            jet += 2;
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
        else if (jet == 5){
            System.out.println("Les esprit de victimes innocentes font s'emplir de colère votre coeur.");
            berserk += 0.6f + rand.nextInt(7) * 0.1f; //0.6~1.2
        } else if (jet == 6){
            System.out.println("L'âme d'un guerrier vénérable vous transmet sa rage vengeresse.");
            berserk += 0.7f + rand.nextInt(9) * 0.1f; //0.7~1.5
        } // D8
        else if (jet == 7){
            System.out.println("Les esprits de guerriers morts au combat insufle une rage profonde dans votre âme !");
            berserk += 1f + rand.nextInt(5) * 0.2f; //1~1.8
        } else if (jet == 8){
            System.out.println("Vous êtes posséde par l'esprit d'un puissant combattant, emplie d'une haine profonde !");
            berserk += 1.5f + rand.nextInt(8) * 0.15f; //1.5~2.55
        } //D8 + 2
        else if (jet == 9){
            System.out.println("Un esprit amplie d'une haine sans fin vous possède !");
            berserk += 1.9f + rand.nextInt(6) * 0.25f; //1.9~3.15
        } else {
            System.out.println("RAAAAAAAAAAAAAAAAAAAH !!!! Vous êtes consummé par la haine, la rage et la folie !");
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
                Plusieurs nuages répondent à votre appel, choississez celui que vous voulez faire venir :\
                
                \t1: ↥☁∿λ
                \t2: ↥☁∘χ
                \t3: ↥☁≋θ
                \t4: ↥☁∇Ω""");
        switch(Input.read()){
            case "1" -> nuage_pluie();
            case "2" -> nuage_grele(ennemi);
            case "3" -> nuage_brume(ennemi);
            case "4" -> nuage_foudre(ennemi);
            default -> {
                System.out.println("Input unknow");
                nuage(ennemi);
            }
        }
    }

    /**
     * Appel des nuages version soin
     * @throws IOException toujours
     */
    private void nuage_pluie() throws IOException {
        System.out.println("Des nages apparaissent dans le ciel et une pluie légère commence à tomber.");
        int jet = Input.D10() + rand.nextInt(3) - 1;
        if (jet <= 2) {
            System.out.println("La pluie tombante recouvre vos blessures.");
            System.out.println("Chaque joueur et familier soigne de 2 points");
        }
        else if (jet <= 5) {
            System.out.println("Les goutes d'eau s'abatent sur vos blessures, qui commencent à se refermer.");
            System.out.println("Chaque joueur et familier soigne de " + jet + " points");
        }
        else if (jet <= 7) {
            System.out.println("Une pluie douce et appaissante vous recouvre.");
            System.out.println("Chaque joueur et familier soigne de " + (jet + 2) + " points");
        }
        else{
            System.out.println("Une force ancienne s'infiltre dans vos corps au travers les gouttes d'eau.");
            System.out.println("Chaque joueur et familier soigne de 10 points.");
            System.out.println("Chaque joueur et familier gagne temporairement 3 points de résistance.");
        }
    }

    /**
     * Appel des nuages version dps AOE
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void nuage_grele(Monstre ennemi) throws IOException {
        System.out.println("De sombres nuages s'amoncèlent au dessus de vous");
        int jet = Input.D10() + rand.nextInt(3) - 1;
        if (jet <= 2) {
            System.out.println("Une fine grèle vous frappe.");
            System.out.println("Chaque joueur et familier subit 1 point de dommage.");
            ennemi.dommage(1);
        }
        else if (jet <= 5) {
            System.out.println("La grèle s'abat sur vous.");
            System.out.println("Chaque joueur et familier subit 2 points de dommage.");
            ennemi.dommage(jet - rand.nextInt(2));
        }
        else if (jet <= 7) {
            System.out.println("Une violente tempête se lève et la grèle vous frappe.");
            System.out.println("Chaque joueur et familier subit 4 points de dommage.");
            ennemi.dommage(jet + rand.nextInt(3) -1);
            if(rand.nextBoolean()){
                ennemi.do_etourdi();
            }
        }
        else{
            System.out.println("Une immense tempête de neige vous frappe de plein fouet.");
            System.out.println("Chaque joueur et familier subit 7 points de dommage.");
            ennemi.dommage(8 + rand.nextInt(5));
            if(rand.nextBoolean()){
                ennemi.affecte();
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
        int jet = Input.D10() + rand.nextInt(3) - 1;
        if (jet <= 2) {
            System.out.println("C'est... joli ?");
        }
        else if (jet <= 5) {
            System.out.println("La brûme commence à vous encercler.");
            System.out.println("Chaque joueur et familier perds temporairement 1 point d'attaque.");
            ennemi.bostAtk(-2, false);
        }
        else if (jet <= 7) {
            System.out.println("Un épais brouillard vous recouvre.");
            System.out.println("Chaque joueur et familier perds temporairement 3 points d'attaque.");
            ennemi.bostAtk(-5, false);
        }
        else{
            System.out.println("Une brûme vous entoure, si dense que vous ne vous voyez presque plus.");
            System.out.println("Il est désormais impossible de tirer.");
            System.out.println("Il est désormais impossible de lancer un sort ciblé sur une autre cible que soit-même.");
            System.out.println("Chaque joueur et familier perds temporairement 8 points d'attaque.");
            ennemi.bostAtk(-15, false);
        }
    }

    /**
     * Appel des nuages version dps debuff
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void nuage_foudre(Monstre ennemi) throws IOException {
        System.out.println("Un nuage apparait au dessus de vous et commence à se rapprocher du sol");
        int jet = Input.D10() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("C'est... joli ?");
        }
        else if (jet <= 4) {
            System.out.println("Vous percevez un fugasse arc électrique.");
            ennemi.affecte();
        }
        else if (jet <= 6) {
            System.out.println("La foudre frappe l'ennemi.");
            ennemi.dommage(jet + rand.nextInt(5) - 3);
            ennemi.affecte();
        }
        else{
            System.out.println("Le nuage s'abat sur le monstre ennemi, suivi d'un éclair.");
            ennemi.dommage(jet + rand.nextInt(5) - 3);
            ennemi.affecte();
            ennemi.bostAtk(-2, false);
        }
    }

    /**
     * Applique la compétence "invocation des éléments" du shaman
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void element(Monstre ennemi) throws IOException {
        System.out.println("""
                Choississez un élément à invoquer :\
                
                \t1: ∆Ψ≋ξ
                \t2: ∆Ψ∴φ
                \t3: ∆Ψ⊡τ
                \t4: ∆Ψ∿μ""");
        switch(Input.read()){
            case "1" -> vent(ennemi);
            case "2" -> feu(ennemi);
            case "3" -> terre(ennemi);
            case "4" -> eau(ennemi);
            default -> {
                System.out.println("Input unknow");
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
        int jet = Input.D6() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("Une légère brise se fait sentir.");
        }
        else if (jet <= 4) {
            System.out.println("Une puissant vent souffle.");
            if(tir_bonus <= 2){
                tir_bonus = 2;
                tour_modif = rand.nextInt(3) -1 + Main.nbj;
            }
            System.out.println("Tous les tirs sont temporairement boostés.");
        }
        else if (jet <= 6) {
            System.out.println("De violente rafale se prononce.");
            if(tir_bonus <= 3){
                tir_bonus = 3;
                tour_modif = Main.nbj + rand.nextInt(3) -1 + rand.nextInt(Main.nbj);
            }
            System.out.println("Tous les tirs sont temporairement boostés.");
            ennemi.dommage(2);
        }
        else{
            System.out.println("Le vent est si puissant que vous avez du mal à ne pas être emporté.");
            if(tir_bonus <= 5){
                tir_bonus = 5;
                attaque_bonus = -3;
                tour_modif = Main.nbj * 2 + rand.nextInt(Main.nbj * 3);
            }
            System.out.println("Tous les tirs sont temporairement boostés.");
            System.out.println("Toutes les attaques sont temporairement baissés.");
            ennemi.dommage(5);
        }
    }

    /**
     * Invocation des éléments version debuff et dps
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void terre(Monstre ennemi) throws IOException {
        System.out.println("La terre commence à trembler...");
        int jet = Input.D6() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("Vous entendez un léger grondement.");
        }
        else if (jet <= 4) {
            System.out.println("Des fragment de roches s'arrachent du sol et herte l'ennemi.");
            ennemi.dommage(3 + rand.nextInt(2));
            if(rand.nextBoolean()){
                ennemi.do_etourdi();
            }
        }
        else if (jet <= 6) {
            System.out.println("Le sol se fends sous l'ennemi !");
            ennemi.dommage(3 + rand.nextInt(2));
            if(rand.nextBoolean()){
                ennemi.do_etourdi();
            }
            else {
                ennemi.affecte();
            }
        }
        else{
            System.out.println("Le sol sous l'ennemi se soulève !");
            ennemi.dommage(4 + rand.nextInt(3));
            ennemi.affecte();
        }
    }

    /**
     * Invocation des éléments version dps
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void feu(Monstre ennemi) throws IOException {
        System.out.println("Vous entendez de légers crépitements...");
        int jet = Input.D6() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("Must been the wind...");
        }
        else if (jet <= 3) {
            System.out.println("L'ennemi semble indisposé par quelque chose.");
            ennemi.dommage(2 + rand.nextInt(2));
        }
        else if (jet <= 5) {
            System.out.println("Le monstre prend feu !");
            ennemi.dommage(4 + rand.nextInt(3));
        }
        else if (jet <= 7) {
            System.out.println("Des flammes s'élève tout autour de l'adversaire !");
            ennemi.dommage(5 + rand.nextInt(5));
        }
        else{
            System.out.println("Un véritable brasier apparait aurour de l'ennemi !");
            ennemi.dommage(6 + rand.nextInt(7));
        }
    }

    /**
     * Invocation des éléments version buff/random
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void eau(Monstre ennemi) throws IOException {
        System.out.println("Vous entendez un léger gargouillement...");
        int jet = Input.D6() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("Vous sentez quelques gouttes de pluie.");
        }
        else if (jet <= 3) {
            System.out.println("Une pluie purificatrice s'abat.");
            System.out.println("Tous les joueurs récupèrent 1PP.");
        }
        else if (jet <= 5) {
            System.out.println("De l'eau jaillit de sous l'adversaire, le faisant glisser.");
            ennemi.dommage(1 + rand.nextInt(2));
            ennemi.do_etourdi(); // glissade
        }
        else if (jet <= 7) {
            System.out.println("Une vague magique frappe l'ennemi !");
            ennemi.dommage_magique(2 + rand.nextInt(2));
            ennemi.affecte(); // affaiblissement magique
        }
        else {
            System.out.println("Un torrent mystique s'abat sur le terrain, emportant l'ennemi et réveillant les joueurs.");
            ennemi.dommage(4 + rand.nextInt(4));
            ennemi.affecte();
            for(int i = 0; i < Main.nbj; i++){
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
                
                \t1: ¤✧▣ζ
                \t2: ¤✧∿η
                \t3: ¤✧⊕ρ
                \t4: ¤✧↑κ""");
        switch(Input.read()){
            case "1" -> benir_def();
            case "2" -> benir_soin();
            case "3" -> benir_vie();
            case "4" -> benir_force();
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
        int jet = Input.D8() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("La cible guérie de 2.");
        }
        else if (jet <= 4) {
            System.out.println("La cible guérie de " + (2 + jet) + ".");
        }
        else if (jet <= 6) {
            System.out.println("La cible guérie de 7.");
        }
        else if (jet <= 8) {
            System.out.println("La cible guérie de 9.");
        }
        else{
            System.out.println("La cible guérie de 11.");
        }
    }

    /**
     * Bénédiction version buff résistance
     * @throws IOException toujours
     */
    private void benir_vie() throws IOException {
        int jet = Input.D8() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("La cible gagne temporairement 1 point de résistance.");
        }
        else if (jet <= 3) {
            System.out.println("La cible gagne temporairement 2 points de résistance.");
        }
        else if (jet <= 5) {
            System.out.println("La cible gagne temporairement 4 points de résistance.");
        }
        else if (jet <= 7) {
            System.out.println("La cible gagne temporairement 6 points de résistance.");
        }
        else{
            System.out.println("La cible gagne temporairement 8 points de résistance.");
        }
    }

    /**
     * Bénédiction version buff attaque
     * @throws IOException toujours
     */
    private void benir_force() throws IOException {
        int jet = Input.D8() + rand.nextInt(3) - 1;
        if(jet <= 1) {
            System.out.println("La cible gagne temporairement 1 point d'attaque.");
        }
        else if (jet <= 3) {
            System.out.println("La cible gagne temporairement 2 points d'attaque.");
        }
        else if (jet <= 5) {
            System.out.println("La cible gagne temporairement 3 points d'attaque.");
        }
        else if (jet <= 7) {
            System.out.println("La cible gagne temporairement 4 points d'attaque.");
        }
        else{
            System.out.println("La cible gagne temporairement 6 points d'attaque.");
        }
    }

    /**
     * Bénédiction version buff résistance/armure
     * @throws IOException toujours
     */
    private void benir_def() throws IOException {
        int jet = Input.D8() + rand.nextInt(3) - 1;
        if(jet <= 3) {
            System.out.println("La cible gagne temporairement 1 point de résistance.");
        }
        else if (jet <= 6) {
            System.out.println("La cible gagne temporairement 3 points de résistance..");
        }
        else{
            System.out.println("La cible gagne temporairement 1 point d'armure.");
        }
    }

    @Override
    public boolean auto_ressuciter(int malus) throws IOException{
        if(this.niveau < 5){
            return false;
        }
        int palier_mort = 9 + malus;
        if(this.niveau >= 9){
            palier_mort -= 1;
        }
        return Input.D10() >= palier_mort; //20%~30% sans malus
    }

}
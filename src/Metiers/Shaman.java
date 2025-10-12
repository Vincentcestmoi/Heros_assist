package Metiers;

import Exterieur.Input;

import Enum.Metier;
import Enum.Position;
import Enum.Action;
import Enum.Competence;

import Monstre.Monstre;
import main.Combat;
import main.Main;

import java.io.IOException;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Shaman extends Joueur {
    Metier metier = Metier.SHAMAN;
    private int possession_atk;

    public Shaman(String nom, Position position, int ob_f) {
        super(nom, position, ob_f);
        vie = 4;
        attaque = 1;
        PP = "mana";
        PP_value = 0;
        PP_max = 0;
        caracteristique = "Ame errante, Second souffle, Eclaireur";
        competences = "Incantation, Lien, Paix intérieure";
        possession_atk = 0;
    }

    public Metier getMetier() {
        return metier;
    }

    protected String nomMetier(){
        return "shaman";
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
            if (!a_familier()) {
                text += "/(li)en";
            }
            text += "/(in)cantation";
        }
        else {
            text += "/(pa)ix intérieure";
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
                if (!a_familier()) {
                    return Action.LIEN;
                }
            }
            case "pa" -> {
                if (est_berserk()) {
                    return Action.CALME;
                }
            }
        }
        return super.action(choix, false);
    }

    @Override
    public boolean traite_action(Action action, Monstre ennemi) throws IOException {
        switch(action) {
            case INCANTATION -> {
                incantation(ennemi);
                return false;
            }
            case LIEN -> {
                lien(ennemi);
                return false;
            }
            case CALME -> {
                calme();
                return false;
            }
        }
        return super.traite_action(action, ennemi);
    }

    @Override
    protected int bonus_atk(){
        return possession_atk;
    }

    @Override
    public int bonus_exploration(){
        return rand.nextInt(2) /* eclaireur */;
    }

    @Override
    public boolean peut_jouer() {
        // peut jouer inconscient
        return est_actif() && !skip;
    }

    @Override
    public boolean peut_ressuciter() {
        return true;
    }

    public boolean ressuciter(int malus) throws IOException {
        if (malus > 2) {
            malus = 2;
        }
        int jet = Input.D6() - malus + rand.nextInt(3) - 1;
        if (jet <= 4) {
            System.out.println("Echec de la résurection");
            return false;
        }
        if (jet <= 6) {
            System.out.println("Résurection avec 1 (max) points de vie");
        }
        else {
            System.out.println("Résurection avec 2 (max) points de vie");
        }
        return true;
    }

    @Override
    public boolean peut_diriger_familier(){
        return est_actif() && a_familier_actif() && est_vivant();
    }

    @Override
    protected int berserk_fuite() throws IOException {
        if(!est_berserk()){
            return 0;
        }
        return Math.min(0, Math.round(Input.D6() * 0.1f - berserk));
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
        int ratio = (int) ((float) ennemi.getVie() / ennemi.getVieMax() * 12);
        if (ennemi.getCompetence() == Competence.PRUDENT || ennemi.getCompetence() == Competence.MEFIANT || ennemi.getCompetence() == Competence.SUSPICIEUX) {
            ratio += 4 + rand.nextInt(3);
        }
        System.out.println(getNom() + " tente de lier son âme à " + ennemi.getNom());
        int result = Input.D12() - ratio + rand.nextInt(3) - 1;
        if (result <= 1) {
            System.out.println("L'âme de " + getNom() + " est violemment rejeté par celle de " + ennemi.getNom() + " !");
            rendre_mort();
        }
        else if (result <= 4) {
            System.out.println(getNom() + " n'est pas parvenu à se lier à " + ennemi.getNom());
        }
        else {
            System.out.println("Les âmes de " + ennemi.getNom() + " et de " + getNom() + " entre en communion !");
            setOb(min(7, rand.nextInt(result) + 1));
            Combat.stop_run();
            ennemi.presente_familier();
        }
    }


    /**
     * Applique la compétence "incantation" du shaman
     * @param ennemi le monstre adverse
     * @throws IOException toujours
     */
    private void incantation(Monstre ennemi) throws IOException {
        switch(Input.incantation()){
            case COLERE -> colere(ennemi);
            case NUAGE -> nuage(ennemi);
            case ELEMENTAIRE -> element(ennemi);
            case BENIE -> benir();
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
                \t2: ≠‼ψχ
                \t3: ≠‼∅δ""");
        switch(Input.read()){
            case "1" -> colere_boost();
            case "2" -> colere_attaque(ennemi);
            case "3" -> colere_berserk();
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
        int jet = Input.D8() + rand.nextInt(3) - 1;
        if(jet <= 3){
            System.out.println("Les esprits des ancients guerriers exalte vos actes.");
            possession_atk += 1;
        }
        else if (jet <= 5){
            System.out.println("Les âmes de vos ancestres guerriers renforcent vos actes.");
            possession_atk += 2;
        }
        else if (jet <= 8){
            System.out.println("L'âme d'un ancien guerrier guide votre main.");
            possession_atk += 4;
        }
        else{
            System.out.println("Votre âme entre en symbiose avec celle de vos ancestres belliqueux.");
            possession_atk += 7;
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
        int jet = Input.D10() + rand.nextInt(3) - 1;
        if(jet <= 3){
            attaque = 2;
        }
        else if(jet <= 5){
            attaque = 4 + rand.nextInt(3);
        }
        else if(jet <= 7){
            attaque = 6 + rand.nextInt(4);
        }
        else{
            attaque = 9 + rand.nextInt(5);
            ennemi.affecte();
        }
        ennemi.dommage_magique(attaque);
    }

    /**
     * Chant de colère version berserk
     * @throws IOException toujours
     */
    private void colere_berserk() throws IOException {
        int jet = Input.D10() + rand.nextInt(3) - 1;
        if (jet <= 3) {
            System.out.println("Un esprit vous emplie de colère.");
            berserk += max(0.1f, jet * 0.1f);
        } else if (jet <= 5) {
            System.out.println("L'âme d'un guerrier emplie votre coeur de haine !");
            berserk += jet * 0.2f + 0.1f;
        } else if (jet <= 7) {
            System.out.println("Une âme pleine de colère emplie votre coeur de rage !");
            berserk += 1.4f + rand.nextInt(3) * 0.1f;
        } else {
            System.out.println("L'âme d'un ancien guerrier tombé au combat incruste votre coeur d'une haine féroce !");
            berserk += 1.5f + rand.nextInt(3) * 0.2f;
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
        return (Input.D20() > 17); //15%
    }

}
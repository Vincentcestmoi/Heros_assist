package main;

import Auxiliaire.Texte;
import Auxiliaire.Utilitaire;
import Enum.Choix_Agareh;
import Enum.Grade;
import Enum.Position;
import Exterieur.Input;
import Metiers.Joueur;
import Monstre.Lieu;
import Monstre.Monstre;

import java.io.IOException;
import java.util.Random;

public class Agareh {
    final Position pos;
    protected boolean forge;                //1 main, 2 mains
    protected final int forge_niveau;
    protected boolean archerie;             //arc, bonus arc, flèches spé
    protected final int archerie_niveau;
    protected boolean armurerie;            //casque, armure
    protected final int armurerie_niveau;
    protected boolean marchand_bouclier;    //bouclier
    protected final int marchand_bouclier_niveau;
    protected boolean tanneur;             //sac, ceinture
    protected final int tanneur_niveau;
    protected boolean bijouterie;           //bracelet
    protected final int bijouterie_niveau;
    protected boolean mage;                 //rune, aucune intéraction possible
    protected final int mage_niveau;
    protected boolean ecurie;               //familier
    protected final int ecurie_niveau;
    
    //************************************************INITIALISATION**************************************************//
    
    Agareh(Position pos) {
        this.pos = pos;
        int[] value = switch (pos){
            case ENFERS, OLYMPE, ASCENDANT -> throw new IllegalStateException("ERREUR : l'AGAREH n'est pas accessible de puis %s !".formatted(pos.name()));
            case PRAIRIE -> init_prairie();
            case VIGNES -> init_vigne();
            case TEMPLE -> init_temple();
            case MER -> init_mer();
            case MONTS -> init_mont();
        };
        //on assigne les valeurs
        this.forge_niveau = value[0];
        this.archerie_niveau = value[1];
        this.armurerie_niveau = value[2];
        this.marchand_bouclier_niveau = value[3];
        this.tanneur_niveau = value[4];
        this.bijouterie_niveau = value[5];
        this.mage_niveau = value[6];
        this.ecurie_niveau = value[7];
        
        this.forge = this.forge_niveau > 0;
        this.archerie = this.archerie_niveau > 0;
        this.armurerie = this.armurerie_niveau > 0;
        this.marchand_bouclier = this.marchand_bouclier_niveau > 0;
        this.tanneur = this.tanneur_niveau > 0;
        this.bijouterie = this.bijouterie_niveau > 0;
        this.mage = this.mage_niveau > 0;
        this.ecurie = this.ecurie_niveau > 0;
    }
    
    /**
     * Renvoie les valeurs des niveaux des bâtiments de la prairie
     * @return une liste d'int correspondant aux niveaux {forge, archerie, armurerie, marchand_bouclier, styliste, bijouterie, mage, ecurie}
     */
    private int[] init_prairie() {
        int forge = 0;
        int archerie = 1;
        int armurerie = 1;
        int marchand_bouclier = 0;
        int styliste = 1;
        int bijouterie = 1;
        int mage = 0;
        int ecurie = 0;
        return new int[] {forge, archerie, armurerie, marchand_bouclier, styliste, bijouterie, mage, ecurie};
    }
    
    /**
     * Renvoie les valeurs des niveaux des bâtiments de la vigne
     * @return une liste d'int correspondant aux niveaux {forge, archerie, armurerie, marchand_bouclier, styliste, bijouterie, mage, ecurie}
     */
    private int[] init_vigne() {
        int forge = 3;
        int archerie = 0;
        int armurerie = 2;
        int marchand_bouclier = 1;
        int styliste = 0;
        int bijouterie = 0;
        int mage = 0;
        int ecurie = 1;
        return new int[] {forge, archerie, armurerie, marchand_bouclier, styliste, bijouterie, mage, ecurie};
    }
    
    /**
     * Renvoie les valeurs des niveaux des bâtiments du temple
     * @return une liste d'int correspondant aux niveaux {forge, archerie, armurerie, marchand_bouclier, styliste, bijouterie, mage, ecurie}
     */
    private int[] init_temple() {
        int forge = 0;
        int archerie = 3;
        int armurerie = 0;
        int marchand_bouclier = 2;
        int styliste = 2;
        int bijouterie = 0;
        int mage = 1;
        int ecurie = 2;
        return new int[] {forge, archerie, armurerie, marchand_bouclier, styliste, bijouterie, mage, ecurie};
    }
    
    /**
     * Renvoie les valeurs des niveaux des bâtiments de la mer
     * @return une liste d'int correspondant aux niveaux {forge, archerie, armurerie, marchand_bouclier, styliste, bijouterie, mage, ecurie}
     */
    private int[] init_mer() {
        int forge = 2;
        int archerie = 0;
        int armurerie = 3;
        int marchand_bouclier = 3;
        int styliste = 0;
        int bijouterie = 2;
        int mage = 0;
        int ecurie = 0;
        return new int[] {forge, archerie, armurerie, marchand_bouclier, styliste, bijouterie, mage, ecurie};
    }
    
    /**
     * Renvoie les valeurs des niveaux des bâtiments des monts
     * @return une liste d'int correspondant aux niveaux {forge, archerie, armurerie, marchand_bouclier, styliste, bijouterie, mage, écurie}
     */
    private int[] init_mont() {
        int forge = 0;
        int archerie = 1;
        int armurerie = 0;
        int marchand_bouclier = 0;
        int styliste = 3;
        int bijouterie = 0;
        int mage = 3;
        int ecurie = 2;
        return new int[] {forge, archerie, armurerie, marchand_bouclier, styliste, bijouterie, mage, ecurie};
    }
    
    //************************************************MAIN************************************************************//
    
    static public void visiter(Joueur joueur) throws IOException {
        
        if (joueur.getPosition() == Position.OLYMPE || joueur.getPosition() == Position.ENFERS || joueur.getPosition() == Position.ASCENDANT) {
            return;
        }
        Texte.entrer_agareh();
        
        Agareh agareh = new Agareh(joueur.getPosition());
        
        Utilitaire.LoopGuard garde = new Utilitaire.LoopGuard();
        boolean run = true;
        Choix_Agareh choix;
        while (run) {
            System.out.println();
            garde.check();
            choix = Input.agareh(agareh, joueur);
            switch (choix) {
                case PARTIR -> run = false;
                case VENDRE_CADAVRE -> agareh.prix_cadavre(joueur);
                case VENDRE_ITEM -> agareh.prix_item();
                case FORGE -> agareh.forge(joueur);
                case ECURIE -> agareh.ecurie(joueur);
                case MARCHE -> agareh.marche(joueur);
                case TANNEUR -> agareh.tanneur(joueur);
                case ARMURERIE -> agareh.armurerie(joueur);
                case PROMOTION -> agareh.promeut(joueur);
                case BIJOUTERIE -> agareh.bijouterie(joueur);
                case MAGASIN_ARC -> agareh.arc(joueur);
                case MARCHAND_BOUCLIER -> agareh.bouclier(joueur);
                case MANNEQUIN -> agareh.frapper_pantin();
                case TRANSPORT -> {
                    if(agareh.transport(joueur)) {
                        run = false;
                    }
                }
            }
        }
        System.out.println("Vous quittez l'AGAREH.\n");
    }
    
    //************************************************action**********************************************************//
    
    /**
     * Propose au joueur d'augmenter son grade contre de l'argent
     * @param joueur le joueur dont il faut augmenter le rang
     */
    private void promeut(Joueur joueur) {
        if(joueur.getGrade() == Grade.AUCUN){
            System.out.println("Si vous souhaitez devenir membre de l'AGAREH, vous devez verser 5PO.");
        } else {
            System.out.printf("Vous pouvez monter en rang, mais cela vous coutera %d PO.\n", joueur.getGrade().ordinal() * 2 + 3);
        }
        if(Input.yn("Acceptez vous de payer ?")){
            joueur.upgradeGrade();
        }
    }
    
    /**
     * Envoie le joueur au marché
     * @param joueur le joueur à envoyer
     */
    private void marche(Joueur joueur) {
        System.out.println("L'AGAREH peut vous mettre en contact avec un marchand pour 1PO.");
        if(Input.yn("Acceptez vous de payer ?")) {
            joueur.aller_au_marche(joueur.getGrade().ordinal() - 1);
        }
    }
    
    private boolean transport(Joueur joueur){
        System.out.println("Où voulez vous vous rendre ?");
        int grade = joueur.getGrade().ordinal();
        for(int i = 0; i < grade; i++){
            System.out.printf("%d : %s\n", i, Main.texte_pos(Position.values()[i]));
        }
        System.out.println("grade : annuler");
        
        int choix = Input.readInt();
        
        if(choix == grade){
            return false;
        }
        if (choix > grade) {
            System.out.println("Votre rang est trop faible pour demander à être conduit si loin.");
            return transport(joueur);
        }
        if(choix < 0){
            System.out.println("Input Unknown");
            return transport(joueur);
        }
        System.out.printf("Vous êtes transporté %s\n", Main.texte_pos(Position.values()[choix]));
        joueur.setPosition(Position.values()[choix]);
        return true;
    }
    
    //************************************************BÂTIMENT********************************************************//
    
    private void forge(Joueur joueur) {
        if(!forge){
            return;
        }
        int niveau_artisan = this.forge_niveau;
        int niveau = pres_forge(joueur, niveau_artisan);
        forge_action(niveau, niveau_artisan);
        post_forge();
    }
    
    private int pres_forge(Joueur joueur, int niveau_artisan) {
        System.out.println("Vous entrez dans une forge.");
        int niveau = GradeInsuffisant(niveau_artisan, joueur);
        System.out.println("Bonjour, bienvenue dans ma forge ! Je peux améliorer vos armes si vous me fournissez les matériaux pour.");
        System.out.printf("Je peux améliorer votre armes jusqu'au niveau +%d.\n", niveau);
        return niveau;
    }
    
    private void post_forge() {
        System.out.println("Le forgeron est occupé, il ne pourra plus vous recevoir aujourd'hui.");
        this.forge = false;
    }
    
    private void forge_action(int niveau, int niveau_artisan) {
        
        Utilitaire.LoopGuard garde = new Utilitaire.LoopGuard();
        int type_equipement;
        do {
            garde.check();
            System.out.println("""
                        1 : faire améliorer une arme à une main
                        2 : faire améliorer une arme à deux main
                        3 : partir
                    """);
            type_equipement = Input.readInt();
        } while(type_equipement < 1 || type_equipement > 3);
        
        if(type_equipement == 3){
            return;
        }
        
        int prix = (type_equipement * 3); //3 pour 1 main, 6 pour 2 mains
        System.out.printf("Le prix de base pour ce genre d'arme est de %d PO.\n", prix);
        
        System.out.println("Quel est le niveau de l'arme que vous voulez améliorer ?");
        int niveau_equipement = niveau_equipement(niveau, niveau_artisan);
        
        //annulation
        if(niveau_equipement == -1){
            forge_action(niveau, niveau_artisan);
            return;
        }
        
        prix += 2 * (niveau_equipement + 1); //prix linéaire par amélioration
        System.out.printf("Le prix pour ce genre d'amélioration est de %d PO.\n", prix);
        
        System.out.println("Quel est le niveau du matériaux que vous voulez que j'utilise ?");
        int niveau_materiau = niveau_materiau(niveau, niveau_equipement, niveau_artisan);
        
        
        if(niveau_materiau == -1){
            forge_action(niveau, niveau_artisan);
            return;
        }
        
        prix += niveau_materiau * 2;
        System.out.printf("Cela fera un total de %d Po\n", prix);
        
        if(!Input.yn("Acceptez vous de payer ?")){
            forge_action(niveau, niveau_artisan);
            return;
        }
        
        int[] bonus = analyse();
        
        System.out.println("Le forgeron améliore votre arme.");
        
        int atk = type_equipement + bonus[0];
        int res = bonus[1];
        if(atk > 0) {
            System.out.printf("L'attaque de votre arme augmente de %d.\n", atk);
        } else if (atk < 0) {
            System.out.printf("L'attaque de votre arme diminue de %d.\n", -atk);
        }
        if(res > 0) {
            System.out.printf("Votre arme vous fournit désormais %d points de resistance additionnels.\n", res);
        } else if (res < 0) {
            System.out.printf("Les modification de votre arme lui font réduire de %d points votre résistance.\n", -res);
        }
        System.out.printf("Votre arme est à présent de niveau +%d.\n", niveau_equipement + 1);
    }
    
    private void arc(Joueur joueur) {
        if(!archerie){
            return;
        }
        int niveau_artisan = this.archerie_niveau;
        int niveau = pres_arc(joueur, niveau_artisan);
        arc_action(niveau, niveau_artisan);
        post_arc();
    }
    
    private int pres_arc(Joueur joueur, int niveau_artisan) {
        System.out.println("Vous entrez dans un magasin d'arc.");
        int niveau = GradeInsuffisant(niveau_artisan, joueur);
        System.out.println("Bonjour, bienvenue dans ma boutique ! Je peux améliorer vos arcs et flèches si vous me fournissez les matériaux pour.");
        System.out.printf("Je peux les améliorer jusqu'au niveau +%d.\n", niveau);
        return niveau;
    }
    
    private void post_arc() {
        System.out.println("Le marchand d'arc est occupé, il ne pourra plus vous recevoir aujourd'hui.");
        this.archerie = false;
    }
    
    private void arc_action(int niveau, int niveau_artisan) {
        
        Utilitaire.LoopGuard garde = new Utilitaire.LoopGuard();
        int type_equipement;
        do {
            garde.check();
            System.out.println("""
                        1 : faire améliorer un arc
                        2 : faire améliorer une amélioration d'arc
                        3 : partir
                    """);
            type_equipement = Input.readInt();
        } while(type_equipement < 1 || type_equipement > 3);
        
        if(type_equipement == 3){
            return;
        }
        
        int prix;
        if (type_equipement == 1){
            prix = 3;
        } else {
            prix = 7;
        }
        System.out.printf("Le prix de base pour ce genre d'équipement est de %d PO.\n", prix);
        
        System.out.println("Quel est le niveau de l'équipement que vous voulez améliorer ?");
        int niveau_equipement = niveau_equipement(niveau, niveau_artisan);
        
        //annulation
        if(niveau_equipement == -1){
            arc_action(niveau, niveau_artisan);
            return;
        }
        
        if(type_equipement == 1){
            prix += Main.corriger(1.5f * (niveau_equipement + 1)); //arc linéaire par amélioration
        } else {
            prix += 2 * (niveau_equipement + 1); //linéaire, mais plus cher
        }
        System.out.printf("Le prix pour ce genre d'amélioration est de %d PO.\n", prix);
        
        System.out.println("Quel est le niveau du matériaux que vous voulez que j'utilise ?");
        int niveau_materiau = niveau_materiau(niveau, niveau_equipement, niveau_artisan);
        
        
        if(niveau_materiau == -1){
            arc_action(niveau, niveau_artisan);
            return;
        }
        
        prix += niveau_materiau * 2;
        System.out.printf("Cela fera un total de %d PO\n", prix);
        
        if(!Input.yn("Acceptez vous de payer ?")){
            forge_action(niveau, niveau_artisan);
            return;
        }
        
        int[] bonus = analyse();
        
        System.out.println("Le marchand d'arc améliore votre équipement.");
        
        int atk = 1 + bonus[0];
        if(atk > 0) {
            System.out.printf("La puissance de vos tir augmente de %d.\n", atk);
        } else if (atk < 0) {
            System.out.printf("La puissance de vos tir diminue de %d.\n", -atk);
        }
        System.out.printf("Votre équipement est à présent de niveau +%d.\n", niveau_equipement + 1);
    }
    
    private void bouclier(Joueur joueur) {
        if(!marchand_bouclier){
            return;
        }
        int niveau_artisan = this.marchand_bouclier_niveau;
        int niveau = pres_bouclier(joueur, niveau_artisan);
        bouclier_action(niveau, niveau_artisan);
        post_bouclier();
    }
    
    private int pres_bouclier(Joueur joueur, int niveau_artisan) {
        System.out.println("Vous entrez dans chez un fabriquant de bouclier.");
        int niveau = GradeInsuffisant(niveau_artisan, joueur);
        System.out.println("Bonjour, bienvenue dans mon atelier ! Je peux renforcer vos bouclier si vous me fournissez les matériaux pour.");
        System.out.printf("Je peux améliorer votre bouclier jusqu'au niveau +%d.\n", niveau);
        return niveau;
    }
    
    private void post_bouclier() {
        System.out.println("Le fabriquant est occupé, il ne pourra plus vous recevoir aujourd'hui.");
        this.marchand_bouclier = false;
    }
    
    private void bouclier_action(int niveau, int niveau_artisan) {
        
        Utilitaire.LoopGuard garde = new Utilitaire.LoopGuard();
        int type_equipement;
        do {
            garde.check();
            System.out.println("""
                        1 : faire améliorer un bouclier
                        2 : partir
                    """);
            type_equipement = Input.readInt();
        } while(type_equipement < 1 || type_equipement > 2);
        
        if(type_equipement == 2){
            return;
        }
        System.out.println("Le prix de base pour ce genre d'équipement est de 4 PO.");
        
        System.out.println("Quel est le niveau de l'équipement que vous voulez améliorer ?");
        int niveau_equipement = niveau_equipement(niveau, niveau_artisan);
        
        //annulation
        if(niveau_equipement == -1){
            bouclier_action(niveau, niveau_artisan);
            return;
        }
        
        int prix = 4 + niveau_equipement + 1; //augmentation lente
        System.out.printf("Le prix pour ce genre d'amélioration est de %d PO.\n", prix);
        
        System.out.println("Quel est le niveau du matériaux que vous voulez que j'utilise ?");
        int niveau_materiau = niveau_materiau(niveau, niveau_equipement, niveau_artisan);
        
        
        if(niveau_materiau == -1){
            bouclier_action(niveau, niveau_artisan);
            return;
        }
        
        prix += niveau_materiau * 2;
        System.out.printf("Cela fera un total de %d PO\n", prix);
        
        if(!Input.yn("Acceptez vous de payer ?")){
            bouclier_action(niveau, niveau_artisan);
            return;
        }
        
        int[] bonus = analyse();
        
        System.out.println("L'artisan améliore votre bouclier.");
        
        int atk = bonus[0];
        int res = bonus[1] + 1;
        int def = bonus[2];
        Random r = new Random();
        if(r.nextInt(3) == 0){
            atk += 1;
        } else {
            res += 1;
        }
        presenterModif(atk, res, def);
        System.out.printf("Votre bouclier est à présent de niveau +%d.\n", niveau_equipement + 1);
    }
    
    private void armurerie(Joueur joueur) {
        if(!armurerie){
            return;
        }
        int niveau_artisan = this.armurerie_niveau;
        int niveau = pres_armurerie(joueur, niveau_artisan);
        armurerie_action(niveau, niveau_artisan);
        post_armurerie();
    }
    
    private int pres_armurerie(Joueur joueur, int niveau_artisan) {
        System.out.println("Vous entrez dans une armurerie.");
        int niveau = GradeInsuffisant(niveau_artisan, joueur);
        System.out.println("Bonjour, bienvenue dans mon atelier ! Je peux améliorer vos casques et armures si vous me fournissez les matériaux pour.");
        System.out.printf("Je peux améliorer votre équipements jusqu'au niveau +%d.\n", niveau);
        return niveau;
    }
    
    private void post_armurerie() {
        System.out.println("L'armurier est occupé, il ne pourra plus vous recevoir aujourd'hui.");
        this.armurerie = false;
    }
    
    private void armurerie_action(int niveau, int niveau_artisan) {
        
        Utilitaire.LoopGuard garde = new Utilitaire.LoopGuard();
        int type_equipement;
        do {
            garde.check();
            System.out.println("""
                        1 : faire améliorer un casque
                        2 : faire améliorer une armure
                        3 : partir
                    """);
            type_equipement = Input.readInt();
        } while(type_equipement < 1 || type_equipement > 3);
        
        if(type_equipement == 3){
            return;
        }
        
        int prix;
        if (type_equipement == 1){
            prix = 3;
        } else {
            prix = 5;
        }
        System.out.printf("Le prix de base pour ce genre d'équipement est de %d PO.\n", prix);
        
        System.out.println("Quel est le niveau de l'équipement que vous voulez améliorer ?");
        int niveau_equipement = niveau_equipement(niveau, niveau_artisan);
        
        //annulation
        if(niveau_equipement == -1){
            armurerie_action(niveau, niveau_artisan);
            return;
        }
        
        if(type_equipement == 1){
            prix += Main.corriger(1.4f * (niveau_equipement + 1)); //casque
        } else {
            prix += Main.corriger(1.8f * (niveau_equipement + 1)); //armure
        }
        System.out.printf("Le prix pour ce genre d'amélioration est de %d PO.\n", prix);
        
        System.out.println("Quel est le niveau du matériaux que vous voulez que j'utilise ?");
        int niveau_materiau = niveau_materiau(niveau, niveau_equipement, niveau_artisan);
        
        
        if(niveau_materiau == -1){
            armurerie_action(niveau, niveau_artisan);
            return;
        }
        
        prix += niveau_materiau * 2;
        System.out.printf("Cela fera un total de %d PO\n", prix);
        
        if(!Input.yn("Acceptez vous de payer ?")){
            armurerie_action(niveau, niveau_artisan);
            return;
        }
        
        int[] bonus = analyse();
        
        System.out.println("L'armurier améliore votre équipement.");
        
        int res = bonus[1];
        int def = bonus[2];
        if(type_equipement == 1 ){ //casque
            res += 1;
        } else { //armure
            res += 2;
            if(niveau_materiau >= 3){
                def += 1;
            }
        }
        
        presenterModif(0, res, def);
        System.out.printf("Votre équipement est à présent de niveau +%d.\n", niveau_equipement + 1);
    }
    
    private void tanneur(Joueur joueur) {
        if(!tanneur){
            return;
        }
        int niveau_artisan = this.tanneur_niveau;
        int niveau = pres_tanneur(joueur, niveau_artisan);
        tanneur_action(niveau, niveau_artisan);
        post_tanneur();
    }
    
    private int pres_tanneur(Joueur joueur, int niveau_artisan) {
        System.out.println("Vous entrez dans une tannerie.");
        int niveau = GradeInsuffisant(niveau_artisan, joueur);
        System.out.println("Bonjour, bienvenue dans mon atelier ! Je peux améliorer vos sacs et ceintures si vous me fournissez les matériaux pour.");
        System.out.printf("Je peux améliorer votre affaires jusqu'au niveau +%d.\n", niveau);
        return niveau;
    }
    
    private void post_tanneur() {
        System.out.println("Le tanneur est occupé, il ne pourra plus vous recevoir aujourd'hui.");
        this.tanneur = false;
    }
    
    private void tanneur_action(int niveau, int niveau_artisan) {
        
        Utilitaire.LoopGuard garde = new Utilitaire.LoopGuard();
        int type_equipement;
        do {
            garde.check();
            System.out.println("""
                        1 : faire améliorer un sac
                        2 : faire améliorer une ceinture
                        3 : partir
                    """);
            type_equipement = Input.readInt();
        } while(type_equipement < 1 || type_equipement > 3);
        
        if(type_equipement == 3){
            return;
        }
        
        int prix;
        if (type_equipement == 1){ //sac
            prix = 6;
        } else { //ceinture
            prix = 5;
        }
        System.out.printf("Le prix de base pour ce genre d'équipement est de %d PO.\n", prix);
        
        System.out.println("Quel est le niveau de l'équipement que vous voulez améliorer ?");
        int niveau_equipement = niveau_equipement(niveau, niveau_artisan);
        
        //annulation
        if(niveau_equipement == -1){
            tanneur_action(niveau, niveau_artisan);
            return;
        }
        
        prix += Main.corriger(1.3f * (niveau_equipement + 1));
        
        System.out.printf("Le prix pour ce genre d'amélioration est de %d PO.\n", prix);
        
        System.out.println("Quel est le niveau du matériaux que vous voulez que j'utilise ?");
        int niveau_materiau = niveau_materiau(niveau, niveau_equipement, niveau_artisan);
        
        
        if(niveau_materiau == -1){
            tanneur_action(niveau, niveau_artisan);
            return;
        }
        
        prix += niveau_materiau * 2;
        System.out.printf("Cela fera un total de %d PO\n", prix);
        
        if(!Input.yn("Acceptez vous de payer ?")){
            tanneur_action(niveau, niveau_artisan);
            return;
        }
        
        int[] bonus = analyse();
        
        System.out.println("Le tanneur améliore votre équipement.");
        
        Random r = new Random();
        
        int atk = 0;
        int res = bonus[1];
        if(type_equipement == 1 ){ //sac
            res += 1;
            if(niveau_materiau >= 3){
                System.out.println("Votre sac peut stocker un objet ou 1 main de plus.");
            }
            presenterModif(0, res, 0);
        } else { //ceinture
            if(r.nextBoolean()) {
                res += 2;
            } else {
                atk += 1;
            }
            presenterModif(atk, res, 0);
        }
        System.out.printf("Votre équipement est à présent de niveau +%d.\n", niveau_equipement + 1);
    }
    
    private void bijouterie(Joueur joueur) {
        if(!bijouterie){
            return;
        }
        int niveau_artisan = this.bijouterie_niveau;
        int niveau = pres_bijouterie(joueur, niveau_artisan);
        bijouterie_action(niveau, niveau_artisan);
        post_bijouterie();
    }
    
    private int pres_bijouterie(Joueur joueur, int niveau_artisan) {
        System.out.println("Vous entrez dans une bijouterie.");
        int niveau = GradeInsuffisant(niveau_artisan, joueur);
        System.out.println("Bonjour, bienvenue dans mon magasin ! Je peux améliorer vos bracelets si vous me fournissez les matériaux pour.");
        System.out.printf("Je peux améliorer votre affaires jusqu'au niveau +%d.\n", niveau);
        return niveau;
    }
    
    private void post_bijouterie() {
        System.out.println("Le bijoutier est occupé, il ne pourra plus vous recevoir aujourd'hui.");
        this.bijouterie = false;
    }
    
    private void bijouterie_action(int niveau, int niveau_artisan) {
        
        Utilitaire.LoopGuard garde = new Utilitaire.LoopGuard();
        int type_equipement;
        do {
            garde.check();
            System.out.println("""
                        1 : faire améliorer un bracelet
                        2 : partir
                    """);
            type_equipement = Input.readInt();
        } while(type_equipement < 1 || type_equipement > 2);
        
        if(type_equipement == 2){
            return;
        }
        
        int prix = 6;
        System.out.printf("Le prix de base pour ce genre d'équipement est de %d PO.\n", prix);
        
        System.out.println("Quel est le niveau de l'équipement que vous voulez améliorer ?");
        int niveau_equipement = niveau_equipement(niveau, niveau_artisan);
        
        //annulation
        if(niveau_equipement == -1){
            bijouterie_action(niveau, niveau_artisan);
            return;
        }
        
        prix += Main.corriger(2.4f * (niveau_equipement + 1));
        
        System.out.printf("Le prix pour ce genre d'amélioration est de %d PO.\n", prix);
        
        System.out.println("Quel est le niveau du matériaux que vous voulez que j'utilise ?");
        int niveau_materiau = niveau_materiau(niveau, niveau_equipement, niveau_artisan);
        
        
        if(niveau_materiau == -1){
            bijouterie_action(niveau, niveau_artisan);
            return;
        }
        
        prix += niveau_materiau * 2;
        System.out.printf("Cela fera un total de %d PO\n", prix);
        
        if(!Input.yn("Acceptez vous de payer ?")){
            bijouterie_action(niveau, niveau_artisan);
            return;
        }
        
        System.out.println("Le bijoutier améliore votre équipement.");
        
        Random r = new Random();
        
        int atk = 0;
        int res = 1;
        int def = 0;
        int proba_res = 5;
        int proba_atk = 2;
        int proba_def = 0;
        switch(niveau_materiau){
            case 1 -> proba_res += 1;
            case 2 -> {
                proba_res += 1;
                proba_atk += 1;
                proba_def += 1;
            }
            case 3 -> {
                proba_res -= 2;
                proba_def += 2;
            }
        }
        int t = r.nextInt(proba_res + proba_def + proba_atk);
        if(t < proba_res){
            res += 1;
        } else if (t - proba_res < proba_atk){
            atk += 1;
        } else {
            def += 1;
        }
        
        presenterModif(atk, res, def);
        System.out.printf("Votre bracelet est à présent de niveau +%d.\n", niveau_equipement + 1);
    }
    
    private void ecurie(Joueur joueur) throws IOException {
        if(!ecurie){
            return;
        }
        int niveau = pres_ecurie(joueur, this.ecurie_niveau);
        ecurie_action(niveau, joueur);
        post_ecurie();
    }
    
    private int pres_ecurie(Joueur joueur, int niveau_artisan) {
        System.out.println("Vous entrez dans une écurie.");
        int niveau = GradeInsuffisant(niveau_artisan, joueur);
        System.out.println("Bonjour, bienvenue dans mon ranch ! Je peux vous aider à vous familiariser avec votre compagnon.");
        return niveau;
    }
    
    private void post_ecurie() {
        System.out.println("Le gestionnaire est occupé, il ne pourra plus vous recevoir aujourd'hui.");
        this.ecurie = false;
    }
    
    private void ecurie_action(int niveau, Joueur joueur) throws IOException {
        System.out.println("Cela vous coutera 3PO.");
        if(Input.yn("Acceptez vous de payer ?")){
            joueur.dresser(niveau);
        }
    }
    
    private void prix_item() {
        System.out.printf("Les sacs et ceintures sont achetées pour %d PO.\n", this.tanneur_niveau + 1);
        System.out.printf("Les casques et armures sont achetées pour %d PO.\n", this.armurerie_niveau + 1);
        System.out.printf("Les armes à une et deux mains sont achetées pour %d PO.\n", this.forge_niveau + 1);
        System.out.printf("Les boucliers sont achetées pour %d PO.\n", this.marchand_bouclier_niveau + 1);
        System.out.printf("Les bracelet sont achetées pour %d PO.\n", 2 * (this.bijouterie_niveau + 1));
        System.out.printf("Les arcs et améliorations d'arcs sont achetées pour %d PO.\n", this.archerie_niveau + 1);
        System.out.printf("Les runes sont achetées pour %d PO.\n", 3 * (this.mage_niveau + 1));
        System.out.println("Tous les autres objets sont achetées pour 1 PO.");
    }
    
    private void prix_cadavre(Joueur joueur) {
        System.out.println("Quel est l'état de votre cadavre ?");
        int prix = Input.readInt();
        switch(joueur.getGrade()){
            case AUCUN -> System.out.println("Vous ne devriez pas être ici avec un grade si faible !");
            case FER -> prix -= 1;
            case ACIER -> {}
            case ARGENT -> prix += 1;
            case OR -> prix += 2;
            case DIAMANT -> prix += 3;
            case MITHRIL -> prix += 4;
            case DRAGON -> prix += 5;
        }
        switch (this.pos){
            case ENFERS, OLYMPE, ASCENDANT -> throw new IllegalStateException("ERREUR : l'AGAREH n'est pas accessible de puis %s !".formatted(pos.name()));
            case TEMPLE, MER -> prix += 1;
            case MONTS -> prix += 2;
            case VIGNES, PRAIRIE -> {}
        }
        Random r = new Random();
        prix += 1 - r.nextInt(3);
        if(prix > 0) {
            System.out.printf("Le cadavre vous est racheté pour %d PO\n", prix);
        } else {
            System.out.println("Ce cadavre n'a aucune valeur.");
        }
    }
    
    
    //************************************************Auxiliaire******************************************************//
    
    /**
     * Calcule si un joueur peut être promu
     * @param joueur le joueur à examiner
     * @return true s'il peut être promu, false sinon
     */
    public static boolean peut_promouvoir(Joueur joueur){
        int value_grade = joueur.getGrade().ordinal();
        int value_pos = joueur.getPosition().ordinal();
        return value_grade < value_pos;
    }
    
    /**
     * Renvoie le niveau maximum utilisable de l'établissement. avertie le joueur s'il ne peut bénéficier de tout
     * @param value la valeur du niveau de l'établissement
     * @param joueur le joueur
     * @return le minimum entre la valeur de l'établissement et le rang du joueur
     */
    public int GradeInsuffisant(int value, Joueur joueur){
        int grade = joueur.getGrade().ordinal();
        if(grade < value){
            System.out.println("Votre grade est trop bas pour pouvoir accéder à toutes les options de cette endroit.");
            return grade;
        }
        return value;
    }
    
    public String text_artisans(){
        String text = "";
        if(this.forge){
            text += "\n\taller à la (fo)rge";
        }
        if(this.archerie){
            text += "\n\taller voir le (ma)rchant d'arc";
        }
        if(this.armurerie){
            text += "\n\taller à l'(ar)murerie";
        }
        if(this.marchand_bouclier){
            text += "\n\taller voir le marchand de (bo)uclier";
        }
        if(this.tanneur){
            text += "\n\taller voir le (ta)nneur";
        }
        if(this.bijouterie){
            text += "\n\taller à la (bi)jouterie";
        }
        if(this.ecurie){
            text += "\n\taller à l'(éc)urie";
        }
        return text;
    }
    
    /**
     * Demande aux joueur le niveau de son équipement actuel à améliorer
     * @param niveau le niveau maximum d'amélioration possible
     * @param niveau_artisan le niveau de l'artisan
     * @return -1 si le joueur revient sur sa décision, le niveau de son équipement sinon
     */
    private int niveau_equipement(int niveau, int niveau_artisan){
        int niveau_equipement;
        do {
            for (int i = 0; i < niveau; i++) {
                System.out.printf("\t%d : +%d \n", i, i);
            }
            System.out.printf("\t%d : retour\n", niveau);
            niveau_equipement = Input.readInt();
            if(niveau_equipement > niveau){
                if(niveau_equipement > niveau_artisan){
                    System.out.println("L'artisan n'a pas le niveau requis pour améliorer cet équipement.");
                } else {
                    System.out.println("Votre grade est trop bas pour demander quelque chose d'aussi puissant.");
                }
                niveau_equipement = -1;
            }
        } while(niveau_equipement < 0);
        
        //quitter
        if(niveau_equipement == niveau){
            return -1;
        }
        return niveau_equipement;
    }
    
    /**
     * Demande aux joueur le niveau des matériaux à utiliser
     * @param niveau_min le niveau minimum requis des matériaux
     * @param niveau_max le niveau maximum supportable des matériaux
     * @param niveau_artisan le niveau de l'artisan
     * @return -1 si le joueur revient sur sa décision, le niveau des matériaux sinon
     */
    private int niveau_materiau(int niveau_max, int niveau_min, int niveau_artisan){
        int niveau_materiau;
        do{
            for(int i = niveau_min; i <= niveau_max; i++) {
                System.out.printf("\t%d : +%d \n", i, i);
            }
            System.out.printf("\t%d : retour\n", niveau_max + 1);
            niveau_materiau = Input.readInt();
            if(niveau_materiau == niveau_max + 1){
                return -1;
            }
            if(niveau_materiau > niveau_max){
                if(niveau_materiau > niveau_artisan){
                    System.out.println("L'artisan n'a pas le niveau requis pour utiliser de tels matériaux.");
                } else {
                    System.out.println("Votre grade est trop bas pour demander quelque chose d'aussi puissant.");
                }
                niveau_materiau = -1;
            }
            if(niveau_materiau < niveau_min){
                System.out.println("Ce matériaux est de trop mauvaise qualité pour améliorer cet équipement.");
                niveau_materiau = -1;
            }
        }while(niveau_materiau < 0);
        
        return niveau_materiau;
    }
    
    /**
     * Renvoie une liste de coefficient d'amélioration apporté par les matériaux
     * @return une liste sous la forme {attaque, résistance, armure}
     */
    private int[] analyse(){
        int atk = 0;
        int res = 0;
        int def = 0;
        System.out.println("Entrez la particularité de vos matériaux : ");
        switch (Input.readInt()){
            case 0 -> {}
            case 1 -> atk = 1;
            case 2 -> atk = 2;
            case 3 -> res = 1;
            case 4 -> res = 2;
            case 5 -> def = 1;
            case 6 -> {
                atk = 1;
                res = 1;
            }
            case 7 -> {
                atk = 2;
                res = -2;
            }
            case 8 -> {
                res = 1;
                def = 1;
            }
            case 9 -> {
                atk = 2;
                def = -1;
            }
            case 10 -> {
                def = 1;
                res = -1;
            }
            default -> {
                System.out.println("Unknown input");
                return analyse();
            }
        }
        return new int[] {atk, res, def};
    }
    
    
    private void presenterModif(int atk, int res, int def) {
        if(atk > 0) {
            System.out.printf("Votre attaque augmente de %d.\n", atk);
        } else if (atk < 0) {
            System.out.printf("Votre attaque diminue de %d.\n", -atk);
        }
        if(res > 0){
            System.out.printf("Votre résistance augmente de %d.\n", res);
        } else if (res < 0){
            System.out.printf("Votre résistance diminue de %d.\n", -res);
        }
        if(def > 0){
            System.out.printf("Votre armure de %d.\n", def);
        } else if (def < 0){
            System.out.printf("Votre armure diminue de %d.\n", -def);
        }
    }
    
    
    private void frapper_pantin() throws IOException {
        Monstre pantin = Lieu.get_dummy();
        System.out.println("Cela vous coutera 2PO par participants.");
        System.out.println("Vous vous trouvez dans une simulation. La mort n'est pas définitive, et vous" + " poss" + "édez des quantités illimités de mana, d'aura et d'ingrédients. Vous pouvez utiliser sans limites" + " tout objet que vous avez sur vous ou que vous fabriquerez sur place, mais rien d'autre.");
        Combat.affrontement(this.pos, -1, pantin);
    }
}

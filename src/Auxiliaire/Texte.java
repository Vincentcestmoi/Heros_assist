package Auxiliaire;

import Exterieur.Output;
import Monstre.Monstre;
import Metiers.Joueur;

public class Texte {
    
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
    
    public static void victoire(String nom){
        System.out.println("***********************************");
        System.out.printf("%s a remporté la partie !!!\n", nom);
        System.out.println("***********************************");
        try {
            Thread.sleep(2000); // pause de 2 seconde
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // bonne pratique : réinterrompre le thread
            System.err.println("Interruption pendant la pause : " + e.getMessage());
        }
        for(int i = 0; i < 100; i++){
            Output.JouerSonLvlUp();
        }
    }
    
    public static void lame_vent(){
        System.out.println("De tranchantes rafales sont projetées par votre épée.");
    }
    
    public static void reaction_equipement(){
        System.out.println("Vos équipements réagissent.");
    }
    
    public static void bonus_lieu(){
        System.out.println("Quelques choses en vous ou sur vous réagit à l'environnement.");
    }
    
    public static void sort_dodo() {
        System.out.println("Vous paupières sont lourdes, vous sombrez dans un profond sommeil.");
    }
    
    public static void aveugler(Monstre ennemi) {
        System.out.printf("Vous aveuglez %s\n", ennemi.getNom());
    }
    
    public static void parchemin_archimage(){
        System.out.println("Votre fierté vous empêche d'utiliser les sorts contenue dans ce parchemin.");
    }
    
    public static void thaumaturge(int PIT, int Po){
        System.out.printf("Grâce à vos dons, vous pouvez choisir %d pièces d'équipements que vous conservez (vous " +
                "devez entrer à nouveau les effets cachées). De plus, vous pouvez conserver jusqu'à %d PO.", PIT, Po);
    }
    
    public static void bracelet_protect(Joueur j){
        System.out.printf("Le médaillon de %s se brise, mais pas sa vie.\n%s récupère toute sa santé.\n", j.getNom(), j.getNom());
    }
    
    public static void absorber(String nom, int gain){
        System.out.printf("%s absorbe l'énergie de sa victime et récupère %dPP\n", nom, gain);
    }
    
    public static void absorber2(String nom, int vie, int atk, int def, int pp){
        System.out.printf("%s absorbe l'énergie vitale de son adversaire et gagne définitivement %d points de résistance," +
                " %d points d'attaque et %d point d'armure, ainsi que %dPP.\n", nom, vie, atk, def, pp);
    }
    
    public static void pie(int quantite, String nom){
        System.out.printf("Sa pie voleuse apporte %d PO à %s.\n", quantite, nom);
    }
    
    public static void retirer_tout(){
        System.out.println("Tous vos items ont été retirés.");
    }
    
    public static void duplicata_impossible(){
        System.out.println("Application impossible : cet effet n'est pas cumulable.");
    }
    
    public static void mana_sort(int value){
        System.out.printf("Combien de mana mettez vous dans le sort ? (min %d) : ", value);
    }
    
    public static void annihilation(){
        System.out.println("Votre rune d'annihilation consomme une de vos rune (si ce n'est pas le cas, elle s'auto-détruit).");
        System.out.println("Un faisceau iridescent percute l'ennemi de plein fouet.");
    }
    
    public static void warning(){
        System.out.println("Attention : cette action n'est pas censée être réalisable.");
    }
    
    public static void resurection_tatouage(){
        System.out.println("Votre tatouage s'éfface, ainsi que les affres de la mort. Vous vous réveillez en pleine forme.");
    }
    
    public static void jete_grenade(int nb){
        System.out.printf("Vous vous débarrassez de %d grenades\n", nb);
    }
    
    public static void recupere_mana(String nom, int quantite){
        System.out.printf("%s récupère %d points de mana.", nom, quantite);
    }
}

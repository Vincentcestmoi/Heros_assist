import java.util.Random;

public class Lieu {

    static Random random = new Random();

    public static Monstre enfers(){
        if(random.nextInt(100) > 97){
            return true_prairie();
        }
        else{
            return true_enfers();
        }
    }

    public static Monstre prairie(){
        int tirage = random.nextInt(100);
        if(tirage < 3){
            return true_enfers();
        }
        else if (tirage > 97){
            return true_vigne();
        }
        else{
            return true_prairie();
        }
    }

    public static Monstre vigne(){
        int tirage = random.nextInt(100);
        if(tirage < 3){
            return true_prairie();
        }
        else if (tirage > 97){
            return true_temple();
        }
        else{
            return true_vigne();
        }
    }

    public static Monstre temple(){
        int tirage = random.nextInt(100);
        if(tirage < 3){
            return true_vigne();
        }
        else if (tirage > 97){
            return true_mer();
        }
        else{
            return true_temple();
        }
    }

    public static Monstre mer(){
        int tirage = random.nextInt(100);
        if(tirage < 3){
            return true_temple();
        }
        else if (tirage > 97){
            return true_mont();
        }
        else{
            return true_mer();
        }
    }

    public static Monstre mont(){
        if(random.nextInt(100) < 3){
            return true_mer();
        }
        else{
            return true_mont();
        }
    }

    public static Monstre true_enfers() {
        Race race;
        do {
            race = Race.enfers[random.nextInt(Race.enfers.length)];
        }while (race == null);
        return new Monstre(race);
    }

    public static Monstre true_prairie() {
        Race race;
        do {
            race = Race.prairie[random.nextInt(Race.prairie.length)];
        }while (race == null);
        return new Monstre(race);
    }

    public static Monstre true_vigne() {
        Race race;
        do {
            race = Race.vigne[random.nextInt(Race.vigne.length)];
        }while (race == null);
        return new Monstre(race);
    }

    public static Monstre true_temple() {
        Race race;
        do {
            race = Race.temple[random.nextInt(Race.temple.length)];
        }while (race == null);
        return new Monstre(race);
    }

    public static Monstre true_mer() {
        Race race;
        do {
            race = Race.mer[random.nextInt(Race.mer.length)];
        }while (race == null);
        return new Monstre(race);
    }

    public static Monstre true_mont() {
        Race race;
        do {
            race = Race.mont[random.nextInt(Race.mont.length)];
        }while (race == null);
        return new Monstre(race);
    }

    public static Monstre olympe() {
        Race race;
        do {
            race = Race.olympe[random.nextInt(Race.olympe.length)];
        }while (race == null);
        return new Monstre(race);
    }

}

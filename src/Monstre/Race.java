package Monstre;

import Enum.Competence;

import java.util.Objects;
import java.util.Random;

public class Race {
    protected String nom;
    protected int attaque;
    protected int vie;
    protected int armure;
    protected int niveau_drop_min;
    protected int niveau_drop_max;
    protected int drop_quantite;
    protected Competence[] competence_possible;

    Race(String nom, int attaque, int vie, int armure, int niveau_drop_min, int niveau_drop_max, int drop_quantite, Competence[] comp) {
        this.nom = nom;
        this.attaque = attaque;
        this.vie = vie;
        this.armure = armure;
        this.niveau_drop_min = niveau_drop_min;
        this.niveau_drop_max = niveau_drop_max;
        this.drop_quantite = drop_quantite;
        this.competence_possible = comp;
    }

    public String get_nom() {
        return nom;
    }

    public int get_attaque() {
        return attaque;
    }

    public int get_vie() {
        return vie;
    }

    public int get_armure() {
        return armure;
    }

    int get_niveau_drop_min() {
        return niveau_drop_min;
    }

    int get_niveau_drop_max() {
        return niveau_drop_max;
    }

    int get_drop_quantite() {
        return drop_quantite;
    }

    Competence competence() {
        Random rand = new Random();
        return competence_possible[rand.nextInt(competence_possible.length)];
    }

    /**
     * Supprime le monstre donné de sa liste
     * @param monstre le nom du monstre à supprimer
     * @implNote n'enregistre pas la suppression dans les fichiers de sauvegarde
     */
    public static void delete_monstre(String monstre) {
        delete_monstre(monstre, false);
    }

    /**
     * Supprime le monstre donné de sa liste
     * @param monstre le nom du monstre à supprimer
     * @param silence si on doit dire avoir supprimé le monstre
     * @implNote n'enregistre pas la suppression dans les fichiers de sauvegarde
     */
    public static void delete_monstre(String monstre, boolean silence) {
        Race[][] lists = {Race.enfers, Race.prairie, Race.vigne, Race.temple, Race.mer, Race.mont, Race.olympe};
        for (Race[] l : lists) {
            for (int i = 0; i < l.length; i++) {
                if (l[i] != null && Objects.equals(l[i].get_nom(), monstre)) {
                    l[i] = null;
                    if(!silence) {
                        System.out.println(monstre + " supprimé(e) avec succès.");
                    }
                    return;
                }
            }
        }
        System.out.println(monstre + " abscent(e) des bases de données, suppression impossible.");
    }

    // data en dur
    static Competence[] nomme_comp = {Competence.PRUDENT, Competence.MEFIANT, Competence.SUSPICIEUX}; //monstre nommé

    static Competence[] dyn_comp = {Competence.EXPLOSION, Competence.AUCUNE};
    static Race dynam_damn = new Race("dynamite damnée", 1, 3, 0, 0, 0, 1, dyn_comp);
    static Competence[] squel_comp = {Competence.AUCUNE, Competence.FRAGILE};
    static Race squelette = new Race("squelette", 1, 5, 0, 0, 0, 1, squel_comp);
    static Competence[] cvs_comp = {Competence.VOL, Competence.VAMPIRISME, Competence.VOLAGE, Competence.VOL_OFF};
    static Race ch_s_v = new Race("chauve-souris vampire", 2, 4, 0, 0, 0, 2, cvs_comp);
    static Competence[] damn_comp = {Competence.AUCUNE, Competence.DAMNATION_RES, Competence.DAMNATION_ATK};
    static Race damne = new Race("Damné", 1, 4, 0, 0, 0, 1, damn_comp);
    static Competence[] guerske_comp = {Competence.AUCUNE, Competence.FRAGILE, Competence.DAMN_ARES};
    static Race guer_ske = new Race("guerrier squelette", 1, 5, 0, 0, 0, 1, guerske_comp);
    static Competence[] guermau_comp = {Competence.FEAR_POSEIDON, Competence.FEAR_DEMETER, Competence.FEAR_DYONISOS};
    static Race guer_maudit = new Race("Guerrier maudit", 2, 4, 0, 0, 0, 1, guermau_comp);
    static Competence[] fant_comp = {Competence.ESPRIT, Competence.SPELL_IMMUNE};
    static Race fantome = new Race("fantôme", 3, 3, 0, 0, 0, 2, fant_comp);
    static Competence[] zomb_comp = {Competence.AUCUNE, Competence.POURRI, Competence.CIBLE_CASQUE,
            Competence.PARTIELLE_SPELL_IMMUNIE, Competence.ARMURE_NATURELLE};
    static Race zombie = new Race("zombie", 1, 6, 0, 0, 0, 2, zomb_comp);
    static Competence[] lich_comp = {Competence.ARMURE_GLACE, Competence.GEL};
    static Race liche = new Race("liche", 3, 8, 0, 0, 1, 2, lich_comp);
    static Race cerbere = new Race("Cerbère", 4, 11, 1, 5, 5, 1, nomme_comp);

    public static Race[] enfers = {dynam_damn, squelette, ch_s_v, damne, guer_ske, guer_maudit, fantome, zombie, liche, cerbere};

    static Competence[] loup_comp = {Competence.MORSURE_EREINTANTE, Competence.MORSURE_MALADIVE, Competence.MORSURE_SAUVAGE};
    static Race loup = new Race("loup", 4, 9, 0, 0, 1, 1, loup_comp);
    static Competence[] karp_comp = {Competence.PHOTOSYNTHESE, Competence.COLERE, Competence.FEAR_DEMETER, Competence.HATE_DEMETER};
    static Race karpoi = new Race("karpoi", 3, 9, 0, 0, 1, 1, karp_comp);
    static Competence[] basi_comp = {Competence.REGARD_APPEURANT, Competence.REGARD_MORTEL,
            Competence.REGARD_PETRIFIANT, Competence.REGARD_TERRIFIANT};
    static Race basilic = new Race("basilic", 4, 10, 0, 1, 1, 2, basi_comp);
    static Competence[] serp_comp = {Competence.RAPIDE, Competence.AUCUNE, Competence.POISON};
    static Race serpent = new Race("serpent", 2, 6, 0, 0, 1, 1, serp_comp);
    static Competence[] serpg_comp = {Competence.RAPIDE, Competence.POISON, Competence.ARMURE_NATURELLE, Competence.VITALITE_NATURELLE, Competence.GEANT};
    static Race serpent_geant = new Race("serpent géant", 4, 10, 0, 1, 1, 2, serpg_comp);
    static Competence[] sang_comp = {Competence.VITALITE_NATURELLE, Competence.ARMURE_NATURELLE, Competence.AUCUNE, Competence.CHARGE};
    static Race sanglier = new Race("sanglier", 3, 10, 0, 0, 1, 1, sang_comp);
    static Competence[] sangf_comp = {Competence.ARMURE_FEU, Competence.FLAMME_ATTAQUE, Competence.FLAMME_DEFENSE};
    static Race sanglier_feu = new Race("sanglier de feu", 4, 10, 0, 1, 1, 1, sangf_comp);
    static Competence[] corn_comp = {Competence.VOL, Competence.VOL_OFF, Competence.VOLAGE, Competence.RAPIDE};
    static Race corneille = new Race("corneille", 2, 8, 0, 0, 1, 1, corn_comp);
    static Competence[] oursa_comp = {Competence.FRAGILE, Competence.BLESSE, Competence.FAIBLE};
    static Race ours_agonisant = new Race("ours agonisant", 5, 13, 1, 1, 1, 2, oursa_comp);
    static Race lycaon = new Race("Lycaon", 5, 17, 1, 5, 5, 1, nomme_comp);
    static Race mormo = new Race("Mormo", 7, 14, 0, 5, 5, 1, nomme_comp);

    public static Race[] prairie = {loup, karpoi, basilic, serpent, serpent_geant, sanglier, sanglier_feu, corneille, ours_agonisant, lycaon, mormo};

    static Competence[] herf_comp = {Competence.ARNAQUE, Competence.VOLEUR_CASQUE, Competence.BENEDICTION};
    static Race herbe_folle = new Race("herbe folle", 1, 6, 0, 0, 0, 0, herf_comp);
    static Competence[] ours_comp = {Competence.VITALITE_NATURELLE, Competence.ARMURE_NATURELLE, Competence.SAUVAGE, Competence.VIOLENT};
    static Race ours = new Race("ours", 5, 19, 1, 1, 1, 2, ours_comp);
    static Competence[] mant_comp = {Competence.POISON, Competence.CIBLE_CASQUE, Competence.POISON_CECITE, Competence.ARMURE_NATURELLE};
    static Race manticore = new Race("manticore", 6, 15, 1, 1, 1, 1, mant_comp);
    static Competence[] pill_comp = {Competence.ESQUIVE, Competence.EQUIPE, Competence.POISON, Competence.POISON_CECITE, Competence.DUO};
    static Race pillard = new Race("pillard", 5, 15, 1, 1, 1, 1, pill_comp);
    static Competence[] corb_comp = {Competence.VOL, Competence.VOLAGE, Competence.VOL_OFF, Competence.RAPIDE, Competence.POISON};
    static Race corbeau = new Race("corbeau", 3, 10, 0, 1, 1, 2, corb_comp);
    static Competence[] bacc_comp = {Competence.ESQUIVE, Competence.FOLIE_MEURTRIERE,
            Competence.VITALITE_NATURELLE, Competence.HATE_DYONISOS, Competence.FEAR_DYONISOS};
    static Race bacchante = new Race("bacchante", 5, 14, 0, 1, 1, 1, bacc_comp);
    static Competence[] saty_comp = {Competence.RAPIDE, Competence.ARNAQUE, Competence.ASSAUT, Competence.FEAR_DYONISOS, Competence.HATE_DYONISOS};
    static Race satyre = new Race("satyre", 5, 13, 0, 1, 1, 1, saty_comp);
    static Competence[] mena_comp = {Competence.ESQUIVE, Competence.FOLIE_MEURTRIERE,
            Competence.VIOLENT, Competence.POISON, Competence.ARNAQUE, Competence.HATE_DYONISOS, Competence.FEAR_DYONISOS};
    static Race menade = new Race("ménade", 7, 15, 2, 1, 2, 1, mena_comp);
    static Race empousa = new Race("Empousa", 9, 21, 1, 5, 5, 1, nomme_comp);
    static Race laton = new Race("Laton", 10, 23, 1, 5, 5, 1, nomme_comp);

    public static Race[] vigne = {herbe_folle, ours, laton, manticore, pillard, corbeau, bacchante, satyre, menade, empousa};

    static Competence[] lier_comp = {Competence.BENEDICTION, Competence.ARNAQUE, Competence.AUCUNE, Competence.SACRE};
    static Race lierre_sacre = new Race("lierre sacré", 1, 1, 0, 1, 1, 1, lier_comp);
    static Competence[] band_comp = {Competence.DUO, Competence.EQUIPE, Competence.ASSAUT, Competence.ASSASSINAT};
    static Race bandit = new Race("bandit", 5, 27, 1, 1, 2, 2, band_comp);
    static Competence[] harp_comp = {Competence.VOL, Competence.VOL_OFF, Competence.VOLAGE, Competence.RAPIDE};
    static Race harpie = new Race("harpie", 7, 19, 1, 1, 2, 1, harp_comp);
    static Competence[] pegan_comp = {Competence.VOL, Competence.VOL_OFF, Competence.VOLAGE, Competence.RAPIDE,
            Competence.CHARGE, Competence.VIOLENT, Competence.VITALITE_NATURELLE, Competence.COLERE, Competence.FORCE_NATURELLE};
    static Race pegase_noir = new Race("pégase noir", 9, 22, 2, 1, 2, 2, pegan_comp);
    static Competence[] pega_comp = {Competence.VOL, Competence.VOL_OFF, Competence.VOLAGE, Competence.RAPIDE,
            Competence.VITALITE_NATURELLE, Competence.ARMURE_NATURELLE};
    static Race pegase = new Race("pégase", 6, 26, 1, 1, 2, 2, pega_comp);
    static Competence[] gorg_comp = {Competence.POISON2, Competence.REGARD_PETRIFIANT, Competence.REGARD_MORTEL, Competence.POISON_CECITE};
    static Race gorgone = new Race("gorgone", 6, 21, 1, 1, 2, 1, gorg_comp);
    static Competence[] fana_comp = {Competence.FOLIE_MEURTRIERE, Competence.VIOLENT, Competence.EXPLOSION, Competence.KAMICASE};
    static Race fanatique = new Race("fanatique", 9, 15, 1, 1, 1, 2, fana_comp);
    static Competence[] serv_comp = {Competence.ESQUIVE, Competence.FURTIF, Competence.RAPIDE, Competence.ASSAUT};
    static Race servante_dorion = new Race("servante d'Orion", 7, 19, 1, 1, 1, 2, serv_comp);
    static Race python = new Race("Python", 7, 29, 3, 5, 5, 1, nomme_comp);
    static Race echidna = new Race("Echidna", 13, 26, 1, 5, 5, 1, nomme_comp);

    public static Race[] temple = {lierre_sacre, bandit, harpie, pegase_noir, pegase, gorgone, fanatique, servante_dorion, python, echidna};

    static Competence[] krak_comp = {Competence.GEANT, Competence.PEAU_DURE, Competence.PEAU_MAGIQUE,
            Competence.PARTIELLE_SPELL_IMMUNIE, Competence.HATE_POSEIDON, Competence.FEAR_POSEIDON};
    static Race kraken = new Race("kraken", 10, 25, 1, 2, 2, 1, krak_comp);
    static Competence[] levi_comp = {Competence.GEANT, Competence.PEAU_MAGIQUE, Competence.PARTIELLE_SPELL_IMMUNIE,
            Competence.HATE_POSEIDON, Competence.FEAR_POSEIDON, Competence.CUIR_MAGIQUE, Competence.ARMURE_NATURELLE, Competence.VITALITE_NATURELLE};
    static Race leviathan = new Race("leviathan", 5, 43, 4, 2, 3, 2, levi_comp);
    static Competence[] hipp_comp = {Competence.RAPIDE, Competence.FEAR_POSEIDON, Competence.HATE_POSEIDON,
            Competence.VITALITE_NATURELLE, Competence.BRUME};
    static Race hippocampe = new Race("hippocampe", 11, 30, 1, 2, 2, 2, hipp_comp);
    static Competence[] grif_comp = {Competence.VOL, Competence.VOL_OFF, Competence.CUIR_MAGIQUE, Competence.ARMURE_NATURELLE,
            Competence.POISON_CECITE, Competence.RAPIDE, Competence.POISON2, Competence.ESQUIVE, Competence.VIOLENT, Competence.VITALITE_NATURELLE};
    static Race griffon = new Race("griffon", 13, 25, 2, 2, 2, 2, grif_comp);
    static Competence[] sire_comp = {Competence.POISON, Competence.PEAU_MAGIQUE, Competence.FEAR_POSEIDON, Competence.HATE_POSEIDON,
            Competence.POISON_CECITE, Competence.CHANT_SIRENE, Competence.CHANT_SIRENE, Competence.CHANT_SIRENE};
    public static Race sirene = new Race("sirène", 7, 23, 1, 2, 2, 1, sire_comp);
    static Competence[] trit_comp = {Competence.PEAU_DURE, Competence.PEAU_MAGIQUE, Competence.VITALITE_NATURELLE,
            Competence.VIOLENT, Competence.ARMURE_NATURELLE, Competence.FEAR_POSEIDON, Competence.HATE_POSEIDON, Competence.SAUVAGE};
    public static Race triton = new Race("triton", 12, 25, 1, 2, 2, 2, trit_comp);
    static Competence[] coqu_perle = {Competence.ARMURE_NATURELLE4, Competence.ARNAQUE, Competence.PEAU_DACIER, Competence.PERLE, Competence.AQUAJET3,
            Competence.AQUAJET2, Competence.AQUAJET3, Competence.AQUAJET2};
    static Race coquillage = new Race("coquillage", 0, 25, 5, 2, 2, 1, coqu_perle);
    static Competence[] symb_comp = {Competence.VOL, Competence.VOL_OFF, Competence.VOLAGE, Competence.VIOLENT, Competence.RAPIDE};
    static Race symbalien = new Race("oiseau de symbale", 8, 21, 1, 2, 2, 1, symb_comp);
    static Race charibe = new Race("Charibe", 13, 44, 3, 5, 5, 1, nomme_comp);
    static Race scylla = new Race("Scylla", 15, 39, 4, 5, 5, 1, nomme_comp);

    public static Race[] mer = {kraken, leviathan, hippocampe, griffon, sirene, triton, coquillage, symbalien, charibe, scylla};

    static Competence[] vent_comp = {Competence.ESPRIT, Competence.VOL, Competence.VOL_OFF, Competence.HATE_ZEUS,
            Competence.FEAR_ZEUS, Competence.INTANGIBLE, Competence.ARMURE_FOUDRE};
    public static Race venti = new Race("venti", 13, 33, 1, 3, 3, 1, vent_comp);
    static Competence[] cycl_comp = {Competence.GEANT, Competence.ARMURE_NATURELLE2, Competence.VITALITE_NATURELLE2,
            Competence.FORCE_NATURELLE2, Competence.VIOLENT, Competence.AUCUNE};
    public static Race cyclope = new Race("cyclope", 15, 36, 3, 3, 3, 2, cycl_comp);
    static Competence[] aura_comp = {Competence.ESPRIT, Competence.VOL, Competence.VOL_OFF,
            Competence.VOLAGE, Competence.INTANGIBLE, Competence.SPELL_IMMUNE};
    public static Race aurai_malefique = new Race("aurai maléfique", 10, 29, 1, 2, 3, 1, aura_comp);
    static Competence[] roch_comp = {Competence.VOLEUR_CASQUE, Competence.ARMURE_NATURELLE4,
            Competence.PEAU_DACIER, Competence.PEAU_MAGIQUE, Competence.ARNAQUE, Competence.DETESTE, Competence.KAMICASE};
    public static Race roche_maudite = new Race("roche maudite", 0, 38, 8, 2, 3, 1, roch_comp);
    static Competence[] dulh_comp = {Competence.PEAU_DURE, Competence.PEAU_MAGIQUE, Competence.CUIR_MAGIQUE, Competence.VAMPIRISME4,
            Competence.REVENANT, Competence.FRAPPE_SPECTRALE};
    public static Race dullahan  = new Race("dullahan", 14, 37, 3, 3, 3, 2, dulh_comp);
    static Competence[] gole_comp = {Competence.GOLEM_PIERRE, Competence.GOLEM_FER, Competence.GOLEM_ACIER, Competence.GOLEM_MITHRIL};
    public static Race golem = new Race("golem", 9, 25, 2, 2, 2, 2, gole_comp);
    static Competence[] arch_comp = {Competence.GEL, Competence.ARMURE_GLACE2, Competence.SPELL_IMMUNE};
    static Race archliche = new Race("archliche", 12, 35, 2, 3, 3, 2, arch_comp);
    static Competence[] illu_comp = {Competence.ILLU_VENTI, Competence.ILLU_SIRENE, Competence.ILLU_CYCLOPE, Competence.ILLU_AURAI,
            Competence.ILLU_ROCHE, Competence.ILLU_DULLA, Competence.ILLU_TRITON, Competence.ILLU_GOLEM};
    static Race illusioniste = new Race("illusioniste", 8, 30, 2, 2, 3, 2, illu_comp);
    static Race typhon = new Race("Typhon", 19, 56, 2, 5, 5, 1, nomme_comp);
    static Race caucase = new Race("l'Aigle du Caucase", 21, 45, 3, 5, 5, 1, nomme_comp);

    public static Race[] mont = {venti, cyclope, aurai_malefique, roche_maudite, dullahan, golem, archliche, illusioniste, typhon, caucase};

    static Competence[] tita_comp = {Competence.CUIR_MAGIQUE, Competence.SPELL_IMMUNE, Competence.GEANT, Competence.FORCE_NATURELLE3,
            Competence.VITALITE_NATURELLE3, Competence.ARMURE_NATURELLE3};
    static Race titan = new Race("titan", 25, 58, 4, 3, 4, 2, tita_comp);
    static Competence[] chro_comp = {Competence.CHRONOS};
    static Race chronos = new Race("Chronos", 35, 83, 7, 4, 4, 6, chro_comp);

    public static Race[] olympe = {titan, titan, titan, chronos};

    static Competence[] pant_comp = {Competence.DUMMY};
    static Race pantin = new Race("Mannequin d'entrainement", 0, 1, 0, 0, 0, 0, pant_comp);
}
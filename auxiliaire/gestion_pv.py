#!/usr/bin/env python3
from random import shuffle

text_min = ["Pathétique...", "Vous n'avez rien senti.", "Un bébé vous aurez frappé plus fort que ça.",
 "Vous avez presque pitié des efforts futiles de votre adversaire.", "Vous pourriez encaisser 10 fois plus à une main",
 "Vous avez plus souffert en éternuant qu'en subissant cette 'attaque'.", "Vous hésitez à vous battre nu(e) pour équilibrer les chances."]

text_mort = ["Vous êtes mort.", "This person is décédée.", "Vous âme se fais la malle ! (t'es dead)", "Quik, t'es mort."]

text_presque_mort = ["Votre vision se trouble, votre souffle s'épuise, vos sens s'amenuisent. La mort vous sourit.",
 "Vous sentez votre sang se refroidir et votre souffle s'épuiser. Vous avez atteint vos limites...", "Vous ne sentez même plus"
 " la douleur, vous n'en avez plus pour longtemp."]

text_proche_mort = ["La douleur vous vrille le crâne, vous ne survivrez sans doute pas au prochain coup.", "La douleur est"
 " sourde, profonde, vous ne résisterez pas à une autre attaque.", "Vous serez les dents, le prochaine assaut sera sans doute le dernier"
 " que vous encaisserez jamais..."]

text_douleur_intense = ["Même vos os semblent pulser de douleur, vous luttez pour rester conscient.", "Une souffrance intense"
 "traverse tout votre corps.", "Tous vos muscles se crispent, votre cerveau est surchargé de message de douleur.", "La blessure"
 " n'est pas fatale, mais pourrait rapidement le devenir..."]

text_douleur_forte = ["Votre corps tout entier vous fait souffir.", "Vous laissez échapper un cri de douleur.",
 "Ce n'est rien d'insurmontable, mais ça pique pas mal quand même.", "Votre visage se tords de douleur, mais vous restez"
 " focus."]

text_douleur_moyenne = ["Vous avez le souffle court et les muscles douloureux.", "Vous ressentez désagréablement l'impact.",
 "La blessure n'est pas profonde, mais douloureuse.", "Vous aimeriez ne pas subir de tels assauts trop souvent."]

text_douleur_faible = ["Vous supportez encore plutôt bien la douleur.", "L'impact n'a rien d'inquiétant, mais n'est pas"
 "non plus négligeable.", "La blessure est superficielle, mais vous saignez.", "Rien d'inquiétant, mais vous prenez "
 "la menace au sérieux."]

text_standard = ["Ce n'est rien d'insurmontable.", "Vous pourriez encaisser bien plus que ça.", "Tranquille !", "Vous n'en"
 " garderez même pas une cicatrice.", "Rien de spécial.", "Une attaque sans prétention, ni danger."]

while True:
    pv_m = int(input("Nombre de PV : "))
    armure = int(input("Nombre d'Armure : "))
    armure_base = armure
    pv_base = pv_m
    pv = pv_m
    run = True
    print("Lettres clé : (D)ommage | (P)oison | (S)oins | (B)oost | Boost (A)rmure | (Q)uit (=redémarre) | (R)eset")
    while run:
        temp = input("d, p, s, b, a, r, q ? ")

        if temp in ["S", "s"]:
            soin = int(input("Soins : "))
            pv = min(pv + soin, pv_m)
            if pv == pv_m:
                print("Vous êtes en pleine forme.")
            elif pv >= pv_m * 3/4:
                print("Vous vous sentez plutôt en forme.")
            elif pv >= pv_m / 2:
                print("Vous vous sentez toujours un peu mal.")
            elif pv >= pv_m / 3:
                print("Vous ne vous sentez pas très bien.")
            elif pv >= pv_m / 4:
                print("Vous vous sentez mal.")
            else:
                print("Vous vous trouvez toujours aux portes de la mort.")

        elif temp in ["B", "b"]:
            temp2 = int(input("Boost de résistance de combien ? : "))
            pv_m += temp2
            pv += temp2

        elif temp in ["A", "a"]:
            temp2 = int(input("Boost d'armure de combien ? : "))
            armure += temp2

        elif temp in ["Q", "q"]:
            run = False

        elif temp in ["R", "r"]:
            pv_m = pv_base
            pv = pv_base
            armure = armure_base

        elif temp in ["D", "d", "", " ", "\n"]:
            attaque = int(input("Dégâts : "))
            degat = max(attaque - armure, 1)
        elif temp in ["P", "p"]:
            degat = int(input("Dégâts direct : "))

        if temp in ["D", "d", "P", "p"]:
            pv -= degat
            if pv <= 0:
                shuffle(text_mort)
                print(text_mort[0])
                pv = 0
            elif pv <= (pv_m * 0.08 ):
                shuffle(text_presque_mort)
                print(text_presque_mort[0])
            elif pv <= (pv_m * 0.16 ):
                shuffle(text_proche_mort)
                print(text_presque_mort[0])
            elif degat > pv_m * 0.75:
                shuffle(text_douleur_intense)
                print(text_douleur_intense[0])
            elif degat > pv_m * 0.6:
                shuffle(text_douleur_forte)
                print(text_douleur_forte[0])
            elif degat > pv_m * 0.45:
                shuffle(text_douleur_moyenne)
                print(text_douleur_moyenne[0])
            elif degat > pv_m * 0.25:
                shuffle(text_douleur_faible)
                print(text_douleur_faible[0])
            elif degat <= pv_m * 0.1:
                shuffle(text_min)
                print(text_min[0])
            else:
                shuffle(text_standard)
                print(text_standard[0])

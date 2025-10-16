#!/usr/bin/env python3
from random import shuffle

text_min = ["Pathétique", "Vous n'avez rien senti.", "Un bébé vous aurez frappé plus fort que ça.",
"Vous avez presque pitié des efforts futiles de votre adversaire."]

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

        elif temp in ["D", "d"]:
            attaque = int(input("Dégâts : "))
            degat = max(attaque - armure, 1)
        elif temp in ["P", "p"]:
            degat = int(input("Dégâts de poison : "))

        if temp in ["D", "d", "P", "p"]:
            pv -= degat
            if pv <= 0:
                print("Vous êtes mort.")
                pv = 0
            elif pv <= (pv_m * 0.08 ):
                print("Votre vision se trouble, votre souffle s'épuise, vos sens s'amenuisent. La mort vous sourit.")
            elif pv <= (pv_m * 0.16 ):
                print("La douleur vous vrille le crâne, vous ne survivrez sans doute pas au prochain coup.")
            elif degat > pv_m * 0.75:
                print("Même vos os semblent pulser de douleur, vous luttez pour rester conscient.")
            elif degat > pv_m * 0.6:
                print("Votre corps tout entier vous fait souffir.")
            elif degat > pv_m * 0.45:
                print("Vous avez le souffle court et les muscles douloureux.")
            elif degat > pv_m * 0.3:
                print("Vous supportez encore plutôt bien la douleur.")
            elif degat <= pv_m * 0.1:
                shuffle(text_min)
                print(text_min[0])

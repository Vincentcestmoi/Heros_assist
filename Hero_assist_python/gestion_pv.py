#!/usr/bin/env python3

while True:
    pv_m = int(input("Nombre de PV : "))
    armure = int(input("Nombre d'Armure : "))
    armure_base = armure
    pv_base = pv_m
    pv = pv_m
    run = True
    while run:
        temp = input("(D)ommage | (S)oins | (B)oost | Boost (A)rmure | (Q)uit (=redémarre) | (R)eset ?")

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
            temp2 = int(input("Boost de résistance de combien? : "))
            pv_m += temp2
            pv += temp2

        elif temp in ["A", "a"]:
            temp2 = int(input("Boost d'armure de combien? : "))
            armure += temp2

        elif temp in ["Q", "q"]:
            run = False

        elif temp in ["R", "r"]:
            pv_m = pv_base
            pv = pv_base
            armure = armure_base

        elif temp in ["D", "d"] :
            attaque = int(input("Dégâts : "))
            degat = max(attaque - armure, 1)
            pv -= degat
            if pv <= 0:
                print("Vous êtes mort.")
                pv = 0
            elif pv <= (pv_m / 10):
                print("Votre vision se trouble, vos sens s'amenuisent, la mort vous sourit.")
            elif degat > pv_m / 2:
                print("Vous ressentez le choc jusque dans vos os.")
            elif degat > pv_m / 3:
                print("Vous sentez votre corps souffrir de l'impact.")
            elif degat > pv_m / 4:
                print("Vous ressentez l'attaque.")
            else:
                print("Vous n'avez presque rien senti.")

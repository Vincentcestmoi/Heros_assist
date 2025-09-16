#!/usr/bin/env python3

from random import shuffle

popo = {"potion douteuse (EXC1D)" : 1, "potion insipide (EX1PV)" : 1, "potion toxique (EXC2D)" : 5,
        "potion de poison (EXC3D)" : 8, "potion instable (EXD)" : 4, "potion de feu (EXD)" : 9, "de force (EX2ATK)" : 9,
        "potion de vie (EX4PV)" : 6, "potion énergétique (M2PP)" : 10}

popo2 = ["potion douteuse (EXC1D)", "potion insipide (EX1PV)", "potion toxique (EXC2D)", "potion de poison (EXC3D)",
         "potion instable (EXD)", "potion de feu (EXD)", "de force (EX2ATK)", "potion de vie (EX4PV)",
         "potion énergétique (M2PP)"]
ingre = int(input("Combien d'ingrédient allez-vous utiliser ? (max 4): "))
temp = int(input("D4 : ")) + ingre + 2
shuffle(popo2)
while popo[popo2[0]] > temp:
    shuffle(popo2)
print("Vous avez concocté une", popo2[0])
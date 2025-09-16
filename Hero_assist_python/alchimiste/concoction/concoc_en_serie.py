#!/usr/bin/env python3

from random import shuffle

popo = {"potion douteuse (EXC1D)" : 1, "potion insipide (EX1PV)" : 1, "potion toxique (EXC2D)" : 5,
        "potion de poison (EXC3D)" : 8, "potion instable (EXD)" : 4, "potion de feu (EXD)" : 9, "potion de force (EX2ATK)" : 9,
        "potion de vie (EX4PV)" : 6, "potion énergétique (M2PP)" : 10, "potion de santé (EX6PV)" : 11,
        "potion d'énergie (M4PP)" : 13, "potion de vigeur (EX3RES)" : 11, "potion de résistance (EX4RES)" : 14,
        "potion de puissance (EX3ATK)" : 14, "flasque nécrosé (EXC4D)" : 11, "potion nécrotyque (EXC5D)" : 14,
        "potion explosive (EXD)" : 15, "potion divine (ALC5PV7RES3ATK)" : 15}

popo2 = ["potion douteuse (EXC1D)", "potion insipide (EX1PV)", "potion toxique (EXC2D)", "potion de poison (EXC3D)",
         "potion instable (EXD)", "potion de feu (EXD)", "potion de force (EX2ATK)", "potion de vie (EX4PV)",
         "potion énergétique (M2PP)", "potion de santé (EX6PV)", "potion d'énergie (M4PP)", "potion de vigeur (EX3RES)",
         "potion de résistance (EX4RES)", "potion de puissance (EX3ATK)", "flasque nécrosé (EXC4D)", "potion nécrotyque (EXC5D)",
         "potion explosive (EXD)", "potion divine (ALC5PV7RES3ATK)"]

ingre = int(input("Combien d'ingrédient allez-vous utiliser ? : "))
temp = int(input("D6 : ")) + ingre
shuffle(popo2)
while temp > 0:
    if popo[popo2[0]] <= temp:
        print("Vous avez concocté une", popo2[0])
        temp -= popo[popo2[0]]
    shuffle(popo2)
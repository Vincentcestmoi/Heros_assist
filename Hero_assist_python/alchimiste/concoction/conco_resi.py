#!/usr/bin/env python3

ingre = int(input("Combien d'ingrédient allez-vous utiliser ? (min 5): "))
jet = int(input("D10 : ")) + ingre
if jet < 11 or ingre < 5:
    print("Vous avez produit une potion insipide (EX1PV).")
elif jet < 14:
    print("Vous avez produit une potion de vigeur (EX3RES).")
elif jet < 19:
    print("Vous avez produit une potion de résistance (EX4RES).")
else:
    print("Vous avez produit une potion de solidification (M4RES1DEF).")
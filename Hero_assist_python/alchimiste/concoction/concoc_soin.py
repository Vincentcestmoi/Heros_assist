#!/usr/bin/env python3

ingre = int(input("Combien d'ingrédient allez-vous utiliser ? (min 3): "))
jet = int(input("D10 : ")) + ingre
if jet < 6 or ingre < 3:
    print("Vous avez produit une potion insipide (EX1PV).")
elif jet < 11:
    print("Vous avez produit une potion de vie (EX4PV).")
elif jet < 16:
    print("Vous avez produit une potion de santé (EX6PV).")
elif jet < 20:
    print("Vous avez produit un fortifiant (EX8PV).")
else:
    print("Vous avez produit une potion de regénération (M10PV).")
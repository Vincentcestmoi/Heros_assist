#!/usr/bin/env python3

ingre = int(input("Combien d'ingrédient allez-vous utiliser ? (min 7): "))
jet = int(input("D10 : ")) + ingre
if jet < 10 or ingre < 7:
    print("Vous avez produit une potion insipide (EX1PV).")
elif jet < 15:
    print("Vous avez produit une potion de santé (EX6PV).")
else:
    print("Vous avez produit une potion divine (ALC5PV7RES3ATK).")
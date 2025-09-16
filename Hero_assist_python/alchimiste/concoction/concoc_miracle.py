#!/usr/bin/env python3

ingre = int(input("Combien d'ingrédient allez-vous utiliser ? (min 10): "))
jet = int(input("D10 : ")) + ingre
if jet < 12 or ingre < 9:
    print("Vous avez produit une potion insipide (EX1PV).")
elif jet < 21:
    print("Vous avez produit une potion de santé (EX6PV).")
else:
    print("Vous avez produit un élixir (ALCRESALTPVRES).")
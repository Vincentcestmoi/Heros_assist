#!/usr/bin/env python3

ingre = int(input("Combien d'ingrédient allez-vous utiliser ? (min 4): "))
jet = int(input("D10 : ")) + ingre
if jet < 9 or ingre < 4:
    print("Vous avez produit une potion insipide (EX1PV).")
elif jet < 14:
    print("Vous avez produit une potion de force (EX2ATK).")
elif jet < 16:
    print("Vous avez produit une potion de puissance (EX3ATK).")
else:
    print("Vous avez produit une potion du colosse (EX4ATK).")
#!/usr/bin/env python3

ingre = int(input("Combien d'ingrédient allez-vous utiliser ? (min 5): "))
jet = int(input("D10 : ")) + ingre
if jet < 10 or ingre < 5:
    print("Vous avez produit une potion insipide (EX1PV).")
elif jet < 13:
    print("Vous avez produit une potion énergétique (M2PP).")
elif jet < 18:
    print("Vous avez produit une potion d'énergie (M4PP).")
elif jet < 20:
    print("Vous avez produit une potion de mana (M6PP).")
else:
    print("Vous avez produit une potion ancestrale (MM+PP).")
#!/usr/bin/env python3

ingre = int(input("Combien d'ingrédient allez-vous utiliser ? (min 2): "))
jet = int(input("D10 : ")) + ingre
if jet < 5 or ingre < 2:
    print("Vous avez produit une potion douteuse (EXC1D).")
elif jet < 8:
    print("Vous avez produit une potion toxique (EXC2D).")
elif jet < 11:
    print("Vous avez produit une potion de poison (EXC3D).")
elif jet < 14:
    print("Vous avez produit une flasque nécrosé (EXC4D).")
else:
    print("Vous avez produit une potion nécrotyque (EXC5D).")
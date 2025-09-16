#!/usr/bin/env python3

ingre = int(input("Combien d'ingrédient allez-vous utiliser ? (min 2): "))
jet = int(input("D10 : ")) + ingre
if jet < 4 or ingre < 2:
    print("Vous avez produit une potion douteuse (EXC1D).")
elif jet < 9:
    print("Vous avez produit une potion instable (EXD).")
elif jet < 15:
    print("Vous avez produit une potion de feu (EXD).")
elif jet < 18:
    print("Vous avez produit une potion explosive (EXD).")
else:
    print("Vous avez produit une bombe (EXD).")
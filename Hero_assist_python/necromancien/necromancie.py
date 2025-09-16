#!/usr/bin/env python3

temp = int(input("D8 :"))
if temp  >= 6:
    print("Réussite de la résurection.")
    if temp == 6:
        print("Résurection avec 4 points de vie.")
    elif temp == 7:
        print("Résurection avec 8 (max) points de vie.")
    elif temp == 8:
        print("Résurection avec 12 (max) points de vie.")
else:
    print("Echec de la résurection.")

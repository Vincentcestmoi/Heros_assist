#!/usr/bin/env python3

from random import randint
temp = int(input("D20 : "))
if temp <= 15 + randint(-5, 4):
    print("Vous ne trouvez rien.")
elif temp <= 20 + randint(-2, 2):
    print("Vous trouvez 1 ingrédient.")
else:
    print("Vous récoltez 2 ingrédients.")
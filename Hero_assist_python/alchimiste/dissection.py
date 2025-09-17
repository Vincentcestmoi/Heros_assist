#!/usr/bin/env python3

from random import randint
temp = int(input("D6 : "))
if temp <= 1 + randint(-1, 1):
    print("Vous n'extrayez rien d'utile.")
elif temp <= 5 + randint(-1, 1):
    print("Vous trouvez 1 ingrédient.")
else:
    print("Vous récoltez 2 ingrédients.")
#!/usr/bin/env python3

temp = int(input("D6 : "))
if temp <= 1:
    print("Vous n'extrayez rien d'utile.")
elif temp < 6:
    print("Vous trouvez 1 ingrédient.")
else:
    print("Vous récoltez 2 ingrédients.")
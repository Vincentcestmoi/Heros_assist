#!/usr/bin/env python3

temp = int(input("D20 : "))
if temp <= 16:
    print("Vous ne trouvez rien.")
elif temp <= 19:
    print("Vous trouvez 1 ingrédient.")
else:
    print("Vous récoltez 2 ingrédients.")
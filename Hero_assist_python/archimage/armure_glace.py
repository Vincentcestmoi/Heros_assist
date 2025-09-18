#!/usr/bin/env python3

from random import randint
mana = int(input("Combien de PP mettez vous dans le sort ? (min 3): "))
jet = int(input("D8 : ")) + mana + randint(-1, 1)
if jet <= 3 or mana < 3:
    print("Le sort ne fonctionne pas.")
elif jet <= 9:
    print("La cible gagne 3 points de résistance.")
elif jet <= 12:
    print("La cible gagne 5 points de résistance.")
elif jet <= 14:
    print("La cible gagne 7 points de résistance et 1 point d'armure.")
elif jet == 15:
    print("La cible gagne 8 points de résistance et 1 point d'armure.")
elif jet == 16:
    print("La cible gagne 9 points de résistance et 1 point d'armure.")
elif jet == 17:
    print("La cible gagne 10 points de résistance et 1 point d'armure.")
else:
    print("La cible gagne 10 points de résistance et 2 point d'armure.")
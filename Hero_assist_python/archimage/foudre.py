#!/usr/bin/env python3

from random import randint
mana = int(input("Combien de PP mettez vous dans le sort ? (min 7) : "))
jet = int(input("D8 : ")) + mana + randint(-1, 1)
if jet <= 7 or mana < 7:
    print("Le sort ne fonctionne pas")
elif jet <= 10:
    print("Le sort inflige 13 dommages magiques.")
elif jet <= 12:
    print("Le sort inflige 15 dommages magiques.")
elif jet <= 14:
    print("Le sort inflige 17 dommages magiques.")
elif jet <= 16:
    print("Le sort inflige 19 dommages magiques.")
elif jet == 17:
    print("Le sort inflige 20 dommages magiques.")
else:
    print("Le sort inflige 23 dommages magiques.")
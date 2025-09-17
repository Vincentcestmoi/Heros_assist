#!/usr/bin/env python3

mana = int(input("Combien de PP mettez vous dans le sort ? (min 7) : "))
jet = int(input("D8 : ")) + mana
if jet < 8 or mana < 7:
    print("Le sort ne fonctionne pas")
elif jet <= 10:
    print("Le sort inflige 10 dommages magiques.")
elif jet <= 12:
    print("Le sort inflige 12 dommages magiques.")
elif jet <= 14:
    print("Le sort inflige 14 dommages magiques.")
elif jet <= 16:
    print("Le sort inflige 16 dommages magiques.")
elif jet == 17:
    print("Le sort inflige 18 dommages magiques.")
else:
    print("Le sort inflige 20 dommages magiques.")
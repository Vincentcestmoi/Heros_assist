#!/usr/bin/env python3

mana = int(input("Combien de PP mettez vous dans le sort ? (min 2)"))
jet = int(input("D4 : ")) + mana
if jet <= 3 or mana < 2:
    print("Le sort ne fonctionne pas")
elif jet <= 6:
    print("Le sort inflige 4 dommages magiques.")
elif jet <= 8:
    print("Le sort inflige 6 dommages magiques.")
elif jet <= 10:
    print("Le sort inflige 8 dommages magiques.")
elif jet == 11:
    print("Le sort inflige 11 dommages magiques.")
elif jet == 12:
    print("Le sort inflige 13 dommages magiques.")
elif jet == 13:
    print("Le sort inflige 15 dommages magiques.")
else:
    print("Le sort inflige 17 dommages magiques.")
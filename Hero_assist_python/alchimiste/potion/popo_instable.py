#!/usr/bin/env python3

temp = int(input("D4 : "))
if temp > 2:
    print("La potion explose et inflige", temp - 2, "dommage(s) supplémentaire à l'ennemi.")
else:
    print("La potion se brise par terre sans rien déclencher.")
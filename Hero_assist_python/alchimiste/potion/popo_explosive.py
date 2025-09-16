#!/usr/bin/env python3

temp = int(input("D6 : "))
if temp <= 1:
    print("La bombe explose en l'air et inflige 4 dommages supplémentaires à l'ennemi.")
elif temp != 6:
    print("La bombe détone au contacte de l'ennemi, lui infligeant ", temp + 4, "dommages supplémentaires.")
else:
    print("La potion herte violemment l'ennemi avant de lui exploser au visage, lui infligeant 12 dommages supplémentaires.")
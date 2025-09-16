#!/usr/bin/env python3

temp = int(input("D4 : "))
if temp <= 1:
    print("La potion prends feu en touchant l'ennemi et lui inflige 2 dommages supplémentaires.")
elif temp != 4:
    print("La potion éclate au contacte de l'ennemi, le brûle et lui inflige ", temp + 2, "dommages supplémentaires.")
else:
    print("La potion explose en une gerbe de flamme qui infligent 7 dommages supplémentaires à l'ennemi.")
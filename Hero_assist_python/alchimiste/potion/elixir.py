#!/usr/bin/env python3

temp = int(input("D8 : "))
if temp <= 1:
    print("La cible avale à moitié la potion, guérit de toute altération d'état, se soigne de 2 et gagne"
          "15 points de résistances temporaires.")
elif temp != 8:
    print("La cible guérit de toute altération d'état, se soigne de", temp ,"et gagne", int(temp/2) + 15,
          "points de résistances temporaires.")
else:
    print("La cible brille d'un halo de lumière, elle est guérit de toute altération d'état, se soigne de 10 et"
        " gagne 20 points de résistances temporaires.")
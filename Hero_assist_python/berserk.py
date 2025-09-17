#!/usr/bin/env python3

if input("Activez vous une rune de haine ?(o/N)") in {"o", "O", "Oui", "oui", "y", "Y"}:
    mana = int(input("Combien de PP mettez vous dans le sort ? (min 1): "))
    jet = int(input("D8 : ")) + mana
    if mana < 1:
        print("le sort ne fonctionne pas.")
    elif jet <= 3:
        print("Vous gagnez temporairement 2 points d'attaque et perdez temporairement 3 points de résistance.")
    elif jet <= 5:
        print("Vous gagnez temporairement 3 points d'attaque et perdez temporairement 4 points de résistance.")
    elif jet <= 8:
        print("Vous gagnez temporairement 4 points d'attaque et perdez temporairement 6 points de résistance.")
    elif jet <= 11:
        print("Vous gagnez temporairement 6 points d'attaque et perdez temporairement 9 points de résistance.")
    elif jet <= 15:
        print("Vous gagnez temporairement 8 points d'attaque et perdez temporairement 13 points de résistance.")
    elif jet <= 17:
        print("Vous gagnez temporairement 12 points d'attaque et perdez temporairement 16 points de résistance.")
    else:
        print("Vous gagnez temporairement 15 points d'attaque et perdez temporairement 19 points de résistance.")

else:
    mana = int(input("Combien de PP mettez vous dans le sort ? (min 2): "))
    jet = int(input("D8 : ")) + mana
    if mana < 2:
        print("Le sort ne fonctionne pas.")
    elif jet <= 4:
        print("Vous gagnez temporairement 1 points d'attaque et perdez temporairement 1 points de résistance.")
    elif jet <= 6:
        print("Vous gagnez temporairement 2 points d'attaque et perdez temporairement 2 points de résistance.")
    elif jet <= 9:
        print("Vous gagnez temporairement 3 points d'attaque et perdez temporairement 3 points de résistance.")
    elif jet <= 12:
        print("Vous gagnez temporairement 4 points d'attaque et perdez temporairement 5 points de résistance.")
    elif jet <= 16:
        print("Vous gagnez temporairement 6 points d'attaque et perdez temporairement 8 points de résistance.")
    else:
        print("Vous gagnez temporairement 8 points d'attaque et perdez temporairement 11 points de résistance.")

print("Dans le cas où votre résistance deviendrai négative, remontez la à 1. Le cas échéant, si vous n'êtes pas mort d'ici la fin du combat, mourrez.")
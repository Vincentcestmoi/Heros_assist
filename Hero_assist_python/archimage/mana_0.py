#!/usr/bin/env python3

jet = int(input("D4 : "))
if jet != 4:
    print("Vous perdez connaissance.")
    print("Jusqu'à ce que vous reprenniez connaissance, vous récupererez 1PP à chaque fin de tour.")
else:
    jet = int(input("D4 : "))
    if jet > 1:
        print("Vous récuperez", jet - 1, "PP.")
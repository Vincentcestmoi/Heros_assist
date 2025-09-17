#!/usr/bin/env python3

jet = int(input("D8 :"))
if jet <= 2:
    print("Vous récupérez 2PP.")
elif jet <= 4:
    print("Vous récupérez 3PP.")
elif jet <= 6:
    print("Vous récupérez 4PP.")
else:
    print("Vous récupérez 5PP.")
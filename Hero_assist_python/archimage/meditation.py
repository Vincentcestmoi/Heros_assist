#!/usr/bin/env python3

from random import randint
jet = int(input("D8 :")) + randint(-1, 1)
if jet <= 2:
    print("Vous récupérez 2PP.")
elif jet <= 4:
    print("Vous récupérez 3PP.")
elif jet <= 7:
    print("Vous récupérez 4PP.")
else:
    print("Vous récupérez 5PP.")
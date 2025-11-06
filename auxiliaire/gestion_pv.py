#!/usr/bin/env python3
from random import choice

# Codes couleurs ANSI
RED = "\033[31m"
GREEN = "\033[32m"
YELLOW = "\033[33m"
CYAN = "\033[36m"
RESET = "\033[0m"

# Messages narratifs
text_min = [
    "Pathétique...", "Vous n'avez rien senti.",
    "Un bébé vous aurait frappé plus fort que ça.",
    "Vous avez presque pitié des efforts futiles de votre adversaire.",
    "Vous pourriez encaisser 10 fois plus à une main.",
    "Vous avez plus souffert en éternuant qu'en subissant cette 'attaque'.",
    "Vous hésitez à vous battre nu(e) pour équilibrer les chances."
]

text_mort = [
    "Vous êtes mort.", "This person is décédée.",
    "Votre âme se fait la malle ! (t'es dead)", "Quick, t'es mort."
]

text_presque_mort = [
    "Votre vision se trouble, votre souffle s'épuise, vos sens s'amenuisent. La mort vous sourit.",
    "Vous sentez votre sang se refroidir et votre souffle s'épuiser. Vous avez atteint vos limites...",
    "Vous ne sentez même plus la douleur, vous n'en avez plus pour longtemps.",
    "Vos forces vous abandonnent, chaque seconde est un supplice."
]

text_proche_mort = [
    "La douleur vous vrille le crâne, vous ne survivrez sans doute pas au prochain coup.",
    "La douleur est sourde, profonde, vous ne résisterez pas à une autre attaque.",
    "Vous serrez les dents, le prochain assaut sera sans doute le dernier que vous encaisserez jamais..."
]

text_douleur_intense = [
    "Même vos os semblent pulser de douleur, vous luttez pour rester conscient.",
    "Une souffrance intense traverse tout votre corps.",
    "Tous vos muscles se crispent, votre cerveau est surchargé de messages de douleur.",
    "La blessure n'est pas fatale, mais pourrait rapidement le devenir...",
    "Votre corps entier hurle, vous luttez pour ne pas sombrer."
]

text_douleur_forte = [
    "Votre corps tout entier vous fait souffrir.",
    "Vous laissez échapper un cri de douleur.",
    "Ce n'est rien d'insurmontable, mais ça pique pas mal quand même.",
    "Votre visage se tord de douleur, mais vous restez concentré.",
    "Chaque respiration est une brûlure.",
    "Votre vision se brouille un instant sous l’intensité du choc."
]

text_douleur_moyenne = [
    "Vous avez le souffle court et les muscles douloureux.",
    "Vous ressentez désagréablement l'impact.",
    "La blessure n'est pas profonde, mais douloureuse.",
    "Vous espérez ne pas revivre ça trop souvent.",
    "Vos jambe tremblent sous le choc.",
    "Votre souffle est court, chaque mouvement devient pénible."
]

text_standard = [
    "Vous supportez encore plutôt bien la douleur.",
    "L'impact n'a rien d'inquiétant, mais n'est pas non plus négligeable.",
    "La blessure est superficielle, mais vous saignez.",
    "Rien d'inquiétant, mais vous prenez la menace au sérieux.",
    "Une égratignure comparée à ce qui pourrait arriver, mais vous restez sur vos gardes.",
    "Un bleu au plus, rien qui ne vous arrête."
]

text_douleur_faible = [
    "Ce n'est rien d'insurmontable.",
    "Vous pourriez encaisser bien plus que ça.",
    "Tranquille !",
    "Vous n'en garderez même pas une cicatrice.",
    "Rien de spécial.",
    "Une attaque sans prétention, ni danger."
]

# Boucle principale
while True:
    pv_m = int(input("Nombre de PV : "))
    armure = int(input("Nombre d'Armure : "))
    armure_base = armure
    pv_base = pv_m
    pv = pv_m
    run = True

    print(f"\n{CYAN}Commandes : (D)ommage | (P)oison | (S)oins | (B)oost PV | Boost (A)rmure | (R)eset | (Q)uit{RESET}\n")

    while run:
        temp = input("Action ? ").strip().lower()

        if temp == "s":
            soin = int(input("Soins : "))
            pv = min(pv + soin, pv_m)
            print("\n" + (GREEN + "Vous êtes en pleine forme." if pv == pv_m else
                   GREEN  + "Vous vous sentez plutôt en forme." if pv >= pv_m * 0.80 else
                   GREEN  + "Vos forces reviennent peu à peu, vous reprenez confiance." if  pv >= pv_m * 0.65 else
                            "Vous vous sentez toujours un peu mal." if pv >= pv_m * 0.45 else
                   YELLOW + "Vous ne vous sentez pas très bien." if pv >= pv_m * 0.30 else
                   YELLOW + "Vous vous sentez mal." if pv >= pv_m * 0.20 else
                   RED    + "Vous ressentez à peine la douleur s'affaiblir." if pv >= pv_m * 0.05 else
                   RED    + "Vous vous trouvez toujours aux portes de la mort.") + RESET + "\n")

        elif temp == "b":
            temp2 = int(input("Boost de résistance (PV) : "))
            pv_m += temp2
            if pv_m <= 0:
                pv_m = 1
            pv += temp2
            if temp2 > 0:
                print(f"\n{CYAN}Votre résistance augmente de {temp2}.{RESET}\n")
            elif temp2 < 0:
                print(f"\n{CYAN}Votre résistance diminue de {-temp2}.{RESET}\n")

        elif temp == "a":
            temp2 = int(input("Boost d'armure : "))
            armure += temp2
            if armure < 0:
                armure = 0
            if temp2 > 0:
                print(f"\n{CYAN}Votre armure augmente de {temp2}.{RESET}\n")
            elif temp2 < 0:
                print(f"\n{CYAN}Votre armure diminue de {-temp2}.{RESET}\n")

        elif temp == "q":
            run = False

        elif temp == "r":
            pv_m = pv_base
            pv = pv_base
            armure = armure_base
            print(f"\n\n{CYAN}--- Réinitialisation effectuée ---{RESET}\n\n")

        elif temp in ["d", ""]:
            attaque = int(input("Dégâts : "))
            degas = max(attaque - armure, 1)

        elif temp == "p":
            degas = int(input("Dégâts directs : "))

        else:
            continue

        if temp in ["d", "p"]:
            pv -= degas
            print()

            if pv <= 0:
                print(RED + choice(text_mort) + RESET)
                pv = 0
            elif pv <= pv_m * 0.07:
                print(RED + choice(text_presque_mort) + RESET)
            elif pv <= pv_m * 0.14:
                print(RED + choice(text_proche_mort) + RESET)
            elif pv <= pv_m * 0.25:
                print(YELLOW + choice(text_douleur_intense) + RESET)
            elif pv <= pv_m * 0.4:
                print(YELLOW + choice(text_douleur_forte) + RESET)
            elif pv <= pv_m * 0.6:
                print(choice(text_douleur_moyenne))
            elif pv <= pv_m * 0.8:
                print(choice(text_standard))
            elif pv <= pv_m * 0.9:
                print(GREEN + choice(text_douleur_faible) + RESET)
            else:
                print(GREEN + choice(text_min) + RESET)

            print()

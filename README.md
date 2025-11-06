# Heros_assist - beta

![Build Status](https://github.com/vincentcestmoi/Heros_assist/actions/workflows/java-shell.yml/badge.svg)  

**Heros_assist** est originalement un programme d’assistance pour accompagner les joueurs dans le jeu de rôle **Héros**.
Il automatise la plupart des interactions non-inter-joueurs et se suffit presque à lui-même.

## 🚀 Lancement du jeu

1. **Compilation** (à faire une seule fois) :
   ```bash
   ./compiler.sh
   ```
2. **Exécution** du jeu :
    ```bash
   ./heros.sh
   ```
3. Quitter le jeu :  
   Lors du choix d’action du tour, entrez `q` deux fois pour quitter proprement.

4. Vérifier la version :
    ```bash
   ./heros.sh --version
   ```

⚠️ Quand vous chargez une save existante sans la poursuivre, elle sera écrasée.

---

## 📜 Consignes de jeu
Ce programme ne gère qu’une partie des règles.
La majorité des données doivent être suivies par les joueurs eux-mêmes.  
L’usage d’un classeur Excel ou d’un tableau est fortement recommandé. (voir le dossier `Table` pour des exemples)

### 🎒 Règles d’équipement

Tout équipement à l'exception des armes à 1 et 2 mains est considéré comme un objet.  
Certains items ont un effet spécial calculé par le jeu, indiqué par un code (ex : #31), pensez à noter ce code !  
Le terme "PP" désigne du mana ou de l'aura.

#### Quantité :
- Casques, boucliers, (tout équipement sauf contre-indication) : 1 par joueur
- Mains : 2 maximum (sauf Archimage : 1 seule)
- Bracelets : 4 maximum
- Consommables, bonus, divers, amélioration d'arc : illimités

### ⚔️ Règles d’attaque
- **Arc** et **mains** sont deux valeurs distinctes
- L'attaque apportée par les **mains** ne compte pas pour les **tirs**
- Les bonus d’**arc** ne comptent pas pour l’**attaque classique**
- Toute altération d’attaque affecte les deux valeurs
- Lorsqu’une attaque est demandée, entrez l’**attaque classique**, pas les tirs

### 🧙‍♂️ Spécificités

Dans un esprit de jeu de rôle, Heros_assist privilégie l’immersion à la précision brute. La plupart des actions ne vous
afficheront pas directement leurs valeurs numériques, mais vous fourniront plutôt une description narrative de
l’effet produit.

Chaque classe possède ses propres forces, faiblesses et mécaniques uniques, qui évoluent avec l’expérience et
les niveaux. Certaines capacités ne sont accessibles qu’à partir d’un certain niveau ou dans des conditions spécifiques.

Le programme ne cherche pas à imposer une lecture rigide des règles :

- Vous pouvez ignorer les suggestions du programme si elles ne correspondent pas à votre style de jeu
- Vous êtes libres de choisir vos propres valeurs, effets ou interprétations

La plupart des actions reposent sur le jugement des joueurs : Héros reste avant tout un jeu de rôle. Il est totalement
possible d'ignorer les informations fournit par le programme ou de choisir vous-même vos données.  
Il est par exemple prévu que les consommables demandent une action pour être utilisés, et qu'un consommable bonus puisse
être utilisé en plus de l'action principale de combat, mais rien n'interdit aux joueurs de ne pas l'appliquer.

--- 

## 🧭 Aides et commandes cachées

Le dossier `Table/` contient un des tableurs de gestion des statistiques.

### 🔍 Commandes spéciales disponibles lors du choix d’action de tour :

| Commande | Effet                                                   |
|----------|---------------------------------------------------------|
| `del`    | Supprime le familier d’un joueur                        |
| `q`      | Quitte proprement la partie en sauvegardant les données |
| `add`    | Ajoute un familier (obéissance minimale)                |
| `re`     | Revient au joueur précédent                             |
| `sui`    | Tue le joueur actif                                     |
| `addit`  | Ajout un objet particulier                              |
| `delit`  | Retire un objet particulier                             |

⚠️ Certaines commandes doivent être confirmées, il suffit de les écrire une seconde fois.

### 🔍 Commandes spéciales disponibles lors du choix d’action de combat :


| Commande | Effet                                                                                                                   |
|----------|-------------------------------------------------------------------------------------------------------------------------|
| `q`      | Met fin abruptement au combat                                                                                           |
| `o`      | Signal un état particulier du joueur actif ou du joueur <br/>en première ligne (mort, assommé, berserk, ou hors combat) |


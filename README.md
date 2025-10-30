# 🛡️ Heros_assist

**Heros_assist** est un programme d’assistance limité pour accompagner les joueurs dans le jeu de rôle **Héros**. Il automatise certaines interactions tout en laissant la majorité des décisions aux joueurs.

---

## 🚀 Lancement du jeu

1. **Compilation** (à faire une seule fois) :
   ```bash
   ./compiler.sh
   ```
2. **Exécution** du jeu :
    ```bash
   ./lancer.sh
   ```
3. Quitter le jeu :  
Lors du choix d’action du tour, entrez `q` deux fois pour quitter proprement.

⚠️ Une seule sauvegarde est disponible, si vous ne la chargez pas, elle sera écrasée.

---

## 📜 Consignes de jeu
Ce programme ne gère qu’une partie des règles.
La majorité des données doivent être suivies par les joueurs eux-mêmes.  
L’usage d’un classeur Excel ou d’un tableau est fortement recommandé. (voir le dossier Table pour des exemples)

### 🎒 Règles d’équipement
- Casques, boucliers, (tout équipement sauf contre-indication) : 1 par joueur
- Mains : 2 maximum (sauf Archimage : 1 seule)
- Bracelets : 4 maximum
- Consommables, bonus, divers : illimités

### ⚔️ Règles d’attaque
- **Arc** et **mains** sont deux valeurs distinctes
- L'attaque apportée par les **mains** ne compte pas pour les **tirs**
- Les bonus d’**arc** ne comptent pas pour l’**attaque classique**
- Toute altération d’attaque affecte les deux valeurs
- Lorsqu’une attaque est demandée, entrez l’**attaque classique**, pas les tirs
 

  La plupart des actions reposent sur le jugement des joueurs : Héros reste avant tout un jeu de rôle. Il est totalement
possible d'ignorer les informations fournit par le programme ou de choisir vous-même vos données.  
Il est par exemple prévu que les consommables demandent une action pour être utilisés, et qu'un consommable bonus puisse 
  être utilisé en plus de l'action principale de combat, mais rien n'interdit aux joueurs de ne pas l'appliquer.

--- 

## 🧭 Aides et commandes cachées

Le dossier `Table/` contient un des tableurs de gestion des statistiques.

### 🔍 Commandes spéciales disponibles lors du choix d’action de tour :

| Commande | Effet                                    |
|----------|------------------------------------------|
| `del`    | Supprime le familier d’un joueur         |
| `add`    | Ajoute un familier (obéissance minimale) |
| `re`     | Revient au joueur précédent              |
| `sui`    | Tue le joueur actif                      |

⚠️ Chaque commande doit être confirmée en la réécrivant une seconde fois.

### 🔍 Commandes spéciales disponibles lors du choix d’action de combat :


| Commande | Effet                                                                                                                  |
|----------|------------------------------------------------------------------------------------------------------------------------|
| `q`      | Met fin abbruptement au combat                                                                                         |
| `r`      | Reviens au joueur précédent, peut causer une <br/>double attaque de la part du monstre adversaire par erreur           |
| `o`      | Signal un état particulier du joueur actif ou du joueur <br/>en première ligne (mort, assomé, berserk, ou hors combat) |

⚠️ Chaque commande vous demandera une confirmation, appuyez simplement sur entrée.

---

## 🐍 Scripts Python complémentaires

Le dossier `Hero_assist_python/` contient des programmes Python utiles pour la gestion du jeu.  
Ils peuvent être exécutés directement en ligne de commande, à condition d’avoir les autorisations nécessaires.

### Exemple :
```bash
./Hero_assist_python/gestion_pv.py
```
Comme son nom l'indique, ce fichier permet de gérer vos point de vie à votre place.

Si l’exécution échoue, vérifiez que le fichier possède les droits d'exécution :
```bash
chmod +x Hero_assist_python/gestion_pv.py
```

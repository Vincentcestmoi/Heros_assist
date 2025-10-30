# Heros_assist
Un programme limité pour aider à jouer à Héros.

## Lancement du jeu
Compilez (uniquement la première fois) en exécutant le shell compiler.sh  
Lancez le jeu avec le shell lancer.sh  
Pour quitter le jeu, entrez q deux fois lors du choix de l'action du tour.  

## Consignes de jeu
La plupart des données doivent être gérées par les joueurs. L'utilisation d'un classeur (type excel) est fortement recommendé.  
Les équipements ont plusieurs types. La plupart sont limités à 1 par joueur, comme les casque ou boucliers.  
Les mains sont limités à 2 (à l'exception de l'archimage qui est limité à 1).  
Les bracelets sont limités à 4.
Les consommables, consommables bonus et divers ne sont pas limités.  
Utiliser un consommable remplace l'action principale. Vous pouvez utiliser un consommable bonus en plus de votre action principale.  
L'arc et les mains sont deux valeurs séparées. L'attaque des mains n compte pas pour les valeurs de tir, et les valeurs de l'arc et des améliorations d'arcs ne comptent pas pour l'attaque classique.  
Toute altération sur l'attaque affecte les deux valeurs. Quand votre attaque est demandé, vous devez remplir votre attaque classique (pas les tirs).

La pluparts des actions sont soumises au bon jugement des joueurs, il s'agit à la base d'un jeu de rôle.


## Aides

Voir dans le dossier Table pour avoir un exemple de gestion des statistiques.
Le choix d'action de jeu a plusieurs actions cachées :
- del, pour supprimer le familier d'un joueur
- add, pour ajouter un familier à un joueur (son obéissance sera au minimum)
- re, pour revenir au joueur précédent
- sui, pour tuer le joueur actif  
- Chacune de ces commandes vous demandera de confirmer en les écrivant une deuxième fois.
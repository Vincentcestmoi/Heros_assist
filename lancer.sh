#!/bin/bash
# lancer.sh — lance le programme avec les .jar et un argument de sauvegarde optionnel
# À exécuter depuis la racine du projet

set -e

# Vérifie les dossiers nécessaires
for dir in Save0 Save1 Save2; do
    if [ ! -d "$dir" ]; then
        echo "📁 Dossier $dir manquant, création automatique..."
        mkdir -p "$dir"
    fi
done

if [ ! -d "bin" ]; then
    echo "Erreur : dossier bin/ introuvable. Compilez d'abord avec ./compiler.sh"
    exit 1
fi

if [ ! -d "lib" ]; then
    echo "Erreur : dossier lib/ introuvable."
    exit 1
fi

# Détection du système pour le séparateur de classpath
SEP=":"
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    SEP=";"
fi

# Lancement avec ou sans argument
if [ $# -eq 0 ]; then
    java -cp "bin${SEP}lib/*" main.Main
elif [ "$1" == "--test" ]; then
    java -cp "bin${SEP}lib/*" main.Main --test
elif [ "$1" == "--test2" ]; then
    cd Save0
    rm -rf info.json
    cd ..
    printf "0\ntest\nO\n3\nJ1\nO\nno\nJ2\nO\nau\nJ3\nO\ngu\nq\nq\n" | java -cp "bin${SEP}lib/*" main.Main
elif [ "$1" == "--test3" ]; then
    printf "0\ntest\nO\ns\nc\nre\nre\nm\n4\nq\nq\n" | java -cp "bin${SEP}lib/*" main.Main
else
    java -cp "bin${SEP}lib/*" main.Main "$@"
fi

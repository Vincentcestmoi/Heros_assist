#!/bin/bash
# compiler.sh — compile les fichiers Java avec les dépendances .jar
# À exécuter depuis la racine du projet

set -e
set -euo pipefail

# Vérifie que les dossiers existent
if [ ! -d "src" ]; then
    echo "Erreur : dossier src/ introuvable."
    exit 1
fi

if [ ! -d "lib" ]; then
    echo "Erreur : dossier lib/ introuvable."
    exit 1
fi

mkdir -p bin
rm -rf bin/*

# Compilation récursive de tous les .java
find src -name "*.java" > sources.txt
javac -cp "lib/*" -d bin @sources.txt
rm sources.txt

echo "✅ Compilation terminée. Les fichiers .class sont dans bin/"
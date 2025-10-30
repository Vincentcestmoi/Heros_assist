#!/bin/bash
# compiler.sh — compile tous les fichiers Java dans src/

set -e
set -euo pipefail

# Vérifie que le dossier src existe
if [ ! -d "src" ]; then
    echo "❌ Erreur : dossier src/ introuvable."
    exit 1
fi

# Crée le dossier bin si nécessaire
mkdir -p bin
rm -rf bin/*

# Compilation
javac -d bin src/*.java

echo "✅ Compilation terminée. Les fichiers .class sont dans bin/"

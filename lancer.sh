#!/bin/bash
# lancer.sh — lance le jeu Java, en s'assurant que le dossier Save existe

set -e
set -euo pipefail

# Vérifie que le dossier bin existe
if [ ! -d "bin" ]; then
    echo "❌ Erreur : dossier bin/ introuvable. Compilez d'abord avec ./compiler.sh"
    exit 1
fi

# Crée le dossier Save si nécessaire
if [ ! -d "Save" ]; then
    echo "📁 Dossier Save manquant, création automatique..."
    mkdir -p Save
fi

# Lancement
java -cp bin Main "$@"

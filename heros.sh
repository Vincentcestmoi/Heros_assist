#!/usr/bin/env bash

set -euo pipefail

# Détection automatique du JAR (le plus récent dans le dossier courant)
JAR=$(ls -t *.jar 2>/dev/null | head -n1 || true)

if [ -z "$JAR" ]; then
    echo "Erreur : aucun fichier .jar trouvé. Compilez d'abord avec ./compiler.sh"
    exit 1
fi

# Vérifie la présence de java
if ! command -v java >/dev/null 2>&1; then
    echo "❌ Erreur : java introuvable."
    echo "👉 Installez-le avec :"
    echo "   - Debian/Ubuntu : sudo apt install default-jre"
    echo "   - Fedora        : sudo dnf install java-17-openjdk"
    echo "   - Arch Linux    : sudo pacman -S jre-openjdk"
    echo "   - NixOS         : nix-shell -p openjdk"
    exit 1
fi

echo "ℹ️ Lancement avec $JAR"

# Lancement
if [ $# -eq 0 ]; then
  java -jar "$JAR"

elif [ "$1" == "--test" ]; then
    if [ $# -eq 1 ]; then
        # juste ./heros.sh --test
        java -jar "$JAR" --test
    else
        TESTFILE="tests/$2.in"
        if [ ! -f "$TESTFILE" ]; then
            echo "⚠️ Fichier de test introuvable : $TESTFILE"
            exit 1
        fi

        # cas particulier : test2
        if [ "$2" == "test2" ]; then
            rm -f Save0/info.json
        fi

        java -jar "$JAR" < "$TESTFILE"
    fi

else
    java -jar "$JAR" "$@"
fi



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
    java -jar "$JAR" --test
elif [ "$1" == "--test2" ]; then
    rm -f Save0/info.json
    printf "0\ntest\nO\n3\nJ1\nO\nno\nJ2\nO\nau\nJ3\nO\ngu\nq\nq\n" | java -jar "$JAR"
elif [ "$1" == "--test3" ]; then
    printf "0\nO\ns\nc\nre\nre\nm\n6\nn\nq\nq\n" | java -jar "$JAR"
elif [ "$1" == "--test4" ]; then
    printf "0\nn\nO\ntest n2\nO\n1\nMoi\nO\nra\nc\nE\n2\nN\nq\nq\n" | java -jar "$JAR"
else
    java -jar "$JAR" "$@"
fi

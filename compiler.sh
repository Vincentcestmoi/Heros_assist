#!/usr/bin/env bash

set -euo pipefail

# Vérification de la JVM
if ! command -v javac >/dev/null 2>&1; then
    echo "❌ Erreur : javac (compilateur Java) introuvable."
    echo "👉 Installez-le avec :"
    echo "   - Debian/Ubuntu : sudo apt install default-jdk"
    echo "   - Fedora        : sudo dnf install java-17-openjdk-devel"
    echo "   - Arch Linux    : sudo pacman -S jdk-openjdk"
    echo "   - NixOS         : nix-shell -p openjdk"
    exit 1
fi

if ! command -v java >/dev/null 2>&1; then
    echo "❌ Erreur : java (machine virtuelle) introuvable."
    echo "👉 Installez-le avec :"
    echo "   - Debian/Ubuntu : sudo apt install default-jre"
    echo "   - Fedora        : sudo dnf install java-17-openjdk"
    echo "   - Arch Linux    : sudo pacman -S jre-openjdk"
    echo "   - NixOS         : nix-shell -p openjdk"
    exit 1
fi

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

find src -name "*.java" > sources.txt

# Construit le classpath (tous les jars de lib + bin)
CP="bin:$(echo lib/*.jar | tr ' ' ':')"

javac -cp "$CP" -d bin @sources.txt
rm sources.txt

echo "✅ Compilation terminée. Les fichiers .class sont dans bin/"

# Détection automatique de la classe Main
MAIN_FILE=$(find src -name "Main.java" | head -n1)
if [ -z "$MAIN_FILE" ]; then
    echo "❌ Erreur : impossible de trouver Main.java"
    exit 1
fi

# Extraction du package (si présent)
PACKAGE=$(grep -E '^package ' "$MAIN_FILE" | sed 's/package\s\+\(.*\);/\1/' || true)

if [ -n "$PACKAGE" ]; then
    MAIN_CLASS="$PACKAGE.Main"
else
    MAIN_CLASS="Main"
fi

echo "ℹ️ Classe principale détectée : $MAIN_CLASS"

# Création du MANIFEST
MANIFEST_FILE=manifest.txt
echo "Main-Class: $MAIN_CLASS" > "$MANIFEST_FILE"
{
  echo -n "Class-Path: "
  for jar in lib/*.jar; do
    [ -e "$jar" ] || continue
    echo -n "lib/$(basename "$jar") "
  done
  echo
} >> "$MANIFEST_FILE"


# Création du jar exécutable
JAR_NAME="Heros.jar"
jar cfm "$JAR_NAME" "$MANIFEST_FILE" -C bin .

rm "$MANIFEST_FILE"

echo "✅ Jar créé : $JAR_NAME"
echo "👉 Lance avec : java -jar $JAR_NAME ou ./heros.sh"

#doit être exécuté depuis le src
# lib est une dépandence indispensable
javac -cp "../lib/*" -d bin *.java #compiler
java -cp "bin:../lib/*" Main ../Save #lancer, en argument le chemin relatif des fichiers sauvegarde
Permet de générer automatiquement tous les scripts pipelines.

[Info techiques]
JDK 1.8
maven 3.5.4

[Paramétrages]
Sauf modification majeur, tout se passe dans les fichiers propeties.
Modifier les fichiers qui sont dans conf ne nessécite pas un mvn clean install.

[Exécution]
Si nécessaire, faire un clean install de l'application.
En ligne de commande, se placer à la racine du projet et lancer la commande 'java -jar target/pipelineScripts-1.0.jar'.
Les fichiers générés sont à commiter sur le repo Git.

[Info pratiques]
Le répertoire à partir duquel lancer la commande java -jar doit être la racine du projet.
Le jar généré ne doit pas être déplacé car il utilise en chemin relatif le dossier 'conf' ainsi que le dossier 'target/libs'.

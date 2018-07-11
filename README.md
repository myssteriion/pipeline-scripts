# Description

Permet de générer automatiquement tous les scripts pipelines d'eSirius.

# Info techiques

**JDK** : 1.8  
**maven** : 3.5.4

# Paramétrages

Sauf modification majeur, tout se passe dans les fichiers du dossier conf.  
Modifier les fichiers qui sont dans conf ne nessécite pas un mvn clean install.  
Si le code java change, faire un clean install de l'application.

# Exécution

En ligne de commande, se placer à la racine du projet et lancer la commande 'java -jar target/pipelineScripts-1.0.jar'.  
Les fichiers générés sont à commiter sur le repo Git.

# Info pratiques

Le répertoire à partir duquel lancer la commande java -jar doit être la racine du projet.  
Le jar généré ne doit pas être déplacé car il utilise en chemin relatif le dossier 'conf' ainsi que le dossier 'target/libs'.

# Attention

La syntaxe __${xxx}__ sera écrit tel quel dans le pipeline généré car c'est une variable d'env (d'un point de vue pipeline).  
La syntaxe __$xxx__ est remplacé directent par le code java lors de l'exécution du jar => ce sont donc des valeurs en dur dans le pipeline.  
Les blocs vides (d'un point de vue pipeline) sont des erreurs de syntaxe (attention au bloc parameter, tools et env vide => commenter les blocs si nécessaire).

# parameters.json

Les commantaires ne sont pas accepté dans les fichiers JSON. La description est faites ici.  
Le fichier décrit les paramètre du script. Pour chaque paramètre, il faut :

 * **name** : le nom du paramètre
 * **type** : le type du paramètre (valeur possible : [boolean, string, choice]
 * **scope** : (facultatif) permet de définir si le paramètre est :  
	- uniquement présent sur le monoBuild (mono)  
	- uniquement présent sur le buildAll (all)  
	- sur les deux (ne pas renseigner la clé)
 * **description** : la description du paramètre
 * si type == boolean ou string :  
 	- **defaultValue** : la valeur par défaut (attention, les booleans ne sont pas à encadrer de doubles cotes)  
 * si type == choice :  
 	- **choices** : la liste de valeur séparée par le caractère ','
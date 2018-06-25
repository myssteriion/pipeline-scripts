pipeline-scripts

Contient tous les scripts Groovy exécutés par les pipelines Jenkins.

A ce jour, il y a énormément de duplicaiton (paramètre, environement, fonction...). Si un des scripts doit être modifié, vérifier aussi tous les autres scripts.

remarque : si le temps et l'envie le permet, faire un script java (ou autre), qui génère tous les Jenkinsfile à partir d'une conf et d'une concaténation de fichiers. A chaque modif, il suffira de lancer le script qui génère les Jenkinsfile. (cela permetra qu'il n'y ai plus de duplication)
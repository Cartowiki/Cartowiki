# Cartowiki

[Cartowiki](http://www.cartowiki.com) est un projet visant à représenter le monde de manière cartographique, de l’an -3000 à aujourd’hui. Il s'agit d'une carte du monde au 31 décembre de chaque année sur une période de 5000 ans.

Cette carte met en avant deux types d’éléments : les villes et les États. Le site dispose d’une base de données contenant l’évolution des frontières et des populations des États ainsi que la localisation et la démographie des villes au fil des siècles.

Tout utilisateur peut contribuer à cette base de données via différentes interfaces après la création d’un compte Cartowiki. Toutefois, même sans inscription, il est possible de visualiser les cartes année après année, sans pouvoir y apporter de modifications.

Ancien github : https://github.com/bhagavadgitadu22/cartowiki

## Développement

Le développement de ce projet a lieu en tant qu'exercice pour un groupe de 4 élèves de l'[École Centrale de Nantes](https://www.ec-nantes.fr) en option Informatique des Systèmes d'Information.

## Installation

Ce projet repose sur plusieurs services Docker, notamment **PostGIS**, **GeoServer**, un backend **Spring Boot**, et un frontend **Angular**. L'installation se fait en quelques étapes simples.

## Prérequis

Avant de commencer, assurez-vous d'avoir installé :  
- [Docker](https://www.docker.com/) (incluant docker-compose)
- Git (optionnel, mais recommandé)  

## Configuration des variables d’environnement

Créez un fichier `.env` à la racine du projet et renseignez les variables nécessaires :  

```ini  
POSTGRES_USER=user
POSTGRES_PASSWORD=password
POSTGRES_DB=db
SPRING_DATASOURCE_URL=jdbc:postgresql://postgis:5432/db  
GEOSERVER_ADMIN_USER=user  
GEOSERVER_ADMIN_PASSWORD=password  
NODE_ENV=development
```

## Étapes d’installation avec des commandes bash

## Démarrage du projet  

**Clonez le dépôt** :  
   ```sh
   git clone https://github.com/votre-repo/cartowiki.git  
   cd cartowiki
   ```

Avant de lancer les services, assurez-vous que toutes les images Docker sont bien construites. Pour cela, utilisez la commande suivante :  

```sh
docker-compose -f docker-compose.dev.yml build
```
Cela va construire les images à partir des Dockerfile définis dans le projet.

**Démarrage des services** :
Pour démarrer tous les services (mode détaché : rajouter -d) :

```sh
docker-compose -f docker-compose.dev.yml up
```

**Arrêter les services** :
Pour arrêter tous les services :

```sh
docker-compose -f docker-compose.dev.yml down
```

**Arrêter les services en supprimant les volumes associés (⚠️ perte des données non sauvegardées)** :
Pour arrêter tous les services :

```sh
docker-compose -f docker-compose.dev.yml down -v
```

# Description de l'API pour ChâTop

Bienvenue sur la documentation de l'API ChâTop, le portail innovant de mise en relation entre futurs locataires et propriétaires dédié à la location saisonnière sur la côte basque. Notre objectif est d'étendre notre service à toute la France, offrant une plateforme facile à utiliser, sécurisée et efficace pour tous les utilisateurs.

## Objectif de l'API

Cette API a été conçue pour fournir le backend nécessaire à notre application Angular, permettant de passer de données mockées à une véritable interaction avec une base de données. Elle sert de pont entre les utilisateurs de notre plateforme et les données stockées, gérant l'authentification, la consultation, la création et la gestion des annonces de locations saisonnières.

## Fonctionnalités Principales

- **Authentification des Utilisateurs :** Supporte l'authentification des locataires et des propriétaires, permettant un accès sécurisé aux fonctionnalités de la plateforme.
- **Gestion des Annonces :** Permet aux propriétaires de créer, modifier et supprimer leurs annonces de location. Les locataires peuvent parcourir, rechercher et réserver des locations.
- **Sécurité :** L'API utilise des standards de sécurité modernes pour protéger les données des utilisateurs et des transactions.
- **Documentation Interactive :** Fournie avec Swagger UI, permettant une exploration facile et un test des différents endpoints de l'API.

## Commencer avec l'API

### Prérequis

- Java 11 ou supérieur
- Maven pour la gestion des dépendances et le build
- MySQL ou un système de gestion de base de données compatible pour le stockage des données

### Installation et Configuration

1. **Cloner le Repository :** Commencez par cloner le dépôt Git contenant le code source et les ressources nécessaires.

    ```
    git clone https://github.com/jbpoujol/OC-projet-3.git
    ```

2. Installation de la Base de Données

Avant de lancer l'API, vous devez créer et configurer la base de données. Voici les étapes à suivre ainsi que le script SQL nécessaire pour créer la structure de base de données requise par l'API.

### Script de Création de la Base de Données

Exécutez le script SQL suivant dans votre système de gestion de base de données (SGBD) pour créer les tables nécessaires :

```sql
CREATE TABLE `USERS` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `email` varchar(255),
  `name` varchar(255),
  `password` varchar(255),
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `RENTALS` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255),
  `surface` numeric,
  `price` numeric,
  `picture` varchar(255),
  `description` varchar(2000),
  `owner_id` integer NOT NULL,
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`owner_id`) REFERENCES `USERS` (`id`)
);

CREATE TABLE `MESSAGES` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `rental_id` integer,
  `user_id` integer,
  `message` varchar(2000),
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`rental_id`) REFERENCES `RENTALS` (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `USERS` (`id`)
);

CREATE UNIQUE INDEX `USERS_index` ON `USERS` (`email`);
```

### Configuration dans `application.properties`

Pour renforcer la sécurité, nous utilisons Jasypt pour chiffrer le mot de passe de la base de données dans le fichier `application.properties`. Voici comment procéder :

#### Chiffrer le Mot de Passe de la Base de Données

Utilisez la commande Maven suivante pour chiffrer votre mot de passe :

```shell
mvn jasypt:encrypt-value "-Djasypt.encryptor.password=jsyptkey" "-Djasypt.plugin.value=VotreMotDePasseDB"
```

Remplacez `VotreMotDePasseDB` par le mot de passe réel de votre base de données et `jsyptkey` par la clé que vous souhaitez utiliser pour le chiffrement.

#### Configurer `application.properties`

Dans votre fichier `src/main/resources/application.properties`, configurez votre connexion à la base de données avec le mot de passe chiffré :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nom_de_votre_base?serverTimezone=UTC
spring.datasource.username=votre_utilisateur
spring.datasource.password=ENC(ValeurChiffrée)
```

Remplacez `ValeurChiffrée` par la sortie de la commande de chiffrement.

Pour déchiffrer le mot de passe au démarrage, fournissez la clé de chiffrement `jasyptkey` en tant qu'argument VM ou variable d'environnement.

3. **Lancer l'Application :** Utilisez Maven pour lancer l'application en fournissant la clé de chiffrement.

    ```
    mvn spring-boot:run -Dspring-boot.run.arguments="--jasypt.encryptor.password=jsyptkey"
    ```

Ou configurez la variable d'environnement `JASYPT_ENCRYPTOR_PASSWORD` avant de lancer l'application.

### Utilisation de l'API

Après le démarrage de l'application, l'API est accessible à l'adresse `http://localhost:3001/api`. Utilisez la collection Postman fournie pour explorer et tester les différents endpoints.

### Documentation Swagger

La documentation interactive Swagger UI est disponible à l'URL suivante : `http://localhost:3001/api/swagger-ui.html`. Celle-ci offre une vue d'ensemble complète des endpoints disponibles, des modèles de données et permet d'exécuter des requêtes directement depuis le navigateur.

## Support

Pour toute question ou assistance supplémentaire, n'hésitez pas à contacter l'équipe technique. Nous sommes là pour vous aider à tirer le meilleur parti de l'API ChâTop.

---

Nous sommes ravis de vous avoir dans notre équipe et impatients de voir comment vos compétences en développement back-end contribueront au succès de ChâTop. Bonne chance et bon développement !

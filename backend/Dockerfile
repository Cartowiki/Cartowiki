# Étape 1 : Utiliser une image avec JDK pour compiler l'application
FROM openjdk:21-jdk-slim as builder

# Définir le répertoire de travail à ./backend
WORKDIR /app

# Copier tout le code source du backend dans l'image
COPY . /app

# Modifie le file ending de mvnw de Windows vers Linux
RUN apt-get update && apt-get install -y dos2unix && dos2unix /app/mvnw

# Donner les permissions d'exécution au wrapper mvnw
RUN chmod +x mvnw

# Exécuter la commande mvnw pour compiler le projet (sans les tests)
RUN ./mvnw clean package -DskipTests

# Étape 2 : Utiliser l'image de base pour exécuter l'application
FROM openjdk:21-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR généré depuis l'étape précédente
COPY --from=builder /app/target/webapp-0.0.1-SNAPSHOT.jar /app/app.jar

# Assurez-vous que le JAR a les bonnes permissions
RUN chmod +x /app/app.jar

# Lancer l'application
CMD ["java", "-jar", "app.jar"]

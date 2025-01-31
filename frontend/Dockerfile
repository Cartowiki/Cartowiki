# Étape 1 : Build de l'application Angular
FROM node:22 AS build
WORKDIR /app

# Copier les fichiers de dépendances
COPY package.json package-lock.json ./
RUN npm install

# Copier le reste des fichiers
COPY . ./

RUN npm install -g @angular/cli

CMD ["ng", "serve", "--host", "0.0.0.0", "--disable-host-check", "--poll", "2000"]

# ==============================================
# Pour la production, décommentez la partie suivante :
# ==============================================

# Étape 2 : Lancer l'application Angular avec Nginx
# FROM nginx:alpine

# Copier le fichier de configuration nginx.conf dans le conteneur Nginx
# COPY nginx.conf /etc/nginx/nginx.conf

# Copier les fichiers construits dans le répertoire de Nginx
# COPY --from=build /app/dist /usr/share/nginx/html

# Exposer le port 80 pour Nginx
# EXPOSE 80

# Commande pour démarrer Nginx
# CMD ["nginx", "-g", "daemon off;"]

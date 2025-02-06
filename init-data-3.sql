-- Création de la table pour les pays (polygones ou multipolygones)
CREATE TABLE countries (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    geom GEOMETRY(MultiPolygon, 4326) -- Utilisation de MultiPolygon pour gérer des polygones multiples
);

-- Création de la table pour les villes (points) avec population
CREATE TABLE cities (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    geom GEOMETRY(Point, 4326),
    population INTEGER -- Population associée à la taille du point
);

-- Insertion des pays avec des géométries en multipolygones
INSERT INTO countries (name, geom)
VALUES
    ('France', ST_GeomFromText('MULTIPOLYGON(((2.5 49.5, 3 50, 4 51, 5 51.5, 5 49, 2.5 49.5), (2 46, 3 45, 4 44, 5 46, 4 47, 2 46)))', 4326)),
    ('Brazil', ST_GeomFromText('MULTIPOLYGON(((-60 5, -58 4, -56 3, -55 2, -60 5)), ((-55 6, -53 7, -51 6, -50 5, -55 6)))', 4326)),
    ('Australia', ST_GeomFromText('MULTIPOLYGON(((-130 -50, -120 -49, -110 -48, -100 -47, -90 -45, -80 -45, -80 -55, -90 -55, -100 -54, -110 -53, -120 -52, -130 -50)))', 4326));


-- Insertion des villes avec des géométries en points et leur population
INSERT INTO cities (name, geom, population)
VALUES
    ('Paris', ST_GeomFromText('POINT(2.3522 48.8566)', 4326), 2148000), -- Paris, France
    ('Rio de Janeiro', ST_GeomFromText('POINT(-43.1729 -22.9068)', 4326), 6748000), -- Rio de Janeiro, Brésil
    ('Sydney', ST_GeomFromText('POINT(151.2093 -33.8688)', 4326), 5312000), -- Sydney, Australie
    ('New York', ST_GeomFromText('POINT(-74.0060 40.7128)', 4326), 8419600), -- New York, USA
    ('Tokyo', ST_GeomFromText('POINT(139.6917 35.6895)', 4326), 13960000), -- Tokyo, Japon
    ('Berlin', ST_GeomFromText('POINT(13.4050 52.5200)', 4326), 3769000), -- Berlin, Allemagne
    ('Cairo', ST_GeomFromText('POINT(31.2357 30.0444)', 4326), 20000000), -- Le Caire, Égypte
    ('Cape Town', ST_GeomFromText('POINT(18.4241 -33.9249)', 4326), 433688), -- Cape Town, Afrique du Sud
    ('Moscow', ST_GeomFromText('POINT(37.6173 55.7558)', 4326), 11920000), -- Moscou, Russie
    ('London', ST_GeomFromText('POINT(-0.1276 51.5074)', 4326), 8982000); -- Londres, Royaume-Uni


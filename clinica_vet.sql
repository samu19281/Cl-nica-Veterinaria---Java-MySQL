CREATE DATABASE IF NOT EXISTS clinica_vet;
USE clinica_vet;

CREATE TABLE mascotes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom_mascota VARCHAR(50) NOT NULL,
    especie VARCHAR(30),
    edat INT,
    propietari_nom VARCHAR(100),
    propietari_dni VARCHAR(10) NOT NULL 
);

INSERT INTO mascotes (nom_mascota, especie, edat, propietari_nom, propietari_dni) VALUES 
('Rex', 'Gos', 5, 'Joan Boscà', '12345678A'),
('Lua', 'Gat', 3, 'Maria Puig', '87654321B');


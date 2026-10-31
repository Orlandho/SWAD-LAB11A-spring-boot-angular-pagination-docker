-- =============================================================================
-- Universidad Privada del Norte (UPN) - Soluciones Web y Aplicaciones Distribuidas
-- Semana 11: Laboratorio 11A - Paginacion con Spring Boot y Consumo desde Angular
-- Script oficial de inicializacion de base de datos y catalogo de productos
-- =============================================================================

CREATE DATABASE IF NOT EXISTS bd_laboratorio11 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bd_laboratorio11;

CREATE TABLE IF NOT EXISTS producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DOUBLE NOT NULL,
    stock INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Limpiar tabla si existe data previa para garantizar idempotencia
DELETE FROM producto;

-- Insertar los 15 registros oficiales definidos en el Paso 12 de la guia
INSERT INTO producto (nombre, precio, stock) VALUES
('Laptop Lenovo', 2500, 10),
('Mouse Logitech', 80, 25),
('Teclado mecanico', 180, 15),
('Monitor 24 pulgadas', 650, 8),
('Webcam Full HD', 160, 12),
('SSD 1TB', 290, 9),
('Memoria RAM 16GB', 210, 20),
('Router WiFi 6', 380, 7),
('Audifonos USB', 95, 30),
('Base refrigerante', 120, 18),
('Impresora multifuncional', 780, 4),
('Disco externo 2TB', 360, 6),
('Cable HDMI', 35, 40),
('Hub USB-C', 150, 16),
('UPS 1000VA', 520, 3);

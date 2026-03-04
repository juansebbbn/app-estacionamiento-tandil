-- Initial database setup for Estacionamiento Tandil
-- This script runs automatically when MySQL container starts

-- Create database if not exists (already created by Docker env)
-- CREATE DATABASE IF NOT EXISTS estacionamiento_tandil;

-- Use the database
USE estacionamiento_tandil;

-- Set character set
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- (Tables will be created automatically by Hibernate/JPA)
-- This script is mainly for initial setup and any custom data

-- Insert initial data if needed
-- Example: INSERT INTO some_table (column1, column2) VALUES ('value1', 'value2');

SET FOREIGN_KEY_CHECKS = 1;

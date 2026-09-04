-- Reference schema for Sunrise Dental Clinic.
--
-- Hibernate creates/updates these tables automatically on startup because
-- spring.jpa.hibernate.ddl-auto=update (see application.properties), so
-- you do NOT need to run this file for the app to work. It is kept here so
-- the "db" folder shows the real table shape, and so you can run it by hand
-- in NetBeans' Services > Databases view (or phpMyAdmin) if you ever want
-- to inspect or recreate the schema without starting the app first.

CREATE DATABASE IF NOT EXISTS dentalclinic;
USE dentalclinic;

CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,          -- ADMIN | RECEPTIONIST | DENTIST
    display_name VARCHAR(255),
    email VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS treatment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    fee DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment (
    appointment_number VARCHAR(255) PRIMARY KEY,
    patient_name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_number VARCHAR(255) NOT NULL,
    dentist_name VARCHAR(255) NOT NULL,
    treatment_type VARCHAR(255) NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL          -- SCHEDULED | COMPLETED | CANCELLED
);

CREATE TABLE IF NOT EXISTS bill (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(255) NOT NULL UNIQUE,
    consultation_fee DECIMAL(10,2) NOT NULL,
    treatment_fee DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    generated_at DATETIME NOT NULL,
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_number)
        REFERENCES appointment (appointment_number)
);

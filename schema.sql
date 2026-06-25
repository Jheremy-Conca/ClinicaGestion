CREATE DATABASE IF NOT EXISTS clinica_db;
USE clinica_db;

CREATE TABLE personas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(8) UNIQUE NOT NULL,
    telefono VARCHAR(9),
    tipo ENUM('PACIENTE','MEDICO') NOT NULL
);

CREATE TABLE pacientes (
    id INT PRIMARY KEY,
    fecha_nacimiento DATE,
    ruc VARCHAR(11),
    razon_social VARCHAR(150),
    FOREIGN KEY (id) REFERENCES personas(id) ON DELETE CASCADE
);

CREATE TABLE medicos (
    id INT PRIMARY KEY,
    cmp VARCHAR(6) UNIQUE NOT NULL,
    especialidad VARCHAR(100),
    FOREIGN KEY (id) REFERENCES personas(id) ON DELETE CASCADE
);

CREATE TABLE consultorios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(10) NOT NULL UNIQUE,
    piso INT,
    disponible BOOLEAN DEFAULT TRUE
);

CREATE TABLE medico_consultorio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medico_id INT NOT NULL,
    consultorio_id INT NOT NULL,
    FOREIGN KEY (medico_id) REFERENCES medicos(id) ON DELETE CASCADE,
    FOREIGN KEY (consultorio_id) REFERENCES consultorios(id) ON DELETE CASCADE
);

CREATE TABLE citas_medicas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    paciente_id INT NOT NULL,
    medico_id INT NOT NULL,
    consultorio_id INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    estado ENUM('PENDIENTE','ATENDIDA','CANCELADA') DEFAULT 'PENDIENTE',
    FOREIGN KEY (paciente_id) REFERENCES personas(id),
    FOREIGN KEY (medico_id) REFERENCES personas(id),
    FOREIGN KEY (consultorio_id) REFERENCES consultorios(id)
);

CREATE TABLE diagnosticos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cita_id INT NOT NULL,
    descripcion TEXT,
    fecha_diagnostico DATE,
    FOREIGN KEY (cita_id) REFERENCES citas_medicas(id) ON DELETE CASCADE
);

CREATE TABLE comprobantes_pago (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cita_id INT NOT NULL,
    monto DECIMAL(10,2),
    fecha DATE,
    tipo ENUM('BOLETA','FACTURA') NOT NULL,
    ruc VARCHAR(11),
    razon_social VARCHAR(150),
    igv DECIMAL(10,2),
    FOREIGN KEY (cita_id) REFERENCES citas_medicas(id)
);

INSERT INTO personas (nombre, apellido, dni, telefono, tipo) VALUES
('Juan', 'Perez Diaz', '71234567', '987654321', 'PACIENTE'),
('Maria', 'Lopez Rojas', '72345678', '976543210', 'PACIENTE'),
('Carlos', 'Gomez Vega', '73456789', '965432109', 'PACIENTE'),
('Ana', 'Torres Sanchez', '74567890', '954321098', 'PACIENTE'),
('Luis', 'Ramirez Castro', '75678901', '943210987', 'PACIENTE'),
('Rosa', 'Flores Mendoza', '76789012', '932109876', 'PACIENTE'),
('Pedro', 'Vargas Quispe', '77890123', '921098765', 'PACIENTE'),
('Lucia', 'Chavez Huaman', '78901234', '910987654', 'PACIENTE'),
('Jorge', 'Salazar Cruz', '79012345', '909876543', 'PACIENTE'),
('Elena', 'Mamani Apaza', '70123456', '998765432', 'PACIENTE');

INSERT INTO pacientes (id, fecha_nacimiento, ruc, razon_social) VALUES
(1, '1990-05-12', NULL, NULL),
(2, '1985-08-23', NULL, NULL),
(3, '1995-01-30', NULL, NULL),
(4, '2000-11-15', NULL, NULL),
(5, '1978-03-09', '20123456789', 'Inversiones Ramirez SAC'),
(6, '1992-07-22', NULL, NULL),
(7, '1988-09-04', NULL, NULL),
(8, '2001-12-18', NULL, NULL),
(9, '1975-06-27', '20456789123', 'Comercial Salazar EIRL'),
(10, '1999-02-14', NULL, NULL);

INSERT INTO personas (nombre, apellido, dni, telefono, tipo) VALUES
('Roberto', 'Fernandez Leon', '81234567', '912345678', 'MEDICO'),
('Patricia', 'Diaz Morales', '82345678', '923456789', 'MEDICO'),
('Miguel', 'Castillo Reyes', '83456789', '934567890', 'MEDICO'),
('Sofia', 'Herrera Paz', '84567890', '945678901', 'MEDICO'),
('Andres', 'Jimenez Rios', '85678901', '956789012', 'MEDICO'),
('Carmen', 'Aguilar Soto', '86789012', '967890123', 'MEDICO'),
('Fernando', 'Paredes Luna', '87890123', '978901234', 'MEDICO'),
('Veronica', 'Campos Silva', '88901234', '989012345', 'MEDICO'),
('Ricardo', 'Nunez Ortiz', '89012345', '990123456', 'MEDICO'),
('Daniela', 'Rojas Medina', '80123456', '901234567', 'MEDICO');

INSERT INTO medicos (id, cmp, especialidad) VALUES
(11, '100001', 'Medicina General'),
(12, '100002', 'Pediatria'),
(13, '100003', 'Cardiologia'),
(14, '100004', 'Ginecologia'),
(15, '100005', 'Dermatologia'),
(16, '100006', 'Traumatologia'),
(17, '100007', 'Neurologia'),
(18, '100008', 'Oftalmologia'),
(19, '100009', 'Medicina General'),
(20, '100010', 'Pediatria');

INSERT INTO consultorios (numero, piso, disponible) VALUES
('101', 1, TRUE),
('102', 1, TRUE),
('103', 1, TRUE),
('104', 1, TRUE),
('201', 2, TRUE),
('202', 2, TRUE),
('203', 2, TRUE),
('204', 2, TRUE),
('301', 3, TRUE),
('302', 3, TRUE);

INSERT INTO medico_consultorio (medico_id, consultorio_id) VALUES
(11, 1),
(12, 2),
(13, 3),
(14, 4),
(15, 5);

UPDATE consultorios SET disponible = FALSE WHERE id IN (1, 2, 3, 4, 5);

INSERT INTO citas_medicas (paciente_id, medico_id, consultorio_id, fecha, hora, estado) VALUES
(1, 11, 1, '2026-06-20', '09:00:00', 'ATENDIDA'),
(2, 12, 2, '2026-06-20', '10:30:00', 'ATENDIDA'),
(3, 13, 3, '2026-06-21', '11:00:00', 'PENDIENTE'),
(4, 14, 4, '2026-06-21', '14:00:00', 'PENDIENTE'),
(5, 15, 5, '2026-06-19', '08:30:00', 'ATENDIDA'),
(6, 11, 1, '2026-06-22', '15:00:00', 'PENDIENTE'),
(7, 12, 2, '2026-06-18', '09:30:00', 'CANCELADA'),
(8, 13, 3, '2026-06-23', '10:00:00', 'PENDIENTE'),
(9, 14, 4, '2026-06-17', '16:00:00', 'ATENDIDA'),
(10, 15, 5, '2026-06-24', '11:30:00', 'PENDIENTE');

INSERT INTO diagnosticos (cita_id, descripcion, fecha_diagnostico) VALUES
(1, 'Paciente presenta cuadro de faringitis aguda. Se indica reposo e hidratacion.', '2026-06-20'),
(2, 'Control pediatrico de rutina. Desarrollo acorde a la edad. Sin observaciones.', '2026-06-20'),
(5, 'Dermatitis de contacto leve en antebrazo derecho. Se receta crema topica.', '2026-06-19'),
(9, 'Control ginecologico anual. Resultados dentro de parametros normales.', '2026-06-17');

INSERT INTO comprobantes_pago (cita_id, monto, fecha, tipo, ruc, razon_social, igv) VALUES
(1, 80.00, '2026-06-20', 'BOLETA', NULL, NULL, NULL),
(2, 70.00, '2026-06-20', 'BOLETA', NULL, NULL, NULL),
(5, 120.00, '2026-06-19', 'FACTURA', '20123456789', 'Inversiones Ramirez SAC', 21.60),
(9, 150.00, '2026-06-17', 'FACTURA', '20456789123', 'Comercial Salazar EIRL', 27.00);

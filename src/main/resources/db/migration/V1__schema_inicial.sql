-- ===================================================
-- SCHEMA INICIAL - Sistema Condominio
-- V1__schema_inicial.sql
-- ===================================================
-- CORRECCIONES APLICADAS:
--  1. residencias.id_propietario -> NOT NULL + ON DELETE RESTRICT
--  2. residencias.estado        -> OCUPADA | DESOCUPADA
--  3. residencias.cuota_mensual -> > 0 (no >= 0)
--  4. cuotas.valor              -> > 0 (no >= 0)
--  5. Función validar_cedula_ecuatoriana()
--  6. CHECK cédula en tabla residentes
--  7. Trigger: máx 10 residentes por casa
--  8. Trigger: máx 10 casas por propietario
--  9. Roles con descripciones correctas
-- 10. Hash BCrypt correcto para Admin2024!
-- ===================================================


-- ===================================================
-- FUNCIÓN: validar_cedula_ecuatoriana
-- ===================================================
-- Reglas:
--   - Exactamente 10 dígitos numéricos
--   - Provincia válida: 01-24
--   - Tercer dígito < 6  (cédulas de personas naturales)
--   - Dígito verificador correcto (módulo 10)
-- ===================================================
CREATE OR REPLACE FUNCTION validar_cedula_ecuatoriana(cedula TEXT)
RETURNS BOOLEAN AS $$
DECLARE
    coeficientes  INT[]  := ARRAY[2,1,2,1,2,1,2,1,2];
    suma          INT    := 0;
    digito        INT;
    producto      INT;
    verificador   INT;
    provincia     INT;
    i             INT;
BEGIN
    -- Longitud exacta de 10 dígitos
    IF length(cedula) != 10 THEN RETURN FALSE; END IF;
    -- Solo dígitos numéricos
    IF cedula !~ '^\d{10}$' THEN RETURN FALSE; END IF;

    provincia := CAST(substring(cedula, 1, 2) AS INT);
    -- Provincia válida 01-24
    IF provincia < 1 OR provincia > 24 THEN RETURN FALSE; END IF;
    -- Tercer dígito menor a 6
    IF CAST(substring(cedula, 3, 1) AS INT) >= 6 THEN RETURN FALSE; END IF;

    -- Módulo 10
    FOR i IN 1..9 LOOP
        digito   := CAST(substring(cedula, i, 1) AS INT);
        producto := digito * coeficientes[i];
        IF producto >= 10 THEN
            producto := producto - 9;
        END IF;
        suma := suma + producto;
    END LOOP;

    IF suma % 10 = 0 THEN
        verificador := 0;
    ELSE
        verificador := 10 - (suma % 10);
    END IF;

    RETURN verificador = CAST(substring(cedula, 10, 1) AS INT);
END;
$$ LANGUAGE plpgsql IMMUTABLE;


-- ===================================================
-- TABLA: residentes
-- ===================================================
CREATE TABLE IF NOT EXISTS residentes (
    id_residente  BIGSERIAL    PRIMARY KEY,
    nombres       VARCHAR(100) NOT NULL,
    apellidos     VARCHAR(100) NOT NULL,
    cedula        VARCHAR(10)  NOT NULL UNIQUE
                      CHECK (validar_cedula_ecuatoriana(cedula)),
    telefono      VARCHAR(15),
    estado        VARCHAR(10)  NOT NULL DEFAULT 'ACTIVO'
                      CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);


-- ===================================================
-- TABLA: residencias
-- ===================================================
-- CAMBIOS:
--   id_propietario  NOT NULL, ON DELETE RESTRICT
--   estado          OCUPADA | DESOCUPADA
--   cuota_mensual   > 0 y <= 500
-- ===================================================
CREATE TABLE IF NOT EXISTS residencias (
    id_residencia  BIGSERIAL    PRIMARY KEY,
    id_propietario BIGINT       NOT NULL
                       REFERENCES residentes(id_residente) ON DELETE RESTRICT,
    codigo_casa    VARCHAR(20)  NOT NULL UNIQUE,
    cuota_mensual  NUMERIC(10,2) NOT NULL
                       CHECK (cuota_mensual > 0 AND cuota_mensual <= 500),
    estado         VARCHAR(15)  NOT NULL DEFAULT 'DESOCUPADA'
                       CHECK (estado IN ('OCUPADA', 'DESOCUPADA')),
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);


-- ===================================================
-- TABLA: residente_residencia
-- ===================================================
CREATE TABLE IF NOT EXISTS residente_residencia (
    id_asignacion    BIGSERIAL PRIMARY KEY,
    id_residente     BIGINT    NOT NULL REFERENCES residentes(id_residente) ON DELETE CASCADE,
    id_residencia    BIGINT    NOT NULL REFERENCES residencias(id_residencia) ON DELETE CASCADE,
    parentesco       VARCHAR(50),
    fecha_asignacion DATE      NOT NULL DEFAULT CURRENT_DATE,
    UNIQUE (id_residente, id_residencia)
);


-- ===================================================
-- TABLA: cuotas
-- ===================================================
CREATE TABLE IF NOT EXISTS cuotas (
    id_cuota      BIGSERIAL    PRIMARY KEY,
    id_residencia BIGINT       NOT NULL REFERENCES residencias(id_residencia) ON DELETE CASCADE,
    mes           SMALLINT     NOT NULL CHECK (mes BETWEEN 1 AND 12),
    anio          SMALLINT     NOT NULL CHECK (anio >= 2000),
    valor         NUMERIC(10,2) NOT NULL CHECK (valor > 0 AND valor <= 500),
    UNIQUE (id_residencia, mes, anio)
);


-- ===================================================
-- TABLA: pagos
-- ===================================================
CREATE TABLE IF NOT EXISTS pagos (
    id_pago      BIGSERIAL    PRIMARY KEY,
    id_cuota     BIGINT       NOT NULL REFERENCES cuotas(id_cuota) ON DELETE RESTRICT,
    fecha_pago   DATE         NOT NULL DEFAULT CURRENT_DATE,
    monto_pagado NUMERIC(10,2) NOT NULL
                     CHECK (monto_pagado > 0 AND monto_pagado <= 500),
    estado       VARCHAR(15)  NOT NULL DEFAULT 'COMPLETADO'
                     CHECK (estado IN ('COMPLETADO', 'PARCIAL', 'PENDIENTE'))
);


-- ===================================================
-- TABLA: comunicados
-- ===================================================
CREATE TABLE IF NOT EXISTS comunicados (
    id_comunicado     BIGSERIAL    PRIMARY KEY,
    titulo            VARCHAR(200) NOT NULL,
    mensaje           TEXT         NOT NULL,
    prioridad         VARCHAR(10)  NOT NULL DEFAULT 'NORMAL'
                          CHECK (prioridad IN ('ALTA', 'NORMAL', 'BAJA')),
    fecha_vencimiento DATE,
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);


-- ===================================================
-- TABLA: comunicado_residente
-- ===================================================
CREATE TABLE IF NOT EXISTS comunicado_residente (
    id_envio      BIGSERIAL PRIMARY KEY,
    id_comunicado BIGINT    NOT NULL REFERENCES comunicados(id_comunicado) ON DELETE CASCADE,
    id_residente  BIGINT    NOT NULL REFERENCES residentes(id_residente)   ON DELETE CASCADE,
    fecha_envio   TIMESTAMP NOT NULL DEFAULT NOW(),
    leido         BOOLEAN   NOT NULL DEFAULT FALSE,
    UNIQUE (id_comunicado, id_residente)
);


-- ===================================================
-- TABLA: roles
-- ===================================================
CREATE TABLE IF NOT EXISTS roles (
    id_rol      BIGSERIAL    PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL UNIQUE,
    descripcion VARCHAR(200)
);


-- ===================================================
-- TABLA: usuarios
-- ===================================================
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario    BIGSERIAL    PRIMARY KEY,
    id_residente  BIGINT       REFERENCES residentes(id_residente) ON DELETE SET NULL,
    usuario       VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    estado        VARCHAR(10)  NOT NULL DEFAULT 'ACTIVO'
                      CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);


-- ===================================================
-- TABLA: usuario_rol
-- ===================================================
CREATE TABLE IF NOT EXISTS usuario_rol (
    id_usuario_rol BIGSERIAL PRIMARY KEY,
    id_usuario     BIGINT    NOT NULL REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    id_rol         BIGINT    NOT NULL REFERENCES roles(id_rol)        ON DELETE CASCADE,
    UNIQUE (id_usuario, id_rol)
);


-- ===================================================
-- TRIGGER: máximo 10 residentes por residencia
-- ===================================================
CREATE OR REPLACE FUNCTION check_max_residentes_por_residencia()
RETURNS TRIGGER AS $$
DECLARE
    total INT;
BEGIN
    SELECT COUNT(*) INTO total
    FROM residente_residencia
    WHERE id_residencia = NEW.id_residencia;

    IF total >= 10 THEN
        RAISE EXCEPTION 'La residencia % ya tiene el máximo de 10 residentes asignados',
                        NEW.id_residencia;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_max_residentes ON residente_residencia;
CREATE TRIGGER trg_max_residentes
    BEFORE INSERT ON residente_residencia
    FOR EACH ROW
    EXECUTE FUNCTION check_max_residentes_por_residencia();


-- ===================================================
-- TRIGGER: máximo 10 residencias por propietario
-- ===================================================
CREATE OR REPLACE FUNCTION check_max_residencias_por_propietario()
RETURNS TRIGGER AS $$
DECLARE
    total INT;
BEGIN
    -- Solo aplica en INSERT o cuando cambia el propietario en UPDATE
    IF (TG_OP = 'UPDATE' AND NEW.id_propietario = OLD.id_propietario) THEN
        RETURN NEW;
    END IF;

    SELECT COUNT(*) INTO total
    FROM residencias
    WHERE id_propietario = NEW.id_propietario;

    IF total >= 10 THEN
        RAISE EXCEPTION 'El propietario % ya tiene el máximo de 10 residencias permitidas',
                        NEW.id_propietario;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_max_residencias ON residencias;
CREATE TRIGGER trg_max_residencias
    BEFORE INSERT OR UPDATE OF id_propietario ON residencias
    FOR EACH ROW
    EXECUTE FUNCTION check_max_residencias_por_propietario();


-- ===================================================
-- DATOS INICIALES
-- ===================================================

-- Roles del sistema
INSERT INTO roles (nombre, descripcion)
VALUES
    ('ADMINISTRADOR', 'Control total del sistema'),
    ('RESIDENTE',     'Acceso limitado del residente')
ON CONFLICT (nombre) DO NOTHING;

-- Usuario administrador por defecto
-- Contraseña: Admin2024!
-- Hash BCrypt (strength 12) generado con BCryptPasswordEncoder
INSERT INTO usuarios (usuario, password_hash, estado)
VALUES (
    'admin',
    '$2a$12$RBNanHEZwH7MscWFlMsqzuLIAZ5jnUMPL/4b4k0kvMWX7K/mGHkMu',
    'ACTIVO'
) ON CONFLICT (usuario) DO NOTHING;

-- Asignar rol ADMINISTRADOR al usuario admin
INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuarios u
CROSS JOIN roles r
WHERE u.usuario = 'admin'
  AND r.nombre = 'ADMINISTRADOR'
ON CONFLICT DO NOTHING;

-- Usuario de prueba para RESIDENTE
INSERT INTO residentes (nombres, apellidos, cedula, telefono, estado)
VALUES ('Residente', 'De Prueba', '1700000001', '0999999999', 'ACTIVO')
ON CONFLICT (cedula) DO NOTHING;

-- Usuario residente1
-- Contraseña: Admin2024! (usamos el mismo hash por simplicidad)
INSERT INTO usuarios (id_residente, usuario, password_hash, estado)
SELECT id_residente, 'residente1', '$2a$12$RBNanHEZwH7MscWFlMsqzuLIAZ5jnUMPL/4b4k0kvMWX7K/mGHkMu', 'ACTIVO'
FROM residentes WHERE cedula = '1700000001'
ON CONFLICT (usuario) DO NOTHING;

-- Asignar rol RESIDENTE al usuario residente1
INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuarios u
CROSS JOIN roles r
WHERE u.usuario = 'residente1'
  AND r.nombre = 'RESIDENTE'
ON CONFLICT DO NOTHING;

-- ===================================================
-- ===================================================
CREATE INDEX IF NOT EXISTS idx_residente_cedula       ON residentes(cedula);
CREATE INDEX IF NOT EXISTS idx_residencia_codigo      ON residencias(codigo_casa);
CREATE INDEX IF NOT EXISTS idx_residencia_propietario ON residencias(id_propietario);
CREATE INDEX IF NOT EXISTS idx_cuota_residencia       ON cuotas(id_residencia);
CREATE INDEX IF NOT EXISTS idx_cuota_mes_anio         ON cuotas(mes, anio);
CREATE INDEX IF NOT EXISTS idx_pago_cuota             ON pagos(id_cuota);
CREATE INDEX IF NOT EXISTS idx_usuario_nombre         ON usuarios(usuario);
CREATE INDEX IF NOT EXISTS idx_comunicado_vencimiento ON comunicados(fecha_vencimiento);

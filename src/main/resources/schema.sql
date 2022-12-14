CREATE TABLE PERSONA
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    nombre             VARCHAR(255) NOT NULL,
    apellido           VARCHAR(255) NOT NULL,
    genero             VARCHAR(255) NOT NULL,
    identificacion     VARCHAR(127) NOT NULL UNIQUE ,
    direccion          VARCHAR(255) NOT NULL,
    telefono           VARCHAR(15)  NOT NULL,
    fecha_nacimiento   DATE         NOT NULL,
    creation_timestamp timestamp    NOT NULL,
    update_timestamp   timestamp,
    PRIMARY KEY (id)
);

CREATE INDEX PERSONA_IDX_1 ON PERSONA(nombre, apellido);
CREATE INDEX PERSONA_IDX_2 ON PERSONA(identificacion);

CREATE TABLE CLIENTE
(
    id                 BIGINT PRIMARY KEY NOT NULL,
--     persona_id          BIGINT       ,
    cliente_id          VARCHAR(255) NOT NULL UNIQUE,
    contrasena         VARCHAR(255) NOT NULL,
    estado             CHAR          NOT NULL,
    creation_timestamp timestamp    NOT NULL,
    update_timestamp   timestamp,
    FOREIGN KEY (id) REFERENCES PERSONA (id)
);

CREATE INDEX CLIENTE_IDX_1 ON CLIENTE(cliente_id);
CREATE INDEX CLIENTE_IDX_2 ON CLIENTE(estado, cliente_id);


CREATE TABLE CUENTA
(
    numero_cuenta                 BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    cliente_id         BIGINT             NOT NULL,
    tipo               CHAR               NOT NULL,
    saldo_inicial      NUMERIC(100000, 2) NOT NULL,
    estado             CHAR               NOT NULL,
    creation_timestamp timestamp          NOT NULL,
    update_timestamp   timestamp,
    PRIMARY KEY (numero_cuenta),
    FOREIGN KEY (numero_cuenta) REFERENCES CLIENTE (id)
);

CREATE INDEX CUENTA_IDX_1 ON CUENTA(cliente_id, estado);

CREATE TABLE MOVIMIENTO
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    cuenta_id         BIGINT             NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    estado CHAR NOT NULL,
    valor      NUMERIC(100000, 2) NOT NULL,
    saldo      NUMERIC(100000, 2) NOT NULL,
    descripcion VARCHAR(100),
    creation_timestamp timestamp          NOT NULL,
    update_timestamp   timestamp,
    PRIMARY KEY (id),
    FOREIGN KEY (cuenta_id) REFERENCES CUENTA(numero_cuenta)
);

CREATE INDEX MOVIMIENTO_IDX_1 ON MOVIMIENTO(cuenta_id, tipo);
CREATE INDEX MOVIMIENTO_IDX_2 ON MOVIMIENTO(creation_timestamp, cuenta_id);
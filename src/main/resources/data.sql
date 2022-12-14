INSERT INTO PERSONA(nombre, apellido, genero, identificacion, direccion, telefono, fecha_nacimiento, creation_timestamp,
                    update_timestamp)
VALUES ('Pedro', 'Santos', 'M', '11111111111', 'Casa #10, La Lotería Santiago de los Caballeros, República Domincana',
        '8091111111', DATE '1990-06-15', CURRENT_TIMESTAMP(),
        CURRENT_TIMESTAMP());

INSERT INTO CLIENTE(id, cliente_id, contrasena, estado, creation_timestamp, update_timestamp)
VALUES (1, 'pedrosantos', '1234', 'A', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO PERSONA(nombre, apellido, genero, identificacion, direccion, telefono, fecha_nacimiento, creation_timestamp,
                    update_timestamp)
VALUES ('Juan', 'Gonzalez', 'M', '11122211111', 'CASA', '8091112222', DATE '1997-02-27', CURRENT_TIMESTAMP(),
        CURRENT_TIMESTAMP());
INSERT INTO CLIENTE(id, cliente_id, contrasena, estado, creation_timestamp, update_timestamp)
VALUES (2, 'juangonzalez', '4567', 'A', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO PERSONA(nombre, apellido, genero, identificacion, direccion, telefono, fecha_nacimiento, creation_timestamp,
                    update_timestamp)
VALUES ('Ana', 'Lopez', 'F', '111222155555', 'CASA', '8093334444', DATE '1981-12-11', CURRENT_TIMESTAMP(),
        CURRENT_TIMESTAMP());
INSERT INTO CLIENTE(id, cliente_id, contrasena, estado, creation_timestamp, update_timestamp)
VALUES (3, 'analopez', '356777', 'A', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());


INSERT INTO CUENTA(cliente_id, tipo, saldo_inicial, estado, creation_timestamp, update_timestamp)
VALUES (2, 'C', 1000, 'A', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO CUENTA(cliente_id, tipo, saldo_inicial, estado, creation_timestamp, update_timestamp)
VALUES (2, 'A', 10000, 'A', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO MOVIMIENTO(cuenta_id, tipo, estado, valor, saldo, creation_timestamp, update_timestamp)
VALUES (2, 'DEPOSITO', 'C', 1000, 11000, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO MOVIMIENTO(cuenta_id, tipo, estado, valor, saldo, creation_timestamp, update_timestamp)
VALUES (2, 'DEPOSITO', 'C', 1000, 12000, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO MOVIMIENTO(cuenta_id, tipo, estado, valor, saldo, creation_timestamp, update_timestamp)
VALUES (2, 'DEPOSITO', 'C', 1000, 13000, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO MOVIMIENTO(cuenta_id, tipo, estado, valor, saldo, creation_timestamp, update_timestamp)
VALUES (1, 'RETIRO', 'C', -156, 844, {ts '2022-10-21 14:00:00'}, {ts '2022-10-21 14:00:00'});
INSERT INTO MOVIMIENTO(cuenta_id, tipo, estado, valor, saldo, creation_timestamp, update_timestamp)
VALUES (1, 'PAGO', 'P', -500, 344, {ts '2022-10-24 12:40:10'}, {ts '2022-10-24 12:40:10'});
INSERT INTO MOVIMIENTO(cuenta_id, tipo, estado, valor, saldo, creation_timestamp, update_timestamp)
VALUES (1, 'PAGO', 'C', -100, 344, {ts '2022-10-24 15:31:12'}, {ts '2022-10-24 15:31:12'});
INSERT INTO MOVIMIENTO(cuenta_id, tipo, estado, valor, saldo, creation_timestamp, update_timestamp)
VALUES (1, 'DEPOSITO', 'C', 1500, 1844, {ts '2022-10-25 09:00:00'}, {ts '2022-10-25 09:00:00'});
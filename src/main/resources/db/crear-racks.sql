-- ====================================================================
-- -CREACION DE TODOS LOS RACKS Y ESPACIOS
-- ====================================================================

USE almacen_db;

-- --- ALMACEN 1 con prefijo 30 (DINAMICOS)
SET @alm = 1;
CALL crear_rack_y_espacios(@alm, 1, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 2, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 3, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 4, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 5, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 6, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 7, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 8, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 9, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 10, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 11, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 12, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 13, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 14, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 15, '30', 'TUNEL', 1, 0);
CALL crear_rack_y_espacios(@alm, 16, '30', 'TUNEL', 1, 0);
CALL crear_rack_y_espacios(@alm, 17, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 18, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 19, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 20, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 21, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 22, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 23, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 24, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 25, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 26, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 27, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 28, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 29, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 30, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 31, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 32, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 33, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 34, '30', 'TUNEL', 1, 0);
CALL crear_rack_y_espacios(@alm, 35, '30', 'TUNEL', 1, 0);
CALL crear_rack_y_espacios(@alm, 36, '30', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 37, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 38, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 39, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 40, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 41, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 42, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 43, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 44, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 45, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 46, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 47, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 48, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 49, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 50, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 51, '30', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 52, '30', 'FIJO_ALM1', 0, 0);

-- --- ALMACEN 1 con prefijo 31 (FIJOS LADO IZQUIERDO)
SET @alm = 4;
CALL crear_rack_y_espacios(@alm, 3, '31', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 4, '31', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 5, '31', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 6, '31', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 7, '31', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 8, '31', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 11, '31', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 12, '31', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 13, '31', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 14, '31', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 15, '31', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 16, '31', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 17, '31', 'FIJO_ALM1', 0, 0);
CALL crear_rack_y_espacios(@alm, 18, '31', 'FIJO_ALM1', 0, 0);

-- --- ALMACEN 2 (prefijo 32)
SET @alm = 2;
CALL crear_rack_y_espacios(@alm, 1, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 2, '32', 'TUNEL', 1, 0);
CALL crear_rack_y_espacios(@alm, 3, '32', 'TUNEL', 1, 0);
CALL crear_rack_y_espacios(@alm, 4, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 5, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 6, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 7, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 8, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 9, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 10, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 11, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 12, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 13, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 14, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 15, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 16, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 17, '32', 'TUNEL', 1, 0);
CALL crear_rack_y_espacios(@alm, 18, '32', 'TUNEL', 1, 0);
CALL crear_rack_y_espacios(@alm, 19, '32', 'REVERSIBLE', 0, 0);
CALL crear_rack_y_espacios(@alm, 20, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 21, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 22, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 23, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 24, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 25, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 26, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 27, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 28, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 29, '32', 'REVERSIBLE', 0, 0);
CALL crear_rack_y_espacios(@alm, 30, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 31, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 32, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 33, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 34, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 35, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 36, '32', 'TUNEL', 1, 0);
CALL crear_rack_y_espacios(@alm, 37, '32', 'TUNEL', 1, 0);
CALL crear_rack_y_espacios(@alm, 38, '32', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 39, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 40, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 41, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 42, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 43, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 44, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 45, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 46, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 47, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 48, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 49, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 50, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 51, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 52, '32', 'FIJO_ALM2', 0, 0);
CALL crear_rack_y_espacios(@alm, 53, '32', 'FIJO_ALM2', 0, 0);

-- --- ALMACEN 3 (prefijo 33)
SET @alm = 3;
CALL crear_rack_y_espacios(@alm, 1, '33', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 2, '33', 'TUNEL', 1, 0);
CALL crear_rack_y_espacios(@alm, 3, '33', 'TUNEL', 1, 0);
CALL crear_rack_y_espacios(@alm, 4, '33', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 5, '33', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 6, '33', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 7, '33', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 8, '33', 'DINAMICO', 0, 0);
CALL crear_rack_y_espacios(@alm, 9, '33', 'DINAMICO', 0, 0);

-- --- PASILLOS (asociados a almacén 1)
SET @alm = 1;
CALL crear_rack_y_espacios(@alm, 1, 'P1', 'PISO', 0, 1);
CALL crear_rack_y_espacios(@alm, 2, 'P2', 'PISO', 0, 1);

-- ====================================================================
-- FIN DEL SCRIPT - TODOS LOS RACKS Y ESPACIOS CREADOS
-- ====================================================================
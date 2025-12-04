INSERT IGNORE INTO rol (tipo, descripcion) VALUES ('ACOMODADOR','Operador que guarda tarimas'), ('DESCANSERO','Operador que cubre ocasiones'), ('SURTIDOR','Operador que ejecuta picklist'), ('SUPERVISOR','Valida y autoriza acciones'), ('CALIDAD','Cambia estado de tarima'), ('ADMIN','Administrador sistema'), ('GERENTE','Acceso a reportes y auditorías');

INSERT IGNORE INTO tipo_rack (codigo, nombre, niveles, espacios_por_nivel, lados_por_rack, descripcion) VALUES ('DINAMICO', 'Dinamico FIFO', 5, 9, 1, 'Rack dinamico FIFO - 9 posiciones por nivel - 5 niveles'), ('TUNEL', 'Tunel FIFO', 3, 9, 1, 'Túnel - 3 niveles - 9 posiciones'), ('REVERSIBLE', 'Reversible FILO', 5, 8, 1, 'Reversible FILO - 8 posiciones por nivel'), ('FIJO_ALM1', 'Fijo Almacen 1', 5, 2, 2, 'Fijo (Almacen 1) - 2 espacios por lado'), ('FIJO_ALM2', 'Fijo Almacen 2', 5, 1, 2, 'Fijo (Almacen 2) - 1 espacio por lado'), ('PISO', 'Piso / Pasillo', 1, 30, 1, 'Pasillo / Espacio en piso - 30 posiciones');

INSERT IGNORE INTO almacen (numero, nombre, prefijo_codigo, activo) VALUES (1, 'ALMACEN 1', '30', 1), (2, 'ALMACEN 2', '32', 1), (3, 'ALMACEN 3', '33', 1), (4, 'ALMACEN 1', '31', 1);

INSERT IGNORE INTO estado_producto (codigo, surtido, descripcion) VALUES ('TERMINADO', 1, 'Producto Terminado'), ('CUARENTENA', 0, 'Producto en Cuarentena'), ('NO_ESTANDAR', 0, 'Producto No estándar'), ('MUESTRA', 0, 'Producto para muestras'), ('ESPECIAL', 0, 'Especial');

INSERT IGNORE INTO producto (codigo, nombre, abreviacion, piezas_por_caja, activo) VALUES ('242052', 'PAN BLANCO GRANDE', 'PBG', 100, 1), ('240123', 'TELERA', 'INT', 84, 1), ('244120', 'VIRGINIA', 'VIR', 98, 1), ('240124', 'PAN BLANCO CHICO', 'PBCH', 200, 1), ('244232', 'PAN INTEGRAL', 'PIN', 100, 1), ('240695', 'MINI BAGUETTE MULTIGRANO', 'MBM', 100, 1), ('240696', 'MINI BAGUETTE ARTESANAL', 'MBA', 100, 1), ('244018', 'BOLLITO', 'BLL', 288, 1);

INSERT IGNORE INTO insumo (codigo, descripcion) VALUES ('TAPE', 'Cinta adhesiva'), ('PLASTICO', 'Film plastico'), ('CARTON', 'Carton'), ('TINTA', 'Tinta de impresora');

INSERT IGNORE INTO maquina (nombre, codigo, insumo_id, activo) VALUES ('C300', 'M-C300', 1, 1);
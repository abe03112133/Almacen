DROP PROCEDURE IF EXISTS crear_rack_y_espacios;

CREATE PROCEDURE crear_rack_y_espacios(
  IN p_almacen_num INT,
  IN p_numero INT,
  IN p_prefijo_codigo VARCHAR(10),
  IN p_tipo_rack_codigo VARCHAR(50),
  IN p_es_tunel TINYINT,
  IN p_es_pasillo TINYINT
)
BEGIN
  DECLARE v_tipo_id INT;
  DECLARE v_almacen_id INT;
  DECLARE v_rack_id INT;
  DECLARE v_niveles INT;
  DECLARE v_espacios_por_nivel INT;
  DECLARE v_lados INT;
  DECLARE v_nivel INT;
  DECLARE v_lado INT;
  DECLARE v_pos INT;
  DECLARE v_codigo_qr VARCHAR(140);
  DECLARE v_num2 VARCHAR(4);
  DECLARE v_msg VARCHAR(255);

  SELECT id INTO v_almacen_id FROM almacen WHERE numero = p_almacen_num LIMIT 1;
  IF v_almacen_id IS NULL THEN
    SET v_msg = 'Almacen no encontrado';
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = v_msg;
  END IF;

  SELECT id, niveles, espacios_por_nivel, lados_por_rack INTO v_tipo_id, v_niveles, v_espacios_por_nivel, v_lados
  FROM tipo_rack WHERE codigo = p_tipo_rack_codigo LIMIT 1;

  IF v_tipo_id IS NULL THEN
    SET v_msg = 'Tipo rack no encontrado';
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = v_msg;
  END IF;

  IF p_es_pasillo = 1 THEN
    SET v_niveles = 1;
    SET v_espacios_por_nivel = 30;
    SET v_lados = 1;
  END IF;

  SET v_num2 = LPAD(p_numero,2,'0');
  INSERT INTO rack (almacen_id, numero, numero_dos_digitos, prefijo_codigo, tipo_rack_id, niveles_activos, espacios_por_nivel, lados_por_rack, es_tunel, es_pasillo, activo)
  VALUES (v_almacen_id, p_numero, v_num2, p_prefijo_codigo, v_tipo_id, v_niveles, v_espacios_por_nivel, v_lados, p_es_tunel, p_es_pasillo, 1);

  SET v_rack_id = LAST_INSERT_ID();

  IF p_tipo_rack_codigo = 'TUNEL' THEN
    SET v_nivel = 3;
    SET v_niveles = 5;
  ELSE
    SET v_nivel = 1;
  END IF;

  WHILE v_nivel <= v_niveles DO
    SET v_lado = 1;
    WHILE v_lado <= v_lados DO
      SET v_pos = 1;
      WHILE v_pos <= v_espacios_por_nivel DO
        IF p_es_pasillo = 1 THEN
          SET v_codigo_qr = CONCAT('PISO-P', p_numero, '-', LPAD(v_pos,2,'0'));
          INSERT INTO espacio (rack_id, nivel, lado, posicion, codigo_qr, estado)
            VALUES (v_rack_id, 0, 1, v_pos, v_codigo_qr, 'DISPONIBLE');
        ELSE
          SET v_codigo_qr = CONCAT('+C1PACON', p_prefijo_codigo, LPAD(p_numero,2,'0'), v_nivel, v_lado);
          INSERT INTO espacio (rack_id, nivel, lado, posicion, codigo_qr, estado)
            VALUES (v_rack_id, v_nivel, v_lado, v_pos, v_codigo_qr, 'DISPONIBLE');
        END IF;
        SET v_pos = v_pos + 1;
      END WHILE;
      SET v_lado = v_lado + 1;
    END WHILE;
    SET v_nivel = v_nivel + 1;
  END WHILE;

END;

-- Conectar como SYSTEM
WHENEVER SQLERROR EXIT SQL.SQLCODE

-- SP CREAR
CREATE OR REPLACE PROCEDURE SP_CREAR_EMPLEADO (
    p_nombre IN VARCHAR2, p_area IN VARCHAR2, p_edad IN NUMBER,
    p_correo IN VARCHAR2, p_sueldo IN NUMBER,
    p_id OUT NUMBER, p_mensaje OUT VARCHAR2
) AS
    v_existe NUMBER;
BEGIN
SELECT COUNT(*) INTO v_existe FROM EMPLEADOS WHERE CORREO_ELECTRONICO = p_correo;
IF v_existe > 0 THEN
        p_id := -1;
        p_mensaje := 'Ya existe un empleado con el correo: ' || p_correo;
        RETURN;
END IF;
INSERT INTO EMPLEADOS (ID, NOMBRE, AREA, EDAD, CORREO_ELECTRONICO, SUELDO)
VALUES (EMPLEADO_SEQ.NEXTVAL, p_nombre, p_area, p_edad, p_correo, p_sueldo)
    RETURNING ID INTO p_id;
p_mensaje := 'Empleado creado exitosamente';
COMMIT;
EXCEPTION WHEN OTHERS THEN ROLLBACK; p_id := -1; p_mensaje := 'Error: ' || SQLERRM;
END;
/

-- SP OBTENER POR ID
CREATE OR REPLACE PROCEDURE SP_OBTENER_EMPLEADO (p_id IN NUMBER, p_cursor OUT SYS_REFCURSOR) AS
BEGIN
OPEN p_cursor FOR SELECT ID, NOMBRE, AREA, EDAD, CORREO_ELECTRONICO, SUELDO FROM EMPLEADOS WHERE ID = p_id;
END;
/

-- SP OBTENER TODOS
CREATE OR REPLACE PROCEDURE SP_OBTENER_TODOS_EMPLEADOS (p_cursor OUT SYS_REFCURSOR) AS
BEGIN
OPEN p_cursor FOR SELECT ID, NOMBRE, AREA, EDAD, CORREO_ELECTRONICO, SUELDO FROM EMPLEADOS ORDER BY ID;
END;
/

-- SP ACTUALIZAR
CREATE OR REPLACE PROCEDURE SP_ACTUALIZAR_EMPLEADO (
    p_id IN NUMBER, p_nombre IN VARCHAR2, p_area IN VARCHAR2, p_edad IN NUMBER,
    p_correo IN VARCHAR2, p_sueldo IN NUMBER,
    p_filas_afectadas OUT NUMBER, p_mensaje OUT VARCHAR2
) AS
    v_existe NUMBER; v_correo_actual VARCHAR2(100);
BEGIN
SELECT COUNT(*), MAX(CORREO_ELECTRONICO) INTO v_existe, v_correo_actual FROM EMPLEADOS WHERE ID = p_id;
IF v_existe = 0 THEN p_filas_afectadas := 0; p_mensaje := 'Empleado no encontrado'; RETURN; END IF;
    IF v_correo_actual != p_correo THEN
SELECT COUNT(*) INTO v_existe FROM EMPLEADOS WHERE CORREO_ELECTRONICO = p_correo;
IF v_existe > 0 THEN p_filas_afectadas := -1; p_mensaje := 'Correo duplicado'; RETURN; END IF;
END IF;
UPDATE EMPLEADOS SET NOMBRE=p_nombre, AREA=p_area, EDAD=p_edad, CORREO_ELECTRONICO=p_correo, SUELDO=p_sueldo WHERE ID=p_id;
p_filas_afectadas := SQL%ROWCOUNT; p_mensaje := 'Actualizado'; COMMIT;
EXCEPTION WHEN OTHERS THEN ROLLBACK; p_filas_afectadas := -1; p_mensaje := SQLERRM;
END;
/

-- SP ELIMINAR
CREATE OR REPLACE PROCEDURE SP_ELIMINAR_EMPLEADO (p_id IN NUMBER, p_filas_afectadas OUT NUMBER, p_mensaje OUT VARCHAR2) AS
BEGIN
DELETE FROM EMPLEADOS WHERE ID = p_id;
p_filas_afectadas := SQL%ROWCOUNT;
    IF p_filas_afectadas = 0 THEN p_mensaje := 'No encontrado'; ELSE p_mensaje := 'Eliminado'; COMMIT; END IF;
EXCEPTION WHEN OTHERS THEN ROLLBACK; p_filas_afectadas := -1; p_mensaje := SQLERRM;
END;
/

-- Verificar creación
SELECT 'SPs creados exitosamente' AS mensaje FROM dual;
SELECT object_name, status FROM user_objects WHERE object_type = 'PROCEDURE' AND object_name LIKE 'SP_%';

EXIT;
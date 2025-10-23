package crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones;

import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;

public class EmpleadoNoEncontrado extends RuntimeException {
    public EmpleadoNoEncontrado(String mensaje) {
        super(mensaje);

    }
}

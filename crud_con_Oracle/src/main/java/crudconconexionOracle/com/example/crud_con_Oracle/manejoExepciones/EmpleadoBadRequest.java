package crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones;

public class EmpleadoBadRequest extends RuntimeException {
    public EmpleadoBadRequest(String mensaje) {
        super(mensaje);

    }

}

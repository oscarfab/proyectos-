package crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones;

public class DuplicateMailException extends RuntimeException {
    public DuplicateMailException(String mensaje) {
        super(mensaje);

    }

}

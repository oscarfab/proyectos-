package crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String mensaje) {
        super(mensaje);
    }
    public DatabaseException(String mensaje, Throwable cause) {
        super(mensaje, cause); // Mensaje + causa opcional
    }

}

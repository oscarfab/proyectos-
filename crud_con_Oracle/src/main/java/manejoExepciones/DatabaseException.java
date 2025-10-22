package manejoExepciones;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String mensaje ,Throwable cause) {
        super(mensaje, cause);

    }
}

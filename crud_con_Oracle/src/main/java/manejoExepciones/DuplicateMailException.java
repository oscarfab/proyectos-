package manejoExepciones;

public class DuplicateMailException extends RuntimeException {
    public DuplicateMailException(String mensaje) {
        super(mensaje);

    }

}

package manejoExepciones;

public class DuplicateMailExeption extends RuntimeException {
    public DuplicateMailExeption(String mensaje) {
        super(mensaje);

    }

}

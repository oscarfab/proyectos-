package manejoExepciones;

public class EmpleadoBadRequest extends RuntimeException {
    public EmpleadoBadRequest(String mensaje) {
        super(mensaje);

    }

}

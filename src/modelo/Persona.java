package modelo;

// Clase padre: Paciente y Cuidador comparten el nombre completo.
// abstract evita crear una Persona sin definir si es paciente o cuidador.
public abstract class Persona {
    private String nombreCompleto;

    public Persona(String nombreCompleto) {
        setNombreCompleto(nombreCompleto);
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    // Validamos el nombre antes de guardarlo.
    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombreCompleto = nombreCompleto.trim();
    }
}

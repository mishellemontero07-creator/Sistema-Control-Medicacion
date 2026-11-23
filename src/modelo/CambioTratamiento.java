package modelo;

import java.time.LocalDate;
import java.util.Objects;

// Guarda lo que el médico indicó sobre un esquema de dosificación después de una cita:
// el tratamiento sigue igual, se modificó o se suspendió.
// Cada cambio queda guardado como historial dentro del esquema.
public class CambioTratamiento {

    // ===== 1. ATRIBUTOS =====

    private EsquemaDosificacion esquema;
    private CitaMedica cita;
    private String estadoAnterior; // Estado del esquema antes del cambio: ACTIVO.
    private String estadoNuevo;    // Lo confirmado: SIN CAMBIOS, MODIFICADO o SUSPENDIDO.
    private String detalle;
    private LocalDate fechaAplicacion;


    // ===== 2. CONSTRUCTOR =====

    public CambioTratamiento(EsquemaDosificacion esquema, CitaMedica cita, String estadoAnterior,
                             String estadoNuevo, String detalle, LocalDate fechaAplicacion) {

        Objects.requireNonNull(esquema, "El esquema no puede ser nulo");
        Objects.requireNonNull(cita, "La cita no puede ser nula");
        Objects.requireNonNull(fechaAplicacion, "La fecha de aplicación no puede ser nula");

        if (estadoAnterior == null || estadoAnterior.trim().isEmpty()
                || estadoNuevo == null || estadoNuevo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar el estado anterior y el nuevo");
        }

        this.esquema = esquema;
        this.cita = cita;
        this.estadoAnterior = estadoAnterior.trim();
        this.estadoNuevo = estadoNuevo.trim();
        this.detalle = detalle == null ? "" : detalle.trim();
        this.fechaAplicacion = fechaAplicacion;
    }


    // ===== 3. GETTERS =====

    public EsquemaDosificacion getEsquema() {
        return esquema;
    }

    public CitaMedica getCita() {
        return cita;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getDetalle() {
        return detalle;
    }

    public LocalDate getFechaAplicacion() {
        return fechaAplicacion;
    }


    // ===== 4. RESUMEN =====

    // Una línea lista para mostrar en pantalla o en el reporte.
    public String resumen() {

        Medicamento medicamento = esquema.getMedicamento();
        String nombre = medicamento == null ? "(sin medicamento)" : medicamento.getNombre();

        return fechaAplicacion + " - " + nombre + ": " + estadoAnterior + " -> " + estadoNuevo
                + (detalle.isEmpty() ? "" : ". " + detalle);
    }

    @Override
    public String toString() {
        return resumen();
    }
}
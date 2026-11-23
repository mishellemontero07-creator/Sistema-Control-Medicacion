package modelo;

import java.time.LocalDate;

// Representa la relación entre UN paciente y UN cuidador.
// Si cambia el responsable, cerramos el vínculo anterior, sin borrarlo del historial.
public class VinculoCuidador {
    private Paciente paciente;
    private Cuidador cuidador;
    private String tipo;       // "principal" o "secundario"
    private String parentesco;
    private LocalDate fechaInicio;
    private LocalDate fechaFin; // null significa que todavía está activo.

    public VinculoCuidador(Paciente paciente, Cuidador cuidador, String tipo,
                           String parentesco, LocalDate fechaInicio) {
        if (paciente == null || cuidador == null) {
            throw new IllegalArgumentException("Debe indicar paciente y cuidador");
        }
        if (tipo == null || (!tipo.equalsIgnoreCase("principal")
                && !tipo.equalsIgnoreCase("secundario"))) {
            throw new IllegalArgumentException("El tipo debe ser principal o secundario");
        }
        if (parentesco == null || parentesco.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar el parentesco o relación");
        }
        if (fechaInicio == null || fechaInicio.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no es válida");
        }
        this.paciente = paciente;
        this.cuidador = cuidador;
        this.tipo = tipo.toLowerCase();
        this.parentesco = parentesco.trim();
        this.fechaInicio = fechaInicio;
        this.fechaFin = null;
    }

    public Paciente getPaciente() { return paciente; }
    public Cuidador getCuidador() { return cuidador; }
    public String getTipo() { return tipo; }
    public String getParentesco() { return parentesco; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }

    public boolean esPrincipal() {
        return tipo.equals("principal");
    }

    public boolean estaActivo() {
        return fechaFin == null;
    }

    // Cerramos el vínculo, pero no lo eliminamos de las listas.
    public void cerrar(LocalDate fecha) {
        if (fecha == null || fecha.isBefore(fechaInicio) || fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de cierre no es válida");
        }
        if (!estaActivo()) {
            throw new IllegalStateException("Este vínculo ya fue cerrado");
        }
        this.fechaFin = fecha;
    }

    // La fecha de fin se toma como EXCLUSIVA: ese día el vínculo ya terminó.
    public boolean estabaActivoEn(LocalDate fecha) {
        return fecha != null
                && !fecha.isBefore(fechaInicio)
                && (fechaFin == null || fecha.isBefore(fechaFin));
    }
}



package modelo;

import java.time.LocalDateTime;
import java.util.Objects;

public class Toma {

    private EsquemaDosificacion esquema;
    private LocalDateTime fechaHoraProgramada;
    private LocalDateTime fechaHoraLimite;
    private LocalDateTime fechaHoraReal;
    private String estado;
    private String confirmadaPor;
    private String observaciones;

    // Constructor
    public Toma(EsquemaDosificacion esquema,
                LocalDateTime fechaHoraProgramada,
                LocalDateTime fechaHoraLimite) {

        Objects.requireNonNull(esquema, "El esquema no puede ser nulo");
        Objects.requireNonNull(fechaHoraProgramada,
                "La fecha programada no puede ser nula");
        Objects.requireNonNull(fechaHoraLimite,
                "La fecha límite no puede ser nula");

        if (fechaHoraLimite.isBefore(fechaHoraProgramada)) {
            throw new IllegalArgumentException(
                    "La fecha límite no puede ser anterior a la fecha programada");
        }

        this.esquema = esquema;
        this.fechaHoraProgramada = fechaHoraProgramada;
        this.fechaHoraLimite = fechaHoraLimite;

        this.fechaHoraReal = null;
        this.estado = "PENDIENTE";
        this.confirmadaPor = "";
        this.observaciones = "";
    }

    // Getters
    public EsquemaDosificacion getEsquema() {
        return esquema;
    }

    public LocalDateTime getFechaHoraProgramada() {
        return fechaHoraProgramada;
    }

    public LocalDateTime getFechaHoraLimite() {
        return fechaHoraLimite;
    }

    public LocalDateTime getFechaHoraReal() {
        return fechaHoraReal;
    }

    public String getEstado() {
        return estado;
    }

    public String getConfirmadaPor() {
        return confirmadaPor;
    }

    public String getObservaciones() {
        return observaciones;
    }

    // Confirmar una toma
    public boolean confirmar(LocalDateTime hora,
                             String quien,
                             String observaciones) {

        if (fechaHoraReal != null) {
            return false;
        }

        if (estado.equals("OMITIDA") || estado.equals("CANCELADA")) {
            return false;
        }

        if (hora == null || quien == null || quien.trim().isEmpty()) {
            return false;
        }

        if (hora.isAfter(LocalDateTime.now())) {
            return false;
        }

        if (hora.isBefore(fechaHoraProgramada.minusMinutes(30))) {
            return false;
        }

        fechaHoraReal = hora;
        confirmadaPor = quien.trim();

        this.observaciones = observaciones == null
                ? ""
                : observaciones.trim();

        if (hora.isAfter(fechaHoraLimite)) {
            estado = "TARDÍA";
        } else {
            estado = "A TIEMPO";
        }

        return true;
    }

    // Omitir una toma
    public void omitir() {

        if (fechaHoraReal == null && !estado.equals("CANCELADA")) {
            estado = "OMITIDA";
        }
    }

    // Cancelar una toma
    public void cancelar() {

        if (estado.equals("PENDIENTE")) {
            estado = "CANCELADA";
        }
    }

    // Clasificar una toma
    public String clasificar() {

        if (fechaHoraReal != null) {

            if (fechaHoraReal.isAfter(fechaHoraLimite)) {
                estado = "TARDÍA";
            } else {
                estado = "A TIEMPO";
            }
        }

        return estado;
    }

    // Comprobar si una toma está vencida
    public boolean estaVencida(LocalDateTime ahora) {

        if (ahora == null) {
            return false;
        }

        return fechaHoraReal == null
                && !estado.equals("CANCELADA")
                && !estado.equals("OMITIDA")
                && ahora.isAfter(fechaHoraLimite);
    }
}

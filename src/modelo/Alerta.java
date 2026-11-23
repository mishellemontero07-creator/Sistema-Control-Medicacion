
package modelo;

import java.time.LocalDateTime;

public class Alerta {

    private Paciente paciente;
    private Cuidador cuidadorDestinatario;
    private Toma tomaAsociada;
    private String tipo;
    private String mensajeCuidador;
    private String mensajeAdultoMayor;
    private String prioridad;
    private LocalDateTime fechaGeneracion;
    private String estado;
    private LocalDateTime fechaAtencion;


    // Constructor para registrar una alerta relacionada con una toma.
    public Alerta(Paciente paciente, Toma tomaAsociada,
                  String tipo, String mensajeCuidador, String prioridad) {

        this.paciente = paciente;
        this.tomaAsociada = tomaAsociada;
        this.tipo = tipo;
        this.mensajeCuidador = mensajeCuidador;
        this.prioridad = prioridad;

        this.fechaGeneracion = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }


    // Devuelve la fecha en la que se genero la alerta.
    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    // Comprueba si la alerta sigue pendiente.
    public boolean estaPendiente() {
        return "PENDIENTE".equalsIgnoreCase(estado);
    }


}


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

}

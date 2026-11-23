
package modelo;

import java.time.LocalDate;
import java.util.ArrayList;

public class AdvertenciaFarmacologica {

    private Paciente paciente;
    private Medicamento medicamento;
    private String tipo;
    private String descripcion;
    private LocalDate fechaEvaluacion;
    private boolean revisada;

    // Constructor
    public AdvertenciaFarmacologica(Paciente paciente, Medicamento medicamento,
                                    String tipo, String descripcion) {

        // IllegalArgumentException indica que un dato recibido no es valido.
        if (paciente == null || medicamento == null) {
            throw new IllegalArgumentException(
                    "El paciente y el medicamento son obligatorios.");
        }

        if (tipo == null || tipo.trim().isEmpty()
                || descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El tipo y la descripcion son obligatorios.");
        }

        this.paciente = paciente;
        this.medicamento = medicamento;
        this.tipo = tipo.trim();
        this.descripcion = descripcion.trim();
        this.fechaEvaluacion = LocalDate.now();
        this.revisada = false;
    }

    // Evalua si falta informacion del medicamento.
    public static ArrayList<AdvertenciaFarmacologica> evaluar(
            Paciente paciente, Medicamento medicamento) {

        if (paciente == null || medicamento == null) {
            // Evita evaluar sin los objetos necesarios.
            throw new IllegalArgumentException(
                    "Debe indicar un paciente y un medicamento.");
        }

        ArrayList<AdvertenciaFarmacologica> advertencias = new ArrayList<>();

        String enfermedad = medicamento.getEnfermedadTratada();

        if (enfermedad == null || enfermedad.trim().isEmpty()) {
            advertencias.add(new AdvertenciaFarmacologica(
                    paciente,
                    medicamento,
                    "INFORMACION INCOMPLETA",
                    "No se ha registrado la enfermedad tratada de este medicamento."
            ));
        }

        String indicacion = medicamento.getIndicacion();

        if (indicacion == null || indicacion.trim().isEmpty()) {
            advertencias.add(new AdvertenciaFarmacologica(
                    paciente,
                    medicamento,
                    "INFORMACION INCOMPLETA",
                    "No se ha registrado la indicacion de este medicamento."
            ));
        }

        return advertencias;
    }

    // Marca la advertencia como revisada.
    public void marcarRevisada() {
        this.revisada = true;
    }
}

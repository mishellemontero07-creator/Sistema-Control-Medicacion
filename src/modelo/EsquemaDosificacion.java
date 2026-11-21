
package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class EsquemaDosificacion {

    private Paciente paciente;
    private Medicamento medicamento;
    private double cantidadPorToma;
    private String unidadDosis;

    private ArrayList<LocalTime> horarios = new ArrayList<>();

    private LocalDate fechaInicio;
    private int duracionDias;
    private int toleranciaMinutos;
    private String estado;

    private ArrayList<Toma> tomas = new ArrayList<>();
    private ArrayList<CambioTratamiento> historialCambios = new ArrayList<>();

}


package modelo;

import java.time.LocalDate;
import java.util.ArrayList;

public class ReporteSeguimiento {

    private Paciente paciente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private ArrayList<Medicamento> medicamentosAnalizados = new ArrayList<>();

    private int tomasExigibles;
    private int tomasATiempo;
    private int tomasTardias;
    private int tomasOmitidas;
    private int tomasPendientes;
    private double porcentajeAdherencia;
    private String nivelAdherencia;


    // Devuelve la fecha inicial del reporte.
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    // Devuelve la fecha final del reporte.
    public LocalDate getFechaFin() {
        return fechaFin;
    }

}

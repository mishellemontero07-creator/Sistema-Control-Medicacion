
package modelo;

import java.util.ArrayList;

public class Medicamento {

    private String nombre;
    private String presentacion;
    private double concentracion;
    private String unidadMedida;
    private String enfermedadTratada;
    private double cantidadInicial;
    private double cantidadActual;
    private int diasMinimosReserva;

    private ArrayList<Double> reposiciones = new ArrayList<>();
    private Paciente paciente;

}

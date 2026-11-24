
package modelo;

import java.util.ArrayList;
import java.time.LocalDate;

public class Medicamento {

    private String nombre;
    private String presentacion;
    private double concentracion;
    private String unidadMedida;
    private String enfermedadTratada;
    private String indicacion;
    private double cantidadInicial;
    private double cantidadActual;
    private int diasMinimosReserva;

    private ArrayList<Double> reposiciones = new ArrayList<>();
    private Paciente paciente;

    public Medicamento(String nombre, String presentacion, double concentracion, String unidadMedida,
                       String enfermedadTratada, String indicacion, double cantidadInicial, int diasMinimosReserva,
                       Paciente paciente) {
        this.nombre = nombre;
        this.presentacion = presentacion;
        this.concentracion = concentracion;
        this.unidadMedida = unidadMedida;
        this.enfermedadTratada = enfermedadTratada;
        this.indicacion = indicacion;
        this.cantidadInicial = cantidadInicial;
        // Ya que cuando registramos inicialmente un medicamento, su cantidad disponible debe
        // comenzar con la cantidad que ingresamos.
        this.cantidadActual = cantidadInicial;
        this.diasMinimosReserva = diasMinimosReserva;
        this.paciente = paciente;
    }

    public String getEnfermedadTratada() {
        return enfermedadTratada;
    }

    public String getIndicacion() {
        return indicacion;
    }

    public boolean descontar(double cantidad) {

        if (cantidad <= 0 || cantidad > cantidadActual || Double.isNaN(cantidad)){
            return false;
        }
        this.cantidadActual -= cantidad;
        return true;
    }

    public void reponer(double cantidad) {

        if (cantidad > 0 && !Double.isNaN(cantidad)) {

            this.cantidadActual += cantidad;
            this.reposiciones.add(cantidad);
        }
    }

    public ArrayList<Double> obtenerReposiciones() {
        return new ArrayList<>(reposiciones);
    }

    public boolean correspondeAEnfermedad(String enfermedad) {

        if (enfermedad == null || enfermedadTratada == null) {
            return false;
        }

        return this.enfermedadTratada.equalsIgnoreCase(enfermedad.trim());
    }

    public ArrayList<AdvertenciaFarmacologica> obtenerAdvertencias() {

        if (paciente == null) {
            return new ArrayList<>();
        }

        return AdvertenciaFarmacologica.evaluar(paciente, this);
    }

    public int calcularDiasEstimados(double consumoDiario) {

        if (consumoDiario <= 0 || Double.isNaN(consumoDiario)) {
            return 0;
        }

        if (cantidadActual <= 0) {
            return 0;
        }

        return (int) (cantidadActual / consumoDiario);
    }

    public LocalDate calcularFechaAgotamiento(double consumoDiario) {

        if (consumoDiario <= 0 || Double.isNaN(consumoDiario)) {
            return null;
        }

        int dias = calcularDiasEstimados(consumoDiario);

        return LocalDate.now().plusDays(dias);
    }

    public String obtenerEstado(double consumoDiario) {

        if (consumoDiario <= 0 || Double.isNaN(consumoDiario)) {
            return "CONSUMO INVALIDO";
        }

        if (cantidadActual <= 0) {
            return "AGOTADO";
        }

        int dias = calcularDiasEstimados(consumoDiario);

        if (dias <= diasMinimosReserva) {
            return "RESERVA BAJA";
        }

        return "SUFICIENTE";
    }


    // Devuelve el nombre del medicamento.
    public String getNombre() {
        return nombre;
    }

    // Permite descontar una cantidad desde el modulo de tomas.
    public boolean descontarCantidad(double cantidad) {
        return descontar(cantidad);
    }

    // Devuelve la cantidad de medicamento que queda disponible.
    public double getCantidadActual() {
        return cantidadActual;
    }

}

package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

// Un esquema de dosificación indica cómo debe tomar un paciente un medicamento:
// cantidad por toma, horarios, fecha de inicio y duración.
// También guarda sus tomas y el historial de cambios que indica el médico.
public class EsquemaDosificacion {

    // ===== 1. ATRIBUTOS =====

    private Paciente paciente;
    private Medicamento medicamento;
    private double cantidadPorToma;
    private String unidadDosis;
    private ArrayList<LocalTime> horarios;
    private LocalDate fechaInicio;
    private int duracionDias;
    private int toleranciaMinutos;
    private String estado; // ACTIVO, SUSPENDIDO o FINALIZADO

    // Composición: las tomas y los cambios pertenecen a este esquema.
    private ArrayList<Toma> tomas = new ArrayList<>();
    private ArrayList<CambioTratamiento> historialCambios = new ArrayList<>();


    // ===== 2. CONSTRUCTOR =====

    // No lanza errores: los datos se revisan con validar() antes de activar el esquema (RF7).
    public EsquemaDosificacion(Paciente paciente, Medicamento medicamento, double cantidadPorToma,
                               String unidadDosis, ArrayList<LocalTime> horarios,
                               LocalDate fechaInicio, int duracionDias, int toleranciaMinutos) {

        this.paciente = paciente;
        this.medicamento = medicamento;
        this.cantidadPorToma = cantidadPorToma;
        this.unidadDosis = unidadDosis;
        this.horarios = limpiarHorarios(horarios);
        this.fechaInicio = fechaInicio;
        this.duracionDias = duracionDias;
        this.toleranciaMinutos = toleranciaMinutos;
        this.estado = "ACTIVO";
    }


    // ===== 3. GETTERS =====
    // Paciente y Toma necesitan getMedicamento(), getTomas(), getPaciente(),
    // getCantidadPorToma() y getToleranciaMinutos().

    public Paciente getPaciente() {
        return paciente;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public double getCantidadPorToma() {
        return cantidadPorToma;
    }

    public String getUnidadDosis() {
        return unidadDosis;
    }

    // Devolvemos una copia para que nadie cambie los horarios sin pasar por modificar().
    public ArrayList<LocalTime> getHorarios() {
        return new ArrayList<>(horarios);
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public int getToleranciaMinutos() {
        return toleranciaMinutos;
    }

    public ArrayList<Toma> getTomas() {
        return new ArrayList<>(tomas);
    }

    public ArrayList<CambioTratamiento> getHistorialCambios() {
        return new ArrayList<>(historialCambios);
    }

    // Si ya pasó la fecha de fin y el esquema seguía activo, pasa a FINALIZADO.
    public String getEstado() {

        LocalDate fin = getFechaFin();

        if (estado.equals("ACTIVO") && fin != null && LocalDate.now().isAfter(fin)) {
            estado = "FINALIZADO";
        }

        return estado;
    }

    public boolean estaActivo() {
        return getEstado().equals("ACTIVO");
    }


    // ===== 4. VALIDACIÓN Y CÁLCULOS (RF7) =====

    public boolean validar() {

        if (paciente == null || medicamento == null) {
            return false;
        }

        if (cantidadPorToma <= 0 || Double.isNaN(cantidadPorToma)) {
            return false;
        }

        if (unidadDosis == null || unidadDosis.trim().isEmpty()) {
            return false;
        }

        if (horarios.isEmpty() || fechaInicio == null || duracionDias <= 0) {
            return false;
        }

        // RF8: la tolerancia configurable va de 5 a 10 minutos.
        return toleranciaMinutos >= 5 && toleranciaMinutos <= 10;
    }

    // Cantidad que se consume en un día. Medicamento la usa para estimar cuánto le queda.
    public double calcularConsumoDiario() {
        return cantidadPorToma * horarios.size();
    }

    // El día de inicio cuenta: 30 días desde el 1 de diciembre terminan el 30 de diciembre.
    public LocalDate getFechaFin() {

        if (fechaInicio == null || duracionDias <= 0) {
            return null;
        }

        return fechaInicio.plusDays(duracionDias - 1);
    }


    // ===== 5. CALENDARIO Y TOMAS (RF8) =====

    public void generarCalendario() {

        if (!validar() || !estaActivo()) {
            return;
        }

        generarTomasDesde(fechaInicio);
    }

    // Crea las tomas de cada día, desde la fecha indicada hasta el fin del tratamiento.
    // El constructor de Toma ya la registra en este esquema (llama a agregarToma),
    // por eso aquí no se vuelve a agregar.
    private void generarTomasDesde(LocalDate desde) {

        LocalDate dia = desde;
        LocalDate fin = getFechaFin();

        while (!dia.isAfter(fin)) {

            for (LocalTime hora : horarios) {

                LocalDateTime programada = LocalDateTime.of(dia, hora);

                // Evita duplicar una toma que ya existe a esa hora.
                if (buscarToma(programada) == null) {
                    new Toma(this, programada, programada.plusMinutes(toleranciaMinutos));
                }
            }

            dia = dia.plusDays(1);
        }
    }

    public boolean agregarToma(Toma toma) {

        if (toma == null || toma.getEsquema() != this || tomas.contains(toma)) {
            return false;
        }

        tomas.add(toma);

        return true;
    }

    // Busca la toma programada a esa hora, sin contar las canceladas.
    public Toma buscarToma(LocalDateTime fechaHora) {

        if (fechaHora == null) {
            return null;
        }

        for (Toma toma : tomas) {

            if (!toma.getEstado().equals("CANCELADA")
                    && toma.getFechaHoraProgramada().equals(fechaHora)) {

                return toma;
            }
        }

        return null;
    }

    // Cancela las tomas pendientes desde esa fecha y hora. Las ya confirmadas no se tocan.
    public void cancelarTomasFuturas(LocalDateTime desde) {

        if (desde == null) {
            return;
        }

        for (Toma toma : tomas) {

            if (!toma.getFechaHoraProgramada().isBefore(desde)) {
                toma.cancelar(); // Toma solo cancela las que están PENDIENTE.
            }
        }
    }


    // ===== 6. CAMBIOS DESPUÉS DE UNA CONSULTA (RF9) =====

    public boolean registrarCambio(CambioTratamiento cambio) {

        if (cambio == null || cambio.getEsquema() != this || historialCambios.contains(cambio)) {
            return false;
        }

        historialCambios.add(cambio);

        return true;
    }

    public ArrayList<CambioTratamiento> buscarCambiosPorFecha(LocalDate fecha) {

        ArrayList<CambioTratamiento> resultado = new ArrayList<>();

        if (fecha == null) {
            return resultado;
        }

        for (CambioTratamiento cambio : historialCambios) {

            if (cambio.getFechaAplicacion().equals(fecha)) {
                resultado.add(cambio);
            }
        }

        return resultado;
    }

    // Nueva dosis u horarios según la consulta. Las tomas pendientes desde fechaAplicacion
    // se cancelan y se crean otras con la indicación nueva.
    public boolean modificar(double nuevaCantidad, ArrayList<LocalTime> nuevosHorarios,
                             LocalDate fechaAplicacion, CitaMedica cita) {

        ArrayList<LocalTime> horariosNuevos = limpiarHorarios(nuevosHorarios);

        if (!validar() || !estaActivo() || cita == null || fechaAplicacion == null) {
            return false;
        }

        if (nuevaCantidad <= 0 || Double.isNaN(nuevaCantidad) || horariosNuevos.isEmpty()) {
            return false;
        }

        // El cambio debe aplicarse dentro de la duración del tratamiento.
        if (fechaAplicacion.isBefore(fechaInicio) || fechaAplicacion.isAfter(getFechaFin())) {
            return false;
        }

        String detalle = "Antes: " + describirDosis(cantidadPorToma, horarios)
                + " | Ahora: " + describirDosis(nuevaCantidad, horariosNuevos);

        cancelarTomasFuturas(fechaAplicacion.atStartOfDay());

        this.cantidadPorToma = nuevaCantidad;
        this.horarios = horariosNuevos;

        generarTomasDesde(fechaAplicacion);

        return registrarCambio(
                new CambioTratamiento(this, cita, "ACTIVO", "MODIFICADO", detalle, fechaAplicacion)
        );
    }

    // Se cancelan las tomas futuras y el esquema queda SUSPENDIDO.
    public boolean suspender(LocalDate fechaAplicacion, CitaMedica cita) {

        if (!estaActivo() || cita == null || fechaAplicacion == null) {
            return false;
        }

        cancelarTomasFuturas(fechaAplicacion.atStartOfDay());

        estado = "SUSPENDIDO";

        return registrarCambio(
                new CambioTratamiento(this, cita, "ACTIVO", "SUSPENDIDO",
                        "Tratamiento suspendido por indicación médica", fechaAplicacion)
        );
    }


    // ===== 7. MÉTODOS AUXILIARES =====

    // Copia los horarios sin repetidos ni vacíos y los deja en orden.
    private ArrayList<LocalTime> limpiarHorarios(ArrayList<LocalTime> originales) {

        ArrayList<LocalTime> limpios = new ArrayList<>();

        if (originales == null) {
            return limpios;
        }

        for (LocalTime hora : originales) {

            if (hora != null && !limpios.contains(hora)) {
                limpios.add(hora);
            }
        }

        Collections.sort(limpios);

        return limpios;
    }

    private String describirDosis(double cantidad, ArrayList<LocalTime> horas) {
        return cantidad + " " + unidadDosis + " a las " + horas;
    }

    @Override
    public String toString() {

        String nombre = medicamento == null ? "(sin medicamento)" : medicamento.getNombre();

        return nombre + ": " + describirDosis(cantidadPorToma, horarios)
                + " durante " + duracionDias + " días (" + getEstado() + ")";
    }
}
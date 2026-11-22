
package modelo;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.util.ArrayList;

// Paciente hereda el nombre completo de Persona.
// Mantiene sus datos de salud y las relaciones con los otros módulos.
public class Paciente extends Persona {

    // ===== 1. ATRIBUTOS =====

    private String cedula;
    private LocalDate fechaNacimiento;
    private String enfermedadesCronicas;
    private String alergias;
    private String condicionesRelevantes;
    private boolean activo;

    // Una lista por cada tipo de información asociada al paciente.
    private ArrayList<VinculoCuidador> vinculos = new ArrayList<>();
    private ArrayList<Medicamento> medicamentos = new ArrayList<>();
    private ArrayList<EsquemaDosificacion> esquemas = new ArrayList<>();
    private ArrayList<CitaMedica> citas = new ArrayList<>();
    private ArrayList<Alerta> alertas = new ArrayList<>();
    private ArrayList<AdvertenciaFarmacologica> advertencias = new ArrayList<>();
    private ArrayList<ReporteSeguimiento> reportes = new ArrayList<>();


    // ===== 2. CONSTRUCTOR =====

    public Paciente(String nombreCompleto, String cedula, LocalDate fechaNacimiento) {
        super(nombreCompleto); // Llama al constructor de Persona.
        setCedula(cedula);
        setFechaNacimiento(fechaNacimiento);
        this.activo = true;
    }


    // ===== 3. CÉDULA =====

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        if (!validarCedulaIngresada(cedula)) {
            throw new IllegalArgumentException("La cédula ecuatoriana no es válida");
        }
        this.cedula = cedula;
    }

    public boolean validarCedula() {
        return validarCedulaIngresada(cedula);
    }

    // static permite validar antes de construir el paciente en Main.
    // Es la validación por módulo 10 para cédulas con tercer dígito de 0 a 5.
    public static boolean validarCedulaIngresada(String nuevaCedula) {

        if (nuevaCedula == null || !nuevaCedula.matches("[0-9]{10}")) {
            return false;
        }

        int provincia = Integer.parseInt(nuevaCedula.substring(0, 2));

        if (provincia < 1 || (provincia > 24 && provincia != 30)) {
            return false;
        }

        int tercerDigito = Integer.parseInt(nuevaCedula.substring(2, 3));

        if (tercerDigito > 5) {
            return false;
        }

        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;

        for (int i = 0; i < coeficientes.length; i++) {

            int digito = Integer.parseInt(nuevaCedula.substring(i, i + 1));
            int producto = digito * coeficientes[i];

            if (producto >= 10) {
                producto -= 9;
            }

            suma += producto;
        }

        int residuo = suma % 10;
        int esperado = (residuo == 0) ? 0 : 10 - residuo;
        int ingresado = Integer.parseInt(nuevaCedula.substring(9, 10));

        return esperado == ingresado;
    }


    // ===== 4. FECHA DE NACIMIENTO =====

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {

        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura ni estar vacía");
        }

        this.fechaNacimiento = fechaNacimiento;
    }

    // Valida el texto ANTES de llamar a LocalDate.parse(), sin try-catch.
    // También comprueba los días reales de cada mes, incluidos años bisiestos.
    public static boolean validarFechaTexto(String texto) {

        if (texto == null || !texto.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            return false;
        }

        int anio = Integer.parseInt(texto.substring(0, 4));
        int mes = Integer.parseInt(texto.substring(5, 7));
        int dia = Integer.parseInt(texto.substring(8, 10));

        if (anio < 1 || mes < 1 || mes > 12 || dia < 1) {
            return false;
        }

        if (dia > YearMonth.of(anio, mes).lengthOfMonth()) {
            return false;
        }

        return !LocalDate.of(anio, mes, dia).isAfter(LocalDate.now());
    }

    public boolean validarFechaNacimiento() {
        return fechaNacimiento != null && !fechaNacimiento.isAfter(LocalDate.now());
    }

    public int calcularEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }


    // ===== 5. DATOS MÉDICOS Y ESTADO =====

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getEnfermedadesCronicas() {
        return enfermedadesCronicas;
    }

    public void setEnfermedadesCronicas(String enfermedadesCronicas) {
        this.enfermedadesCronicas = comprobarTexto(enfermedadesCronicas, "enfermedades crónicas");
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = comprobarTexto(alergias, "alergias");
    }

    public String getCondicionesRelevantes() {
        return condicionesRelevantes;
    }

    public void setCondicionesRelevantes(String condicionesRelevantes) {
        this.condicionesRelevantes = comprobarTexto(condicionesRelevantes, "condiciones relevantes");
    }

    // Evitamos repetir el mismo if en los tres setters anteriores.
    private String comprobarTexto(String texto, String campo) {

        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar " + campo + " o escribir 'Ninguna'");
        }

        return texto.trim();
    }


    // ===== 6. VÍNCULOS CON CUIDADORES =====

    // Registra el mismo vínculo en Paciente y en Cuidador.
    // No permite dos vínculos activos con el mismo cuidador ni dos principales.
    public boolean vincularCuidador(Cuidador cuidador, String tipo, String parentesco) {

        if (cuidador == null || tipo == null || parentesco == null || parentesco.trim().isEmpty()) {
            return false;
        }

        if (!tipo.equalsIgnoreCase("principal") && !tipo.equalsIgnoreCase("secundario")) {
            return false;
        }

        if (buscarVinculoActivo(cuidador) != null) {
            return false;
        }

        if (tipo.equalsIgnoreCase("principal") && getCuidadorPrincipal() != null) {
            return false;
        }

        VinculoCuidador nuevo = new VinculoCuidador(this, cuidador, tipo, parentesco, LocalDate.now());

        return registrarEnAmbos(nuevo);
    }

    private boolean registrarEnAmbos(VinculoCuidador nuevo) {

        if (!agregarVinculo(nuevo)) {
            return false;
        }

        if (!nuevo.getCuidador().agregarVinculo(nuevo)) {
            vinculos.remove(nuevo); // Evitamos una relación guardada solo en un lado.
            return false;
        }

        return true;
    }

    private VinculoCuidador buscarVinculoActivo(Cuidador cuidador) {

        for (VinculoCuidador vinculo : vinculos) {

            if (vinculo.getCuidador() == cuidador && vinculo.estaActivo()) {
                return vinculo;
            }
        }

        return null;
    }

    // Cambiamos el principal sin borrar el historial:
    // el principal anterior pasa a secundario y el secundario elegido pasa a principal.
    // El nuevo principal DEBE estar vinculado antes como secundario.
    public boolean cambiarCuidadorPrincipal(Cuidador nuevo) {

        if (nuevo == null) {
            return false;
        }

        VinculoCuidador vinculoNuevo = buscarVinculoActivo(nuevo);

        if (vinculoNuevo == null) {
            return false;
        }

        VinculoCuidador anterior = buscarVinculoPrincipalActivo();

        if (anterior != null && anterior.getCuidador() == nuevo) {
            return true; // Ya era el responsable principal.
        }

        String parentescoNuevo = vinculoNuevo.getParentesco();
        String parentescoAnterior = anterior == null ? null : anterior.getParentesco();
        Cuidador cuidadorAnterior = anterior == null ? null : anterior.getCuidador();

        LocalDate hoy = LocalDate.now();

        // Cerramos los dos registros activos; quedan guardados como historial.
        vinculoNuevo.cerrar(hoy);

        if (anterior != null) {
            anterior.cerrar(hoy);
        }

        boolean cambio = registrarEnAmbos(
                new VinculoCuidador(this, nuevo, "principal", parentescoNuevo, hoy)
        );

        if (cuidadorAnterior != null) {
            registrarEnAmbos(
                    new VinculoCuidador(this, cuidadorAnterior, "secundario", parentescoAnterior, hoy)
            );
        }

        return cambio;
    }

    private VinculoCuidador buscarVinculoPrincipalActivo() {

        for (VinculoCuidador vinculo : vinculos) {

            if (vinculo.estaActivo() && vinculo.esPrincipal()) {
                return vinculo;
            }
        }

        return null;
    }

    public Cuidador getCuidadorPrincipal() {

        VinculoCuidador principal = buscarVinculoPrincipalActivo();

        return principal == null ? null : principal.getCuidador();
    }

    // Método del UML. Las tomas se guardan dentro de los esquemas de dosificación.
    // CONTRATO: EsquemaDosificacion necesita getTomas().
    public ArrayList<Toma> getTomas() {

        ArrayList<Toma> todas = new ArrayList<>();

        for (EsquemaDosificacion esquema : esquemas) {

            if (esquema.getTomas() != null) {

                for (Toma toma : esquema.getTomas()) {

                    if (!todas.contains(toma)) {
                        todas.add(toma);
                    }
                }
            }
        }

        return todas;
    }

    public boolean agregarVinculo(VinculoCuidador vinculo) {

        if (vinculo == null || vinculo.getPaciente() != this || vinculos.contains(vinculo)) {
            return false;
        }

        for (VinculoCuidador actual : vinculos) {

            if (actual.estaActivo() && vinculo.estaActivo()) {

                if (actual.getCuidador() == vinculo.getCuidador()) {
                    return false;
                }

                if (actual.esPrincipal() && vinculo.esPrincipal()) {
                    return false;
                }
            }
        }

        vinculos.add(vinculo);

        return true;
    }

    public ArrayList<VinculoCuidador> buscarVinculosDeCuidador(Cuidador cuidador) {

        ArrayList<VinculoCuidador> resultado = new ArrayList<>();

        for (VinculoCuidador vinculo : vinculos) {

            if (vinculo.getCuidador() == cuidador) {
                resultado.add(vinculo);
            }
        }

        return resultado;
    }

    // Busca vínculos que se cruzan con el período solicitado, incluidos históricos.
    public ArrayList<Cuidador> obtenerCuidadoresEnPeriodo(LocalDate desde, LocalDate hasta) {

        ArrayList<Cuidador> resultado = new ArrayList<>();

        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            return resultado;
        }

        for (VinculoCuidador vinculo : vinculos) {

            boolean inicioAntesDeFin = !vinculo.getFechaInicio().isAfter(hasta);

            boolean finDespuesDeInicio = vinculo.getFechaFin() == null
                    || vinculo.getFechaFin().isAfter(desde);

            if (inicioAntesDeFin && finDespuesDeInicio
                    && !resultado.contains(vinculo.getCuidador())) {

                resultado.add(vinculo.getCuidador());
            }
        }

        return resultado;
    }

    public Cuidador obtenerCuidadorPrincipalEn(LocalDate fecha) {

        Cuidador resultado = null;

        for (VinculoCuidador vinculo : vinculos) {

            if (vinculo.esPrincipal() && vinculo.estabaActivoEn(fecha)) {
                resultado = vinculo.getCuidador();
            }
        }

        return resultado;
    }

    public int contarCuidadoresActivos() {

        ArrayList<Cuidador> cuidadores = new ArrayList<>();

        for (VinculoCuidador vinculo : vinculos) {

            if (vinculo.estaActivo() && !cuidadores.contains(vinculo.getCuidador())) {
                cuidadores.add(vinculo.getCuidador());
            }
        }

        return cuidadores.size();
    }


    // ===== 7. MEDICAMENTOS =====

    // CONTRATO EXTERNO: Medicamento necesita getNombre(), getEnfermedadTratada().
    public boolean agregarMedicamento(Medicamento medicamento) {

        if (medicamento == null || medicamentos.contains(medicamento)) {
            return false;
        }

        medicamentos.add(medicamento);

        return true;
    }

    public ArrayList<Medicamento> buscarMedicamentosPorNombre(String nombre) {

        ArrayList<Medicamento> resultado = new ArrayList<>();

        if (nombre == null || nombre.trim().isEmpty()) {
            return resultado;
        }

        for (Medicamento medicamento : medicamentos) {

            if (medicamento.getNombre() != null
                    && medicamento.getNombre().toLowerCase().contains(nombre.trim().toLowerCase())) {

                resultado.add(medicamento);
            }
        }

        return resultado;
    }

    public ArrayList<Medicamento> buscarMedicamentosPorEnfermedad(String enfermedad) {

        ArrayList<Medicamento> resultado = new ArrayList<>();

        if (enfermedad == null || enfermedad.trim().isEmpty()) {
            return resultado;
        }

        for (Medicamento medicamento : medicamentos) {

            if (medicamento.getEnfermedadTratada() != null
                    && medicamento.getEnfermedadTratada().toLowerCase()
                    .contains(enfermedad.trim().toLowerCase())) {

                resultado.add(medicamento);
            }
        }

        return resultado;
    }


    // ===== 8. ESQUEMAS DE DOSIFICACIÓN =====

    // CONTRATO EXTERNO: EsquemaDosificacion necesita getMedicamento() y getTomas().
    public boolean agregarEsquema(EsquemaDosificacion esquema) {

        if (esquema == null || esquemas.contains(esquema)) {
            return false;
        }

        esquemas.add(esquema);

        return true;
    }

    public ArrayList<EsquemaDosificacion> buscarEsquemasPorMedicamento(Medicamento medicamento) {

        ArrayList<EsquemaDosificacion> resultado = new ArrayList<>();

        for (EsquemaDosificacion esquema : esquemas) {

            if (medicamento != null && esquema.getMedicamento() == medicamento) {
                resultado.add(esquema);
            }
        }

        return resultado;
    }


    // ===== 9. CITAS MÉDICAS =====

    // CONTRATO EXTERNO: CitaMedica necesita getEspecialidad(), getProfesional(),
    // getEstado() y getFechaHora().
    public boolean agregarCita(CitaMedica cita) {

        if (cita == null || citas.contains(cita)) {
            return false;
        }

        citas.add(cita);

        return true;
    }

    public ArrayList<CitaMedica> buscarCitasPorEspecialidad(String especialidad) {

        ArrayList<CitaMedica> resultado = new ArrayList<>();

        if (especialidad == null || especialidad.trim().isEmpty()) {
            return resultado;
        }

        for (CitaMedica cita : citas) {

            if (cita.getEspecialidad() != null
                    && cita.getEspecialidad().equalsIgnoreCase(especialidad.trim())) {

                resultado.add(cita);
            }
        }

        return resultado;
    }

    public int contarCitasPorEspecialidad(String especialidad) {
        return buscarCitasPorEspecialidad(especialidad).size();
    }

    public CitaMedica obtenerUltimaCitaConProfesional(String profesional) {

        CitaMedica ultima = null;

        if (profesional == null || profesional.trim().isEmpty()) {
            return null;
        }

        for (CitaMedica cita : citas) {

            if (cita.getProfesional() != null
                    && cita.getProfesional().equalsIgnoreCase(profesional.trim())
                    && cita.getFechaHora() != null
                    && (ultima == null || cita.getFechaHora().isAfter(ultima.getFechaHora()))) {

                ultima = cita;
            }
        }

        return ultima;
    }

    public int contarCitas() {
        return citas.size();
    }

    public int contarCitasRealizadas() {

        int contador = 0;

        for (CitaMedica cita : citas) {

            if ("realizada".equalsIgnoreCase(cita.getEstado())) {
                contador++;
            }
        }

        return contador;
    }


    // ===== 10. ALERTAS =====

    // CONTRATO EXTERNO: Alerta necesita getFechaGeneracion() y estaPendiente().
    public boolean agregarAlerta(Alerta alerta) {

        if (alerta == null || alertas.contains(alerta)) {
            return false;
        }

        alertas.add(alerta);

        return true;
    }

    public ArrayList<Alerta> buscarAlertasPorFecha(LocalDate fecha) {

        ArrayList<Alerta> resultado = new ArrayList<>();

        if (fecha == null) {
            return resultado;
        }

        for (Alerta alerta : alertas) {

            if (alerta.getFechaGeneracion() != null
                    && alerta.getFechaGeneracion().toLocalDate().equals(fecha)) {

                resultado.add(alerta);
            }
        }

        return resultado;
    }

    public int contarAlertasPorFecha(LocalDate fecha) {
        return buscarAlertasPorFecha(fecha).size();
    }

    public LocalDate obtenerDiaConMasAlertas() {

        LocalDate diaMaximo = null;
        int cantidadMaxima = 0;

        for (Alerta alerta : alertas) {

            if (alerta.getFechaGeneracion() == null) {
                continue;
            }

            LocalDate dia = alerta.getFechaGeneracion().toLocalDate();
            int cantidad = contarAlertasPorFecha(dia);

            if (cantidad > cantidadMaxima) {
                cantidadMaxima = cantidad;
                diaMaximo = dia;
            }
        }

        return diaMaximo; // null si todavía no existen alertas.
    }

    public ArrayList<Alerta> buscarAlertasPendientes() {

        ArrayList<Alerta> resultado = new ArrayList<>();

        for (Alerta alerta : alertas) {

            if (alerta.estaPendiente()) {
                resultado.add(alerta);
            }
        }

        return resultado;
    }


    // ===== 11. ADVERTENCIAS FARMACOLÓGICAS =====

    // CONTRATO EXTERNO: AdvertenciaFarmacologica necesita isRevisada().
    public boolean agregarAdvertencia(AdvertenciaFarmacologica advertencia) {

        if (advertencia == null || advertencias.contains(advertencia)) {
            return false;
        }

        advertencias.add(advertencia);

        return true;
    }

    public ArrayList<AdvertenciaFarmacologica> buscarAdvertenciasPendientes() {

        ArrayList<AdvertenciaFarmacologica> resultado = new ArrayList<>();

        for (AdvertenciaFarmacologica advertencia : advertencias) {

            if (!advertencia.isRevisada()) {
                resultado.add(advertencia);
            }
        }

        return resultado;
    }


    // ===== 12. REPORTES =====

    // CONTRATO EXTERNO: ReporteSeguimiento necesita getFechaInicio(), getFechaFin().
    public boolean agregarReporte(ReporteSeguimiento reporte) {

        if (reporte == null || reportes.contains(reporte)) {
            return false;
        }

        reportes.add(reporte);

        return true;
    }

    // Incluye reportes cuyos períodos se cruzan con el rango solicitado.
    public ArrayList<ReporteSeguimiento> buscarReportesPorPeriodo(LocalDate desde, LocalDate hasta) {

        ArrayList<ReporteSeguimiento> resultado = new ArrayList<>();

        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            return resultado;
        }

        for (ReporteSeguimiento reporte : reportes) {

            if (reporte.getFechaInicio() != null && reporte.getFechaFin() != null
                    && !reporte.getFechaInicio().isAfter(hasta)
                    && !reporte.getFechaFin().isBefore(desde)) {

                resultado.add(reporte);
            }
        }

        return resultado;
    }
}


package interfaz;

import Pruebas.MainPacientes;
import modelo.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuTratamientos {

    private static ArrayList<EsquemaDosificacion> esquemas =
            new ArrayList<>();

    public static void iniciar(Scanner sc) {

        String opcion;

        do {
            System.out.println("\n===== GESTION DE TRATAMIENTOS =====");
            System.out.println("1. Crear esquema de dosificacion");
            System.out.println("2. Consultar tratamientos");
            System.out.println("3. Ver calendario de tomas");
            System.out.println("4. Modificar tratamiento");
            System.out.println("5. Suspender tratamiento");
            System.out.println("0. Volver al menu principal");
            System.out.print("Seleccione una opcion: ");

            opcion = sc.nextLine().trim();

            switch (opcion) {
                case "1":
                    crearEsquema(sc);
                    break;
                case "2":
                    consultarTratamientos(sc);
                    break;
                case "3":
                    verCalendario(sc);
                    break;
                case "4":
                    modificarTratamiento(sc);
                    break;
                case "5":
                    suspenderTratamiento(sc);
                    break;
                case "0":
                    System.out.println("Volviendo al menu principal...");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }

        } while (!opcion.equals("0"));
    }

    // ===== REGISTRAR TRATAMIENTO =====

    private static void crearEsquema(Scanner sc) {

        System.out.println("\n--- CREAR ESQUEMA DE DOSIFICACION ---");

        String cedula = leerTexto(sc, "Cedula del paciente:");
        Paciente paciente = MainPacientes.buscarPaciente(cedula);

        if (paciente == null) {
            System.out.println("Paciente no registrado.");
            return;
        }

        String nombre = leerTexto(sc,
                "Nombre del medicamento registrado:");

        ArrayList<Medicamento> encontrados =
                paciente.buscarMedicamentosPorNombre(nombre);

        if (encontrados.isEmpty()) {
            System.out.println(
                    "Este paciente no tiene ese medicamento registrado.");
            return;
        }

        System.out.println("\nMedicamentos encontrados:");

        for (int i = 0; i < encontrados.size(); i++) {
            System.out.println((i + 1) + ". "
                    + encontrados.get(i).getNombre());
        }

        int numero = leerEntero(sc, "Seleccione el medicamento:");

        if (numero < 1 || numero > encontrados.size()) {
            System.out.println("Numero de medicamento invalido.");
            return;
        }

        Medicamento medicamento = encontrados.get(numero - 1);

        double cantidad = leerDouble(sc,
                "Cantidad por toma (ejemplo: 1):");

        String unidad = leerTexto(sc,
                "Unidad de dosis (ejemplo: tableta):");

        ArrayList<LocalTime> horarios = leerHorarios(sc);

        LocalDate inicio = leerFecha(sc,
                "Fecha de inicio (AAAA-MM-DD):");

        int duracion = leerEntero(sc,
                "Duracion del tratamiento en dias:");

        int tolerancia = leerEntero(sc,
                "Tolerancia en minutos (5 a 10):");

        while (tolerancia < 5 || tolerancia > 10) {
            tolerancia = leerEntero(sc,
                    "Ingrese una tolerancia entre 5 y 10:");
        }

        EsquemaDosificacion esquema = new EsquemaDosificacion(
                paciente,
                medicamento,
                cantidad,
                unidad,
                horarios,
                inicio,
                duracion,
                tolerancia
        );

        if (!esquema.validar()) {
            System.out.println("Error: los datos del esquema no son validos.");
            return;
        }

        if (paciente.agregarEsquema(esquema)) {

            esquemas.add(esquema);
            esquema.generarCalendario();

            System.out.println("\nTratamiento registrado correctamente.");
            System.out.println("Paciente: " + paciente.getNombreCompleto());
            System.out.println("Medicamento: " + medicamento.getNombre());
            System.out.println("Fecha final: " + esquema.getFechaFin());
            System.out.println("Estado: " + esquema.getEstado());
            System.out.println("Tomas generadas: "
                    + esquema.getTomas().size());

        } else {
            System.out.println("No se pudo registrar el tratamiento.");
        }
    }

    // ===== CONSULTAR TRATAMIENTOS =====

    private static void consultarTratamientos(Scanner sc) {

        System.out.println("\n--- CONSULTAR TRATAMIENTOS ---");

        ArrayList<EsquemaDosificacion> encontrados =
                buscarEsquemasDelPaciente(sc);

        if (encontrados.isEmpty()) {
            System.out.println("No hay tratamientos registrados.");
            return;
        }

        for (int i = 0; i < encontrados.size(); i++) {

            EsquemaDosificacion esquema = encontrados.get(i);

            System.out.println("\nTratamiento " + (i + 1));
            System.out.println("Medicamento: "
                    + esquema.getMedicamento().getNombre());
            System.out.println("Cantidad por toma: "
                    + esquema.getCantidadPorToma()
                    + " " + esquema.getUnidadDosis());
            System.out.println("Horarios: " + esquema.getHorarios());
            System.out.println("Inicio: " + esquema.getFechaInicio());
            System.out.println("Fin: " + esquema.getFechaFin());
            System.out.println("Estado: " + esquema.getEstado());
        }
    }

    // ===== CALENDARIO DE TOMAS =====

    private static void verCalendario(Scanner sc) {

        System.out.println("\n--- CALENDARIO DE TOMAS ---");

        EsquemaDosificacion esquema = seleccionarEsquema(sc);

        if (esquema == null) {
            return;
        }

        ArrayList<Toma> tomas = esquema.getTomas();

        if (tomas.isEmpty()) {
            System.out.println("No hay tomas generadas.");
            return;
        }

        System.out.println("Medicamento: "
                + esquema.getMedicamento().getNombre());

        for (Toma toma : tomas) {
            System.out.println(
                    toma.getFechaHoraProgramada()
                            + " | Estado: " + toma.getEstado());
        }
    }

    // ===== MODIFICAR TRATAMIENTO =====

    private static void modificarTratamiento(Scanner sc) {

        System.out.println("\n--- MODIFICAR TRATAMIENTO ---");

        EsquemaDosificacion esquema = seleccionarEsquema(sc);

        if (esquema == null) {
            return;
        }

        if (!esquema.estaActivo()) {
            System.out.println("El tratamiento no esta activo.");
            return;
        }

        double nuevaCantidad = leerDouble(sc,
                "Nueva cantidad por toma:");

        ArrayList<LocalTime> nuevosHorarios = leerHorarios(sc);

        System.out.println("Periodo del esquema: "
                + esquema.getFechaInicio() + " a "
                + esquema.getFechaFin());

        LocalDate fechaCambio = leerFecha(sc,
                "Fecha en que se aplica el cambio (AAAA-MM-DD):");

        // Cita provisional para probar el modulo 3.
        // Se reemplazara por una cita registrada en el modulo 5.
        CitaMedica cita = new CitaMedica();

        boolean resultado = esquema.modificar(
                nuevaCantidad,
                nuevosHorarios,
                fechaCambio,
                cita
        );

        if (resultado) {
            System.out.println("Tratamiento modificado correctamente.");
            System.out.println("Nuevo calendario:");
            System.out.println("Tomas registradas: "
                    + esquema.getTomas().size());
        } else {
            System.out.println(
                    "No se pudo modificar. Revise la fecha y el estado.");
        }
    }

    // ===== SUSPENDER TRATAMIENTO =====

    private static void suspenderTratamiento(Scanner sc) {

        System.out.println("\n--- SUSPENDER TRATAMIENTO ---");

        EsquemaDosificacion esquema = seleccionarEsquema(sc);

        if (esquema == null) {
            return;
        }

        LocalDate fecha = leerFecha(sc,
                "Fecha de suspension (AAAA-MM-DD):");

        // Cita provisional para pruebas.
        CitaMedica cita = new CitaMedica();

        if (esquema.suspender(fecha, cita)) {
            System.out.println("Tratamiento suspendido correctamente.");
            System.out.println("Estado: " + esquema.getEstado());
        } else {
            System.out.println("No se pudo suspender el tratamiento.");
        }
    }

    // ===== BUSQUEDAS =====

    private static ArrayList<EsquemaDosificacion>
    buscarEsquemasDelPaciente(Scanner sc) {

        ArrayList<EsquemaDosificacion> resultado = new ArrayList<>();

        String cedula = leerTexto(sc, "Cedula del paciente:");
        Paciente paciente = MainPacientes.buscarPaciente(cedula);

        if (paciente == null) {
            System.out.println("Paciente no registrado.");
            return resultado;
        }

        for (EsquemaDosificacion esquema : esquemas) {
            if (esquema.getPaciente() == paciente) {
                resultado.add(esquema);
            }
        }

        return resultado;
    }

    private static EsquemaDosificacion seleccionarEsquema(Scanner sc) {

        ArrayList<EsquemaDosificacion> encontrados =
                buscarEsquemasDelPaciente(sc);

        if (encontrados.isEmpty()) {
            System.out.println("No hay tratamientos para seleccionar.");
            return null;
        }

        for (int i = 0; i < encontrados.size(); i++) {
            System.out.println((i + 1) + ". "
                    + encontrados.get(i));
        }

        int numero = leerEntero(sc, "Seleccione un tratamiento:");

        if (numero < 1 || numero > encontrados.size()) {
            System.out.println("Tratamiento no encontrado.");
            return null;
        }

        return encontrados.get(numero - 1);
    }

    // ===== LECTURAS Y VALIDACIONES =====

    private static String leerTexto(Scanner sc, String mensaje) {

        System.out.println(mensaje);
        String dato = sc.nextLine().trim();

        while (dato.isEmpty()) {
            System.out.println("Este campo no puede estar vacio:");
            dato = sc.nextLine().trim();
        }

        return dato;
    }

    private static int leerEntero(Scanner sc, String mensaje) {

        String dato = leerTexto(sc, mensaje);

        while (!dato.matches("[0-9]{1,6}")
                || Integer.parseInt(dato) <= 0) {
            dato = leerTexto(sc,
                    "Ingrese un numero entero positivo:");
        }

        return Integer.parseInt(dato);
    }

    private static double leerDouble(Scanner sc, String mensaje) {

        String dato = leerTexto(sc, mensaje);

        while (!dato.matches("[0-9]{1,6}(\\.[0-9]{1,3})?")
                || Double.parseDouble(dato) <= 0) {
            dato = leerTexto(sc,
                    "Ingrese un numero positivo (ejemplo: 1 o 1.5):");
        }

        return Double.parseDouble(dato);
    }

    private static LocalDate leerFecha(Scanner sc, String mensaje) {

        while (true) {

            String dato = leerTexto(sc, mensaje);

            if (dato.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {

                int anio = Integer.parseInt(dato.substring(0, 4));
                int mes = Integer.parseInt(dato.substring(5, 7));
                int dia = Integer.parseInt(dato.substring(8, 10));

                if (anio >= 1 && mes >= 1 && mes <= 12
                        && dia >= 1
                        && dia <= YearMonth.of(anio, mes).lengthOfMonth()) {

                    return LocalDate.of(anio, mes, dia);
                }
            }

            System.out.println("Fecha invalida. Use AAAA-MM-DD.");
        }
    }

    private static ArrayList<LocalTime> leerHorarios(Scanner sc) {

        ArrayList<LocalTime> horarios = new ArrayList<>();

        int cantidad = leerEntero(sc,
                "Cuantos horarios diarios tendra el medicamento?");

        while (cantidad > 6) {
            cantidad = leerEntero(sc,
                    "Ingrese un maximo de 6 horarios diarios:");
        }

        while (horarios.size() < cantidad) {

            String texto = leerTexto(sc,
                    "Ingrese horario " + (horarios.size() + 1)
                            + " (HH:mm):");

            if (!texto.matches("([01][0-9]|2[0-3]):[0-5][0-9]")) {
                System.out.println("Hora invalida. Ejemplo: 08:00");
                continue;
            }

            LocalTime hora = LocalTime.parse(texto);

            if (horarios.contains(hora)) {
                System.out.println("Ese horario ya fue ingresado.");
            } else {
                horarios.add(hora);
            }
        }

        return horarios;
    }
}

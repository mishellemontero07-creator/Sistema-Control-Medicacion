
package Pruebas;

import modelo.Toma;
import modelo.EsquemaDosificacion;
import modelo.Medicamento;
import modelo.Paciente;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class PruebaToma {

    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Toma> tomas = new ArrayList<>();

    public static void main(String[] args) {

        String opcion;

        do {
            System.out.println("\n===== SISTEMA DE CONTROL DE MEDICACION =====");
            System.out.println("MODULO 4: REGISTRO Y SEGUIMIENTO DE TOMAS");
            System.out.println("1. Registrar toma");
            System.out.println("2. Mostrar tomas");
            System.out.println("3. Confirmar toma");
            System.out.println("4. Omitir toma");
            System.out.println("5. Cancelar toma");
            System.out.println("6. Consultar tomas vencidas");
            System.out.println("7. Clasificar tomas");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");

            opcion = sc.nextLine().trim();

            switch (opcion) {
                case "1":
                    registrarToma();
                    break;
                case "2":
                    mostrarTomas();
                    break;
                case "3":
                    confirmarToma();
                    break;
                case "4":
                    omitirToma();
                    break;
                case "5":
                    cancelarToma();
                    break;
                case "6":
                    consultarVencidas();
                    break;
                case "7":
                    clasificarTomas();
                    break;
                case "0":
                    System.out.println("Saliendo del modulo de tomas.");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }

        } while (!opcion.equals("0"));
    }

    // Lectura de texto sin aceptar campos vacios
    private static String leerNoVacio(String mensaje) {

        System.out.print(mensaje);
        String texto = sc.nextLine().trim();

        while (texto.isEmpty()) {
            System.out.println("Error: el campo no puede quedar vacio.");
            System.out.print(mensaje);
            texto = sc.nextLine().trim();
        }

        return texto;
    }

    // Lectura de numeros enteros
    private static int leerEntero(String mensaje, int minimo) {

        while (true) {
            try {
                System.out.print(mensaje);
                int numero = Integer.parseInt(sc.nextLine().trim());

                if (numero >= minimo) {
                    return numero;
                }

                System.out.println("Ingrese un numero mayor o igual a " + minimo);

            } catch (NumberFormatException e) {
                System.out.println("Error: debe ingresar un numero entero.");
            }
        }
    }

    // Lectura de fechas y horas
    private static LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            try {
                String fecha = leerNoVacio(mensaje + " (AAAA-MM-DDTHH:MM): ");
                return LocalDateTime.parse(fecha, new DateTimeFormatterBuilder()
                        .appendPattern("uuuu-M-d'T'H:m")
                        .toFormatter().withResolverStyle(ResolverStyle.STRICT));
            } catch (DateTimeParseException e) {
                System.out.println("Fecha incorrecta. Ejemplo: 2026-11-23T14:30");
            }
        }
    }

    // Seleccionar una toma registrada
    private static Toma seleccionarToma() {

        if (tomas.isEmpty()) {
            System.out.println("No existen tomas registradas.");
            return null;
        }

        mostrarTomas();

        int numero = leerEntero("Numero de toma: ", 1);

        if (numero > tomas.size()) {
            System.out.println("No existe esa toma.");
            return null;
        }

        return tomas.get(numero - 1);
    }

    // Registrar una toma
    private static double leerDouble(String mensaje, double minimo) {
        while (true) {
            try {
                System.out.print(mensaje);
                double valor = Double.parseDouble(sc.nextLine().trim());
                if (Double.isFinite(valor) && valor >= minimo) return valor;
                System.out.println("Ingrese un valor mayor o igual a " + minimo);
            } catch (NumberFormatException e) {
                System.out.println("Error: ingrese un numero decimal valido.");
            }
        }
    }

    private static void registrarToma() {
        System.out.println("\n--- REGISTRAR TOMA ---");
        try {
            String nombrePaciente = leerNoVacio("Nombre del paciente: ");
            String cedula = leerNoVacio("Cedula ecuatoriana: ");
            while (!Paciente.validarCedulaIngresada(cedula)) {
                System.out.println("Cedula invalida.");
                cedula = leerNoVacio("Cedula ecuatoriana: ");
            }
            LocalDate nacimiento;
            while (true) {
                try {
                    nacimiento = LocalDate.parse(leerNoVacio("Nacimiento (AAAA-MM-DD): "));
                    if (!nacimiento.isAfter(LocalDate.now())) break;
                    System.out.println("La fecha no puede ser futura.");
                } catch (DateTimeParseException e) {
                    System.out.println("Fecha invalida. Ejemplo: 1950-08-15");
                }
            }
            Paciente paciente = new Paciente(nombrePaciente, cedula, nacimiento);
            String nombre = leerNoVacio("Medicamento: ");
            String presentacion = leerNoVacio("Presentacion: ");
            double concentracion = leerDouble("Concentracion: ", 0.01);
            String unidad = leerNoVacio("Unidad de medida: ");
            String enfermedad = leerNoVacio("Enfermedad tratada: ");
            String indicacion = leerNoVacio("Indicacion: ");
            double inventario = leerDouble("Cantidad disponible: ", 0.01);
            int reserva = leerEntero("Dias minimos de reserva: ", 0);
            Medicamento medicamento = new Medicamento(nombre, presentacion, concentracion,
                    unidad, enfermedad, indicacion, inventario, reserva, paciente);
            double dosis = leerDouble("Cantidad por toma: ", 0.01);
            String unidadDosis = leerNoVacio("Unidad de dosis: ");
            int tolerancia = leerEntero("Tolerancia (5 a 10 minutos): ", 5);
            while (tolerancia > 10) {
                System.out.println("La tolerancia debe estar entre 5 y 10 minutos.");
                tolerancia = leerEntero("Tolerancia (5 a 10 minutos): ", 5);
            }
            LocalDateTime programada = leerFechaHora("Fecha y hora programada");
            ArrayList<LocalTime> horarios = new ArrayList<>();
            horarios.add(programada.toLocalTime());
            EsquemaDosificacion esquema = new EsquemaDosificacion(paciente, medicamento,
                    dosis, unidadDosis, horarios, programada.toLocalDate(), 1, tolerancia);
            if (!esquema.validar()) {
                System.out.println("Datos del esquema invalidos.");
                return;
            }
            Toma toma = new Toma(esquema, programada, programada.plusMinutes(tolerancia));
            tomas.add(toma);
            System.out.println("Toma registrada correctamente. Estado: " + toma.getEstado());
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo registrar: " + e.getMessage());
        }
    }

    // Mostrar las tomas
    private static void mostrarTomas() {

        System.out.println("\n--- TOMAS REGISTRADAS ---");

        if (tomas.isEmpty()) {
            System.out.println("No existen tomas registradas.");
            return;
        }

        for (int i = 0; i < tomas.size(); i++) {

            Toma toma = tomas.get(i);

            System.out.println("\nToma #" + (i + 1));
            System.out.println("Estado: " + toma.getEstado());
            System.out.println("Programada: " +
                    toma.getFechaHoraProgramada());
            System.out.println("Fecha limite: " +
                    toma.getFechaHoraLimite());
            System.out.println("Fecha real: " +
                    toma.getFechaHoraReal());
            System.out.println("Confirmada por: " +
                    toma.getConfirmadaPor());
            System.out.println("Observaciones: " +
                    toma.getObservaciones());
        }
    }

    // Confirmar una toma
    private static void confirmarToma() {

        System.out.println("\n--- CONFIRMAR TOMA ---");

        Toma toma = seleccionarToma();

        if (toma == null) {
            return;
        }

        String cuidador = leerNoVacio("Nombre del cuidador: ");

        System.out.print("Observaciones (puede dejar vacio): ");
        String observaciones = sc.nextLine().trim();

        LocalDateTime hora =
                leerFechaHora("Fecha y hora de confirmacion");

        boolean resultado = toma.confirmar(
                hora,
                cuidador,
                observaciones
        );

        if (resultado) {
            System.out.println("Toma confirmada correctamente.");
            System.out.println("Estado: " + toma.getEstado());
        } else {
            System.out.println(
                    "No se pudo confirmar. Compruebe los datos y las dependencias del esquema."
            );
        }
    }

    // Omitir una toma
    private static void omitirToma() {

        System.out.println("\n--- OMITIR TOMA ---");

        Toma toma = seleccionarToma();

        if (toma == null) {
            return;
        }

        toma.omitir();

        System.out.println("Estado actual: " + toma.getEstado());
    }

    // Cancelar una toma
    private static void cancelarToma() {

        System.out.println("\n--- CANCELAR TOMA ---");

        Toma toma = seleccionarToma();

        if (toma == null) {
            return;
        }

        toma.cancelar();

        System.out.println("Estado actual: " + toma.getEstado());
    }

    // Consultar tomas vencidas
    private static void consultarVencidas() {

        System.out.println("\n--- TOMAS VENCIDAS ---");

        LocalDateTime ahora = LocalDateTime.now();
        boolean encontrada = false;

        for (int i = 0; i < tomas.size(); i++) {

            Toma toma = tomas.get(i);

            if (toma.estaVencida(ahora)) {

                System.out.println("Toma #" + (i + 1));
                System.out.println(
                        "Fecha limite: " + toma.getFechaHoraLimite()
                );

                encontrada = true;
            }
        }

        if (!encontrada) {
            System.out.println("No existen tomas vencidas.");
        }
    }

    // Clasificar las tomas
    private static void clasificarTomas() {

        System.out.println("\n--- CLASIFICACION DE TOMAS ---");

        if (tomas.isEmpty()) {
            System.out.println("No existen tomas registradas.");
            return;
        }

        for (int i = 0; i < tomas.size(); i++) {

            Toma toma = tomas.get(i);

            String estado = toma.clasificar();

            System.out.println(
                    "Toma #" + (i + 1) + ": " + estado
            );
        }
    }

}

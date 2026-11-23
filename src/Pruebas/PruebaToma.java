
package Pruebas;

import modelo.Toma;
import modelo.EsquemaDosificacion;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import modelo.Paciente;
import modelo.Medicamento;
import java.time.LocalDate;
import java.time.LocalTime;



public class PruebaToma {

    private static Scanner sc = new Scanner(System.in);
    private static ArrayList<Toma> tomas = new ArrayList<>();

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
            System.out.println("8. Consultar alertas");
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
                case "8":
                    consultarAlertas();
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
                String fecha = leerNoVacio(
                        mensaje + " (AAAA-MM-DDTHH:MM): "
                );

                return LocalDateTime.parse(fecha);

            } catch (DateTimeParseException e) {
                System.out.println("Fecha u hora incorrecta.");
                System.out.println("Ejemplo: 2026-11-22T14:30");
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
    private static void registrarToma() {

        System.out.println("\n--- REGISTRAR TOMA ---");

        LocalDateTime programada =
                leerFechaHora("Fecha y hora programada");

        LocalDateTime limite =
                leerFechaHora("Fecha y hora limite");

        if (limite.isBefore(programada)) {
            System.out.println(
                    "Error: la fecha limite no puede ser anterior a la programada."
            );
            return;
        }


        // Creamos un paciente y medicamento de prueba.
        Paciente paciente = new Paciente(
                "Paciente de prueba",
                "1700000001",
                LocalDate.of(1950, 1, 1)
        );

        Medicamento medicamento = new Medicamento(
                "Medicamento de prueba",
                "Tableta",
                500,
                "mg",
                "Prueba",
                "Solo para pruebas",
                100,
                5,
                paciente
        );

        ArrayList<LocalTime> horarios = new ArrayList<>();
        horarios.add(programada.toLocalTime());

        // Creamos el esquema usando el constructor de Diago.
        EsquemaDosificacion esquema = new EsquemaDosificacion(
                paciente,
                medicamento,
                1,
                "tableta",
                horarios,
                programada.toLocalDate(),
                1,
                5
        );


        try {
            Toma toma = new Toma(esquema, programada, limite);

            tomas.add(toma);

            System.out.println("Toma registrada correctamente.");
            System.out.println("Estado inicial: " + toma.getEstado());

        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo registrar la toma: " + e.getMessage());
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

    // Consultar alertas de tomas retrasadas
    private static void consultarAlertas() {

        System.out.println("\n--- ALERTAS DE TOMAS ---");

        LocalDateTime ahora = LocalDateTime.now();
        boolean encontrada = false;

        for (int i = 0; i < tomas.size(); i++) {

            Toma toma = tomas.get(i);

            if (toma.debeGenerarAlerta(ahora)) {

                System.out.println("Toma #" + (i + 1));
                System.out.println("Tiene una toma retrasada.");

                encontrada = true;
            }
        }

        if (!encontrada) {
            System.out.println("No existen alertas pendientes.");
        }
    }
}

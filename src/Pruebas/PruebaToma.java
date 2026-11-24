
package Pruebas;

import modelo.Toma;
import modelo.EsquemaDosificacion;
import modelo.Paciente;
import modelo.Medicamento;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class PruebaToma {

    private static Scanner sc = new Scanner(System.in);
    private static ArrayList<Toma> tomas = new ArrayList<>();

    // Permite ejecutar el modulo de manera independiente.
    public static void main(String[] args) {
        iniciar(sc);
    }

    // Usa el Scanner del menu principal para evitar lecturas duplicadas.
    public static void iniciar(Scanner scanner) {
        sc = scanner;
        String opcion;

        do {
            System.out.println("\n===== SISTEMA DE CONTROL DE MEDICACION =====");
            System.out.println("MODULO 4: REGISTRO Y SEGUIMIENTO DE TOMAS");
            System.out.println("1. Cargar tomas del paciente");
            System.out.println("2. Mostrar tomas");
            System.out.println("3. Confirmar toma");
            System.out.println("4. Omitir toma");
            System.out.println("5. Cancelar toma");
            System.out.println("6. Consultar tomas vencidas");
            System.out.println("7. Clasificar tomas");
            System.out.println("8. Consultar alertas");
            System.out.println("0. Volver al menu principal");
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
                    System.out.println("Volviendo al menu principal...");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }

        } while (!opcion.equals("0"));
    }

    // Lectura de texto sin aceptar campos vacios.
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

    // Lectura de numeros enteros.
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
                // Se captura el error cuando el texto no es un entero.
                System.out.println("Error: debe ingresar un numero entero.");
            }
        }
    }

    // Lectura de fechas y horas.
    private static LocalDateTime leerFechaHora(String mensaje) {

        while (true) {
            try {
                String fecha = leerNoVacio(
                        mensaje + " (AAAA-MM-DDTHH:MM): "
                );

                return LocalDateTime.parse(fecha);

            } catch (DateTimeParseException e) {
                // Se captura el error cuando la fecha u hora no tiene un formato valido.
                System.out.println("Fecha u hora incorrecta.");
                System.out.println("Ejemplo: 2026-11-22T14:30");
            }
        }
    }

    // Seleccionar una toma cargada desde el paciente.
    private static Toma seleccionarToma() {

        if (tomas.isEmpty()) {
            System.out.println("No existen tomas cargadas.");
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

    // Carga las tomas de tratamientos existentes, sin crear medicamentos de prueba.
    private static void registrarToma() {

        System.out.println("\n--- CARGAR TOMAS DEL PACIENTE ---");

        String cedula = leerNoVacio("Cedula del paciente: ");
        Paciente paciente = MainPacientes.buscarPaciente(cedula);

        // Limpiamos el listado anterior para trabajar solo con el paciente buscado.
        tomas.clear();

        if (paciente == null) {
            System.out.println("Paciente no registrado.");
            return;
        }

        ArrayList<Toma> tomasProgramadas = paciente.getTomas();

        if (tomasProgramadas.isEmpty()) {
            System.out.println("El paciente no tiene tomas programadas.");
            System.out.println("Primero registre un tratamiento y genere su calendario.");
            return;
        }

        // addAll conserva las mismas tomas, no crea copias de los objetos.
        tomas.addAll(tomasProgramadas);

        System.out.println("Paciente: " + paciente.getNombreCompleto());
        System.out.println("Tomas cargadas: " + tomas.size());
        mostrarTomas();
    }

    // Mostrar las tomas.
    private static void mostrarTomas() {

        System.out.println("\n--- TOMAS REGISTRADAS ---");

        if (tomas.isEmpty()) {
            System.out.println("No existen tomas cargadas.");
            return;
        }

        for (int i = 0; i < tomas.size(); i++) {

            Toma toma = tomas.get(i);
            EsquemaDosificacion esquema = toma.getEsquema();
            Medicamento medicamento = esquema.getMedicamento();

            System.out.println("\nToma #" + (i + 1));
            System.out.println("Medicamento: " + medicamento.getNombre());
            System.out.println("Cantidad por toma: " + esquema.getCantidadPorToma()
                    + " " + esquema.getUnidadDosis());
            System.out.println("Estado: " + toma.getEstado());
            System.out.println("Programada: " + toma.getFechaHoraProgramada());
            System.out.println("Fecha limite: " + toma.getFechaHoraLimite());
            System.out.println("Fecha real: " + toma.getFechaHoraReal());
            System.out.println("Confirmada por: " + toma.getConfirmadaPor());
            System.out.println("Observaciones: " + toma.getObservaciones());
        }
    }

    // Confirmar una toma.
    private static void confirmarToma() {

        System.out.println("\n--- CONFIRMAR TOMA ---");

        Toma toma = seleccionarToma();

        if (toma == null) {
            return;
        }

        String cuidador = leerNoVacio("Nombre del cuidador: ");

        System.out.print("Observaciones (puede dejar vacio): ");
        String observaciones = sc.nextLine().trim();

        LocalDateTime hora = leerFechaHora("Fecha y hora de confirmacion");

        // confirmar() ya descuenta el medicamento vinculado al esquema.
        boolean resultado = toma.confirmar(hora, cuidador, observaciones);

        if (resultado) {
            Medicamento medicamento = toma.getEsquema().getMedicamento();

            System.out.println("Toma confirmada correctamente.");
            System.out.println("Estado: " + toma.getEstado());
            System.out.println("Medicamento: " + medicamento.getNombre());
            System.out.println("Cantidad disponible: " + medicamento.getCantidadActual());
        } else {
            System.out.println("No se pudo confirmar la toma.");
            System.out.println("Revise el estado, la fecha, la tolerancia y el stock disponible.");
        }
    }

    // Omitir una toma.
    private static void omitirToma() {

        System.out.println("\n--- OMITIR TOMA ---");

        Toma toma = seleccionarToma();

        if (toma == null) {
            return;
        }

        toma.omitir();

        System.out.println("Estado actual: " + toma.getEstado());
    }

    // Cancelar una toma.
    private static void cancelarToma() {

        System.out.println("\n--- CANCELAR TOMA ---");

        Toma toma = seleccionarToma();

        if (toma == null) {
            return;
        }

        toma.cancelar();

        System.out.println("Estado actual: " + toma.getEstado());
    }

    // Consultar tomas vencidas.
    private static void consultarVencidas() {

        System.out.println("\n--- TOMAS VENCIDAS ---");

        LocalDateTime ahora = LocalDateTime.now();
        boolean encontrada = false;

        for (int i = 0; i < tomas.size(); i++) {

            Toma toma = tomas.get(i);

            if (toma.estaVencida(ahora)) {
                System.out.println("Toma #" + (i + 1));
                System.out.println("Fecha limite: " + toma.getFechaHoraLimite());
                encontrada = true;
            }
        }

        if (!encontrada) {
            System.out.println("No existen tomas vencidas.");
        }
    }

    // Clasificar las tomas.
    private static void clasificarTomas() {

        System.out.println("\n--- CLASIFICACION DE TOMAS ---");

        if (tomas.isEmpty()) {
            System.out.println("No existen tomas cargadas.");
            return;
        }

        for (int i = 0; i < tomas.size(); i++) {

            Toma toma = tomas.get(i);
            String estado = toma.clasificar();

            System.out.println("Toma #" + (i + 1) + ": " + estado);
        }
    }

    // Consultar alertas de tomas retrasadas.
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

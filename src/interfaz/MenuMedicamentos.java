
package interfaz;

import Pruebas.MainPacientes;
import modelo.Medicamento;
import modelo.Paciente;

import java.util.ArrayList;
import java.util.Scanner;

public class MenuMedicamentos {

    private static ArrayList<Medicamento> medicamentos = new ArrayList<>();
    private static ArrayList<Paciente> propietarios = new ArrayList<>();

    public static void iniciar(Scanner sc) {
        String opcion;

        do {
            System.out.println("\n===== GESTION DE MEDICAMENTOS =====");
            System.out.println("1. Registrar medicamento");
            System.out.println("2. Listar medicamentos");
            System.out.println("3. Descontar medicamento (ajuste manual)");
            System.out.println("4. Reponer medicamento");
            System.out.println("5. Consultar disponibilidad");
            System.out.println("0. Volver al menu principal");
            System.out.print("Seleccione una opcion: ");

            opcion = sc.nextLine().trim();

            switch (opcion) {
                case "1":
                    registrar(sc);
                    break;
                case "2":
                    listar();
                    break;
                case "3":
                    descontar(sc);
                    break;
                case "4":
                    reponer(sc);
                    break;
                case "5":
                    consultar(sc);
                    break;
                case "0":
                    System.out.println("Volviendo al menu principal...");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }

        } while (!opcion.equals("0"));
    }

    private static void registrar(Scanner sc) {

        System.out.println("\n--- REGISTRAR MEDICAMENTO ---");

        String cedula = leerTexto(sc, "Cedula del paciente:");
        Paciente paciente = MainPacientes.buscarPaciente(cedula);

        if (paciente == null) {
            System.out.println("Error: primero debe registrar al paciente.");
            return;
        }

        String nombre = leerTexto(sc, "Nombre del medicamento:");
        String presentacion = leerTexto(sc, "Presentacion (ejemplo: Tabletas):");
        double concentracion = leerDouble(sc, "Concentracion (ejemplo: 50):");
        String unidad = leerTexto(sc, "Unidad de concentracion (ejemplo: mg):");
        String enfermedad = leerTexto(sc, "Enfermedad tratada:");
        String indicaciones = leerTexto(sc, "Indicaciones:");
        int cantidad = leerEntero(sc, "Cantidad disponible:");

        // Este valor representa dias de reserva, no unidades de stock.
        int minimo = leerEntero(sc, "Dias minimos de reserva:");

        Medicamento medicamento = new Medicamento(
                nombre, presentacion, concentracion, unidad,
                enfermedad, indicaciones, cantidad, minimo, paciente
        );

        if (paciente.agregarMedicamento(medicamento)) {

            // Guardamos el mismo objeto asociado al paciente.
            medicamentos.add(medicamento);
            propietarios.add(paciente);

            System.out.println("Medicamento registrado correctamente.");
            System.out.println("Cantidad disponible: "
                    + medicamento.getCantidadActual());

        } else {
            System.out.println("No se pudo registrar el medicamento.");
        }
    }

    private static void listar() {

        System.out.println("\n--- MEDICAMENTOS REGISTRADOS ---");

        if (medicamentos.isEmpty()) {
            System.out.println("Todavia no hay medicamentos.");
            return;
        }

        for (int i = 0; i < medicamentos.size(); i++) {

            Medicamento medicamento = medicamentos.get(i);
            Paciente paciente = propietarios.get(i);

            System.out.println((i + 1) + ". " + medicamento.getNombre()
                    + " | Paciente: " + paciente.getNombreCompleto()
                    + " | Cantidad disponible: "
                    + medicamento.getCantidadActual());
        }
    }

    private static Medicamento seleccionar(Scanner sc) {

        listar();

        if (medicamentos.isEmpty()) {
            return null;
        }

        int numero = leerEntero(sc, "Numero del medicamento:");

        if (numero < 1 || numero > medicamentos.size()) {
            System.out.println("Medicamento no encontrado.");
            return null;
        }

        return medicamentos.get(numero - 1);
    }

    private static void descontar(Scanner sc) {

        System.out.println("\n--- DESCONTAR MEDICAMENTO (AJUSTE MANUAL) ---");

        System.out.println("Las tomas confirmadas se descuentan automaticamente.");

        Medicamento medicamento = seleccionar(sc);

        if (medicamento == null) {
            return;
        }

        double cantidad = leerDouble(sc, "Cantidad a descontar:");

        if (medicamento.descontar(cantidad)) {

            System.out.println("Cantidad descontada correctamente.");
            System.out.println("Cantidad restante: "
                    + medicamento.getCantidadActual());

        } else {
            System.out.println(
                    "No se pudo descontar esa cantidad. Revise el stock disponible.");
        }
    }

    private static void reponer(Scanner sc) {

        System.out.println("\n--- REPONER MEDICAMENTO ---");

        Medicamento medicamento = seleccionar(sc);

        if (medicamento == null) {
            return;
        }

        double cantidad = leerDouble(sc, "Cantidad a reponer:");

        medicamento.reponer(cantidad);

        System.out.println("Reposicion realizada.");
        System.out.println("Cantidad disponible: "
                + medicamento.getCantidadActual());
    }

    private static void consultar(Scanner sc) {

        System.out.println("\n--- CONSULTAR DISPONIBILIDAD ---");

        Medicamento medicamento = seleccionar(sc);

        if (medicamento == null) {
            return;
        }

        // Consulta el stock actualizado del medicamento registrado.
        System.out.println("Medicamento: " + medicamento.getNombre());

        System.out.println("Cantidad disponible: "
                + medicamento.getCantidadActual());

        System.out.println("Estado: " + medicamento.obtenerEstado(1));

        System.out.println("Dias estimados si se utiliza una unidad diaria: "
                + medicamento.calcularDiasEstimados(1));
    }

    private static String leerTexto(Scanner sc, String mensaje) {

        System.out.println(mensaje);
        String texto = sc.nextLine().trim();

        while (texto.isEmpty()) {
            System.out.println("El campo no puede estar vacio:");
            texto = sc.nextLine().trim();
        }

        return texto;
    }

    private static int leerEntero(Scanner sc, String mensaje) {

        String dato = leerTexto(sc, mensaje);

        while (!dato.matches("[0-9]{1,8}")
                || Integer.parseInt(dato) <= 0) {

            dato = leerTexto(sc,
                    "Ingrese un numero entero positivo valido:");
        }

        return Integer.parseInt(dato);
    }

    private static double leerDouble(Scanner sc, String mensaje) {

        String dato = leerTexto(sc, mensaje);

        while (!dato.matches("[0-9]{1,8}(\\.[0-9]{1,4})?")
                || Double.parseDouble(dato) <= 0) {

            dato = leerTexto(sc,
                    "Ingrese un numero positivo (ejemplo: 50 o 12.5):");
        }

        return Double.parseDouble(dato);
    }
}

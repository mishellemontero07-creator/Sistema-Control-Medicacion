
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
            System.out.println("3. Descontar medicamento");
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
        String unidad = leerTexto(sc, "Unidad (ejemplo: mg):");
        String enfermedad = leerTexto(sc, "Enfermedad tratada:");
        String indicaciones = leerTexto(sc, "Indicaciones:");
        int cantidad = leerEntero(sc, "Cantidad disponible:");
        int minimo = leerEntero(sc, "Stock minimo:");

        Medicamento medicamento = new Medicamento(
                nombre, presentacion, concentracion, unidad,
                enfermedad, indicaciones, cantidad, minimo, paciente
        );

        if (paciente.agregarMedicamento(medicamento)) {
            medicamentos.add(medicamento);
            propietarios.add(paciente);
            System.out.println("Medicamento registrado correctamente.");
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
                    + " | Paciente: " + paciente.getNombreCompleto());
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

        System.out.println("\n--- DESCONTAR MEDICAMENTO ---");
        Medicamento medicamento = seleccionar(sc);

        if (medicamento == null) {
            return;
        }

        int cantidad = leerEntero(sc, "Cantidad a descontar:");

        if (medicamento.descontar(cantidad)) {
            System.out.println("Cantidad descontada correctamente.");
        } else {
            System.out.println("No se pudo descontar esa cantidad.");
        }
    }

    private static void reponer(Scanner sc) {

        System.out.println("\n--- REPONER MEDICAMENTO ---");
        Medicamento medicamento = seleccionar(sc);

        if (medicamento == null) {
            return;
        }

        int cantidad = leerEntero(sc, "Cantidad a reponer:");
        medicamento.reponer(cantidad);

        System.out.println("Reposicion realizada.");
    }

    private static void consultar(Scanner sc) {

        System.out.println("\n--- CONSULTAR DISPONIBILIDAD ---");
        Medicamento medicamento = seleccionar(sc);

        if (medicamento == null) {
            return;
        }

        System.out.println("Medicamento: " + medicamento.getNombre());
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

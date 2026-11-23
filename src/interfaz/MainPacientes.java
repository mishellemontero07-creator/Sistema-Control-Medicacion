package interfaz;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import modelo.Cuidador;
import modelo.Paciente;

// Menú de prueba del módulo 1. No reemplaza el menú general del equipo.
public class MainPacientes {

    private static Scanner sc = new Scanner(System.in);
    private static ArrayList<Paciente> pacientes = new ArrayList<>();
    private static ArrayList<Cuidador> cuidadores = new ArrayList<>();

    public static void main(String[] args) {
        String opcion;

        do {
            System.out.println("\n===== SISTEMA DE CONTROL DE MEDICACIÓN =====");
            System.out.println("MÓDULO 1: PACIENTES Y CUIDADORES");
            System.out.println("1. Registrar paciente");
            System.out.println("2. Registrar cuidador");
            System.out.println("3. Vincular cuidador a paciente");
            System.out.println("4. Cambiar cuidador principal");
            System.out.println("5. Consultar paciente");
            System.out.println("6. Listar pacientes y cuidadores");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = sc.nextLine().trim();

            switch (opcion) {
                case "1": registrarPaciente(); break;
                case "2": registrarCuidador(); break;
                case "3": vincularCuidador(); break;
                case "4": cambiarPrincipal(); break;
                case "5": consultarPaciente(); break;
                case "6": listarTodos(); break;
                case "0": System.out.println("Hasta luego."); break;
                default: System.out.println("Opción inválida. Intente nuevamente.");
            }
        } while (!opcion.equals("0"));
    }

    // Se utiliza en todos los campos de texto para no aceptar entradas vacías.
    private static String leerNoVacio(String mensaje) {
        System.out.println(mensaje);
        String texto = sc.nextLine().trim();

        while (texto.isEmpty()) {
            System.out.println("Error: este campo no puede quedar vacío. Intente nuevamente:");
            texto = sc.nextLine().trim();
        }
        return texto;
    }

    private static Paciente buscarPaciente(String cedula) {
        for (Paciente paciente : pacientes) {
            if (paciente.getCedula().equals(cedula)) {
                return paciente;
            }
        }
        return null;
    }

    private static Cuidador buscarCuidador(String telefono) {
        for (Cuidador cuidador : cuidadores) {
            if (cuidador.getTelefono().equals(telefono)) {
                return cuidador;
            }
        }
        return null;
    }

    // Registro completo con cédula única y fecha válida antes de construir el objeto.
    private static void registrarPaciente() {
        System.out.println("\n--- REGISTRAR PACIENTE ---");
        String nombre = leerNoVacio("Nombre completo:");

        String cedula = leerNoVacio("Cédula ecuatoriana:");
        while (!Paciente.validarCedulaIngresada(cedula) || buscarPaciente(cedula) != null) {
            if (!Paciente.validarCedulaIngresada(cedula)) {
                System.out.println("Error: cédula ecuatoriana inválida.");
            } else {
                System.out.println("Error: ya existe un paciente con esa cédula.");
            }
            cedula = leerNoVacio("Ingrese otra cédula:");
        }

        String fechaTexto = leerNoVacio("Fecha de nacimiento AAAA-MM-DD (ejemplo: 1950-08-15):");
        while (!Paciente.validarFechaTexto(fechaTexto)) {
            System.out.println("Error: fecha inexistente, futura o con formato incorrecto.");
            fechaTexto = leerNoVacio("Intente nuevamente (AAAA-MM-DD):");
        }

        Paciente paciente = new Paciente(nombre, cedula, LocalDate.parse(fechaTexto));
        paciente.setEnfermedadesCronicas(leerNoVacio("Enfermedades crónicas (o escriba Ninguna):"));
        paciente.setAlergias(leerNoVacio("Alergias (o escriba Ninguna):"));
        paciente.setCondicionesRelevantes(leerNoVacio("Condiciones relevantes (o escriba Ninguna):"));
        pacientes.add(paciente);

        System.out.println("Paciente registrado: " + paciente.getNombreCompleto()
                + " (" + paciente.calcularEdad() + " años).");
        System.out.println("Aviso: todavía no tiene un cuidador principal asignado.");
    }

    private static void registrarCuidador() {
        System.out.println("\n--- REGISTRAR CUIDADOR ---");
        String nombre = leerNoVacio("Nombre completo del cuidador:");
        String telefono = leerNoVacio("Teléfono ecuatoriano (ejemplo: 0991234567):");

        while (!Cuidador.validarTelefonoIngresado(telefono) || buscarCuidador(telefono) != null) {
            if (!Cuidador.validarTelefonoIngresado(telefono)) {
                System.out.println("Error: teléfono inválido.");
            } else {
                System.out.println("Error: ya hay un cuidador con ese teléfono.");
            }
            telefono = leerNoVacio("Ingrese otro teléfono:");
        }

        Cuidador cuidador = new Cuidador(nombre, telefono);
        cuidadores.add(cuidador);
        System.out.println("Cuidador registrado correctamente: " + cuidador.getNombreCompleto());
    }

    private static void vincularCuidador() {
        System.out.println("\n--- VINCULAR CUIDADOR ---");
        Paciente paciente = buscarPaciente(leerNoVacio("Cédula del paciente:"));
        if (paciente == null) {
            System.out.println("Error: paciente no registrado.");
            return;
        }

        Cuidador cuidador = buscarCuidador(leerNoVacio("Teléfono del cuidador:"));
        if (cuidador == null) {
            System.out.println("Error: cuidador no registrado.");
            return;
        }

        String tipo = leerNoVacio("Tipo (principal o secundario):").toLowerCase();
        while (!tipo.equals("principal") && !tipo.equals("secundario")) {
            tipo = leerNoVacio("Error: escriba principal o secundario:").toLowerCase();
        }
        String parentesco = leerNoVacio("Parentesco o relación (por ejemplo: hija, enfermero):");

        if (paciente.vincularCuidador(cuidador, tipo, parentesco)) {
            System.out.println("Vínculo guardado correctamente.");
        } else {
            System.out.println("No se pudo vincular. Revise si ya existe un vínculo activo"
                    + " o si ya hay un cuidador principal.");
        }
    }

    private static void cambiarPrincipal() {
        System.out.println("\n--- CAMBIAR CUIDADOR PRINCIPAL ---");
        Paciente paciente = buscarPaciente(leerNoVacio("Cédula del paciente:"));
        if (paciente == null) {
            System.out.println("Error: paciente no registrado.");
            return;
        }

        Cuidador nuevo = buscarCuidador(leerNoVacio("Teléfono del NUEVO cuidador principal:"));
        if (nuevo == null) {
            System.out.println("Error: cuidador no registrado.");
            return;
        }

        if (paciente.cambiarCuidadorPrincipal(nuevo)) {
            System.out.println("Cuidador principal actualizado: " + nuevo.getNombreCompleto());
            System.out.println("Los vínculos anteriores permanecen en el historial.");
        } else {
            System.out.println("No se pudo cambiar: el nuevo cuidador debe estar vinculado al paciente.");
        }
    }

    private static void consultarPaciente() {
        System.out.println("\n--- CONSULTAR PACIENTE ---");
        Paciente paciente = buscarPaciente(leerNoVacio("Cédula del paciente:"));
        if (paciente == null) {
            System.out.println("Error: paciente no registrado.");
            return;
        }

        System.out.println("Nombre: " + paciente.getNombreCompleto());
        System.out.println("Cédula: " + paciente.getCedula());
        System.out.println("Fecha de nacimiento: " + paciente.getFechaNacimiento());
        System.out.println("Edad: " + paciente.calcularEdad());
        System.out.println("Enfermedades crónicas: " + paciente.getEnfermedadesCronicas());
        System.out.println("Alergias: " + paciente.getAlergias());
        System.out.println("Condiciones relevantes: " + paciente.getCondicionesRelevantes());
        System.out.println("Activo: " + paciente.isActivo());
        System.out.println("Cantidad de cuidadores activos: " + paciente.contarCuidadoresActivos());

        Cuidador principal = paciente.getCuidadorPrincipal();
        System.out.println("Cuidador principal: "
                + (principal == null ? "Sin asignar" : principal.getNombreCompleto()));
    }

    private static void listarTodos() {
        System.out.println("\n--- PACIENTES REGISTRADOS ---");
        if (pacientes.isEmpty()) {
            System.out.println("No existen pacientes registrados.");
        }
        for (Paciente paciente : pacientes) {
            System.out.println(paciente.getNombreCompleto() + " | " + paciente.getCedula());
        }

        System.out.println("\n--- CUIDADORES REGISTRADOS ---");
        if (cuidadores.isEmpty()) {
            System.out.println("No existen cuidadores registrados.");
        }
        for (Cuidador cuidador : cuidadores) {
            System.out.println(cuidador.getNombreCompleto() + " | " + cuidador.getTelefono()
                    + " | pacientes activos: " + cuidador.contarPacientesActivos());
        }
    }
}

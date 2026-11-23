
package interfaz;

import Pruebas.MainPacientes;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String opcion;

        do {
            System.out.println("\n===== SISTEMA DE CONTROL DE MEDICACIÓN =====");
            System.out.println("1. Gestión de pacientes y cuidadores");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            opcion = sc.nextLine().trim();

            switch (opcion) {
                case "1":
                    MainPacientes.main(args);
                    break;

                case "0":
                    System.out.println("Gracias por utilizar el sistema.");
                    break;

                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }

        } while (!opcion.equals("0"));
    }
}


package interfaz;

import Pruebas.MainPacientes;
import Pruebas.PruebaMedicamentos;
import Pruebas.PruebaTratamientos;
import Pruebas.PruebaToma;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String opcion;

        do {
            System.out.println("\n===== SISTEMA DE CONTROL DE MEDICACION =====");
            System.out.println("PROYECTO DE PROGRAMACION II");
            System.out.println();
            System.out.println("1. Gestion de pacientes y cuidadores");
            System.out.println("2. Pruebas de medicamentos");
            System.out.println("3. Pruebas de tratamientos");
            System.out.println("4. Registro y seguimiento de tomas");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");

            opcion = sc.nextLine().trim();

            switch (opcion) {

                case "1":
                    MainPacientes.main(args);
                    break;

                case "2":
                    MenuMedicamentos.iniciar(sc);
                    break;

                case "3":
                    PruebaTratamientos.main(args);
                    break;

                case "4":
                    PruebaToma.main(args);
                    break;

                case "0":
                    System.out.println("Gracias por utilizar el sistema.");
                    break;

                default:
                    System.out.println("Opcion invalida.");
            }

        } while (!opcion.equals("0"));
    }
}
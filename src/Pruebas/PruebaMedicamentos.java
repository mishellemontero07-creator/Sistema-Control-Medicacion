
package Pruebas;

import modelo.Paciente;
import modelo.Medicamento;
import modelo.AdvertenciaFarmacologica;

import java.time.LocalDate;
import java.util.ArrayList;

public class PruebaMedicamentos {

    private static int correctas = 0;
    private static int incorrectas = 0;

    public static void main(String[] args) {

        System.out.println("===== SISTEMA DE CONTROL DE MEDICACION =====");
        System.out.println("MODULO 2: PRUEBAS DE MEDICAMENTOS\n");

        // 1. Creamos un paciente de prueba.
        Paciente paciente = new Paciente(
                "Maria Perez",
                "1700000001",
                LocalDate.of(1950, 8, 15)
        );

        // 2. Registramos un medicamento.
        Medicamento medicamento = new Medicamento(
                "Losartan",
                "Tabletas",
                50,
                "mg",
                "Hipertension",
                "Tomar una tableta diaria",
                30,
                5,
                paciente
        );

        verificar("Registrar medicamento",
                paciente.agregarMedicamento(medicamento));

        verificar("Consultar nombre",
                medicamento.getNombre().equals("Losartan"));

        verificar("Buscar medicamento por nombre",
                paciente.buscarMedicamentosPorNombre("Losartan").size() == 1);

        verificar("Cantidad inicial de 30 tabletas",
                medicamento.calcularDiasEstimados(1) == 30);

        // 3. Descontamos medicamentos.
        verificar("Descontar 3 tabletas",
                medicamento.descontar(3));

        verificar("Quedan 27 tabletas",
                medicamento.calcularDiasEstimados(1) == 27);

        verificar("Rechazar descuento mayor al disponible",
                !medicamento.descontar(100));

        // 4. Reponemos medicamentos.
        medicamento.reponer(10);

        verificar("Reposicion de 10 tabletas",
                medicamento.calcularDiasEstimados(1) == 37);

        verificar("Guardar historial de reposiciones",
                medicamento.obtenerReposiciones().size() == 1);

        // 5. Comprobamos los calculos.
        verificar("Calcular fecha de agotamiento",
                medicamento.calcularFechaAgotamiento(1)
                        .equals(LocalDate.now().plusDays(37)));

        verificar("Estado suficiente",
                medicamento.obtenerEstado(1).equals("SUFICIENTE"));

        // 6. Probamos las advertencias.
        verificar("Medicamento con informacion completa",
                medicamento.obtenerAdvertencias().isEmpty());

        Medicamento incompleto = new Medicamento(
                "Medicamento incompleto",
                "Tabletas",
                50,
                "mg",
                "",
                "",
                10,
                5,
                paciente
        );

        ArrayList<AdvertenciaFarmacologica> advertencias =
                incompleto.obtenerAdvertencias();

        verificar("Detectar informacion incompleta",
                advertencias.size() == 2);

        if (!advertencias.isEmpty()) {
            advertencias.get(0).marcarRevisada();

            verificar("Marcar advertencia como revisada",
                    advertencias.get(0).isRevisada());
        }

        // 7. Comprobamos la reserva baja y el agotamiento.
        medicamento.descontar(33);

        verificar("Detectar reserva baja",
                medicamento.obtenerEstado(1).equals("RESERVA BAJA"));

        medicamento.descontar(4);

        verificar("Detectar medicamento agotado",
                medicamento.obtenerEstado(1).equals("AGOTADO"));

        // Resultado final.
        System.out.println("\n===== RESULTADOS FINALES =====");
        System.out.println("Pruebas correctas: " + correctas);
        System.out.println("Pruebas incorrectas: " + incorrectas);

        if (incorrectas == 0) {
            System.out.println("MODULO 2: PRUEBAS EXITOSAS");
        } else {
            System.out.println("MODULO 2: REVISAR ERRORES");
        }
    }

    private static void verificar(String descripcion, boolean resultado) {

        if (resultado) {
            System.out.println("[OK] " + descripcion);
            correctas++;
        } else {
            System.out.println("[ERROR] " + descripcion);
            incorrectas++;
        }
    }
}


package Pruebas;

import modelo.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class PruebaTratamientos {

    private static int correctas = 0;
    private static int incorrectas = 0;

    public static void main(String[] args) {

        System.out.println("===== MODULO 3: TRATAMIENTOS =====");

        // Paciente de prueba
        Paciente paciente = new Paciente(
                "Maria Perez",
                "1700000001",
                LocalDate.of(1950, 8, 15)
        );

        // Medicamento de prueba
        Medicamento medicamento = new Medicamento(
                "Losartan", "Tabletas", 50, "mg",
                "Hipertension", "Una tableta por toma",
                30, 5, paciente
        );

        LocalDate inicio = LocalDate.now();

        ArrayList<LocalTime> horarios = new ArrayList<>();
        horarios.add(LocalTime.of(20, 0));
        horarios.add(LocalTime.of(8, 0));

        // Esquema de tres dias, dos tomas diarias
        EsquemaDosificacion esquema =
                new EsquemaDosificacion(
                        paciente, medicamento, 1,
                        "tableta", horarios, inicio, 3, 5
                );

        verificar("Validar tratamiento", esquema.validar());

        verificar("Calcular consumo diario",
                esquema.calcularConsumoDiario() == 2);

        verificar("Calcular fecha de finalizacion",
                esquema.getFechaFin().equals(inicio.plusDays(2)));

        verificar("Ordenar horarios",
                esquema.getHorarios().get(0)
                        .equals(LocalTime.of(8, 0)));

        // Generar calendario
        esquema.generarCalendario();

        verificar("Generar 6 tomas programadas",
                esquema.getTomas().size() == 6);

        esquema.generarCalendario();

        verificar("Evitar tomas duplicadas",
                esquema.getTomas().size() == 6);

        Toma tomaAnterior = esquema.buscarToma(
                inicio.plusDays(1).atTime(8, 0)
        );

        verificar("Buscar toma programada",
                tomaAnterior != null);

        // Simulamos una cita medica
        CitaMedica cita = new CitaMedica();

        // Modificar tratamiento desde el segundo dia
        ArrayList<LocalTime> nuevosHorarios = new ArrayList<>();
        nuevosHorarios.add(LocalTime.of(10, 0));

        boolean modificado = esquema.modificar(
                2, nuevosHorarios, inicio.plusDays(1), cita
        );

        verificar("Modificar tratamiento", modificado);

        verificar("Actualizar cantidad por toma",
                esquema.getCantidadPorToma() == 2);

        verificar("Cancelar tomas anteriores",
                tomaAnterior != null &&
                        tomaAnterior.getEstado().equals("CANCELADA"));

        verificar("Generar nuevos horarios",
                esquema.buscarToma(
                        inicio.plusDays(1).atTime(10, 0)
                ) != null);

        verificar("Guardar historial de modificacion",
                esquema.getHistorialCambios().size() == 1);

        // Suspender tratamiento
        boolean suspendido = esquema.suspender(
                inicio.plusDays(2), cita
        );

        verificar("Suspender tratamiento", suspendido);

        verificar("Estado suspendido",
                esquema.getEstado().equals("SUSPENDIDO"));

        verificar("Registrar dos cambios en historial",
                esquema.getHistorialCambios().size() == 2);

        System.out.println("\n===== RESULTADOS FINALES =====");
        System.out.println("Pruebas correctas: " + correctas);
        System.out.println("Pruebas incorrectas: " + incorrectas);

        if (incorrectas == 0) {
            System.out.println("MODULO 3: PRUEBAS EXITOSAS");
        } else {
            System.out.println("MODULO 3: REVISAR ERRORES");
        }
    }

    private static void verificar(String descripcion,
                                  boolean resultado) {

        if (resultado) {
            System.out.println("[OK] " + descripcion);
            correctas++;
        } else {
            System.out.println("[ERROR] " + descripcion);
            incorrectas++;
        }
    }
}
